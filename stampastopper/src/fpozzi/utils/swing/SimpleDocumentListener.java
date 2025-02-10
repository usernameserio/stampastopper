package fpozzi.utils.swing;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class SimpleDocumentListener implements DocumentListener
{

	@Override
	public void changedUpdate(DocumentEvent e)
	{}

	@Override
	public void insertUpdate(DocumentEvent e)
	{
		textUpdate();		
	}

	@Override
	public void removeUpdate(DocumentEvent e)
	{
		textUpdate();
	}

	protected abstract void textUpdate();

}
