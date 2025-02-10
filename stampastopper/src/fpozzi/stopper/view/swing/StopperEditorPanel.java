package fpozzi.stopper.view.swing;

import java.awt.LayoutManager;

import javax.swing.JPanel;

import fpozzi.stopper.model.pdf.PdfStopper;
import fpozzi.stopper.view.StopperEditorView;

public abstract class StopperEditorPanel<T extends PdfStopper<?>> extends JPanel implements StopperEditorView<T>
{

	private static final long serialVersionUID = 1L;

	protected T pdfStopper;

	public StopperEditorPanel()
	{
		super();
	}

	public StopperEditorPanel(LayoutManager layout)
	{
		super(layout);
	}
	

	public final void setPdfStopper(T pdfStopper)
	{
		this.pdfStopper = pdfStopper;
		toForm(pdfStopper);
	}
	
	protected abstract void toForm(T pdfStopper);
}
