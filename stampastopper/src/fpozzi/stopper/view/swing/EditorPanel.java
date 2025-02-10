package fpozzi.stopper.view.swing;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.positioners.LeftAbovePositioner;
import fpozzi.stopper.StampaStopperLogger;
import fpozzi.stopper.model.pdf.CardPdf;
import fpozzi.stopper.model.pdf.EmptyPdfStopper;
import fpozzi.stopper.model.pdf.FruttaPdfStopper;
import fpozzi.stopper.model.pdf.GastroPdfStopper;
import fpozzi.stopper.model.pdf.PdfPromoStopper;
import fpozzi.stopper.model.pdf.PdfStopper;
import fpozzi.stopper.model.pdf.PdfStopperRequest;
import fpozzi.stopper.model.pdf.PdfStopperRequestObserver;
import fpozzi.stopper.model.pdf.PdfStopperStyle;
import fpozzi.stopper.model.pdf.PrezzacciPdfStopper;
import fpozzi.stopper.model.pdf.ScontoPercentualePdfStopper;
import fpozzi.stopper.model.pdf.TaglioPrezzoPdfStopper;
import fpozzi.stopper.model.pdf.UnoPiuUnoPdfStopper;
import fpozzi.stopper.model.pdf.UnsupportedStyleException;
import fpozzi.stopper.view.CardEditorView;
import fpozzi.stopper.view.EditorView;
import fpozzi.stopper.view.PromoStopperEditorView;
import fpozzi.stopper.view.StopperEditorView;
import fpozzi.utils.swing.JTitleBar;

public class EditorPanel extends JPanel implements EditorView, PdfStopperRequestObserver
{
	private static final long serialVersionUID = 1L;

	// private final JFormattedTextField copieField;
	private final JComboBox<PdfStopperStyle> stileComboBox;

	private final String PROMO = "PROMO", GASTRO = "GASTRO", FRUTTA = "FRUTTA", CARD = "CARD", VUOTO = "VUOTO";
	private final PromoStopperEditorPanel promoEditorPanel;
	private final EmptyStopperEditorPanel emptyEditorPanel;
	private final GastroStopperEditorPanel gastroEditorPanel;
	private final FruttaStopperEditorPanel fruttaEditorPanel;
	private final CardEditorPanel cardEditorPanel;

	private final JPanel requestEditorPanel;
	private final JPanel stopperEditorPanel;
	private final JPanel nothingToEditPanel;
	private StopperEditorPanel<?> activeEditorPanel;
	private final CardLayout stopperEditorPanelLayout;

	private final JLabel previewLabel;
	private BalloonTip previewBalloonTip;

	private final List<EditorViewObserver> observers;

	private PdfStopperRequest currentRequest;

	public EditorPanel()
	{
		super(new BorderLayout());

		observers = new LinkedList<EditorViewObserver>();

		this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		this.setPreferredSize(new Dimension(280, 500));

		this.add(new JTitleBar("Editor", Style.Fonts.boldFont, Style.titleFontColor, null, Style.titleBgColor, Style.titleBgColor2),
				BorderLayout.NORTH);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createLineBorder(Style.titleBgColor));

		requestEditorPanel = new JPanel(new BorderLayout());

		nothingToEditPanel = new JPanel();
		nothingToEditPanel.add(new JLabel("Nessuno stopper selezionato"));

		mainPanel.add(nothingToEditPanel, BorderLayout.NORTH);

		JPanel stilePanel = new JPanel(new BorderLayout());
		stilePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		stilePanel.add(new JLabel("Stile  "), BorderLayout.WEST);
		stileComboBox = new JComboBox<PdfStopperStyle>();
		stileComboBox.setMaximumRowCount(3);
		stileComboBox.setRenderer(new PdfStopperStyleListCellRenderer());
		stileComboBox.setEditable(true);
		stileComboBox.setEditor(new PdfStopperStyleComboBoxEditor());
		stileComboBox.setPreferredSize(new Dimension(120, 21));
		stilePanel.add(stileComboBox, BorderLayout.CENTER);
		stilePanel.setBackground(new Color(230, 230, 230));

		requestEditorPanel.add(stilePanel, BorderLayout.NORTH);

		stopperEditorPanel = new JPanel();
		stopperEditorPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.LIGHT_GRAY));
		stopperEditorPanelLayout = new CardLayout();
		stopperEditorPanel.setLayout(stopperEditorPanelLayout);
		requestEditorPanel.add(stopperEditorPanel, BorderLayout.CENTER);

		promoEditorPanel = new PromoStopperEditorPanel();
		stopperEditorPanel.add(promoEditorPanel, PROMO);
		gastroEditorPanel = new GastroStopperEditorPanel();
		stopperEditorPanel.add(gastroEditorPanel, GASTRO);
		fruttaEditorPanel = new FruttaStopperEditorPanel();
		stopperEditorPanel.add(fruttaEditorPanel, FRUTTA);
		emptyEditorPanel = new EmptyStopperEditorPanel();
		stopperEditorPanel.add(emptyEditorPanel, VUOTO);
		cardEditorPanel = new CardEditorPanel();
		stopperEditorPanel.add(cardEditorPanel, CARD);

		JPanel cmdPanel = new JPanel();
		cmdPanel.setBackground(new Color(230, 230, 230));

		cmdPanel.add(Box.createRigidArea(new Dimension(40, 0)));

		JButton saveButton = new JButton("Applica modifiche   ");
		saveButton.setIcon(Icons.TICK.image);
		saveButton.setMargin(new Insets(8, 25, 8, 25));
		saveButton.setOpaque(false);
		saveButton.setVerticalTextPosition(SwingConstants.CENTER);
		saveButton.setHorizontalTextPosition(SwingConstants.LEFT);
		saveButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent evt)
			{
				for (EditorViewObserver observer : observers)
				{
					PdfStopperRequest requestToBeSaved = currentRequest;
					try
					{
						observer.changeStyle(requestToBeSaved, stileComboBox.getModel().getElementAt(stileComboBox.getSelectedIndex()));
						observer.changeStopper(requestToBeSaved, activeEditorPanel.getPdfStopper());
					}
					catch (UnsupportedStyleException e)
					{
						StampaStopperLogger.get().info(
								"Stopper '" + requestToBeSaved.getPdfStopper().getDescrizione() + "' non può essere modificato, stile non valido.");
						setStopperRequest(requestToBeSaved);
					}
				}
			}

		});

		cmdPanel.add(saveButton);

		previewBalloonTip = null;
		previewLabel = new JLabel(Icons.EYE.image);
		previewLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		previewLabel.addMouseListener(new MouseListener()
		{

			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent arg0)
			{
				try
				{
					previewLabel.requestFocus();

					Image image = activeEditorPanel.getPdfStopper().getPreview(
							stileComboBox.getModel().getElementAt(stileComboBox.getSelectedIndex()),
							(int)(getWindow().getWidth()*0.7f),  (int)(getWindow().getHeight()*0.7f)
							);

					if (previewBalloonTip != null)
						previewBalloonTip.closeBalloon();

					previewBalloonTip = new BalloonTip(previewLabel, "Anteprima", Style.makePreviewTipBalloonStyle(), false);
					JLabel previewLabel = new JLabel(new ImageIcon(image));
					JPanel previewPanel = new JPanel();
					previewPanel.setOpaque(false);
					previewPanel.add(previewLabel);
					previewLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
					previewBalloonTip.setContents(previewPanel);
					previewBalloonTip.setPositioner(new LeftAbovePositioner(20, 10));
				}
				catch (Exception e)
				{
					StampaStopperLogger.get().info("Impossibile creare anteprima, stile non supportato.");
				}
			}

			@Override
			public void mouseExited(MouseEvent arg0)
			{

				if (previewBalloonTip != null)
					previewBalloonTip.closeBalloon();
				previewBalloonTip = null;
			}

			@Override
			public void mousePressed(MouseEvent arg0)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent arg0)
			{
				// TODO Auto-generated method stub

			}

		});

		cmdPanel.add(previewLabel);

		requestEditorPanel.add(cmdPanel, BorderLayout.SOUTH);

		mainPanel.add(requestEditorPanel, BorderLayout.CENTER);

		add(mainPanel);
	}
	
	private Window window = null;
	
	private Window getWindow()
	{
		if (window == null)
			window = SwingUtilities.getWindowAncestor(this);
		return window;
	}

	@Override
	public void attachObserver(EditorViewObserver observer)
	{
		observers.add(observer);
	}

	@Override
	public void detachObserver(EditorViewObserver observer)
	{
		observers.remove(observer);
	}

	@Override
	public PromoStopperEditorView getPromoStopperEditorView()
	{
		return promoEditorPanel;
	}

	@Override
	public StopperEditorView<EmptyPdfStopper> getEmptyStopperEditorView()
	{
		return emptyEditorPanel;
	}

	@Override
	public StopperEditorView<FruttaPdfStopper> getFruttaStopperEditorView()
	{
		return fruttaEditorPanel;
	}

	@Override
	public StopperEditorView<GastroPdfStopper> getGastroStopperEditorView()
	{
		return gastroEditorPanel;
	}

	@Override
	public CardEditorView getCardEditorView()
	{
		return cardEditorPanel;
	}

	public void edit(PdfPromoStopper promoStopper)
	{
		stopperEditorPanelLayout.show(stopperEditorPanel, PROMO);
		activeEditorPanel = promoEditorPanel;
		promoEditorPanel.setPdfStopper(promoStopper);
	}

	@Override
	public void edit(PrezzacciPdfStopper stopper)
	{
		edit((PdfPromoStopper) stopper);

	}

	@Override
	public void edit(ScontoPercentualePdfStopper stopper)
	{
		edit((PdfPromoStopper) stopper);
	}

	@Override
	public void edit(TaglioPrezzoPdfStopper stopper)
	{
		edit((PdfPromoStopper) stopper);
	}

	@Override
	public void edit(UnoPiuUnoPdfStopper stopper)
	{
		edit((PdfPromoStopper) stopper);
	}

	@Override
	public void edit(FruttaPdfStopper fruttaPdftopper)
	{
		stopperEditorPanelLayout.show(stopperEditorPanel, FRUTTA);
		activeEditorPanel = fruttaEditorPanel;
		fruttaEditorPanel.setPdfStopper(fruttaPdftopper);
	}

	@Override
	public void edit(GastroPdfStopper gastronomiaPdfStopper)
	{
		stopperEditorPanelLayout.show(stopperEditorPanel, GASTRO);
		activeEditorPanel = gastroEditorPanel;
		gastroEditorPanel.setPdfStopper(gastronomiaPdfStopper);
	}

	@Override
	public void edit(CardPdf pdfCard)
	{
		stopperEditorPanelLayout.show(stopperEditorPanel, CARD);
		activeEditorPanel = cardEditorPanel;
		cardEditorPanel.setPdfStopper(pdfCard);
	}

	@Override
	public void edit(EmptyPdfStopper stopperVuoto)
	{
		stopperEditorPanelLayout.show(stopperEditorPanel, VUOTO);
		activeEditorPanel = emptyEditorPanel;
		emptyEditorPanel.setPdfStopper(stopperVuoto);
	}

	@Override
	public void setStopperRequest(PdfStopperRequest stopperRequest)
	{
		if (currentRequest != null)
			currentRequest.detachObserver(this);
		currentRequest = stopperRequest;
		if (previewBalloonTip != null)
			previewBalloonTip.closeBalloon();

		stileComboBox.removeAllItems();
		// copieField.setValue(null);
		if (stopperRequest == null)
		{
			nothingToEditPanel.setVisible(true);
			requestEditorPanel.setVisible(false);
			stileComboBox.setEnabled(false);
		}
		else
		{
			currentRequest.attachObserver(this);
			nothingToEditPanel.setVisible(false);
			requestEditorPanel.setVisible(true);
			for (PdfStopperStyle stile : stopperRequest.getPdfStopper().getCellFactory().getSupportedStyles())
				stileComboBox.addItem(stile);
			stileComboBox.setSelectedItem(stopperRequest.getStyle());
			// copieField.setValue(stopperRequest.getCopies());
			stileComboBox.setEnabled(true);
			// copieField.setEnabled(true);
			stopperRequest.getPdfStopper().acceptEditor(this);
		}
	}

	@Override
	public void stopperChanged(PdfStopperRequest request, PdfStopper<?> oldStopper, PdfStopper<?> newStopper)
	{
		if (request == currentRequest)
			setStopperRequest(request);
	}

	@Override
	public void styleChanged(PdfStopperRequest request, PdfStopperStyle oldStyle, PdfStopperStyle newStyle)
	{
		if (request == currentRequest)
			stileComboBox.setSelectedItem(newStyle);
	}

	@Override
	public void copiesChanged(PdfStopperRequest request, int oldCopies, int newCopies)
	{
		// do nothing

	}

	private class PdfStopperStyleListCellRenderer extends JLabel implements ListCellRenderer<PdfStopperStyle>
	{

		private static final long serialVersionUID = 1L;

		private PdfStopperStyleListCellRenderer()
		{
			this.setHorizontalAlignment(SwingConstants.CENTER);
			this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			this.addMouseListener(new MouseAdapter()
			{

				public void mouseEntered(MouseEvent e)
				{
					setBackground(Color.RED);

				}

				@Override
				public void mouseExited(MouseEvent e)
				{
					setBackground(Color.WHITE);

				}

			});
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends PdfStopperStyle> list, PdfStopperStyle value, int index, boolean isSelected,
				boolean cellHasFocus)
		{
			if (value != null)
			{
				if (isSelected)
				{
					setBackground(Color.BLUE);
				}
				else
				{
					setBackground(Color.BLACK);
				}

				ImageIcon icon = new ImageIcon(value.getRappresentazione());
				setIcon(icon);
			}

			return this;
		}

	}

	private class PdfStopperStyleComboBoxEditor extends BasicComboBoxEditor
	{

		private final JLabel label;
		private PdfStopperStyle selectedStyle;

		private PdfStopperStyleComboBoxEditor()
		{
			label = new JLabel();
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.addMouseListener(new MouseAdapter()
			{

				@Override
				public void mouseClicked(MouseEvent arg0)
				{
					if (stileComboBox.isPopupVisible())
						stileComboBox.hidePopup();
					else
						stileComboBox.showPopup();
				}

			});
		}

		public Object getItem()
		{
			return this.selectedStyle.getDescrizione();
		}

		public void setItem(Object item)
		{
			this.selectedStyle = (PdfStopperStyle) item;
			if (selectedStyle == null)
			{
				label.setText("");
				label.setToolTipText("");
			}
			else
			{
				label.setText(selectedStyle.getDescrizione());
				label.setToolTipText(selectedStyle.getDescrizione());
			}
		}

		public Component getEditorComponent()
		{
			return label;
		}
	}

}
