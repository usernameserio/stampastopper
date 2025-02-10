package fpozzi.stopper.model.pdf;

import fpozzi.stopper.model.pdf.PdfStopperRequest;

public interface PdfStopperRequestObserver
{

	public void stopperChanged(PdfStopperRequest request, PdfStopper<?> oldStopper, PdfStopper<?> newStopper);

	public void styleChanged(PdfStopperRequest request, PdfStopperStyle oldStyle, PdfStopperStyle newStyle);
	
	public void copiesChanged(PdfStopperRequest request, int oldCopies, int newCopies);
	
}