package fpozzi.stopper.model.pdf.cell;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Utilities;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.draw.LineSeparator;

import fpozzi.stopper.model.PromoStopper;
import fpozzi.stopper.model.pdf.PdfStopperStyle;
import fpozzi.stopper.model.pdf.UnsupportedStyleException;
import fpozzi.stopper.model.pdf.style.fonts.FontFactory;
import fpozzi.stopper.model.pdf.style.fonts.FontFactory.FontStyle;
import fpozzi.utils.format.FormatUtils;

public class UnoPiuUnoPdfCellFactory extends PromoPdfCellFactory<PromoStopper>
{

	public static final UnoPiuUnoPdfCellFactory instance;
	public static final Image backgroundImageSestoA4;

	static
	{
		Image image = null;
		try
		{
			image = Image.getInstance(UnoPiuUnoPdfCellFactory.class.getResource("/img/pdf/uno_piu_uno.png"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		backgroundImageSestoA4 = image;

		instance = new UnoPiuUnoPdfCellFactory(new PdfStopperStyle[] { PdfStopperStyle.SESTO_A4_BN, PdfStopperStyle.SESTO_A4_COLORI,
				PdfStopperStyle.CARTA_SPECIALE_UNOPIUUNO });
	}

	protected UnoPiuUnoPdfCellFactory(PdfStopperStyle[] supportedStyles)
	{
		super(supportedStyles);
	}

	@Override
	protected PdfPCell makeEastCell(PromoStopper stopper, PdfStopperStyle style) throws UnsupportedStyleException
	{
		PdfPCell eastCell = new PdfPCell();
		Paragraph priceParagraph;
		Paragraph prezzoEffettivoParagraph, pricePerUmEffettivoParagraph;
		Paragraph pezzoSingoloParagraph;
		Paragraph duePezziParagraph;
		Paragraph prezzoUmPerPezzoSingoloParagraph;
		switch (style)
		{
		case SESTO_A4_BN:

			eastCell = new PdfPCell();
			eastCell.setBorder(PdfPCell.NO_BORDER);
			eastCell.setPaddingLeft(Utilities.millimetersToPoints(3));

			pezzoSingoloParagraph = new Paragraph();
			pezzoSingoloParagraph.setAlignment(Element.ALIGN_LEFT);
			pezzoSingoloParagraph.add(new Chunk("1 pezzo ", FontFactory.getFont(heavyFontStyle, 13)));
			pezzoSingoloParagraph.add(new Chunk("€ " + stopper.getPrezzoPromo().getValue() + "\n", FontFactory.getFont(boldFontStyle, 13)));
			eastCell.addElement(pezzoSingoloParagraph);

			prezzoUmPerPezzoSingoloParagraph = new Paragraph();
			prezzoUmPerPezzoSingoloParagraph.setAlignment(Element.ALIGN_RIGHT);
			prezzoUmPerPezzoSingoloParagraph.setIndentationRight(Utilities.millimetersToPoints(7f));
			prezzoUmPerPezzoSingoloParagraph.setLeading(0.0f, 0f);
			prezzoUmPerPezzoSingoloParagraph.add(makePrezzoPerUMChunk(stopper.getQuantita(), stopper.getPrezzoPromo().getValue(),
					FontFactory.getFont(normalFontStyle, 8)));
			eastCell.addElement(prezzoUmPerPezzoSingoloParagraph);

			eastCell.addElement(new Chunk(new LineSeparator(0.5f, 95, BaseColor.BLACK, Element.ALIGN_LEFT, 12)));

			duePezziParagraph = new Paragraph();
			duePezziParagraph.setLeading(5);
			duePezziParagraph.setAlignment(Element.ALIGN_LEFT);
			duePezziParagraph.add(new Chunk("2 pezzi\n", FontFactory.getFont(heavyFontStyle, 18)));
			eastCell.addElement(duePezziParagraph);

			priceParagraph = new Paragraph();
			priceParagraph.setAlignment(Element.ALIGN_LEFT);
			priceParagraph.setSpacingBefore(Utilities.millimetersToPoints(11f));
			priceParagraph.add(makePrezzoScontatoPhrase(stopper, FontFactory.getFont(boldFontStyle, 52), FontFactory.getFont(boldFontStyle, 33)));
			eastCell.addElement(priceParagraph);

			prezzoEffettivoParagraph = new Paragraph();
			prezzoEffettivoParagraph.setAlignment(Element.ALIGN_LEFT);
			prezzoEffettivoParagraph.add(new Chunk("al pezzo ", FontFactory.getFont(normalFontStyle, 9)));
			prezzoEffettivoParagraph.add(makePricePhrase(stopper.getPrezzoPromo().getValue() / 2.0,
					FontFactory.getFont(heavyFontStyle, 9),
					FontFactory.getFont(heavyFontStyle, 9),
					FontFactory.getFont(heavyFontStyle, 9)));
			eastCell.addElement(prezzoEffettivoParagraph);
			
			pricePerUmEffettivoParagraph = new Paragraph();
			pricePerUmEffettivoParagraph.setAlignment(Element.ALIGN_RIGHT);
			pricePerUmEffettivoParagraph.setIndentationRight(Utilities.millimetersToPoints(7f));
			pricePerUmEffettivoParagraph.setLeading(0,0);
			pricePerUmEffettivoParagraph.add(makePrezzoPerUMChunk(stopper.getQuantita(), stopper.getPrezzoPromo().getValue() / 2.0,
					FontFactory.getFont(normalFontStyle, 8)));
			eastCell.addElement(pricePerUmEffettivoParagraph);

			break;

		case SESTO_A4_COLORI:
		case CARTA_SPECIALE_UNOPIUUNO:

			eastCell = new PdfPCell();
			eastCell.setBorder(PdfPCell.NO_BORDER);
			eastCell.setPaddingLeft(Utilities.millimetersToPoints(3));

			pezzoSingoloParagraph = new Paragraph();
			pezzoSingoloParagraph.setAlignment(Element.ALIGN_LEFT);
			pezzoSingoloParagraph.add(new Chunk("1 pezzo ", FontFactory.getFont(heavyFontStyle, 12)));
			pezzoSingoloParagraph.add(new Chunk("€ " + FormatUtils.twoDecimalDigitsFormat.format(stopper.getPrezzoPromo().getValue()) + "\n", FontFactory.getFont(boldFontStyle, 12)));
			eastCell.addElement(pezzoSingoloParagraph);

			prezzoUmPerPezzoSingoloParagraph = new Paragraph();
			prezzoUmPerPezzoSingoloParagraph.setAlignment(Element.ALIGN_RIGHT);
			prezzoUmPerPezzoSingoloParagraph.setIndentationRight(Utilities.millimetersToPoints(10f));
			prezzoUmPerPezzoSingoloParagraph.setLeading(0.0f, 0f);
			prezzoUmPerPezzoSingoloParagraph.add(makePrezzoPerUMChunk(stopper.getQuantita(), stopper.getPrezzoPromo().getValue(),
					FontFactory.getFont(normalFontStyle, 8)));
			eastCell.addElement(prezzoUmPerPezzoSingoloParagraph);

			duePezziParagraph = new Paragraph();
			duePezziParagraph.setAlignment(Element.ALIGN_LEFT);
			duePezziParagraph.setSpacingBefore(Utilities.millimetersToPoints(3f));
			duePezziParagraph.add(new Chunk("2 pezzi\n", FontFactory.getFont(heavyFontStyle, 17)));
			eastCell.addElement(duePezziParagraph);

			priceParagraph = new Paragraph();
			priceParagraph.setAlignment(Element.ALIGN_LEFT);
			priceParagraph.setSpacingBefore(Utilities.millimetersToPoints(9f));
			priceParagraph.add(makePrezzoScontatoPhrase(stopper, FontFactory.getFont(boldFontStyle, 48), FontFactory.getFont(boldFontStyle, 30)));
			eastCell.addElement(priceParagraph);

			prezzoEffettivoParagraph = new Paragraph();
			prezzoEffettivoParagraph.setAlignment(Element.ALIGN_LEFT);
			prezzoEffettivoParagraph.setSpacingBefore(-Utilities.millimetersToPoints(1));
			prezzoEffettivoParagraph.add(new Chunk("al pezzo ", FontFactory.getFont(normalFontStyle, 8)));
			prezzoEffettivoParagraph.add(makePricePhrase(stopper.getPrezzoPromo().getValue()/ 2,
					FontFactory.getFont(heavyFontStyle, 8),
					FontFactory.getFont(heavyFontStyle, 8),
					FontFactory.getFont(heavyFontStyle, 8)));
			eastCell.addElement(prezzoEffettivoParagraph);
			
			pricePerUmEffettivoParagraph = new Paragraph();
			pricePerUmEffettivoParagraph.setAlignment(Element.ALIGN_RIGHT);
			pricePerUmEffettivoParagraph.setIndentationRight(Utilities.millimetersToPoints(10f));
			pricePerUmEffettivoParagraph.setLeading(0,0);
			pricePerUmEffettivoParagraph.add(makePrezzoPerUMChunk(stopper.getQuantita(), stopper.getPrezzoPromo().getValue() / 2.0,
					FontFactory.getFont(normalFontStyle, 8)));
			eastCell.addElement(pricePerUmEffettivoParagraph);

			break;

		default:
			throw new UnsupportedStyleException();
		}

		return eastCell;

	}

	@Override
	public Paragraph makeHeaderParagraph()
	{
		Paragraph headerParagraph = new Paragraph();
		headerParagraph.add(new Chunk("1+1", FontFactory.getFont(heavyFontStyle, 45)).setCharacterSpacing(-6));
		headerParagraph.add(new Chunk(" GRATIS", FontFactory.getFont(heavyFontStyle, 45)).setCharacterSpacing(3));
		return headerParagraph;
	}

	@Override
	public PdfCellDecorator makeCellDecorator(PdfStopperStyle style) throws UnsupportedStyleException
	{
		if (style == PdfStopperStyle.CARTA_SPECIALE_UNOPIUUNO)
			return new PdfCellDecorator(false);

		if (style == PdfStopperStyle.SESTO_A4_COLORI)
			return new PdfCellDecorator(true, 0,  backgroundImageSestoA4);

		throw new UnsupportedStyleException();
	}
}