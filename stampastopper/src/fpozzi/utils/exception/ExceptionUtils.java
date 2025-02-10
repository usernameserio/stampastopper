package fpozzi.utils.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils
{

	public static String stack2string(Exception e)
	{
		try
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return "------\r\n" + sw.toString() + "------\r\n";
		} 
		catch (Exception e2)
		{
			return "bad stack2string";
		}
	}

}
