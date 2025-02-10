package fpozzi.stopper.dao;

import java.io.File;
import java.util.TreeMap;

import org.xBaseJ.DBF;

import fpozzi.gdoshop.model.articolo.CodiceEan;
import fpozzi.gdoshop.model.articolo.CodiceInterno;

class EanDAO extends AbstractDbfDAO
{

	static private EanDAO instance = null;

	static public synchronized EanDAO getInstance(File dbDirectory) throws Exception
	{
		if (instance == null)
		{
			instance = new EanDAO(new File(dbDirectory, "\\Archivi9\\MAG007.DBF"));
		}
		return instance;
	}

	private TreeMap<CodiceEan, CodiceInterno> eanToCodiceMap;

	private EanDAO(File dbFile) throws Exception
	{
		super(dbFile);
	}

	@Override
	protected String getDbDescription()
	{
		return "ean_articoli";
	}

	@Override
	public synchronized void initDb() throws Exception
	{
		super.initDb();
		rebuildCache();
	}

	public void rebuildCache() throws Exception
	{
		eanToCodiceMap = new TreeMap<CodiceEan, CodiceInterno>();
		DBF db = getDb();
		db.startTop();
		for (int i = 1; i <= db.getRecordCount(); i++)
		{
			db.read();
			eanToCodiceMap.put(new CodiceEan(db.getField(2).get()), new CodiceInterno(db.getField(9).get()));
		}

	}

	public CodiceInterno getCodiceFromEAN(CodiceEan ean)
	{
		return eanToCodiceMap.get(ean);
	}
}