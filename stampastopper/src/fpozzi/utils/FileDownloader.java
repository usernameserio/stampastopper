package fpozzi.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import fpozzi.utils.misc.ObservableWrapper;


public class FileDownloader
{

	private String userAgent;
	private boolean overwrite;
	private ObservableWrapper<String> observableAction;

	public FileDownloader(String userAgent)
	{
		this.userAgent = userAgent;
		this.overwrite = false;
		observableAction = new ObservableWrapper<String>();
	}

	public FileDownloader()
	{
		this(null);
	}
		

	public ObservableWrapper<String> getObservableAction()
	{
		return observableAction;
	}

	public boolean overwriteEnabled()
	{
		return overwrite;
	}

	public void setOverwriteEnabled(boolean overwriteEnabled)
	{
		this.overwrite = overwriteEnabled;
	}

	public File downloadFromUrl(URL url, File destinationFolder,
			String destinationFileName) throws IOException
	{

		InputStream is = null;
		FileOutputStream fos = null;

		File localFile = new File(destinationFolder, destinationFileName);

		if (!localFile.exists() || overwrite)
			try
			{

				if (!destinationFolder.exists())
					destinationFolder.mkdirs();

				URLConnection urlConn = url.openConnection();
				
				urlConn.setConnectTimeout(3000);

				long remoteFileSize = urlConn.getContentLength();

				observableAction.setValue(
						"Download di "
								+ filenameFromUrl(url)
								+ (remoteFileSize > 0 ? " ["
										+ humanReadableByteCount(
												urlConn.getContentLength(),
												true) + "]" : ""));
				observableAction.notifyObservers(this);

				if (userAgent != null)
					urlConn.setRequestProperty("User-Agent", userAgent);

				is = urlConn.getInputStream();
	
				fos = new FileOutputStream(localFile);

				byte[] buffer = new byte[4096];
				int len;

				while ((len = is.read(buffer)) > 0)
					fos.write(buffer, 0, len);

			} finally
			{
				try
				{
					if (is != null)
						is.close();
				} finally
				{
					if (fos != null)
						fos.close();
				}
			}

		return localFile;
	}

	public File downloadFromUrl(URL url, File destinationFolder)
			throws IOException
	{

		return downloadFromUrl(url, destinationFolder, filenameFromUrl(url));

	}

	private static String filenameFromUrl(URL url)
	{
		return url.getFile().substring(url.getFile().lastIndexOf('/') + 1,
				url.getFile().length());
	}

	public static String humanReadableByteCount(long bytes, boolean si)
	{
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1)
				+ (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

}
