package fpozzi.stopper.view.swing;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;

import net.java.balloontip.styles.BalloonTipStyle;
import net.java.balloontip.styles.EdgedBalloonStyle;

public class Style
{

	static public final Color titleBgColor = new Color(180, 180, 180);
	static public final Color titleBgColor2 = titleBgColor;
	static public final Color titleFontColor = Color.WHITE;
	static public final Color balloonBgColor = new Color(255, 248, 176);

	static public final Color editorPanelColor = titleBgColor.brighter();

	static public BalloonTipStyle makePreviewTipBalloonStyle()
	{
		return new EdgedBalloonStyle(balloonBgColor, Color.GRAY);
	}

	public static class Fonts
	{
		static public final Font defaultFont, italicFont, boldFont, noteFont, noteBoldFont;

		static
		{
			defaultFont = new JLabel("").getFont();
			italicFont = new Font(defaultFont.getName(), Font.ITALIC, defaultFont.getSize());
			boldFont = new Font(defaultFont.getName(), Font.BOLD, defaultFont.getSize());

			noteFont = new Font(defaultFont.getName(), Font.PLAIN, defaultFont.getSize() - 1);
			noteBoldFont = new Font(noteFont.getName(), Font.BOLD, noteFont.getSize());
		}
	}
}
