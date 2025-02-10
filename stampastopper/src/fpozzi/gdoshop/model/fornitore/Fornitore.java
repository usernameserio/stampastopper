package fpozzi.gdoshop.model.fornitore;

public class Fornitore
{

	private String codice;
	private String descrizione;
	
	public Fornitore(String codice, String descrizione)
	{
		super();
		this.codice = codice;
		this.descrizione = descrizione;
	}
	
	public String getCodice()
	{
		return codice;
	}
	
	public String getDescrizione()
	{
		return descrizione;
	}
	
	public String toString()
	{
		return codice + " - " + descrizione;
	}

	
}
