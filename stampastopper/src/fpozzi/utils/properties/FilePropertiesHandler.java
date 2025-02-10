package fpozzi.utils.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;

public class FilePropertiesHandler<PROPLISTTYPE extends Enum<?>>
{
	private EnumedProperties<PROPLISTTYPE> defaultProperties;
	
	private EnumedProperties<PROPLISTTYPE> properties;
	private File propertiesFile;
	
	public FilePropertiesHandler(File propertiesFile,
			EnumedProperties<PROPLISTTYPE> defaultProperties)
	{
		super();
		this.propertiesFile = propertiesFile;
		this.defaultProperties = defaultProperties;
		load();
	}
	
	public void setProperty(PROPLISTTYPE propertyName, String value)
	{
		properties.setProperty(propertyName, value);
	}
	
	public String getProperty(PROPLISTTYPE propertyName)
	{
		return properties.getProperty(propertyName);
	}
	
	public String getProperty(PROPLISTTYPE propertyName, String defaultValue)
	{
		return properties.getProperty(propertyName, defaultValue);
	}
	
	public File getPropertiesFile()
	{
		return propertiesFile;
	}
	
	private void load()
	{
		properties = new EnumedProperties<PROPLISTTYPE>();
		FileInputStream inStream = null;
		try 
		{
			inStream = new FileInputStream(propertiesFile);
			properties.load(inStream);
		} 
		catch (IOException e) 
		{
			for (Enumeration<?> property = defaultProperties.propertyNames(); property.hasMoreElements();)
			{				
				String propertyName = (String) property.nextElement();
				properties.setProperty(
						propertyName, 
						defaultProperties.getProperty(propertyName));
			}
			store();
		}
		finally
		{
			if (inStream!=null)
				try
				{ inStream.close();} catch (IOException e){}
		}

	}
	
	public void store()
	{
		FileOutputStream outStream = null;
		try 
		{
			outStream = new FileOutputStream(propertiesFile);
			properties.store(outStream, "");
		} 
		catch (IOException e) 
		{
			// do nothing
		}
		finally
		{
			if (outStream!=null)
				try
				{ outStream.close();} catch (IOException e){}
		}
	}
}
