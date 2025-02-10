package fpozzi.utils.swing.document;

public class LengthLimitDocument extends FilteredDocument
{

	private static final long serialVersionUID = 1L;

	public LengthLimitDocument(int length)
	{
		super();
		filters.add(new LengthLimitDocumentFilter(length));
	}

}
