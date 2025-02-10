package fpozzi.stopper.view.swing;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ButtonTab extends JPanel
{
	private static final long serialVersionUID = 1L;

	private final JLabel titleLabel;
	private final JButton closeButton;

	public ButtonTab()
	{
		super(new BorderLayout());

		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 0));
		setOpaque(false);
		titleLabel = new JLabel();
		titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		add(titleLabel, BorderLayout.CENTER);

		closeButton = new JButton(Icons.CLOSE_TAB.image);
		closeButton.setOpaque(false);
		closeButton.setContentAreaFilled(false);
		closeButton.setBorderPainted(false);
		closeButton.setBorder(BorderFactory.createEmptyBorder());
		add(closeButton, BorderLayout.EAST);
	}

	public JLabel getTitleLabel()
	{
		return titleLabel;
	}

	public JButton getCloseButton()
	{
		return closeButton;
	}
	
	
}