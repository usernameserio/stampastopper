package fpozzi.stopper.model.pdf;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import fpozzi.gdoshop.model.articolo.Codice;
import fpozzi.gdoshop.model.articolo.Quantita;
import fpozzi.stopper.model.Prezzo;
import fpozzi.stopper.model.PromoStopper;
import fpozzi.stopper.model.pdf.cell.ScontoPercentualePdfCellFactory;
import fpozzi.stopper.serialization.XMLNames;

public class ScontoPercentualePdfStopper extends PdfPromoStopper
{

	public ScontoPercentualePdfStopper(PromoStopper stopper)
	{
		super(stopper, ScontoPercentualePdfCellFactory.instance);
	}

	@Override
	public Element makeXMLElement(Document doc)
	{
		Element stopperElement = super.makeXMLElement(doc);

		stopperElement.setAttribute("tipo", "scontopercentuale");

		Element element = doc.createElement(XMLNames.Elements.percsconto);
		element.appendChild(doc.createTextNode(getStopper().getPrezzoPromo().getPercentualeSconto() + ""));

		Node childNode;

		for (int i = 0; (childNode = stopperElement.getChildNodes().item(i)) != null; i++)
			if (childNode.getNodeName().equals(XMLNames.Elements.prezzo))
			{
				stopperElement.insertBefore(element, stopperElement.getChildNodes().item(i + 1));
				return stopperElement;
			}

		return stopperElement;
	}
	
	@Override
	public String getAbbr()
	{
		return  "SC%" + (getStopper().isFacoltativo() ? "*" : "");
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
		if (!(otherObject instanceof ScontoPercentualePdfStopper))
			return false;

		return getStopper().equals(((ScontoPercentualePdfStopper) otherObject).getStopper());
	}

	@Override
	public PdfStopperStyle convertStyle(PdfStopperStyle style) throws UnsupportedStyleException
	{
		if (style == PdfStopperStyle.CARTA_SPECIALE_OFFERTA || style == PdfStopperStyle.CARTA_SPECIALE_UNOPIUUNO)
			return PdfStopperStyle.CARTA_SPECIALE_SCONTO;
		return super.convertStyle(style);
	}
	
	@Override
	public Object clone()
	{
		return new ScontoPercentualePdfStopper((PromoStopper) getStopper().clone());
	}
	
	

}
