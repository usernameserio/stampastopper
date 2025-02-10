package fpozzi.stopper.view;

import java.io.File;

import fpozzi.stopper.model.pdf.CodaStampa;

public interface MainView
{
	
	public void setObserver(MainViewObserver observer);

	public EditorView getEditorView();

	public CodaStampaView makeCodaStampaView();
	
	public void addCodaStampaView(CodaStampaView view);

	public void removeCodaStampaView(CodaStampaView view);

	public void setActiveCodaStampaView(CodaStampaView view);
	
	public interface MainViewObserver
	{
		public void codaStampaSelected(CodaStampa codaStampa);
		
		public void newCodaStampa();
		
		public void open(File file);
		
		public void save();
		
		public void saveAs(File file);
		
		public void saveAll();

		public void refresh();
		
		public boolean close(CodaStampa codaStampa);
		
		public boolean closeAll();
	}
	
}
