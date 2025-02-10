package fpozzi.stopper.model.pdf;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fpozzi.gdoshop.model.articolo.Codice;
import fpozzi.gdoshop.model.articolo.Quantita;
import fpozzi.stopper.model.Prezzo;
import fpozzi.stopper.model.PromoStopper;
import fpozzi.stopper.model.pdf.cell.UnoPiuUnoPdfCellFactory;

public class UnoPiuUnoPdfStopper extends PdfPromoStopper
{

	public UnoPiuUnoPdfStopper(PromoStopper stopper)
	{
		super(stopper, UnoPiuUnoPdfCellFactory.instance);
	}
	
	public Element makeXMLElement(Document doc)
	{
		Element stopperElement = super.makeXMLElement(doc);
		
		stopperElement.setAttribute("tipo", "unopiuuno");
		
		return stopperElement;
	}
	
	@Override
	public String getAbbr()
	{
		return  "1+1" + (getStopper().isFacoltativo() ? "*" : "");
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
		if (!(otherObject instanceof UnoPiuUnoPdfStopper))
			return false;
		
		return getStopper().equals(((UnoPiuUnoPdfStopper)otherObject).getStopper());		
	}
	
	@Override
	public PdfStopperStyle convertStyle(PdfStopperStyle style) throws UnsupportedStyleException
	{
		if (style == PdfStopperStyle.CARTA_SPECIALE_SCONTO || style == PdfStopperStyle.CARTA_SPECIALE_OFFERTA)
			return PdfStopperStyle.CARTA_SPECIALE_UNOPIUUNO;
		return super.convertStyle(style);
	}
	
	@Override
	public Object clone()
	{
		return new UnoPiuUnoPdfStopper((PromoStopper) getStopper().clone());
	}
}
