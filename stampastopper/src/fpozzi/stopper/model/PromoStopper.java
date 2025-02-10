package fpozzi.stopper.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fpozzi.gdoshop.model.offerta.PeriodoOfferta;
import fpozzi.stopper.serialization.XMLNames;
import fpozzi.utils.Utils;
import fpozzi.utils.date.DateUtils;

public class PromoStopper extends ArticoloStopper
{

	private PrezzoPromo prezzoPromo;
	private PeriodoOfferta periodo;
	private boolean conCard;
	private Integer pezziMax;
	private boolean facoltativo;
	private boolean prezzoPerUMNascosto;

	public PromoStopper()
	{
		super();
		this.prezzoPromo = null;
		this.periodo = null;
		this.conCard = false;
		this.pezziMax = null;
		this.facoltativo = false;
		this.prezzoPerUMNascosto = false;
	}

	public PrezzoPromo getPrezzoPromo()
	{
		return prezzoPromo;
	}
	
	public PeriodoOfferta getPeriodo()
	{
		return periodo;
	}

	public boolean isConCard()
	{
		return conCard;
	}

	public Integer getPezziMax()
	{
		return pezziMax;
	}
	
	public boolean isFacoltativo()
	{
		return facoltativo;
	}
	
	public boolean isPrezzoPerUMNascosto()
	{
		return prezzoPerUMNascosto;
	}
	
	public void setPrezzoPromo(PrezzoPromo prezzoPromo)
	{
		this.prezzoPromo = prezzoPromo;
	}

	public void setPeriodo(PeriodoOfferta periodo)
	{
		this.periodo = periodo;
	}

	public void setConCard(boolean conCard)
	{
		this.conCard = conCard;
	}

	public void setPezziMax(Integer pezziMax)
	{
		this.pezziMax = pezziMax;
	}
	
	public void setPrezzoPerUMNascosto(boolean prezzoPerUMNascosto)
	{
		this.prezzoPerUMNascosto = prezzoPerUMNascosto;
	}
	
	public void setFacoltativo(boolean facoltativo)
	{
		this.facoltativo = facoltativo;
	}

	@Override
	protected Element makeStopperElement(Document doc)
	{
		return doc.createElement(XMLNames.Elements.stopperpromo);
	}

	@Override
	public Element makeXMLElement(Document doc)
	{
		Element element, stopperElement = super.makeXMLElement(doc);

		element = doc.createElement(XMLNames.Elements.prezzoofferta);
		element.appendChild(doc.createTextNode(prezzoPromo.getValue() + ""));
		stopperElement.appendChild(element);

		if (pezziMax!=null &&  pezziMax> 0)
		{
			element = doc.createElement(XMLNames.Elements.maxpezzi);
			element.appendChild(doc.createTextNode(pezziMax + ""));
			stopperElement.appendChild(element);
		}

		if (conCard)
		{
			element = doc.createElement(XMLNames.Elements.concard);
			stopperElement.appendChild(element);
		}
		
		if (prezzoPerUMNascosto)
		{
			element = doc.createElement(XMLNames.Elements.prezzoPerUMNascosto);
			stopperElement.appendChild(element);
		}
		
		if (facoltativo)
		{
			element = doc.createElement(XMLNames.Elements.facoltativo);
			stopperElement.appendChild(element);
		}

		if (periodo != null)
		{
			element = doc.createElement(XMLNames.Elements.periodo);
			element.setAttribute(XMLNames.Attributes.inizio, DateUtils.italianDateFormat.format(periodo.getInizio()));
			element.setAttribute(XMLNames.Attributes.fine, DateUtils.italianDateFormat.format(periodo.getFine()));
			stopperElement.appendChild(element);
		}

		return stopperElement;
	}

	@Override
	public boolean equals(Object otherObject)
	{
		if (otherObject instanceof PromoStopper)
		{
			PromoStopper otherStopper = (PromoStopper) otherObject;
			if (!super.equals(otherStopper))
				return false;
			if (!Utils.equalsWithNulls(prezzoPromo, otherStopper.prezzoPromo))
				return false;
			if (!Utils.equalsWithNulls(periodo, otherStopper.periodo))
				return false;
			if (conCard != otherStopper.conCard)
				return false;
			if (facoltativo != otherStopper.facoltativo)
				return false;
			if (prezzoPerUMNascosto != otherStopper.prezzoPerUMNascosto)
				return false;
			if (!Utils.equalsWithNulls(pezziMax, otherStopper.pezziMax))
				return false;
			
			return true;
		}
		return false;
	}

	@Override
	public Object clone()
	{
		PromoStopper clone = new PromoStopper();
		clone.setCodiceInterno(getCodiceInterno());
		clone.getRigheDescrizione().addAll(getRigheDescrizione());
		clone.setPrezzo(getPrezzo());
		clone.getQuantita().addAll(getQuantita());
		clone.setPrezzoPromo(prezzoPromo);
		clone.setPrezzoPerUMNascosto(prezzoPerUMNascosto);
		clone.setConCard(conCard);
		clone.setFacoltativo(facoltativo);
		clone.setPeriodo(periodo);
		clone.setPezziMax(pezziMax);
		return clone; 
	}
}
