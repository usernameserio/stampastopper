package fpozzi.stopper.model.pdf;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fpozzi.gdoshop.model.articolo.Codice;
import fpozzi.gdoshop.model.articolo.Quantita;
import fpozzi.stopper.model.Prezzo;
import fpozzi.stopper.model.PrezzoPromo;
import fpozzi.stopper.model.PromoStopper;
import fpozzi.stopper.model.pdf.cell.TaglioPrezzoPdfCellFactory;

public class TaglioPrezzoPdfStopper extends PdfPromoStopper
{

	static public final PdfStopperFactory factory = new PdfStopperFactory()
	{

		@Override
		public PdfStopper<?> makeDefaultPdfStopper()
		{
			PromoStopper defaultPromostopper = new PromoStopper();
			defaultPromostopper.getRigheDescrizione().add("Nuovo");
			defaultPromostopper.setPrezzoPromo(new PrezzoPromo(0.01));
			return new TaglioPrezzoPdfStopper(defaultPromostopper);
		}

	};

	public TaglioPrezzoPdfStopper(PromoStopper stopper)
	{
		super(stopper, TaglioPrezzoPdfCellFactory.instance);
	}

	@Override
	public Element makeXMLElement(Document doc)
	{
		Element stopperElement = super.makeXMLElement(doc);

		stopperElement.setAttribute("tipo", "taglioprezzo");

		return stopperElement;
	}

	@Override
	public String getAbbr()
	{
		return "TP" + (getStopper().isFacoltativo() ? "*" : "");
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
		if (!(otherObject instanceof TaglioPrezzoPdfStopper))
			return false;

		return getStopper().equals(((TaglioPrezzoPdfStopper) otherObject).getStopper());
	}

	@Override
	public PdfStopperStyle convertStyle(PdfStopperStyle style) throws UnsupportedStyleException
	{
		if (style == PdfStopperStyle.CARTA_SPECIALE_SCONTO || style == PdfStopperStyle.CARTA_SPECIALE_UNOPIUUNO)
			return PdfStopperStyle.CARTA_SPECIALE_OFFERTA;
		return super.convertStyle(style);
	}

	@Override
	public Object clone()
	{
		return new TaglioPrezzoPdfStopper((PromoStopper) getStopper().clone());
	}

}
