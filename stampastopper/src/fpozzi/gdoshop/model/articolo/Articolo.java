package fpozzi.gdoshop.model.articolo;


import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Date;

import fpozzi.gdoshop.model.fornitore.Fornitore;
import fpozzi.utils.date.DateUtils;
import fpozzi.utils.format.FormatUtils;

public class Articolo extends ArticoloBase
{

	protected final Collection<String> codiciEan;
	protected final Quantita grammatura;
	
	protected final double costo, prezzo;
	protected final int iva;
	protected final Fornitore fornitore;
	protected final int indiceRotazione;
	protected final int pezziPerCollo;
	protected final double giacenza;
	protected final Date dataInserimento, dataOrdine, dataAcquisto, dataVendita;
	protected final boolean venditaAPeso;
	protected final boolean annullato, inOccasioneAcquisto, inOfferta;
	private final int reparto;


	public Articolo(CodiceInterno codiceInterno, String descrizione, 
			int reparto, Collection<String> codiciEan, 
			boolean venditaAPeso, Quantita grammatura, double costo, double prezzo, 
			int iva, Fornitore fornitore, int indiceRotazione, int pezziPerCollo, double giacenza, Date dataInserimento, Date dataOrdine,
			Date dataAcquisto, Date dataVendita, boolean annullato, boolean inOccasioneAcquisto, boolean inOfferta)
	{
		super(codiceInterno, descrizione);
		this.reparto = reparto;
		this.codiciEan = codiciEan;
		this.grammatura = grammatura;
		this.costo = costo;
		this.prezzo = prezzo;
		this.iva = iva;
		this.fornitore = fornitore;
		this.indiceRotazione = indiceRotazione;
		this.pezziPerCollo = pezziPerCollo;
		this.giacenza = giacenza;
		this.dataInserimento = dataInserimento;
		this.dataOrdine = dataOrdine;
		this.dataAcquisto = dataAcquisto;
		this.dataVendita = dataVendita;
		this.annullato = annullato;
		this.venditaAPeso = venditaAPeso;
		this.inOccasioneAcquisto = inOccasioneAcquisto;
		this.inOfferta = inOfferta;
	}
	
	public int getReparto()
	{
		return reparto;
	}
	
	public boolean isVenditaAPeso()
	{
		return venditaAPeso;
	}

	public boolean isInOccasioneAcquisto()
	{
		return inOccasioneAcquisto;
	}

	public Quantita getGrammatura()
	{
		return grammatura;
	}

	public int getPezziPerCollo()
	{
		return pezziPerCollo;
	}

	public double getGiacenza()
	{
		return giacenza;
	}
	
	static private final DecimalFormat giacenzaFormat = new DecimalFormat("0.###");
	
	public String getFormattedGiacenza()
	{
		return giacenzaFormat.format(giacenza);
	}
	

	public Date getDataInserimento()
	{
		return dataInserimento;
	}
	
	public Date getDataOrdine()
	{
		return dataOrdine;
	}

	public Date getDataAcquisto()
	{
		return dataAcquisto;
	}

	public int getIva()
	{
		return iva;
	}

	public double getPrezzo()
	{
		return prezzo;
	}

	public double getCosto()
	{
		return costo;
	}
	
	public int getIndiceRotazione()
	{
		return indiceRotazione;
	}

	public Fornitore getFornitore()
	{
		return fornitore;
	}

	public boolean isInOccasioneAquisto()
	{
		return inOccasioneAcquisto;
	}

	public Collection<String> getCodiciEan()
	{
		return codiciEan;
	}

	public Date getDataVendita()
	{
		return dataVendita;
	}

	public boolean isAnnullato()
	{
		return annullato;
	}

	public boolean isInOfferta()
	{
		return inOfferta;
	}
	
	public double getMargine()
	{
		return calcolaMargine(prezzo, costo, iva);
	}
	
	protected static double calcolaMargine(double prezzo, double costo, double iva)
	{
		if (prezzo==0)
			return 0;
		
		return 100*(prezzo - costo*(1+iva/100.0))/prezzo;
	}
	
	public String getFormattedMargine()
	{
		return FormatUtils.twoDecimalDigitsFormat.format(getMargine()) + "%";
	}
	
	public String getFormattedCosto()
	{
		return  "€ " + FormatUtils.threeDecimalDigitsFormat.format(getCosto());
	}
	
	public String getFormattedPrezzo()
	{
		return "€ " + FormatUtils.twoDecimalDigitsFormat.format(getPrezzo());
	}
	
	public String getFormattedDataAcquisto()
	{
		return DateUtils.italianDateFormat.format(dataAcquisto);
	}
	
	public String getFormattedDataOrdine()
	{
		return DateUtils.italianDateFormat.format(dataOrdine);
	}
	
	public String getFormattedDataVendita()
	{
		return DateUtils.italianDateFormat.format(dataVendita);
	}
	
	public String getFormattedDataInserimento()
	{
		return DateUtils.italianDateFormat.format(dataInserimento);
	}
	
	public String getIndiceMarginalita()
	{
		double margine = getMargine();
		if (margine<1)
			return "E-";
					
		if (margine<11)
			return "E";
		
		if (margine<21)
			return "D";
		
		if (margine<31)
			return "C";
		
		if (margine<41)
			return "B";
		
		if (margine<51)
			return "A";
		
		return "A+";
	}
	
	public static boolean isFresco(String codiceInterno)
	{
		return codiceInterno.length()==7 && codiceInterno.charAt(0)=='7'; 
	}
	
	public String getInfo()
	{
		if (codiceInterno.valore.length()==7 && fornitore.getCodice().equals("900001")) 
		{
			if (codiceInterno.valore.charAt(0)=='6') 
				return "SURGELATO";
			if (codiceInterno.valore.charAt(0)=='7') 
				return "FRESCO";
			if (codiceInterno.valore.charAt(0)=='5') 
				return "ORTOFRUTTA";
			return "CE.DI.";
		}
		
		return "DELIVERY";
	}
	
}
