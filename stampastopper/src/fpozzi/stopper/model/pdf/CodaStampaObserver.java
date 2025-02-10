package fpozzi.stopper.model.pdf;

public interface CodaStampaObserver
{

	public void requestAdded(CodaStampa codaStampa, PdfStopperRequest request);

	public void requestRemoved(CodaStampa codaStampa, PdfStopperRequest request);

	public void requestMoved(CodaStampa codaStampa, PdfStopperRequest request, PdfStopperStyle oldStyle, PdfStopperStyle newStyle);
	
	public void requestChanged(CodaStampa codaStampa, PdfStopperRequest request);
	
	public void stopperCountChanged(CodaStampa codaStampa, PdfStopperStyle style, int oldCount, int newCount);
	
}
