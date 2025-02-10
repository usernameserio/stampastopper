package fpozzi.stopper.view;

import java.util.List;

import fpozzi.stopper.model.pdf.PdfPromoStopper;

public interface PromoStopperEditorView extends StopperEditorView<PdfPromoStopper>
{
	public void setObserver(PromoEditorObserver observer);
	
	public void notifySearchResult(List<PdfPromoStopper> searchResult);
	
	public void setSearchEnabled(boolean b);
	
	public interface PromoEditorObserver
	{
		public void searchCode(String ean);
	}
}
