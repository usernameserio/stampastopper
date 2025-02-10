package fpozzi.utils.threading;

public class CancelableThread extends Thread
{
	private boolean cancelled = false;

	public CancelableThread()
	{
		super();
	}

	public synchronized void cancel()
	{
		cancelled = true;
	}

	public synchronized boolean isCancelled()
	{
		return cancelled;
	}

}
