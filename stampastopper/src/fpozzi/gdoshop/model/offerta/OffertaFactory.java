package fpozzi.gdoshop.model.offerta;

import java.util.Date;

import fpozzi.gdoshop.model.articolo.Articolo;
import fpozzi.utils.format.FormatUtils;

public class OffertaFactory
{

	static public OffertaArticolo fromDBStorico(Articolo articolo,
			String descTipoOfferta, int valoreOfferta, double prezzoIntero,
			Date dataInizio, Date dataFine)
	{
		String descrizione = "";
		double prezzoOfferta = 0;
		TipoOfferta tipoOfferta = null;
		
		if(descTipoOfferta.equals("TAGLIO PREZZO"))
		{
			tipoOfferta = TipoOfferta.TAGLIO_PREZZO;
			descrizione = descTipoOfferta;
			prezzoOfferta = valoreOfferta/100.0;
		}
		else if (descTipoOfferta.equals("SCONTO % SU PRODOTTO"))
		{
			tipoOfferta = TipoOfferta.SCONTO_PERCENTUALE;
			descrizione = "SCONTO " + valoreOfferta + "%" ;
			prezzoOfferta = prezzoIntero * (1.0-valoreOfferta/100.0);
		}
		else if (descTipoOfferta.equals("SCONTO VALORE"))
		{
			tipoOfferta = TipoOfferta.SCONTO_VALORE;
			descrizione = "SCONTO € " + FormatUtils.twoDecimalDigitsFormat.format(valoreOfferta/100.0);
			prezzoOfferta = prezzoIntero - valoreOfferta/100.0;
		}
		else if (descTipoOfferta.equals("BOLLINI SU ARTICOLO"))
		{
			tipoOfferta = TipoOfferta.BOLLINI;
			descrizione = valoreOfferta + " BOLLINI";
			prezzoOfferta = prezzoIntero;
		}
		else if (descTipoOfferta.equals("PROMOZIONE MxN"))
		{
			String mn =  valoreOfferta + "";
			if (mn.equals("21"))
			{
				tipoOfferta = TipoOfferta.UNO_PIU_UNO;
				descrizione = "PROMO 1+1";
			}
			else
			{
				tipoOfferta = TipoOfferta.M_PER_N;
				descrizione = "PROMO " + mn.substring(0, 1) + "X" + mn.substring(1, 2);
			}
			prezzoOfferta = prezzoIntero;
		}
		else 
			return null;

		return new OffertaArticolo(articolo, tipoOfferta, descrizione, 
				prezzoIntero, prezzoOfferta, Double.NaN, Double.NaN, 
				new PeriodoOfferta(dataInizio, dataFine),
				DestinatarioOfferta.SCONOSCIUTO);
	}
	
}
