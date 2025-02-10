package fpozzi.stopper.dao;

import java.util.List;

import fpozzi.gdoshop.model.articolo.CodiceEan;
import fpozzi.gdoshop.model.articolo.CodiceInterno;
import fpozzi.gdoshop.model.offerta.PeriodoOfferta;
import fpozzi.stopper.model.pdf.PdfPromoStopper;

public interface PromoDAO 
{	
	public List<PdfPromoStopper> getAll(PeriodoOfferta periodo) throws Exception;		
	public List<PdfPromoStopper> getAll(CodiceInterno codiceArticolo) throws Exception;		
	public List<PdfPromoStopper> getAll(CodiceEan codiceEan) throws Exception;		
	public PeriodoOfferta[] getAllPeriodi() throws Exception;
}
