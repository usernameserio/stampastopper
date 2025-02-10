package fpozzi.stopper.view.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.JTextComponent;

import fpozzi.utils.StringUtils;

class QuickTextEditPopupMenu extends JPopupMenu implements ActionListener, CaretListener
{
	private static final long serialVersionUID = 1L;

	private final JMenuItem boldItem, ucaseItem, lcaseItem, fucaseItem, ccaseItem, scaseItem;
	private final JTextComponent field;

	private boolean caseItemsEnabled = false;

	public QuickTextEditPopupMenu(JTextComponent field)
	{
		this.field = field;

		field.addCaretListener(this);

		boldItem = new JMenuItem("Neretto", KeyEvent.VK_B);
		boldItem.setIcon(Icons.TEXT_BOLD.image);
		boldItem.addActionListener(this);
		boldItem.setEnabled(false);
		this.add(boldItem);

		ucaseItem = new JMenuItem("MAIUSCOLO", KeyEvent.VK_U);
		ucaseItem.setIcon(Icons.TEXT_UPPERCASE.image);
		ucaseItem.addActionListener(this);
		ucaseItem.setEnabled(false);
		this.add(ucaseItem);

		lcaseItem = new JMenuItem("minuscolo", KeyEvent.VK_L);
		lcaseItem.setIcon(Icons.TEXT_LOWERCASE.image);
		lcaseItem.addActionListener(this);
		lcaseItem.setEnabled(false);
		this.add(lcaseItem);

		fucaseItem = new JMenuItem("Prima lettera maiuscola", KeyEvent.VK_F);
		// lcaseItem.setIcon(Icons..image);
		fucaseItem.addActionListener(this);
		fucaseItem.setEnabled(false);
		this.add(fucaseItem);

		ccaseItem = new JMenuItem("Prime Lettere Maiuscole", KeyEvent.VK_C);
		// lcaseItem.setIcon(Icons..image);
		ccaseItem.addActionListener(this);
		ccaseItem.setEnabled(false);
		this.add(ccaseItem);

		scaseItem = new JMenuItem("Smartcase", KeyEvent.VK_C);
		// lcaseItem.setIcon(Icons..image);
		scaseItem.addActionListener(this);
		scaseItem.setEnabled(false);
		this.add(scaseItem);
	}

	@Override
	public void actionPerformed(ActionEvent evt)
	{
		int selStart = field.getSelectionStart(), selEnd = field.getSelectionEnd();
		String text = field.getText();
		String a, b, c;
		a = text.substring(0, selStart);
		b = text.substring(selStart, selEnd);
		c = text.substring(selEnd, text.length());

		if (evt.getSource() == boldItem)
		{
			b = "*" + b + "*";
			selEnd = selEnd + 2;
		} else if (evt.getSource() == ucaseItem)
		{
			b = b.toUpperCase();
		} else if (evt.getSource() == lcaseItem)
		{
			b = b.toLowerCase();
		} else if (evt.getSource() == fucaseItem)
		{
			b = b.substring(0, 1).toUpperCase() + b.substring(1, b.length()).toLowerCase();
		} else if (evt.getSource() == ccaseItem)
		{
			b = StringUtils.camelCase(b);
		} else if (evt.getSource() == scaseItem)
		{
			b = StringUtils.smartCase(b);
		}

		field.setText(a + b + c);
		field.setSelectionStart(selStart);
		field.setSelectionEnd(selEnd);
	}

	@Override
	public void caretUpdate(CaretEvent arg0)
	{
		if (field.getSelectedText() == null && caseItemsEnabled)
		{
			ucaseItem.setEnabled(false);
			lcaseItem.setEnabled(false);
			ccaseItem.setEnabled(false);
			fucaseItem.setEnabled(false);
			scaseItem.setEnabled(false);
			boldItem.setEnabled(false);
			caseItemsEnabled = false;
		} else if (field.getSelectedText() != null && !caseItemsEnabled)
		{
			ucaseItem.setEnabled(true);
			lcaseItem.setEnabled(true);
			boldItem.setEnabled(true);
			ccaseItem.setEnabled(true);
			scaseItem.setEnabled(true);
			fucaseItem.setEnabled(true);
			caseItemsEnabled = true;
		}
	}
}