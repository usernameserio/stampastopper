package fpozzi.utils.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;

public class JTitleBar extends GradientPanel
{
	private final JLabel titleLabel;
	
	public JTitleBar(String title, Font font, Color fontColor, Icon icon, Color backgroundColor, Color controlColor)
	{
		super(backgroundColor, controlColor);
		
		titleLabel = new JLabel(title, icon, JLabel.LEADING);
		if (font!=null) titleLabel.setFont(font);
		titleLabel.setForeground(fontColor);

		setLayout(new BorderLayout());
		add(titleLabel, BorderLayout.WEST);
		int borderOffset = 2;
		if (icon == null)
		{
			borderOffset += 1;
		}
		setBorder(BorderFactory.createEmptyBorder(borderOffset, 4, borderOffset, 1));
		
	}
	
	public JTitleBar(String title, Font font, Color fontColor, Icon icon, Color backgroundColor)
	{
		this(title, font, fontColor, icon, backgroundColor, backgroundColor);
	}
	
	public JTitleBar(String title, Font font, Color fontColor, Icon icon)
	{
		this(title, font, fontColor, icon, SystemColor.control);
	}
	
	public JTitleBar(String title, Font font, Color fontColor)
	{
		this(title, font, fontColor, null);
	}
	
	public JTitleBar(String title, Font font)
	{
		this(title, font, SystemColor.controlText);
	}
	
	public JTitleBar(String title)
	{
		this(title, null);
	}
	
	public JLabel getTitleLabel()
	{
		return titleLabel;
	}

}