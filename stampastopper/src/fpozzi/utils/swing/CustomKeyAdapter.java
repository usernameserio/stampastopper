package fpozzi.utils.swing;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class CustomKeyAdapter
{

	public static final KeyAdapter integersOnlyKeyAdapter = new KeyAdapter()
	{
		public void keyTyped(KeyEvent e)
		{
			char c = e.getKeyChar();
			if ((c >= '0' && c <= '9') || (c == KeyEvent.VK_BACK_SPACE))
				 return;
			e.consume();
		}
	};
	
	public static final KeyAdapter numbersOnlyKeyAdapter = new KeyAdapter()
	{
		public void keyTyped(KeyEvent e)
		{
			char c = e.getKeyChar();
			if ((c >= '0' && c <= '9') || (c == KeyEvent.VK_BACK_SPACE) || (c == '.') || (c == ','))
				 return;
			e.consume();
		}
	};
}
