package fpozzi.stopper.view.swing.codastampa;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;

import fpozzi.stopper.model.Card;
import fpozzi.stopper.model.PromoStopper;
import fpozzi.stopper.model.pdf.CodaStampa;
import fpozzi.stopper.model.pdf.PdfStopper;
import fpozzi.stopper.model.pdf.PdfStopperRequest;
import fpozzi.stopper.model.pdf.PdfStopperStyle;
import fpozzi.stopper.view.AddView;
import fpozzi.stopper.view.CodaStampaView;
import fpozzi.stopper.view.swing.AddPanel;
import fpozzi.stopper.view.swing.ButtonTab;
import fpozzi.stopper.view.swing.Icons;
import fpozzi.stopper.view.swing.MainMenu;
import fpozzi.stopper.view.swing.Style;
import fpozzi.stopper.view.swing.codastampa.StyleCodaStampaTable.ColumnHeader;
import fpozzi.utils.Clipboard;
import fpozzi.utils.ClipboardObserver;
import fpozzi.utils.Utils;
import fpozzi.utils.swing.ConfirmDialogOptions;

public class CodaStampaPanel extends JPanel implements CodaStampaView, ClipboardObserver<List<PdfStopperRequest>>
{

	private static final long serialVersionUID = 1L;

	private final ButtonTab tab;

	private Clipboard<List<PdfStopperRequest>> clipboard;

	private CodaStampa codaStampa;

	private HashMap<PdfStopperStyle, StyleCodaStampaPanel> csPanels;

	private final AddPanel addPanel;

	private final JScrollPane scrollPane;

	private final JButton generateAllButton, pasteButton;

	private final JPanel emptyQueuePanel;

	private CodaStampaViewObserver observer;

	private final List<RequestSelectionObserver> selectionObservers;

	private File file;

	private JTextField searchField;
	
	private PdfStopperRequest selectedRequest;

	int codeVisibili;
	int codeConStopper;

	public CodaStampaPanel()
	{
		super(new BorderLayout());

		this.tab = new ButtonTab();
		
		clipboard = null;
		
		selectedRequest = null;

		this.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 2));

		observer = null;
		selectionObservers = new LinkedList<RequestSelectionObserver>();
		/*
		 * titleBar = new JTitleBar("Coda di stampa", Style.Fonts.boldFont,
		 * Style.titleFontColor, null, Style.titleBgColor, Style.titleBgColor2);
		 * this.add(titleBar, BorderLayout.NORTH);
		 */

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createLineBorder(Style.titleBgColor));

		JPanel topCmdPanel = new JPanel(new BorderLayout());
		topCmdPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
		topCmdPanel.setBackground(new Color(230, 230, 230));

		JPanel aggiungiPanelContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
		aggiungiPanelContainer.setOpaque(false);
		addPanel = new AddPanel();
		pasteButton = new CodaStampaButton("Incolla  ", Icons.PAGE_PASTE.image, new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent arg0)
			{
				for (PdfStopperRequest request : clipboard.getValue())
					observer.addRequest((PdfStopperRequest) request.clone());
			}
		});
		pasteButton.setEnabled(false);
		addPanel.add(pasteButton);
		addPanel.add(Box.createHorizontalStrut(5));

		addPanel.add(new JSeparator(JSeparator.VERTICAL));
		addPanel.add(new JSeparator(JSeparator.VERTICAL));
		addPanel.add(Box.createHorizontalStrut(5));
		aggiungiPanelContainer.add(addPanel);
		topCmdPanel.add(aggiungiPanelContainer, BorderLayout.WEST);

		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.LINE_AXIS));
		searchPanel.setOpaque(false);

		JLabel searchLabel = new JLabel("Filtra ", SwingConstants.TRAILING);
		searchLabel.setFont(Style.Fonts.italicFont);
		searchPanel.add(searchLabel);
		searchLabel.setAlignmentY(CENTER_ALIGNMENT);

		final JPanel searchFieldContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
		searchFieldContainer.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
		searchFieldContainer.setOpaque(false);
		searchField = new JTextField(10)
		{/*
			private static final long serialVersionUID = 1L;
			
			@Override
			public int getWidth()
			{
				return searchFieldContainer.getWidth() - 10;
			}
*/
		};
		searchField.getDocument().addDocumentListener(new DocumentListener()
		{

			private void changeFilter()
			{
				String[] searchWords = searchField.getText().split("\\s+");
				List<RowFilter<CodaStampaTableModel, Integer>> filters = new ArrayList<RowFilter<CodaStampaTableModel, Integer>>(searchWords.length);
				for (final String searchWord : searchWords)
				{
					filters.add(new RowFilter<CodaStampaTableModel, Integer>()
					{
						@Override
						public boolean include(javax.swing.RowFilter.Entry<? extends CodaStampaTableModel, ? extends Integer> entry)
						{
							CodaStampaTableModel model = entry.getModel();
							PdfStopperRequest request = model.getRequests().get(entry.getIdentifier());
							return (request.getPdfStopper().getDescrizione().toLowerCase().contains(searchWord.toLowerCase()));
						}
					});
				}
				for (StyleCodaStampaPanel csPanel : csPanels.values())
					csPanel.getTable().getRowSorter().setRowFilter(RowFilter.andFilter(filters));
			}

			public void changedUpdate(DocumentEvent e)
			{
				changeFilter();
			}

			public void insertUpdate(DocumentEvent e)
			{
				changeFilter();
			}

			public void removeUpdate(DocumentEvent e)
			{
				changeFilter();
			}
		});
		searchFieldContainer.add(searchField);
		searchPanel.add(searchFieldContainer);
		topCmdPanel.add(searchPanel, BorderLayout.CENTER);

		mainPanel.add(topCmdPanel, BorderLayout.NORTH);

		csPanels = new HashMap<PdfStopperStyle, StyleCodaStampaPanel>();

		JPanel csPanelsContainer = new JPanel();
		csPanelsContainer.setLayout(new BoxLayout(csPanelsContainer, BoxLayout.PAGE_AXIS));

		emptyQueuePanel = new JPanel();
		emptyQueuePanel.add(new JLabel("Non ci sono ancora stopper nella coda di stampa."));

		csPanelsContainer.add(emptyQueuePanel);

		for (int i = 0; i < PdfStopperStyle.values().length; i++)
		{
			StyleCodaStampaPanel codaPanel = new StyleCodaStampaPanel(this, PdfStopperStyle.values()[i]);
			codaPanel.setVisible(false);
			csPanels.put(PdfStopperStyle.values()[i], codaPanel);
			csPanelsContainer.add(codaPanel);
		}

		scrollPane = new JScrollPane(csPanelsContainer);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());

		mainPanel.add(scrollPane, BorderLayout.CENTER);

		this.add(mainPanel, BorderLayout.CENTER);

		JPanel cmdPanel = new JPanel();
		cmdPanel.add(Box.createRigidArea(new Dimension(20, 0)));
		cmdPanel.setBackground(new Color(230, 230, 230));

		generateAllButton = new JButton("Genera tutti gli stopper    ");
		generateAllButton.setMargin(new Insets(10, 25, 10, 25));
		generateAllButton.setOpaque(false);
		generateAllButton.setIcon(Icons.WIZARD.image);
		generateAllButton.setVerticalTextPosition(SwingConstants.CENTER);
		generateAllButton.setHorizontalTextPosition(SwingConstants.LEFT);
		generateAllButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent evt)
			{
				for (Entry<PdfStopperStyle, StyleCodaStampaPanel> csPanel : csPanels.entrySet())
				{
					TableCellEditor editor = csPanel.getValue().getTable().getCellEditor();
					if (editor != null)
						csPanel.getValue().getTable().getCellEditor().stopCellEditing();
				}
				observer.generateAllPDFs();
			}

		});

		cmdPanel.add(generateAllButton);

		this.add(cmdPanel, BorderLayout.SOUTH);

		this.setCodeVisibili(0);
		this.setCodeConStopper(0);
	}

	public ButtonTab getTab()
	{
		return tab;
	}

	public AddView getAddView()
	{
		return addPanel;
	}

	public CodaStampa getCodaStampa()
	{
		return codaStampa;
	}
	
	public CodaStampaViewObserver getObserver()
	{
		return observer;
	}

	public List<RequestSelectionObserver> getSelectionObservers()
	{
		return selectionObservers;
	}

	@Override
	public void setCodaStampa(CodaStampa codaStampa)
	{
		this.codaStampa = codaStampa;
		if (codaStampa != null)
		{
			int codeVisibili = 0;
			for (Entry<PdfStopperStyle, List<PdfStopperRequest>> coda : codaStampa.getRequests().entrySet())
			{
				StyleCodaStampaPanel codaPanel = csPanels.get(coda.getKey());
				codaPanel.getTable().setModel(new CodaStampaTableModel(coda.getValue()));
				this.stopperCountChanged(codaStampa, coda.getKey(), 0, codaStampa.getStoppersCount(coda.getKey()));
				codaPanel.setVisible(!coda.getValue().isEmpty());
				if (codaPanel.isVisible())
					codeVisibili++;
			}
			setCodeVisibili(codeVisibili);
		}

	}

	public Clipboard<List<PdfStopperRequest>> getClipboard()
	{
		return clipboard;
	}	

	public HashMap<PdfStopperStyle, StyleCodaStampaPanel> getStyleCodaStampaPanels()
	{
		return csPanels;
	}

	public File getFile()
	{
		return file;
	}

	@Override
	public void setFile(File file)
	{
		this.file = file;
		tab.getTitleLabel().setText(file == null ? "nuova scheda" : file.getName());
	}

	public void setObserver(CodaStampaViewObserver observer)
	{
		this.observer = observer;
	}

	public void attachSelectionObserver(RequestSelectionObserver observer)
	{
		selectionObservers.add(observer);
	}

	public void detachSelectionObserver(RequestSelectionObserver observer)
	{
		selectionObservers.remove(observer);
	}

	@Override
	public void setCommandsEnabled(boolean enabled)
	{
		generateAllButton.setEnabled(enabled);
	}

	@Override
	public void selectRequest(PdfStopperRequest request)
	{

		for (Entry<PdfStopperStyle, StyleCodaStampaPanel> csPanel : csPanels.entrySet())
			csPanel.getValue().getTable().clearSelection();
		StyleCodaStampaTable selectTable = csPanels.get(request.getStyle()).getTable();
		boolean foundInFilteredTable = false;
		if (!searchField.getText().trim().isEmpty())
		{
			for (int i=0; i<selectTable.getRowCount() && !foundInFilteredTable;i++)
				foundInFilteredTable = selectTable.getModel().getRequests().get(selectTable.getRowSorter().convertRowIndexToModel(i))==request;
		}
		if (!foundInFilteredTable)
			searchField.setText("");
		int index = selectTable.convertRowIndexToView(csPanels.get(request.getStyle()).getTable().getModel().getRequests().indexOf(request));
		selectTable.getSelectionModel().setSelectionInterval(index, index);
		/*
		 * Rectangle rect =
		 * selectTable.getCellRect(selectTable.getSelectedRow(), 0, true);
		 * System.out.println(rect.x + " " + rect.y); JViewport viewport =
		 * scrollPane.getViewport(); Rectangle r2 = viewport.getViewRect();
		 * System.out.println(r2.x + " " + r2.y); if
		 * (rect.y+r2.getHeight()>r2.y) selectTable.scrollRectToVisible(new
		 * Rectangle(rect.x, rect.y+(int)r2.getHeight(), (int) r2.getWidth(),
		 * (int) r2.getHeight()));
		 */
		selectTable.requestFocus();
	}

	@Override
	public PdfStopperRequest getSelectedRequest()
	{
		return selectedRequest;
	}
	

	void setSelectedRequest(PdfStopperRequest selectedRequest)
	{
		this.selectedRequest = selectedRequest;
	}

	@Override
	public void requestAdded(CodaStampa codaStampa, PdfStopperRequest request)
	{
		final PdfStopperStyle style = request.getStyle();
		final StyleCodaStampaPanel csPanel = csPanels.get(style);
		if (!csPanel.isVisible())
		{
			csPanel.setVisible(true);
			setCodeVisibili(codeVisibili + 1);
		}
		csPanel.getTable().getModel().fireTableDataChanged();
		if (request.getPdfStopper().getCodice() != null)
			csPanel.getTable().resizeCodiceLength(request.getPdfStopper().getCodice().valore.length());
		if (request.getPdfStopper().getStopper() instanceof Card)
		{
			csPanel.getTable().setColumnSize(ColumnHeader.PREZZO.ordinal(), 0, false);
		}
		if (request.getPdfStopper().getStopper() instanceof PromoStopper)
		{
			csPanel.getTable().setColumnSize(ColumnHeader.DATA_INIZIO.ordinal(), 60, false);
			csPanel.getTable().setColumnSize(ColumnHeader.DATA_FINE.ordinal(), 60, false);
		}
		showSavedFlag();
	}

	@Override
	public void requestRemoved(CodaStampa codaStampa, PdfStopperRequest request)
	{
		final PdfStopperStyle style = request.getStyle();
		final StyleCodaStampaPanel csPanel = csPanels.get(style);
		if (csPanel.getTable().getModel().getRowCount() == 0)
		{
			csPanel.setVisible(false);
			setCodeVisibili(codeVisibili - 1);
		}
		csPanel.getTable().getModel().fireTableDataChanged();
		showSavedFlag();
	}

	@Override
	public void requestMoved(CodaStampa codaStampa, PdfStopperRequest request, PdfStopperStyle oldStyle, PdfStopperStyle newStyle)
	{
		if (csPanels.get(oldStyle).getTable().getModel().getRowCount() == 0)
		{
			csPanels.get(oldStyle).setVisible(false);
			setCodeVisibili(codeVisibili - 1);
		}
		if (!csPanels.get(newStyle).isVisible())
		{
			csPanels.get(newStyle).setVisible(true);
			setCodeVisibili(codeVisibili + 1);
		}
		csPanels.get(oldStyle).getTable().getModel().fireTableDataChanged();
		csPanels.get(newStyle).getTable().getModel().fireTableDataChanged();

		this.selectRequest(request);
	}

	private void setCodeVisibili(int value)
	{
		codeVisibili = value;
		emptyQueuePanel.setVisible(codeVisibili == 0);
	}

	private void setCodeConStopper(int value)
	{
		codeConStopper = value;
		generateAllButton.setEnabled(codeConStopper > 0);
	}

	@Override
	public boolean askPermissionToOverwrite(File file)
	{

		int answer = JOptionPane.showConfirmDialog(this, "Il file " + file.getAbsolutePath() + " esiste già.\n Vuoi sostituirlo?",
				"Salvataggio su file esistente", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null);
		return answer == 0;
	}

	@Override
	public ConfirmDialogOptions askSaveBeforeClosing()
	{
		if (file != null)
		{
			int answer = JOptionPane.showConfirmDialog(this, "Salvare le modifiche apportate al file " + file.getAbsolutePath()
					+ " prima di chiudere?", "Salvataggio in chiusura", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null);
			return ConfirmDialogOptions.values()[answer];
		}
		else
		{
			return askSaveAsNewFile();
		}
	}

	public ConfirmDialogOptions askSaveAsNewFile()
	{
		int answer = JOptionPane.showConfirmDialog(this, "Salvare la coda di stampa in nuovo file?", "Salvataggio in nuovo file",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null);
		if (answer == JOptionPane.YES_OPTION)
		{
			int returnVal = MainMenu.fileChooser.showDialog(this, "Salva");

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{

				try
				{
					observer.saveAs(MainMenu.fileChooser.getSelectedFile());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return ConfirmDialogOptions.values()[answer];
	}

	@Override
	public void stopperChanged(PdfStopperRequest request, PdfStopper<?> oldStopper, PdfStopper<?> newStopper)
	{
		StyleCodaStampaPanel panel = csPanels.get(request.getStyle());
		int rowChanged = panel.getTable().getModel().getRequests().indexOf(request);
		if (rowChanged>=0)
		{
			if (!oldStopper.getClass().equals(newStopper.getClass()))
			{
				panel.getTable().getModel().fireTableCellUpdated(rowChanged, ColumnHeader.TIPO.ordinal());
			}
			if (!Utils.equalsWithNulls(oldStopper.getCodice(), newStopper.getCodice()))
			{
				panel.getTable().getModel().fireTableCellUpdated(rowChanged, ColumnHeader.CODICE.ordinal());
				if (newStopper.getCodice()!=null)
					panel.getTable().resizeCodiceLength(newStopper.getCodice().valore.length());
			}
			if (!oldStopper.getDescrizione().equals(newStopper.getDescrizione()))
			{
				panel.getTable().getModel().fireTableCellUpdated(rowChanged, ColumnHeader.DESCRIZIONE.ordinal());

			}
			if (oldStopper.getStopper() instanceof PromoStopper && newStopper.getStopper() instanceof PromoStopper)
			{
				PromoStopper oldPs = (PromoStopper) oldStopper.getStopper();
				PromoStopper newPs = (PromoStopper) newStopper.getStopper();
				if (!Utils.equalsWithNulls(oldPs.getPeriodo(), newPs.getPeriodo()));
				{
					panel.getTable().getModel().fireTableCellUpdated(rowChanged, ColumnHeader.DATA_INIZIO.ordinal());
					panel.getTable().getModel().fireTableCellUpdated(rowChanged, ColumnHeader.DATA_FINE.ordinal());
				}
			}
			if (!Utils.equalsWithNulls(oldStopper.getPrezzo(), newStopper.getPrezzo()))
			{
				panel.getTable().getModel().fireTableCellUpdated(rowChanged, ColumnHeader.PREZZO.ordinal());
			}
			if (panel.getTable().getRowSorter().convertRowIndexToView(rowChanged)==-1)
				panel.getTable().getModel().fireTableDataChanged();
		}

	}

	@Override
	public void copiesChanged(PdfStopperRequest request, int oldCopies, int newCopies)
	{
		int rowChanged = csPanels.get(request.getStyle()).getTable().getModel().getRequests().indexOf(request);
		csPanels.get(request.getStyle()).getTable().getModel().fireTableCellUpdated(rowChanged, ColumnHeader.COPIE.ordinal());
	}

	@Override
	public void styleChanged(PdfStopperRequest request, PdfStopperStyle oldStyle, PdfStopperStyle newStyle)
	{
		// change in codastampa will take care of this
	}

	@Override
	public boolean askPermissionToClear()
	{
		int answer = JOptionPane.showConfirmDialog(this, "Questa operazione annullerà tutte le modifiche effettuate. Procedere?",
				"Reset coda di stampa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null); // default
																										// button
																										// title
		return answer == 0;
	}

	@Override
	public void requestChanged(CodaStampa codaStampa, PdfStopperRequest request)
	{
		showSavedFlag();
	}

	private void showSavedFlag()
	{
		String title = tab.getTitleLabel().getText();
		if (!title.startsWith("* "))
			tab.getTitleLabel().setText("* " + title);
	}

	@Override
	public void stopperCountChanged(CodaStampa codaStampa, PdfStopperStyle style, int oldCount, int newCount)
	{
		csPanels.get(style).setStopperCount(newCount);
		if (oldCount == 0 && newCount > 0)
			setCodeConStopper(codeConStopper + 1);
		else if (newCount == 0 && oldCount > 0)
			setCodeConStopper(codeConStopper - 1);
	}


	@Override
	public void valueChanged(final List<PdfStopperRequest> value)
	{
		pasteButton.setEnabled(value != null);
	}

	@Override
	public void setClipboard(Clipboard<List<PdfStopperRequest>> clipboard)
	{
		if (this.clipboard != null)
			this.clipboard.detachObserver(this);
		this.clipboard = clipboard;
		if (this.clipboard != null)
		{
			pasteButton.setEnabled(clipboard.getValue() != null);
			this.clipboard.attachObserver(this);
		}
	}

	@Override
	public boolean askPermissionToDelete(File file)
	{

		int answer = JOptionPane.showConfirmDialog(this, "Acquisizione da lettore laser completata.\n"+ 
				"Cancellare il file " + file.getName() + "?",
				"Eliminazione file lettore laser", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
		return answer == 0;
		
	}
	
	static final Object[] options = {"Ho chiuso, continua.", "Annulla"};

	@Override
	public boolean askToCloseOpenFile(File fileInUse)
	{
		int answer = JOptionPane.showOptionDialog(this, "Il PDF di nome \'" + fileInUse.getName() + "\' deve essere sovrascritto "
				+ "\nma attualmente risulta aperto da un altro programma.\n\n"+
				"Chiudi il file e riprova oppure annulla la generazione del PDF.",
				"PDF in uso", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0] );
		return answer == 0;
	}


}
