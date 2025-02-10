package fpozzi.utils.swing;

import java.awt.Toolkit;
import java.text.DecimalFormat;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.utils.TimingUtils;
import fpozzi.utils.Range;
import fpozzi.utils.format.FormatUtils;

public class JFormattedNumberField extends VerifiedTextField<Double>
{
	private static final long serialVersionUID = 1L;
	
	private DecimalFormat formatter;
	private Range range;

	public JFormattedNumberField(DecimalFormat formatter, Double defaultValue, Range range)
	{
		super(defaultValue);
		this.formatter = formatter;
		this.range = range;
	}

	public JFormattedNumberField(DecimalFormat formatter, double defaultValue)
	{
		this(formatter, defaultValue, new Range(Double.MIN_VALUE, true, Double.MAX_VALUE, true));
	}

	@Override
	public void setValue(Double d)
	{
		setText(d == null ? null : d.toString());
	}

	public boolean validate(String input)
	{
		String balloonMsg = null;

		if (input == null || input.trim().isEmpty())
		{
			if (defaultValue == null)
			{
				currentValue = null;
			}
			else
			{
				balloonMsg = "Non può essere lasciato vuoto";
			}
		}
		else
		{
			input = input.replace(',', '.');
			try
			{
				double currentValue = FormatUtils.roundToDecimalDigits(Double.parseDouble(input), formatter.getMaximumFractionDigits());
				if (range.contains(currentValue))
				{
					this.currentValue = currentValue;
				}
				else
				{
					balloonMsg = "Deve essere >" + (range.isMinInclusive() ? "=" : "")
							+ FormatUtils.optionalDecimalDigitsFormat.format(range.getMin())
							+ " e <" + (range.isMaxInclusive() ? "=" : "")
							+ FormatUtils.optionalDecimalDigitsFormat.format(range.getMax());
				}
			}
			catch (Exception e)
			{
				balloonMsg = "Valore immesso non valido";
			}
		}

		setTextWithoutValidation(currentValue == null ? "" : formatter.format(currentValue));

		if (balloonMsg != null)
		{
			BalloonTip balloonTip = new BalloonTip(this, balloonMsg);
			balloonTip.setCloseButton(null);;
			TimingUtils.showTimedBalloon(balloonTip, 3000);
			Toolkit.getDefaultToolkit().beep();
			return false;
		}

		return true;
	}

}