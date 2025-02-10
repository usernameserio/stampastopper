package fpozzi.gdoshop.dao;

public class DAOUtils
{
	
	public static int calcolaIva(double costo, double prezzo, double margine)
	{
		return (int) Math.round((prezzo / costo) * (100.0 - margine) - 100.0);
	}

}
