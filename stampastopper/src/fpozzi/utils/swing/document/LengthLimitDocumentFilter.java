package fpozzi.utils.swing.document;

import javax.swing.text.Document;

public class LengthLimitDocumentFilter implements DocumentFilter
{

	private final int limit;

	public LengthLimitDocumentFilter(int limit)
	{
		this.limit = limit;
	}
	
	@Override
	public String filter(Document document, String insertedString)
	{
		if ((document.getLength() + insertedString.length()) <= limit)
		{
			return insertedString;
		}
		else
			return insertedString.substring(0, limit-document.getLength());
	}

}
