package fpozzi.utils.swing.document;

public class DecimalsOnlyDocument extends NumbersOnlyDocument
{

	private static final long serialVersionUID = 1L;

	public DecimalsOnlyDocument(boolean allowNegatives, int length)
	{
		super(allowNegatives, length);
	}

	public DecimalsOnlyDocument(boolean allowNegatives)
	{
		super(allowNegatives);
	}

	@Override
	protected NumbersOnlyDocumentFilter makeNumberFilter(boolean allowNegatives)
	{
		return new DecimalsOnlyDocumentFilter(allowNegatives);
	}
	
}
