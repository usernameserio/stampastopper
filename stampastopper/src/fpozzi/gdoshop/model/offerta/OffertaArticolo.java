package fpozzi.gdoshop.model.offerta;

import java.util.Comparator;

import fpozzi.gdoshop.model.articolo.Articolo;
import fpozzi.utils.format.FormatUtils;

public class OffertaArticolo
{
	static public String TAG = OffertaArticolo.class.getName();
	
	static public final Comparator<OffertaArticolo> dataInizioComparator = new Comparator<OffertaArticolo>()
	{
		public int compare(OffertaArticolo offerta, OffertaArticolo otherOfferta)
		{
			return otherOfferta.periodo.getInizio().compareTo(offerta.periodo.getInizio());
		}
	};

	private final Articolo articolo;
	
	private final PeriodoOfferta periodo;
	private final TipoOfferta tipoOfferta;
	private final DestinatarioOfferta destinatario;
	private final double costoScontato, prezzoScontato;
	private final String descrizione;

	public OffertaArticolo(Articolo articolo,
			TipoOfferta tipoOfferta,
			String descrizione,
			double prezzoOriginale, double prezzoOfferta, 
			double costoOriginale, double costoScontato,
			PeriodoOfferta periodo,
			DestinatarioOfferta destinatario)
	{
		this.articolo = articolo;
		this.tipoOfferta = tipoOfferta;
		this.descrizione = descrizione;
		this.prezzoScontato = prezzoOfferta;	
		this.costoScontato = costoScontato;
		this.periodo = periodo;
		this.destinatario = destinatario;
	}
	
	public Articolo getArticolo()
	{
		return articolo;
	}
	
	public TipoOfferta getTipoOfferta()
	{
		return tipoOfferta;
	}
	
	public String getDescrizione()
	{
		return descrizione;
	}

	public PeriodoOfferta getPeriodo()
	{
		return periodo;
	}
	
	public double getPrezzoScontato()
	{
		return prezzoScontato;
	}
	
	public DestinatarioOfferta getDestinatario()
	{
		return destinatario;
	}	

	public double getCostoScontato()
	{
		return costoScontato;
	}


	public String toString()
	{
		return descrizione + 
				"\n" + periodo +
				"\nprezzo: " + "€ " + FormatUtils.twoDecimalDigitsFormat.format(prezzoScontato);
	}
}
