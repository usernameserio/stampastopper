package fpozzi.stopper.view.swing.codastampa;

import java.awt.Insets;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

public class CodaStampaButton extends JButton
{
	private static final long serialVersionUID = 1L;
	
	public CodaStampaButton(String description, Icon icon, Action action)
	{
		super(action);
		setMargin(new Insets(5, 15, 5, 10));
		setIcon(icon);
		setVerticalTextPosition(SwingConstants.CENTER);
		setHorizontalTextPosition(SwingConstants.LEFT);
		setFocusPainted(false);
		setText(description + " ");
		setIcon(icon);
	}
	
	public CodaStampaButton(String description, Icon icon)
	{
		this(description, icon, null);
	}

}