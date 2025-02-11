package fpozzi.stopper.model.pdf;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.w3c.dom.Element;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import fpozzi.gdoshop.model.articolo.Codice;
import fpozzi.gdoshop.model.articolo.Quantita;
import fpozzi.stopper.model.Prezzo;
import fpozzi.stopper.model.Stopper;
import fpozzi.stopper.model.pdf.cell.PdfCellFactory;

public abstract class PdfStopper<S extends Stopper> implements Cloneable
{

	private final S stopper;
	private final PdfCellFactory<S> cellFactory;
	private final Map<PdfStopperStyle, StopperPreview> cachedPreviews;

	public PdfStopper(S stopper, PdfCellFactory<S> factory)
	{
		super();
		this.stopper = stopper;
		this.cellFactory = factory;
		cachedPreviews = new HashMap<PdfStopperStyle, StopperPreview>();
	}

	public S getStopper()
	{
		return stopper;
	}

	public PdfCellFactory<S> getCellFactory()
	{
		return cellFactory;
	}

	public PdfStopperStyle convertStyle(PdfStopperStyle style) throws UnsupportedStyleException
	{
		if (!getCellFactory().getSupportedStyles().contains(style))
			throw new UnsupportedStyleException();

		return style;
	}

	public abstract String getAbbr();

	public abstract Codice getCodice();

	public abstract String getDescrizione();

	public abstract Prezzo getPrezzo();

	public abstract List<Quantita> getQuantita();

	public boolean canBeMergedInto(PdfStopper<?> otherStopper)
	{
		return false;
	}

	public void mergeInto(PdfStopper<?> otherStopper) throws MergeException
	{
		throw new MergeException("Operazione non supportata per questo tipo di stopper");
	}

	public PdfPCell makePdfCell(PdfStopperStyle style, PdfContentByte pdfContentByte) throws UnsupportedStyleException, DocumentException
	{

		if (!cellFactory.getSupportedStyles().contains(style))
			throw new UnsupportedStyleException();

		return cellFactory.makePDF(stopper, style, pdfContentByte);
	}

	public Element makeXMLElement(org.w3c.dom.Document doc)
	{
		return stopper.makeXMLElement(doc);
	}

	public Image getPreview(PdfStopperStyle style, int dpi) throws UnsupportedStyleException, DocumentException, IOException
	{
		StopperPreview preview = cachedPreviews.get(style);
		if (preview == null || preview.dpi != dpi)
			synchronized (this)
			{
				preview = makePreview(convertStyle(style), dpi);
				cachedPreviews.put(style, preview);
			}

		return preview.image;
	}

	public class StopperPreview
	{
		PdfStopperStyle style;
		int dpi;
		BufferedImage image;

		public StopperPreview(PdfStopperStyle style, int dpi, BufferedImage image)
		{
			super();
			this.style = style;
			this.dpi = dpi;
			this.image = image;
		}

	}

	public Image getPreview(PdfStopperStyle style, int maxWidth, int maxHeight) throws UnsupportedStyleException, DocumentException, IOException
	{
		return getPreview(style, calculateOptimalDPI(style.getFormat(), maxWidth, maxHeight));
	}

	static private final int idealDPI = 96;

	public int calculateOptimalDPI(PdfStopperFormat format, int maxWidth, int maxHeight)
	{
		int dpi = idealDPI;
		int actualWidth = (int) (format.getStopperRectangle().getWidth() * idealDPI / 72f);
		int actualHeight = (int) (format.getStopperRectangle().getHeight() * idealDPI / 72f);

		if (actualWidth > maxWidth)
			dpi = (int) Math.min(idealDPI, idealDPI * maxWidth / actualWidth);

		if (actualHeight > maxHeight)
			dpi = (int) Math.min(dpi, idealDPI * maxHeight / actualHeight);

		return dpi;
	}

	private StopperPreview makePreview(PdfStopperStyle style, int dpi) throws DocumentException, UnsupportedStyleException, IOException
	{
		Document document = new Document(style.getFormat().getStopperRectangle(), 0, 0, 0, 0);

		ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();

		PdfWriter writer = PdfWriter.getInstance(document, baosPDF);

		document.open();

		PdfPTable table = new PdfPTable(1);
		table.setTotalWidth(style.getFormat().getWidth());
		table.setLockedWidth(true);
		table.addCell(makePdfCell(style, writer.getDirectContent()));

		document.add(table);

		document.close();

		writer.close();

		PDDocument pdDoc = null;
		try
		{
			pdDoc = PDDocument.load(baosPDF.toByteArray());
			PDFRenderer pdfRenderer = new PDFRenderer(pdDoc);

			BufferedImage image = pdfRenderer.renderImageWithDPI(0, dpi);

			return new StopperPreview(style, dpi, image);
		}
		finally
		{
			pdDoc.close();
		}
	}

	public abstract void acceptEditor(PdfStopperEditor editor);

	public abstract Object clone();

}
