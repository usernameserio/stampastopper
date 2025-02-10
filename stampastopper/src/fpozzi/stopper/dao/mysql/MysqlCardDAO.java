package fpozzi.stopper.dao.mysql;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import fpozzi.stopper.dao.CardDAO;

public class MysqlCardDAO implements CardDAO
{
	private final MysqlDataSource dataSource;
	
	private final String NomeClienteByCardQuery =
			"SELECT nome, cognome FROM clienti WHERE codtessera=?";
	
	public MysqlCardDAO(MysqlDataSource dataSource) throws Exception
	{
		this.dataSource = dataSource;
	}
	
	public String getCustomerName(String cardNumber) throws Exception
	{
		Connection conn = dataSource.getConnection();
		PreparedStatement st = conn.prepareStatement(NomeClienteByCardQuery);
		st.setString(1, cardNumber);
		ResultSet rs = st.executeQuery();
		if (rs.next())
		{
			String customerName =  rs.getString(1).trim() + " " + rs.getString(2).trim();
			rs.close();
			conn.close();
			return customerName;
		}		

		return null;
	}

}
