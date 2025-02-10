package fpozzi.stopper.model.pdf.style.fonts;	

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import org.apache.pdfbox.io.IOUtils;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;

public class FontFactory
{
	public enum FontStyle { 
		CLAN_BOOK("Clan-Book.otf") , 
		CLAN_ULTRA("Clan-Ultra.otf"), 
		CLAN_BOLD("Clan-Bold.otf"),
		CLAN_NARROW("ClanPro-NarrBold.otf"),
				
		FUTURA_BOOK("Futura-Std-Book.ttf") , 
		FUTURA_BOLD("Futura-Std-Bold.ttf"), 
		FUTURA_MEDIUM("Futura-Std-Medium.ttf"),
		FUTURA_HEAVY("Futura-Std-Heavy.ttf")
		;

		private BaseFont baseFont;
		
		private	FontStyle(String fontFileName)
		{
			try
			{
				byte[] bytes = IOUtils.toByteArray(getClass().getResourceAsStream("/fonts/" + fontFileName));
				this.baseFont = (BaseFont.createFont(fontFileName, BaseFont.CP1252, BaseFont.EMBEDDED, true, bytes, null));
			} 
			catch (DocumentException | IOException e)
			{
				System.err.println(e.getMessage());
				System.exit(0);
			}
		}
		
		public BaseFont geBaseFont()
		{
				return baseFont;
		};
		
	}
	
	private static final ArrayList<TreeMap<Integer,Font>> font;
	
	public static final Image estimatedSign;
	
	public static void Init() {}
	
	static
	{

		font = new ArrayList<TreeMap<Integer,Font>>(FontStyle.values().length);
		for (int i=0; i<FontStyle.values().length; i++)
			font.add(i, new TreeMap<Integer,Font>());
		
		Image i = null;
		try
		{
			i = Image.getInstance(FontFactory.class.getResource("/fonts/estimated_sign.png"));
		}
		catch (BadElementException | IOException e)
		{
			e.printStackTrace();
		} 
		estimatedSign = i;
		
	}
	
	public static Font getFont(FontStyle style, int size)
	{
		TreeMap<Integer,Font> fontMap = font.get(style.ordinal());
		if (fontMap.get(size)==null)
			fontMap.put(size, new Font(style.geBaseFont(), size));
		return fontMap.get(size);
	}
	
}
