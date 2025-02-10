package fpozzi.utils.date;

import java.util.Calendar;
import java.util.Date;

public class PeriodoStandard extends Periodo
{
	public enum TipoPeriodoStandard {
		GIORNO, SETTIMANA, MESE, BIMESTRE, TRIMESTRE, QUADRIMESTRE, SEMESTRE, ANNO };	
		
	static public Periodo makePeriodoStandard(
			Date dataInclusa, TipoPeriodoStandard tipoPeriodo)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataInclusa);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		if (tipoPeriodo==TipoPeriodoStandard.SETTIMANA)
			cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		if (tipoPeriodo==TipoPeriodoStandard.MESE)
			cal.set(Calendar.DAY_OF_MONTH, 1);
		if (tipoPeriodo==TipoPeriodoStandard.BIMESTRE)
		{
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.MONTH, ((int)Math.floor((cal.get(Calendar.MONTH))/2.0))*2);
		}
		if (tipoPeriodo==TipoPeriodoStandard.TRIMESTRE)
		{
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.MONTH, ((int)Math.floor((cal.get(Calendar.MONTH))/3.0))*3);
		}
		if (tipoPeriodo==TipoPeriodoStandard.QUADRIMESTRE)
		{
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.MONTH, ((int)Math.floor((cal.get(Calendar.MONTH))/4.0))*4);
		}
		if (tipoPeriodo==TipoPeriodoStandard.SEMESTRE)
		{
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.MONTH, ((int)Math.floor((cal.get(Calendar.MONTH))/6.0))*6);
		}
		if (tipoPeriodo==TipoPeriodoStandard.ANNO)
			cal.set(Calendar.DAY_OF_YEAR, 1);
		
		Date inizioGiorno = cal.getTime(); 
				
		cal.add(Calendar.DAY_OF_YEAR, 1);
		cal.add(Calendar.MILLISECOND, -1);
		
		if (tipoPeriodo==TipoPeriodoStandard.SETTIMANA)
		{
			cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
			cal.add(Calendar.DAY_OF_YEAR, 1);
		}
		if (tipoPeriodo==TipoPeriodoStandard.MESE)
		{
			cal.add(Calendar.MONTH, 1);
			cal.add(Calendar.DAY_OF_YEAR, -1);
		}
		if (tipoPeriodo==TipoPeriodoStandard.BIMESTRE)
		{
			cal.add(Calendar.MONTH, 2);
			cal.add(Calendar.DAY_OF_YEAR, -1);
			
		}
		if (tipoPeriodo==TipoPeriodoStandard.TRIMESTRE)
		{
			cal.add(Calendar.MONTH, 3);
			cal.add(Calendar.DAY_OF_YEAR, -1);
			
		}
		if (tipoPeriodo==TipoPeriodoStandard.QUADRIMESTRE)
		{
			cal.add(Calendar.MONTH, 4);
			cal.add(Calendar.DAY_OF_YEAR, -1);
			
		}
		if (tipoPeriodo==TipoPeriodoStandard.SEMESTRE)
		{
			cal.add(Calendar.MONTH, 6);
			cal.add(Calendar.DAY_OF_YEAR, -1);
			
		}
		if (tipoPeriodo==TipoPeriodoStandard.ANNO)
		{
			cal.add(Calendar.YEAR, 1);
			cal.add(Calendar.DAY_OF_YEAR, -1);
		}
		Date fineGiorno = cal.getTime();
		
		return new Periodo(inizioGiorno, fineGiorno);
	}
	
	private final TipoPeriodoStandard tipoPeriodo;

	protected PeriodoStandard(Date dataInizio, Date dataFine,
			TipoPeriodoStandard tipoPeriodo)
	{
		super(dataInizio, dataFine);
		this.tipoPeriodo = tipoPeriodo;
	}

	public TipoPeriodoStandard getTipoPeriodo()
	{
		return tipoPeriodo;
	}

}
