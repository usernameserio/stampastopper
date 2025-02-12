package fpozzi.stopper.view.swing.codastampa;

import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import fpozzi.gdoshop.model.articolo.Codice;
import fpozzi.stopper.model.Prezzo;
import fpozzi.stopper.model.PromoStopper;
import fpozzi.stopper.model.pdf.PdfStopperRequest;
import fpozzi.stopper.view.swing.codastampa.StyleCodaStampaTable.ColumnHeader;

class CodaStampaTableModel extends AbstractTableModel
{

	private static final long serialVersionUID = 1L;	

	final private List<PdfStopperRequest> requests;

	public CodaStampaTableModel(List<PdfStopperRequest> requests)
	{
		this.requests = requests;
	}

	public List<PdfStopperRequest> getRequests()
	{
		return requests;
	}

	@Override
	public int getRowCount()
	{
		return this.getRequests().size();
	}

	@Override
	public int getColumnCount()
	{
		return ColumnHeader.values().length;
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		return ColumnHeader.values()[columnIndex].getText();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		PdfStopperRequest row = getRequests().get(rowIndex);
		if (columnIndex == ColumnHeader.ANTEPRIMA.ordinal())
		{
			return null;
		}
		if (columnIndex == ColumnHeader.TIPO.ordinal())
		{
			return row.getPdfStopper().getAbbr();
		}
		if (columnIndex == ColumnHeader.CODICE.ordinal())
		{
			if (row.getPdfStopper().getStopper() == null)
				return null;
			return row.getPdfStopper().getCodice();
		}
		if (columnIndex == ColumnHeader.DESCRIZIONE.ordinal())
		{
			if (row.getPdfStopper().getStopper() == null)
				return (" ");
			return row.getPdfStopper().getDescrizione();
		}
		if (columnIndex == ColumnHeader.DATA_INIZIO.ordinal() || columnIndex == ColumnHeader.DATA_FINE.ordinal())
		{
			if (row.getPdfStopper().getStopper() == null || !(row.getPdfStopper().getStopper() instanceof PromoStopper))
				return null;
			PromoStopper promoStopper = (PromoStopper) row.getPdfStopper().getStopper();
			if (promoStopper.getPeriodo() == null)
				return null;
			if (columnIndex == ColumnHeader.DATA_INIZIO.ordinal())
				return promoStopper.getPeriodo().getInizio();
			else 
				return promoStopper.getPeriodo().getFine();
		}
		if (columnIndex == ColumnHeader.PREZZO.ordinal())
		{
			if (row.getPdfStopper().getStopper() == null)
				return null;
			return row.getPdfStopper().getPrezzo();
		}
		if (columnIndex == ColumnHeader.COPIE.ordinal())
		{
			return row.getCopies();
		}

		return null;
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int colIndex)
	{
		PdfStopperRequest row = getRequests().get(rowIndex);
		if (colIndex == ColumnHeader.COPIE.ordinal())
		{
			row.setCopies(((Double) value).intValue());
		}

	}

	@Override
	public Class<?> getColumnClass(int c)
	{
		if (c == ColumnHeader.TIPO.ordinal())
			return String.class;
		if (c == ColumnHeader.CODICE.ordinal())
			return Codice.class;
		if (c == ColumnHeader.DESCRIZIONE.ordinal())
			return String.class;
		if (c == ColumnHeader.DATA_INIZIO.ordinal() || c == ColumnHeader.DATA_FINE.ordinal())
			return Date.class;
		if (c == ColumnHeader.PREZZO.ordinal())
			return Prezzo.class;
		if (c == ColumnHeader.COPIE.ordinal())
			return Integer.class;
		
		return Object.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int colIndex)
	{
		return colIndex == ColumnHeader.COPIE.ordinal();
	}

}