	package fpozzi.stopper.view.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import fpozzi.gdoshop.model.articolo.Quantita;
import fpozzi.gdoshop.model.articolo.UnitaMisura;
import fpozzi.stopper.model.GastronomiaStopper;
import fpozzi.stopper.model.Prezzo;
import fpozzi.stopper.model.pdf.GastroPdfStopper;
import fpozzi.utils.Range;
import fpozzi.utils.StringUtils;
import fpozzi.utils.format.FormatUtils;
import fpozzi.utils.swing.JFormattedNumberField;
import fpozzi.utils.swing.SelectAllFocusAdapter;
import fpozzi.utils.swing.document.DecimalsOnlyDocument;
import fpozzi.utils.swing.document.FilteredDocument;
import fpozzi.utils.swing.document.LengthLimitDocument;

public class GastroStopperEditorPanel extends StopperEditorPanel<GastroPdfStopper>
{

	private static final long serialVersionUID = 1L;

	private final QuantitaEditorPanel qtaPanel;
	private final JTextField rigaDescFields[];
	private final JTextArea ingredientiField, noteField;
	private final JFormattedNumberField prezzoField;

	private final JPopupMenu ingrMenu, noteMenu;

	static final String formLayoutColumns[] = new String[] { "right:50dlu, 3dlu, ", "right:40dlu, 3dlu, ", "right:40dlu, 3dlu, ",
			"right:max(pref;20dlu)" };

	static final FormLayout defaultFormLayout = new FormLayout(StringUtils.join(formLayoutColumns, 0, ""), "");;

	private final JPanel formPanel;

	public GastroStopperEditorPanel()
	{
		super(new BorderLayout());

		JLabel label;

		formPanel = new JPanel();
		formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
		formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel columnFormPanel;

		DefaultFormBuilder builder;

		builder = new DefaultFormBuilder(defaultFormLayout);
		builder.setDefaultDialogBorder();

		label = (new JLabel("Descrizione"));
		builder.append(label);
		builder.nextLine();

		rigaDescFields = new JTextField[2];
		for (int i = 1; i <= rigaDescFields.length; i++)
		{
			label = (new JLabel("riga " + i + " "));
			label.setFont(Style.Fonts.italicFont);
			label.setForeground(Color.GRAY);
			builder.append(label);
			rigaDescFields[i - 1] = new JTextField(30);
			FilteredDocument doc = new LengthLimitDocument(30);
			//doc.getFilters().add(AllCapsDocumentFilter.instance);
			rigaDescFields[i - 1].setDocument(doc);
			rigaDescFields[i - 1].addFocusListener(new SelectAllFocusAdapter(rigaDescFields[i - 1]));
			rigaDescFields[i - 1].setComponentPopupMenu(new QuickTextEditPopupMenu(rigaDescFields[i - 1]));
			builder.append(rigaDescFields[i - 1], 5);
			builder.nextLine();
		}

		builder.appendSeparator();

		builder.append(new JLabel("Quantità"));
		label = new JLabel("UdM");
		label.setFont(Style.Fonts.noteFont);
		label.setForeground(Color.DARK_GRAY);
		builder.append(label);
		label = new JLabel("Valore");
		label.setFont(Style.Fonts.noteFont);
		label.setForeground(Color.DARK_GRAY);
		builder.append(label);
		label = new JLabel("Molt.");
		label.setFont(Style.Fonts.noteFont);
		label.setForeground(Color.DARK_GRAY);
		builder.append(label);
		builder.nextLine();

		String[] gramDescs = new String[UnitaMisura.values().length + 1];
		gramDescs[0] = "";
		for (UnitaMisura udm : UnitaMisura.values())
			gramDescs[udm.ordinal() + 1] = udm.toString();

		qtaPanel = new QuantitaEditorPanel(builder, QuantitaEditorPanel.fullUdmOptions, "");

		builder.appendSeparator();

		builder.append(new JLabel("Prezzo"));
		prezzoField = new JFormattedNumberField(FormatUtils.twoDecimalDigitsFormat, null, new Range(0, false, 100, false));
		prezzoField.setDocument(new DecimalsOnlyDocument(false, 9));
		prezzoField.setColumns(6);
		prezzoField.setHorizontalAlignment(JTextField.RIGHT);
		prezzoField.addFocusListener(new SelectAllFocusAdapter(prezzoField));

		builder.append(prezzoField);
		builder.nextLine();
		
		builder.appendSeparator();

		builder.append(new JLabel(""));
		JPanel descrNotePanel = new JPanel();
		label = new JLabel("*allergene*   -> ");
		label.setFont(Style.Fonts.noteFont);
		label.setForeground(Color.DARK_GRAY);
		descrNotePanel.add(label);
		label = new JLabel("allergene");
		label.setFont(Style.Fonts.noteBoldFont);
		label.setForeground(Color.DARK_GRAY);
		descrNotePanel.add(label);
		builder.append(descrNotePanel, 5);
		builder.nextLine();

		label = (new JLabel("Ingredienti"));
		label.setAlignmentY(TOP_ALIGNMENT);
		builder.append(label);
		ingredientiField = new JTextArea();
		ingredientiField.setFont(Style.Fonts.defaultFont);
		ingredientiField.setRows(4);
		ingredientiField.setLineWrap(true);
		ingredientiField.setWrapStyleWord(true);
		ingredientiField.setBorder(prezzoField.getBorder());
		ingrMenu = new JPopupMenu();
		ingrMenu.add(new TextareaOptionMenuItem("*latte* vaccino, sale, caglio", ingredientiField));
		ingrMenu.add(new TextareaOptionMenuItem("*latte* caprino, sale, caglio", ingredientiField));
		ingrMenu.add(new TextareaOptionMenuItem("*latte* ovino, sale, caglio", ingredientiField));
		ingrMenu.add(new TextareaOptionMenuItem("vedere libro degli ingredienti", ingredientiField));
		ingrMenu.add(new TextareaOptionMenuItem("sulla confezione", ingredientiField));
		ingredientiField.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mousePressed(MouseEvent me)
			{
				if (SwingUtilities.isRightMouseButton(me))
				{
					ingrMenu.show(me.getComponent(), me.getX(), me.getY());
				}
			}

		});
		builder.append(ingredientiField, 5);

		builder.nextLine();

		label = (new JLabel("Note"));
		builder.append(label);
		noteField = new JTextArea();
		noteField.setFont(Style.Fonts.defaultFont);
		noteField.setRows(4);
		noteField.setLineWrap(true);
		noteField.setWrapStyleWord(true);
		noteField.setBorder(prezzoField.getBorder());
		noteMenu = new JPopupMenu();
		noteField.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mousePressed(MouseEvent me)
			{
				if (SwingUtilities.isRightMouseButton(me))
				{
					if (noteMenu.getSubElements().length > 0)
						noteMenu.show(me.getComponent(), me.getX(), me.getY());
				}
			}

		});
		builder.append(noteField, 5);

		builder.nextLine();

		columnFormPanel = builder.getPanel();
		columnFormPanel.setBorder(BorderFactory.createEmptyBorder());
		formPanel.add(columnFormPanel);

		JScrollPane scrollPane = new JScrollPane(formPanel);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		this.add(scrollPane, BorderLayout.CENTER);

		this.setPreferredSize(new Dimension(270, this.getPreferredSize().height));

	}

	@Override
	public GastroPdfStopper getPdfStopper()
	{
		GastroPdfStopper pdfStopper = null;

		List<String> righeDesc = new ArrayList<String>(2);
		String rigaDesc;
		for (JTextField rigaDescField : rigaDescFields)
		{
			rigaDesc = rigaDescField.getText();
			if (rigaDesc != null && !rigaDesc.isEmpty())
				righeDesc.add(rigaDescField.getText());
		}

		Quantita qta = qtaPanel.makeGrammatura();

		Prezzo prezzo = null;
		Double prezzoValue = prezzoField.getValue();
		if (prezzoValue != null)
			prezzo = new Prezzo(prezzoValue);

		String ingredienti = ingredientiField.getText();
		if (ingredienti.isEmpty())
			ingredienti = null;

		String note = noteField.getText();
		if (note.isEmpty())
			note = null;

		GastronomiaStopper stopper = new GastronomiaStopper();
		stopper.getRigheDescrizione().addAll(righeDesc);
		stopper.getQuantita().add(qta);
		stopper.setPrezzo(prezzo);
		stopper.setIngredienti(ingredienti);
		stopper.setNote(note);

		pdfStopper = new GastroPdfStopper(stopper);

		return pdfStopper;
	}

	@Override
	public void toForm(GastroPdfStopper pdfStopper)
	{
		for (JTextField rigaField : rigaDescFields)
			rigaField.setText(null);
		qtaPanel.setQuantita(null);
		prezzoField.setText(null);
		ingredientiField.setText(null);
		noteField.setText(null);

		if (pdfStopper != null)
		{
			GastronomiaStopper stopper = pdfStopper.getStopper();

			for (int i = 0; i < stopper.getRigheDescrizione().size() && i < rigaDescFields.length; i++)
				rigaDescFields[i].setText(stopper.getRigheDescrizione().get(i));

			if (stopper.getQuantita().size() > 0)
				qtaPanel.setQuantita(stopper.getQuantita().get(0));

			if (stopper.getPrezzo() != null && stopper.getPrezzo().getValue() > 0)
				prezzoField.setValue(stopper.getPrezzo().getValue());

			ingredientiField.setText(stopper.getIngredienti());
			noteField.setText(stopper.getNote());
		}

	}

	class RigaDescrizionePopupMenu extends JPopupMenu implements ActionListener, CaretListener
	{
		private static final long serialVersionUID = 1L;

		private final JMenuItem ucaseItem, lcaseItem;
		private final JTextField field;

		private boolean caseItemsEnabled = false;

		public RigaDescrizionePopupMenu(JTextField field)
		{
			this.field = field;

			field.addCaretListener(this);

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

			if (evt.getSource() == ucaseItem)
			{
				b = b.toUpperCase();
			}
			else if (evt.getSource() == lcaseItem)
			{
				b = b.toLowerCase();
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
				caseItemsEnabled = false;
			}
			else if (field.getSelectedText() != null && !caseItemsEnabled)
			{
				ucaseItem.setEnabled(true);
				lcaseItem.setEnabled(true);
				caseItemsEnabled = true;
			}
		}
	}

}
