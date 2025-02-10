package fpozzi.utils.swing.observable;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

public abstract class ObservableJComboBox<V> extends ObservableField<JComboBox<V>, V>
{

	public ObservableJComboBox(JComboBox<V> field)
	{
		super(field);
		field.addFocusListener(new FieldFocusListener());
		field.addItemListener(new FieldItemListener());
	}

	@Override
	public V getCurrentValue()
	{
		return field.getModel().getElementAt(field.getSelectedIndex());
	}
	

	@Override
	public void revert()
	{
		field.setSelectedItem(getInitialValue());
	}

	protected class FieldItemListener implements ItemListener
	{

		@Override
		public void itemStateChanged(ItemEvent arg0)
		{
			whileEdit();			
		}
		
	}
}
