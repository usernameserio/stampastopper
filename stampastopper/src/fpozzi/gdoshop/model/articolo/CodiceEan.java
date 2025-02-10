package fpozzi.gdoshop.model.articolo;

public class CodiceEan extends Codice
{
	public CodiceEan(String valore)
	{
		super(valore);
	}

	@Override
	public boolean equals(Object otherObject)
	{
		return ((otherObject instanceof CodiceEan) && ((CodiceEan) otherObject).valore.equals(valore));
	}

}