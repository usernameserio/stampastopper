package fpozzi.stopper.model.pdf;

import com.itextpdf.text.Image;

public enum PdfStopperPaper 
{
	BLANK(null),
	PROMO_FRUTTA(null),
	PROMO_TAGLIO_PREZZO_PAPER("/img/pdf/taglio_prezzo.png"),
	PROMO_UNO_PIU_UNO_PAPER("/img/pdf/uno_piu_uno.png"),
	PROMO_SCONTO_PERCENTUALE_PAPER("/img/pdf/sconto_percentuale.png");
	
	private final Image backgroundImage;

	private PdfStopperPaper(String backgroundImagePath)
	{
		
		Image backgroundImage = null;
		
		if (backgroundImagePath!=null)
		try
		{
			backgroundImage = Image.getInstance(backgroundImagePath);
		} 
		catch (Exception e){e.printStackTrace();}
		
		this.backgroundImage = backgroundImage;
		
	}

	public Image getBackgroundImage()
	{
		return backgroundImage;
	}

	
}
