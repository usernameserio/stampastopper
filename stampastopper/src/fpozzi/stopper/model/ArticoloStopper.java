package fpozzi.stopper.model;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fpozzi.gdoshop.model.articolo.Articolo;
import fpozzi.gdoshop.model.articolo.Codice;
import fpozzi.gdoshop.model.articolo.CodiceInterno;
import fpozzi.gdoshop.model.articolo.Quantita;
import fpozzi.stopper.StampaStopperDAO;
import fpozzi.stopper.serialization.XMLNames;
import fpozzi.utils.Utils;

public abstract class ArticoloStopper extends Stopper
{
	public final static int maxRigheDescrizione = 5;
	public final static int defaultLunghezzaMaxRigaDescrizione = 22;
	public final static int maxQta = 4;

	private final List<String> righeDescrizione;
	private int lunghezzaMaxRigaDescrizione = defaultLunghezzaMaxRigaDescrizione;
	private Articolo articolo;
	private CodiceInterno codiceInterno;
	private Prezzo prezzo;
	private final List<Quantita> quantita;

	public ArticoloStopper()
	{
		super();
		this.righeDescrizione = new ArrayList<String>(maxRigheDescrizione);
		this.quantita = new ArrayList<Quantita>(maxQta);
		this.prezzo = null;
		this.codiceInterno = null;
		this.articolo = null;
	}

	public Prezzo getPrezzo()
	{
		return prezzo;
	}

	public CodiceInterno getCodiceInterno()
	{
		return codiceInterno;
	}

	public Codice getCodice()
	{
		return codiceInterno;
	}

	public List<String> getRigheDescrizione()
	{
		return righeDescrizione;
	}

	public void setDescrizione(String descrizione)
	{
		righeDescrizione.clear();

		int puntoDiTaglio;
		while (!descrizione.isEmpty())
		{
			puntoDiTaglio = descrizione.length();
			if (puntoDiTaglio > lunghezzaMaxRigaDescrizione)
			{
				puntoDiTaglio = lunghezzaMaxRigaDescrizione;
				while (puntoDiTaglio > 0 && descrizione.charAt(puntoDiTaglio) != ' ')
					puntoDiTaglio--;
				if (puntoDiTaglio == 0)
					puntoDiTaglio = lunghezzaMaxRigaDescrizione;
			}
			String riga = descrizione.substring(0, puntoDiTaglio).trim();
			descrizione = descrizione.substring(puntoDiTaglio);
			righeDescrizione.add(riga);
		}
	}

	public String getDescrizione()
	{
		String descrizione = "";
		for (String riga : righeDescrizione)
			descrizione += riga + " ";
		return descrizione.trim();
	}

	public List<Quantita> getQuantita()
	{
		return quantita;
	}

	public void setCodiceInterno(CodiceInterno codiceInterno)
	{
		this.codiceInterno = codiceInterno;
	}

	public void setPrezzo(Prezzo prezzo)
	{
		this.prezzo = prezzo;
	}

	public synchronized Articolo getArticolo()
	{
		if (articolo == null)
		{
			try
			{
				articolo = StampaStopperDAO.getInstance().getArticoloDAO().getArticoloByCodiceInterno(codiceInterno);
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return articolo;
	}

	public void setArticolo(Articolo articolo)
	{
		this.articolo = articolo;
	}

	public Element makeXMLElement(Document doc)
	{

		Element stopperElement = makeStopperElement(doc);

		// ELEMENT: CODICE

		if (codiceInterno != null)
		{
			Element codiceElement = doc.createElement(XMLNames.Elements.codice);
			codiceElement.appendChild(doc.createTextNode(codiceInterno.valore));
			stopperElement.appendChild(codiceElement);
		}

		// ELEMENT: DESCRIZIONE

		Element descrizioneElement = doc.createElement(XMLNames.Elements.descrizione);

		for (String riga : righeDescrizione)
		{
			Element rigaElement = doc.createElement(XMLNames.Elements.riga);
			rigaElement.appendChild(doc.createTextNode(riga));
			descrizioneElement.appendChild(rigaElement);
		}

		stopperElement.appendChild(descrizioneElement);

		// ELEMENT: GRAMMATURA

		for (Quantita gram : quantita)
		{
			Element grammaturaElement = doc.createElement(XMLNames.Elements.quantita);

			grammaturaElement.setAttribute(XMLNames.Attributes.udm, gram.getUnitaMisura().toString());

			if (gram.getValore() != null && gram.getValore() > 0)
				grammaturaElement.setAttribute(XMLNames.Attributes.valore, gram.getValore() + "");

			if (gram.getMoltiplicatore() != null && gram.getMoltiplicatore() > 1)

				grammaturaElement.setAttribute(XMLNames.Attributes.mult, gram.getMoltiplicatore() + "");

			stopperElement.appendChild(grammaturaElement);
		}

		if (getPrezzo() != null && getPrezzo().getValue() > 0)
		{
			Element prezzoElement = doc.createElement(XMLNames.Elements.prezzo);
			prezzoElement.appendChild(doc.createTextNode(getPrezzo().getValue() + ""));
			stopperElement.appendChild(prezzoElement);
		}

		return stopperElement;

	}

	protected abstract Element makeStopperElement(Document doc);

	@Override
	public boolean equals(Object otherObject)
	{
		if (otherObject instanceof ArticoloStopper)
		{
			ArticoloStopper otherStopper = (ArticoloStopper) otherObject;
			if (!Utils.equalsWithNulls(codiceInterno, otherStopper.codiceInterno))
				return false;
			if (!Utils.equalsWithNulls(prezzo, otherStopper.prezzo))
				return false;
			if (!righeDescrizione.equals(otherStopper.righeDescrizione))
				return false;
			if (!quantita.equals(otherStopper.quantita))
				return false;

			return true;
		}
		return false;
	}

	public abstract Object clone();
}
