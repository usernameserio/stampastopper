package fpozzi.stopper.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fpozzi.stopper.serialization.XMLNames;
import fpozzi.utils.Utils;

public class FruttaStopper extends ArticoloStopper
{
	
	public enum Categoria { I("1ª"), II("2ª"), EXTRA("Extra");
		
		private final String alternativeText;
	
		Categoria(String alternativeText)
		{
			this.alternativeText = alternativeText;
		}
		
		public String getAlternativeText()
		{
			return alternativeText;
		}
	}

	private String origine, calibro, additivi;
	private Categoria categoria;
	private Integer tasto;

	public FruttaStopper()
	{
		super();
		this.calibro = null;
		this.origine = null;
		this.categoria = null;
		this.tasto = null;
		this.additivi = null;
	}
	
	public String getCalibro()
	{
		return calibro;
	}

	public String getOrigine()
	{
		return origine;
	}

	public Categoria getCategoria()
	{
		return categoria;
	}

	public Integer getTasto()
	{
		return tasto;
	}

	public void setCalibro(String calibro)
	{
		this.calibro = calibro;
	}

	public void setOrigine(String origine)
	{
		this.origine = origine;
	}

	public void setCategoria(Categoria categoria)
	{
		this.categoria = categoria;
	}

	public void setTasto(Integer tasto)
	{
		this.tasto = tasto;
	}

	public String getAdditivi()
	{
		return additivi;
	}

	public void setAdditivi(String elencoAdditivi)
	{
		this.additivi = elencoAdditivi;
	}

	@Override
	protected Element makeStopperElement(Document doc)
	{
		return doc.createElement(XMLNames.Elements.stopperfrutta);
	}

	@Override
	public Element makeXMLElement(Document doc)
	{
		Element stopperElement =  super.makeXMLElement(doc);
		
		if (calibro != null)
		{
			Element calibroElement = doc.createElement(XMLNames.Elements.calibro);
			calibroElement.appendChild(doc.createTextNode(calibro));
			stopperElement.appendChild(calibroElement);
		}
		
		
		if (origine != null && !origine.isEmpty())
		{
			Element origineElement = doc.createElement(XMLNames.Elements.origine);
			origineElement.appendChild(doc.createTextNode(origine));
			stopperElement.appendChild(origineElement);
		}

		if (categoria != null)
		{
			Element categoriaElement = doc.createElement(XMLNames.Elements.categoria);
			categoriaElement.appendChild(doc.createTextNode(categoria.toString()));
			stopperElement.appendChild(categoriaElement);
		}
		
		if (tasto != null)
		{
			Element tastoElement = doc.createElement(XMLNames.Elements.tasto);
			tastoElement.appendChild(doc.createTextNode(tasto.toString()));
			stopperElement.appendChild(tastoElement);
		}
		
		if (additivi != null)
		{
			Element elencoAdditiviElement = doc.createElement(XMLNames.Elements.additivi);
			elencoAdditiviElement.appendChild(doc.createTextNode(additivi));
			stopperElement.appendChild(elencoAdditiviElement);
		}
		
		return stopperElement;
	}

	@Override
	public boolean equals(Object otherObject)
	{
		if (otherObject instanceof FruttaStopper)
		{
			FruttaStopper otherStopper = (FruttaStopper) otherObject;
			if (!super.equals(otherStopper))
				return false;
			if (!Utils.equalsWithNulls(calibro, otherStopper.calibro))
				return false;
			if (!Utils.equalsWithNulls(origine, otherStopper.origine))
				return false;
			if (!Utils.equalsWithNulls(categoria, otherStopper.categoria))
				return false;
			if (!Utils.equalsWithNulls(tasto, otherStopper.tasto))
				return false;
			if (!Utils.equalsWithNulls(additivi, otherStopper.additivi))
				return false;
			
			return true;
		}
		return false;
	}
	
	@Override
	public Object clone()
	{
		FruttaStopper clone = new FruttaStopper();
		clone.setCodiceInterno(getCodiceInterno());
		clone.getRigheDescrizione().addAll(getRigheDescrizione());
		clone.setPrezzo(getPrezzo());
		clone.getQuantita().addAll(getQuantita());
		clone.setCalibro(calibro);
		clone.setCategoria(categoria);
		clone.setOrigine(origine);
		clone.setTasto(tasto);
		return clone; 
	}

}
