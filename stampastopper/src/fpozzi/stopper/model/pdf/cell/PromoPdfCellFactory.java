package fpozzi.stopper.model.pdf.cell;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Utilities;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;

import fpozzi.gdoshop.model.offerta.PeriodoOfferta;
import fpozzi.stopper.StampaStopperProperties;
import fpozzi.stopper.StampaStopperProperties.StampaStopperProperty;
import fpozzi.stopper.model.PromoStopper;
import fpozzi.stopper.model.pdf.PdfStopperStyle;
import fpozzi.stopper.model.pdf.UnsupportedStyleException;
import fpozzi.stopper.model.pdf.style.fonts.FontFactory;
import fpozzi.stopper.model.pdf.style.fonts.FontFactory.FontStyle;
import fpozzi.utils.date.DateUtils;

public abstract class PromoPdfCellFactory<S extends PromoStopper> extends ArticoloPdfCellFactory<S>
{

	public static final Image soloTitolariPremiaySestoA4;

	static
	{
		Image image = null;
		image = null;
		try
		{
			image = Image.getInstance(PromoPdfCellFactory.class.getResource("/img/pdf/solotitolaripremiaty.png"));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		soloTitolariPremiaySestoA4 = image;
	}

	public PromoPdfCellFactory(PdfStopperStyle[] supportedStyles)
	{
		super(supportedStyles);
		
		if (StampaStopperProperties.getInstance().getProperty(StampaStopperProperty.FONT_PROMO).equals("clan"))
		{
			normalFontStyle = FontStyle.CLAN_BOOK;
			mediumFontStyle = FontStyle.CLAN_NARROW;
			boldFontStyle=FontStyle.CLAN_ULTRA;
			heavyFontStyle=FontStyle.CLAN_BOLD;
		}
		else
		{
			normalFontStyle = FontStyle.FUTURA_BOOK;
			mediumFontStyle = FontStyle.FUTURA_MEDIUM;
			boldFontStyle=FontStyle.FUTURA_BOLD;
			heavyFontStyle=FontStyle.FUTURA_HEAVY;
		}
		
	}

	public abstract Paragraph makeHeaderParagraph();
	
	protected final FontStyle normalFontStyle, mediumFontStyle, heavyFontStyle, boldFontStyle;

	public PdfPCell makePDF(final S stopper, PdfStopperStyle style, PdfContentByte pdfContentByte)
			throws UnsupportedStyleException
	{
		PdfPCell stopperCell = new PdfPCell();

		stopperCell.setFixedHeight(style.getFormat().getHeight());
		stopperCell.setBorder(PdfPCell.NO_BORDER);
		stopperCell.setPadding(0);

		PdfPTable stopperTable = new PdfPTable(2);
		stopperTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		stopperTable.getDefaultCell().setPadding(Utilities.millimetersToPoints(3));
		stopperTable.setWidthPercentage(100);
		stopperTable.setExtendLastRow(true);

		PdfCellDecorator decorator = null;
		PdfPCell northCell, westCell, southCell;
		Paragraph descriptionParagraph, extrainfosParagraph, maxPezziParagraph, quantitaParagraph = null;

		switch (style)
		{
			case SESTO_A4_BN:
				decorator = new PdfCellDecorator(true);

				// NORTH

				northCell = new PdfPCell();
				northCell.setFixedHeight(Utilities.millimetersToPoints(20));
				northCell.setBorder(PdfPCell.NO_BORDER);
				northCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				northCell.setColspan(2);
				northCell.setPaddingTop(Utilities.millimetersToPoints(12));
				northCell.setPaddingLeft(Utilities.millimetersToPoints(8));
				northCell.addElement(makeHeaderParagraph());

				stopperTable.addCell(northCell);

				// WEST

				westCell = new PdfPCell();
				westCell.setBorder(PdfPCell.NO_BORDER);
				westCell.setFixedHeight(Utilities.millimetersToPoints(38));
				westCell.setPaddingRight(Utilities.millimetersToPoints(5));

				descriptionParagraph = makeDescrizioneParagraph(stopper, FontFactory.getFont(boldFontStyle, 17),
						FontFactory.getFont(normalFontStyle, 16));
				descriptionParagraph.setAlignment(Element.ALIGN_RIGHT);
				descriptionParagraph.setLeading(0, 1.1f);
				descriptionParagraph.setSpacingAfter(Utilities.millimetersToPoints(2.5f));
				westCell.addElement(descriptionParagraph);

				quantitaParagraph = makeQuantitaParagraph(stopper, 15, 11);
				westCell.addElement(quantitaParagraph);

				if (stopper.getRigheDescrizione().size() < 5 && quantitaParagraph==null)
				{
					for (int i = 0; i < 3 - stopper.getRigheDescrizione().size(); i++)
						westCell.addElement(new Chunk("\n"));

					if (stopper.getCodiceInterno() != null)
					{
						Paragraph codeParagraph = new Paragraph();
						codeParagraph.setSpacingBefore(15f);
						codeParagraph.setLeading(1, 1.0f);
						codeParagraph.setIndentationRight(Utilities.millimetersToPoints(0.5f));
						codeParagraph.setAlignment(Element.ALIGN_RIGHT);
						codeParagraph.add(new Chunk("cod. " + stopper.getCodiceInterno(),
								FontFactory.getFont(normalFontStyle, 9)));
						westCell.addElement(codeParagraph);
					}
				}

				westCell.setCellEvent(new PdfPCellEvent()
				{

					@Override
					public void cellLayout(PdfPCell cell, Rectangle position, PdfContentByte[] canvases)
					{

						PdfContentByte canvas = canvases[PdfPTable.LINECANVAS];
						canvas.saveState();

						Rectangle rect = new Rectangle(position.getLeft(), position.getBottom(), position.getRight(),
								position.getTop());
						rect.setBorder(Rectangle.BOX);
						rect.setBorderWidthTop(0);
						rect.setBorderWidthBottom(0);
						rect.setBorderWidthLeft(0);
						rect.setBorderWidthRight(0.8f);
						rect.setBorderColorRight(BaseColor.BLACK);
						canvas.rectangle(rect);
						canvas.stroke();
						canvas.restoreState();
					}

				});

				stopperTable.addCell(westCell);

				// EAST

				stopperTable.addCell(makeEastCell(stopper, style));

				// SOUTH

				southCell = new PdfPCell();
				southCell.setBorder(PdfPCell.NO_BORDER);
				southCell.setColspan(2);
				southCell.setPaddingTop(0);

				extrainfosParagraph = new Paragraph();
				extrainfosParagraph.setAlignment(Element.ALIGN_RIGHT);
				extrainfosParagraph.setIndentationRight(Utilities.millimetersToPoints(10f));

				Chunk dateRangeChunk = new Chunk(" ", FontFactory.getFont(heavyFontStyle, 9));

				if (stopper.getPeriodo() != null && !(stopper.isConCard() && stopper.getPezziMax() > 0))
				{
					dateRangeChunk = new Chunk(makePeriodoOffertaDescription(stopper.getPeriodo(), stopper.isConCard()),
							FontFactory.getFont(heavyFontStyle, 9));
				}

				extrainfosParagraph.add(dateRangeChunk);

				if (stopper.isConCard())
				{
					Chunk fidCardChunk = new Chunk("SOLO CON CARTA PREMIATY",
							FontFactory.getFont(boldFontStyle, 11));
					fidCardChunk.setCharacterSpacing(-0.6f);
					extrainfosParagraph.add(new Chunk("  "));
					extrainfosParagraph.add(fidCardChunk);
				}

				southCell.addElement(extrainfosParagraph);

				if (stopper.getPezziMax() != null && stopper.getPezziMax() > 0)
				{
					maxPezziParagraph = new Paragraph();
					maxPezziParagraph.setIndentationLeft(Utilities.millimetersToPoints(8f));
					maxPezziParagraph.setAlignment(Element.ALIGN_LEFT);
					maxPezziParagraph.setLeading(0.0f, 0f);
					maxPezziParagraph.add(new Chunk("MASSIMO ", FontFactory.getFont(heavyFontStyle, 11))
							.setCharacterSpacing(-0.6f));
					maxPezziParagraph
							.add(new Chunk(stopper.getPezziMax() + "", FontFactory.getFont(boldFontStyle, 14)));
					maxPezziParagraph.add(new Chunk(" PEZZ" + (stopper.getPezziMax()>1? "I":"O")  + 
							" PER CLIENTE", FontFactory.getFont(heavyFontStyle, 11))
							.setCharacterSpacing(-0.6f));
					southCell.addElement(maxPezziParagraph);
				}

				stopperTable.addCell(southCell);

			break;

			case SESTO_A4_COLORI:
			case CARTA_SPECIALE_OFFERTA:
			case CARTA_SPECIALE_SCONTO:
			case CARTA_SPECIALE_UNOPIUUNO:

				decorator = makeCellDecorator(style);

				northCell = new PdfPCell();
				northCell.setFixedHeight(
						Utilities.millimetersToPoints(style == PdfStopperStyle.SESTO_A4_COLORI ? 15 : 14));
				northCell.setBorder(PdfPCell.NO_BORDER);
				northCell.setColspan(2);

				stopperTable.addCell(northCell);

				// WEST

				westCell = new PdfPCell();
				westCell.setBorder(PdfPCell.NO_BORDER);
				westCell.setFixedHeight(Utilities.millimetersToPoints(42));
				westCell.setPaddingRight(Utilities.millimetersToPoints(5));

				descriptionParagraph = makeDescrizioneParagraph(stopper, FontFactory.getFont(boldFontStyle, 15),
						FontFactory.getFont(normalFontStyle, 14));
				descriptionParagraph.setAlignment(Element.ALIGN_RIGHT);
				descriptionParagraph.setLeading(0, 1.2f);
				westCell.addElement(descriptionParagraph);

				quantitaParagraph = makeQuantitaParagraph(stopper, 15, 11);
				westCell.addElement(quantitaParagraph);
					
				westCell.addElement(makeCodiceParagraph(stopper, 15, 8, 
						stopper.getRigheDescrizione().size()+(quantitaParagraph==null?0:1)));
				
				// canvas.addImage(soloTitolariPremiaySestoA4);

				westCell.setCellEvent(new PdfPCellEvent()
				{

					@Override
					public void cellLayout(PdfPCell cell, Rectangle position, PdfContentByte[] canvases)
					{
						try
						{
							PdfContentByte canvas = canvases[PdfPTable.BACKGROUNDCANVAS];
							canvas.saveState();

							if (stopper.isConCard())
							{
								soloTitolariPremiaySestoA4.scaleToFit(Utilities.millimetersToPoints(30f),
										Utilities.millimetersToPoints(30f));
								soloTitolariPremiaySestoA4.setAbsolutePosition(
										position.getLeft(),
										position.getBottom() + Utilities.millimetersToPoints(17f));
								canvas.addImage(soloTitolariPremiaySestoA4);
							}

							Rectangle rect = new Rectangle(position.getLeft(), position.getBottom() + Utilities.millimetersToPoints(17f),
									position.getRight(), position.getTop());
							rect.setBackgroundColor(new BaseColor(255, 255, 255, 16));
							canvas.rectangle(rect);

							canvas.stroke();
							canvas.restoreState();
						}

						catch (Exception e)
						{
							e.printStackTrace();
						}
					}

				});

				stopperTable.addCell(westCell);

				// EAST

				stopperTable.addCell(makeEastCell(stopper, style));

				// SOUTH

				southCell = new PdfPCell();
				southCell.setBorder(PdfPCell.NO_BORDER);
				southCell.setColspan(2);
				if (stopper.getPezziMax() == null || stopper.getPezziMax() <= 0)
					southCell.setPaddingTop(Utilities.millimetersToPoints(4));

				extrainfosParagraph = new Paragraph();
				extrainfosParagraph.setAlignment(Element.ALIGN_RIGHT);
				extrainfosParagraph.setLeading(0, 1f);
				extrainfosParagraph.setIndentationRight(Utilities.millimetersToPoints(8f));

				if (stopper.getPeriodo() != null)
				{
					String descrizionePeriodo = makePeriodoOffertaDescription(stopper.getPeriodo(),
							stopper.isConCard());
					descrizionePeriodo = descrizionePeriodo.substring(0, 1).toUpperCase()
							+ descrizionePeriodo.substring(1);
					dateRangeChunk = new Chunk(descrizionePeriodo, FontFactory.getFont(heavyFontStyle, 10));
					extrainfosParagraph.add(dateRangeChunk);
				}

				if (stopper.isConCard())
				{
					Chunk fidCardChunk = new Chunk("SOLO CON CARTA PREMIATY",
							FontFactory.getFont(boldFontStyle, 10)).setCharacterSpacing(-0.6f);
					extrainfosParagraph.add(new Chunk("  "));
					extrainfosParagraph.add(fidCardChunk);
				}

				if (stopper.getPezziMax() != null && stopper.getPezziMax() > 0)
				{
					extrainfosParagraph.add(new Chunk("\nMASSIMO ", FontFactory.getFont(heavyFontStyle, 11))
							.setCharacterSpacing(-0.6f));
					extrainfosParagraph.add(
							new Chunk(stopper.getPezziMax().toString(), FontFactory.getFont(boldFontStyle, 12)));
					extrainfosParagraph
							.add(new Chunk(" PEZZ" + (stopper.getPezziMax()>1? "I":"O")  + 
									" PER CLIENTE", FontFactory.getFont(heavyFontStyle, 11))
									.setCharacterSpacing(-0.6f));
				}

				southCell.addElement(extrainfosParagraph);

				stopperTable.addCell(southCell);
			break;
			case CARTA_SPECIALE_OFFERTA_GRANDE:

				decorator = makeCellDecorator(style);

				northCell = new PdfPCell();
				northCell.setFixedHeight(Utilities.millimetersToPoints(65));
				northCell.setBorder(PdfPCell.NO_BORDER);
				northCell.setColspan(2);

				stopperTable.addCell(northCell);

				// WEST

				westCell = new PdfPCell();
				westCell.setBorder(PdfPCell.NO_BORDER);
				westCell.setFixedHeight(Utilities.millimetersToPoints(123));
				westCell.setPaddingRight(Utilities.millimetersToPoints(8));

				descriptionParagraph = makeDescrizioneParagraph(stopper, FontFactory.getFont(boldFontStyle, 38),
						FontFactory.getFont(normalFontStyle, 38));
				descriptionParagraph.setAlignment(Element.ALIGN_RIGHT);
				descriptionParagraph.setLeading(0, 1.2f);
				westCell.addElement(descriptionParagraph);

				quantitaParagraph = makeQuantitaParagraph(stopper, 38, 28);
				westCell.addElement(quantitaParagraph);

				westCell.addElement(makeCodiceParagraph(stopper, 38, 16, 
						stopper.getRigheDescrizione().size()+(quantitaParagraph==null?0:1)));

				stopperTable.addCell(westCell);

				// EAST

				stopperTable.addCell(makeEastCell(stopper, style));

				// SOUTH

				southCell = new PdfPCell();
				southCell.setBorder(PdfPCell.NO_BORDER);
				southCell.setColspan(2);
				if (stopper.getPezziMax() == null || stopper.getPezziMax() <= 0)
					southCell.setPaddingTop(Utilities.millimetersToPoints(4));

				extrainfosParagraph = new Paragraph();
				extrainfosParagraph.setAlignment(Element.ALIGN_RIGHT);
				extrainfosParagraph.setLeading(0, 1f);
				extrainfosParagraph.setIndentationRight(Utilities.millimetersToPoints(8f));

				if (stopper.getPeriodo() != null)
				{
					String descrizionePeriodo = makePeriodoOffertaDescription(stopper.getPeriodo(),
							stopper.isConCard());
					descrizionePeriodo = descrizionePeriodo.substring(0, 1).toUpperCase()
							+ descrizionePeriodo.substring(1);
					dateRangeChunk = new Chunk(descrizionePeriodo, FontFactory.getFont(heavyFontStyle, 20));
					extrainfosParagraph.add(dateRangeChunk);
				}

				if (stopper.isConCard())
				{
					Chunk fidCardChunk = new Chunk("SOLO CON CARTA PREMIATY",
							FontFactory.getFont(boldFontStyle, 20)).setCharacterSpacing(-0.6f);
					extrainfosParagraph.add(new Chunk("  "));
					extrainfosParagraph.add(fidCardChunk);
				}

				if (stopper.getPezziMax() != null && stopper.getPezziMax() > 0)
				{
					extrainfosParagraph.add(new Chunk("\nMASSIMO ", FontFactory.getFont(heavyFontStyle, 20))
							.setCharacterSpacing(-0.6f));
					extrainfosParagraph.add(
							new Chunk(stopper.getPezziMax().toString(), FontFactory.getFont(boldFontStyle, 25)));
					extrainfosParagraph
							.add(new Chunk(" PEZZ" + (stopper.getPezziMax()>1? "I":"O")  + 
									" PER CLIENTE", FontFactory.getFont(heavyFontStyle, 20))
									.setCharacterSpacing(-0.6f));
				}

				southCell.addElement(extrainfosParagraph);

				stopperTable.addCell(southCell);
			break;

			default:
			break;
		}

		stopperCell.setCellEvent(decorator);

		stopperCell.addElement(stopperTable);

		return stopperCell;

	}

	private Paragraph makeCodiceParagraph(S stopper, int emptyLineFontSize, int codiceFontSize, int linesBefore)
	{
		Paragraph paragraph = null;
		if (stopper.getCodiceInterno()!=null && linesBefore<5)
		{
			paragraph = new Paragraph();
			//paragraph.setSpacingBefore(40);
			paragraph.setAlignment(Element.ALIGN_RIGHT);
			paragraph.setLeading(0, 1.2f);
			int emptyLines = 4 - linesBefore;
			for (int i = 0; i < emptyLines; i++)
			{
				paragraph.add(new Chunk(" \n", FontFactory.getFont(normalFontStyle, emptyLineFontSize)));
			}

				paragraph.add(new Chunk("cod. " + stopper.getCodiceInterno(),
						FontFactory.getFont(normalFontStyle, codiceFontSize)).setLineHeight(emptyLineFontSize/3*4));
		}
		return paragraph;
	}

	protected abstract PdfCellDecorator makeCellDecorator(PdfStopperStyle style) throws UnsupportedStyleException;

	protected abstract PdfPCell makeEastCell(S stopper, PdfStopperStyle style) throws UnsupportedStyleException;

	protected Paragraph makeDescrizioneParagraph(S stopper, Font uCaseFont, Font lCaseFont)
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
	
	protected Paragraph makeQuantitaParagraph(S stopper, int lineFontSize, int qtaFontSize)
	{
		Paragraph paragraph = null;
		if (stopper.getQuantita().size() != 0 && stopper.getQuantita().get(0).getValore() != null
				&& (stopper.getRigheDescrizione().size() < 5
						//|| (stopper.getCodiceInterno() == null && stopper.getRigheDescrizione().size() == 4)
						))
		{
			paragraph = new Paragraph();
			paragraph.setAlignment(Element.ALIGN_RIGHT);
			paragraph.setLeading(0, 1.2f);
			paragraph.add(new Chunk(" ", FontFactory.getFont(normalFontStyle, lineFontSize)));
			paragraph.add(
					makeQuantitaPhrase(stopper.getQuantita(), FontFactory.getFont(normalFontStyle, qtaFontSize)));
		}
		return paragraph;
	}

	protected Phrase makePrezzoScontatoPhrase(S stopper, Font iPartFont, Font fPartFont)
	{
		return makePricePhrase(stopper.getPrezzoPromo().getValue(),
				FontFactory.getFont(heavyFontStyle, (int)fPartFont.getSize()),
				iPartFont, fPartFont);
	}

	protected String makePeriodoOffertaDescription(PeriodoOfferta periodo, boolean compatto)
	{

		String descPeriodo;

		if (compatto)
		{
			descPeriodo = "Dal " + DateUtils.italianDateFormatNoYear.format(periodo.getInizio()) + " al "
					+ DateUtils.italianDateFormatNoYear.format(periodo.getFine());
		} else
		{
			Calendar inizio = GregorianCalendar.getInstance();
			inizio.setTime(periodo.getInizio());

			Calendar fine = GregorianCalendar.getInstance();
			fine.setTime(periodo.getFine());

			descPeriodo = "Dal" + (vuoleDoppia(inizio.get(Calendar.DAY_OF_MONTH)) ? "l\'" : " ")
					+ inizio.get(Calendar.DAY_OF_MONTH) + " ";

			if (inizio.get(Calendar.MONTH) != fine.get(Calendar.MONTH))
				descPeriodo += new DateFormatSymbols().getMonths()[inizio.get(Calendar.MONTH)] + " ";

			if (inizio.get(Calendar.YEAR) != fine.get(Calendar.YEAR))
				descPeriodo += inizio.get(Calendar.YEAR) + " ";

			descPeriodo += "al" + (vuoleDoppia(fine.get(Calendar.DAY_OF_MONTH)) ? "l\'" : " ")
					+ fine.get(Calendar.DAY_OF_MONTH) + " "
					+ new DateFormatSymbols().getMonths()[fine.get(Calendar.MONTH)] + " " + fine.get(Calendar.YEAR);
		}
		return descPeriodo;
	}

}
