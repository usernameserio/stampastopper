package fpozzi.utils.swing.document;

public abstract class NumbersOnlyDocument extends FilteredDocument
{

	private static final long serialVersionUID = 1L;

	public NumbersOnlyDocument(boolean allowNegatives, int length)
	{
		super();
		if (length>0)
			filters.add(new LengthLimitDocumentFilter(length));
		filters.add(makeNumberFilter(allowNegatives));
	}
	
	public NumbersOnlyDocument(boolean allowNegatives)
	{
		this(allowNegatives, 0);
	}
	
	protected abstract NumbersOnlyDocumentFilter makeNumberFilter(boolean allowNegatives);
	
}
