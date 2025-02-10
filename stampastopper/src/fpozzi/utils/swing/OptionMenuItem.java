package fpozzi.utils.swing;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.text.JTextComponent;

public class OptionMenuItem extends JMenuItem
{

	public OptionMenuItem(final String optionText, final Icon icon, final JTextComponent targetField)
	{
		super(new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				targetField.setText(optionText);
			}
		});
		this.setText(optionText);
		this.setIcon(icon);
	}

}