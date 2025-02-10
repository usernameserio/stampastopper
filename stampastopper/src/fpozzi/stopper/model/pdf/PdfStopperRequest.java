package fpozzi.stopper.model.pdf;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;

public class PdfStopperRequest implements Cloneable
{

	private final List<PdfStopperRequestObserver> observers;

	private PdfStopper<?> pdfStopper;
	private PdfStopperStyle style;
	private int copies;

	public PdfStopperRequest(PdfStopper<?> pdfStopper, PdfStopperStyle style, int copies) throws UnsupportedStyleException
	{
		observers = new CopyOnWriteArrayList<PdfStopperRequestObserver>();

		this.pdfStopper = pdfStopper;
		this.setStyle(style);
		this.setCopies(copies);
	}

	public void attachObserver(PdfStopperRequestObserver observer)
	{
		observers.add(observer);
	}

	public void detachObserver(PdfStopperRequestObserver observer)
	{
		observers.remove(observer);
	}

	public PdfStopper<?> getPdfStopper()
	{
		return pdfStopper;
	}

	public void setPdfStopper(PdfStopper<?> pdfStopper) throws UnsupportedStyleException
	{
		PdfStopper<?> oldPdfStopper = this.pdfStopper;
		this.pdfStopper = pdfStopper;

		PdfStopperStyle newStyle;

		try
		{
			newStyle = pdfStopper.convertStyle(style);

			if (newStyle != style)
				setStyle(newStyle);
		}
		catch (UnsupportedStyleException e)
		{
			this.pdfStopper = oldPdfStopper;
			throw e;
		}

		for (PdfStopperRequestObserver observer : observers)
			observer.stopperChanged(this, oldPdfStopper, pdfStopper);
	}

	public PdfStopperStyle getStyle()
	{
		return style;
	}

	public void setStyle(PdfStopperStyle style) throws UnsupportedStyleException
	{
		if (style != this.style)
		{
			if (!pdfStopper.getCellFactory().supportsStyle(style))
				throw new UnsupportedStyleException();
			PdfStopperStyle oldStyle = this.style;
			this.style = style;
			for (PdfStopperRequestObserver observer : observers)
				observer.styleChanged(this, oldStyle, style);
		}
	}

	public int getCopies()
	{
		return copies;
	}

	public void setCopies(int copies)
	{
		int oldCopies = this.copies;
		this.copies = copies;
		for (PdfStopperRequestObserver observer : observers)
			observer.copiesChanged(this, oldCopies, copies);
	}

	public PdfPCell makePDF(PdfContentByte pdfContentByte) throws UnsupportedStyleException, DocumentException
	{
		return pdfStopper.makePdfCell(style, pdfContentByte);
	}

	public Element makeXMLElement(Document doc)
	{
		Element stopperElement = pdfStopper.makeXMLElement(doc);

		if (copies >= 0)
			stopperElement.setAttribute("copie", copies + "");

		return stopperElement;
	}

	/*
	 * @Override public boolean equals(Object otherObject) { if (!(otherObject
	 * instanceof PdfStopperRequest)) return false;
	 * 
	 * PdfStopperRequest otherRequest = (PdfStopperRequest) otherObject;
	 * 
	 * if (copies != otherRequest.copies) return false;
	 * 
	 * if (!style.equals(otherRequest.style)) return false;
	 * 
	 * if (!pdfStopper.equals(otherRequest.pdfStopper)) return false;
	 * 
	 * return true; }
	 */

	public Object clone()
	{
		try
		{
			return new PdfStopperRequest((PdfStopper<?>) pdfStopper.clone(), style, copies);
		}
		catch (UnsupportedStyleException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
