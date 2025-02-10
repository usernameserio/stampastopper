package fpozzi.gdoshop.model.articolo;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fpozzi.stopper.serialization.XMLNames;

public class ListaIngredienti implements Cloneable
{

	private boolean suLibroIngredienti;
	private final List<Ingrediente> ingredienti;

	public ListaIngredienti()
	{
		super();
		suLibroIngredienti = true;
		ingredienti = new LinkedList<Ingrediente>();
	}

	public void setSuLibroIngredienti(boolean suLibroIngredienti)
	{
		this.suLibroIngredienti = suLibroIngredienti;
	}

	public boolean isSuLibroIngredienti()
	{
		return suLibroIngredienti;
	}

	public List<Ingrediente> getIngredienti()
	{
		return ingredienti;
	}

	public Element makeXMLElement(Document doc)
	{
		Element listaElement = doc.createElement(XMLNames.Elements.elencoIngredienti);
		
		listaElement.setAttribute(XMLNames.Attributes.sulibro, suLibroIngredienti+"");
		
		for (Ingrediente ingrediente : ingredienti)
		{
			listaElement.appendChild(ingrediente.makeXMLElement(doc));
		}
		
		return listaElement;
	}

	@Override
	public Object clone()
	{
		ListaIngredienti clone = new ListaIngredienti();
		clone.getIngredienti().addAll(ingredienti);
		clone.setSuLibroIngredienti(suLibroIngredienti);
		return clone;
	}
	
	@Override
	public boolean equals(Object otherObject)
	{
		if (otherObject instanceof ListaIngredienti)
		{
			ListaIngredienti otherLista = (ListaIngredienti) otherObject;
			if (suLibroIngredienti != otherLista.suLibroIngredienti)
				return false;
			if (!ingredienti.equals(otherLista.ingredienti))
				return false;

			return true;
		}
		return false;
	}

}
