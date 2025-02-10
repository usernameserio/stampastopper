package fpozzi.stopper.model.pdf;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fpozzi.gdoshop.model.articolo.Quantita;
import fpozzi.stopper.model.PrezzoPromo;
import fpozzi.stopper.model.PromoStopper;
import fpozzi.stopper.model.pdf.cell.PdfCellFactory;
import fpozzi.utils.StringUtils;

public abstract class PdfPromoStopper extends PdfStopper<PromoStopper>
{

	public PdfPromoStopper(PromoStopper stopper, PdfCellFactory<PromoStopper> factory)
	{
		super(stopper, factory);
	}

	@Override
	public boolean canBeMergedInto(PdfStopper<?> otherPdfStopper)
	{
		if (otherPdfStopper==null)
			return false;
		
		if (!otherPdfStopper.getClass().equals(this.getClass()))
			return false;
		
		PromoStopper otherPromoStopper = ((PdfPromoStopper) otherPdfStopper).getStopper();
		
		if (!getStopper().getPrezzoPromo().equals(otherPromoStopper.getPrezzoPromo()))
			return false;
		
		return (getStopper().getPeriodo()==null || getStopper().getPeriodo().equals(otherPromoStopper.getPeriodo()));
	}
	
	private static final Pattern descriptionSplitPattern = Pattern.compile("\\s*(\\w+)(\\W+|$)"); 

	@Override
	public void mergeInto(PdfStopper<?> otherStopper) throws MergeException
	{
		PromoStopper otherPromoStopper = (PromoStopper) otherStopper.getStopper();
		
		List<String> words, commonWords = new LinkedList<String>(), commonCleanWords = new LinkedList<String>();
		Matcher descriptionSplitPatternMatcher = descriptionSplitPattern.matcher(otherStopper.getDescrizione());
		while (descriptionSplitPatternMatcher.find())
		{
			commonWords.add(descriptionSplitPatternMatcher.group());
			commonCleanWords.add(descriptionSplitPatternMatcher.group(1).toLowerCase());
		}
		
		words = new LinkedList<String>(Arrays.asList(getDescrizione().toLowerCase().split("\\W+")));

		for (int e = 0, i = 0; i < commonCleanWords.size(); i++)
		{
			if (!words.contains(commonCleanWords.get(i)))
			{
				commonWords.remove(i-e++);
			}
		}		
		
		if (commonWords.isEmpty())
			throw new MergeException("Pare non ci siano parole comuni nelle descrizioni");
		
		String[] commonWordsArray =new String[commonWords.size()];
		commonWords.toArray(commonWordsArray);
		
		otherPromoStopper.setCodiceInterno(null);
		
		otherPromoStopper.setDescrizione(StringUtils.join(commonWordsArray, 0, ""));

		for (Quantita quantitaLoop : getQuantita())
			if (!otherStopper.getQuantita().contains(quantitaLoop))
				otherStopper.getQuantita().add(quantitaLoop);
				
	}

	
}
