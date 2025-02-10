package fpozzi.stopper.model.pdf;

import java.util.List;

import fpozzi.gdoshop.model.articolo.Codice;
import fpozzi.gdoshop.model.articolo.Quantita;
import fpozzi.gdoshop.model.articolo.UnitaMisura;
import fpozzi.stopper.model.FruttaStopper;
import fpozzi.stopper.model.Prezzo;
import fpozzi.stopper.model.FruttaStopper.Categoria;
import fpozzi.stopper.model.pdf.cell.FruttaPdfCellFactory;

public class FruttaPdfStopper extends PdfStopper<FruttaStopper>
{

	static public final PdfStopperFactory factory = new PdfStopperFactory()
	{

		@Override
		public PdfStopper<?> makeDefaultPdfStopper()
		{
			FruttaStopper defaultPromostopper = new FruttaStopper();
			defaultPromostopper.setPrezzo(new Prezzo(0.01));
			defaultPromostopper.getRigheDescrizione().add("NUOVO");
			defaultPromostopper.getQuantita().add(new Quantita(UnitaMisura.KG));
			defaultPromostopper.setCategoria(Categoria.I);
			defaultPromostopper.setOrigine("Italia");

			return new FruttaPdfStopper(defaultPromostopper);
		}

	};

	public FruttaPdfStopper(FruttaStopper stopper)
	{
		super(stopper, FruttaPdfCellFactory.instance);
	}

	public void acceptEditor(PdfStopperEditor editor)
	{
		editor.edit(this);
	}

	@Override
	public String getAbbr()
	{
		return "FRUT";
	}

	@Override
	public Codice getCodice()
	{
		return getStopper().getCodice();
	}

	@Override
	public String getDescrizione()
	{
		Integer tasto = getStopper().getTasto();
		return "[ " + (tasto == null ? " / " : tasto) + " ] " + getStopper().getDescrizione() + " - " +getStopper().getOrigine();
	}

	@Override
	public Prezzo getPrezzo()
	{
		return getStopper().getPrezzo();
	}

	@Override
	public List<Quantita> getQuantita()
	{
		return getStopper().getQuantita();
	}

	@Override
	public boolean equals(Object otherObject)
	{
		if (!(otherObject instanceof FruttaPdfStopper))
			return false;

		return getStopper().equals(((FruttaPdfStopper) otherObject).getStopper());
	}

	@Override
	public Object clone()
	{
		return new FruttaPdfStopper((FruttaStopper) getStopper().clone());
	}

}
