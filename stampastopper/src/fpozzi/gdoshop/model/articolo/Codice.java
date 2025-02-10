package fpozzi.gdoshop.model.articolo;

public abstract class Codice implements Comparable<Codice>
{
	public final String valore;

	public Codice(String valore)
	{
		super();
		this.valore = valore.trim();
	}
	
	@Override
	public int hashCode()
	{
		return valore.hashCode();
	}

	@Override
	public String toString()
	{
		return valore;
	}

	@Override
	public int compareTo(Codice o)
	{
		return valore.compareTo(o.valore);
	}
	 

}