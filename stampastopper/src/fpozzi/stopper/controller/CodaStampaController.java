package fpozzi.stopper.controller;

import java.awt.Desktop;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import javax.print.PrintService;

import com.itextpdf.text.DocumentException;

import fpozzi.gdoshop.model.offerta.PeriodoOfferta;
import fpozzi.stopper.StampaStopperLogger;
import fpozzi.stopper.StampaStopperProperties;
import fpozzi.stopper.StampaStopperProperties.StampaStopperProperty;
import fpozzi.stopper.model.PromoStopper;
import fpozzi.stopper.model.pdf.CodaStampa;
import fpozzi.stopper.model.pdf.CodaStampaObserver;
import fpozzi.stopper.model.pdf.MergeException;
import fpozzi.stopper.model.pdf.PdfStopper;
import fpozzi.stopper.model.pdf.PdfStopperRequest;
import fpozzi.stopper.model.pdf.PdfStopperStyle;
import fpozzi.stopper.model.pdf.UnsupportedStyleException;
import fpozzi.stopper.view.CodaStampaView;
import fpozzi.stopper.view.CodaStampaView.CodaStampaViewObserver;
import fpozzi.utils.exception.FileInUseException;
import fpozzi.utils.swing.ConfirmDialogOptions;

public class CodaStampaController implements CodaStampaViewObserver, CodaStampaObserver
{	
	private final PrintService printService;
	
	private final CodaStampaView view;

	private CodaStampa codaStampa;

	private File file;

	private boolean changedSinceLastSave;

	private final AddController addController;

	public CodaStampaController(File file, CodaStampaView view) throws Exception
	{
		this.view = view;
		view.setObserver(this);
		addController = new AddController(this);
		this.codaStampa = null;
		setFile(file);

		if (file == null)
		{
			setCodaStampa(new CodaStampa());
		}
		else if (file.exists())
		{
			setCodaStampa(CodaStampa.fromFile(file));
		}
		else
			throw new FileNotFoundException();
		
		String stampante = StampaStopperProperties.getInstance().getProperty(StampaStopperProperty.STAMPANTE).trim();
		
		PrintService printService = null;
		if (!stampante.equals(""))						
		{
			 for(PrintService ps: PrinterJob.lookupPrintServices()) 
		            if (ps.getName().indexOf(stampante)>=0) 
		            	printService = ps;
			 if (printService == null)
				 StampaStopperLogger.get().severe("Stampante '" + stampante + "' non trovata!");
		}
		this.printService = printService;
	
	}

	public CodaStampaView getView()
	{
		return view;
	}

	public CodaStampa getCodaStampa()
	{
		return codaStampa;
	}

	private void setCodaStampa(CodaStampa codaStampa)
	{
		if (this.codaStampa != null)
		{
			codaStampa.detachObserver(view);
			codaStampa.detachObserver(this);
			for (List<PdfStopperRequest> requests : codaStampa.getRequests().values())
				for (PdfStopperRequest request : requests)
				{
					request.detachObserver(view);
				}
		}
		this.codaStampa = codaStampa;
		if (codaStampa != null)
		{
			view.setCodaStampa(codaStampa);
			codaStampa.attachObserver(view);
			codaStampa.attachObserver(this);
			for (List<PdfStopperRequest> requests : codaStampa.getRequests().values())
				for (PdfStopperRequest request : requests)
				{
					request.attachObserver(view);
				}
		}
	}

	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file = file;
		changedSinceLastSave = false;
		view.setFile(file);
	}

	public boolean hasChangedSinceLastSave()
	{
		return changedSinceLastSave;
	}

	public void saveAs(File file)
	{
		if (file == null)
		{
			view.askSaveAsNewFile();
		}
		else if (changedSinceLastSave)
		{
			boolean performSave;
			if (file.exists())
			{
				performSave = file.equals(this.file) || view.askPermissionToOverwrite(file);

			}
			else
			{
				performSave = true;
				if (!file.getName().endsWith(".xml"))
					file = new File(file.getParent(), file.getName() + ".xml");
			}

			if (performSave)
			{
				try
				{
					codaStampa.toFile(file);
					setFile(file);
					StampaStopperLogger.get().info("Coda di stampa salvata con successo in " + file.getName());
				}
				catch (Exception e)
				{
					StampaStopperLogger.get().severe("Impossibile salvare il file " + file.getName() + ": " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	public void generaPDF(PdfStopperStyle style)
	{
		generatePDF(style, makePdfFileStamp());
	}

	private String makePdfFileStamp()
	{
		if (file != null)
		{
			int extensionStart = file.getName().indexOf('.');

			if (extensionStart >= 0)
				return file.toString().substring(0, file.toString().length() - file.getName().length() + extensionStart);
			else
				return file.toString();
		}

		return "new";
	}

	private void generatePDF(PdfStopperStyle style, String stamp)
	{

		try
		{
			File pdfFile = codaStampa.makePdfFile(style, stamp);
			StampaStopperLogger.get().info("Il file '" + pdfFile.getName() + "' è stato creato.");
			Desktop.getDesktop().open(pdfFile);
		}
		catch (FileInUseException e)
		{
			if (view.askToCloseOpenFile(e.getFileInUse()))
				generatePDF(style, stamp);
		}
		catch (DocumentException | UnsupportedStyleException | IOException e)
		{
			StampaStopperLogger.get().severe("Impossibile generare un pdf per lo stile \'" + style.getDescrizione() + "\': " + e.getMessage());
		}

	}
	
	public void print(PdfStopperStyle style)
	{
		try
		{
			if (printService==null)
				throw new Exception("stampante non trovata");
				
			codaStampa.printPdfFile(style, printService);
			StampaStopperLogger.get().info("La coda '" + style.getDescrizione() + "' è stata mandata in stampa.");
		}
		catch (Exception e)
		{
			StampaStopperLogger.get().severe("Impossibile stampare per lo stile \'" + style.getDescrizione() + "\': " + e.getMessage());
		}

	}

	public void generateAllPDFs()
	{
		String stamp = makePdfFileStamp();

		for (PdfStopperStyle style : PdfStopperStyle.values())
		{
			if (codaStampa.getRequests(style).size() > 0)
			{
				generatePDF(style, stamp);
			}
		}
	}

	public void addRequest(PdfStopperRequest request)
	{
		request.attachObserver(view);
		codaStampa.addRequest(request);
	}

	public void removeRequest(PdfStopperRequest request)
	{
		request.detachObserver(view);
		codaStampa.removeRequest(request);
	}

	public void save()
	{
		saveAs(file);
	}

	public void refresh()
	{
		try
		{
			if (!changedSinceLastSave || view.askPermissionToClear())
				setCodaStampa(CodaStampa.fromFile(file));
			changedSinceLastSave = false;
			view.setFile(file);
			StampaStopperLogger.get().info("Coda di stampa ricaricata con successo da " + file.getName());
		}
		catch (Exception e)
		{
			StampaStopperLogger.get().info("Ricarica non riuscita da " + file.getName());
		}
	}

	@Override
	public void clear()
	{
		if (changedSinceLastSave && view.askPermissionToClear())
			setCodaStampa(new CodaStampa());
	}

	public boolean saveAndClose()
	{
		if (changedSinceLastSave)
		{
			ConfirmDialogOptions answer = view.askSaveBeforeClosing();
			if (answer == ConfirmDialogOptions.CANCEL)
				return false;
			if (answer == ConfirmDialogOptions.YES)
				try
				{
					save();
				}
				catch (Exception e)
				{
					StampaStopperLogger.get().severe("Errore durante il salvataggio");
				}
		}
		return true;
	}

	@Override
	public void changeStyle(PdfStopperRequest request, PdfStopperStyle newStyle)
	{
		try
		{
			request.setStyle(newStyle);
			logModificaStopper(request);
		}
		catch (UnsupportedStyleException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void changePeriodoOfferta(List<PdfStopperRequest> requests, PeriodoOfferta periodo)
	{
		List<PdfStopperRequest> promoStopperRequests = new ArrayList<PdfStopperRequest>(requests.size());
		for (PdfStopperRequest request : requests)
		{
			PromoStopper stopper;
			PdfStopper<?> pdfStopper = request.getPdfStopper();
			if (pdfStopper.getStopper() instanceof PromoStopper)
			{
				PdfStopper<?> clonedPdfStopper = (PdfStopper<?>)pdfStopper.clone();
				stopper = (PromoStopper) clonedPdfStopper.getStopper();
				stopper.setPeriodo(periodo);
				try
				{
					request.setPdfStopper(clonedPdfStopper); // this will trigger
														// request observers
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				promoStopperRequests.add(request);
			}
		}
		logModificaStopper(promoStopperRequests);
	}

	private void logModificaStopper(PdfStopperRequest request)
	{
		StampaStopperLogger.get().info("Stopper '" + request.getPdfStopper().getDescrizione() + "' modificato");
	}

	private void logModificaStopper(Collection<PdfStopperRequest> requests)
	{
		if (requests.size() == 1)
			logModificaStopper(requests.iterator().next());
		else
			StampaStopperLogger.get().info(requests.size() + " stopper modificati");
	}

	@Override
	public void requestAdded(CodaStampa codaStampa, PdfStopperRequest request)
	{
		changedSinceLastSave = true;
	}

	@Override
	public void requestRemoved(CodaStampa codaStampa, PdfStopperRequest request)
	{
		changedSinceLastSave = true;
	}

	@Override
	public void requestMoved(CodaStampa codaStampa, PdfStopperRequest request, PdfStopperStyle oldStyle, PdfStopperStyle newStyle)
	{
		changedSinceLastSave = true;
	}

	@Override
	public void requestChanged(CodaStampa codaStampa, PdfStopperRequest request)
	{
		changedSinceLastSave = true;
	}

	@Override
	public void stopperCountChanged(CodaStampa codaStampa, PdfStopperStyle style, int oldCount, int newCount)
	{

	}

	@Override
	public void duplicate(PdfStopperRequest request)
	{
		try
		{
			PdfStopperRequest duplicateRequest = new PdfStopperRequest((PdfStopper<?>) request.getPdfStopper().clone(), request.getStyle(), 1);
			addRequest(duplicateRequest);
			view.selectRequest(duplicateRequest);
		}
		catch (UnsupportedStyleException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void finalize() throws Throwable
	{
		addController.finalize();
		super.finalize();
	}

	Pattern descriptionSplitPattern = Pattern.compile("\\s*(\\w+)(\\W+|$)");

	@Override
	public void mergeRequests(List<PdfStopperRequest> selectedRequests, PdfStopperRequest selectionLeader) throws MergeException
	{
		if (selectedRequests.size() < 2)
			return;

		PdfStopper<?> selectionLeaderStopper = (PdfStopper<?>) selectionLeader.getPdfStopper().clone();
		int copies = selectionLeader.getCopies();

		for (PdfStopperRequest selectedRequest : selectedRequests)
		{
			if (selectedRequest != selectionLeader)
			{
				selectedRequest.getPdfStopper().mergeInto(selectionLeaderStopper);
				copies += selectedRequest.getCopies();
			}
		}

		try
		{
			selectionLeader.setPdfStopper(selectionLeaderStopper);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		selectionLeader.setCopies(copies);

		for (PdfStopperRequest selectedRequest : selectedRequests)
		{
			if (selectedRequest != selectionLeader)
			{
				removeRequest(selectedRequest);
			}
		}

		view.selectRequest(selectionLeader);
	}

	@Override
	public void refreshRequests(List<PdfStopperRequest> selectedRequests, PdfStopperRequest selectionLeader)
	{
	
		
	}

}
