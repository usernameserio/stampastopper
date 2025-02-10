package fpozzi.stopper.model.pdf.cell;

import java.util.List;

import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;

import fpozzi.stopper.model.PromoStopper;
import fpozzi.stopper.model.pdf.PdfStopperStyle;

public class TaglioPrezzoOrtofruttaPdfCellFactory extends TaglioPrezzoPdfCellFactory
{
	public static final TaglioPrezzoOrtofruttaPdfCellFactory instance;
	
	static
	{
		instance = new TaglioPrezzoOrtofruttaPdfCellFactory(new PdfStopperStyle[] { 
				PdfStopperStyle.SESTO_A4_BN,
				PdfStopperStyle.SESTO_A4_COLORI, 
				PdfStopperStyle.CARTA_SPECIALE_OFFERTA,
				PdfStopperStyle.CARTA_SPECIALE_OFFERTA_GRANDE });
	}
	
	protected TaglioPrezzoOrtofruttaPdfCellFactory(PdfStopperStyle[] supportedStyles)
	{
		super(supportedStyles);
	}

	@Override
	protected Paragraph makeDescrizioneParagraph(PromoStopper stopper, Font uCaseFont, Font lCaseFont)
	{
		Paragraph descriptionParagraph = new Paragraph();

		List<String> righeDescrizione = stopper.getRigheDescrizione();

		for (int i = 0; i < righeDescrizione.size(); i++)
		{

			String rigaDescrizione = righeDescrizione.get(i);
			descriptionParagraph.add(makePhrase(rigaDescrizione, lCaseFont, uCaseFont));
		}
		return descriptionParagraph;
	}
	
}
