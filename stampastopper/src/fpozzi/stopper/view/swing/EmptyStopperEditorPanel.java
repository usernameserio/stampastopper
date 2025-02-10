package fpozzi.stopper.view.swing;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

import fpozzi.stopper.model.pdf.EmptyPdfStopper;

public class EmptyStopperEditorPanel extends StopperEditorPanel<EmptyPdfStopper>
{

	private static final long serialVersionUID = 1L;

	public EmptyStopperEditorPanel()
	{
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		add(Box.createRigidArea(new Dimension(0, 10)));
		JLabel label = new JLabel("Stopper vuoto");
		label.setAlignmentX(CENTER_ALIGNMENT);
		add(label);
	}

	@Override
	public EmptyPdfStopper getPdfStopper()
	{
		return EmptyPdfStopper.instance;
	}

	@Override
	protected void toForm(EmptyPdfStopper pdfStopper)
	{}


}
