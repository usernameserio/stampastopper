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
import fpozzi.utils.format.FormatUtils;

public class ScontoPercentualePdfCellFactory extends PromoPdfCellFactory<PromoStopper>
{

	public static final ScontoPercentualePdfCellFactory instance;
	public static final Image backgroundImageSestoA4;

	static
	{
		Image image = null;
		try
		{
			image = Image.getInstance(UnoPiuUnoPdfCellFactory.class.getResource("/img/pdf/sconto_percentuale.png"));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		backgroundImageSestoA4 = image;

		instance = new ScontoPercentualePdfCellFactory(new PdfStopperStyle[] { PdfStopperStyle.SESTO_A4_BN,
				PdfStopperStyle.SESTO_A4_COLORI, PdfStopperStyle.CARTA_SPECIALE_SCONTO });
	}

	private ScontoPercentualePdfCellFactory(PdfStopperStyle[] supportedStyles)
	{
		super(supportedStyles);
	}

	public PdfPCell makeEastCell(PromoStopper stopper, PdfStopperStyle style) throws UnsupportedStyleException
	{

		PdfPCell eastCell = new PdfPCell();
		Paragraph priceParagraph, pricePerKiloParagraph, risparmioPercentualeParagraph;

		switch (style)
		{
			case SESTO_A4_BN:

				eastCell = new PdfPCell();
				eastCell.setBorder(PdfPCell.NO_BORDER);
				eastCell.setPaddingLeft(Utilities.millimetersToPoints(3));
				eastCell.setPaddingTop(Utilities.millimetersToPoints(2));

				risparmioPercentualeParagraph = new Paragraph();
				risparmioPercentualeParagraph.setAlignment(Element.ALIGN_LEFT);
				risparmioPercentualeParagraph
						.add(new Chunk("RISPARMI IL ", FontFactory.getFont(heavyFontStyle, 25))
								.setCharacterSpacing(-0.8f));
				risparmioPercentualeParagraph.add(new Chunk(stopper.getPrezzoPromo().getPercentualeSconto() + "%\n",
						FontFactory.getFont(boldFontStyle, 25)));
				risparmioPercentualeParagraph
						.add(new Chunk(new LineSeparator(0.5f, 95, BaseColor.BLACK, Element.ALIGN_LEFT, 10)));
				eastCell.addElement(risparmioPercentualeParagraph);

				priceParagraph = new Paragraph();
				priceParagraph.setAlignment(Element.ALIGN_LEFT);
				priceParagraph.setIndentationLeft(Utilities.millimetersToPoints(7f));
				priceParagraph.setSpacingBefore(Utilities.millimetersToPoints(10));
				priceParagraph.add(makePrezzoScontatoPhrase(stopper, FontFactory.getFont(boldFontStyle, 64),
						FontFactory.getFont(boldFontStyle, 38)));
				eastCell.addElement(priceParagraph);

				pricePerKiloParagraph = new Paragraph();
				pricePerKiloParagraph.setAlignment(Element.ALIGN_LEFT);
				pricePerKiloParagraph.setSpacingBefore(Utilities.millimetersToPoints(1.5f));
				pricePerKiloParagraph.setIndentationLeft(Utilities.millimetersToPoints(1f));
				pricePerKiloParagraph.add(new Chunk("Invece di € "
						+ FormatUtils.twoDecimalDigitsFormat.format(stopper.getPrezzo().getValue()) + "    ",
						FontFactory.getFont(normalFontStyle, 12)));
				if (!stopper.isPrezzoPerUMNascosto())
					pricePerKiloParagraph.add(makePrezzoPerUMChunk(stopper.getQuantita(),
							stopper.getPrezzoPromo().getValue(), FontFactory.getFont(normalFontStyle, 9)));
				eastCell.addElement(pricePerKiloParagraph);

			break;

			case SESTO_A4_COLORI:
			case CARTA_SPECIALE_SCONTO:

				eastCell = new PdfPCell();
				eastCell.setBorder(PdfPCell.NO_BORDER);
				eastCell.setPaddingLeft(Utilities.millimetersToPoints(3));
				eastCell.setPaddingTop(Utilities.millimetersToPoints(4));

				risparmioPercentualeParagraph = new Paragraph();
				risparmioPercentualeParagraph.setAlignment(Element.ALIGN_LEFT);
				risparmioPercentualeParagraph
						.add(new Chunk("RISPARMI IL ", FontFactory.getFont(heavyFontStyle, 25))
								.setCharacterSpacing(-1.5f));
				risparmioPercentualeParagraph.add(new Chunk(stopper.getPrezzoPromo().getPercentualeSconto() + "%",
						FontFactory.getFont(boldFontStyle, 25)));
				eastCell.addElement(risparmioPercentualeParagraph);

				priceParagraph = new Paragraph();
				priceParagraph.setAlignment(Element.ALIGN_LEFT);
				priceParagraph.setIndentationLeft(Utilities.millimetersToPoints(2f));
				priceParagraph.setSpacingBefore(Utilities.millimetersToPoints(14));
				priceParagraph.add(makePrezzoScontatoPhrase(stopper, FontFactory.getFont(boldFontStyle, 59),
						FontFactory.getFont(boldFontStyle, 33)));
				eastCell.addElement(priceParagraph);

				pricePerKiloParagraph = new Paragraph();
				pricePerKiloParagraph.setAlignment(Element.ALIGN_LEFT);
				pricePerKiloParagraph.setIndentationLeft(Utilities.millimetersToPoints(2f));
				pricePerKiloParagraph.add(new Chunk("Invece di € "
						+ FormatUtils.twoDecimalDigitsFormat.format(stopper.getPrezzo().getValue()) + "     ",
						FontFactory.getFont(normalFontStyle, 9)));
				if (!stopper.isPrezzoPerUMNascosto())
					pricePerKiloParagraph.add(makePrezzoPerUMChunk(stopper.getQuantita(),
							stopper.getPrezzoPromo().getValue(), FontFactory.getFont(normalFontStyle, 9)));
				eastCell.addElement(pricePerKiloParagraph);

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
		headerParagraph.add(new Chunk("SCONTO", FontFactory.getFont(boldFontStyle, 45)).setCharacterSpacing(5));
		return headerParagraph;
	}

	@Override
	public PdfCellDecorator makeCellDecorator(PdfStopperStyle style) throws UnsupportedStyleException
	{
		if (style == PdfStopperStyle.CARTA_SPECIALE_SCONTO)
			return new PdfCellDecorator(false);

		if (style == PdfStopperStyle.SESTO_A4_COLORI)
			return new PdfCellDecorator(true, 0, backgroundImageSestoA4);

		throw new UnsupportedStyleException();
	}
}
