package fpozzi.utils.swing;

import java.awt.Toolkit;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.utils.TimingUtils;
import fpozzi.utils.swing.document.FilteredDocument;
import fpozzi.utils.swing.document.IntegersOnlyDocumentFilter;
import fpozzi.utils.swing.document.LengthLimitDocument;

public class Ean13Field extends VerifiedTextField<String>
{

	private static final long serialVersionUID = 1L;

	public Ean13Field(String defaultValue)
	{
		super(defaultValue);
		FilteredDocument doc = new LengthLimitDocument(13);
		doc.getFilters().add(new IntegersOnlyDocumentFilter(false));
		setDocument(doc);
		setText(defaultValue);
		this.setColumns(13);
	}

	public boolean validate(String input)
	{
		String errorBalloonMessage = null;
		String warningBalloonMessage = null;

		if (input == null || input.trim().isEmpty())
		{
			if (defaultValue == null)
			{
				currentValue = null;
			}
			else
			{
				errorBalloonMessage = "Non può essere lasciato vuoto";
			}
		}
		else
		{
			if (input.length()<13)
			{
				String filledInput = input;
				while (filledInput.length()<13) filledInput += "0";
				int parityDigit = calculateEanParity(filledInput);
				currentValue = filledInput.substring(0, 12) + parityDigit;
				warningBalloonMessage = "Non sono state inserite tutte e 13 le cifre del codice";
			}
			else 
			{
				int parityDigit = calculateEanParity(input);
				if (parityDigit!=(input.charAt(12)-'0'))
				{
					currentValue = input.substring(0, 12) + parityDigit;
					warningBalloonMessage = "Verificare il codice";
				}
				else
					currentValue = input;
			}

		}

		setTextWithoutValidation(currentValue);
		
		if (errorBalloonMessage != null)
		{
			BalloonTip balloonTip = new BalloonTip(this, errorBalloonMessage);
			balloonTip.setCloseButton(null);
			TimingUtils.showTimedBalloon(balloonTip, 3000);
			Toolkit.getDefaultToolkit().beep();
			return false;
		}
		
		if (warningBalloonMessage!=null)
		{
			BalloonTip balloonTip = new BalloonTip(this, warningBalloonMessage);
			balloonTip.setCloseButton(null);;
			TimingUtils.showTimedBalloon(balloonTip, 3000);
			Toolkit.getDefaultToolkit().beep();
		}


		return true;
	}

	public static int calculateEanParity(String ean)
	{
		char[] eanNoParity = ean.substring(0, ean.length() - 1).toCharArray();

		int sum = 0;
		for (int i = eanNoParity.length; i > 0; i--)
		{
			int digit = eanNoParity[i - 1] - '0';

			if (i % 2 == 0)
			{ // odd
				sum += digit * 3;
			}
			else
			{ // even
				sum += digit;
			}
		}
		return (10 - (sum % 10)) % 10;
	}

}