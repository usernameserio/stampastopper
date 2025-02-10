package fpozzi.utils.swing.observable;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

public abstract class ObservableJTextComponent<T extends JTextComponent> extends ObservableField<T, String>
{

	public ObservableJTextComponent(T field)
	{
		super(field);
		field.addFocusListener(new FieldFocusListener());
		field.getDocument().addDocumentListener(new FieldDocumentListener());
	}

	@Override
	public String getCurrentValue()
	{
		return field.getText();
	}

	@Override
	public void revert()
	{
		field.setText(getInitialValue());		
	}

	protected class FieldDocumentListener implements DocumentListener
	{

		@Override
		public void changedUpdate(DocumentEvent arg0){}

		@Override
		public void insertUpdate(DocumentEvent arg0)
		{
			 whileEdit();
		}

		@Override
		public void removeUpdate(DocumentEvent arg0)
		{
			 whileEdit();			
		}

	}

}
