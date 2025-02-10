package fpozzi.utils.swing.document;

public class IntegersOnlyDocumentFilter extends NumbersOnlyDocumentFilter
{

	public IntegersOnlyDocumentFilter(boolean allowsNegatives)
	{
		super(allowsNegatives);
	}

	@Override
	protected boolean validateChar(char c)
	{
		return false;
	}

}
