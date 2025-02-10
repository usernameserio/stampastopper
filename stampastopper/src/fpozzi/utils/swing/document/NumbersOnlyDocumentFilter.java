package fpozzi.utils.swing.document;

import javax.swing.text.Document;

public abstract class NumbersOnlyDocumentFilter implements DocumentFilter
{

	private final boolean allowsNegatives;

	public NumbersOnlyDocumentFilter(boolean allowsNegatives)
	{
		this.allowsNegatives = allowsNegatives;
	}

	@Override
	public String filter(Document document, String insertedString)
	{
		char[] cArray = insertedString.toCharArray();
		char c;
		boolean invalidCharFound = false;
		for (int i = 0; !invalidCharFound && i < cArray.length; i++)
		{
			c = cArray[i];
			invalidCharFound = !((c >= '0' && c <= '9') || (c == '-' && allowsNegatives) || validateChar(c));
		}
		if (invalidCharFound)
			return "";
		else
			return insertedString;
	}

	protected abstract boolean validateChar(char c);
}
