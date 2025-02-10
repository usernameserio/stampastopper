package fpozzi.stopper.model.pdf.cell;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Paragraph;

import fpozzi.stopper.model.pdf.PdfStopperStyle;
import fpozzi.stopper.model.pdf.style.fonts.FontFactory;
import fpozzi.stopper.model.pdf.style.fonts.FontFactory.FontStyle;

public class PrezzacciPdfCellFactory extends TaglioPrezzoPdfCellFactory
{
	public static final PrezzacciPdfCellFactory instance;

	static
	{
		instance = new PrezzacciPdfCellFactory(new PdfStopperStyle[] { PdfStopperStyle.SESTO_A4_BN });
	}

	public PrezzacciPdfCellFactory(PdfStopperStyle[] supportedStyles)
	{
		super(supportedStyles);
	}

	@Override
	public Paragraph makeHeaderParagraph()
	{
		Paragraph headerParagraph = new Paragraph();
		headerParagraph.add(new Chunk("SOTTOCOSTO", FontFactory.getFont(FontStyle.FUTURA_BOLD, 45)).setCharacterSpacing(1));
		return headerParagraph;
	}

}
