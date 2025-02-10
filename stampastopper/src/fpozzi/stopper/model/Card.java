package fpozzi.stopper.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fpozzi.gdoshop.model.articolo.CodiceEan;
import fpozzi.stopper.serialization.XMLNames;
import fpozzi.utils.Utils;

public class Card extends Stopper
{
	
	private String tag;
	private CodiceEan codiceEan;

	public Card()
	{
		super();
		this.tag = null;
		this.codiceEan = null;
	}

	public String getTag()
	{
		return tag;
	}

	public void setTag(String tag)
	{
		this.tag = tag;
	}

	public CodiceEan getCodiceEan()
	{
		return codiceEan;
	}

	public void setCodiceEan(CodiceEan codiceEan)
	{
		this.codiceEan = codiceEan;
	}

	public String getDescrizione()
	{
		return  tag;
	}

	@Override
	public Element makeXMLElement(Document doc)
	{
		Element cardElement = doc.createElement(XMLNames.Elements.card);
		
		if (tag != null)
		{
			Element tagElement = doc.createElement(XMLNames.Elements.tag);
			tagElement.appendChild(doc.createTextNode(tag));
			cardElement.appendChild(tagElement);
		}
		
		if (codiceEan != null)
		{
			Element eanElement = doc.createElement(XMLNames.Elements.codiceEan);
			eanElement.appendChild(doc.createTextNode(codiceEan.valore));
			cardElement.appendChild(eanElement);
		}
		
		return cardElement;
	}

	
	@Override
	public boolean equals(Object otherObject)
	{
		if (otherObject instanceof Card)
		{
			Card otherCard = (Card) otherObject;
			if (!Utils.equalsWithNulls(tag, otherCard.tag))
				return false;
			if (!Utils.equalsWithNulls(codiceEan, otherCard.codiceEan))
				return false;
			
			return true;
		}
		return false;
	}
	
	@Override
	public Object clone()
	{
		Card clone = new Card();
		clone.tag = tag;
		clone.codiceEan = codiceEan;		
		return clone; 
	}

}
