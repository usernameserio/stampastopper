package fpozzi.utils.exception;

import java.io.File;
import java.io.FileNotFoundException;

public class FileInUseException extends FileNotFoundException
{

	private static final long serialVersionUID = 1L;
	
	private final File fileInUse;

	public FileInUseException(File fileInUse)
	{
		super();
		this.fileInUse = fileInUse;
	}

	public File getFileInUse()
	{
		return fileInUse;
	}
		
}
