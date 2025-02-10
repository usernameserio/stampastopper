package fpozzi.stopper.view;

import java.io.File;
import java.util.List;

import fpozzi.gdoshop.model.offerta.PeriodoOfferta;
import fpozzi.stopper.model.pdf.PdfStopper;
import fpozzi.stopper.model.pdf.PdfStopperStyle;

public interface AddView
{

	public void setObserver(AddViewObserver observer);
	
	public void setDefaultStyle(Class<? extends PdfStopper<?>> stopperClass, PdfStopperStyle style);
	
	public interface AddViewObserver
	{
		public void makeNewRequest(Class<? extends PdfStopper<?>> stopperClass, PdfStopperStyle style);

		public List<PeriodoOfferta> getAllPeriodi();

		public void importRequests(PeriodoOfferta periodo);

		public List<File> getAllLettoreLaserFiles();

		public void importRequests(File file);

		public void setDefaultStyle(Class<? extends PdfStopper<?>> stopperClass, PdfStopperStyle style);
	}
	
}

