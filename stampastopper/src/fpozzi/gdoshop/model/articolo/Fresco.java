package fpozzi.gdoshop.model.articolo;

import java.sql.Date;
import java.util.Collection;

import fpozzi.gdoshop.model.fornitore.Fornitore;
import fpozzi.utils.format.FormatUtils;


public class Fresco extends Articolo
{

	public Fresco(CodiceInterno codiceInterno, String descrizione, int reparto, Collection<String> codiciEan, 
			boolean venditaAPeso, Quantita grammatura, double costo, double prezzo,
			int iva, Fornitore fornitore, int indiceRotazione, int pezziPerCollo, double giacenza, Date dataInserimento, Date dataOrdine,
			Date dataAcquisto, Date dataVendita, boolean annullato, boolean inOfferta, boolean inOccasioneAcquisto)
	{
		super(codiceInterno, descrizione, reparto, codiciEan, venditaAPeso, grammatura, prezzo, costo, iva, fornitore, indiceRotazione, pezziPerCollo, giacenza, dataInserimento,
				dataOrdine, dataAcquisto, dataVendita, annullato, inOfferta, inOccasioneAcquisto);
	}

	private static double calcolaContributo(Articolo fresco) throws Exception
	{
		return fresco.grammatura.getValoreNormalizzato()*0.12;
	}
	
	private static double calcolaCostoConContributo(Articolo fresco) throws Exception
	{
		return calcolaContributo(fresco) + fresco.getCosto();
	}
	
	public String getCostoConContributoFormatted() 
			throws Exception
	{
		return "€ " + FormatUtils.threeDecimalDigitsFormat.format(
				calcolaCostoConContributo(this));
	}
	
	public double getMargineConContributo() throws Exception
	{
		return Articolo.calcolaMargine(
				this.getPrezzo(), 
				calcolaCostoConContributo(this),  
				this.getIva());
	}
	
	public String getMargineConContributoFormatted() 
			throws Exception
	{
		return FormatUtils.twoDecimalDigitsFormat.format(
				this.getMargineConContributo())+ "%";
	}

}
