package fpozzi.stopper.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fpozzi.stopper.serialization.XMLNames;
import fpozzi.utils.Utils;

public class GastronomiaStopper extends ArticoloStopper
{

	private String ingredienti;
	private String note;

	public GastronomiaStopper()
	{
		super();
		ingredienti = null;
		note = null;
	}

	public String getIngredienti()
	{
		return ingredienti;
	}

	public void setIngredienti(String ingredienti)
	{
		this.ingredienti = ingredienti;
	}

	public String getNote()
	{
		return note;
	}

	public void setNote(String note)
	{
		this.note = note;
	}

	@Override
	protected Element makeStopperElement(Document doc)
	{
		return doc.createElement(XMLNames.Elements.stoppergastronomia);
	}

	@Override
	public Element makeXMLElement(Document doc)
	{
		Element stopperElement = super.makeXMLElement(doc);

		if (ingredienti != null && !ingredienti.isEmpty())
		{
			Element elencoIngredientiElement = doc.createElement(XMLNames.Elements.elencoIngredienti);
			elencoIngredientiElement.appendChild(doc.createTextNode(ingredienti));
			stopperElement.appendChild(elencoIngredientiElement);
		}
		
		if (note != null && !note.isEmpty())
		{
			Element noteElement = doc.createElement(XMLNames.Elements.note);
			noteElement.appendChild(doc.createTextNode(note));
			stopperElement.appendChild(noteElement);
		}

		return stopperElement;
	}

	@Override
	public boolean equals(Object otherObject)
	{
		if (otherObject instanceof GastronomiaStopper)
		{
			GastronomiaStopper otherStopper = (GastronomiaStopper) otherObject;
			if (!super.equals(otherStopper))
				return false;
			
			if (!Utils.equalsWithNulls(ingredienti, otherStopper.ingredienti))
				return false;
			
			if (!Utils.equalsWithNulls(note, otherStopper.note))
				return false;

			return true;
		}
		return false;
	}

	@Override
	public Object clone()
	{
		GastronomiaStopper clone = new GastronomiaStopper();
		clone.setCodiceInterno(getCodiceInterno());
		clone.getRigheDescrizione().addAll(getRigheDescrizione());
		clone.setPrezzo(getPrezzo());
		clone.getQuantita().addAll(getQuantita());
		clone.setIngredienti(ingredienti);
		clone.setNote(note);
		return clone;
	}
}