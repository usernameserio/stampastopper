package fpozzi.stopper.dao.mysql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import fpozzi.gdoshop.model.articolo.Articolo;
import fpozzi.gdoshop.model.articolo.CodiceEan;
import fpozzi.gdoshop.model.articolo.CodiceInterno;
import fpozzi.gdoshop.model.articolo.Quantita;
import fpozzi.gdoshop.model.articolo.UnitaMisura;
import fpozzi.gdoshop.model.offerta.PeriodoOfferta;
import fpozzi.gdoshop.model.offerta.PeriodoOfferta.PeriodoOffertaID;
import fpozzi.stopper.dao.ArticoloDAO;
import fpozzi.stopper.dao.PromoDAO;
import fpozzi.stopper.model.Prezzo;
import fpozzi.stopper.model.PrezzoPromo;
import fpozzi.stopper.model.PromoStopper;
import fpozzi.stopper.model.pdf.PdfPromoStopper;
import fpozzi.stopper.model.pdf.ScontoPercentualePdfStopper;
import fpozzi.stopper.model.pdf.TaglioPrezzoPdfStopper;
import fpozzi.stopper.model.pdf.UnoPiuUnoPdfStopper;
import fpozzi.utils.StringUtils;

public class MysqlPromoDAO implements PromoDAO
{
	private final ArticoloDAO artDAO;
	private final MysqlDataSource dataSource;

	private final String getPromoBaseQuery = "SELECT s.offerta, s.codiceart, s.prezzo,"
			+ "LEAST(a.prezzo, IFNULL(i1.oldpre_1, a.prezzo), IFNULL(i2.oldpre_2, a.prezzo), IFNULL(i3.oldpre_3, a.prezzo), IFNULL(i4.oldpre_4, a.prezzo)) as pp,"
			+ "s.soglia_qta, s.destinatar, s.tipooff, s.codiceoff, s.descroffer, s.datainizio, s.datafine,"
			+ "s.mxn, s.sconto, SUBSTRING(r.campo01, 1, 1) " + "FROM scadenze s "
			+ "JOIN articok a ON a.codice=s.codiceart " + "JOIN repa r ON r.sett=a.sett "
			+ "LEFT JOIN infoart i1 ON i1.codice=s.codiceart AND i1.d_oldpre_1>DATE_SUB(NOW(), INTERVAL 1 MONTH) "
			+ "LEFT JOIN infoart i2 ON i2.codice=s.codiceart AND i2.d_oldpre_2>DATE_SUB(NOW(), INTERVAL 1 MONTH) "
			+ "LEFT JOIN infoart i3 ON i3.codice=s.codiceart AND i3.d_oldpre_3>DATE_SUB(NOW(), INTERVAL 1 MONTH) "
			+ "LEFT JOIN infoart i4 ON i4.codice=s.codiceart AND i4.d_oldpre_4>DATE_SUB(NOW(), INTERVAL 1 MONTH) ",
			getPromoByCodiceOffertaQuery = getPromoBaseQuery
					+ "WHERE s.codiceoff=? AND s.descroffer=? AND s.datainizio=? AND s.datafine=?",
			getPromoByCodiceArticoloQuery = getPromoBaseQuery + "WHERE s.codiceart=?";

	public MysqlPromoDAO(MysqlDataSource dataSource, ArticoloDAO artDAO)
	{
		this.dataSource = dataSource;
		this.artDAO = artDAO;
	}

	private static Pattern qtaInDescPattern = Pattern
			.compile("\\s*(" + "((ml|cl|gr|lt|kg|sg|g|(t\\.))\\s*[\\.\\,]?\\s*\\d+([\\,\\.]\\d+)?(\\+\\d+)?)" + "|"
					+ "(\\d+([\\,\\.]\\d+)?(\\+\\d+)?\\s*(ml|cl|gr|lt|kg|g)\\.?(\\W+|$))" + ")\\s*");
	private static Pattern multiplePattern = Pattern.compile("\\s*(x\\s*(\\d+)(\\s*\\+\\s*(\\d+))?)");

	private PdfPromoStopper make(ResultSet rs) throws Exception
	{
		int tipoOff = Integer.parseInt(rs.getString(1));
		if (tipoOff > 10)
			return null;

		CodiceInterno codice = new CodiceInterno(rs.getString(2).trim());

		Articolo art = artDAO.getArticoloByCodiceInterno(codice);

		if (art == null)
			return null;

		String descrizioneOriginale = art.getDescrizione().toLowerCase();
		Matcher multipleMatcher = multiplePattern.matcher(descrizioneOriginale);
		int mult = 0;
		while (multipleMatcher.find())
		{
			mult = Integer.parseInt(multipleMatcher.group(2));
			if (multipleMatcher.group(4) != null)
				mult += Integer.parseInt(multipleMatcher.group(4));
		}
		descrizioneOriginale = multipleMatcher.replaceAll(" ");

		String descSenzaQta = qtaInDescPattern.matcher(descrizioneOriginale).replaceAll(" ");
		descSenzaQta = descSenzaQta.replace("G&P", "Gusto&Passione").replace("E&P", "Equilibrio&Piacere");

		double prezzoPromoRaw = rs.getDouble(3) / 100.0;

		Prezzo prezzo = rs.getString(14).equals("1") ? new Prezzo(rs.getDouble(4) / 100.0) : null;

		if ((tipoOff == 1 || tipoOff == 2 || tipoOff == 4) && prezzoPromoRaw != 0)
			prezzo = new Prezzo(prezzoPromoRaw);

		Integer pezziMax = null;
		try
		{
			pezziMax = rs.getInt(5);
		} catch (Exception e)
		{
		}

		boolean conCard = rs.getString(6).trim().equals("FF");

		boolean facoltativo = rs.getString(7).equals("STP");

		PeriodoOffertaID periodoID = new PeriodoOffertaID(Integer.parseInt(rs.getString(8)), rs.getString(9));
		PeriodoOfferta periodo = new PeriodoOfferta(rs.getDate(10), rs.getDate(11), periodoID);

		PromoStopper stopper = new PromoStopper();

		stopper.setDescrizione(StringUtils.smartCase(descSenzaQta));

		stopper.setCodiceInterno(art.getCodiceInterno());

		Quantita qta = art.getGrammatura();
		UnitaMisura um;
		if (qta != null && qta.getValore() != null
				&& !((um = qta.getUnitaMisura()) == UnitaMisura.PZ && qta.getValore() == 1))
		{
			if (mult > 0)
			{
				if (um == UnitaMisura.PZ)
					qta = new Quantita(qta.getUnitaMisura(), (double) mult);
				else if (mult <= 10)
				{
					double nuovoValore = qta.getValore() / (mult * 1.0);

					if (nuovoValore % 1 != 0)
						if (um == UnitaMisura.CL && nuovoValore * 10 % 1 == 0)
						{
							um = UnitaMisura.ML;
							nuovoValore = nuovoValore * 10;
						} else if (nuovoValore < 1)
							if (um == UnitaMisura.LT)
							{
								if (nuovoValore * 100 % 1 == 0)
								{
									um = UnitaMisura.CL;
									nuovoValore = nuovoValore * 100;
								} else
								{
									um = UnitaMisura.ML;
									nuovoValore = nuovoValore * 1000;
								}
							} else if (um == UnitaMisura.KG)
							{
								um = UnitaMisura.GR;
								nuovoValore = nuovoValore * 1000;
							}

					qta = new Quantita(um, nuovoValore, mult);
				}
			}
		}
		if (qta != null)
			stopper.getQuantita().add(qta);

		stopper.setPrezzo(prezzo);

		stopper.setPeriodo(periodo);

		if (pezziMax > 0)
			stopper.setPezziMax(pezziMax);

		stopper.setConCard(conCard);

		stopper.setFacoltativo(facoltativo);
		
		stopper.setPrezzoPerUMNascosto(art.getReparto()==2);

		if (tipoOff == 1 && rs.getString(12).equals("0201")) // MxN
		{
			stopper.setPrezzoPromo(new PrezzoPromo(prezzoPromoRaw));
			return new UnoPiuUnoPdfStopper(stopper);
		} else if (tipoOff == 2) // SCONTO %
		{
			int sconto = Double.valueOf(rs.getDouble(13)).intValue();
			stopper.setPrezzoPromo(new PrezzoPromo(prezzo, sconto));
			return new ScontoPercentualePdfStopper(stopper);
		} else if (tipoOff == 4) // SCONTO %
		{
			double sconto = Double.valueOf(rs.getDouble(13)) / 100.0;
			stopper.setPrezzoPromo(new PrezzoPromo(prezzo.getValue() - sconto));
			return new TaglioPrezzoPdfStopper(stopper);
		} else
		/*{
			if (prezzo != null)
			{
				int sconto = (int) Math.round((1 - (prezzoPromoRaw / prezzo.getValue())) * 100);
				if ((sconto > 10 && sconto % 10 == 0) || (sconto > 25 && sconto % 5 == 0) || sconto == 33)
				{
					stopper.setPrezzoPromo(new PrezzoPromo(prezzo, prezzoPromoRaw));
					return new ScontoPercentualePdfStopper(stopper);
				}
			}*/
			if (tipoOff == 3) // TP
			{
				stopper.setPrezzoPromo(new PrezzoPromo(prezzoPromoRaw));
				return new TaglioPrezzoPdfStopper(stopper);
			}
		//}

		return null;
	}

	public synchronized List<PdfPromoStopper> getAll(PeriodoOfferta periodo) throws Exception
	{
		PdfPromoStopper stopper;
		List<PdfPromoStopper> allStoppers = new ArrayList<PdfPromoStopper>();

		Connection conn = dataSource.getConnection();
		PreparedStatement st = conn.prepareStatement(getPromoByCodiceOffertaQuery);
		st.setString(1, (String.format("%04d", periodo.getID().getCodice())));
		st.setString(2, periodo.getID().getDescrizione());
		st.setDate(3, new Date(periodo.getInizio().getTime()));
		st.setDate(4, new Date(periodo.getFine().getTime()));
		ResultSet rs = st.executeQuery();
		while (rs.next())
			try
			{
				stopper = make(rs);
				if (stopper != null)
					allStoppers.add(stopper);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		rs.close();
		conn.close();
		return allStoppers;

	}

	public synchronized List<PdfPromoStopper> getAll(CodiceInterno codiceArticolo) throws Exception
	{
		PdfPromoStopper stopper;
		List<PdfPromoStopper> allStoppers = new ArrayList<PdfPromoStopper>();

		Connection conn = dataSource.getConnection();
		PreparedStatement st = conn.prepareStatement(getPromoByCodiceArticoloQuery);
		st.setString(1, codiceArticolo.toString());
		ResultSet rs = st.executeQuery();
		while (rs.next())
			try
			{
				stopper = make(rs);
				if (stopper != null)
					allStoppers.add(stopper);
			} catch (Exception e)
			{
				e.printStackTrace();
			}

		return allStoppers;
	}

	public synchronized List<PdfPromoStopper> getAll(CodiceEan codiceEan) throws Exception
	{
		CodiceInterno codiceArticolo = artDAO.getCodiceInternoFromCodiceEAN(codiceEan);
		if (codiceArticolo != null)
			return getAll(codiceArticolo);
		else
			return Collections.emptyList();
	}

	public synchronized PeriodoOfferta[] getAllPeriodi() throws Exception
	{
		PeriodoOfferta periodo;
		List<PeriodoOfferta> periodi = new ArrayList<PeriodoOfferta>();

		Connection conn = dataSource.getConnection();
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery("SELECT codiceoff, descroffer, datainizio, datafine " + "FROM scadenze "
				+ "GROUP BY codiceoff, descroffer, datainizio, datafine " + "ORDER BY datainizio, datafine");
		while (rs.next())
		{
			try
			{
				periodo = new PeriodoOfferta(rs.getDate(3), rs.getDate(4),
						new PeriodoOffertaID(Integer.parseInt(rs.getString(1)), rs.getString(2)));
				periodi.add(periodo);
			} catch (Exception e)
			{
				// e.printStackTrace();
			}
		}

		return periodi.toArray(new PeriodoOfferta[periodi.size()]);
	}

}
