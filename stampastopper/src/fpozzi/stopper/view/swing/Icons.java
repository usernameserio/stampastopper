package fpozzi.stopper.view.swing;

import javax.swing.ImageIcon;

public enum Icons {

	PENCIL, EYE, PAGE_PASTE, WIZARD, WIZARD_SMALL("wizard_16.png"), DUPLICA("application_double.png"), COPIA("page_copy.png"), 
	CROSS, JOIN("arrow_join.png"), STYLE_EDIT, ZERO_COPIES, CALENDAR_EDIT, CALENDAR_DELETE, ADD_RED, ADD, ADD_YELLOW, ADD_BLUE, 
	NEW, CALENDAR, SCANNING, CLOSE_TAB("cross_small_gray.png"), FIND("magnifier.png"), TICK, TEXT_UPPERCASE, TEXT_LOWERCASE,
	TEXT_BOLD, TAB_ADD, ARROW_SELECT, GDOSHOP;

	final private static String folder = "/img/";

	final public ImageIcon image;

	private Icons(String imageName)
	{
		image = new ImageIcon(getClass().getResource(folder + imageName));
	}

	private Icons()
	{
		image = new ImageIcon(getClass().getResource(folder + this.name().toLowerCase() + ".png"));
	}

}
