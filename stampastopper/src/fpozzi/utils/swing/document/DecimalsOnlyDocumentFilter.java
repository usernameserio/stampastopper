package fpozzi.utils.swing.document;

public class DecimalsOnlyDocumentFilter extends NumbersOnlyDocumentFilter
{


	public DecimalsOnlyDocumentFilter(boolean allowsNegatives)
	{
		super(allowsNegatives);
	}

	@Override
	protected boolean validateChar(char c)
	{
		return c == '.' || c == ',';
	}

}
