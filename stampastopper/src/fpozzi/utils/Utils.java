package fpozzi.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.AbstractCollection;
import java.util.Iterator;

public class Utils {

	public static String join(AbstractCollection<String> s, String delimiter) {
		if (s == null || s.isEmpty())
			return "";
		Iterator<String> iter = s.iterator();
		StringBuilder builder = new StringBuilder(iter.next());
		while (iter.hasNext()) {
			builder.append(delimiter).append(iter.next());
		}
		return builder.toString();
	}

	public static void copyfile(File srFile, File dtFile) throws IOException {
		InputStream in = new FileInputStream(srFile);

		OutputStream out = new FileOutputStream(dtFile);

		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		
		in.close();
		out.close();
	}
	
	public static final boolean equalsWithNulls(Object a, Object b)
	{
		if (a == b)
			return true;
		if ((a == null) || (b == null))
			return false;
		return a.equals(b);
	}
	
	public static boolean isNumeric(String string) throws IllegalArgumentException
	{
	   boolean isnumeric = false;

	   if (string != null && !string.equals(""))
	   {
	      isnumeric = true;
	      char chars[] = string.toCharArray();

	      for(int d = 0; d < chars.length; d++)
	      {
	         isnumeric &= Character.isDigit(chars[d]);

	         if(!isnumeric)
	         break;
	      }
	   }
	   return isnumeric;
	}

}
