package fpozzi.stopper.model.pdf.cell;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;

public class PdfCellDecorator implements PdfPCellEvent
{

	private final Image backgroundImage;
	private final float spacing;
	private final boolean ritagliabile;
	
	private float borderThickness = 0.4f;
	private int borderGreyIntensity = 0xaa;
	private float[] dashArray = new float[] { 3.0f, 3.0f };
	private float dashPhase = 3;

	public PdfCellDecorator(boolean ritagliabile, float spacing, Image backgroundImage)
	{
		this.backgroundImage = backgroundImage;
		this.ritagliabile = ritagliabile;
		this.spacing = spacing;
	}

	public PdfCellDecorator(boolean ritagliabile, float spacing)
	{
		this(ritagliabile, spacing, null);
	}
	
	public PdfCellDecorator(boolean ritagliabile)
	{
		this(ritagliabile, 0, null);
	}

	public float getBorderThickness()
	{
		return borderThickness;
	}

	public void setBorderThickness(float borderThickness)
	{
		this.borderThickness = borderThickness;
	}

	public int getBorderGreyIntensity()
	{
		return borderGreyIntensity;
	}

	public void setBorderGreyIntensity(int borderGreyIntensity)
	{
		this.borderGreyIntensity = borderGreyIntensity;
	}

	public float[] getDashArray()
	{
		return dashArray;
	}

	public void setDashArray(float[] dashArray)
	{
		this.dashArray = dashArray;
	}

	public float getDashPhase()
	{
		return dashPhase;
	}

	public void setDashPhase(float dashPhase)
	{
		this.dashPhase = dashPhase;
	}

	public Image getBackgroundImage()
	{
		return backgroundImage;
	}

	public float getSpacing()
	{
		return spacing;
	}

	public boolean isRitagliabile()
	{
		return ritagliabile;
	}

	public void cellLayout(PdfPCell cell, Rectangle rectangle, PdfContentByte[] canvases)
	{

		PdfContentByte canvas;
		if (backgroundImage != null)
		{
			Image guida;
			canvas = canvases[PdfPTable.BASECANVAS];
			try
			{
				guida = Image.getInstance(backgroundImage);
				guida.setAbsolutePosition(rectangle.getLeft(), rectangle.getBottom());
				guida.scaleAbsolute(rectangle.getWidth(), rectangle.getHeight());
				canvas.addImage(guida);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		if (ritagliabile)
		{
			canvas = canvases[PdfPTable.LINECANVAS];
			canvas.saveState();
			canvas.setLineWidth(borderThickness);
			canvas.setColorStroke(new BaseColor(borderGreyIntensity, borderGreyIntensity, borderGreyIntensity));
			canvas.setLineDash(dashArray, dashPhase);
			canvas.rectangle(rectangle.getLeft()+spacing, rectangle.getBottom()-spacing, rectangle.getWidth()-spacing*2, rectangle.getHeight()-spacing*2);
			canvas.stroke();
			canvas.restoreState();
		}
	}

}
