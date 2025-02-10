package fpozzi.gdoshop.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MainDB
{
	
	static
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e)
		{
		}
	}
		
	private Connection connection;
	
	public MainDB(String address, String port, String username,
			String password) throws Exception
	{
		super();
		connection = DriverManager.getConnection("jdbc:mysql://" + address
				+ ":" + port + "/proxima?connectTimeout=3000", username, password);
		
		if (connection == null)
			throw new Exception("Impossibile connettersi");
	}

	public Connection getConnection()
	{
		return connection;
	}
	
	public void close() throws SQLException
	{
		if (!connection.isClosed())
			connection.close();
	}
	
	public boolean isClosed() throws SQLException
	{
		return connection.isClosed();
	}


}
