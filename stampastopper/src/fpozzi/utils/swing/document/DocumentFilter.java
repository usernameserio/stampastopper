package fpozzi.utils.swing.document;

import javax.swing.text.Document;

public interface DocumentFilter
{

	public String filter(Document document, String insertedString);
	
}
