package fpozzi.utils.properties;

import java.util.Properties;

@SuppressWarnings("serial")
public class EnumedProperties<ENUMTYPE extends Enum<?>> extends Properties
{

	public String getProperty(ENUMTYPE key, String defaultValue)
	{
		return super.getProperty(key.name().toLowerCase(), defaultValue);
	}

	public String getProperty(ENUMTYPE key)
	{
		return super.getProperty(key.name().toLowerCase());
	}

	public synchronized Object setProperty(ENUMTYPE key, String value)
	{
		return super.setProperty(key.name().toLowerCase(), value);
	}

	
	
}
