package fpozzi.utils.date;

import java.util.Date;


public class Periodo
{
	
	protected final Date dataInizio, dataFine;
	protected final int durata;
	
	public Periodo(Date dataInizio, Date dataFine)
	{
		this.dataInizio = dataInizio;
		this.dataFine = dataFine;
		this.durata = (int) DateUtils.daysBetween(this.dataFine, this.dataInizio)+1;
	}

	public Date getInizio()
	{
		return dataInizio;
	}

	public Date getFine()
	{
		return dataFine;
	}

	public int getDurata()
	{
		return durata;
	}

	public boolean isInclusoIn(Periodo altroPeriodo)
	{
		if (dataInizio.before(altroPeriodo.dataInizio))
			return false;
		
		if (dataFine.after(altroPeriodo.dataFine))
			return false;
		
		return true;
	}
	
	public boolean isSovrappostoCon(Periodo altroPeriodo)
	{
		if (dataInizio.after(altroPeriodo.dataFine))
			return false;
		
		if (dataFine.before(altroPeriodo.dataInizio))
			return false;
		
		return true;
	}
	
	@Override
	public boolean equals(Object altroOggetto)
	{
		if (!(altroOggetto instanceof Periodo))
			return false;
		
		Periodo altroPeriodo = (Periodo) altroOggetto;
		return (dataInizio.equals(altroPeriodo.dataInizio) && 
				dataFine.equals(altroPeriodo.dataFine));
	}
	
	@Override
	public int hashCode()
	{
		return dataInizio.hashCode() ^ dataFine.hashCode();
	}

	public String toString()
	{
		return "Periodo dal " + DateUtils.italianDateFormat.format(dataInizio) + 
				" al " + DateUtils.italianDateFormat.format(dataFine);
	}
	
}
