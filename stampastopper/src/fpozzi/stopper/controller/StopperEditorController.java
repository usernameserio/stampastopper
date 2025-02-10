package fpozzi.stopper.controller;

import fpozzi.stopper.model.pdf.PdfStopper;
import fpozzi.stopper.view.StopperEditorView;

public class StopperEditorController<T extends PdfStopper<?>, V extends StopperEditorView<T>>
{
	protected final V view;
	
	private T currentStopper; 
	
	public StopperEditorController(V view)
	{
		this.view = view;
		setCurrentStopper(null);
	}
	
	public final V getView()
	{
		return view;
	}

	public final T getCurrentStopper()
	{
		return currentStopper;
	}

	public final void setCurrentStopper(T currentStopper) 
	{
		this.currentStopper = currentStopper;
		//view.setPdfStopper(currentStopper);
	}
	

}
