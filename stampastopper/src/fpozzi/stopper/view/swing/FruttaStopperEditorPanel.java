package fpozzi.stopper.view.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import fpozzi.gdoshop.model.articolo.Quantita;
import fpozzi.gdoshop.model.articolo.UnitaMisura;
import fpozzi.stopper.model.FruttaStopper;
import fpozzi.stopper.model.FruttaStopper.Categoria;
import fpozzi.stopper.model.Prezzo;
import fpozzi.stopper.model.pdf.FruttaPdfStopper;
import fpozzi.utils.Range;
import fpozzi.utils.StringUtils;
import fpozzi.utils.format.FormatUtils;
import fpozzi.utils.swing.JFormattedNumberField;
import fpozzi.utils.swing.NonEmptyTextField;
import fpozzi.utils.swing.SelectAllFocusAdapter;
import fpozzi.utils.swing.document.AllCapsDocumentFilter;
import fpozzi.utils.swing.document.DecimalsOnlyDocument;
import fpozzi.utils.swing.document.FilteredDocument;
import fpozzi.utils.swing.document.IntegersOnlyDocument;
import fpozzi.utils.swing.document.LengthLimitDocument;

public class FruttaStopperEditorPanel extends StopperEditorPanel<FruttaPdfStopper>
{

	private static final long serialVersionUID = 1L;

	private static final String[] paesiFrutta = new String[] { "Italia", "Spagna", "Paesi Bassi", "Francia", "Germania", "Turchia", 
			"Israele", "Marocco", "Sud Africa", "Costa Rica", "Ecuador", "Argentina", "Cile", "Brasile", "N.Zelanda" };

	private static final String[] additiviOptions = new String[] { "BUCCIA EDIBILE",
			"TRATTATO IN SUPERFICIE CON E904, E914" };

	private final QuantitaEditorPanel qtaPanel;
	private final JTextField rigaDescFields[], calibroField;
	private final JTextArea additiviField;
	private final NonEmptyTextField origineField;
	private final JFormattedNumberField prezzoField, tastoField;
	private final JComboBox<Categoria> categoriaField;
	private final JPopupMenu origineMenu, additiviMenu;

	static final String formLayoutColumns[] = new String[] { "right:50dlu, 3dlu, ", "right:40dlu, 3dlu, ",
			"right:40dlu, 3dlu, ", "right:max(pref;20dlu)" };

	static final FormLayout defaultFormLayout = new FormLayout(StringUtils.join(formLayoutColumns, 0, ""), "");
	static final FormLayout fruttaFormLayout = new FormLayout(StringUtils.join(formLayoutColumns, 1, ""), "");

	private final JPanel formPanel;

	public FruttaStopperEditorPanel()
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
			doc.getFilters().add(AllCapsDocumentFilter.instance);
			rigaDescFields[i - 1].setDocument(doc);
			rigaDescFields[i - 1].addFocusListener(new SelectAllFocusAdapter(rigaDescFields[i - 1]));
			builder.append(rigaDescFields[i - 1], 5);
			builder.nextLine();
		}

		builder.appendSeparator();

		builder.append(new JLabel(""));
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

		qtaPanel = new QuantitaEditorPanel(builder, QuantitaEditorPanel.weightsOnlyUdmOptions, "Quantità");

		builder.appendSeparator();

		builder.append(new JLabel("Prezzo"));
		prezzoField = new JFormattedNumberField(FormatUtils.twoDecimalDigitsFormat, null,
				new Range(0, false, 100, false));
		prezzoField.setDocument(new DecimalsOnlyDocument(false, 9));
		prezzoField.setColumns(6);
		prezzoField.setHorizontalAlignment(JTextField.RIGHT);
		prezzoField.addFocusListener(new SelectAllFocusAdapter(prezzoField));

		builder.append(prezzoField);
		builder.nextLine();

		builder.appendSeparator();

		builder.append(new JLabel("Tasto"));
		tastoField = new JFormattedNumberField(FormatUtils.integerFormat, null, new Range(0, false, 1000, false));
		tastoField.setDocument(new IntegersOnlyDocument(false, 3));
		tastoField.setColumns(3);
		tastoField.setHorizontalAlignment(JTextField.RIGHT);
		tastoField.addFocusListener(new SelectAllFocusAdapter(tastoField));
		builder.append(tastoField);

		builder.nextLine();

		builder.append(new JLabel("Origine"));
		origineField = new NonEmptyTextField("Italia");
		FilteredDocument doc = new LengthLimitDocument(30);
		origineField.setDocument(doc);
		origineMenu = new JPopupMenu();
		for (String paese : paesiFrutta)
		{
			origineMenu.add(new TextareaOptionMenuItem(paese, origineField));
		}
		origineField.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mousePressed(MouseEvent me)
			{
				if (SwingUtilities.isRightMouseButton(me))
				{
					origineMenu.show(me.getComponent(), me.getX(), me.getY());
				}
			}

		});
		builder.append(origineField, 3);

		builder.nextLine();

		builder.append(new JLabel("Calibro"));
		calibroField = new JTextField(6);
		calibroField.setDocument(new LengthLimitDocument(6));
		calibroField.setColumns(6);
		calibroField.setHorizontalAlignment(JTextField.RIGHT);
		calibroField.addFocusListener(new SelectAllFocusAdapter(calibroField));
		builder.append(calibroField);

		builder.nextLine();

		builder.append(new JLabel("Categoria"));
		categoriaField = new JComboBox<Categoria>(new Categoria[] { Categoria.II, Categoria.I, Categoria.EXTRA });
		builder.append(categoriaField);

		builder.nextLine();

		builder.append(new JLabel("Note"));
		additiviField = new JTextArea();
		additiviField.setFont(Style.Fonts.defaultFont);
		additiviField.setRows(1);
		additiviField.setLineWrap(true);
		additiviField.setWrapStyleWord(true);
		additiviField.setBorder(prezzoField.getBorder());
		doc = new LengthLimitDocument(50);
		additiviField.setDocument(doc);
		additiviMenu = new JPopupMenu();
		for (String additiviOption : additiviOptions)
		{
			additiviMenu.add(new TextareaOptionMenuItem(additiviOption, additiviField));
		}
		additiviField.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mousePressed(MouseEvent me)
			{
				if (SwingUtilities.isRightMouseButton(me))
				{
					additiviMenu.show(me.getComponent(), me.getX(), me.getY());
				}
			}

		});
		builder.append(additiviField, 3);

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
	public FruttaPdfStopper getPdfStopper()
	{
		FruttaPdfStopper pdfStopper = null;

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

		FruttaStopper stopper = new FruttaStopper();
		stopper.getRigheDescrizione().addAll(righeDesc);
		stopper.getQuantita().add(qta);
		stopper.setPrezzo(prezzo);
		stopper.setCalibro(calibroField.getText());
		stopper.setCategoria(categoriaField.getItemAt(categoriaField.getSelectedIndex()));
		stopper.setOrigine(origineField.getText());
		stopper.setTasto(tastoField.getValue() != null ? tastoField.getValue().intValue() : null);
		stopper.setAdditivi(additiviField.getText());

		pdfStopper = new FruttaPdfStopper(stopper);

		return pdfStopper;
	}

	@Override
	public void toForm(FruttaPdfStopper pdfStopper)
	{
		for (JTextField rigaField : rigaDescFields)
			rigaField.setText(null);
		qtaPanel.setQuantita(null);
		prezzoField.setText(null);
		tastoField.setText(null);
		calibroField.setText(null);
		origineField.reset();
		additiviField.setText(null);

		if (pdfStopper != null)
		{
			FruttaStopper stopper = pdfStopper.getStopper();

			for (int i = 0; i < stopper.getRigheDescrizione().size() && i < rigaDescFields.length; i++)
				rigaDescFields[i].setText(stopper.getRigheDescrizione().get(i));

			if (stopper.getCalibro() != null)
				calibroField.setText(stopper.getCalibro());

			if (stopper.getQuantita().size() > 0)
				qtaPanel.setQuantita(stopper.getQuantita().get(0));

			if (stopper.getPrezzo() != null && stopper.getPrezzo().getValue() > 0)
				prezzoField.setValue(stopper.getPrezzo().getValue());

			if (stopper.getTasto() != null)
				tastoField.setValue(stopper.getTasto().doubleValue());

			if (stopper.getAdditivi() != null)
				additiviField.setText(stopper.getAdditivi());

			categoriaField.setSelectedItem(stopper.getCategoria());

			origineField.setText(stopper.getOrigine());
		}

	}

}
