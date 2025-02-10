package fpozzi.stopper.model.pdf;

import java.util.List;

import fpozzi.gdoshop.model.articolo.Codice;
import fpozzi.gdoshop.model.articolo.Quantita;
import fpozzi.gdoshop.model.articolo.UnitaMisura;
import fpozzi.stopper.model.GastronomiaStopper;
import fpozzi.stopper.model.Prezzo;
import fpozzi.stopper.model.pdf.cell.GastroPdfCellFactory;

public class GastroPdfStopper extends PdfStopper<GastronomiaStopper>
{
	
	static public final PdfStopperFactory factory = new PdfStopperFactory()
	{

		@Override
		public PdfStopper<?> makeDefaultPdfStopper()
		{
			GastronomiaStopper defaultPromostopper = new GastronomiaStopper();
			defaultPromostopper.setPrezzo(new Prezzo(0.01));
			defaultPromostopper.getRigheDescrizione().add("NUOVO");
			defaultPromostopper.getQuantita().add(new Quantita(UnitaMisura.KG));
			return new GastroPdfStopper(defaultPromostopper);
		}

	};

	public GastroPdfStopper(GastronomiaStopper stopper)
	{
		super(stopper, GastroPdfCellFactory.instance);
	}
	
	@Override
	public String getAbbr()
	{
		return "GAST";
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
		return getStopper().getPrezzo();
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
		
		return getStopper().equals(((GastroPdfStopper)otherObject).getStopper());		
	}

	@Override
	public Object clone()
	{
		return new GastroPdfStopper((GastronomiaStopper) getStopper().clone());
	}
}
