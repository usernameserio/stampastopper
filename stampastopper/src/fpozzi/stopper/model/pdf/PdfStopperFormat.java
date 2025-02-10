package fpozzi.stopper.model.pdf;

import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Utilities;

public enum PdfStopperFormat {

	A4("A4", PageSize.A4.rotate(), 1, 1), 
	A7("A7", PageSize.A4, 2, 4), 
	SESTO_A4("sesto di A4 orizzontanle", PageSize.A4.rotate(), 2, 3),
	CARD("sesto di A4 verticale", PageSize.A4, 2, 3),
	A6("A6", PageSize.A4.rotate(), 2, 2),
	A5("A5", PageSize.A4, 1, 2),
	TERZO_A4("terzo di A4", PageSize.A4, 1, 3),
	STRISCIA_CORTA("striscia corta", PageSize.A4, 1, 8);	
	
	
	private final String description;
	private final float width, height;
	private final float widthInMM, heightInMM;
	private final int cols, rows;
	private final Rectangle stopperRectangle, pageRectangle;

	private PdfStopperFormat(String description, Rectangle page, int cols, int rows)
	{
		this(description, page, cols, rows, page.getWidth()/cols, page.getHeight()/rows);
	}
	
	private PdfStopperFormat(String description, Rectangle page, int cols, int rows, float width, float height)
	{
		this.description = description;
		this.pageRectangle = page;
		this.width = width;
		this.widthInMM  = Utilities.pointsToMillimeters(width);
		this.height = height;
		this.heightInMM  = Utilities.pointsToMillimeters(height);
		this.cols = cols;
		this.rows = rows;
		this.stopperRectangle = new Rectangle(0, 0, width, height);
	}

	public String getDescription()
	{
		return description;
	}

	public float getWidth()
	{
		return width;
	}
	
	public float getWidthInMM()
	{
		return widthInMM;
	}

	public float getHeight()
	{
		return height;
	}
	
	public float getHeightInMM()
	{
		return heightInMM;
	}

	public Rectangle getStopperRectangle()
	{
		return stopperRectangle;
	}

	public Rectangle getPageRectangle()
	{
		return pageRectangle;
	}
	
	public int getRows()
	{
		return rows;
	}
	
	public int getCols()
	{
		return cols;
	}
	
	public int getStoppersPerPage()
	{
		return rows * cols;
	}

}
