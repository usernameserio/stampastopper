package fpozzi.stopper.model.pdf.cell;

import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;

import fpozzi.stopper.model.Stopper;
import fpozzi.stopper.model.pdf.PdfStopperStyle;

public class EmptyPdfCellFactory extends PdfCellFactory<Stopper>
{

	public static final EmptyPdfCellFactory instance;
	
	static 
	{
		instance = new EmptyPdfCellFactory(PdfStopperStyle.values());
	}
	
	private EmptyPdfCellFactory(PdfStopperStyle[] supportedStyles)
	{
		super(supportedStyles);
	}

	@Override
	public PdfPCell makePDF(Stopper stopper, PdfStopperStyle style, PdfContentByte pdfContentByte)
	{
		PdfPCell stopperCell = new PdfPCell();

		stopperCell.setFixedHeight(style.getFormat().getHeight());
		stopperCell.setBorder(PdfPCell.NO_BORDER);
		stopperCell.setPadding(0);

		return stopperCell;
	}

}
