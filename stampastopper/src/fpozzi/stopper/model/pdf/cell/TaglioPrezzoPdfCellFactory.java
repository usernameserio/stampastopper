package fpozzi.stopper.model.pdf.cell;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Utilities;
import com.itextpdf.text.pdf.PdfPCell;

import fpozzi.stopper.model.PromoStopper;
import fpozzi.stopper.model.pdf.PdfStopperStyle;
import fpozzi.stopper.model.pdf.UnsupportedStyleException;
import fpozzi.stopper.model.pdf.style.fonts.FontFactory;
import fpozzi.utils.format.FormatUtils;

public class TaglioPrezzoPdfCellFactory extends PromoPdfCellFactory<PromoStopper>
{

	public static final TaglioPrezzoPdfCellFactory instance;
	public static final Image backgroundImageSestoA4, backgroundImageA4;

	static
	{
		Image image = null;
		try
		{
			image = Image.getInstance(TaglioPrezzoPdfCellFactory.class.getResource("/img/pdf/taglio_prezzo.png"));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		backgroundImageSestoA4 = image;

		image = null;
		try
		{
			image = Image.getInstance(TaglioPrezzoPdfCellFactory.class.getResource("/img/pdf/taglio_prezzo_a4.png"));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		backgroundImageA4 = image;

		instance = new TaglioPrezzoPdfCellFactory(
				new PdfStopperStyle[] { PdfStopperStyle.SESTO_A4_BN, PdfStopperStyle.SESTO_A4_COLORI,
						PdfStopperStyle.CARTA_SPECIALE_OFFERTA, PdfStopperStyle.CARTA_SPECIALE_OFFERTA_GRANDE });
	}

	protected TaglioPrezzoPdfCellFactory(PdfStopperStyle[] supportedStyles)
	{
		super(supportedStyles);
	}

	@Override
	protected PdfPCell makeEastCell(PromoStopper stopper, PdfStopperStyle style) throws UnsupportedStyleException
	{
		PdfPCell eastCell = new PdfPCell();
		Paragraph alpezzoParagraph;
		Paragraph priceParagraph;
		Paragraph pricePerKiloParagraph;
		switch (style)
		{
			case SESTO_A4_BN:

				eastCell.setBorder(PdfPCell.NO_BORDER);
				eastCell.setPaddingLeft(Utilities.millimetersToPoints(3));
				eastCell.setPaddingTop(Utilities.millimetersToPoints(2));

				alpezzoParagraph = new Paragraph();
				alpezzoParagraph.setAlignment(Element.ALIGN_LEFT);
				alpezzoParagraph.setIndentationLeft(Utilities.millimetersToPoints(7f));
				alpezzoParagraph
						.add(new Chunk(String.format("%-20s",
								stopper.getQuantita().size() > 0 && stopper.getQuantita().get(0).getValore() == null
										? (vuoleDoppia(stopper.getQuantita().get(0).getUnitaMisura()) ? "all'" : "al ")
												+ stopper.getQuantita().get(0).getUnitaMisura().getSing()
										: " "),
								FontFactory.getFont(normalFontStyle, 12)));
				if (!stopper.isPrezzoPerUMNascosto() && stopper.getQuantita().size() > 0
						&& stopper.getQuantita().get(0).getValore() != null)
				{
					alpezzoParagraph.add(makePrezzoPerUMChunk(stopper.getQuantita(),
							stopper.getPrezzoPromo().getValue(), FontFactory.getFont(normalFontStyle, 9)));
				}
				eastCell.addElement(alpezzoParagraph);

				priceParagraph = new Paragraph();
				priceParagraph.setAlignment(Element.ALIGN_LEFT);
				priceParagraph.setIndentationLeft(Utilities.millimetersToPoints(7f));
				priceParagraph.setSpacingBefore(Utilities.millimetersToPoints(14));
				priceParagraph.add(makePrezzoScontatoPhrase(stopper, FontFactory.getFont(boldFontStyle, 64),
						FontFactory.getFont(boldFontStyle, 38)));
				eastCell.addElement(priceParagraph);

				if (stopper.getPrezzo() != null)
				{
					pricePerKiloParagraph = new Paragraph();
					pricePerKiloParagraph.setAlignment(Element.ALIGN_LEFT);
					pricePerKiloParagraph.setIndentationLeft(Utilities.millimetersToPoints(7f));
					pricePerKiloParagraph.setSpacingBefore(Utilities.millimetersToPoints(0.5f));
					pricePerKiloParagraph.add(new Chunk("Invece di € "
							+ FormatUtils.twoDecimalDigitsFormat.format(stopper.getPrezzo().getValue()) + "    ",
							FontFactory.getFont(normalFontStyle, 9)));
					eastCell.addElement(pricePerKiloParagraph);
				}

			break;

			case SESTO_A4_COLORI:
			case CARTA_SPECIALE_OFFERTA:

				eastCell.setBorder(PdfPCell.NO_BORDER);
				eastCell.setFixedHeight(Utilities.millimetersToPoints(40));
				eastCell.setPaddingLeft(Utilities.millimetersToPoints(3));
				eastCell.setPaddingTop(Utilities.millimetersToPoints(2));

				alpezzoParagraph = new Paragraph();
				alpezzoParagraph.setAlignment(Element.ALIGN_LEFT);
				alpezzoParagraph.setIndentationLeft(Utilities.millimetersToPoints(2f));
				alpezzoParagraph.add(new Chunk(

						stopper.getQuantita().size() > 0 && stopper.getQuantita().get(0).getValore() == null
								? (vuoleDoppia(stopper.getQuantita().get(0).getUnitaMisura()) ? "all'" : "al ")
										+ stopper.getQuantita().get(0).getUnitaMisura().getSing()
								: "al pezzo",
						FontFactory.getFont(normalFontStyle, 12)));
				if (stopper.getPrezzo() != null)
				{
					alpezzoParagraph.add(new Chunk(
							" invece di € " + FormatUtils.twoDecimalDigitsFormat.format(stopper.getPrezzo().getValue()),
							FontFactory.getFont(normalFontStyle, 12)));

				}
				eastCell.addElement(alpezzoParagraph);

				priceParagraph = new Paragraph();
				priceParagraph.setAlignment(Element.ALIGN_LEFT);
				priceParagraph.setIndentationLeft(Utilities.millimetersToPoints(2f));
				priceParagraph.setSpacingBefore(Utilities.millimetersToPoints(14));
				priceParagraph.add(makePrezzoScontatoPhrase(stopper, FontFactory.getFont(boldFontStyle, 59),
						FontFactory.getFont(boldFontStyle, 33)));
				eastCell.addElement(priceParagraph);

				if (!stopper.isPrezzoPerUMNascosto() && stopper.getQuantita().size() > 0)
				{
					pricePerKiloParagraph = new Paragraph();
					pricePerKiloParagraph.setAlignment(Element.ALIGN_LEFT);
					pricePerKiloParagraph.setIndentationLeft(Utilities.millimetersToPoints(2f));
					pricePerKiloParagraph.setSpacingBefore(Utilities.millimetersToPoints(0.5f));
					pricePerKiloParagraph.add(makePrezzoPerUMChunk(stopper.getQuantita(),
							stopper.getPrezzoPromo().getValue(), FontFactory.getFont(normalFontStyle, 9)));
					eastCell.addElement(pricePerKiloParagraph);
				}
			break;

			case CARTA_SPECIALE_OFFERTA_GRANDE:

				eastCell.setBorder(PdfPCell.NO_BORDER);
				eastCell.setPaddingLeft(Utilities.millimetersToPoints(8));
				eastCell.setPaddingTop(Utilities.millimetersToPoints(20));

				alpezzoParagraph = new Paragraph();
				alpezzoParagraph.setAlignment(Element.ALIGN_LEFT);
				alpezzoParagraph.setSpacingBefore(Utilities.millimetersToPoints(18));
				alpezzoParagraph.setIndentationLeft(Utilities.millimetersToPoints(2f));
				alpezzoParagraph.add(
						new Chunk(stopper.getQuantita().size() > 0 && stopper.getQuantita().get(0).getValore() == null
								? (vuoleDoppia(stopper.getQuantita().get(0).getUnitaMisura()) ? "all'" : "al ")
										+ stopper.getQuantita().get(0).getUnitaMisura().getSing()
								: "al pezzo", FontFactory.getFont(normalFontStyle, 29)));
				if (stopper.getPrezzo() != null)
				{
					alpezzoParagraph.add(new Chunk(" invece di € "
							+ FormatUtils.twoDecimalDigitsFormat.format(stopper.getPrezzo().getValue()) + "    ",
							FontFactory.getFont(normalFontStyle, 29)));
				}

				eastCell.addElement(alpezzoParagraph);

				priceParagraph = new Paragraph();
				priceParagraph.setAlignment(Element.ALIGN_LEFT);
				priceParagraph.setIndentationLeft(Utilities.millimetersToPoints(2f));
				priceParagraph.setSpacingBefore(Utilities.millimetersToPoints(40));
				priceParagraph.add(makePrezzoScontatoPhrase(stopper, FontFactory.getFont(boldFontStyle, 140),
						FontFactory.getFont(boldFontStyle, 80)));
				eastCell.addElement(priceParagraph);

				if (!stopper.isPrezzoPerUMNascosto() && stopper.getQuantita().size() > 0)
				{
					pricePerKiloParagraph = new Paragraph();
					pricePerKiloParagraph.setAlignment(Element.ALIGN_LEFT);
					pricePerKiloParagraph.setIndentationLeft(Utilities.millimetersToPoints(2f));
					pricePerKiloParagraph.setSpacingBefore(Utilities.millimetersToPoints(10));

					pricePerKiloParagraph.add(makePrezzoPerUMChunk(stopper.getQuantita(),
							stopper.getPrezzoPromo().getValue(), FontFactory.getFont(normalFontStyle, 22)));

					eastCell.addElement(pricePerKiloParagraph);
				}

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
		headerParagraph.add(new Chunk("OFFERTA", FontFactory.getFont(heavyFontStyle, 45)).setCharacterSpacing(4));
		return headerParagraph;
	}

	@Override
	public PdfCellDecorator makeCellDecorator(PdfStopperStyle style) throws UnsupportedStyleException
	{
		if (style == PdfStopperStyle.CARTA_SPECIALE_OFFERTA || style == PdfStopperStyle.CARTA_SPECIALE_OFFERTA_GRANDE)
			return new PdfCellDecorator(false);

		if (style == PdfStopperStyle.SESTO_A4_COLORI)
			return new PdfCellDecorator(true, 0, backgroundImageSestoA4);

		if (style == PdfStopperStyle.CARTA_SPECIALE_OFFERTA_GRANDE)
			return new PdfCellDecorator(true, 0, backgroundImageA4);

		throw new UnsupportedStyleException();
	}

}
