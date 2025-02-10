package fpozzi.stopper.view.swing.codastampa;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import fpozzi.stopper.model.Card;
import fpozzi.stopper.model.Prezzo;
import fpozzi.stopper.model.PromoStopper;
import fpozzi.stopper.model.pdf.PdfStopperRequest;
import fpozzi.stopper.view.CodaStampaView.RequestSelectionObserver;
import fpozzi.stopper.view.swing.Icons;
import fpozzi.utils.Range;
import fpozzi.utils.StringUtils;
import fpozzi.utils.date.DateUtils;
import fpozzi.utils.format.FormatUtils;
import fpozzi.utils.swing.JFormattedNumberField;
import fpozzi.utils.swing.JFormattedNumberFieldEditor;

public class StyleCodaStampaTable extends JTable
{

	private static final long serialVersionUID = 1L;

	public enum ColumnHeader {

		ANTEPRIMA(""), TIPO("Tipo"), CODICE("Codice"), DESCRIZIONE("Descrizione"),
		DATA_INIZIO("Inizio"), DATA_FINE("Fine"),
		PREZZO("Prezzo"), COPIE("Copie");

		private final String text;

		private ColumnHeader(String text)
		{
			this.text = text;
		}

		public String getText()
		{
			return text;
		}

	}
	
	private static class PreviewHeaderRenderer implements TableCellRenderer {

	    DefaultTableCellRenderer renderer;

		public PreviewHeaderRenderer(JTable table)
		{
			renderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
			renderer.setHorizontalAlignment(JLabel.CENTER);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col)
		{
			JLabel component = (JLabel) renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

			component.setIcon(Icons.EYE.image);
			return component;
		}
	}
/*
	private static final TableCellRenderer previewHeaderCellRenderer = new DefaultTableCellHeaderRenderer ()
	{
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			JLabel headerLabel = new JLabel(Icons.EYE.image);
			headerLabel.setOpaque(false);
			this.add(headerLabel);
			return this;
		}
	};
*/
	private class PreviewTableCellRenderer extends CodaStampaTableCellRenderer
	{
		private static final long serialVersionUID = 1L;
		
		private PreviewTableCellRenderer(StyleCodaStampaTable table)
		{
			super(table);
			this.setHorizontalAlignment(CENTER);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			/*
			if (getTable().previewManager.getRowBeingPreviewed() == row)
				this.setIcon(Icons.ARROW_SELECT.image);
			else
				this.setIcon(null);
			*/
			return label;
		}
	};

	private class TagCellRenderer extends CodaStampaTableCellRenderer
	{
		private static final long serialVersionUID = 1L;
		
		public TagCellRenderer(StyleCodaStampaTable table)
		{
			super(table);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			setHorizontalAlignment(CENTER);
			return this;
		}
	};

	private class DescrizioneCellRenderer extends CodaStampaTableCellRenderer
	{
		private static final long serialVersionUID = 1L;
		
		public DescrizioneCellRenderer(StyleCodaStampaTable table)
		{
			super(table);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			String text = (String) value;
			Matcher matcher = StringUtils.asteriskIncludedToken.matcher((CharSequence) text);
			int start = 0;
			String htmlText = "<html>"; 
			while (matcher.find())
			{
				String boldWord = matcher.group(2);
				if (boldWord==null)
					boldWord = "";
				htmlText += text.substring(start, matcher.start()) + "<b>" + boldWord.toUpperCase() + "</b>";
				start = matcher.end();
			}
			htmlText += "</html>";
			this.setText(htmlText);
			return this;
		}
		
	};

	private class CodiceCellRenderer extends CodaStampaTableCellRenderer
	{
		private static final long serialVersionUID = 1L;
		
		public CodiceCellRenderer(StyleCodaStampaTable table)
		{
			super(table);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			setHorizontalAlignment(RIGHT);
			return this;
		}
	};

	private class CopieCellRenderer extends CodaStampaTableCellRenderer
	{
		private static final long serialVersionUID = 1L;

		public CopieCellRenderer(StyleCodaStampaTable table)
		{
			super(table);
		}
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (hasFocus)
			{
				table.editCellAt(row, column);
				((DefaultCellEditor) table.getCellEditor()).getComponent().requestFocus();
			}
			setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			setHorizontalAlignment(JLabel.RIGHT);
			return this;
		}
	};
	// private static final TableCellRenderer prezzoCellRenderer = new
	// PdfStopperPrezzoCellRendeder();
	private class PrezzoCellRenderer extends CodaStampaTableCellRenderer
	{
		private static final long serialVersionUID = 1L;
		
		public PrezzoCellRenderer(StyleCodaStampaTable table)
		{
			super(table);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			Prezzo prezzo = (Prezzo) value;
			setText(prezzo != null ? "€ " + prezzo.toString() : "");
			setHorizontalAlignment(RIGHT);
			return this;
		}
	};
	
	private class DataPromoCellRenderer extends CodaStampaTableCellRenderer
	{
		private static final long serialVersionUID = 1L;
		
		public DataPromoCellRenderer(StyleCodaStampaTable table)
		{
			super(table);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			Date dataPromo = (Date) value;
			setText(dataPromo != null ? DateUtils.italianDateFormat.format(dataPromo): "");
			setHorizontalAlignment(RIGHT);
			return this;
		}
	};

	private final StyleCodaStampaPanel csPanel;

	private CodaStampaTableModel model;
	private TableRowSorter<CodaStampaTableModel> sorter;
	private TableSelectionPopupMenu tableSelectionPopupMenu;
	SingleColumnPreviewManager previewManager;

	private int longestCodice;

	public StyleCodaStampaTable(StyleCodaStampaPanel styleCodaStampaPanel)
	{
		this.csPanel = styleCodaStampaPanel;

		setPreferredScrollableViewportSize(new Dimension(getPreferredScrollableViewportSize().width, 100));
		setRowHeight(20);
		putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		// assume JTable is named "table"
		int condition = JComponent.WHEN_FOCUSED;
		InputMap inputMap = getInputMap(condition);
		ActionMap actionMap = getActionMap();

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "DELETE");
		actionMap.put("DELETE", new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e)
			{
				getCsPanel().removeSelectedRows();
			}
		});

		tableSelectionPopupMenu = new TableSelectionPopupMenu(this);

		getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{

			@Override
			public void valueChanged(ListSelectionEvent evt)
			{
				final int index = getSelectionModel().getLeadSelectionIndex();
				PdfStopperRequest selectedRequest = index >= 0 && Arrays.binarySearch(getSelectedRows(), index) >= 0 ? model.getRequests().get(
						convertRowIndexToModel(index)) : null;

				if (selectedRequest != getCsPanel().getCodaStampaPanel().getSelectedRequest())
				{

					getCsPanel().getCodaStampaPanel().setSelectedRequest(selectedRequest);

					if (!(selectedRequest == null && !evt.getValueIsAdjusting()))
						for (StyleCodaStampaPanel csPanelLoop : getCsPanel().getCodaStampaPanel().getStyleCodaStampaPanels().values())
							if (csPanelLoop != getCsPanel())
								csPanelLoop.getTable().clearSelection();

					for (RequestSelectionObserver observer : getCsPanel().getCodaStampaPanel().getSelectionObservers())
						observer.requestSelected(selectedRequest);

				}
			}

		});

		addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent me)
			{
				Point p = me.getPoint();
				int row = rowAtPoint(p);
				if (SwingUtilities.isRightMouseButton(me) && row >= 0)
				{
					int[] selRows = getSelectedRows();
					//System.out.println(Arrays.toString(selRows));
					if (selRows.length < 2 || Arrays.binarySearch(selRows, row) < 0)
					{
						setRowSelectionInterval(row, row);
					}
					tableSelectionPopupMenu.show(me.getComponent(), me.getX(), me.getY());
				}
			}

		});

		previewManager = new SingleColumnPreviewManager(this);
	}

	public StyleCodaStampaPanel getStyleCodaStampaPanel()
	{
		return getCsPanel();
	}

	public CodaStampaTableModel getModel()
	{
		return model;
	}

	public StyleCodaStampaPanel getCsPanel()
	{
		return csPanel;
	}

	public TableRowSorter<CodaStampaTableModel> getRowSorter()
	{
		return sorter;
	}

	public void setModel(final CodaStampaTableModel model)
	{
		this.model = model;
		super.setModel(model);
		setAutoCreateColumnsFromModel( false );
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		sorter = new TableRowSorter<CodaStampaTableModel>(model);
		sorter.setComparator(ColumnHeader.PREZZO.ordinal(), Prezzo.defaultComparator);
		sorter.setSortable(ColumnHeader.ANTEPRIMA.ordinal(), false);
		sorter.setSortsOnUpdates(true);
		setRowSorter(sorter);

		getColumnModel().getColumn(ColumnHeader.ANTEPRIMA.ordinal()).setHeaderRenderer(new PreviewHeaderRenderer(this));
		getColumnModel().getColumn(ColumnHeader.ANTEPRIMA.ordinal()).setCellRenderer(new PreviewTableCellRenderer(this));
		setColumnSize(ColumnHeader.ANTEPRIMA.ordinal(), 24, false);

		getColumnModel().getColumn(ColumnHeader.TIPO.ordinal()).setCellRenderer(new TagCellRenderer(this));
		setColumnSize(ColumnHeader.TIPO.ordinal(), 40, false);

		getColumnModel().getColumn(ColumnHeader.CODICE.ordinal()).setCellRenderer(new CodiceCellRenderer(this));
		setColumnSize(ColumnHeader.CODICE.ordinal(), 0, false);

		getColumnModel().getColumn(ColumnHeader.DESCRIZIONE.ordinal()).setCellRenderer(new DescrizioneCellRenderer(this));
		
		getColumnModel().getColumn(ColumnHeader.DATA_INIZIO.ordinal()).setCellRenderer(new DataPromoCellRenderer(this));
		setColumnSize(ColumnHeader.DATA_INIZIO.ordinal(), 0, false);

		getColumnModel().getColumn(ColumnHeader.DATA_FINE.ordinal()).setCellRenderer(new DataPromoCellRenderer(this));
		setColumnSize(ColumnHeader.DATA_FINE.ordinal(), 0, false);


		getColumnModel().getColumn(ColumnHeader.PREZZO.ordinal()).setCellRenderer(new PrezzoCellRenderer(this));
		setColumnSize(ColumnHeader.PREZZO.ordinal(), 50, false);

		TableCellEditor editor = new JFormattedNumberFieldEditor(new JFormattedNumberField(FormatUtils.integerFormat, 1.0, new Range(0, true, 100,
				true)));
		getColumnModel().getColumn(ColumnHeader.COPIE.ordinal()).setCellEditor(editor);
		getColumnModel().getColumn(ColumnHeader.COPIE.ordinal()).setCellRenderer(new CopieCellRenderer(this));
		setColumnSize(ColumnHeader.COPIE.ordinal(), 40, false);

		longestCodice = 0;
		boolean hiddenDate = true;
		boolean visiblePrezzo = true;

		for (PdfStopperRequest request : model.getRequests())
		{
			if (request.getPdfStopper().getCodice() != null)
				resizeCodiceLength(request.getPdfStopper().getCodice().valore.length());
			
			if (hiddenDate && request.getPdfStopper().getStopper() instanceof PromoStopper)
			{
				setColumnSize(ColumnHeader.DATA_INIZIO.ordinal(), 60, false);
				setColumnSize(ColumnHeader.DATA_FINE.ordinal(), 60, false);
				hiddenDate = false;
			}
			
			if (visiblePrezzo && request.getPdfStopper().getStopper() instanceof Card)
			{
				setColumnSize(ColumnHeader.PREZZO.ordinal(), 0, false);
				visiblePrezzo = false;
			}
		}
	}
	
	public void setColumnSize(int columnIndex, int size, boolean resizable)
	{
		getColumnModel().getColumn(columnIndex).setMinWidth(size);
		getColumnModel().getColumn(columnIndex).setMaxWidth(resizable ? Integer.MAX_VALUE : size);
		getColumnModel().getColumn(columnIndex).setPreferredWidth(size);
	}

	public void resizeCodiceLength(int codicelength)
	{
		if (codicelength > longestCodice)
		{
			longestCodice = codicelength;
			getColumnModel().getColumn(ColumnHeader.CODICE.ordinal()).setMinWidth(20 / 3 + 15);
			getColumnModel().getColumn(ColumnHeader.CODICE.ordinal()).setMaxWidth(20 / 3 * codicelength + 15);
			getColumnModel().getColumn(ColumnHeader.CODICE.ordinal()).setPreferredWidth(20 / 3 * codicelength + 15);
		}
	}

	protected boolean processKeyBinding(KeyStroke stroke, KeyEvent evt, int condition, boolean pressed)
	{
		if (!pressed)
		{
			return super.processKeyBinding(stroke, evt, condition, pressed);
		}

		if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN)
		{

			if (getCellEditor() != null)
			{
				getCellEditor().stopCellEditing();
			}
			changeSelection(getRowCount() - 1, 0, false, false);
			return true;

		}
		else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP)
		{
			if (getCellEditor() != null)
			{
				getCellEditor().stopCellEditing();
			}
			changeSelection(0, 0, false, false);
			return true;
		}
		else if (evt.getKeyCode() == KeyEvent.VK_ENTER)
		{
			if (isEditing())
				getCellEditor().stopCellEditing();
		}

		return super.processKeyBinding(stroke, evt, condition, pressed);
	}

};
