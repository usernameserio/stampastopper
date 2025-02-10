package fpozzi.utils.swing.document;

import javax.swing.text.Document;

public class AllCapsDocumentFilter implements DocumentFilter
{
	static public final AllCapsDocumentFilter instance = new AllCapsDocumentFilter();

	@Override
	public String filter(Document document, String insertedString)
	{
		return insertedString.toUpperCase();
	}

}
