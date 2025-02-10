package fpozzi.utils.swing;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ValueCheckAction extends AbstractAction
{
	
	private final JFormattedTextField textField;
	private final String correctValueDescription;
	
	public ValueCheckAction(JFormattedTextField textField, String correctValueDescription)
	{
		this.textField = textField;
		this.correctValueDescription = correctValueDescription;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (!textField.isEditValid())
		{ // The text is invalid.
			if (userSaysRevert())
			{ // reverted
				textField.postActionEvent(); // inform the editor
			}
		} else
			try
			{ // The text is valid,
				textField.commitEdit(); // so use it.
				textField.postActionEvent(); // stop editing
			} catch (java.text.ParseException exc)
			{
			}
	}
	
	protected boolean userSaysRevert()
	{
		Toolkit.getDefaultToolkit().beep();
		textField.selectAll();
		Object[] options = { "Modifica", "Ripristina" };
		int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(textField), 
				"Il valore deve essere " + correctValueDescription + ".\n" + 
				"Puoi modificare il campo oppure ripristinarlo all'ultimo valore valido.", "Valore non accettato",
				JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[1]);

		if (answer == 1)
		{ // Revert!
			textField.setValue(textField.getValue());
			return true;
		}
		return false;
	}
	
}