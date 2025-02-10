package fpozzi.stopper.view;

import fpozzi.stopper.model.pdf.EmptyPdfStopper;
import fpozzi.stopper.model.pdf.FruttaPdfStopper;
import fpozzi.stopper.model.pdf.GastroPdfStopper;
import fpozzi.stopper.model.pdf.PdfStopper;
import fpozzi.stopper.model.pdf.PdfStopperEditor;
import fpozzi.stopper.model.pdf.PdfStopperRequest;
import fpozzi.stopper.model.pdf.PdfStopperStyle;
import fpozzi.stopper.model.pdf.UnsupportedStyleException;

public interface EditorView extends PdfStopperEditor
{	
	
	public void attachObserver(EditorViewObserver observer);
	
	public void detachObserver(EditorViewObserver observer);
	
	public void setStopperRequest(PdfStopperRequest stopperRequest);
	
	public PromoStopperEditorView getPromoStopperEditorView();

	public StopperEditorView<EmptyPdfStopper> getEmptyStopperEditorView();

	public StopperEditorView<FruttaPdfStopper> getFruttaStopperEditorView();

	public StopperEditorView<GastroPdfStopper> getGastroStopperEditorView();
	
	public CardEditorView getCardEditorView();
	
	public interface EditorViewObserver
	{		
		public void changeStyle(PdfStopperRequest request, PdfStopperStyle newStyle) throws UnsupportedStyleException;
		public void changeStopper(PdfStopperRequest request, PdfStopper<?> newStopper) throws UnsupportedStyleException;
		public void changeCopies(PdfStopperRequest request, int newCopies);
	}
	
}
