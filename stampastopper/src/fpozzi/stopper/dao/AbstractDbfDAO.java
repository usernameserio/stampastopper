package fpozzi.stopper.dao;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.xBaseJ.DBF;

import fpozzi.utils.misc.FileWatcherThread;
import fpozzi.utils.misc.FileWatcherThread.FileObserver;

public abstract class AbstractDbfDAO implements FileObserver
{

	static protected final File dir = new File("tmp");
	static
	{
		if (!dir.exists())
			dir.mkdirs();
		dir.deleteOnExit();
	}

	protected final File dbFile, indexFile;
	protected File dbFileCopy;
	private DBF db;
	private FileWatcherThread dbFileWatcherThread;
	private boolean needsUpdating;
	
	protected AbstractDbfDAO(File dbFile, File indexFile) throws Exception
	{
		this.dbFile = dbFile;
		this.indexFile = indexFile;

		dbFileCopy = null;
		db = null;
		dbFileWatcherThread = null;

		initDb();
		if (db != null)
		{
			dbFileWatcherThread = new FileWatcherThread(dbFile);
			dbFileWatcherThread.attachObserver(this);
			dbFileWatcherThread.start();
		}
	}

	protected AbstractDbfDAO(File dbFile) throws Exception
	{
		this(dbFile, null);
	}
	
	@Override
	public void fileUpdated()
	{
		needsUpdating = true;
	}
	
	protected synchronized DBF getDb() throws Exception
	{
		if (needsUpdating)
			initDb();
		return db;
	}
	
	public synchronized void initDb() throws Exception
	{
		if (db != null)
			db.close();

		dbFileCopy = new File(dir, dbFile.getName());
		dbFileCopy.deleteOnExit();

		
		Files.copy(dbFile.toPath(), dbFileCopy.toPath(), StandardCopyOption.REPLACE_EXISTING);

		db = new DBF(dbFileCopy.getAbsolutePath(), DBF.READ_ONLY);
		if (indexFile != null)
			db.useIndex(indexFile.getAbsolutePath());
		
		needsUpdating = false;
	}

	protected abstract String getDbDescription();

	@Override
	public void finalize() throws Throwable
	{
		if (dbFileWatcherThread!=null)
			dbFileWatcherThread.cancel();
		if (db != null)
			db.close();
		super.finalize();
	}

}
