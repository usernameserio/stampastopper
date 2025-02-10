package fpozzi.gdoshop.model.offerta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import fpozzi.gdoshop.dao.MainDB;
import fpozzi.gdoshop.model.articolo.Articolo;
import fpozzi.utils.date.DateUtils;

public class OffertaMysqlDAO
{

	private MainDB db;
	
	private PreparedStatement offertaStmt;

	public OffertaMysqlDAO(MainDB db) throws SQLException 
	{
		super();
		this.db=db;
		
		offertaStmt = db.getConnection().prepareStatement(
				"SELECT stoa_valoff, stoa_note, stoa_prezzo " +
				"FROM stoart " +
				"WHERE stoa_codice=? AND stoa_codoff<>'' " +
				"AND stoa_data_ora>=? AND stoa_data_ora<?" 
				);
	}
	
	public MainDB getMainDB()
	{
		return db;
	}
	
	public synchronized OffertaArticolo makeOfferta(Articolo articolo, Date dataInizio, Date dataFine) 
			throws Exception
	{
		ResultSet rs;

		OffertaArticolo off = null;
		
		offertaStmt.setString(1, articolo.getCodiceInterno().valore);
		offertaStmt.setString(2, DateUtils.americanDateFormat.format(dataInizio));
		offertaStmt.setString(3, DateUtils.americanDateFormat.format(new Date(dataInizio.getTime()+DateUtils.millisInADay)));
		rs = offertaStmt.executeQuery();
		
		String tipoOfferta; double prezzoIntero; int valoreOfferta;
		if (rs.next())
		{
			valoreOfferta = rs.getInt(1);
			tipoOfferta = rs.getString(2);
			prezzoIntero = rs.getDouble(3);
			off = OffertaFactory.fromDBStorico(articolo, 
					tipoOfferta, valoreOfferta,
					prezzoIntero/100.0, dataInizio, dataFine);
		}
		rs.close();
		
		return off;
	}

}
