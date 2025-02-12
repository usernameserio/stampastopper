package fpozzi.stopper;

import java.io.File;

import fpozzi.utils.properties.EnumedProperties;
import fpozzi.utils.properties.FilePropertiesHandler;

public class StampaStopperProperties extends FilePropertiesHandler<StampaStopperProperties.StampaStopperProperty>
{
	
	static private StampaStopperProperties instance;
	
	static public void Init() throws Exception
	{
		if (instance!=null)
			throw new Exception("StampaStopperProperties singleton già instanziato");
		instance = new StampaStopperProperties();
	}
	
	static public void Init(File propertiesFile) throws Exception
	{
		if (instance!=null)
			throw new Exception("StampaStopperProperties singleton già instanziato");
		instance = new StampaStopperProperties(propertiesFile);
	}
	
	static public StampaStopperProperties getInstance()
	{
		return instance;
	}

	static public enum StampaStopperProperty {
		WINDOW, SCHEDE_APERTE, SCHEDA_ATTIVA, GDOSHOP_DIR, CARD_PREFIX, 
		DEFAULT_PROMO_STYLE, DEFAULT_GASTRO_STYLE, DEFAULT_FRUTTA_STYLE,
		MYSQL_SERVER, MYSQL_PORT, MYSQL_DB, MYSQL_USER, MYSQL_PWD, FONT_PROMO,
		ENABLE_GDOSHOP, ENABLE_PROMO, ENABLE_GASTRO, ENABLE_CARDS, ENABLE_FRUTTA,
		 STAMPANTE
		
	}

	static private EnumedProperties<StampaStopperProperties.StampaStopperProperty> defaultProperties = new EnumedProperties<StampaStopperProperties.StampaStopperProperty>();
	static
	{
		defaultProperties.setProperty(StampaStopperProperty.WINDOW, "");
		defaultProperties.setProperty(StampaStopperProperty.SCHEDE_APERTE, "");
		defaultProperties.setProperty(StampaStopperProperty.SCHEDA_ATTIVA, "");
		defaultProperties.setProperty(StampaStopperProperty.GDOSHOP_DIR, "C:\\GDOshop");
		defaultProperties.setProperty(StampaStopperProperty.CARD_PREFIX, "");
		
		defaultProperties.setProperty(StampaStopperProperty.STAMPANTE, "");
		
		defaultProperties.setProperty(StampaStopperProperty.FONT_PROMO, "futura");
		
		defaultProperties.setProperty(StampaStopperProperty.ENABLE_GDOSHOP, "true");
		defaultProperties.setProperty(StampaStopperProperty.ENABLE_PROMO, "true");
		defaultProperties.setProperty(StampaStopperProperty.ENABLE_GASTRO, "true");		
		defaultProperties.setProperty(StampaStopperProperty.ENABLE_CARDS, "true");
		defaultProperties.setProperty(StampaStopperProperty.ENABLE_FRUTTA, "true");

		defaultProperties.setProperty(StampaStopperProperty.DEFAULT_PROMO_STYLE, "");
		defaultProperties.setProperty(StampaStopperProperty.DEFAULT_FRUTTA_STYLE, "");		
		defaultProperties.setProperty(StampaStopperProperty.DEFAULT_GASTRO_STYLE, "");
		
		defaultProperties.setProperty(StampaStopperProperty.MYSQL_SERVER, "");
		defaultProperties.setProperty(StampaStopperProperty.MYSQL_PORT, "");
		defaultProperties.setProperty(StampaStopperProperty.MYSQL_DB, "");
		defaultProperties.setProperty(StampaStopperProperty.MYSQL_USER, "");
		defaultProperties.setProperty(StampaStopperProperty.MYSQL_PWD, "");
		
		defaultProperties.setProperty(StampaStopperProperty.MYSQL_PWD, "");
		
		//defaultProperties.setProperty(StampaStopperProperty., "");
	}

	static private String defaultCfgFilename = "stampastopper.cfg";

	private StampaStopperProperties(File propertiesFile)
	{
		super(propertiesFile, defaultProperties);
	}

	private StampaStopperProperties()
	{
		super(new File(defaultCfgFilename), defaultProperties);
	}

}
