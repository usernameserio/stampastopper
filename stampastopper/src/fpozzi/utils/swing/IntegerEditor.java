package fpozzi.utils.swing;

import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;

import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 * Implements a cell editor that uses a formatted text field to edit Integer
 * values.
 */
public class IntegerEditor extends DefaultCellEditor
{
	final JFormattedTextField textField;
	final ValueCheckAction valueCheckAction;
	final NumberFormat integerFormat;
	final private Integer minimum, maximum;

	public IntegerEditor(int min, int max)
	{
		super(new JFormattedTextField());

		this.setClickCountToStart(1);

		textField = (JFormattedTextField) getComponent();
		textField.addKeyListener(CustomKeyAdapter.integersOnlyKeyAdapter);

		minimum = new Integer(min);
		maximum = new Integer(max);

		valueCheckAction = new ValueCheckAction(textField, "un numero intero positivo compreso tra " + minimum + " e " + maximum);

		// Set up the editor for the integer cells.
		integerFormat = NumberFormat.getIntegerInstance();
		NumberFormatter intFormatter = new NumberFormatter(integerFormat);
		intFormatter.setFormat(integerFormat);
		intFormatter.setMinimum(minimum);
		intFormatter.setMaximum(maximum);

		textField.setFormatterFactory(new DefaultFormatterFactory(intFormatter));
		textField.setValue(minimum);
		textField.setHorizontalAlignment(JTextField.LEFT);
		textField.setFocusLostBehavior(JFormattedTextField.PERSIST);

		// React when the user presses Enter while the editor is
		// active. (Tab is handled as specified by
		// JFormattedTextField's focusLostBehavior property.)
		textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
		textField.getActionMap().put("check", valueCheckAction);

		textField.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						textField.selectAll();
					}
				});
			}
		});
	}

	// Override to invoke setValue on the formatted text field.
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		JFormattedTextField textField = (JFormattedTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
		textField.setValue(value);
		textField.selectAll();
		return textField;
	}

	// Override to ensure that the value remains an Integer.
	public Object getCellEditorValue()
	{
		JFormattedTextField textField = (JFormattedTextField) getComponent();
		Object o = textField.getValue();
		if (o instanceof Integer)
		{
			return o;
		}
		else if (o instanceof Number)
		{
			return new Integer(((Number) o).intValue());
		}
		else
		{
			return null;
		}
	}

	// Override to check whether the edit is valid,
	// setting the value if it is and complaining if
	// it isn't. If it's OK for the editor to go
	// away, we need to invoke the superclass's version
	// of this method so that everything gets cleaned up.
	public boolean stopCellEditing()
	{
		JFormattedTextField textField = (JFormattedTextField) getComponent();
		if (textField.isEditValid())
		{
			try
			{
				textField.commitEdit();
			}
			catch (java.text.ParseException exc)
			{
			}

		}
		else
		{
			if (!valueCheckAction.userSaysRevert())
			{
				return false;
			}
		}
		return super.stopCellEditing();
	}

}
