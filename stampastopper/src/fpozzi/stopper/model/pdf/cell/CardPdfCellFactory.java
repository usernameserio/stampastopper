package fpozzi.stopper.model.pdf.cell;

import java.io.IOException;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Utilities;
import com.itextpdf.text.pdf.BarcodeEAN;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import fpozzi.stopper.model.Card;
import fpozzi.stopper.model.pdf.PdfStopperStyle;

public class CardPdfCellFactory extends PdfCellFactory<Card>
{

	public static final CardPdfCellFactory instance;

	static
	{
		instance = new CardPdfCellFactory(new PdfStopperStyle[] { PdfStopperStyle.CARD_COLORI });
	}
	
	private CardPdfCellFactory(PdfStopperStyle[] supportedStyles)
	{
		super(supportedStyles);
	}

	@Override
	public PdfPCell makePDF(Card card, PdfStopperStyle style, PdfContentByte pdfContentByte)
	{

		PdfCellDecorator outerCellDecorator = new PdfCellDecorator(true);
		outerCellDecorator.setBorderGreyIntensity(0xdd);
		outerCellDecorator.setDashArray(new float[]{5,10});
		outerCellDecorator.setDashPhase(5);
		
		PdfPCell stopperCell = new PdfPCell();
		stopperCell.setPadding(0);
		stopperCell.setCellEvent(outerCellDecorator);
		stopperCell.setFixedHeight(style.getFormat().getHeight());
		stopperCell.setBorder(PdfPCell.NO_BORDER);
		stopperCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		stopperCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		
		PdfPTable cardTable = new PdfPTable(1);

		cardTable.setTotalWidth(Utilities.millimetersToPoints(80));
		cardTable.setLockedWidth(true);
		
		PdfPCell cardCell = new PdfPCell();
		cardCell.setBorder(PdfPCell.NO_BORDER);
		cardCell.setCellEvent(new PdfCellDecorator(true));
		cardCell.setFixedHeight(style.getFormat().getHeight()/2f);
		cardCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cardCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
	
		Image logoImage = null;
		
		try
		{
			logoImage = Image.getInstance(CardPdfCellFactory.class.getResource("/img/pdf/logo_full.png"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		logoImage.setWidthPercentage(75);
		logoImage.setAlignment(Element.ALIGN_CENTER);
		
		cardCell.addElement(logoImage);
		
		Paragraph p2 = new Paragraph();
		p2.setAlignment(Element.ALIGN_CENTER);
		p2.add(new Chunk(card.getTag() + "\n"));
		cardCell.addElement(p2);

		BarcodeEAN codeEAN = new BarcodeEAN();
		codeEAN.setCode(card.getCodiceEan().valore);
		codeEAN.setGuardBars(false);
		codeEAN.setSize(Utilities.millimetersToPoints(2));
		codeEAN.setBarHeight(Utilities.millimetersToPoints(6));
		//codeEAN.setFont(FontStyle.BOOK.getBaseFont());
		Image barcodeImage = codeEAN.createImageWithBarcode(pdfContentByte, null, null);
		barcodeImage.setSpacingBefore(Utilities.millimetersToPoints(2));
		barcodeImage.setWidthPercentage(80);
		barcodeImage.setAlignment(Element.ALIGN_MIDDLE);
		cardCell.addElement(barcodeImage);

		//cardCell.setRotation(90);

		cardTable.addCell(cardCell);
		cardTable.addCell(cardCell);
		
		stopperCell.addElement(cardTable);
		
		return stopperCell;
	}
	
}