package fpozzi.stopper;

import fpozzi.stopper.dao.ArticoloDAO;
import fpozzi.stopper.dao.CardDAO;
import fpozzi.stopper.dao.PromoDAO;

public class StampaStopperDAO
{
	static private StampaStopperDAO instance = null;

	static public void Init(ArticoloDAO articoloDAO, 
			PromoDAO promoDAO, CardDAO cardDAO) throws Exception
	{
		if (instance!=null)
				throw new Exception("singleton DAO già istanziato");
		instance = new StampaStopperDAO(articoloDAO, promoDAO, cardDAO);
	}

	public static StampaStopperDAO getInstance()
	{
		return instance;
	}

	private final ArticoloDAO articoloDAO;
	private final PromoDAO promoDAO;
	private final CardDAO cardDAO;

	private StampaStopperDAO(ArticoloDAO articoloDAO, PromoDAO promoDAO, CardDAO cardDAO)
	{
		super();
		this.articoloDAO = articoloDAO;
		this.promoDAO = promoDAO;
		this.cardDAO = cardDAO;
	}

	public ArticoloDAO getArticoloDAO()
	{
		return articoloDAO;
	}

	public PromoDAO getPromoDAO()
	{
		return promoDAO;
	}

	public CardDAO getCardDAO()
	{
		return cardDAO;
	}


}
