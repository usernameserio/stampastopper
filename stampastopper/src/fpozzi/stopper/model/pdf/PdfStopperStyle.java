package fpozzi.stopper.model.pdf;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;


public enum PdfStopperStyle 
{
	
	A4_BN("A4 - carta normale - stampa b/n", PdfStopperFormat.A4, false, false, "/img/pdf/a4_bn.png", null ),	
	OTTAVO_A4_BN("ottavo di A4 - carta normale - stampa b/n", PdfStopperFormat.A7, false, false, "/img/pdf/ottavo_a4_bn.png", null ),
	SESTO_A4_BN("sesto di A4 - carta normale - stampa b/n", PdfStopperFormat.SESTO_A4, false, false, "/img/pdf/sesto_a4_bn.png" , null),
	CARD_BN("card - carta normale - stampa b/n", PdfStopperFormat.CARD, false, false, "/img/pdf/card_bn.png" , null),
	QUARTO_A4_BN("quarto di A4 - carta normale - stampa b/n", PdfStopperFormat.A6, false, false, "/img/pdf/quarto_a4_bn.png" , null),
	META_A4_BN("mezzo A4 - carta normale - stampa b/n", PdfStopperFormat.A5, false, false, "/img/pdf/meta_a4_bn.png" , null),
	TERZO_A4_BN("terzo A4 - carta normale - stampa b/n", PdfStopperFormat.TERZO_A4, false, false, "/img/pdf/terzo_a4_bn.png" , null),

	CARD_COLORI("card - carta normale - stampa a colori", PdfStopperFormat.CARD, true, false, "/img/pdf/card_colori.png" , null),
	SESTO_A4_COLORI("sesto di A4 - carta normale - stampa a colori", PdfStopperFormat.SESTO_A4, true, false, "/img/pdf/sesto_a4_colori.png" , null),
	META_A4_COLORI("mezzo A4 - carta normale - stampa a colori", PdfStopperFormat.A5, true, false, "/img/pdf/meta_a4_colori.png", null),
	
	CARTA_SPECIALE_OFFERTA("sesto di A4 - carta speciale taglio prezzo", PdfStopperFormat.SESTO_A4, false, true, "/img/pdf/sesto_a4_cs_tp.png", "/img/pdf/taglio_prezzo.png"),
	CARTA_SPECIALE_OFFERTA_GRANDE("A4 - carta speciale taglio prezzo", PdfStopperFormat.A4, false, true, "/img/pdf/a4_cs_tp.png", "/img/pdf/taglio_prezzo_a4.png"),
	CARTA_SPECIALE_UNOPIUUNO("sesto di A4 - carta speciale 1+1", PdfStopperFormat.SESTO_A4, false, true, "/img/pdf/sesto_a4_cs_up.png", "/img/pdf/uno_piu_uno.png"),
	CARTA_SPECIALE_SCONTO("sesto di A4 - carta speciale sconto %", PdfStopperFormat.SESTO_A4, false, true, "/img/pdf/sesto_a4_cs_sp.png", "/img/pdf/sconto_percentuale.png"), 

	CARTA_SPECIALE_OTTAVO_BORDATO("ottavo di A4 - carta pretagliata bordata - stampa b/n", PdfStopperFormat.A7, false, true, "/img/pdf/ottavo_bordata.png", null ),
	
	STRISCIA_CORTA("striscia corta - carta normale - stampa b/n", PdfStopperFormat.STRISCIA_CORTA, false, false, "/img/pdf/striscia_corta_bn.png", null );
	
	private final String descrizione;
	private final PdfStopperFormat format;
	private final boolean richiedeCartaSpeciale, richiedeStampaAColori;
	private final BufferedImage representationImage, cartaSpecialeImage;

	private PdfStopperStyle(String descrizione, PdfStopperFormat format,  
			boolean richiedeStampaAColori, boolean richiedeCartaSpeciale, 
			String iconPath, String cartaSpecialeImagePath)
	{
		this.descrizione = descrizione;
		this.format = format;
		this.richiedeCartaSpeciale = richiedeCartaSpeciale;
		this.richiedeStampaAColori = richiedeStampaAColori;
		
		BufferedImage cartaSpecialeImage = null, representationImage = null;
		if (cartaSpecialeImagePath!=null)
		try
		{
			cartaSpecialeImage = ImageIO.read(getClass().getResource(cartaSpecialeImagePath));
		} 
		catch (IOException e) {e.printStackTrace();}	
		
		try
		{
			representationImage = ImageIO.read(getClass().getResource(iconPath));
		}
		catch (IOException e) {e.printStackTrace();}	
		
		this.representationImage = representationImage;
		this.cartaSpecialeImage = cartaSpecialeImage;
	}
	
	public String getDescrizione()
	{
		return descrizione;
	}

	public PdfStopperFormat getFormat()
	{
		return format;
	}

	public boolean richiedeCartaSpeciale()
	{
		return richiedeCartaSpeciale;
	}


	public boolean richiedeStampaAColori()
	{
		return richiedeStampaAColori;
	}

	public Image getCartaSpecialeImage()
	{
		return cartaSpecialeImage;
	}

	public Image getRappresentazione()
	{
		return representationImage;
	}

}
