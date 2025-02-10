package fpozzi.utils.swing;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

public abstract class VerifiedTextField<T> extends JTextField
{
	private static final long serialVersionUID = 1L;

	private final InputVerifier inputVerifier;

	protected T defaultValue, currentValue;

	public VerifiedTextField(T defaultValue)
	{
		this.defaultValue = defaultValue;
		this.currentValue = defaultValue;
		inputVerifier = new InputVerifier()
		{
			@Override
			public boolean verify(JComponent input)
			{
				return validate(getText());
			}
		};
		this.setInputVerifier(inputVerifier);
	}

	@Override
	public void setInputVerifier(InputVerifier inputVerifier)
	{
		super.setInputVerifier(this.inputVerifier);
	}

	public T getDefaultValue()
	{
		return defaultValue;
	}

	public void setDefaultValue(T defaultValue)
	{
		this.defaultValue = defaultValue;
	}

	@Override
	public void setText(String text)
	{
		validate(text);
	}

	protected void setTextWithoutValidation(String text)
	{
		super.setText(text);
	}

	public T getValue()
	{
		return currentValue;
	}

	public void setValue(T value)
	{
		setText(value == null ? null : value + "");
	}

	public void reset()
	{
		super.setText(defaultValue == null ? null : defaultValue + "");
		currentValue = defaultValue;
	}

	protected abstract boolean validate(String text);
}
