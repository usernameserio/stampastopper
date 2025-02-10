package fpozzi.utils.swing;

import java.awt.event.FocusAdapter;

import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

public class SelectAllFocusAdapter extends FocusAdapter
{
	private JTextComponent field;

	public SelectAllFocusAdapter(JTextComponent field)
	{
		this.field = field;
	}

	@Override
	public void focusGained(java.awt.event.FocusEvent evt)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				field.selectAll();
			}
		});
	}
};
