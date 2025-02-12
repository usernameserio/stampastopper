package fpozzi.stopper.view.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.positioners.RightBelowPositioner;
import net.java.balloontip.utils.TimingUtils;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import fpozzi.gdoshop.model.articolo.CodiceInterno;
import fpozzi.gdoshop.model.articolo.Quantita;
import fpozzi.gdoshop.model.articolo.UnitaMisura;
import fpozzi.gdoshop.model.offerta.PeriodoOfferta;
import fpozzi.stopper.model.Prezzo;
import fpozzi.stopper.model.PrezzoPromo;
import fpozzi.stopper.model.PromoStopper;
import fpozzi.stopper.model.pdf.PdfPromoStopper;
import fpozzi.stopper.model.pdf.ScontoPercentualePdfStopper;
import fpozzi.stopper.model.pdf.TaglioPrezzoPdfStopper;
import fpozzi.stopper.model.pdf.UnoPiuUnoPdfStopper;
import fpozzi.stopper.view.PromoStopperEditorView;
import fpozzi.stopper.view.swing.codastampa.CodaStampaTableCellRenderer;
import fpozzi.utils.Range;
import fpozzi.utils.StringUtils;
import fpozzi.utils.format.FormatUtils;
import fpozzi.utils.swing.JFormattedNumberField;
import fpozzi.utils.swing.SelectAllFocusAdapter;
import fpozzi.utils.swing.SimpleDocumentListener;
import fpozzi.utils.swing.UndoableTextField;
import fpozzi.utils.swing.document.DecimalsOnlyDocument;
import fpozzi.utils.swing.document.FilteredDocument;
import fpozzi.utils.swing.document.IntegersOnlyDocument;
import fpozzi.utils.swing.document.IntegersOnlyDocumentFilter;
import fpozzi.utils.swing.document.LengthLimitDocument;

public class PromoStopperEditorPanel extends StopperEditorPanel<PdfPromoStopper> implements PromoStopperEditorView
{

	private static final long serialVersionUID = 1L;

	private final QuantitaEditorPanel gramPanels[];
	private final JTextComponent codiceField, rigaDescField[];
	private final JFormattedNumberField prezzoPromoField, prezzoField, percScontoField, pezziMaxField;

	private final JCheckBox conCardCheckBox, nascondiPrezzoPerUmCheckBox;
	private final JDatePickerImpl dalDatePicker, alDatePicker;
	private final JButton alButton, dalButton;
	private final JTextField alField, dalField;
	private final JLabel ultimoAcquistoField, ultimaVenditaField;

	private final UtilDateModel dalDateModel, alDateModel;
	private final JRadioButton tpButton, scButton, upButton;

	private final JButton searchButton;
	private PromoEditorObserver observer;

	private Map<String, Boolean> alreadyDisabled;
	private int disabledRows = 0;

	static final String formLayoutColumns[] = new String[] { "right:50dlu, 3dlu, ", "right:40dlu, 3dlu, ",
			"right:40dlu, 3dlu, ", "right:max(pref;20dlu)" };

	static final FormLayout defaultFormLayout = new FormLayout(StringUtils.join(formLayoutColumns, 0, ""), "");;

	private final JPanel formPanel;

	private void setRowEnabled(String enabler, boolean enable)
	{
		if (!enable && !alreadyDisabled.get(enabler))
		{
			disabledRows++;
			rigaDescField[rigaDescField.length - disabledRows].setEnabled(false);
			alreadyDisabled.put(enabler, true);
		} else if (enable && alreadyDisabled.get(enabler))
		{
			rigaDescField[rigaDescField.length - disabledRows].setEnabled(true);
			disabledRows--;
			alreadyDisabled.put(enabler, false);
		}
	}

	public PromoStopperEditorPanel()
	{
		super(new BorderLayout());

		alreadyDisabled = new HashMap<String, Boolean>();

		JLabel label;

		formPanel = new JPanel();
		formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
		formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel columnFormPanel;

		DefaultFormBuilder builder;

		builder = new DefaultFormBuilder(defaultFormLayout);
		builder.setDefaultDialogBorder();
		builder.append(new JLabel("Codice"));
		codiceField = new JTextField(10);
		FilteredDocument doc = new LengthLimitDocument(13) {
			@Override
			public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException
			{
				super.insertString(offset, str.trim(), attr);
			}
			
		}
		;
		doc.getFilters().add(new IntegersOnlyDocumentFilter(false));
		codiceField.setDocument(doc);
		codiceField.addFocusListener(new SelectAllFocusAdapter(codiceField));
		alreadyDisabled.put("codice", false);
		
		codiceField.getDocument().addDocumentListener(new SimpleDocumentListener()
		{
			@Override
			public void textUpdate()
			{
				boolean isEmpty = codiceField.getText().trim().isEmpty();
				//setRowEnabled("codice", isEmpty);
				searchButton.setEnabled(!isEmpty);
			}
		});
		
		builder.append(codiceField, 3);

		searchButton = new JButton(new AbstractAction()
		{

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent evt)
			{
				observer.searchCode(codiceField.getText());
			}
		})
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void setEnabled(boolean enabled)
			{
				if (enabled)
					enabled = codiceField.getText().trim().length() >= 7;
				super.setEnabled(enabled);
			}
		};
		//searchButton.setFocusPainted(false);
		//searchButton.setRolloverEnabled(false);
		//searchButton.setBorderPainted(false);
		searchButton.setMargin(new Insets(2, 7, 2, 7));
		searchButton.setIcon(Icons.FIND.image);
		builder.append(searchButton);
		builder.nextLine();

		builder.appendSeparator();

		label = (new JLabel("Descrizione"));
		builder.append(label);
		JPanel descrNotePanel = new JPanel();
		label = (new JLabel("*Una Parola* -> "));
		label.setFont(Style.Fonts.noteFont);
		label.setForeground(Color.DARK_GRAY);
		descrNotePanel.add(label);
		label = (new JLabel("Una Parola"));
		label.setFont(Style.Fonts.noteBoldFont);
		label.setForeground(Color.DARK_GRAY);
		descrNotePanel.add(label);
		builder.append(descrNotePanel, 5);
		builder.nextLine();

		rigaDescField = new UndoableTextField[5];
		for (int i = 1; i <= rigaDescField.length; i++)
		{
			label = (new JLabel("riga " + i + " "));
			label.setFont(Style.Fonts.italicFont);
			label.setForeground(Color.GRAY);
			builder.append(label);
			rigaDescField[i - 1] = new UndoableTextField(30);
			FilteredDocument rigaDescDocument = new LengthLimitDocument(30);
			rigaDescField[i - 1].setDocument(rigaDescDocument);
			// rigaDescField[i - 1].setHorizontalAlignment(JTextField.RIGHT);
			rigaDescField[i - 1].setComponentPopupMenu(new QuickTextEditPopupMenu(rigaDescField[i - 1]));
			builder.append(rigaDescField[i - 1], 5);

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

		gramPanels = new QuantitaEditorPanel[3];
		alreadyDisabled.put("gram", false);
		for (int i = 0; i < gramPanels.length; i++)
		{
			gramPanels[i] = new QuantitaEditorPanel(builder, QuantitaEditorPanel.fullUdmOptions,
					(i + 1) + "ª quantità ");
			/*
			gramPanels[i].getUdmComboBox().addItemListener(new ItemListener()
			{
				@Override
				public void itemStateChanged(ItemEvent arg0)
				{
					boolean enable = true;
					for (int i = 0; i < gramPanels.length && enable; i++)
						enable = enable && (gramPanels[i].getUdmComboBox().getSelectedIndex() < 1
								|| gramPanels[i].getQtaField().getText().isEmpty());
					setRowEnabled("gram", enable);
				}
			});
			 */
		}

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

		ButtonGroup group = new ButtonGroup();

		builder.appendRow("3dlu");
		builder.nextRow();

		builder.appendRow("12dlu");
		builder.append(new JLabel("Tipo offerta"));

		tpButton = new JRadioButton("taglio prezzo");
		tpButton.setMnemonic(KeyEvent.VK_T);
		tpButton.setActionCommand("TP");
		tpButton.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					prezzoField.setDefaultValue(null);
					percScontoField.setDefaultValue(null);
					percScontoField.setEnabled(false);
				}
			}
		});
		builder.append(tpButton, 5);
		builder.appendRow(RowSpec.decodeSpecs("top:12dlu")[0]);
		group.add(tpButton);

		builder.append(new JLabel(""));
		scButton = new JRadioButton("sconto percentuale");
		scButton.setMnemonic(KeyEvent.VK_S);
		scButton.setActionCommand("SC%");
		scButton.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					prezzoField.setDefaultValue(0.01);
					if (prezzoField.getValue() == null)
						prezzoField.setValue(0.01);
					percScontoField.setEnabled(true);
					percScontoField.setDefaultValue(0.0);
					if (percScontoField.getValue() == null)
						percScontoField.setValue((1 - prezzoPromoField.getValue() / prezzoField.getValue()) * 100.0);
					percScontoField.setEnabled(true);
				}
			}
		});

		builder.append(scButton, 5);
		builder.appendRow(RowSpec.decodeSpecs("top:12dlu")[0]);
		group.add(scButton);

		builder.append(new JLabel(""));
		upButton = new JRadioButton("1 + 1");
		upButton.setMnemonic(KeyEvent.VK_1);
		upButton.setActionCommand("1+1");
		upButton.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					prezzoField.setDefaultValue(null);
					percScontoField.setEnabled(false);
					percScontoField.setDefaultValue(null);
				}
			}
		});

		builder.append(upButton, 5);
		builder.nextLine();
		group.add(upButton);

		builder.append(new JLabel("Prezzo offerta"));
		prezzoPromoField = new JFormattedNumberField(FormatUtils.twoDecimalDigitsFormat, 0.01,
				new Range(0, false, 100, false));
		prezzoPromoField.setColumns(6);
		prezzoPromoField.setDocument(new DecimalsOnlyDocument(false, 6));
		prezzoPromoField.setHorizontalAlignment(JTextField.RIGHT);
		prezzoPromoField.addFocusListener(new SelectAllFocusAdapter(prezzoPromoField));
		builder.append(prezzoPromoField);

		builder.append(new JLabel("% sconto"));
		percScontoField = new JFormattedNumberField(FormatUtils.integerFormat, null, new Range(0, true, 100, true));
		percScontoField.setDocument(new IntegersOnlyDocument(false, 3));
		percScontoField.setColumns(3);
		percScontoField.setHorizontalAlignment(JTextField.RIGHT);
		percScontoField.addFocusListener(new SelectAllFocusAdapter(percScontoField));
		builder.append(percScontoField);

		builder.nextLine();

		builder.append(new JLabel("Pezzi max"));
		pezziMaxField = new JFormattedNumberField(FormatUtils.integerFormat, null, new Range(0, false, 100, false));
		pezziMaxField.setDocument(new IntegersOnlyDocument(false, 2));
		pezziMaxField.setColumns(3);
		pezziMaxField.setHorizontalAlignment(JTextField.RIGHT);
		pezziMaxField.addFocusListener(new SelectAllFocusAdapter(pezziMaxField));
		builder.append(pezziMaxField);

		builder.append(new JLabel("Card"));
		conCardCheckBox = new JCheckBox();
		builder.append(conCardCheckBox);

		builder.nextLine();

		builder.append(new JLabel("Periodo"));

		dalDateModel = new UtilDateModel();
		JDatePanelImpl dalDatePanel = new JDatePanelImpl(dalDateModel);
		dalDatePicker = new JDatePickerImpl(dalDatePanel, FormatUtils.dateLabelFormatter);
		dalDatePicker.setTextEditable(true);
		dalField = (JTextField) dalDatePicker.getComponent(0);
		dalButton = (JButton) dalDatePicker.getComponent(1);
		dalField.setHorizontalAlignment(JTextField.CENTER);
		dalField.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseClicked(MouseEvent e)
			{
				alDatePicker.hidePopup();
				dalButton.doClick();
			}

		});
		builder.append(dalField);

		alDateModel = new UtilDateModel();
		JDatePanelImpl alDatePanel = new JDatePanelImpl(alDateModel);
		alDatePicker = new JDatePickerImpl(alDatePanel, FormatUtils.dateLabelFormatter);
		alDatePicker.setTextEditable(true);
		alField = (JTextField) alDatePicker.getComponent(0);
		alButton = (JButton) alDatePicker.getComponent(1);
		alField.setHorizontalAlignment(JTextField.CENTER);
		alField.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseClicked(MouseEvent e)
			{
				dalDatePicker.hidePopup();
				alButton.doClick();
			}

		});
		builder.append(alField);

		JButton resetPeriodoButton = new JButton(new ImageIcon(getClass().getResource("/img/calendar_delete.png")));
		resetPeriodoButton.setToolTipText("Cancella date");
		resetPeriodoButton.setMargin(new Insets(2, 4, 2, 4));
		resetPeriodoButton.setFocusPainted(false);
		resetPeriodoButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent evt)
			{
				alDatePicker.getModel().setValue(null);
				dalDatePicker.getModel().setValue(null);
			}

		});
		builder.append(resetPeriodoButton);

		nascondiPrezzoPerUmCheckBox = new JCheckBox();
		/*
		 * builder.nextLine();
		 * 
		 * builder.append(new JLabel("prezzo/um"));
		 * builder.append(nascondiPrezzoPerUmCheckBox);
		 * 
		 * builder.nextLine();
		 */
		this.addComponentListener(new ComponentAdapter()
		{

			public void componentHidden(ComponentEvent evt)
			{
				clearDatePickers();
			}
		});
		/*
		 * JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
		 * topFrame.addWindowListener(new WindowAdapter(){
		 * 
		 * @Override public void windowClosing(WindowEvent e) { clearDatePickers(); }
		 * 
		 * @Override public void windowDeactivated(WindowEvent e) { clearDatePickers();
		 * }
		 * 
		 * @Override public void windowIconified(WindowEvent e) { clearDatePickers(); }
		 * 
		 * @Override public void windowLostFocus(WindowEvent e) { clearDatePickers(); }
		 * 
		 * });
		 */

		builder.appendSeparator();

		builder.append(new JLabel("Ult. acquisto"));
		ultimoAcquistoField = new JLabel();
		builder.append(ultimoAcquistoField, 4);

		builder.append(new JLabel("Ult. vendita"));
		ultimaVenditaField = new JLabel();
		builder.append(ultimaVenditaField, 4);

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
	public PdfPromoStopper getPdfStopper()
	{
		PdfPromoStopper pdfStopper = null;

		CodiceInterno codiceInterno = new CodiceInterno(codiceField.getText());
		if (codiceInterno.valore.isEmpty())
			codiceInterno = null;

		List<String> righeDesc = new ArrayList<String>(5);
		int ultimaRigaNonVuota = 0;
		for (int i = 0; i < rigaDescField.length; i++)
		{

			if (rigaDescField[i].isEnabled())
			{
				String riga = rigaDescField[i].getText();
				righeDesc.add(riga);
				if (!riga.isEmpty())
					ultimaRigaNonVuota = i;
			}
		}

		righeDesc = righeDesc.subList(0, ultimaRigaNonVuota + 1);

		ArrayList<Quantita> grammature = new ArrayList<Quantita>(3);
		Quantita grammatura;
		for (int i = 0; i < 3; i++)
		{
			grammatura = gramPanels[i].makeGrammatura();
			if (grammatura != null)
				grammature.add(grammatura);
		}

		Prezzo prezzo = null;
		Double prezzoValue = prezzoField.getValue();
		if (prezzoValue != null)
			prezzo = new Prezzo(prezzoValue);

		PrezzoPromo prezzoPromo = null;

		Double prezzoPromoValue = prezzoPromoField.getValue();
		Double percScontoValue = percScontoField.getValue();

		if (scButton.isSelected())
		{
			if (percScontoValue != null)
			{
				prezzoPromo = new PrezzoPromo(prezzoPromoValue, percScontoValue.intValue());
			} else
			{
				prezzoPromo = new PrezzoPromo(prezzo, prezzoPromoValue);
				percScontoField.setValue(new Double(prezzoPromo.getPercentualeSconto()));
			}

		} else
		{
			prezzoPromo = new PrezzoPromo(prezzoPromoValue);
		}

		Double maxPezziValue = pezziMaxField.getValue();
		int maxPezzi = maxPezziValue == null ? 0 : maxPezziValue.intValue();

		boolean conCard = conCardCheckBox.isSelected();
		boolean nascondiPrezzoPerUM = nascondiPrezzoPerUmCheckBox.isSelected();

		PeriodoOfferta periodo = null;
		Date daDate = dalDateModel.getValue();
		Date aDate = alDateModel.getValue();
		if (aDate != null && daDate != null)
			periodo = new PeriodoOfferta(daDate, aDate);
		else if (aDate == null ^ daDate == null)
		{
			BalloonTip balloonTip = new BalloonTip(aDate == null ? alField : dalField,
					"Completare l'intervallo di date");
			balloonTip.setCloseButton(null);
			TimingUtils.showTimedBalloon(balloonTip, 3000);
		}

		PromoStopper stopper = new PromoStopper();
		stopper.getRigheDescrizione().addAll(righeDesc);
		stopper.setCodiceInterno(codiceInterno);
		stopper.getQuantita().addAll(grammature);
		stopper.setPrezzo(prezzo);
		stopper.setPrezzoPromo(prezzoPromo);
		stopper.setPeriodo(periodo);
		stopper.setConCard(conCard);
		stopper.setPezziMax(maxPezzi);
		stopper.setPrezzoPerUMNascosto(nascondiPrezzoPerUM);

		if (scButton.isSelected())
		{
			pdfStopper = new ScontoPercentualePdfStopper(stopper);
		} else if (tpButton.isSelected())
		{
			pdfStopper = new TaglioPrezzoPdfStopper(stopper);
		} else if (upButton.isSelected())
		{
			pdfStopper = new UnoPiuUnoPdfStopper(stopper);
		}

		return pdfStopper;
	}

	private void clearDatePickers()
	{
		dalDatePicker.hidePopup();
		alDatePicker.hidePopup();
	}

	@Override
	public void toForm(PdfPromoStopper pdfStopper)
	{
		if (searchBalloonTip != null)
		{
			searchBalloonTip.closeBalloon();
			searchBalloonTip = null;
		}
		clearDatePickers();

		tpButton.setSelected(true);
		codiceField.setText(null);
		for (JTextComponent rigaField : rigaDescField)
			rigaField.setText(null);
		for (QuantitaEditorPanel gramPanel : gramPanels)
			gramPanel.setQuantita(null);
		prezzoField.setText(null);
		prezzoPromoField.setValue(0.01);
		percScontoField.setText(null);
		pezziMaxField.setText(null);

		conCardCheckBox.setSelected(false);
		nascondiPrezzoPerUmCheckBox.setSelected(false);
		alDatePicker.getModel().setValue(null);
		dalDatePicker.getModel().setValue(null);
		ultimaVenditaField.setText(null);
		ultimoAcquistoField.setText(null);

		if (pdfStopper != null)
		{
			PromoStopper stopper = (PromoStopper) pdfStopper.getStopper();
			if (stopper.getCodiceInterno() != null)
				codiceField.setText(stopper.getCodiceInterno().valore);
			for (int i = 0; i < stopper.getRigheDescrizione().size() && i < rigaDescField.length; i++)
				rigaDescField[i].setText(stopper.getRigheDescrizione().get(i));

			for (int i = 0; i < stopper.getQuantita().size() && i < gramPanels.length; i++)
			{
				gramPanels[i].setQuantita(stopper.getQuantita().get(i));
			}

			if (stopper.getPrezzo() != null && stopper.getPrezzo().getValue() > 0)
				prezzoField.setText(FormatUtils.twoDecimalDigitsFormat.format(stopper.getPrezzo().getValue()));

			if (stopper.getPrezzoPromo() != null)
				prezzoPromoField
						.setText(FormatUtils.twoDecimalDigitsFormat.format(stopper.getPrezzoPromo().getValue()));
			percScontoField.setText("" + (stopper.getPrezzoPromo().getPercentualeSconto() > 0
					? stopper.getPrezzoPromo().getPercentualeSconto()
					: ""));
			pezziMaxField.setText(
					"" + (stopper.getPezziMax() != null && stopper.getPezziMax() > 0 ? stopper.getPezziMax() : ""));

			if (pdfStopper instanceof TaglioPrezzoPdfStopper)
				tpButton.setSelected(true);
			else if (pdfStopper instanceof ScontoPercentualePdfStopper)
				scButton.setSelected(true);
			else if (pdfStopper instanceof UnoPiuUnoPdfStopper)
				upButton.setSelected(true);

			conCardCheckBox.setSelected(stopper.isConCard());
			nascondiPrezzoPerUmCheckBox.setSelected(stopper.isPrezzoPerUMNascosto());

			if (stopper.getPeriodo() != null)
			{
				dalDateModel.setValue(stopper.getPeriodo().getInizio());
				alDateModel.setValue(stopper.getPeriodo().getFine());
			}
			try
			{
				ultimoAcquistoField.setText(pdfStopper.getStopper().getArticolo().getFormattedDataAcquisto());
			} catch (Exception e)
			{
				ultimoAcquistoField.setText("-");
			}

			try
			{
				ultimaVenditaField.setText(pdfStopper.getStopper().getArticolo().getFormattedDataVendita());
			} catch (Exception e)
			{
				ultimaVenditaField.setText("-");
			}
		}

	}

	@Override
	public void setObserver(PromoEditorObserver observer)
	{
		this.observer = observer;
	}

	private BalloonTip searchBalloonTip = null;

	@Override
	public void notifySearchResult(List<PdfPromoStopper> searchResult)
	{
		Toolkit.getDefaultToolkit().beep();
		if (searchResult.size() == 1)
		{
			toForm(searchResult.get(0));
		} else if (searchResult.size() == 0)
		{
			searchBalloonTip = new BalloonTip(searchButton, "Questo articolo non compare tra le offerte");
			searchBalloonTip.setPositioner(new RightBelowPositioner(15, 10));
			searchBalloonTip.setCloseButton(null);
			TimingUtils.showTimedBalloon(searchBalloonTip, 3000);
		} else
		{
			JPanel alternativesPanel = new JPanel();
			alternativesPanel.setOpaque(false);
			alternativesPanel.setLayout(new BoxLayout(alternativesPanel, BoxLayout.Y_AXIS));

			alternativesPanel.add(new JLabel("Trovata più di una promozione per "));
			alternativesPanel.add(new JLabel("'" + searchResult.get(0).getStopper().getDescrizione() + "'"));

			JLabel label = new JLabel("Selezionarne una per continuare:");
			label.setBorder(BorderFactory.createEmptyBorder(2, 0, 6, 0));
			alternativesPanel.add(label);

			for (final PdfPromoStopper stopper : searchResult)
			{
				JLabel option = new JLabel(stopper.getStopper().getPeriodo().toString());
				option.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
				option.setOpaque(false);
				option.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				option.addMouseListener(new SearchResultOptionMouseListener(option, stopper));
				alternativesPanel.add(option);
			}

			searchBalloonTip = new BalloonTip(searchButton, "Alternative");
			searchBalloonTip.setContents(alternativesPanel);
			searchBalloonTip.setPositioner(new RightBelowPositioner(45, 10));
			searchBalloonTip.setCloseButton(null);
		}

	}

	private class SearchResultOptionMouseListener extends MouseAdapter
	{

		JLabel optionLabel;
		PdfPromoStopper stopper;

		public SearchResultOptionMouseListener(JLabel optionLabel, PdfPromoStopper stopper)
		{
			super();
			this.optionLabel = optionLabel;
			this.stopper = stopper;
		}

		@Override
		public void mouseEntered(MouseEvent e)
		{
			optionLabel.setBackground(CodaStampaTableCellRenderer.selectionLeaderBgColor);
			optionLabel.setForeground(Color.WHITE);
			optionLabel.setOpaque(true);
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
			optionLabel.setOpaque(false);
			optionLabel.setBackground(null);
			optionLabel.setForeground(Color.BLACK);
		}

		@Override
		public void mouseReleased(MouseEvent arg0)
		{
			toForm(stopper);
		}

	}

	@Override
	public void setSearchEnabled(boolean enabled)
	{
		searchButton.setVisible(enabled);
	}

}
