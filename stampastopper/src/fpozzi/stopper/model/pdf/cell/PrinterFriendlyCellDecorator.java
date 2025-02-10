package fpozzi.stopper.model.pdf.cell;

import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Utilities;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class PrinterFriendlyCellDecorator extends PdfCellDecorator
{

	public PrinterFriendlyCellDecorator(boolean ritagliabile)
	{
		super(ritagliabile);
	}
	
	public PrinterFriendlyCellDecorator()
	{
		super(true);
	}
	
	@Override
	public void cellLayout(PdfPCell cell, Rectangle rectangle, PdfContentByte[] canvases)
	{
		super.cellLayout(cell, rectangle, canvases);

		PdfContentByte canvas = canvases[PdfPTable.BASECANVAS];
		try
		{
			canvas.saveState();
            PdfGState gs1 = new PdfGState();
            gs1.setFillOpacity(0.3f);
            canvas.setGState(gs1);
			Image logo_omino = Image.getInstance("res/logo_omino.png");
			logo_omino.setAbsolutePosition(rectangle.getLeft()+Utilities.millimetersToPoints(5), 
					rectangle.getBottom()+Utilities.millimetersToPoints(5));
			logo_omino.scaleAbsolute(Utilities.millimetersToPoints(25), Utilities.millimetersToPoints(25));
			canvas.addImage(logo_omino);
			canvas.restoreState();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

}
