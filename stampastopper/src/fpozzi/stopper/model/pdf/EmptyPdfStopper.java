package fpozzi.stopper.model.pdf;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fpozzi.gdoshop.model.articolo.Codice;
import fpozzi.gdoshop.model.articolo.Quantita;
import fpozzi.stopper.model.Prezzo;
import fpozzi.stopper.model.Stopper;
import fpozzi.stopper.model.pdf.cell.EmptyPdfCellFactory;
import fpozzi.stopper.serialization.XMLNames;

public class EmptyPdfStopper extends PdfStopper<Stopper>
{

	static public final EmptyPdfStopper instance = new EmptyPdfStopper();

	static public final PdfStopperFactory factory = new PdfStopperFactory()
	{

		@Override
		public PdfStopper<?> makeDefaultPdfStopper()
		{
			return instance;
		}

	};

	private EmptyPdfStopper()
	{
		super(null, EmptyPdfCellFactory.instance);
	}

	@Override
	public Element makeXMLElement(Document doc)
	{
		return doc.createElement(XMLNames.Elements.stoppervuoto);
	}

	public void acceptEditor(PdfStopperEditor editor)
	{
		editor.edit(this);
	}

	@Override
	public boolean equals(Object otherObject)
	{
		return (otherObject instanceof EmptyPdfStopper);
	}

	@Override
	public PdfStopperStyle convertStyle(PdfStopperStyle style) throws UnsupportedStyleException
	{
		return style;
	}

	@Override
	public Object clone()
	{
		return instance;
	}

	@Override
	public Codice getCodice()
	{
		return null;
	}

	@Override
	public String getDescrizione()
	{
		return null;
	}

	@Override
	public Prezzo getPrezzo()
	{
		return null;
	}

	@Override
	public List<Quantita> getQuantita()
	{
		return null;
	}

	@Override
	public String getAbbr()
	{
		return "Ø";
	}

}
