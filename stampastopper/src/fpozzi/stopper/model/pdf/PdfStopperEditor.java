package fpozzi.stopper.model.pdf;


public interface PdfStopperEditor
{
	public void edit(PrezzacciPdfStopper stopper);
	
	public void edit(ScontoPercentualePdfStopper stopper);
	
	public void edit(TaglioPrezzoPdfStopper stopper);
	
	public void edit(UnoPiuUnoPdfStopper stopper);
	
	public void edit(FruttaPdfStopper stopper);
	
	public void edit(EmptyPdfStopper stoppero);

	public void edit(GastroPdfStopper gastronomiaPdfStopper);
	
	public void edit(CardPdf card);

}
