package fpozzi.stopper.model.pdf.cell;

import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Utilities;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;

import fpozzi.gdoshop.model.articolo.Quantita;
import fpozzi.gdoshop.model.articolo.UnitaMisura;
import fpozzi.stopper.model.FruttaStopper;
import fpozzi.stopper.model.pdf.PdfStopperStyle;
import fpozzi.stopper.model.pdf.UnsupportedStyleException;
import fpozzi.stopper.model.pdf.style.fonts.FontFactory;
import fpozzi.stopper.model.pdf.style.fonts.FontFactory.FontStyle;
import fpozzi.utils.format.FormatUtils;

public class FruttaPdfCellFactory extends ArticoloPdfCellFactory<FruttaStopper>
{

	public static final FruttaPdfCellFactory instance;

	static
	{
		instance = new FruttaPdfCellFactory(
				new PdfStopperStyle[] { PdfStopperStyle.META_A4_BN, PdfStopperStyle.TERZO_A4_BN,
						PdfStopperStyle.SESTO_A4_BN, PdfStopperStyle.CARTA_SPECIALE_OTTAVO_BORDATO,
						PdfStopperStyle.CARTA_SPECIALE_OFFERTA});
	}

	private FruttaPdfCellFactory(PdfStopperStyle[] supportedStyles)
	{
		super(supportedStyles);
	}

	@Override
	public PdfPCell makePDF(FruttaStopper stopper, PdfStopperStyle style, PdfContentByte pdfContentByte)
			throws UnsupportedStyleException
	{
		if (style==PdfStopperStyle.CARTA_SPECIALE_OFFERTA)
		{
			
		}

		if (style == PdfStopperStyle.META_A4_BN)
		{

			PdfPCell stopperCell = new PdfPCell();

			stopperCell.setCellEvent(new PdfCellDecorator(true));

			stopperCell.setFixedHeight(style.getFormat().getHeight());
			stopperCell.setBorder(PdfPCell.NO_BORDER);
			stopperCell.setPadding(Utilities.millimetersToPoints(12f));

			PdfPTable stopperTable = new PdfPTable(4);
			stopperTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
			stopperTable.getDefaultCell().setPadding(Utilities.millimetersToPoints(3));
			stopperTable.setExtendLastRow(true);
			stopperTable.setWidthPercentage(100);

			stopperCell.addElement(stopperTable);

			// NORTH-WEST

			PdfPCell tastoCell = new PdfPCell();

			tastoCell.setBorder(PdfPCell.NO_BORDER);
			tastoCell.setPadding(0);

			tastoCell.setCellEvent(new PdfPCellEvent()
			{
				public void cellLayout(PdfPCell cell, Rectangle rectangle, PdfContentByte[] canvases)
				{

					final float borderThickness = 2f;
					final float margin = 18f;

					PdfContentByte canvas;

					canvas = canvases[PdfPTable.LINECANVAS];

					canvas.saveState();
					canvas.setLineWidth(borderThickness);
					canvas.setLineDash(4, 8, 1);
					canvas.roundRectangle(rectangle.getLeft() + margin / 2, rectangle.getBottom() + margin / 2,
							rectangle.getWidth() - margin, rectangle.getHeight() - margin, 20);
					canvas.stroke();
					canvas.restoreState();
				}
			});

			Phrase tastoPhrase = new Phrase(2);
			if (stopper.getTasto() == null)
			{
				tastoCell.setPaddingTop(31);
				Paragraph p = new Paragraph(new Chunk("GIÀ\n", FontFactory.getFont(FontStyle.FUTURA_BOLD, 34)));
				p.setAlignment(Element.ALIGN_CENTER);
				p.setLeading(0, 0.8f);
				tastoCell.addElement(p);
				p = new Paragraph(
						new Chunk("PESATO", FontFactory.getFont(FontStyle.FUTURA_BOLD, 20)).setCharacterSpacing(-1.5f));
				p.setAlignment(Element.ALIGN_CENTER);
				p.setLeading(5, 1f);
				tastoCell.addElement(p);
			} else
			{
				tastoCell.setPaddingTop(18);
				Paragraph tastoParagraph = new Paragraph();
				tastoPhrase.add(new Chunk("TASTO\n", FontFactory.getFont(FontStyle.FUTURA_BOLD, 25)).setCharacterSpacing(-1));
				tastoPhrase.add(new Chunk("" + stopper.getTasto(), FontFactory.getFont(FontStyle.FUTURA_BOLD, 45))
						.setCharacterSpacing(-1));
				tastoParagraph.setLeading(0, 1f);
				tastoParagraph.setAlignment(Element.ALIGN_CENTER);
				tastoParagraph.add(tastoPhrase);

				tastoCell.addElement(tastoParagraph);
			}

			stopperTable.addCell(tastoCell);

			// NORTH-EAST

			PdfPCell descrizioneCell = new PdfPCell();
			descrizioneCell.setColspan(3);
			descrizioneCell.setBorder(PdfPCell.NO_BORDER);
			descrizioneCell.setFixedHeight(Utilities.millimetersToPoints(40));
			descrizioneCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

			Phrase descriptionPhrase = new Phrase(2);

			List<String> righeDescrizione = stopper.getRigheDescrizione();

			int fontSizeDescrizione = 59, fontSizeMax = 90, fontSizeMin = 36;
			float fontSpacingDescrizione = -3;
			if (righeDescrizione.get(0).length() > 9)
				fontSizeDescrizione = fontSizeDescrizione - Math
						.round((fontSizeDescrizione - fontSizeMin) * ((righeDescrizione.get(0).length() - 9) / 9f));
			else
			{
				if (righeDescrizione.size() < 2 || righeDescrizione.get(1).equals(""))
					fontSizeDescrizione = fontSizeMax
							- Math.round((fontSizeMax - fontSizeDescrizione) * righeDescrizione.get(0).length() / 9f);

				fontSpacingDescrizione = fontSpacingDescrizione * 1f / righeDescrizione.get(0).length();
			}

			// System.out.print(fontSizeDescrizione + " " +
			// fontSpacingDescrizione);

			descriptionPhrase.add(new Chunk(righeDescrizione.get(0) + "\n",
					FontFactory.getFont(FontStyle.FUTURA_BOLD, fontSizeDescrizione))
							.setCharacterSpacing(fontSpacingDescrizione));

			if (righeDescrizione.size() > 1 && !righeDescrizione.get(1).equals(""))
			{
				descriptionPhrase
						.add(new Chunk(righeDescrizione.get(1), FontFactory.getFont(FontStyle.FUTURA_BOLD, fontSizeMin))
								.setCharacterSpacing(-1.5f));
			}

			Paragraph descriptionParagraph = new Paragraph();
			descriptionParagraph.setAlignment(Element.ALIGN_CENTER);
			descriptionParagraph.setLeading(0, 1.1f);
			descriptionParagraph.setSpacingAfter(Utilities.millimetersToPoints(2.5f));
			descriptionParagraph.add(descriptionPhrase);
			descrizioneCell.addElement(descriptionParagraph);

			stopperTable.addCell(descrizioneCell);

			// CENTER

			PdfPCell centerCell = new PdfPCell();
			centerCell.setBorder(PdfPCell.NO_BORDER);
			centerCell.setColspan(4);
			centerCell.setFixedHeight(Utilities.millimetersToPoints(70));

			Paragraph priceParagraph = new Paragraph();
			priceParagraph.setAlignment(Element.ALIGN_CENTER);

			Phrase pricePhrase = new Phrase(3);

			long iPart = (long) stopper.getPrezzo().getValue();
			long fPart = Math.round(((stopper.getPrezzo().getValue() - iPart) * 100));

			pricePhrase.add(new Chunk("€" + (iPart < 10 ? " " : ""), FontFactory.getFont(FontStyle.FUTURA_BOOK, 80)));
			pricePhrase.add(
					new Chunk(iPart + "", FontFactory.getFont(FontStyle.FUTURA_BOLD, 140)).setCharacterSpacing(-5f));
			pricePhrase.add(new Chunk("," + (fPart < 10 ? "0" + fPart : fPart),
					FontFactory.getFont(FontStyle.FUTURA_BOLD, 100)));
			priceParagraph.add(pricePhrase);
			priceParagraph.setLeading(0, 1f);
			priceParagraph.setIndentationRight(Utilities.millimetersToPoints(18f));

			centerCell.addElement(priceParagraph);

			Quantita qta = stopper.getQuantita().get(0);

			Paragraph misuraParagraph = new Paragraph();
			misuraParagraph.setAlignment(Element.ALIGN_RIGHT);
			misuraParagraph.setIndentationRight(Utilities.millimetersToPoints(20f));
			misuraParagraph.setSpacingBefore(-3);
			misuraParagraph.add(new Chunk(qta.getValore() == null ? qta.getUnitaMisura().getSing() : "cad",
					FontFactory.getFont(FontStyle.FUTURA_BOOK, 30)));

			centerCell.addElement(misuraParagraph);

			Paragraph prezzoPerUMParagraph = new Paragraph();
			prezzoPerUMParagraph.setAlignment(Element.ALIGN_RIGHT);
			prezzoPerUMParagraph.setSpacingBefore(5);
			prezzoPerUMParagraph.setIndentationRight(Utilities.millimetersToPoints(47f));
			String prezzoPerUM = "";
			if (qta.getValore() != null)
			{
				prezzoPerUM = "Peso netto: " + FormatUtils.optionalDecimalDigitsFormat.format(qta.getValore()) + " "
						+ qta.getUnitaMisura().getAbbrPlur() + "         " + "€ " + FormatUtils.twoDecimalDigitsFormat
								.format(Quantita.getPrezzoPerUM(qta, stopper.getPrezzo().getValue()))
						+ " / kg";
			}
			prezzoPerUMParagraph.add(new Chunk(prezzoPerUM, FontFactory.getFont(FontStyle.FUTURA_BOOK, 20)));

			centerCell.addElement(prezzoPerUMParagraph);

			stopperTable.addCell(centerCell);

			// SOUTH-WEST

			PdfPCell origineCell = new PdfPCell();
			origineCell.setBorder(PdfPCell.NO_BORDER);
			origineCell.setColspan(2);
			origineCell.setPaddingLeft(Utilities.millimetersToPoints(5f));

			Paragraph origineParagraph = new Paragraph();
			origineParagraph.setAlignment(Element.ALIGN_LEFT);
			origineParagraph.setLeading(0, 1.1f);
			origineParagraph.setSpacingAfter(Utilities.millimetersToPoints(2.5f));
			origineParagraph.add(
					new Chunk("Origine: ", FontFactory.getFont(FontStyle.FUTURA_BOOK, 24)).setCharacterSpacing(-1));
			origineParagraph.add(new Chunk(stopper.getOrigine(), FontFactory.getFont(FontStyle.FUTURA_HEAVY, 24))
					.setCharacterSpacing(-1));
			origineCell.addElement(origineParagraph);

			stopperTable.addCell(origineCell);

			// SOUTH-EAST

			PdfPCell categoriaCell = new PdfPCell();
			categoriaCell.setBorder(PdfPCell.NO_BORDER);
			categoriaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			categoriaCell.setPaddingRight(Utilities.millimetersToPoints(5f));
			categoriaCell.setColspan(2);

			Paragraph categoriaParagraph = new Paragraph();
			categoriaParagraph.setAlignment(Element.ALIGN_RIGHT);
			categoriaParagraph.setLeading(0, 1.1f);
			categoriaParagraph.setSpacingAfter(Utilities.millimetersToPoints(2.5f));
			categoriaParagraph.add(
					new Chunk("Categoria: ", FontFactory.getFont(FontStyle.FUTURA_BOOK, 24)).setCharacterSpacing(-1));
			categoriaParagraph.add(new Chunk(stopper.getCategoria().getAlternativeText(),
					FontFactory.getFont(FontStyle.FUTURA_HEAVY, 24)).setCharacterSpacing(-1));
			categoriaCell.addElement(categoriaParagraph);

			stopperTable.addCell(categoriaCell);

			return stopperCell;
		}

		else if (style == PdfStopperStyle.CARTA_SPECIALE_OTTAVO_BORDATO)
		{

			PdfPCell stopperCell = new PdfPCell();

			stopperCell.setCellEvent(new PdfCellDecorator(false));

			stopperCell.setFixedHeight(style.getFormat().getHeight());
			stopperCell.setBorder(PdfPCell.NO_BORDER);
			stopperCell.setPadding(Utilities.millimetersToPoints(5f));

			PdfPTable stopperTable = new PdfPTable(4);
			stopperTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
			stopperTable.getDefaultCell().setPadding(Utilities.millimetersToPoints(3));
			stopperTable.setExtendLastRow(true);
			stopperTable.setWidthPercentage(100);

			stopperCell.addElement(stopperTable);

			// NORTH

			PdfPCell descrizioneCell = new PdfPCell();
			descrizioneCell.setColspan(4);
			descrizioneCell.setBorder(PdfPCell.NO_BORDER);
			descrizioneCell.setFixedHeight(Utilities.millimetersToPoints(21));
			descrizioneCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

			Phrase descriptionPhrase = new Phrase(2);

			List<String> righeDescrizione = stopper.getRigheDescrizione();
			int fontSizeDescrizione = 24, fontSizeMax = 34, fontSizeMin = 18;
			float fontSpacingDescrizione = -1;
			if (righeDescrizione.get(0).length() > 16)
				fontSizeDescrizione = fontSizeDescrizione - Math
						.round((fontSizeDescrizione - fontSizeMin) * ((righeDescrizione.get(0).length() - 16) / 16f));
			/*
			 * else { if (righeDescrizione.size() < 2 || righeDescrizione.get(1).equals(""))
			 * fontSizeDescrizione = fontSizeMax - Math.round((fontSizeMax -
			 * fontSizeDescrizione) * righeDescrizione.get(0).length() / 16f);
			 * 
			 * fontSpacingDescrizione = fontSpacingDescrizione * 1f /
			 * righeDescrizione.get(0).length(); }
			 */
			fontSpacingDescrizione = fontSpacingDescrizione * 1f / righeDescrizione.get(0).length();
			// System.out.print(fontSizeDescrizione + " " +
			// fontSpacingDescrizione);

			descriptionPhrase.add(new Chunk(righeDescrizione.get(0) + "\n",
					FontFactory.getFont(FontStyle.FUTURA_MEDIUM, fontSizeDescrizione))
							.setCharacterSpacing(fontSpacingDescrizione));

			if (righeDescrizione.size() > 1 && !righeDescrizione.get(1).equals(""))
			{
				if (righeDescrizione.get(1).length() > 16)
					fontSizeDescrizione = fontSizeDescrizione - Math.round(
							(fontSizeDescrizione - fontSizeMin) * ((righeDescrizione.get(1).length() - 16) / 16f));

				fontSpacingDescrizione = fontSpacingDescrizione * 1f / righeDescrizione.get(1).length();

				descriptionPhrase.add(new Chunk(righeDescrizione.get(1),
						FontFactory.getFont(FontStyle.FUTURA_MEDIUM, fontSizeDescrizione))
								.setCharacterSpacing(fontSpacingDescrizione));
			}

			Paragraph descriptionParagraph = new Paragraph();
			descriptionParagraph.setAlignment(Element.ALIGN_CENTER);
			descriptionParagraph.setLeading(0, 1.0f);
			// descriptionParagraph.setSpacingAfter(Utilities.millimetersToPoints(2.5f));
			descriptionParagraph.add(descriptionPhrase);

			Paragraph additiviParagraph = null;
			if (stopper.getAdditivi() != null && !stopper.getAdditivi().equals(""))
			{
				additiviParagraph = new Paragraph();
				additiviParagraph.setAlignment(Element.ALIGN_CENTER);
				additiviParagraph.setLeading(0, 0.9f);
				if (righeDescrizione.size() > 1)
				{
					descriptionParagraph.setLeading(0, 0.9f);
					additiviParagraph.setLeading(0, 0.5f);
				}
				additiviParagraph
						.add(new Chunk("\n" + stopper.getAdditivi(), FontFactory.getFont(FontStyle.FUTURA_BOOK, 11)));

			}

			descrizioneCell.addElement(descriptionParagraph);
			if (additiviParagraph != null)
				descrizioneCell.addElement(additiviParagraph);

			stopperTable.addCell(descrizioneCell);

			// CENTER-EAST

			PdfPCell centerCell = new PdfPCell();
			centerCell.setBorder(PdfPCell.NO_BORDER);
			centerCell.setColspan(3);
			centerCell.setFixedHeight(Utilities.millimetersToPoints(37));

			Paragraph priceParagraph = new Paragraph();
			priceParagraph.setAlignment(Element.ALIGN_LEFT);

			Phrase pricePhrase = new Phrase(3);

			long iPart = (long) stopper.getPrezzo().getValue();
			long fPart = Math.round(((stopper.getPrezzo().getValue() - iPart) * 100));

			pricePhrase.add(new Chunk("€" + (iPart < 10 ? " " : ""), FontFactory.getFont(FontStyle.FUTURA_MEDIUM, 30)));
			pricePhrase.add(new Chunk(iPart + "", FontFactory.getFont(FontStyle.FUTURA_HEAVY, 64))
					.setCharacterSpacing(iPart < 10 ? -3f : -9f));
			pricePhrase.add(
					new Chunk("," + (fPart < 10 ? "0" + fPart : fPart), FontFactory.getFont(FontStyle.FUTURA_HEAVY, 56))
							.setCharacterSpacing(iPart < 10 ? 0 : -2f));
			priceParagraph.add(pricePhrase);
			priceParagraph.setLeading(0, 0.9f);
			priceParagraph.setIndentationLeft(Utilities.millimetersToPoints(iPart < 10 ? 5f : 2f));

			centerCell.addElement(priceParagraph);

			Quantita qta = stopper.getQuantita().get(0);

			Paragraph misuraParagraph = new Paragraph();
			misuraParagraph.setAlignment(Element.ALIGN_LEFT);
			misuraParagraph.setIndentationLeft(Utilities.millimetersToPoints(56f));
			misuraParagraph.setLeading(0, 0.5f);
			misuraParagraph.setSpacingBefore(-2);
			misuraParagraph.add(new Chunk(qta.getValore() == null ? qta.getUnitaMisura().getAbbrSing() : "cad",
					FontFactory.getFont(FontStyle.FUTURA_MEDIUM, 22)));

			centerCell.addElement(misuraParagraph);

			Paragraph prezzoPerUMParagraph = new Paragraph();
			prezzoPerUMParagraph.setAlignment(Element.ALIGN_LEFT);
			prezzoPerUMParagraph.setIndentationLeft(Utilities.millimetersToPoints(5f));
			prezzoPerUMParagraph.setLeading(0, 1.8f);
			if (qta.getValore() != null)
			{
				prezzoPerUMParagraph.add(new Chunk(
						qta.getUnitaMisura().getNormalizzata() == UnitaMisura.KG ? "Peso netto: " : "Contenuto: ",
						FontFactory.getFont(FontStyle.FUTURA_BOOK, 11)));
				prezzoPerUMParagraph.add(new Chunk(FormatUtils.optionalDecimalDigitsFormat.format(qta.getValore()) + " "
						+ qta.getUnitaMisura().getAbbrPlur(), FontFactory.getFont(FontStyle.FUTURA_HEAVY, 11)));
				prezzoPerUMParagraph.add(new Chunk(
						"     € "
								+ FormatUtils.twoDecimalDigitsFormat
										.format(Quantita.getPrezzoPerUM(qta, stopper.getPrezzo().getValue()))
								+ "/" + qta.getUnitaMisura().getNormalizzata().getAbbrSing(),
						FontFactory.getFont(FontStyle.FUTURA_BOOK, 11)));
			} else if (qta.getUnitaMisura().getNormalizzata() == UnitaMisura.KG
					&& qta.getUnitaMisura() != UnitaMisura.KG)
			{
				prezzoPerUMParagraph.setLeading(0, 1.3f);
				prezzoPerUMParagraph.setAlignment(Element.ALIGN_CENTER);
				prezzoPerUMParagraph.add(new Chunk(
						"al " + qta.getUnitaMisura().getNormalizzata().getAbbrSing() + " € "
								+ FormatUtils.twoDecimalDigitsFormat
										.format(Quantita.getPrezzoPerUM(qta, stopper.getPrezzo().getValue())),
						FontFactory.getFont(FontStyle.FUTURA_BOOK, 11)));
			}

			centerCell.addElement(prezzoPerUMParagraph);

			stopperTable.addCell(centerCell);

			// CENTER-WEST

			PdfPCell tastoCell = new PdfPCell();

			tastoCell.setBorder(PdfPCell.NO_BORDER);

			tastoCell.setCellEvent(new PdfPCellEvent()
			{
				public void cellLayout(PdfPCell cell, Rectangle rectangle, PdfContentByte[] canvases)
				{

					final float borderThickness = 0.5f;
					final float margin = 10f;

					PdfContentByte canvas;

					canvas = canvases[PdfPTable.LINECANVAS];

					canvas.saveState();
					canvas.setLineWidth(borderThickness);
					// canvas.setLineDash(4, 8, 1);
					canvas.roundRectangle(rectangle.getLeft() + margin / 2, rectangle.getBottom() + 42 + margin / 2,
							rectangle.getWidth() - margin, rectangle.getHeight() - margin - 40, 5);
					/*
					 * canvas.roundRectangle(rectangle.getLeft() + margin / 2, rectangle.getBottom()
					 * + margin / 2, rectangle.getWidth() - margin, rectangle.getHeight() - margin,
					 * 20);
					 */
					canvas.stroke();
					canvas.restoreState();
				}
			});

			Phrase tastoPhrase = new Phrase(2);
			if (stopper.getTasto() == null || qta.getValore() != null)
			{
				tastoCell.setPaddingTop(12);
				Paragraph p = new Paragraph(new Chunk("GIÀ\n", FontFactory.getFont(FontStyle.FUTURA_MEDIUM, 16)));
				p.setAlignment(Element.ALIGN_CENTER);
				p.setLeading(0, 0.8f);
				tastoCell.addElement(p);
				p = new Paragraph(new Chunk("PESATO", FontFactory.getFont(FontStyle.FUTURA_MEDIUM, 16))
						.setCharacterSpacing(-1.5f));
				p.setAlignment(Element.ALIGN_CENTER);
				p.setLeading(5, 1f);
				tastoCell.addElement(p);
			} else
			{
				tastoCell.setPaddingTop(9);
				Paragraph tastoParagraph = new Paragraph();
				tastoPhrase.add(
						new Chunk("TASTO\n", FontFactory.getFont(FontStyle.FUTURA_MEDIUM, 12)).setCharacterSpacing(-1));
				tastoPhrase.add(new Chunk("" + stopper.getTasto(), FontFactory.getFont(FontStyle.FUTURA_HEAVY, 24))
						.setCharacterSpacing(-1));
				tastoParagraph.setLeading(0, 1.1f);
				tastoParagraph.setAlignment(Element.ALIGN_CENTER);
				tastoParagraph.add(tastoPhrase);

				tastoCell.addElement(tastoParagraph);
			}

			stopperTable.addCell(tastoCell);

			// SOUTH-WEST

			PdfPCell origineCell = new PdfPCell();
			origineCell.setBorder(PdfPCell.NO_BORDER);
			origineCell.setColspan(4);

			Paragraph origineParagraph = new Paragraph();
			origineParagraph.setIndentationLeft(Utilities.millimetersToPoints(5f));
			origineParagraph.setAlignment(Element.ALIGN_LEFT);
			origineParagraph.setLeading(0, .1f);
			origineParagraph.add(new Chunk("Origine: ", FontFactory.getFont(FontStyle.FUTURA_BOOK, 11)));
			origineParagraph.add(new Chunk(stopper.getOrigine(), FontFactory.getFont(FontStyle.FUTURA_HEAVY, 11)));

			origineParagraph.add(new Chunk("    Cat.: " + stopper.getCategoria().getAlternativeText(),
					FontFactory.getFont(FontStyle.FUTURA_BOOK, 11)));

			if (stopper.getCalibro() != null && !stopper.getCalibro().equals(""))
				origineParagraph.add(
						new Chunk("    Cal.: " + stopper.getCalibro(), FontFactory.getFont(FontStyle.FUTURA_BOOK, 11)));

			origineCell.addElement(origineParagraph);

			stopperTable.addCell(origineCell);

			return stopperCell;
		}

		float[] columns;
		int infoHeaderFontSize, tastoFontSize, infoFontSize, calibroFontSize;
		float tastoPaddingBottom, infoPadding, infoPaddingBottom;
		int descrizioneDefaultFontSize, descrizioneFontSizeMax, descrizioneFontSizeMin;
		int additiviFontSize;
		float descrizioneFontSpacing, descrizioneCellHeight;
		int prezzoEuroFontSize, prezzoIntegerFontSize, prezzoDecimalFontSize;
		int misuraFontSize, prezzoPerUMSpacing, prezzoPerUmFontSize;

		if (style == PdfStopperStyle.TERZO_A4_BN)
		{
			columns = new float[] { 1.1f, 3 };
			infoHeaderFontSize = 12;
			tastoFontSize = 59;
			tastoPaddingBottom = Utilities.millimetersToPoints(7f);
			infoFontSize = 24;
			calibroFontSize = 20;
			infoPadding = Utilities .millimetersToPoints(3f);
			infoPaddingBottom = Utilities.millimetersToPoints(5f);
			descrizioneDefaultFontSize = 59;
			descrizioneFontSizeMax = 90;
			descrizioneFontSizeMin = 36;
			descrizioneFontSpacing = -3;
			descrizioneCellHeight = Utilities.millimetersToPoints(37);
			additiviFontSize = 12;
			prezzoEuroFontSize = 80;
			prezzoIntegerFontSize = 120;
			prezzoDecimalFontSize = 90;
			misuraFontSize = 30;
			prezzoPerUmFontSize = 16;
			prezzoPerUMSpacing = 12;
		}
		else if (style == PdfStopperStyle.SESTO_A4_BN)
		{
			columns = new float[] { 1.1f, 3 };
			infoHeaderFontSize = 8;
			tastoFontSize = 45;
			tastoPaddingBottom = Utilities.millimetersToPoints(5f);
			infoFontSize = 16;
			calibroFontSize = 14;
			infoPadding = Utilities.millimetersToPoints(1f);
			infoPaddingBottom = Utilities.millimetersToPoints(3f);
			descrizioneDefaultFontSize = 40;
			descrizioneFontSizeMax = 60;
			descrizioneFontSizeMin = 24;
			descrizioneFontSpacing = -2;
			descrizioneCellHeight = Utilities.millimetersToPoints(27);
			additiviFontSize = 11;
			prezzoEuroFontSize = 50;
			prezzoIntegerFontSize = 77;
			prezzoDecimalFontSize = 60;
			misuraFontSize = 22;
			prezzoPerUmFontSize = 11;
			prezzoPerUMSpacing = -2;		
		} else
			throw new UnsupportedStyleException();

		Quantita qta = stopper.getQuantita().get(0);

		PdfPCell stopperCell = new PdfPCell();

		stopperCell.setCellEvent(new PdfCellDecorator(true));

		stopperCell.setFixedHeight(style.getFormat().getHeight());
		stopperCell.setBorder(PdfPCell.NO_BORDER);
		stopperCell.setPadding(Utilities.millimetersToPoints(5f));

		PdfPTable stopperTable = new PdfPTable(columns);
		stopperTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		stopperTable.setExtendLastRow(true);
		stopperTable.setWidthPercentage(100);

		stopperCell.addElement(stopperTable);

		PdfPCell westCell = new PdfPCell();
		westCell.setBorder(PdfPCell.NO_BORDER);
		westCell.setPadding(Utilities.millimetersToPoints(2f));
		westCell.setPaddingLeft(0);

		PdfPTable westTable = new PdfPTable(2);
		westTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		westTable.setExtendLastRow(true);
		westTable.setWidthPercentage(100);

		{

			// TASTO

			PdfPCell tastoCell = new PdfPCell();
			tastoCell.setColspan(2);
			tastoCell.setBorder(PdfPCell.NO_BORDER);
			tastoCell.setPadding(infoPadding);
			tastoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

			Paragraph tastoParagraph = new Paragraph();
			tastoParagraph.setLeading(0, 1f);
			tastoParagraph.setAlignment(Element.ALIGN_LEFT);
			tastoParagraph.add(new Chunk("TASTO", FontFactory.getFont(FontStyle.FUTURA_MEDIUM, infoHeaderFontSize)));
			tastoCell.addElement(tastoParagraph);

			Paragraph numeroTastoParagraph = new Paragraph();
			numeroTastoParagraph.setLeading(0, 1.0f);
			numeroTastoParagraph.setAlignment(Element.ALIGN_CENTER);
			numeroTastoParagraph.add(new Chunk((stopper.getTasto() == null ? "\\" : "" + stopper.getTasto()),
					FontFactory.getFont(FontStyle.FUTURA_BOLD, tastoFontSize)).setCharacterSpacing(-1));
			tastoCell.addElement(numeroTastoParagraph);

			tastoCell.setPaddingBottom(tastoPaddingBottom);
			tastoCell.setCellEvent(new HorizontalLightBorderCellEvent());

			westTable.addCell(tastoCell);

			// PESO
			/*
			 * if (qta.getValore() != null) { PdfPCell pesoCell = new
			 * PdfPCell(); pesoCell.setColspan(2);
			 * pesoCell.setBorder(PdfPCell.NO_BORDER); pesoCell.setCellEvent(new
			 * HorizontalLightBorderCellEvent());
			 * pesoCell.setPadding(Utilities.millimetersToPoints(1f));
			 * 
			 * Paragraph pesoParagraph = new Paragraph();
			 * pesoParagraph.setLeading(0, 1.1f);
			 * pesoParagraph.setAlignment(Element.ALIGN_CENTER);
			 * pesoParagraph.setSpacingAfter(Utilities.millimetersToPoints(
			 * 2f)); pesoParagraph.add(new Chunk("Peso netto: ",
			 * FontFactory.getFont(FontStyle.FUTURA_BOOK, 10))); pesoParagraph.add(new
			 * Chunk(FormatUtils.optionalDecimalDigitsFormat.format(qta.
			 * getValore()) + " " + qta.getUnitaMisura().getAbbrPlur(),
			 * FontFactory.getFont(FontStyle.FUTURA_HEAVY, 14)));
			 * pesoCell.addElement(pesoParagraph);
			 * 
			 * westTable.addCell(pesoCell); }
			 */
			// CATEGORIA

			PdfPCell categoriaCell = new PdfPCell();
			categoriaCell.setBorder(PdfPCell.NO_BORDER);
			categoriaCell.setPadding(infoPadding);
			categoriaCell.setCellEvent(new HorizontalLightBorderCellEvent());
			categoriaCell.setCellEvent(new VerticalLightBorderCellEvent());

			Paragraph categoriaHeaderParagraph = new Paragraph();
			categoriaHeaderParagraph.setLeading(0, 1f);
			categoriaHeaderParagraph.setAlignment(Element.ALIGN_LEFT);
			categoriaHeaderParagraph.add(new Chunk("CAT.", FontFactory.getFont(FontStyle.FUTURA_MEDIUM, infoHeaderFontSize)));
			categoriaCell.addElement(categoriaHeaderParagraph);

			Paragraph categoriaParagraph = new Paragraph();
			categoriaParagraph.setAlignment(Element.ALIGN_CENTER);
			categoriaParagraph.setLeading(0, 1.2f);
			categoriaParagraph.add(new Chunk(stopper.getCategoria().getAlternativeText(),
					FontFactory.getFont(FontStyle.FUTURA_HEAVY, infoFontSize)));
			categoriaCell.addElement(categoriaParagraph);
			categoriaCell.setPaddingBottom(infoPaddingBottom);
			westTable.addCell(categoriaCell);

			// CALIBRO

			PdfPCell calibroCell = new PdfPCell();
			calibroCell.setBorder(PdfPCell.NO_BORDER);
			calibroCell.setPadding(infoPadding);
			calibroCell.setCellEvent(new HorizontalLightBorderCellEvent());

			Paragraph calibroHeaderParagraph = new Paragraph();
			calibroHeaderParagraph.setLeading(0, 1f);
			calibroHeaderParagraph.setAlignment(Element.ALIGN_LEFT);
			calibroHeaderParagraph.add(new Chunk("CALIBRO", FontFactory.getFont(FontStyle.FUTURA_MEDIUM, infoHeaderFontSize)));
			calibroCell.addElement(calibroHeaderParagraph);

			Paragraph calibroParagraph = new Paragraph();
			calibroParagraph.setLeading(0, 1.2f);
			calibroParagraph.setAlignment(Element.ALIGN_CENTER);
			calibroParagraph.add(new Chunk(stopper.getCalibro() == null ? "" : stopper.getCalibro(),
					FontFactory.getFont(FontStyle.FUTURA_HEAVY, calibroFontSize)));
			calibroCell.addElement(calibroParagraph);
			calibroCell.setPaddingBottom(infoPaddingBottom);
			westTable.addCell(calibroCell);

			// ORIGINE

			PdfPCell origineCell = new PdfPCell();
			origineCell.setColspan(2);
			origineCell.setBorder(PdfPCell.NO_BORDER);
			origineCell.setPadding(infoPadding);

			Paragraph origineHeaderParagraph = new Paragraph();
			origineHeaderParagraph.setLeading(0, 1f);
			origineHeaderParagraph.setAlignment(Element.ALIGN_LEFT);
			origineHeaderParagraph.add(new Chunk("ORIGINE", FontFactory.getFont(FontStyle.FUTURA_MEDIUM, infoHeaderFontSize)));
			origineCell.addElement(origineHeaderParagraph);

			Paragraph origineParagraph = new Paragraph();
			origineParagraph.setAlignment(Element.ALIGN_CENTER);
			origineParagraph.setLeading(0, 1.6f);
			origineParagraph.add(new Chunk(stopper.getOrigine(), FontFactory.getFont(FontStyle.FUTURA_HEAVY, infoFontSize)));
			origineCell.addElement(origineParagraph);
			origineCell.setPaddingBottom(infoPaddingBottom);
			westTable.addCell(origineCell);
		}
		westCell.addElement(westTable);
		stopperTable.addCell(westCell);

		PdfPCell eastCell = new PdfPCell();
		eastCell.setBorder(PdfPCell.NO_BORDER);
		eastCell.setPadding(0);
		eastCell.setCellEvent(new VerticalBorderCellEvent());

		PdfPTable eastTable = new PdfPTable(1);
		eastTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		eastTable.setExtendLastRow(true);
		eastTable.setWidthPercentage(100);

		{

			// DESCRIZIONE

			PdfPCell descrizioneCell = new PdfPCell();
			descrizioneCell.setBorder(PdfPCell.NO_BORDER);
			descrizioneCell.setFixedHeight(descrizioneCellHeight);
			descrizioneCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

			Phrase descriptionPhrase = new Phrase(2);

			List<String> righeDescrizione = stopper.getRigheDescrizione();

			if (righeDescrizione.get(0).length() > 9)
				descrizioneDefaultFontSize = descrizioneDefaultFontSize
						- Math.round((descrizioneDefaultFontSize - descrizioneFontSizeMin)
								* ((righeDescrizione.get(0).length() - 9) / 9f));
			else
			{
				if (righeDescrizione.size() < 2 || righeDescrizione.get(1).equals(""))
					descrizioneDefaultFontSize = descrizioneFontSizeMax
							- Math.round((descrizioneFontSizeMax - descrizioneDefaultFontSize)
									* righeDescrizione.get(0).length() / 9f);

				descrizioneFontSpacing = descrizioneFontSpacing * 1f / righeDescrizione.get(0).length();
			}

			// System.out.print(fontSizeDescrizione + " " +
			// fontSpacingDescrizione);

			descriptionPhrase.add(new Chunk(righeDescrizione.get(0) + "\n",
					FontFactory.getFont(FontStyle.FUTURA_BOLD, descrizioneDefaultFontSize))
							.setCharacterSpacing(descrizioneFontSpacing));

			if (righeDescrizione.size() > 1 && !righeDescrizione.get(1).equals(""))
			{
				descriptionPhrase.add(
						new Chunk(righeDescrizione.get(1), FontFactory.getFont(FontStyle.FUTURA_BOLD, descrizioneFontSizeMin))
								.setCharacterSpacing(-1.5f));
			}

			Paragraph descriptionParagraph = new Paragraph();
			descriptionParagraph.setAlignment(Element.ALIGN_CENTER);
			descriptionParagraph.setLeading(0, 1f);
			descriptionParagraph.setSpacingAfter(Utilities.millimetersToPoints(2.5f));
			descriptionParagraph.add(descriptionPhrase);
			descrizioneCell.addElement(descriptionParagraph);

			// ADDITIVI

			if (stopper.getAdditivi() != null)
			{
				Paragraph additiviParagraph = new Paragraph();
				additiviParagraph.setAlignment(Element.ALIGN_CENTER);
				additiviParagraph.setLeading(0, 0.7f);
				additiviParagraph.add(
						new Chunk(stopper.getAdditivi(), FontFactory.getFont(FontStyle.FUTURA_MEDIUM, additiviFontSize)));
				descrizioneCell.addElement(additiviParagraph);
			}

			eastTable.addCell(descrizioneCell);

			// PREZZO

			PdfPCell prezzoCell = new PdfPCell();
			prezzoCell.setBorder(PdfPCell.NO_BORDER);

			Paragraph priceParagraph = new Paragraph();
			priceParagraph.setAlignment(Element.ALIGN_CENTER);

			Phrase pricePhrase = new Phrase(3);

			long iPart = (long) stopper.getPrezzo().getValue();
			long fPart = Math.round(((stopper.getPrezzo().getValue() - iPart) * 100));

			pricePhrase.add(
					new Chunk("€" + (iPart < 10 ? " " : ""), FontFactory.getFont(FontStyle.FUTURA_BOOK, prezzoEuroFontSize)));
			pricePhrase.add(new Chunk(iPart + "", FontFactory.getFont(FontStyle.FUTURA_BOLD, prezzoIntegerFontSize))
					.setCharacterSpacing(-5f));
			pricePhrase.add(new Chunk("," + (fPart < 10 ? "0" + fPart : fPart),
					FontFactory.getFont(FontStyle.FUTURA_BOLD, prezzoDecimalFontSize)));
			priceParagraph.add(pricePhrase);
			priceParagraph.setLeading(0, 0.85f);
			priceParagraph.setIndentationRight(Utilities.millimetersToPoints(12f));

			prezzoCell.addElement(priceParagraph);

			Paragraph misuraParagraph = new Paragraph();
			misuraParagraph.setAlignment(Element.ALIGN_RIGHT);
			misuraParagraph.setIndentationRight(Utilities.millimetersToPoints(6f));
			misuraParagraph.setSpacingBefore(-6);
			misuraParagraph.add(new Chunk((qta.getValore() == null ? qta.getUnitaMisura().getAbbrSing() : "cad"),
					FontFactory.getFont(FontStyle.FUTURA_BOOK, misuraFontSize)));

			prezzoCell.addElement(misuraParagraph);

			Paragraph prezzoPerUMParagraph = new Paragraph();
			prezzoPerUMParagraph.setAlignment(Element.ALIGN_CENTER);
			prezzoPerUMParagraph.setSpacingBefore(prezzoPerUMSpacing);
			if (qta.getValore() != null && qta.getUnitaMisura()!=UnitaMisura.PZ)
			{
				prezzoPerUMParagraph.add(new Chunk("Peso netto: " , FontFactory.getFont(FontStyle.FUTURA_MEDIUM, prezzoPerUmFontSize)));
				prezzoPerUMParagraph.add(new Chunk(FormatUtils.optionalDecimalDigitsFormat.format(qta.getValore()) + " "
						+ qta.getUnitaMisura().getAbbrPlur()  , FontFactory.getFont(FontStyle.FUTURA_BOLD, prezzoPerUmFontSize)));
				prezzoPerUMParagraph.add(new Chunk("       € " , FontFactory.getFont(FontStyle.FUTURA_MEDIUM, prezzoPerUmFontSize)));
				prezzoPerUMParagraph.add(new Chunk( FormatUtils.twoDecimalDigitsFormat.format(
						Quantita.getPrezzoPerUM(qta, stopper.getPrezzo().getValue())), 
						FontFactory.getFont(FontStyle.FUTURA_BOLD, prezzoPerUmFontSize)));
				prezzoPerUMParagraph.add(new Chunk(" / kg" , FontFactory.getFont(FontStyle.FUTURA_MEDIUM, prezzoPerUmFontSize)));
			}
			prezzoCell.addElement(prezzoPerUMParagraph);

			eastTable.addCell(prezzoCell);
		}
		eastCell.addElement(eastTable);
		stopperTable.addCell(eastCell);

		return stopperCell;
	}

	final static private float[] dashArray = new float[] { 3.0f, 3.0f };
	final static private float dashPhase = 3;

	private class VerticalBorderCellEvent implements PdfPCellEvent
	{

		@Override
		public void cellLayout(PdfPCell cell, Rectangle position, PdfContentByte[] canvases)
		{
			PdfContentByte canvas = canvases[PdfPTable.LINECANVAS];
			canvas.saveState();
			canvas.setLineDash(dashArray, dashPhase);

			Rectangle rect = new Rectangle(position.getLeft(), position.getBottom(), position.getRight(),
					position.getTop());
			rect.setBorder(Rectangle.BOX);
			rect.setBorderWidthTop(0);
			rect.setBorderWidthBottom(0);
			rect.setBorderWidthRight(0);
			rect.setBorderWidthLeft(0.5f);
			rect.setBorderColorLeft(BaseColor.GRAY);
			canvas.rectangle(rect);

			canvas.stroke();
			canvas.restoreState();

		}
	}

	private class HorizontalLightBorderCellEvent implements PdfPCellEvent
	{

		@Override
		public void cellLayout(PdfPCell cell, Rectangle position, PdfContentByte[] canvases)
		{
			PdfContentByte canvas = canvases[PdfPTable.LINECANVAS];
			canvas.saveState();
			canvas.setLineDash(2, 4, 1);

			Rectangle rect = new Rectangle(position.getLeft(), position.getBottom(), position.getRight(),
					position.getTop());
			rect.setBorder(Rectangle.BOX);
			rect.setBorderWidthTop(0);
			rect.setBorderWidthBottom(0.5f);
			rect.setBorderWidthLeft(0);
			rect.setBorderWidthRight(0f);
			rect.setBorderColorBottom(BaseColor.GRAY);
			canvas.rectangle(rect);

			canvas.stroke();
			canvas.restoreState();

		}
	}

	private class VerticalLightBorderCellEvent implements PdfPCellEvent
	{

		@Override
		public void cellLayout(PdfPCell cell, Rectangle position, PdfContentByte[] canvases)
		{
			PdfContentByte canvas = canvases[PdfPTable.LINECANVAS];
			canvas.saveState();
			canvas.setLineDash(2, 4, 1);

			Rectangle rect = new Rectangle(position.getLeft(), position.getBottom(), position.getRight(),
					position.getTop());
			rect.setBorder(Rectangle.BOX);
			rect.setBorderWidthTop(0);
			rect.setBorderWidthBottom(0);
			rect.setBorderWidthLeft(0);
			rect.setBorderWidthRight(0.5f);
			rect.setBorderColorRight(BaseColor.GRAY);
			canvas.rectangle(rect);

			canvas.stroke();
			canvas.restoreState();

		}
	}
}
