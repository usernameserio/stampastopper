package fpozzi.gdoshop.model.articolo;

public class ArticoloBase
{
	
	protected final CodiceInterno codiceInterno;
	protected final String descrizione;
	
	public ArticoloBase(CodiceInterno codiceInterno, String descrizione)
	{
		super();
		this.codiceInterno = codiceInterno;
		this.descrizione = descrizione;
	}
	
	public CodiceInterno getCodiceInterno()
	{
		return codiceInterno;
	}

	public String getDescrizione()
	{
		return descrizione;
	}


	@Override
	public String toString() {
		return codiceInterno + " " + descrizione;
	}	

}
