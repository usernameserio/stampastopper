package fpozzi.utils.swing;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.JPanel;

class GradientPanel extends JPanel
{
	private static final long serialVersionUID = -6385751027379193053L;

	private final Color controlColor;

	public GradientPanel(Color background, Color controlColor)
	{
		setBackground(background);
		this.controlColor = controlColor;
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		if (isOpaque())
		{
			int width = getWidth();
			int height = getHeight();

			Graphics2D g2 = (Graphics2D) g;
			Paint oldPaint = g2.getPaint();
			g2.setPaint(new GradientPaint(0, 0, getBackground(), width, 0, controlColor));
			g2.fillRect(0, 0, width, height);
			g2.setPaint(oldPaint);
		}
	}
}