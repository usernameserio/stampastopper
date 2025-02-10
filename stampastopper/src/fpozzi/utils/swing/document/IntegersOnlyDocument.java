package fpozzi.utils.swing.document;

public class IntegersOnlyDocument extends NumbersOnlyDocument
{

	private static final long serialVersionUID = 1L;

	public IntegersOnlyDocument(boolean allowNegatives, int length)
	{
		super(allowNegatives, length);
	}

	public IntegersOnlyDocument(boolean allowNegatives)
	{
		super(allowNegatives);
	}

	@Override
	protected NumbersOnlyDocumentFilter makeNumberFilter(boolean allowNegatives)
	{
		return new IntegersOnlyDocumentFilter(allowNegatives);
	}

}
