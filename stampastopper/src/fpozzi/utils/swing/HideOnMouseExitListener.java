package fpozzi.utils.swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

public class HideOnMouseExitListener extends MouseAdapter implements Runnable
{
	public static final int defaultTimeout = 500; 
	
	private Thread timer = null;
	private final JComponent component;
	private int timeout;
	
	public HideOnMouseExitListener(JComponent component, int timeout)
	{
		this.component = component;
		this.timeout = timeout;
	}
	
	public HideOnMouseExitListener(JComponent component)
	{
		this(component, defaultTimeout);
	}

	public void mouseEntered(MouseEvent arg0)
	{
		if (timer != null && timer.isAlive())
			timer.interrupt();
	}

	public void mouseExited(MouseEvent arg0)
	{
		timer = new Thread(this);
		timer.start();
	}

	public void run()
	{
		try
		{
			Thread.sleep(timeout);
		}
		catch (InterruptedException e)
		{
			return;
		}
		component.setVisible(false);
	}
}