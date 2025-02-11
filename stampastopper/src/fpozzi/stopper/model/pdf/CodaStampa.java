package fpozzi.stopper.model.pdf;

import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.print.PrintService;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfBoolean;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import fpozzi.stopper.serialization.CodaStampaXMLSerializer;
import fpozzi.utils.exception.FileInUseException;

public class CodaStampa implements PdfStopperRequestObserver
{

	static final public DateFormat pdfTimestamp = new SimpleDateFormat("yyMMddHHmmss");

	private final List<CodaStampaObserver> observers;

	private final Map<PdfStopperStyle, List<PdfStopperRequest>> requests;

	private final Map<PdfStopperStyle, Integer> stoppersCount;

	public CodaStampa()
	{
		super();
		observers = new LinkedList<CodaStampaObserver>();
		requests = new LinkedHashMap<PdfStopperStyle, List<PdfStopperRequest>>();
		stoppersCount = new HashMap<PdfStopperStyle, Integer>();
		for (PdfStopperStyle style : PdfStopperStyle.values())
		{
			requests.put(style, new LinkedList<PdfStopperRequest>());
			stoppersCount.put(style, 0);
		}
	}

	public void attachObserver(CodaStampaObserver observer)
	{
		observers.add(observer);
	}

	public void detachObserver(CodaStampaObserver observer)
	{
		observers.remove(observer);
	}

	public List<PdfStopperRequest> getRequests(PdfStopperStyle style)
	{
		return Collections.unmodifiableList(requests.get(style));
	}

	public Map<PdfStopperStyle, List<PdfStopperRequest>> getRequests()
	{
		return Collections.unmodifiableMap(requests);
	}

	public void addRequest(final PdfStopperRequest request)
	{
		final PdfStopperStyle style = request.getStyle();
		requests.get(style).add(request);
		changeStoppersCount(style, request.getCopies());
		request.attachObserver(this);
		for (CodaStampaObserver observer : observers)
			observer.requestAdded(this, request);
	}

	public void removeRequest(final PdfStopperRequest request)
	{
		final PdfStopperStyle style = request.getStyle();
		requests.get(style).remove(request);
		changeStoppersCount(style, -request.getCopies());
		request.detachObserver(this);
		for (CodaStampaObserver observer : observers)
			observer.requestRemoved(this, request);
	}

	public boolean contains(PdfStopperRequest request)
	{
		return requests.get(request.getStyle()).contains(request);
	}

	public int getStoppersCount(PdfStopperStyle style)
	{
		return stoppersCount.get(style);
	}

	private void changeStoppersCount(final PdfStopperStyle style, final int delta)
	{
		if (delta != 0)
		{
			final int oldCount = stoppersCount.get(style);
			stoppersCount.put(style, oldCount + delta);
			for (CodaStampaObserver observer : observers)
				observer.stopperCountChanged(this, style, oldCount, oldCount + delta);
		}
	}

	/*
	 * public void replaceRequest(ObservableObservablePdfStopperRequest oldRequest,
	 * ObservableObservablePdfStopperRequest newRequest) {
	 * List<ObservableObservablePdfStopperRequest> targetRequests =
	 * requests.get(newRequest.getStyle()); if
	 * (oldRequest.getStyle().equals(newRequest.getStyle())) {
	 * targetRequests.set(targetRequests.indexOf(oldRequest), newRequest); } else {
	 * removeRequest(oldRequest); addRequest(newRequest); }
	 * 
	 * }
	 */

	public void stopperChanged(PdfStopperRequest request, PdfStopper<?> oldStopper, PdfStopper<?> newStopper)
	{
		for (CodaStampaObserver observer : observers)
			observer.requestChanged(this, request);
	}

	public void styleChanged(PdfStopperRequest request, PdfStopperStyle oldStyle, PdfStopperStyle newStyle)
	{
		requests.get(oldStyle).remove(request);
		changeStoppersCount(oldStyle, -request.getCopies());
		requests.get(newStyle).add(request);
		changeStoppersCount(newStyle, +request.getCopies());
		for (CodaStampaObserver observer : observers)
		{
			observer.requestMoved(this, request, oldStyle, newStyle);
			observer.requestChanged(this, request);
		}
	}

	public void copiesChanged(final PdfStopperRequest request, int oldCopies, int newCopies)
	{
		final PdfStopperStyle style = request.getStyle();
		changeStoppersCount(style, newCopies - oldCopies);
		for (CodaStampaObserver observer : observers)
			observer.requestChanged(this, request);
	}

	private PdfPTable makeTable(PdfStopperStyle style, PdfContentByte pdfContentByte)
			throws UnsupportedStyleException, DocumentException
	{
		PdfPTable table = new PdfPTable(style.getFormat().getCols());
		table.setTotalWidth(style.getFormat().getPageRectangle().getWidth());
		table.setLockedWidth(true);

		PdfPCell stopperCell;

		boolean leftToRight = style.getFormat().getPageRectangle().getWidth() <= style.getFormat().getPageRectangle()
				.getHeight();

		int rows = style.getFormat().getRows(), cols = style.getFormat().getCols();
		int initialCol = leftToRight ? 0 : cols - 1;

		List<PdfPCell[][]> cells = new LinkedList<PdfPCell[][]>();
		PdfPCell[][] cellsOnPage = null;

		// int cellCounter = 0;
		int c = initialCol, r = 0;

		PdfPCell emptyStopperCell = new PdfStopperRequest(EmptyPdfStopper.instance, style, 1).makePDF(pdfContentByte);

		for (PdfStopperRequest request : requests.get(style))
		{
			stopperCell = request.makePDF(pdfContentByte);
			for (int k = 0; k < request.getCopies(); k++)
			{
				if (c == initialCol && r == 0)
				{
					cellsOnPage = new PdfPCell[cols][];
					for (int col = 0; col < cols; col++)
					{
						cellsOnPage[col] = new PdfPCell[rows];
						for (int row = 0; row < rows; row++)
							cellsOnPage[col][row] = emptyStopperCell;
					}
					cells.add(cellsOnPage);
				}
				if (!leftToRight)
				{
					cellsOnPage[c][r++] = stopperCell;
					if (r == rows)
					{
						r = 0;
						c--;
					}
					if (c == -1)
					{
						c = initialCol;
					}
				} else
				{
					cellsOnPage[c++][r] = stopperCell;
					if (c == cols)
					{
						c = 0;
						r++;
					}
					if (r == rows)
					{
						r = 0;
					}
				}
			}
		}

		for (PdfPCell[][] cellsOnPageLoop : cells)
		{
			for (int i = 0; i < rows; i++)

				for (int j = 0; j < cols; j++)
				{
					table.addCell(cellsOnPageLoop[j][i]);
				}
		}

		table.completeRow();
		return table;
	}

	public File makePdfFile(PdfStopperStyle style, String stamp)
			throws DocumentException, UnsupportedStyleException, FileInUseException, IOException
	{
		File outputFile = new File(stamp + "_" + style.toString().toLowerCase() + ".pdf");

		FileOutputStream os;
		try
		{
			os = new FileOutputStream(outputFile);
		} catch (IOException e)
		{
			throw new FileInUseException(outputFile);
		}

		try
		{
			os.write(makePDF(style).toByteArray());
		} finally
		{
			os.close();
		}

		return outputFile;
	}

	public void printPdfFile(PdfStopperStyle style, PrintService printService)
			throws DocumentException, UnsupportedStyleException, IOException, PrinterException
	{
		PDDocument pdDoc = null;
		PrinterJob job = PrinterJob.getPrinterJob();

		try
		{

			job.setPrintService(printService);
			job.setJobName(style.toString().toLowerCase());
			pdDoc = PDDocument.load(makePDF(style).toByteArray());

			job.setPageable(new PDFPageable(pdDoc));
			job.print();

		} catch (PrinterAbortException e)
		{
			// avoid abort exception
		} catch (PrinterException e)
		{
			throw new PrinterException("Error while printing");
		} finally
		{
			pdDoc.close();
		}
	}

	private String makeTimestamp()
	{
		return pdfTimestamp.format(GregorianCalendar.getInstance().getTime());
	}

	public File makeFile(PdfStopperStyle style) throws Exception
	{
		return makePdfFile(style, makeTimestamp());
	}

	public List<File> makeFiles(String stamp) throws Exception
	{
		List<File> files = new LinkedList<File>();

		for (int i = 0; i < PdfStopperStyle.values().length; i++)
		{
			PdfStopperStyle style = PdfStopperStyle.values()[i];
			if (!requests.get(style).isEmpty())
				files.add(makePdfFile(style, stamp));
		}

		return files;
	}

	public List<File> makeFiles() throws Exception
	{
		return makeFiles(makeTimestamp());
	}

	public ByteArrayOutputStream makePDF(PdfStopperStyle style) throws DocumentException, UnsupportedStyleException
	{
		Document document = new Document(style.getFormat().getPageRectangle(), 0, 0, 0, 0);

		ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();

		PdfWriter writer = PdfWriter.getInstance(document, baosPDF);
		writer.setViewerPreferences(PdfWriter.PageLayoutSinglePage);
		writer.addViewerPreference(PdfName.PRINTSCALING, PdfName.NONE);
		writer.addViewerPreference(PdfName.CENTERWINDOW, PdfBoolean.PDFTRUE);
		document.open();

		document.add(makeTable(style, writer.getDirectContent()));

		document.close();

		writer.close();

		return baosPDF;
	}

	public static CodaStampa fromFile(File file) throws Exception
	{
		return CodaStampaXMLSerializer.deserialize(CodaStampaXMLSerializer.read(new FileInputStream(file)));
	}

	public void toFile(File file) throws Exception
	{
		CodaStampaXMLSerializer.store(CodaStampaXMLSerializer.serialize(this), new FileOutputStream(file));
	}

}
