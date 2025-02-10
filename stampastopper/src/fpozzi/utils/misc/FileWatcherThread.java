package fpozzi.utils.misc;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fpozzi.utils.threading.CancelableThread;

public class FileWatcherThread extends CancelableThread
{

	private final File watchedFile;
	private final List<FileObserver> observers;

	public FileWatcherThread(final File watchedFile)
	{
		this.watchedFile = watchedFile;
		observers = new LinkedList<FileObserver>();
	}
	
	public void attachObserver(FileObserver observer)
	{
		observers.add(observer);
	}
	
	public void detachObserver(FileObserver observer)
	{
		observers.remove(observer);
	}

	@Override
	public void run()
	{
		try (WatchService watcher = FileSystems.getDefault().newWatchService())
		{
			Path path = watchedFile.toPath().getParent();
			path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
			while (!this.isCancelled())
			{
				WatchKey key;
				try
				{
					key = watcher.poll(25, TimeUnit.MILLISECONDS);
				}
				catch (InterruptedException e)
				{
					return;
				}
				if (key == null)
				{
					Thread.yield();
					continue;
				}
				for (WatchEvent<?> event : key.pollEvents())
				{
					WatchEvent.Kind<?> kind = event.kind();

					@SuppressWarnings("unchecked")
					WatchEvent<Path> ev = (WatchEvent<Path>) event;
					Path filename = ev.context();

					if (kind == StandardWatchEventKinds.OVERFLOW)
					{
						continue;
					}
					else if (kind == java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY && filename.toString().equals(watchedFile.getName()))
					{
						for (FileObserver observer: observers)
							observer.fileUpdated();
					}
					boolean valid = key.reset();
					if (!valid)
					{
						break;
					}
				}
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}
	
	public interface FileObserver 
	{
		public void fileUpdated();
	}

}
