package fpozzi.utils.swing;

import java.util.Observable;
import java.util.Observer;

import javax.swing.SwingWorker;

import fpozzi.utils.misc.ObservableProgress;

public abstract class Task<T, V> extends SwingWorker<T, V>
{

	protected ObservableProgress progress;

	public Task()
	{
		progress = new ObservableProgress();
		progress.addObserver(new Observer()
		{

			public void update(Observable observable, Object arg1)
			{
				ObservableProgress progress = (ObservableProgress) observable;
				int intProgress = Math.round(progress.getValue());
				if (intProgress > getProgress())
					setProgress(intProgress);
			}
		});
	}

}
