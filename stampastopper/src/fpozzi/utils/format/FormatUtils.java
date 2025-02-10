package fpozzi.utils.format;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.JFormattedTextField.AbstractFormatter;

import net.sourceforge.jdatepicker.impl.DateComponentFormatter;
import fpozzi.utils.date.DateUtils;

public class FormatUtils
{

	static final public DecimalFormat twoDecimalDigitsFormat = new DecimalFormat("0.00");
	static final public DecimalFormat integerFormat = new DecimalFormat("0");

	static
	{
		twoDecimalDigitsFormat.setRoundingMode(RoundingMode.HALF_UP);
		integerFormat.setRoundingMode(RoundingMode.HALF_UP);
	}

	static final public DecimalFormat threeDecimalDigitsFormat = new DecimalFormat("0.000");

	static final public DecimalFormat optionalDecimalDigitsFormat = new DecimalFormat("0.###");

	static public double roundToDecimalDigits(double d, int decimalDigits)
	{
		return Math.round(((long) (d * Math.pow(10.0, decimalDigits + 1))) / 10.0) / Math.pow(10.0, decimalDigits);
	}

	static public void main(String args[])
	{
		// double d = 0.125;
		Double d = 99.9;

		System.out.println(twoDecimalDigitsFormat.format(d));
		System.out.println(roundToDecimalDigits(d, 2));
		System.out.println(roundToDecimalDigits(d, 0));

		try
		{
			System.out.println(integerFormat.getRoundingMode());
			System.out.println(integerFormat.parse("99.9"));
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static public final AbstractFormatter dateLabelFormatter = new DateComponentFormatter(DateUtils.italianDateFormat);
}
