package fpozzi.gdoshop.model.articolo;

public class CodiceInterno extends Codice
{
	public CodiceInterno(String valore)
	{
		super(valore);
	}

	@Override
	public boolean equals(Object otherObject)
	{
		return ((otherObject instanceof CodiceInterno) && ((CodiceInterno) otherObject).valore.equals(valore));
	}

}