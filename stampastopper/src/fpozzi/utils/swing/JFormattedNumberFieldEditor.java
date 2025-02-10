package fpozzi.utils.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import fpozzi.utils.swing.document.IntegersOnlyDocument;

/**
 * Implements a cell editor that uses a formatted text field to edit Integer
 * values.
 */
public class JFormattedNumberFieldEditor extends DefaultCellEditor
{
	private final JFormattedNumberField field;

	public JFormattedNumberFieldEditor(JFormattedNumberField field)
	{
		super(field);

		this.field = field;

		this.setClickCountToStart(2);

		field.setDocument(new IntegersOnlyDocument(false, 3));
		
		field.addFocusListener(new SelectAllFocusAdapter(field));
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		field.setValue(((Number) value).doubleValue());
		field.selectAll();
		return field;
	}

	public Object getCellEditorValue()
	{
		field.validate(field.getText());
		return field.getValue();
	}

	@Override
	public boolean stopCellEditing()
	{
		return super.stopCellEditing();
	}
	
	 

}
