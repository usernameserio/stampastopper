package fpozzi.gdoshop.model.articolo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import fpozzi.utils.date.DateUtils;
import fpozzi.utils.date.Periodo;
import fpozzi.utils.date.PeriodoCompleto;
import fpozzi.utils.date.PeriodoStandard;
import fpozzi.utils.date.PeriodoStandard.TipoPeriodoStandard;

public class MovimentatoAggregato extends Movimentato
{
	protected final Collection<? extends Movimentato> movimentatiParziali;
	protected final double maxParziale;
	protected final TipoPeriodoStandard tipoPeriodiParziali;
	
	public MovimentatoAggregato(Articolo articolo, 
			CausaleMovimento causaleMovimento,
			PeriodoCompleto periodo, TipoPeriodoStandard tipoPeriodiParziali,
			Collection<? extends Movimentato> movimentatiParziali) throws Exception
	{
		super(articolo, causaleMovimento, periodo, makeQta(movimentatiParziali));
		
		this.tipoPeriodiParziali = tipoPeriodiParziali;
		
		ArrayList<Movimentato> listaCompletaMovimentatiParziali = new ArrayList<Movimentato>();

		Periodo p;
		Date dataInizioPeriodoParziale = periodo.getInizio();
		do
		{
			p =  PeriodoStandard.makePeriodoStandard(dataInizioPeriodoParziale, tipoPeriodiParziali);
			listaCompletaMovimentatiParziali.add(new Movimentato(articolo, causaleMovimento, p, 0));
			dataInizioPeriodoParziale = new Date(p.getFine().getTime()+1);
		}
		while (!dataInizioPeriodoParziale.after(periodo.getFine()));
		
		ArrayList<Movimentato> movimentatiParzialiOrdinati = new ArrayList<Movimentato>(movimentatiParziali);
		Collections.sort(movimentatiParzialiOrdinati, Movimentato.dataInizioComparator);
		
		double maxParziale = 0;
		Movimentato movimentatoDestinazione;
		boolean found; double qtaParziale; int i=0;
		for (Movimentato movimentatoSorgente : movimentatiParzialiOrdinati)
		{
			if (movimentatoSorgente.causaleMovimento==this.causaleMovimento)
			{
				found=false;
				movimentatoDestinazione = null;
				while(!found && i<listaCompletaMovimentatiParziali.size())
				{
					movimentatoDestinazione = listaCompletaMovimentatiParziali.get(i);
					if (movimentatoSorgente.getPeriodo().isInclusoIn(movimentatoDestinazione.getPeriodo()))
						found=true;
					else if(movimentatoSorgente.getPeriodo().isSovrappostoCon(movimentatoDestinazione.getPeriodo())) 
							throw new Exception(
									"Movimentato parziale con periodo non compatibile");
					else
						i++;
				}
				if (found)
				{
					qtaParziale = movimentatoDestinazione.getQta()+movimentatoSorgente.getQta();
					listaCompletaMovimentatiParziali.set(i, 
							new Movimentato(articolo, causaleMovimento, movimentatoDestinazione.periodo, qtaParziale));
					if (qtaParziale>maxParziale)
						maxParziale=qtaParziale;
				}
			}
		}

		this.movimentatiParziali = Collections.unmodifiableList(listaCompletaMovimentatiParziali);
		
		this.maxParziale = maxParziale;
	}
	
	public MovimentatoAggregato(Articolo articolo, 
			CausaleMovimento causaleMovimento,
			TipoPeriodoStandard tipoPeriodiParziali,
			Collection<? extends Movimentato> movimentatiParziali) throws Exception
	{
		this(articolo, causaleMovimento, 
				makePeriodo(tipoPeriodiParziali, movimentatiParziali), 
				tipoPeriodiParziali, movimentatiParziali);
	}
	
	private static double makeQta(
			Collection<? extends Movimentato> movimentatiParziali)
	{
		double qta = 0;
		for (Movimentato v : movimentatiParziali)
		{
			qta += v.getQta();
		}
		
		return qta;
	}


	private static PeriodoCompleto makePeriodo(
			TipoPeriodoStandard tipoPeriodiParziali,
			Collection<? extends Movimentato> movimentatiParziali) throws Exception
	{
		if (movimentatiParziali.size()==0)
			throw new Exception();
		Date dataInizio = DateUtils.FUTURE.getTime(), dataFine = DateUtils.PAST.getTime();
		for (Movimentato v : movimentatiParziali)
		{
			if (v.periodo.getInizio().before(dataInizio))
				dataInizio = v.periodo.getInizio();
			if (v.periodo.getFine().after(dataFine))
				dataFine = v.periodo.getFine();
		}
		return new PeriodoCompleto(dataInizio, dataFine, tipoPeriodiParziali);
	}


	public Collection<? extends Movimentato> getMovimentatiParziali()
	{
		return movimentatiParziali;
	}

	public TipoPeriodoStandard getTipoPeriodiParziali()
	{
		return tipoPeriodiParziali;
	}

	public double getMaxParziale()
	{
		return maxParziale;
	}
	
	static public MovimentatoAggregato makeMovimentatoAggregato(TermAnagDB db,
			Articolo articolo, 
			CausaleMovimento causaleMovimento,
			Date aPartireDa, Date finoA, 
			TipoPeriodoStandard tipoPeriodiParziali) 
					throws Exception
	{
		PeriodoCompleto pCompleto = new PeriodoCompleto(aPartireDa, finoA, tipoPeriodiParziali);
		Collection<Movimentato> movimentatiParziali = 
				db.getMovimentato(articolo, causaleMovimento, tipoPeriodiParziali, 
						pCompleto.getInizio(), pCompleto.getFine(), false);
		
		return new MovimentatoAggregato(articolo, causaleMovimento, pCompleto, tipoPeriodiParziali, movimentatiParziali);
	}
}
