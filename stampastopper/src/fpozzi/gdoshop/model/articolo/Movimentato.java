package fpozzi.gdoshop.model.articolo;

import java.util.Comparator;

import fpozzi.utils.date.Periodo;
import fpozzi.utils.format.FormatUtils;

public class Movimentato
{
	public enum CausaleMovimento {VENDITA, ACQUISTO}
	
	protected final CausaleMovimento causaleMovimento;
	protected final Articolo articolo;
	protected final Periodo periodo;
	protected final double qta;

	public Movimentato(Articolo articolo, 
			CausaleMovimento causaleMovimento,
			Periodo periodo, double qta)
	{
		super();
		this.articolo = articolo;
		this.causaleMovimento = causaleMovimento;
		this.periodo = periodo;
		this.qta = qta;
	}

	public Articolo getArticolo()
	{
		return articolo;
	}

	public double getQta()
	{
		return qta;
	}
	
	public CausaleMovimento getCausaleMovimento()
	{
		return causaleMovimento;
	}

	public Periodo getPeriodo()
	{
		return periodo;
	}
	
	public int getColli()
	{
		return (int) Math.round(qta/articolo.getPezziPerCollo());
	}	
	
	public String getFormattedQta()
	{
		return FormatUtils.optionalDecimalDigitsFormat.format(getQta());
	}

	static public final Comparator<Movimentato> dataInizioComparator = 
			new Comparator<Movimentato>()
	{

		public int compare(Movimentato v1, Movimentato v2)
		{
			return v1.periodo.getInizio().compareTo(v2.periodo.getInizio());
		}
	}; 

	public String toString()
	{
		return causaleMovimento + " di " + getFormattedQta() + "pz per articolo " + articolo + " nel " + periodo;
	}
}
