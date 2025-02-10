package fpozzi.gdoshop.model.offerta;

import java.text.ParseException;
import java.util.Date;

import fpozzi.utils.Utils;
import fpozzi.utils.date.DateUtils;
import fpozzi.utils.date.PeriodoCompleto;
import fpozzi.utils.date.PeriodoStandard.TipoPeriodoStandard;

public class PeriodoOfferta extends PeriodoCompleto
{
	private final PeriodoOffertaID id;

	public PeriodoOfferta(Date dataInizialeInclusa, Date dataFinaleInclusa, PeriodoOffertaID id)
	{
		super(dataInizialeInclusa, dataFinaleInclusa, TipoPeriodoStandard.GIORNO);
		this.id = id;
	}

	public PeriodoOfferta(Date dataInizialeInclusa, Date dataFinaleInclusa)
	{
		this(dataInizialeInclusa, dataFinaleInclusa, null);
	}

	public PeriodoOfferta(String dataIniziale, String dataFinale) throws ParseException
	{
		this(DateUtils.italianDateFormat.parse(dataIniziale), DateUtils.italianDateFormat.parse(dataFinale));
	}
	
	public PeriodoOffertaID getID()
	{
		return id;
	}

	@Override
	public boolean equals(Object altroOggetto)
	{
		if (!(altroOggetto instanceof PeriodoOfferta))
			return false;

		if (!super.equals(altroOggetto))
			return false;

		PeriodoOfferta altroPeriodo = (PeriodoOfferta) altroOggetto;

		if (!Utils.equalsWithNulls(id, altroPeriodo.id))
			return false;

		return true;
	}
	

	public String toString()
	{
		return DateUtils.italianDateFormat.format(dataInizio) + " - " + DateUtils.italianDateFormat.format(dataFine) + "  " + id.descrizione;
	}

	public static class PeriodoOffertaID
	{
		private final int codice;
		private final String descrizione;

		public PeriodoOffertaID(int codice, String descrizione)
		{
			super();
			this.codice = codice;
			this.descrizione = descrizione;
		}

		public int getCodice()
		{
			return codice;
		}

		public String getDescrizione()
		{
			return descrizione;
		}

		@Override
		public boolean equals(Object altroOggetto)
		{
			if (!(altroOggetto instanceof PeriodoOffertaID))
				return false;

			PeriodoOffertaID altroID = (PeriodoOffertaID) altroOggetto;

			return altroID.codice == codice;
		}

		@Override
		public int hashCode()
		{
			return codice;
		}
		
		
	}
}
