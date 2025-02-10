package fpozzi.stopper.controller;

import fpozzi.stopper.StampaStopperDAO;
import fpozzi.stopper.model.pdf.CardPdf;
import fpozzi.stopper.view.CardEditorView;
import fpozzi.stopper.view.CardEditorView.CardEditorObserver;

public class CardEditorController extends StopperEditorController<CardPdf, CardEditorView> implements CardEditorObserver
{
	public CardEditorController(CardEditorView view)
	{
		super(view);		
		view.setObserver(this);
	}
	
	@Override
	public void searchCode(String ean)
	{
		String customerName = null;
			try
			{
				customerName = StampaStopperDAO.getInstance().getCardDAO().getCustomerName(ean);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		getView().notifySearchResult(customerName);		
	}
	

}
