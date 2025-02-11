package fpozzi.stopper.view.swing.codastampa;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;

import fpozzi.stopper.StampaStopperProperties;
import fpozzi.stopper.StampaStopperProperties.StampaStopperProperty;
import fpozzi.stopper.model.pdf.PdfStopperStyle;
import fpozzi.stopper.view.swing.Icons;
import fpozzi.utils.format.FormatUtils;

class StyleCodaStampaPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private final CodaStampaPanel parentPanel;
	private final PdfStopperStyle style;

	private final StyleCodaStampaTable table;

	private final JButton generaButton, stampaButton;
	private final JLabel stopperTotaliLabel;
	private final JLabel pagineTotaliLabel;

	public StyleCodaStampaPanel(CodaStampaPanel codaStampaPanel, PdfStopperStyle style)
	{
		super(new BorderLayout());

		this.parentPanel = codaStampaPanel;
		this.style = style;
		this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

		JPanel leftPanel = new JPanel()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public Dimension getPreferredSize()
			{
				return new Dimension(122, super.getPreferredSize().height);
			}
		};
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setBorder(new EmptyBorder(15, 10, 5, 10));
		leftPanel.setBackground(new Color(245, 245, 245));

		JPanel stylePanel = new JPanel();
		stylePanel.setOpaque(false);
		stylePanel.setLayout(new BoxLayout(stylePanel, BoxLayout.Y_AXIS));
		stylePanel.setAlignmentX(LEFT_ALIGNMENT);

		JLabel stylePictureLabel = new JLabel(new ImageIcon(style.getRappresentazione()));
		stylePictureLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		JPanel stylePicturePanel = new JPanel()
		{
			private static final long serialVersionUID = 1L;

			private final Dimension dim = new Dimension(102, 108);

			@Override
			public Dimension getMaximumSize()
			{
				return dim;
			}

			@Override
			public Dimension getMinimumSize()
			{
				return dim;
			}

		};
		stylePicturePanel.setOpaque(false);
		stylePicturePanel.setAlignmentX(LEFT_ALIGNMENT);
		stylePicturePanel.add(stylePictureLabel);

		stylePanel.add(stylePicturePanel);

		stylePanel.add(Box.createRigidArea(new Dimension(0, 10)));

		JLabel label;
		Font boldFont, italicFont;

		label = new JLabel("Formato:");
		stylePanel.add(label);
		String fontName = label.getFont().getFontName();

		label = new JLabel("  " + style.getFormat().getDescription());
		boldFont = new Font(fontName, Font.BOLD, label.getFont().getSize());
		label.setFont(boldFont);

		italicFont = new Font(fontName, Font.PLAIN, label.getFont().getSize() - 1);
		label.setFont(boldFont);
		stylePanel.add(label);
		label = new JLabel("  " + FormatUtils.integerFormat.format(style.getFormat().getWidthInMM()) + " x "
				+ FormatUtils.integerFormat.format(style.getFormat().getHeightInMM()) + " mm ");
		label.setFont(italicFont);
		stylePanel.add(label);

		stylePanel.add(Box.createRigidArea(new Dimension(0, 3)));

		label = new JLabel("Carta:");
		stylePanel.add(label);

		label = new JLabel("  " + (style.richiedeCartaSpeciale() ? "speciale" : "normale"));
		label.setFont(boldFont);
		stylePanel.add(label);

		stylePanel.add(Box.createRigidArea(new Dimension(0, 3)));

		label = new JLabel("Stampante:");
		stylePanel.add(label);

		label = new JLabel("  " + (style.richiedeStampaAColori() ? "colori" : "b/n"));
		label.setFont(boldFont);
		stylePanel.add(label);

		leftPanel.add(stylePanel);

		leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));

		add(leftPanel, BorderLayout.WEST);

		table = new StyleCodaStampaTable(this);

		JPanel tableContainer = new JPanel(new BorderLayout());
		tableContainer.add(table.getTableHeader(), BorderLayout.PAGE_START);
		tableContainer.add(table, BorderLayout.CENTER);
		// tableContainer.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		tableContainer.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));

		JPanel southPanel = new JPanel();
		southPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

		JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		p.setOpaque(false);

		label = new JLabel("n° stopper:  ");
		p.add(label);
		stopperTotaliLabel = new JLabel();
		stopperTotaliLabel.setFont(boldFont);
		stopperTotaliLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
		stopperTotaliLabel.setPreferredSize(new Dimension(25, label.getPreferredSize().height));
		p.add(stopperTotaliLabel);
		southPanel.add(p);

		p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		p.setOpaque(false);

		label = new JLabel("n° pagine: ");
		p.add(label);
		pagineTotaliLabel = new JLabel();
		pagineTotaliLabel.setFont(boldFont);
		pagineTotaliLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
		pagineTotaliLabel.setPreferredSize(new Dimension(20, label.getPreferredSize().height));
		p.add(pagineTotaliLabel);
		southPanel.add(p);

		southPanel.add(Box.createRigidArea(new Dimension(10, 0)));

		generaButton = new JButton("Genera PDF  ");

		generaButton.setOpaque(false);
		generaButton.setAlignmentX(CENTER_ALIGNMENT);
		generaButton.setIcon(Icons.WIZARD_SMALL.image);
		generaButton.setVerticalTextPosition(SwingConstants.CENTER);
		generaButton.setHorizontalTextPosition(SwingConstants.LEFT);
		generaButton.setVisible(!StampaStopperProperties.getInstance().getProperty(StampaStopperProperty.STAMPA_DIRETTA).equals("true"));
		generaButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent evt)
			{
				for (Entry<PdfStopperStyle, StyleCodaStampaPanel> csPanel : parentPanel.getStyleCodaStampaPanels().entrySet())
				{
					TableCellEditor editor = csPanel.getValue().table.getCellEditor();
					if (editor != null)
						csPanel.getValue().table.getCellEditor().stopCellEditing();
				}
				parentPanel.getObserver().generatePDF(StyleCodaStampaPanel.this.style);
			}

		});
		southPanel.add(generaButton);
		
		stampaButton = new JButton("Stampa  ");

		stampaButton.setOpaque(false);
		stampaButton.setAlignmentX(CENTER_ALIGNMENT);
		stampaButton.setIcon(Icons.WIZARD_SMALL.image);
		stampaButton.setVerticalTextPosition(SwingConstants.CENTER);
		stampaButton.setHorizontalTextPosition(SwingConstants.LEFT);
		stampaButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent evt)
			{
				for (Entry<PdfStopperStyle, StyleCodaStampaPanel> csPanel : parentPanel.getStyleCodaStampaPanels().entrySet())
				{
					TableCellEditor editor = csPanel.getValue().table.getCellEditor();
					if (editor != null)
						csPanel.getValue().table.getCellEditor().stopCellEditing();
				}
				parentPanel.getObserver().print(StyleCodaStampaPanel.this.style);
			}

		});
		southPanel.add(stampaButton);

		tableContainer.add(southPanel, BorderLayout.SOUTH);

		add(tableContainer, BorderLayout.CENTER);

	}

	public CodaStampaPanel getCodaStampaPanel()
	{
		return parentPanel;
	}

	public PdfStopperStyle getStyle()
	{
		return style;
	}

	public StyleCodaStampaTable getTable()
	{
		return table;
	}

	public void removeSelectedRows()
	{
		int[] selectedRows = table.getSelectedRows();
		int[] selectedRowsConverted = new int[selectedRows.length];
		int newSelectedRow = table.getSelectionModel().getLeadSelectionIndex();

		for (int i = 0; i < selectedRows.length; i++)
		{
			selectedRowsConverted[i] = table.convertRowIndexToModel(selectedRows[i]);
		}
		Arrays.sort(selectedRowsConverted);

		int offset = 0;
		for (int modelIndex : selectedRowsConverted)
		{
			parentPanel.getObserver().removeRequest(table.getModel().getRequests().get(modelIndex - offset++));
		}

		newSelectedRow = Math.min(newSelectedRow - offset + 1, table.getRowCount() - 1);
		if (newSelectedRow >= 0)
			table.getSelectionModel().setSelectionInterval(newSelectedRow, newSelectedRow);

	}

	public void setStopperCount(int newCount)
	{
		stopperTotaliLabel.setText(newCount + "");
		pagineTotaliLabel.setText(newCount / style.getFormat().getStoppersPerPage() + (newCount % style.getFormat().getStoppersPerPage() > 0 ? 1 : 0)
				+ "");
		generaButton.setEnabled(newCount > 0);
	}

}