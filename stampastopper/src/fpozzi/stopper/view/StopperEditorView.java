package fpozzi.stopper.view;

import fpozzi.stopper.model.pdf.PdfStopper;

public interface StopperEditorView<T extends PdfStopper<?>>
{
	public void setPdfStopper(T pdfStopper);
	public T getPdfStopper();
}
