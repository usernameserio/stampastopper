package fpozzi.utils.date;

import java.util.Date;

import fpozzi.utils.date.PeriodoStandard.TipoPeriodoStandard;

public class PeriodoCompleto extends Periodo
{
	
	private final TipoPeriodoStandard tipoPeriodo;

	public PeriodoCompleto(Date dataInizialeInclusa, Date dataFinaleInclusa, 
			TipoPeriodoStandard tipoPeriodo)
	{
		super (PeriodoStandard.makePeriodoStandard(dataInizialeInclusa, tipoPeriodo).getInizio(),
				PeriodoStandard.makePeriodoStandard(dataFinaleInclusa, tipoPeriodo).getFine());
		this.tipoPeriodo = tipoPeriodo;
	}

	public TipoPeriodoStandard getTipoPeriodo()
	{
		return tipoPeriodo;
	}
	
	@Override
	public boolean equals(Object altroOggetto)
	{
		if (!(altroOggetto instanceof PeriodoCompleto))
			return false;
		
		if (!super.equals(altroOggetto))
			return false;
		
		PeriodoCompleto altroPeriodo = (PeriodoCompleto) altroOggetto;
		return tipoPeriodo == altroPeriodo.tipoPeriodo;
	}
	
	
}
