package fpozzi.stopper.controller;

import java.util.List;

import fpozzi.gdoshop.model.articolo.CodiceEan;
import fpozzi.gdoshop.model.articolo.CodiceInterno;
import fpozzi.stopper.StampaStopperDAO;
import fpozzi.stopper.model.pdf.PdfPromoStopper;
import fpozzi.stopper.view.PromoStopperEditorView;
import fpozzi.stopper.view.PromoStopperEditorView.PromoEditorObserver;

public class PromoEditorController extends StopperEditorController<PdfPromoStopper, PromoStopperEditorView> implements PromoEditorObserver
{
	public PromoEditorController(PromoStopperEditorView view)
	{
		super(view);
		view.setObserver(this);
	}

	@Override
	public void searchCode(final String codice)
	{
		try
		{
			List<PdfPromoStopper> stoppers; 
			stoppers = StampaStopperDAO.getInstance().getPromoDAO().getAll(new CodiceInterno(codice));
			if (stoppers.isEmpty())
				stoppers = StampaStopperDAO.getInstance().getPromoDAO().getAll(new CodiceEan(codice));
			view.notifySearchResult(stoppers);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
