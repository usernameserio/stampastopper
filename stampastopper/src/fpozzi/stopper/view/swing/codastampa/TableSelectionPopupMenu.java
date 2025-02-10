package fpozzi.stopper.view.swing.codastampa;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import fpozzi.gdoshop.model.offerta.PeriodoOfferta;
import fpozzi.stopper.model.PromoStopper;
import fpozzi.stopper.model.pdf.MergeException;
import fpozzi.stopper.model.pdf.PdfStopperRequest;
import fpozzi.stopper.model.pdf.PdfStopperStyle;
import fpozzi.stopper.view.swing.Icons;
import fpozzi.utils.date.DateUtils;

class TableSelectionPopupMenu extends JPopupMenu
{
	private static final long serialVersionUID = 1L;

	private final StyleCodaStampaTable csTable;

	private final JMenu cambiaStileMenu;

	private final JMenuItem cambiaDateMenuItem, nessunaDataMenuItem, unisciMenuItem;

	private PdfStopperRequest lastSelectedRequest;
	private List<PdfStopperRequest> selectedRequests;

	public TableSelectionPopupMenu(StyleCodaStampaTable styleCodaStampaTable)
	{

		csTable = styleCodaStampaTable;

		JMenuItem duplicateItem = new JMenuItem("Duplica", KeyEvent.VK_D);
		duplicateItem.setIcon(Icons.DUPLICA.image);
		duplicateItem.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				for (PdfStopperRequest request : selectedRequests)
				{
					csTable.getStyleCodaStampaPanel().getCodaStampaPanel().getObserver().duplicate(request);
				}

			}

		});
		add(duplicateItem);

		JMenuItem copyItem = new JMenuItem("Copia", KeyEvent.VK_V);
		copyItem.setIcon(Icons.COPIA.image);
		copyItem.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if (selectedRequests.size() > 0)
					csTable.getStyleCodaStampaPanel().getCodaStampaPanel().getClipboard().setValue(selectedRequests);
			}

		});
		add(copyItem);

		JMenuItem removeItem = new JMenuItem("Rimuovi", KeyEvent.VK_R);
		removeItem.setIcon(Icons.CROSS.image);
		removeItem.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				csTable.getStyleCodaStampaPanel().removeSelectedRows();
			}

		});
		add(removeItem);

		addSeparator();

		unisciMenuItem = new JMenuItem("Unisci", KeyEvent.VK_U);
		unisciMenuItem.setIcon(Icons.JOIN.image);
		unisciMenuItem.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					csTable.getStyleCodaStampaPanel().getCodaStampaPanel().getObserver().mergeRequests(selectedRequests, lastSelectedRequest);
				}
				catch (MergeException me)
				{
					JOptionPane.showMessageDialog(null,
							me.getMessage(),
						    "Operazione non riuscita",
						    JOptionPane.WARNING_MESSAGE);
				}
			}

		});
		add(unisciMenuItem);
		
		addSeparator();
		
		JMenuItem zeroCopiesMenuItem = new JMenuItem("Azzera copie", KeyEvent.VK_0);
		zeroCopiesMenuItem.setIcon(Icons.ZERO_COPIES.image);
		zeroCopiesMenuItem.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				for (int selRow : csTable.getSelectedRows())
				{
					csTable.getModel().getRequests().get(csTable.convertRowIndexToModel(selRow)).setCopies(0);
				}

			}

		});
		add(zeroCopiesMenuItem);

		addSeparator();

		cambiaStileMenu = new JMenu("Cambia stile");
		add(cambiaStileMenu);
		cambiaStileMenu.setIcon(Icons.STYLE_EDIT.image);

		addSeparator();

		cambiaDateMenuItem = new JMenuItem("Cambia date offerta");
		add(cambiaDateMenuItem);
		cambiaDateMenuItem.setIcon(Icons.CALENDAR_EDIT.image);
		cambiaDateMenuItem.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				int[] selRows = csTable.getSelectedRows();
				List<PdfStopperRequest> selectedRequests = new ArrayList<PdfStopperRequest>(selRows.length);
				Date dataInizio = null, dataFine = null;
				for (int selRow : selRows)
				{
					PdfStopperRequest selectedRequest = csTable.getModel().getRequests().get(selRow);
					if (selectedRequest.getPdfStopper().getStopper() instanceof PromoStopper)
					{
						selectedRequests.add(selectedRequest);
						PeriodoOfferta periodo = ((PromoStopper) selectedRequest.getPdfStopper().getStopper()).getPeriodo();
						if (periodo != null)
						{
							if (dataInizio == null)
								dataInizio = periodo.getInizio();
							else if (!dataInizio.equals(periodo.getInizio()))
								dataInizio = DateUtils.TODAY.getTime();

							if (dataFine == null)
								dataFine = periodo.getFine();
							else if (!dataFine.equals(periodo.getFine()))
								dataFine = DateUtils.TODAY.getTime();
						}
					}
				}

				UtilDateModel inizioDateModel = new UtilDateModel();
				inizioDateModel.setValue(dataInizio);
				JDatePanelImpl inizioDatePanel = new JDatePanelImpl(inizioDateModel);

				UtilDateModel fineDateModel = new UtilDateModel();
				fineDateModel.setValue(dataFine);
				JDatePanelImpl fineDatePanel = new JDatePanelImpl(fineDateModel);

				JOptionPane.showMessageDialog(null, new JComponent[] { new JLabel("Data inizio offerta"), inizioDatePanel,
						new JLabel("Data fine offerta"), fineDatePanel }, "Date offerta", JOptionPane.PLAIN_MESSAGE);

				PeriodoOfferta periodo = null;
				if (inizioDateModel.getValue() != null && fineDateModel.getValue() != null)
					periodo = new PeriodoOfferta(inizioDateModel.getValue(), fineDateModel.getValue());

				csTable.getStyleCodaStampaPanel().getCodaStampaPanel().getObserver().changePeriodoOfferta(selectedRequests, periodo);
			}

		});

		nessunaDataMenuItem = new JMenuItem("Nessuna data");
		nessunaDataMenuItem.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				int[] selRows = csTable.getSelectedRows();
				List<PdfStopperRequest> selectedRequests = new ArrayList<PdfStopperRequest>(selRows.length);
				for (int selRow : selRows)
				{
					PdfStopperRequest selectedRequest = csTable.getModel().getRequests().get(selRow);
					if (selectedRequest.getPdfStopper().getStopper() instanceof PromoStopper)
					{
						selectedRequests.add(selectedRequest);
					}
				}

				csTable.getStyleCodaStampaPanel().getCodaStampaPanel().getObserver().changePeriodoOfferta(selectedRequests, null);
			}

		});
		nessunaDataMenuItem.setIcon(Icons.CALENDAR_DELETE.image);
		add(nessunaDataMenuItem);

		addPopupMenuListener(new PopupMenuListener()
		{

			@Override
			public void popupMenuCanceled(PopupMenuEvent e)
			{
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
			{
				cambiaStileMenu.removeAll();
			}

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e)
			{
				lastSelectedRequest = null;

				int leadSelectionIndex = csTable.getSelectionModel().getLeadSelectionIndex();
				if (leadSelectionIndex >= 0)
					lastSelectedRequest = csTable.getModel().getRequests().get(csTable.convertRowIndexToModel(leadSelectionIndex));

				int[] selRows = csTable.getSelectedRows();
				selectedRequests = new ArrayList<PdfStopperRequest>(selRows.length);
				boolean hasPromos = false;
				for (int selRow : selRows)
				{
					PdfStopperRequest request = csTable.getModel().getRequests().get(csTable.convertRowIndexToModel(selRow));
					selectedRequests.add(request);
					hasPromos = hasPromos || (request.getPdfStopper().getStopper() instanceof PromoStopper);
				}
				nessunaDataMenuItem.setVisible(hasPromos);
				cambiaDateMenuItem.setVisible(hasPromos);

				LinkedList<PdfStopperStyle> stiliCondivisi = new LinkedList<PdfStopperStyle>();
				for (PdfStopperStyle stile : PdfStopperStyle.values())
					if (stile != csTable.getStyleCodaStampaPanel().getStyle())
						stiliCondivisi.add(stile);

				boolean unibili = selectedRequests.size() > 1;

				for (PdfStopperRequest selectedRequest : selectedRequests)
				{
					stiliCondivisi.retainAll(selectedRequest.getPdfStopper().getCellFactory().getSupportedStyles());
					unibili = unibili && selectedRequest.getPdfStopper().canBeMergedInto(lastSelectedRequest.getPdfStopper());
				}

				cambiaStileMenu.setEnabled(stiliCondivisi.size() > 0);

				unisciMenuItem.setEnabled(unibili);

				JMenuItem cambiaStileItem;

				for (final PdfStopperStyle stileCondiviso : stiliCondivisi)
				{
					cambiaStileItem = new JMenuItem(stileCondiviso.getDescrizione());
					cambiaStileItem.setIcon(new ImageIcon(stileCondiviso.getRappresentazione()));
					cambiaStileItem.addActionListener(new ActionListener()
					{
						@Override
						public void actionPerformed(ActionEvent arg0)
						{

							for (PdfStopperRequest request : selectedRequests)
								csTable.getStyleCodaStampaPanel().getCodaStampaPanel().getObserver().changeStyle(request, stileCondiviso);
						}

					});
					cambiaStileMenu.add(cambiaStileItem);
				}

			}

		});

	}

}