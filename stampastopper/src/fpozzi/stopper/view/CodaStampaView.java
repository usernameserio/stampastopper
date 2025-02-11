package fpozzi.stopper.view;

import java.io.File;
import java.util.List;

import fpozzi.gdoshop.model.offerta.PeriodoOfferta;
import fpozzi.stopper.model.pdf.CodaStampa;
import fpozzi.stopper.model.pdf.CodaStampaObserver;
import fpozzi.stopper.model.pdf.MergeException;
import fpozzi.stopper.model.pdf.PdfStopperRequest;
import fpozzi.stopper.model.pdf.PdfStopperRequestObserver;
import fpozzi.stopper.model.pdf.PdfStopperStyle;
import fpozzi.utils.Clipboard;
import fpozzi.utils.swing.ConfirmDialogOptions;

public interface CodaStampaView extends CodaStampaObserver, PdfStopperRequestObserver
{

	public void setObserver(CodaStampaViewObserver observer);
	
	public void attachSelectionObserver(RequestSelectionObserver observer);

	public void detachSelectionObserver(RequestSelectionObserver observer);

	public AddView getAddView();
	
	public void setClipboard(Clipboard<List<PdfStopperRequest>> clipboard);

	public void setCodaStampa(CodaStampa codaStampa);

	public CodaStampa getCodaStampa();

	public void setFile(File file);

	public File getFile();

	public boolean askPermissionToOverwrite(File file);

	public void setCommandsEnabled(boolean enabled);

	public void selectRequest(PdfStopperRequest request);

	public PdfStopperRequest getSelectedRequest();

	public boolean askPermissionToClear();
	
	public ConfirmDialogOptions askSaveAsNewFile();

	public ConfirmDialogOptions askSaveBeforeClosing();

	public boolean askPermissionToDelete(File file);

	public boolean askToCloseOpenFile(File fileInUse);

	public interface CodaStampaViewObserver
	{

		public void generatePDF(PdfStopperStyle style);
		
		public void print(PdfStopperStyle style);

		public void generateAllPDFs();

		public void addRequest(PdfStopperRequest request);

		public void removeRequest(PdfStopperRequest request);

		public void changePeriodoOfferta(List<PdfStopperRequest> requests, PeriodoOfferta periodo);

		public void clear();

		public void changeStyle(PdfStopperRequest request, PdfStopperStyle newStyle);
		
		public void saveAs(File file);

		public void duplicate(PdfStopperRequest request);

		public void mergeRequests(List<PdfStopperRequest> selectedRequests, PdfStopperRequest selectionLeader) throws MergeException;

		public void refreshRequests(List<PdfStopperRequest> selectedRequests, PdfStopperRequest selectionLeader);
	}

	public interface RequestSelectionObserver
	{

		public void requestSelected(PdfStopperRequest selectedRequest);

	}

}
