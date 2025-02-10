package fpozzi.stopper.view;

import fpozzi.stopper.model.pdf.CardPdf;

public interface CardEditorView extends StopperEditorView<CardPdf>
{
	public void setObserver(CardEditorObserver observer);
	
	public void notifySearchResult(String searchResult);
	
	public void setSearchEnabled(boolean b);
	
	public interface CardEditorObserver
	{
		public void searchCode(String ean);
	}
}
