package fpozzi.utils.swing;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JLabel;

public class UnderlinedLabel extends JLabel
{

	private static final long serialVersionUID = 1L;

	public UnderlinedLabel()
	{
		this("");
	}

	public UnderlinedLabel(String text)
	{
		super(text);
	}

	public void paint(Graphics g)
	{
		Rectangle r;
		super.paint(g);
		r = g.getClipBounds();
		g.drawLine(0, r.height+1 - getFontMetrics(getFont()).getDescent(),
				getFontMetrics(getFont()).stringWidth(getText()), r.height+1
						- getFontMetrics(getFont()).getDescent());
	}
}
