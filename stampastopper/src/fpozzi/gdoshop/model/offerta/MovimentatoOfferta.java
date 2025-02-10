package fpozzi.gdoshop.model.offerta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import fpozzi.gdoshop.model.articolo.Articolo;
import fpozzi.gdoshop.model.articolo.Movimentato;
import fpozzi.gdoshop.model.articolo.MovimentatoAggregato;
import fpozzi.gdoshop.model.articolo.TermAnagDB;
import fpozzi.gdoshop.model.articolo.Movimentato.CausaleMovimento;
import fpozzi.utils.date.DateUtils;
import fpozzi.utils.date.Periodo;
import fpozzi.utils.date.PeriodoStandard.TipoPeriodoStandard;

public class MovimentatoOfferta
{

	public static final String TAG = MovimentatoOfferta.class.getName();
	
	private final OffertaArticolo offerta;
	private final MovimentatoAggregato venduto;
	
	private MovimentatoOfferta(OffertaArticolo offerta, 
			MovimentatoAggregato venduto)
	{
		super();
		this.offerta = offerta;
		this.venduto = venduto;
	}
	
	public OffertaArticolo getOfferta()
	{
		return offerta;
	}
	
	public MovimentatoAggregato getVenduto()
	{
		return venduto;
	}
	
	public static final Date dueAnniFa = 
			new Date(DateUtils.now().getTime().getTime() - 2 * DateUtils.millisInAYear); 
	
	
	static public List<MovimentatoOfferta> makeVendutiOfferte(TermAnagDB artDB, OffertaMysqlDAO offDB, Articolo articolo)
	{
		List<MovimentatoOfferta> vendutiOfferte = new ArrayList<MovimentatoOfferta>();

		try
		{
			List<Movimentato> vendutiGiornalieri = 
					artDB.getMovimentato(articolo, CausaleMovimento.VENDITA, 
							TipoPeriodoStandard.GIORNO, dueAnniFa, DateUtils.now().getTime(), 
							true);
			vendutiGiornalieri.add(
					new Movimentato(articolo, CausaleMovimento.VENDITA,
							new Periodo(DateUtils.FUTURE.getTime(), DateUtils.FUTURE.getTime()), 0));

			if (vendutiGiornalieri.size() > 0)
			{
				OffertaArticolo off;
				List<Movimentato> vendutiGiornalieriOfferta = new LinkedList<Movimentato>(); 
				Date dataVenditaPrecedente = null;
				Date dataInizio = vendutiGiornalieri.get(0).getPeriodo().getInizio();
				Date dataFine;
				for (Movimentato vg : vendutiGiornalieri)
				{
					if (dataVenditaPrecedente != null && 
							DateUtils.daysBetween(vg.getPeriodo().getFine(),dataVenditaPrecedente) > 10)
					{
						dataFine = dataVenditaPrecedente;
						off = offDB.makeOfferta(articolo, dataInizio, dataFine);
						if (off!=null)
						{
							MovimentatoAggregato vp = 
									new MovimentatoAggregato(articolo,
											CausaleMovimento.VENDITA,
											TipoPeriodoStandard.GIORNO,
											vendutiGiornalieriOfferta);
								vendutiOfferte.add(new MovimentatoOfferta(off, vp));
						}
						dataInizio = vg.getPeriodo().getInizio();
						vendutiGiornalieriOfferta = new LinkedList<Movimentato>(); 
					}
					dataVenditaPrecedente = vg.getPeriodo().getFine();
					vendutiGiornalieriOfferta.add(vg);
				}
				
				Collections.sort(vendutiOfferte, defaultComparator);
			}
		} catch (Exception e)
		{
			//Log.d(TAG, e.getMessage() + "");
		}

		return vendutiOfferte;
	}
	
	static public Comparator<MovimentatoOfferta> defaultComparator =
			new Comparator<MovimentatoOfferta> (){

				@Override
				public int compare(MovimentatoOfferta vpo1,
						MovimentatoOfferta vpo2)
				{
					return OffertaArticolo.dataInizioComparator.compare(
							vpo1.getOfferta(), vpo2.getOfferta());
				}
		
	};
	
}
