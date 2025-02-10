package fpozzi.stopper.model.pdf.cell;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;

import fpozzi.gdoshop.model.articolo.Quantita;
import fpozzi.gdoshop.model.articolo.UnitaMisura;
import fpozzi.stopper.model.ArticoloStopper;
import fpozzi.stopper.model.pdf.PdfStopperStyle;
import fpozzi.utils.format.FormatUtils;

public abstract class ArticoloPdfCellFactory<S extends ArticoloStopper> extends PdfCellFactory<S>
{
	protected ArticoloPdfCellFactory(PdfStopperStyle[] supportedStyles)
	{
		super(supportedStyles);
	}
	
	protected Phrase makePrezzoPhrase(S stopper, Font cPartFont, Font iPartFont, Font fPartFont)
	{
		return makePricePhrase(stopper.getPrezzo().getValue(), cPartFont, iPartFont, fPartFont);
	}

	protected static Phrase makePricePhrase(double prezzo, Font cPartFont,Font iPartFont, Font fPartFont)
	{
		Phrase pricePhrase = new Phrase(3);
		pricePhrase.add(new Chunk("€", cPartFont));
		
		BigDecimal prezzoBD = new BigDecimal(prezzo+"").setScale(2, RoundingMode.HALF_UP);
		String[] prezzoParts = prezzoBD.toPlainString().split("\\.");

		pricePhrase.add(new Chunk((prezzoParts[0].length() <2 ? " " : "") + prezzoParts[0], iPartFont).setCharacterSpacing(-1f));
		pricePhrase.add(new Chunk("," + prezzoParts[1], fPartFont));
		return pricePhrase;
	}
	
	protected Chunk makePrezzoPerUMChunk(List<Quantita> quantita, double prezzo, Font font)
	{
		String prezzoPerUMText = "";

		prezzoPerUMText = "";
		UnitaMisura umPrec = null;
		
		for (Quantita qta : quantita)
		{
			if (!	(qta.getUnitaMisura()==UnitaMisura.PZ && qta.getValore()==0)  && 
					!(qta.getUnitaMisura()==qta.getUnitaMisura().getNormalizzata() && (qta.getValore()==null ||
							qta.getValore()==1 || qta.getValore()==0)))
			{
				if (!prezzoPerUMText.equals(""))
					prezzoPerUMText += "/";
				if (qta.getUnitaMisura().getNormalizzata() != umPrec)
					prezzoPerUMText += "al " + qta.getUnitaMisura().getNormalizzata().getAbbrSing() + " € ";
				Quantita qtaCopy = qta;
				if (qta.getValore()==null)
					qtaCopy = new Quantita(qta.getUnitaMisura(), 1d);
				prezzoPerUMText += FormatUtils.twoDecimalDigitsFormat.format(
						FormatUtils.roundToDecimalDigits(Quantita.getPrezzoPerUM(qtaCopy, prezzo), 2));
				umPrec = qta.getUnitaMisura().getNormalizzata();
			}
		}
		
		return new Chunk(prezzoPerUMText, font);
	}
	
	protected Phrase makeQuantitaPhrase(List<Quantita> quantita, Font qtaFont)
	{

			Phrase quantitaPhrase = new Phrase();
			String qtaStr = "";
			UnitaMisura umPrec=null;
			for (int g=0; g<quantita.size(); g++)
			{
				if (!(quantita.get(g).getUnitaMisura()==UnitaMisura.PZ && quantita.get(g).getValore()==0))
				{
					if (!qtaStr.isEmpty())
						qtaStr += "/";
					if (quantita.get(g).getUnitaMisura()!=umPrec)
						qtaStr += quantita.get(g).getUnitaMisura().getAbbrPlur() + " ";
					qtaStr += FormatUtils.optionalDecimalDigitsFormat.format(quantita.get(g).getValore()==null? 1 : quantita.get(g).getValore());
					if (quantita.get(g).getMoltiplicatore()!=1)
						qtaStr += "x" + quantita.get(g).getMoltiplicatore();
					umPrec = quantita.get(g).getUnitaMisura();
				}
			}
			quantitaPhrase.add(new Chunk(qtaStr, qtaFont).setCharacterSpacing(-0.5f));

			return quantitaPhrase;
	}
	
	protected static boolean vuoleDoppia(int dayOfMonth)
	{
		return (dayOfMonth == 1 || dayOfMonth == 8 || dayOfMonth == 11);
	}
	
	protected static boolean vuoleDoppia(UnitaMisura um)
	{
		return (um == UnitaMisura.HG);
	}

}
