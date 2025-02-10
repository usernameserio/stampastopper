package fpozzi.gdoshop.model.articolo;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fpozzi.stopper.serialization.XMLNames;

public class Ingrediente implements Cloneable
{

	private final String descrizione;
	private final boolean allergene;
	private final Float percentuale;

	private final List<Ingrediente> ingredienti;

	public Ingrediente(String descrizione, boolean allergene, Float percentuale)
	{
		super();
		this.descrizione = descrizione;
		this.allergene = allergene;
		this.percentuale = percentuale;
		this.ingredienti = new LinkedList<Ingrediente>();
	}

	public Ingrediente(String descrizione, boolean allergene)
	{
		this(descrizione, allergene, null);
	}

	public Ingrediente(String descrizione)
	{
		this(descrizione, false, null);
	}

	public String getDescrizione()
	{
		return descrizione;
	}

	public boolean isAllergene()
	{
		return allergene;
	}

	public float getPercentuale()
	{
		return percentuale;
	}

	public List<Ingrediente> getIngredienti()
	{
		return ingredienti;
	}

	public Element makeXMLElement(Document doc)
	{
		Element ingredienteElement = doc.createElement(XMLNames.Elements.ingrediente);

		ingredienteElement.setAttribute(XMLNames.Attributes.descrizione, descrizione + "");

		if (allergene == true)
			ingredienteElement.setAttribute(XMLNames.Attributes.allergene, allergene + "");

		if (percentuale != null && percentuale > 0 && percentuale <= 100)
			ingredienteElement.setAttribute(XMLNames.Attributes.percentuale, percentuale + "");

		for (Ingrediente ingrediente : ingredienti)
			ingredienteElement.appendChild(ingrediente.makeXMLElement(doc));

		return ingredienteElement;
	}

	@Override
	public Object clone()
	{
		Ingrediente clone = new Ingrediente(descrizione, allergene, percentuale);
		for (Ingrediente ingrediente : ingredienti)
			clone.getIngredienti().add((Ingrediente) ingrediente.clone());
		return clone;
	}

	@Override
	public boolean equals(Object otherObject)
	{
		if (otherObject instanceof Ingrediente)
		{
			Ingrediente otherIngrediente = (Ingrediente) otherObject;
			return descrizione.equals(otherIngrediente.descrizione) && allergene == otherIngrediente.allergene
					&& percentuale == otherIngrediente.percentuale && ingredienti.equals(otherIngrediente.ingredienti);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return descrizione.hashCode()^(allergene ? 1:0);
	}
	
}
