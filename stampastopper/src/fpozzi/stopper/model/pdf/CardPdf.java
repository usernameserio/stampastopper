package fpozzi.stopper.model.pdf;

import java.util.List;

import fpozzi.gdoshop.model.articolo.Codice;
import fpozzi.gdoshop.model.articolo.CodiceEan;
import fpozzi.gdoshop.model.articolo.Quantita;
import fpozzi.stopper.model.Card;
import fpozzi.stopper.model.Prezzo;
import fpozzi.stopper.model.pdf.cell.CardPdfCellFactory;

public class CardPdf extends PdfStopper<Card>
{
	
	static public final PdfStopperFactory factory = new PdfStopperFactory()
	{
		final CodiceEan defaultCodiceEan = new CodiceEan("0000000000000");
		
		@Override
		public PdfStopper<?> makeDefaultPdfStopper()
		{
			Card defaultCard = new Card();
			defaultCard.setTag("NUOVO");
			defaultCard.setCodiceEan(defaultCodiceEan);
			return new CardPdf(defaultCard);
		}

	};

	public CardPdf(Card stopper)
	{
		super(stopper, CardPdfCellFactory.instance);
	}

	public void acceptEditor(PdfStopperEditor editor)
	{
		editor.edit(this);
	}
	
	@Override
	public String getAbbr()
	{
		return "CARD";
	}

	@Override
	public Codice getCodice()
	{
		return getStopper().getCodiceEan();
	}

	@Override
	public String getDescrizione()
	{
		return getStopper().getDescrizione();
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
	public boolean equals(Object otherObject)
	{
		if (!(otherObject instanceof CardPdf))
			return false;
		
		return getStopper().equals(((CardPdf)otherObject).getStopper());		
	}

	@Override
	public Object clone()
	{
		return new CardPdf((Card) getStopper().clone());
	}
}
