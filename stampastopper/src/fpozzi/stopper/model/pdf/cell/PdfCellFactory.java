package fpozzi.stopper.model.pdf.cell;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;

import fpozzi.stopper.model.Stopper;
import fpozzi.stopper.model.pdf.PdfStopperStyle;
import fpozzi.stopper.model.pdf.UnsupportedStyleException;
import fpozzi.utils.StringUtils;

public abstract class PdfCellFactory<S extends Stopper>
{
	protected final List<PdfStopperStyle> supportedStyles;

	protected PdfCellFactory(PdfStopperStyle[] supportedStyles)
	{
		this.supportedStyles = Arrays.asList(supportedStyles);
	}
	
	public List<PdfStopperStyle> getSupportedStyles()
	{
		return supportedStyles;
	}

	public boolean supportsStyle(PdfStopperStyle style)
	{
		for (PdfStopperStyle supportedStyle : supportedStyles)
			if (supportedStyle == style)
				return true;
		return false;
	}

	public abstract PdfPCell makePDF(S stopper, PdfStopperStyle style, PdfContentByte pdfContentByte) throws DocumentException, UnsupportedStyleException;

	protected Phrase makePhrase(String riga, Font normalFont, Font boldFont)
	{
		Phrase descriptionPhrase = new Phrase(30);

		descriptionPhrase.add(new Chunk(" ", boldFont));
		Matcher matcher = StringUtils.asteriskIncludedToken.matcher(riga);
		int nonBoldTokenStart = 0;
		while (matcher.find())
		{
			String nonBoldWord = riga.substring(nonBoldTokenStart, matcher.start());
			if (nonBoldWord.length() > 0)
				descriptionPhrase.add(new Chunk(nonBoldWord, normalFont).setCharacterSpacing(-0.3f));

			String boldWord = matcher.group(2);
			if (boldWord != null && boldWord.length() > 0)
				descriptionPhrase.add(new Chunk(boldWord, boldFont));

			nonBoldTokenStart = matcher.end();
		}
		descriptionPhrase.add(new Chunk("\n", boldFont));

		return descriptionPhrase;

	}

}
