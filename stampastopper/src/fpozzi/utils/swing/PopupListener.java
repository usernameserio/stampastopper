package fpozzi.utils.swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

public class PopupListener extends MouseAdapter
{
	
	private final JPopupMenu popupmenu;
	
	public PopupListener(JPopupMenu popupmenu)
	{
		super();
		this.popupmenu = popupmenu;
	}

	public void mousePressed(MouseEvent e)
	{
		maybeShowPopup(e);
	}

	public void mouseReleased(MouseEvent e)
	{
		maybeShowPopup(e);
	}

	private void maybeShowPopup(MouseEvent e)
	{
		if (e.isPopupTrigger())
		{
			popupmenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}