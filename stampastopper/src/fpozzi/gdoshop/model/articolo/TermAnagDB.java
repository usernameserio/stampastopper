package fpozzi.gdoshop.model.articolo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fpozzi.gdoshop.dao.DAOUtils;
import fpozzi.gdoshop.dao.MainDB;
import fpozzi.gdoshop.model.articolo.Movimentato.CausaleMovimento;
import fpozzi.gdoshop.model.fornitore.Fornitore;
import fpozzi.utils.date.DateUtils;
import fpozzi.utils.date.PeriodoStandard;
import fpozzi.utils.date.PeriodoStandard.TipoPeriodoStandard;

public class TermAnagDB
{

	private MainDB db;
	
	private PreparedStatement articoloFromEanStmt, eansFromCodiceStmt,
			articoloFromCodiceStmt,
			articoloFromDescrizioneStmt[];
	
	private HashMap<TipoPeriodoStandard, PreparedStatement> movimentatoStmt;

	public TermAnagDB(MainDB db) throws SQLException 
	{
		super();
		this.db=db;

		String baseSelectStart = "SELECT DISTINCT(t.codice), descrizione, pxv, "
				+ "t.um, oldpre_1, costo, "
				+ "marg, giac, annullam, stato, t.filler, desfor, oa_inizio, oa_fine, "
				+ "datacarico, datavendit, dataord, qta_ordine, info1, codfor, t.gramm, datains, sett"
				+ "FROM term_anag t "
				+ "JOIN infoart i ON t.codice = i.codice "
				+ "JOIN articok a ON a.codice = t.codice";
		
		String baseSelectEnd = " GROUP BY t.codice";

		articoloFromEanStmt = db.getConnection().prepareStatement(baseSelectStart
				+ "WHERE barcode = ?" + baseSelectEnd + " LIMIT 1");

		articoloFromCodiceStmt = db.getConnection().prepareStatement(baseSelectStart
				+ "WHERE t.codice = ?" + baseSelectEnd + " LIMIT 1");

		eansFromCodiceStmt = db.getConnection()
				.prepareStatement("SELECT DISTINCT(barcode) FROM term_anag WHERE codice = ?");
		
		String baseRicercaArticoloWhere = 
				"WHERE LENGTH(t.codice)=7 AND codfor=\"900001\" ";
		
		articoloFromDescrizioneStmt = new PreparedStatement[3];
		
		articoloFromDescrizioneStmt[0] = db.getConnection().prepareStatement(baseSelectStart +
					baseRicercaArticoloWhere +
						"AND descrizione LIKE ? " 
					+ baseSelectEnd);
		
		articoloFromDescrizioneStmt[1] = db.getConnection().prepareStatement(baseSelectStart +
				baseRicercaArticoloWhere +
					"AND descrizione LIKE ? AND descrizione LIKE ?" 
				+ baseSelectEnd);
		
		articoloFromDescrizioneStmt[2] = db.getConnection().prepareStatement(baseSelectStart +
				baseRicercaArticoloWhere +
					"AND descrizione LIKE ? AND descrizione LIKE ? AND descrizione LIKE ?" 
				+ baseSelectEnd);
		
		movimentatoStmt = new HashMap<TipoPeriodoStandard, PreparedStatement>();
				
		movimentatoStmt.put(TipoPeriodoStandard.GIORNO, db.getConnection().prepareStatement(
					"SELECT DATE_FORMAT(stoa_data_ora, '%Y-%m-%d'), stoa_qta " +
					"FROM stoart " +
					"WHERE (stoa_causalemov=? OR stoa_causalemov=?) " +
					"AND stoa_codice=? AND stoa_codoff<>? " +
					"AND stoa_data_ora>=? AND stoa_data_ora<=? " +
					"ORDER BY stoa_data_ora")
				);
		
		movimentatoStmt.put(TipoPeriodoStandard.SETTIMANA, db.getConnection().prepareStatement(
					"SELECT stoa_data_ora_WoY, stoa_data_ora_YoW, SUM(stoa_qta) " +
					"FROM stoart " +
					"WHERE (stoa_causalemov=? OR stoa_causalemov=?) " +
					"AND stoa_codice=? AND stoa_codoff<>? " +
					"AND stoa_data_ora>=? AND stoa_data_ora<=? " +
					"GROUP BY stoa_data_ora_WoY ")
				);
		
		movimentatoStmt.put(TipoPeriodoStandard.MESE, db.getConnection().prepareStatement(
				"SELECT MONTH(stoa_data_ora) AS m, YEAR(stoa_data_ora) as y, SUM(stoa_qta) " +
				"FROM stoart " +
				"WHERE (stoa_causalemov=? OR stoa_causalemov=?) " +
				"AND stoa_codice=? AND stoa_codoff<>? " +
				"AND stoa_data_ora>=? AND stoa_data_ora<=? " +
				"GROUP BY y, m")
			);

	}

	public MainDB getMainDB()
	{
		return db;
	}

	public synchronized Articolo getArticoloFromEan(String codiceEAN) throws Exception
	{
		return getArticoloFromCodice(codiceEAN, articoloFromEanStmt);
	}

	public synchronized Articolo getArticoloByCodiceInterno(String codice)
			throws Exception
	{
		return getArticoloFromCodice(codice, articoloFromCodiceStmt);
	}

	private Articolo getArticoloFromCodice(String codice,
			PreparedStatement select) throws Exception
	{

		ResultSet rs;
		Articolo articolo = null;

		select.setString(1, codice);
		rs = select.executeQuery();

		if (rs.next())
		{
			articolo = makeArticolo(rs);
		}

		rs.close();

		return articolo;

	}
	
	private synchronized Articolo makeArticolo(ResultSet rs) throws Exception
	{
		
		/*
		 * 		t.codice descrizione, pxv, t.um, prezzo, 
		 * 		costo, marg, giac, annullam, stato,
		 * 		t.filler, desfor, oa_inizio, oa_fine, datacarico, 
		 * 		datavendit, dataord, qta_ordine, info1, codfor, 
		 * 		t.gramm, datains "
		 */
		CodiceInterno codiceInterno = new CodiceInterno(rs.getString(1));
		
		/*
		if (Articolo.isFresco(codiceInterno))
			articolo = new Fresco(rs.getString(1), rs.getString(2));
		else
			articolo = new Articolo(rs.getString(1), rs.getString(2));
		*/

		Collection<String> codiciEan = new ArrayList<String>();
		eansFromCodiceStmt.setString(1, codiceInterno.valore);
		ResultSet rsEans = eansFromCodiceStmt.executeQuery();
		while (rsEans.next())
		{
			String ean = rsEans.getString(1);
			if (!ean.equals(codiceInterno) && !ean.startsWith("99999"))
				codiciEan.add(ean);
		}
		
		int indiceRotazione = -1;
		Matcher m = infoFieldPattern.matcher(rs.getString(19));
		if (m.find())
			indiceRotazione = Integer.valueOf(m.group(1));
		
		double costo = rs.getDouble(6), prezzo = rs.getInt(5)/100.0;
		
		Articolo articolo =
				new Articolo(codiceInterno, 
				rs.getString(2), /* DESCRIZIONE */ 
				rs.getInt(23), /* REPARTO */
				codiciEan, false,
				new Quantita(UnitaMisura.fromString(rs.getString(4)) , new Double(rs.getInt(21))),
				costo, /* COSTO  */
				prezzo, /* PREZZO */
				DAOUtils.calcolaIva(costo,prezzo, rs.getDouble(7)),/* IVA */
				new Fornitore(rs.getString(20), rs.getString(12)), /* FORNITORE */
				indiceRotazione, 
				0, /* PZ X COLLO */
				rs.getDouble(8), /* GIACENZA */
				rs.getDate(22),/* DATA INS */
				rs.getDate(17),/* DATA ORD */
				rs.getDate(15),/* DATA ACQ */
				rs.getDate(16),/* DATA VEN */
				rs.getString(9).trim().equals("S"), /* ANNULLATO */
				rs.getString(10).trim().equals("P"), /* O.A. */
				rs.getString(11).trim().equals("OFFERTA") /* OFFERTA */
				); 



		rsEans.close();
		
		return articolo;

	}

	public synchronized List<Articolo> getArticoliFromRicercaDescrizione(String[] paroleRicercate) 
			throws Exception
	{

		ResultSet rs;
		List<Articolo> articoli = new LinkedList<Articolo>();
		
		if (paroleRicercate.length>0)
		{			
			int nParoleRicercate = Math.min(paroleRicercate.length, 3);
			PreparedStatement selectStmt = articoloFromDescrizioneStmt[nParoleRicercate-1];
			for (int i = 0; i<nParoleRicercate; i++)
				selectStmt.setString(i+1, "%" + paroleRicercate[i] + "%");
			rs = selectStmt.executeQuery();
	
			while (rs.next())
			{
				articoli.add(makeArticolo(rs));
			}
	
			rs.close();
		}

		return articoli;
	}

	public synchronized List<Movimentato> getMovimentato(Articolo articolo, 
			CausaleMovimento causaleMovimento,
			TipoPeriodoStandard tipoPeriodo, Date aPartireDa, Date finoA, 
			boolean isOfferta) 
			throws Exception
	{
		return getMovimentato(articolo, causaleMovimento, tipoPeriodo, 
				DateUtils.americanDateFormat.format(aPartireDa),
				DateUtils.americanDateFormat.format(finoA),
				isOfferta
				);
	}
	
	
	private List<Movimentato> getMovimentato(Articolo articolo, 
			CausaleMovimento causaleMovimento,
			TipoPeriodoStandard tipoPeriodo, String aPartireDa, String finoA, 
			boolean offerta) 
			throws Exception
	{
		ResultSet rs;
		List<Movimentato> movimentatoOfferta = new LinkedList<Movimentato>();
		
		PreparedStatement venditePeriodoStmt = movimentatoStmt.get(tipoPeriodo);

		if (venditePeriodoStmt==null)
			throw new Exception("Tipo periodo non supportato");
		
		if (causaleMovimento==CausaleMovimento.ACQUISTO)
		{
			venditePeriodoStmt.setString(1, "CA");
			venditePeriodoStmt.setString(2, "CD");
		}
		else
		{
			venditePeriodoStmt.setString(1, "VP");
			venditePeriodoStmt.setString(2, "XX");
		}
			
		venditePeriodoStmt.setString(3, articolo.getCodiceInterno().valore);
		venditePeriodoStmt.setString(4, offerta ? "" : "pippo");
		venditePeriodoStmt.setString(5, aPartireDa);
		venditePeriodoStmt.setString(6, finoA);
		rs = venditePeriodoStmt.executeQuery();
		while (rs.next())
		{
			if (tipoPeriodo==TipoPeriodoStandard.GIORNO)
			{
				movimentatoOfferta.add(new Movimentato(articolo,
						causaleMovimento,
						PeriodoStandard.makePeriodoStandard(
								DateUtils.americanDateFormat.parse(rs.getString(1)),
								TipoPeriodoStandard.GIORNO),
						rs.getDouble(2)));
			}
			else if (tipoPeriodo==TipoPeriodoStandard.SETTIMANA)
			{
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.WEEK_OF_YEAR, rs.getInt(1));
				cal.set(Calendar.YEAR, Integer.valueOf(rs.getString(2)));
				movimentatoOfferta.add(new Movimentato(articolo,
						causaleMovimento,
						PeriodoStandard.makePeriodoStandard(cal.getTime(), 
								TipoPeriodoStandard.SETTIMANA),
						rs.getDouble(3)));
			}
			else if (tipoPeriodo==TipoPeriodoStandard.MESE)
			{
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.MONTH, rs.getInt(1)-1);
				cal.set(Calendar.YEAR, rs.getInt(2));
				movimentatoOfferta.add(new Movimentato(articolo,
						causaleMovimento,
						PeriodoStandard.makePeriodoStandard(cal.getTime(), 
								TipoPeriodoStandard.MESE),
						rs.getDouble(3)));
			}
		}
		rs.close();
		return movimentatoOfferta;
	}


	static private final Pattern infoFieldPattern = Pattern
			.compile("I\\.R\\.:\\s*\\+?([^\\s]*) ([^\\s]*)$");

}
