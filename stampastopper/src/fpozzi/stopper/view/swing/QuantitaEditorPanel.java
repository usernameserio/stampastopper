package fpozzi.stopper.view.swing;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.DefaultFormBuilder;

import fpozzi.gdoshop.model.articolo.Quantita;
import fpozzi.gdoshop.model.articolo.UnitaMisura;
import fpozzi.utils.Range;
import fpozzi.utils.format.FormatUtils;
import fpozzi.utils.swing.CustomKeyAdapter;
import fpozzi.utils.swing.JFormattedNumberField;
import fpozzi.utils.swing.SelectAllFocusAdapter;
import fpozzi.utils.swing.SimpleDocumentListener;
import fpozzi.utils.swing.document.DecimalsOnlyDocument;

public class QuantitaEditorPanel
{

	static public final UnitaMisura[] fullUdmOptions;
	static
	{
		fullUdmOptions = new UnitaMisura[UnitaMisura.values().length + 1];
		fullUdmOptions[0] = null;
		for (UnitaMisura udm : UnitaMisura.values())
			fullUdmOptions[udm.ordinal() + 1] = udm;
	}

	static public final UnitaMisura[] weightsOnlyUdmOptions;
	static
	{
		weightsOnlyUdmOptions = new UnitaMisura[] { UnitaMisura.KG, UnitaMisura.HG, UnitaMisura.GR, UnitaMisura.PZ };
	}

	private final JFormattedNumberField valoreField, moltField;
	private final JComboBox<UnitaMisura> udmComboBox;

	public QuantitaEditorPanel(DefaultFormBuilder builder, UnitaMisura[] udmOptions, String tag)
	{

		JLabel label = (new JLabel(tag));
		label.setFont(Style.Fonts.italicFont);
		label.setForeground(Color.GRAY);

		builder.append(label);

		udmComboBox = new JComboBox<UnitaMisura>(udmOptions);
		udmComboBox.setMaximumRowCount(udmOptions.length);
		udmComboBox.addItemListener(new ItemListener()
		{

			@Override
			public void itemStateChanged(ItemEvent evt)
			{
				if (evt.getStateChange() == ItemEvent.SELECTED)
				{
					udmSelectionChanged();
				}
			}
		});

		builder.append(udmComboBox);

		valoreField = new JFormattedNumberField(FormatUtils.optionalDecimalDigitsFormat, null, new Range(0, false, 10000, false));
		valoreField.setColumns(7);
		valoreField.setDocument(new DecimalsOnlyDocument(false, 6));
		valoreField.setHorizontalAlignment(JTextField.RIGHT);
		valoreField.addFocusListener(new SelectAllFocusAdapter(valoreField));
		valoreField.getDocument().addDocumentListener(new SimpleDocumentListener()
		{

			@Override
			public void textUpdate()
			{
				moltField.setEnabled(!valoreField.getText().trim().equals("") && udmComboBox.getSelectedItem() != UnitaMisura.PZ);
			}

		});
		builder.append(valoreField);

		moltField = new JFormattedNumberField(FormatUtils.integerFormat, null, new Range(1, false, 100, false));
		moltField.setColumns(3);
		moltField.setHorizontalAlignment(JTextField.RIGHT);
		moltField.addFocusListener(new SelectAllFocusAdapter(moltField));
		moltField.addKeyListener(CustomKeyAdapter.integersOnlyKeyAdapter);

		builder.append(moltField);

		builder.nextLine();

	}

	public JTextField getQtaField()
	{
		return valoreField;
	}

	public JTextField getMoltField()
	{
		return moltField;
	}

	public JComboBox<UnitaMisura> getUdmComboBox()
	{
		return udmComboBox;
	}
	
	private void udmSelectionChanged()
	{
		valoreField.setEnabled(udmComboBox.getSelectedItem() != null);
		moltField.setEnabled(udmComboBox.getSelectedItem() != null && udmComboBox.getSelectedItem() != UnitaMisura.PZ
				&& !valoreField.getText().trim().equals(""));
	}

	public Quantita makeGrammatura()
	{
		if (udmComboBox.getSelectedItem() == null)
			return null;

		Quantita quantita;

		UnitaMisura udm = udmComboBox.getItemAt(udmComboBox.getSelectedIndex());

		Double qtaValue = valoreField.getValue();

		if (qtaValue == null)
		{
			quantita = new Quantita(udm);
		}
		else
		{
			Double moltValue = moltField.getValue();
			if (udm != UnitaMisura.PZ && moltValue != null)
			{
				quantita = new Quantita(udm, qtaValue, moltValue.intValue());
			}
			else
			{
				quantita = new Quantita(udm, qtaValue);
			}

		}

		return quantita;
	}

	public void setQuantita(Quantita qta)
	{
		udmComboBox.setSelectedIndex(0);
		valoreField.setText(null);
		valoreField.setEnabled(false);
		moltField.setText(null);
		moltField.setEnabled(false);

		if (qta != null)
		{
			udmComboBox.getModel().setSelectedItem(qta.getUnitaMisura());
			udmSelectionChanged();
			valoreField.setValue(qta.getValore());
			moltField.setText("" + (qta.getMoltiplicatore() != null && qta.getMoltiplicatore() > 1 ? qta.getMoltiplicatore() : ""));

		}
	}

}
