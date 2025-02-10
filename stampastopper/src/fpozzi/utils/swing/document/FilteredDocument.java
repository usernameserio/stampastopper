package fpozzi.utils.swing.document;

import java.util.LinkedList;
import java.util.List;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

public class FilteredDocument extends DefaultStyledDocument
{

	private static final long serialVersionUID = 1L;
	
	protected final List<DocumentFilter> filters;

	public FilteredDocument()
	{
		this.filters = new LinkedList<DocumentFilter>();
	}

	public FilteredDocument(List<DocumentFilter> filters)
	{
		this.filters = filters;
	}

	public List<DocumentFilter> getFilters()
	{
		return filters;
	}

	@Override
	public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException
	{
		if (str == null || str.isEmpty())
			return;

		String insertedString = String.copyValueOf(str.toCharArray());

		for (DocumentFilter filter : filters)
		{
			insertedString = filter.filter(this, insertedString);
		}
		
		if (insertedString == null || insertedString.isEmpty())
			return;
		
		super.insertString(offset, insertedString, attr);
	}

}