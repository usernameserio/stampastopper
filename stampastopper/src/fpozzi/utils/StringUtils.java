package fpozzi.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils
{

	static private Pattern spaces = Pattern.compile("\\s+");

	static public String compactSpaces(String originalString)
	{
		return spaces.matcher(originalString).replaceAll(" ");
	}

	static public String makeFirstLetterUppercase(String originalString)
	{
		return originalString.substring(0, 1).toUpperCase() + originalString.substring(1);
	}

	static public String rightTrim(String s)
	{
		int positionTrim = s.length() - 1;
		while (positionTrim >= 0 && s.charAt(positionTrim) == ' ')
			positionTrim--;
		return s.substring(0, positionTrim + 1);
	}

	public static String join(String[] strings, int startIndex, String separator)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = startIndex; i < strings.length; i++)
		{
			if (i != startIndex)
				sb.append(separator);
			sb.append(strings[i]);
		}
		return sb.toString();
	}

	public static final Pattern allCapitalsToken = Pattern.compile("([^a-zà-ÿ]+([\\s\\']+|$))*");
	public static final Pattern asteriskTerminatedToken = Pattern.compile("([^\\'\\s]+)\\*|$");
	public static final Pattern phraseSplitter = Pattern.compile("$|[\\s\\']+");
	public static final Pattern asteriskIncludedToken = Pattern.compile("(\\*(.*?)\\*)|$");

	public static class Token
	{
		public final String word;
		public final boolean matchesCriteria;

		public Token(String word, boolean matchesCriteria)
		{
			super();
			this.word = word;
			this.matchesCriteria = matchesCriteria;
		}

	}
	
	public static String camelCase(String str)
	{
		StringBuilder sb = new StringBuilder(str);
		boolean isPreviousCharASpace = true;

		for (int i = 0; i < sb.length(); i++)
		{
			char ch = sb.charAt(i);
			if (isPreviousCharASpace)
			{
				sb.setCharAt(i, Character.toUpperCase(ch));
			} else
			{
				sb.setCharAt(i, Character.toLowerCase(ch));
			}

			isPreviousCharASpace = !(Character.isAlphabetic(ch) || ch=='\'');
		}

		return sb.toString();
	}

	private static Pattern sempreMinuscoloPattern = Pattern.compile(
			"(^|\\s+)("
			+ "E|O|"
			+ "Di|Del|Degli|Della|Delle|D'"
			+ "A|Ai|Al|Alla|Alle|Allo|Agli|"
			+ "Da|Dal|"
			+ "In|Nel|"
			+ "Con|Col|"
			+ "Su|Sul|Sulla|Sulle|Sugli|"
			+ "Per"
			+ ")($|\\s+)");
	private static Pattern sempreMaiuscoloPattern = Pattern.compile("(^|\\s+)(Doc|Igt|Docg|Igp|Aia)($|\\s+)");

	public static String smartCase(String str)
	{
		str = camelCase(str);

		Matcher m = sempreMinuscoloPattern.matcher(str);

		StringBuilder sb = new StringBuilder();
		int last = 0;
		while (m.find())
		{
			sb.append(str.substring(last, m.start()));
			sb.append(m.group(0).toLowerCase());
			last = m.end();
		}
		sb.append(str.substring(last));
		str = sb.toString();

		m = sempreMaiuscoloPattern.matcher(str);

		sb = new StringBuilder();
		last = 0;
		while (m.find())
		{
			sb.append(str.substring(last, m.start()));
			sb.append(m.group(0).toUpperCase());
			last = m.end();
		}
		sb.append(str.substring(last));
		return sb.toString();
	}


	public static void main(String args[])
	{
		String rigaText = "*Cacca* di *culo**";
		Matcher matcher = asteriskIncludedToken.matcher(rigaText);
		int start = 0;
		while (matcher.find())
		{
			String boldWord = matcher.group(2);
			if (boldWord==null)
				boldWord = "";
			System.out.print(rigaText.substring(start, matcher.start()) + boldWord.toUpperCase());
			start = matcher.end();
		}
	}
}
