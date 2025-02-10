package fpozzi.stopper.model.pdf;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fpozzi.gdoshop.model.articolo.Codice;
import fpozzi.gdoshop.model.articolo.Quantita;
import fpozzi.stopper.model.Prezzo;
import fpozzi.stopper.model.PromoStopper;
import fpozzi.stopper.model.pdf.cell.PrezzacciPdfCellFactory;

public class PrezzacciPdfStopper extends PdfPromoStopper
{

	public PrezzacciPdfStopper(PromoStopper stopper)
	{
		super(stopper,  PrezzacciPdfCellFactory.instance);
	}
	
	public Element makeXMLElement(Document doc)
	{
		Element stopperElement = super.makeXMLElement(doc);
		
		stopperElement.setAttribute("tipo", "sottocosto");
		
		return stopperElement;
	}
	
	@Override
	public String getAbbr()
	{
		return getStopper().isFacoltativo() ? "*" : "" +"SOTT";
	}

	@Override
	public Codice getCodice()
	{
		return getStopper().getCodice();
	}

	@Override
	public String getDescrizione()
	{
		return getStopper().getDescrizione();
	}

	@Override
	public Prezzo getPrezzo()
	{
		return getStopper().getPrezzoPromo();
	}

	@Override
	public List<Quantita> getQuantita()
	{
		return getStopper().getQuantita();
	}
	
	public void acceptEditor(PdfStopperEditor editor)
	{
		editor.edit(this);
	}
	
	@Override
	public boolean equals(Object otherObject)
	{
		if (!(otherObject instanceof PrezzacciPdfStopper))
			return false;
		
		return getStopper().equals(((PrezzacciPdfStopper)otherObject).getStopper());		
	}
	
	@Override
	public Object clone()
	{
		return new PrezzacciPdfStopper((PromoStopper) getStopper().clone());
	}
}
