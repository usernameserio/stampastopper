package fpozzi.stopper;

import java.util.logging.Logger;

public class StampaStopperLogger
{
	
	private static Logger instance = null;

	static
	{
		instance = Logger.getLogger(StampaStopperLogger.class.getName());
		instance.setUseParentHandlers(false);
	}

	static public Logger get()
	{
		return instance;
	}

}
