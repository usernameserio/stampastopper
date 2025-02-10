package fpozzi.utils.swing;

import java.awt.Toolkit;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.utils.TimingUtils;

public class NonEmptyTextField extends VerifiedTextField<String>
{

	private static final long serialVersionUID = 1L;

	public NonEmptyTextField(String defaultValue)
	{
		super(defaultValue);
		setText(defaultValue);
	}

	public boolean validate(String input)
	{
		String errorBalloonMessage = null;

		if (input == null || input.trim().isEmpty())
		{
			errorBalloonMessage = "Non può essere lasciato vuoto";
		}
		else
		{
			currentValue = input;
		}
		
		setTextWithoutValidation(currentValue);
		
		if (errorBalloonMessage != null)
		{
			BalloonTip balloonTip = new BalloonTip(this, errorBalloonMessage);
			balloonTip.setCloseButton(null);;
			TimingUtils.showTimedBalloon(balloonTip, 3000);
			Toolkit.getDefaultToolkit().beep();
			return false;
		}

		return true;
	}

}