package fpozzi.utils.swing;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;

import net.java.balloontip.positioners.BalloonTipPositioner;

public class SmartTipPositioner extends BalloonTipPositioner
{
	private final Window window;
	private final int x, y, xOffset, yOffset, xTipOffset, yTipOffset;

	public SmartTipPositioner(Window window, int x, int y, int xOffset, int yOffset, int xTipOffset, int yTipOffset)
	{
		super();
		this.window = window;
		this.x = x;
		this.y = y;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.xTipOffset = xTipOffset;
		this.yTipOffset = yTipOffset;
	}

	public void determineAndSetLocation(Rectangle attached)
	{
		int balloonX = attached.x + x + xOffset;
		balloonTip.getStyle().flipX(false);
		int rightOffscreen = balloonX + balloonTip.getPreferredSize().width - window.getWidth();
		if (rightOffscreen > 0)
		{
			int leftOffscreen = balloonTip.getPreferredSize().width + yOffset * 2 - balloonX;
			if (leftOffscreen < rightOffscreen)
			{
				balloonX -= balloonTip.getPreferredSize().width + xOffset * 2;
				balloonTip.getStyle().flipX(true);
			}
		}

		int balloonY = attached.y + y + yOffset;
		balloonTip.getStyle().flipY(true);
		if (balloonY + balloonTip.getPreferredSize().height > window.getHeight())
		{
			balloonY -= balloonTip.getPreferredSize().height + yOffset * 2;
			balloonTip.getStyle().flipY(false);
		}
		balloonTip.setBounds(balloonX, balloonY, balloonTip.getPreferredSize().width, balloonTip.getPreferredSize().height);
		balloonTip.validate();
	}

	public Point getTipLocation()
	{
		return new Point(x, y);
	}

	protected void onStyleChange()
	{
		balloonTip.getStyle().setHorizontalOffset(xTipOffset);
		balloonTip.getStyle().setVerticalOffset(yTipOffset);

	}
}