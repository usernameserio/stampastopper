package fpozzi.stopper.dao;

import java.io.File;

import fpozzi.stopper.StampaStopperLogger;
import fpozzi.stopper.StampaStopperProperties;
import fpozzi.stopper.StampaStopperProperties.StampaStopperProperty;

public abstract class DbfDAOLoader<DAO>
{

	public DAO DAOfromProperties(StampaStopperProperties properties)
	{
		DAO dao = null;

		String gdoshopDirPath = properties.getProperty(StampaStopperProperty.GDOSHOP_DIR, "");
		if (!gdoshopDirPath.isEmpty())
		{
			File gdoshopDir = new File(gdoshopDirPath);
			if (gdoshopDir.exists() && gdoshopDir.isDirectory())
			{
				try
				{
					dao = makeDAO(gdoshopDir);
				}
				catch (Exception e)
				{
					StampaStopperLogger.get().severe("Errore acquisizione dati. C'è un'altra copia del programma in esecuzione? Chiuderla e riprovare.");
					e.printStackTrace();
				}
			}
		}
		return dao;
	}

	public abstract DAO makeDAO(File gdoshopDir) throws Exception;

}
