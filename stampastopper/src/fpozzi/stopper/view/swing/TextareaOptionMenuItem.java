package fpozzi.stopper.view.swing;

import javax.swing.text.JTextComponent;

import fpozzi.utils.swing.OptionMenuItem;

public class TextareaOptionMenuItem extends OptionMenuItem
{

	public TextareaOptionMenuItem(String optionText, JTextComponent targetField)
	{
		super(optionText, Icons.PENCIL.image, targetField);
		setText(optionText);
	}

}