package fpozzi.stopper.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import fpozzi.gdoshop.dao.DAOUtils;
import fpozzi.gdoshop.model.articolo.Articolo;
import fpozzi.gdoshop.model.articolo.CodiceEan;
import fpozzi.gdoshop.model.articolo.CodiceInterno;
import fpozzi.gdoshop.model.articolo.Quantita;
import fpozzi.gdoshop.model.articolo.UnitaMisura;
import fpozzi.gdoshop.model.fornitore.Fornitore;
import fpozzi.stopper.dao.ArticoloDAO;
import fpozzi.utils.StringUtils;

public class MysqlArticoloDAO implements ArticoloDAO
{
	private final MysqlDataSource dataSource;

	private final String codiceFromEanQuery = "SELECT codice FROM mag007 WHERE ean=?",
			basicArticoloQuery = "SELECT a.codice, a.descrizio, a.descrizio2, a.ean, a.um, a.gramm,"
					+ "a.costo, a.prezzo, a.marg, a.codfor, a.desfor, a.pxv, a.giac,"
					+ "i.datains, i.dataord, i.datacarico, i.datavendit, a.tipoart, a.sett " + "FROM articok a "
					+ "JOIN infoart i ON i.codice=a.codice ",
			articoloFromCodiceQuery = basicArticoloQuery + "WHERE a.codice=?",
			articoloFromEanQuery = basicArticoloQuery + "JOIN mag007 m ON m.codice=a.codice " + "WHERE m.ean=?";

	public MysqlArticoloDAO(MysqlDataSource dataSource) throws Exception
	{
		this.dataSource = dataSource;
	}

	public Articolo getArticoloByCodiceInterno(final CodiceInterno codice) throws Exception
	{
		if (codice == null)
			return null;

		Connection conn = dataSource.getConnection();
		PreparedStatement st = conn.prepareStatement(articoloFromCodiceQuery);
		st.setString(1, codice.toString());
		ResultSet rs = st.executeQuery();
		while (rs.next())
		{
			Articolo articolo = makeArticolo(rs);
			rs.close();
			conn.close();
			return articolo;
		}
		return null;
	}

	public Articolo getArticoloByCodiceEan(final CodiceEan ean) throws Exception
	{
		Connection conn = dataSource.getConnection();
		PreparedStatement st = conn.prepareStatement(articoloFromEanQuery);
		st.setString(1, padLeftZeros(ean.toString(), 13));
		ResultSet rs = st.executeQuery();
		while (rs.next())
		{
			Articolo articolo = makeArticolo(rs);
			rs.close();
			conn.close();
			return articolo;
		}
		return null;
	}

	@Override
	public CodiceInterno getCodiceInternoFromCodiceEAN(final CodiceEan ean) throws Exception
	{

		Connection conn = dataSource.getConnection();
		PreparedStatement st = conn.prepareStatement(codiceFromEanQuery);
		st.setString(1, padLeftZeros(ean.toString(), 13));
		ResultSet rs = st.executeQuery();
		while (rs.next())
		{
			CodiceInterno codice = new CodiceInterno(rs.getString(1));
			rs.close();
			conn.close();
			return codice;
		}

		return null;
	}

	final private StringBuilder sb = new StringBuilder();

	private Articolo makeArticolo(final ResultSet rs) throws Exception
	{
		CodiceInterno codice = new CodiceInterno(rs.getString(1));

		String descrizione = StringUtils.rightTrim(StringUtils.compactSpaces(rs.getString(2) + rs.getString(3)));

		int start = 0;
		int index;
		while ((index = descrizione.substring(start).indexOf('`')) >= 0)
		{
			if (index > 0)
			{
				char replacement = '\'';
				switch (descrizione.charAt(start + index - 1))
				{
					case 'a':
						replacement = 'à';
					break;
					case 'e':
						replacement = 'è';
					break;
					case 'i':
						replacement = 'ì';
					break;
					case 'o':
						replacement = 'ò';
					break;
					case 'u':
						replacement = 'ù';
					break;
					case 'A':
						replacement = 'À';
					break;
					case 'E':
						replacement = 'È';
					break;
					case 'I':
						replacement = 'Ì';
					break;
					case 'O':
						replacement = 'Ò';
					break;
					case 'U':
						replacement = 'Ù';
					break;
				}
				if (replacement != '\'')
				{
					descrizione = descrizione.substring(0, start + index - 1) + replacement + descrizione.substring(start + index + 1);
					index--;
				}
				else
				{
					descrizione = descrizione.substring(0, start + index) + replacement + descrizione.substring(start + index + 1);
				}
			}
			start += index + 1;
		}

		Collection<String> ean = new ArrayList<String>(Arrays.asList(new String[] { rs.getString(4) }));

		char tipoArt = rs.getString(18).charAt(0);
		int reparto = Integer.parseInt(rs.getString(19));
		boolean venditaAPeso = tipoArt == '1' || tipoArt == '2' || tipoArt == '5';

		double valoreQta = (double)rs.getInt(6);
		UnitaMisura um = UnitaMisura.fromString(rs.getString(5));
		Quantita qta;

		if (valoreQta == 1 && um == UnitaMisura.PZ)
			qta = null;
		else if (valoreQta == 1 && venditaAPeso)
			qta = new Quantita(um);
		else
		{
			if (valoreQta >= 1000)
			{
				if (um == UnitaMisura.GR && valoreQta % 10 == 0)
				{
					um = UnitaMisura.KG;
					valoreQta = valoreQta / 1000;
				} else if (um == UnitaMisura.ML && valoreQta % 10 == 0)
				{
					um = UnitaMisura.LT;
					valoreQta = valoreQta / 1000;
				}
			} else if (valoreQta >= 100)
			{
				if (reparto != 2 && um == UnitaMisura.ML && valoreQta % 10 == 0)
				{
					um = UnitaMisura.CL;
					valoreQta = valoreQta / 10;
				}
				if (um == UnitaMisura.CL && valoreQta % 10 == 10)
				{
					um = UnitaMisura.LT;
					valoreQta = valoreQta / 100;
				}
			}
			qta = new Quantita(um, valoreQta);
		}

		double costo = rs.getDouble(7) / 100.0;
		double prezzo = rs.getDouble(8) / 100.0;
		int iva = DAOUtils.calcolaIva(costo, prezzo, rs.getDouble(9));
		Fornitore fornitore = new Fornitore(rs.getString(10), rs.getString(11));
		int ir = -1;
		int pxv = rs.getInt(12);
		String giacenzaField = rs.getDouble(13) + "";
		double giacenza = giacenzaField.isEmpty() ? 0 : Double.parseDouble(giacenzaField);
		Date dataIns = rs.getDate(14), dataOrd = rs.getDate(15), dataAcq = rs.getDate(16), dataVen = rs.getDate(17);
		boolean annullato = false, occasione = false, offerta = false;

		return new Articolo(codice, descrizione, reparto, ean, venditaAPeso, qta, costo, prezzo, iva, fornitore, ir, pxv,
				giacenza, dataIns, dataOrd, dataAcq, dataVen, annullato, offerta, occasione);
	}

	private String padLeftZeros(final String inputString, final int length)
	{
		if (inputString.length() >= length)
		{
			return inputString;
		}

		sb.setLength(0);
		while (sb.length() < length - inputString.length())
		{
			sb.append('0');
		}
		sb.append(inputString);
		return sb.toString();
	}
}
