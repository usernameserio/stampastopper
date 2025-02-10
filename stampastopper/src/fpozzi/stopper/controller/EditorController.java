package fpozzi.stopper.controller;

import fpozzi.stopper.StampaStopperLogger;
import fpozzi.stopper.model.pdf.EmptyPdfStopper;
import fpozzi.stopper.model.pdf.FruttaPdfStopper;
import fpozzi.stopper.model.pdf.GastroPdfStopper;
import fpozzi.stopper.model.pdf.CardPdf;
import fpozzi.stopper.model.pdf.PdfStopper;
import fpozzi.stopper.model.pdf.PdfStopperEditor;
import fpozzi.stopper.model.pdf.PdfStopperRequest;
import fpozzi.stopper.model.pdf.PdfStopperStyle;
import fpozzi.stopper.model.pdf.PrezzacciPdfStopper;
import fpozzi.stopper.model.pdf.ScontoPercentualePdfStopper;
import fpozzi.stopper.model.pdf.TaglioPrezzoPdfStopper;
import fpozzi.stopper.model.pdf.UnoPiuUnoPdfStopper;
import fpozzi.stopper.model.pdf.UnsupportedStyleException;
import fpozzi.stopper.view.CodaStampaView.RequestSelectionObserver;
import fpozzi.stopper.view.EditorView;
import fpozzi.stopper.view.EditorView.EditorViewObserver;

public class EditorController implements RequestSelectionObserver, EditorViewObserver, PdfStopperEditor
{
	private final EditorView view;

	private PdfStopperRequest currentStopperRequest;

	private final CardEditorController cardEditorController;
	private final PromoEditorController promoEditorController;

	public EditorController(EditorView view)
	{
		this.view = view;
		view.attachObserver(this);

		cardEditorController = new CardEditorController(view.getCardEditorView());
		promoEditorController = new PromoEditorController(view.getPromoStopperEditorView());
		setCurrentStopperRequest(null);
	}

	public EditorView getView()
	{
		return view;
	}

	public PdfStopperRequest getCurrentStopperRequest()
	{
		return currentStopperRequest;
	}

	public final void setCurrentStopperRequest(PdfStopperRequest stopperRequest)
	{
		this.currentStopperRequest = stopperRequest;
		if (stopperRequest != null)
			stopperRequest.getPdfStopper().acceptEditor(this);
		view.setStopperRequest(stopperRequest);
	}

	@Override
	public void requestSelected(PdfStopperRequest selectedRequest)
	{
		setCurrentStopperRequest(selectedRequest);
	}

	@Override
	public void changeStyle(PdfStopperRequest request, PdfStopperStyle newStyle) throws UnsupportedStyleException
	{
		if (request.getStyle() != newStyle)
		{
			request.setStyle(newStyle);
			StampaStopperLogger.get().info("Stile di '" + request.getPdfStopper().getDescrizione() + "' modificato.");
		}
	}

	@Override
	public void changeStopper(PdfStopperRequest request, PdfStopper<?> newStopper) throws UnsupportedStyleException
	{
		if (!request.getPdfStopper().equals(newStopper))
		{
			request.setPdfStopper(newStopper);
			StampaStopperLogger.get().info("Stopper '" + request.getPdfStopper().getDescrizione() + "' modificato.");
		}
	}

	@Override
	public void changeCopies(PdfStopperRequest request, int newCopies)
	{
		if (request.getCopies() != newCopies)
			request.setCopies(newCopies);
	}

	@Override
	public void edit(PrezzacciPdfStopper stopper)
	{

	}

	@Override
	public void edit(ScontoPercentualePdfStopper stopper)
	{
		promoEditorController.setCurrentStopper(stopper);
	}

	@Override
	public void edit(TaglioPrezzoPdfStopper stopper)
	{
		promoEditorController.setCurrentStopper(stopper);
	}

	@Override
	public void edit(UnoPiuUnoPdfStopper stopper)
	{
		promoEditorController.setCurrentStopper(stopper);
	}

	@Override
	public void edit(FruttaPdfStopper stopper)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void edit(EmptyPdfStopper stoppero)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void edit(GastroPdfStopper gastronomiaPdfStopper)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void edit(CardPdf card)
	{
		cardEditorController.setCurrentStopper(card);
	}

}
