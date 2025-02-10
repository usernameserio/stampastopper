package fpozzi.stopper.model.pdf.cell;

import java.util.List;
import java.util.regex.Matcher;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import fpozzi.gdoshop.model.articolo.Quantita;
import fpozzi.gdoshop.model.articolo.UnitaMisura;
import fpozzi.stopper.model.GastronomiaStopper;
import fpozzi.stopper.model.Prezzo;
import fpozzi.stopper.model.pdf.PdfStopperStyle;
import fpozzi.stopper.model.pdf.style.fonts.FontFactory;
import fpozzi.stopper.model.pdf.style.fonts.FontFactory.FontStyle;
import fpozzi.utils.StringUtils;
import fpozzi.utils.format.FormatUtils;

public class GastroPdfCellFactory extends ArticoloPdfCellFactory<GastronomiaStopper>
{

	public static final GastroPdfCellFactory instance;

	static
	{
		instance = new GastroPdfCellFactory(new PdfStopperStyle[] { PdfStopperStyle.OTTAVO_A4_BN, PdfStopperStyle.QUARTO_A4_BN,
				PdfStopperStyle.STRISCIA_CORTA });
	}

	private GastroPdfCellFactory(PdfStopperStyle[] supportedStyles)
	{
		super(supportedStyles);
	}

	@Override
	public PdfPCell makePDF(GastronomiaStopper stopper, PdfStopperStyle style, PdfContentByte pdfContentByte) throws DocumentException
	{
		PdfPCell stopperCell = new PdfPCell();
		stopperCell.setCellEvent(new PdfCellDecorator(true));
		stopperCell.setFixedHeight(style.getFormat().getHeight());
		stopperCell.setBorder(PdfPCell.NO_BORDER);
		stopperCell.setPadding(14);
		stopperCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

		PdfPTable stopperTable;
		Paragraph descriptionParagraph, priceParagraph;
		List<String> righeDescrizione = stopper.getRigheDescrizione();
		long iPart = 0, fPart = 0;
		Quantita qta = stopper.getQuantita().size() > 0 ? stopper.getQuantita().get(0) : null;
		Prezzo prezzo = stopper.getPrezzo();

		boolean hasIngrCell = stopper.getIngredienti() != null || stopper.getNote() != null;
		boolean hasPesoParagraph = qta != null && qta.getValore() != null;

		float descCellHeight, priceCellHeight, priceParagraphLeading, priceIndentation, misuraSpacingBefore, pesoSpacingBefore, pesoIndentationRight, misuraBigIndentation, misuraSmallIndentation, maxSouthCellSize;
		int descFontSize, currencyFontSize, iPartFontSize, fPartFontSize, misuraFontSize, prefIngrFontSize, pesoFontSize, estimatedLineWidth;

		if (style == PdfStopperStyle.OTTAVO_A4_BN)
		{
			descCellHeight = 52;
			descFontSize = 24;
			priceCellHeight = !hasIngrCell || hasPesoParagraph ? 107 : 85;
			priceParagraphLeading = 0.9f;
			currencyFontSize = 54;
			iPartFontSize = 74;
			fPartFontSize = 66;
			priceIndentation = 40;
			misuraBigIndentation = 20;
			misuraSmallIndentation = 4;
			misuraFontSize = 24;
			misuraSpacingBefore = 6;
			pesoSpacingBefore = 10;
			pesoIndentationRight = 8;
			pesoFontSize = 12;
			maxSouthCellSize = style.getFormat().getHeight() - descCellHeight - priceCellHeight - 28;
			prefIngrFontSize = 11;
			estimatedLineWidth = 50;
		}
		else if (style == PdfStopperStyle.QUARTO_A4_BN)
		{
			descCellHeight = 80;
			descFontSize = 30;
			priceCellHeight = !hasIngrCell || hasPesoParagraph ? 154 : 130;
			priceParagraphLeading = 0.9f;
			currencyFontSize = 83;
			iPartFontSize = 112;
			fPartFontSize = 100;
			priceIndentation = 52;
			misuraBigIndentation = 30;
			misuraSmallIndentation = 6;
			misuraFontSize = 24;
			misuraSpacingBefore = 10;
			pesoSpacingBefore = 18;
			pesoIndentationRight = 8;
			pesoFontSize = 16;
			maxSouthCellSize = style.getFormat().getHeight() - descCellHeight - priceCellHeight - 28;
			prefIngrFontSize = 14;
			estimatedLineWidth = 50;
		}
		else
		{
			descCellHeight = 48;
			descFontSize = 24;
			priceCellHeight = style.getFormat().getHeight() - 28;
			priceParagraphLeading = 0.8f;
			currencyFontSize = 54;
			iPartFontSize = 74;
			fPartFontSize = 66;
			priceIndentation = 40;
			misuraBigIndentation = 0;
			misuraSmallIndentation = 0;
			misuraFontSize = 24;
			misuraSpacingBefore = 6;
			pesoSpacingBefore = -7;
			pesoIndentationRight = 60;
			pesoFontSize = 11;
			maxSouthCellSize = priceCellHeight - descCellHeight;
			prefIngrFontSize = 11;
			estimatedLineWidth = 60;
		}

		int ingrFontSize = prefIngrFontSize;
		int lineWidth = estimatedLineWidth;
		float southCellHeight;

		while ((southCellHeight = ingrFontSize
				* 1.1f
				* ((stopper.getIngredienti() != null ? stopper.getIngredienti().length() / lineWidth
						+ (stopper.getIngredienti().length() % lineWidth == 0 ? 0 : 1) : 0) + (stopper.getNote() != null ? stopper.getNote().length()
						/ lineWidth + (stopper.getNote().length() % lineWidth == 0 ? 0 : 1) : 0)) + 2) > maxSouthCellSize)
		{
			ingrFontSize--;
			lineWidth = Math.round(lineWidth * (ingrFontSize + 1) / ingrFontSize * 1f);
		}
		// System.out.println(stopper.getDescrizione() + " " + southCellHeight+
		// " " + maxSouthCellSize + " " + ingrFontSize + " " + lineWidth);

		if (style == PdfStopperStyle.OTTAVO_A4_BN || style == PdfStopperStyle.QUARTO_A4_BN)
		{
			stopperTable = new PdfPTable(1);
			stopperTable.setWidthPercentage(100);
		}
		else
		{
			stopperTable = new PdfPTable(2);
			stopperTable.setExtendLastRow(true);
			stopperTable.setWidthPercentage(100);
			stopperTable.setWidths(new float[] { 1.15f, 1f });
		}

		stopperCell.addElement(stopperTable);

		// NORTH

		PdfPCell descriptionCell = new PdfPCell();
		descriptionCell.setBorder(PdfPCell.NO_BORDER);
		// descriptionCell.setBorderColor(BaseColor.RED);
		descriptionCell.setPadding(0);
		descriptionCell.setMinimumHeight(descCellHeight);
		descriptionCell.setVerticalAlignment(style == PdfStopperStyle.STRISCIA_CORTA && !hasIngrCell ? Element.ALIGN_MIDDLE : Element.ALIGN_TOP);

		descriptionParagraph = new Paragraph();
		descriptionParagraph.setAlignment(Element.ALIGN_CENTER);
		descriptionParagraph.setLeading(0, righeDescrizione.size() == 1 ? 1.4f : 0.9f);
		descriptionParagraph.add(makePhrase(righeDescrizione.get(0), FontFactory.getFont(FontStyle.FUTURA_MEDIUM, descFontSize),
				FontFactory.getFont(FontStyle.FUTURA_BOLD, descFontSize)));
		descriptionCell.addElement(descriptionParagraph);

		if (righeDescrizione.size() > 1)
		{
			descriptionParagraph = new Paragraph();
			descriptionParagraph.setAlignment(Element.ALIGN_CENTER);
			descriptionParagraph.setLeading(0, 1f);
			descriptionParagraph.add(makePhrase(righeDescrizione.get(1), FontFactory.getFont(FontStyle.FUTURA_MEDIUM, descFontSize),
					FontFactory.getFont(FontStyle.FUTURA_BOLD, descFontSize)));
			descriptionCell.addElement(descriptionParagraph);
		}

		stopperTable.addCell(descriptionCell);

		// CENTER

		PdfPCell priceCell = new PdfPCell();
		priceCell.setBorder(PdfPCell.NO_BORDER);
		priceCell.setBorderColor(BaseColor.RED);
		priceCell.setPadding(0);
		priceCell.setFixedHeight(priceCellHeight);
		priceCell.setVerticalAlignment(Element.ALIGN_TOP);
		if (style == PdfStopperStyle.STRISCIA_CORTA && hasIngrCell)
			priceCell.setRowspan(2);

		priceParagraph = new Paragraph();
		priceParagraph.setAlignment(style == PdfStopperStyle.STRISCIA_CORTA ? Element.ALIGN_RIGHT : Element.ALIGN_CENTER);
		priceParagraph.setLeading(0, priceParagraphLeading);

		if (prezzo != null)
		{
			Phrase pricePhrase = new Phrase(3);

			iPart = (long) prezzo.getValue();
			fPart = Math.round(((prezzo.getValue() - iPart) * 100));

			pricePhrase.add(new Chunk("€ ", FontFactory.getFont(FontStyle.FUTURA_MEDIUM, currencyFontSize)).setCharacterSpacing(-1f));
			pricePhrase.add(new Chunk(iPart + "", FontFactory.getFont(FontStyle.FUTURA_HEAVY, iPartFontSize)).setCharacterSpacing(-3f));
			pricePhrase.add(new Chunk("," + (fPart < 10 ? "0" + fPart : fPart), FontFactory.getFont(FontStyle.FUTURA_HEAVY, fPartFontSize)));
			priceParagraph.add(pricePhrase);
			priceParagraph.setIndentationRight(priceIndentation);
		}
		priceCell.addElement(priceParagraph);

		if (qta != null)
		{
			/*
			 * Paragraph slashParagraph = new Paragraph();
			 * slashParagraph.setIndentationLeft (78f));
			 * slashParagraph.setSpacingBefore(-10); slashParagraph.add(new
			 * Chunk("/", FontFactory.getFont(FontStyle.BOOK, 18)));
			 * 
			 * centerCell.addElement(slashParagraph);
			 */

			Paragraph misuraParagraph = new Paragraph();
			misuraParagraph.setSpacingBefore(-misuraSpacingBefore);
			misuraParagraph.setAlignment(Element.ALIGN_RIGHT);
			misuraParagraph.setIndentationRight((iPart < 10 ? misuraBigIndentation : misuraSmallIndentation));
			misuraParagraph.add(new Chunk(qta.getValore() != null || qta.getUnitaMisura() == UnitaMisura.PZ ? "cad" : qta.getUnitaMisura().getSing(),
					FontFactory.getFont(FontStyle.FUTURA_MEDIUM, misuraFontSize)));

			priceCell.addElement(misuraParagraph);

			if (qta.getValore() != null)
			{
				// centerCell.addElement(new LineSeparator(0.5f, 67,
				// BaseColor.LIGHT_GRAY, Element.ALIGN_CENTER, 0));
				Paragraph pesoParagraph = new Paragraph();
				pesoParagraph.setLeading(0, 1.0f);
				pesoParagraph.setSpacingBefore(pesoSpacingBefore);
				pesoParagraph.setIndentationRight(pesoIndentationRight);
				pesoParagraph.setAlignment(style == PdfStopperStyle.STRISCIA_CORTA ? Element.ALIGN_RIGHT : Element.ALIGN_CENTER);
				pesoParagraph.add(new Chunk("Peso netto: " + FormatUtils.optionalDecimalDigitsFormat.format(qta.getValore()) + " "
						+ qta.getUnitaMisura().getAbbrSing() + " ", FontFactory.getFont(FontStyle.FUTURA_MEDIUM, pesoFontSize)));
				// FontFactory.estimatedSign.scaleToFit(100, 7);
				// pesoParagraph.add(new
				// Chunk(FontFactory.estimatedSign,0,0));
				pesoParagraph.add(new Chunk(style == PdfStopperStyle.STRISCIA_CORTA ? "         " : "    ", FontFactory.getFont(FontStyle.FUTURA_MEDIUM,
						pesoFontSize)));
				String prezzoKg = "€ "
						+ FormatUtils.twoDecimalDigitsFormat.format(FormatUtils.roundToDecimalDigits(
								Quantita.getPrezzoPerUM(qta, stopper.getPrezzo().getValue()), 2)) + "/"
						+ qta.getUnitaMisura().getNormalizzata().getAbbrSing();
				pesoParagraph.add(new Chunk(prezzoKg, FontFactory.getFont(FontStyle.FUTURA_MEDIUM, pesoFontSize)));
				priceCell.addElement(pesoParagraph);
				// centerCell.addElement(new LineSeparator(0.5f, 67,
				// BaseColor.LIGHT_GRAY, Element.ALIGN_CENTER, -5));
			}
		}

		stopperTable.addCell(priceCell);

		if (hasIngrCell)
		{
			PdfPCell ingrCell = new PdfPCell();
			ingrCell.setPadding(0);
			ingrCell.setFixedHeight(southCellHeight);
			ingrCell.setBorder(PdfPCell.NO_BORDER);
			ingrCell.setVerticalAlignment(Element.ALIGN_TOP);

			PdfPTable bottomTable = new PdfPTable(3);
			bottomTable.setWidthPercentage(100);
			bottomTable.setWidths(new float[] { 0.13f, 0.74f, 0.13f });

			PdfPCell cell;

			if (stopper.getIngredienti() != null)
			{
				cell = new PdfPCell();
				cell.setBorder(PdfPCell.NO_BORDER);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPadding(0);
				cell.setPaddingRight(5);
				Paragraph p = new Paragraph();
				p.setAlignment(Element.ALIGN_RIGHT);
				p.setLeading(0, 1.0f);
				p.add(new Chunk("Ingr.:", FontFactory.getFont(FontStyle.FUTURA_MEDIUM, ingrFontSize)));
				cell.addElement(p);
				bottomTable.addCell(cell);

				cell = new PdfPCell();
				cell.setBorder(PdfPCell.NO_BORDER);
				cell.setPadding(0);
				cell.setColspan(2);
				Paragraph ingredientiParagraph = new Paragraph();
				ingredientiParagraph.setLeading(0, 1.1f);
				ingredientiParagraph.setSpacingAfter(2);
				ingredientiParagraph.add(makeIngredientiPhrase(stopper.getIngredienti(), ingrFontSize));
				cell.addElement(ingredientiParagraph);
				bottomTable.addCell(cell);
			}

			if (stopper.getNote() != null)
			{
				cell = new PdfPCell();
				cell.setBorder(PdfPCell.NO_BORDER);
				cell.setPadding(0);
				bottomTable.addCell(cell);

				cell = new PdfPCell();
				cell.setBorder(PdfPCell.NO_BORDER);
				cell.setPadding(0);
				Paragraph ingredientiParagraph = new Paragraph();
				ingredientiParagraph.setLeading(0, 1.1f);
				ingredientiParagraph.add(makeIngredientiPhrase(stopper.getNote(), ingrFontSize));
				cell.addElement(ingredientiParagraph);
				bottomTable.addCell(cell);

				cell = new PdfPCell();
				cell.setBorder(PdfPCell.NO_BORDER);
				cell.setPadding(0);
				bottomTable.addCell(cell);
			}

			ingrCell.addElement(bottomTable);
			stopperTable.addCell(ingrCell);

		}

		return stopperCell;

	}

	private Phrase makeIngredientiPhrase(String ingredienti, int ingrFontSize)
	{
		Phrase ingredientiPhrase = new Phrase();

		Matcher matcher = StringUtils.asteriskIncludedToken.matcher(ingredienti);
		int nonBoldTokenStart = 0;
		while (matcher.find())
		{
			String nonBoldWord = ingredienti.substring(nonBoldTokenStart, matcher.start());
			if (nonBoldWord.length() > 0)
				ingredientiPhrase.add(new Chunk(nonBoldWord, FontFactory.getFont(FontStyle.FUTURA_MEDIUM, ingrFontSize)));

			String boldWord = matcher.group(2);
			if (boldWord != null && boldWord.length() > 0)
				ingredientiPhrase.add(new Chunk(boldWord, FontFactory.getFont(FontStyle.FUTURA_BOLD, ingrFontSize)));

			nonBoldTokenStart = matcher.end();
		}

		return ingredientiPhrase;

	}
}
