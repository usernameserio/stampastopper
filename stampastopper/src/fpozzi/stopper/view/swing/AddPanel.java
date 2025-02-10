package fpozzi.stopper.view.swing;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import fpozzi.gdoshop.model.offerta.PeriodoOfferta;
import fpozzi.stopper.StampaStopperProperties;
import fpozzi.stopper.StampaStopperProperties.StampaStopperProperty;
import fpozzi.stopper.model.pdf.CardPdf;
import fpozzi.stopper.model.pdf.FruttaPdfStopper;
import fpozzi.stopper.model.pdf.GastroPdfStopper;
import fpozzi.stopper.model.pdf.PdfStopper;
import fpozzi.stopper.model.pdf.PdfStopperStyle;
import fpozzi.stopper.model.pdf.TaglioPrezzoPdfStopper;
import fpozzi.stopper.model.pdf.cell.FruttaPdfCellFactory;
import fpozzi.stopper.model.pdf.cell.GastroPdfCellFactory;
import fpozzi.stopper.model.pdf.cell.TaglioPrezzoPdfCellFactory;
import fpozzi.stopper.view.AddView;
import fpozzi.stopper.view.swing.codastampa.CodaStampaButton;
import fpozzi.utils.date.DateUtils;

public class AddPanel extends JPanel implements AddView
{
	private static final long serialVersionUID = 1L;

	private AddViewObserver observer;

	private final Map<Class<? extends PdfStopper<?>>, AddStopperSelector> addStopperSelectors;

	private final CodaStampaButton gdoShopButton;
	private final GDOshopPopupMenu gdoshopPopupMenu;

	public AddPanel()
	{
		addStopperSelectors = new HashMap<Class<? extends PdfStopper<?>>, AddStopperSelector>();

		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		setOpaque(false);

		add(new JSeparator(JSeparator.VERTICAL));
		add(new JSeparator(JSeparator.VERTICAL));
		add(Box.createHorizontalStrut(5));

		/*
		 * JLabel aggiungiLabel = new JLabel(" Aggiungi stopper ");
		 * aggiungiLabel.setFont(Style.Fonts.italicFont);
		 * aggiungiLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		 * aggiungiPanel.add(aggiungiLabel);
		 */

		if (StampaStopperProperties.getInstance().getProperty(StampaStopperProperty.ENABLE_GDOSHOP).equals("true"))
		{
			gdoshopPopupMenu = new GDOshopPopupMenu();
			gdoShopButton = new CodaStampaButton("GDOshop", Icons.GDOSHOP.image, new AbstractAction()
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e)
				{
					gdoshopPopupMenu.show(gdoShopButton, 0, gdoShopButton.getHeight());
				}
			});
			gdoShopButton.addMouseListener(new MouseAdapter()
			{
				public void mousePressed(MouseEvent me)
				{
					if (SwingUtilities.isRightMouseButton(me))
					{
						gdoShopButton.doClick();
					}
				}
			});
			add(gdoShopButton);
			add(Box.createHorizontalStrut(5));
			add(new JSeparator(JSeparator.VERTICAL));
			add(Box.createHorizontalStrut(5));
		}
		else
		{
			gdoshopPopupMenu =  null;
			gdoShopButton = null;
		}

		CodaStampaButton addStopperButton;
		AddStopperPopupMenu addStopperPopupmenu;
		AddStopperSelector addStopperSelector;

		if (StampaStopperProperties.getInstance().getProperty(StampaStopperProperty.ENABLE_PROMO).equals("true"))
		{
			addStopperButton = new CodaStampaButton("Promo", Icons.ADD_RED.image);
			add(addStopperButton);
			add(Box.createHorizontalStrut(5));
			addStopperPopupmenu = new AddStopperPopupMenu(TaglioPrezzoPdfStopper.class,
					TaglioPrezzoPdfCellFactory.instance.getSupportedStyles());
			addStopperSelector = new AddStopperSelector(TaglioPrezzoPdfStopper.class, addStopperButton,
					addStopperPopupmenu);
			addStopperSelectors.put(addStopperSelector._stopperClass, addStopperSelector);
		}

		if (StampaStopperProperties.getInstance().getProperty(StampaStopperProperty.ENABLE_GASTRO).equals("true"))
		{
			addStopperButton = new CodaStampaButton("Gastro", Icons.ADD_YELLOW.image);
			add(addStopperButton);
			add(Box.createHorizontalStrut(5));
			addStopperPopupmenu = new AddStopperPopupMenu(GastroPdfStopper.class,
					GastroPdfCellFactory.instance.getSupportedStyles());
			addStopperSelector = new AddStopperSelector(GastroPdfStopper.class, addStopperButton, addStopperPopupmenu);
			addStopperSelectors.put(addStopperSelector._stopperClass, addStopperSelector);
		}

		if (StampaStopperProperties.getInstance().getProperty(StampaStopperProperty.ENABLE_FRUTTA).equals("true"))
		{
			addStopperButton = new CodaStampaButton("Frutta", Icons.ADD.image);
			add(addStopperButton);
			add(Box.createHorizontalStrut(5));
			addStopperPopupmenu = new AddStopperPopupMenu(FruttaPdfStopper.class,
					FruttaPdfCellFactory.instance.getSupportedStyles());
			addStopperSelector = new AddStopperSelector(FruttaPdfStopper.class, addStopperButton, addStopperPopupmenu);
			addStopperSelectors.put(addStopperSelector._stopperClass, addStopperSelector);
		}

		if (StampaStopperProperties.getInstance().getProperty(StampaStopperProperty.ENABLE_CARDS).equals("true"))
		{
			addStopperButton = new CodaStampaButton("Card", Icons.ADD_BLUE.image);
			add(addStopperButton);
			add(Box.createHorizontalStrut(5));
			addStopperSelector = new AddStopperSelector(CardPdf.class, addStopperButton, null);
			addStopperSelectors.put(addStopperSelector._stopperClass, addStopperSelector);
		}
		add(new JSeparator(JSeparator.VERTICAL));
		add(Box.createHorizontalStrut(5));
	}

	@Override
	public void setObserver(AddViewObserver observer)
	{
		this.observer = observer;
	}

	public class AddStopperSelector
	{
		private final Class<? extends PdfStopper<?>> _stopperClass;
		private PdfStopperStyle defaultStyle;

		private CodaStampaButton _button;
		private AddStopperPopupMenu _popup;

		private AddStopperSelector(Class<? extends PdfStopper<?>> stopperClass, CodaStampaButton button,
				AddStopperPopupMenu popup)
		{
			this._stopperClass = stopperClass;
			defaultStyle = null;

			this._button = button;
			String buttonText = button.getText();
			Icon buttonIcon = button.getIcon();
			button.setAction(new AbstractAction()
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e)
				{
					observer.makeNewRequest(_stopperClass, defaultStyle);
				}
			});
			button.setText(buttonText);
			button.setIcon(buttonIcon);
			button.addMouseListener(new MouseAdapter()
			{
				public void mousePressed(MouseEvent me)
				{
					if (SwingUtilities.isRightMouseButton(me) && _popup != null)
					{
						_popup.show(_button, 0, _button.getHeight());
					}
				}

			});

			this._popup = popup;
		}

		public void setDefaultStyle(PdfStopperStyle style)
		{
			if (_popup != null && defaultStyle != null)
			{
				_popup.styleItems.get(defaultStyle).setIcon(null);
			}
			this.defaultStyle = style;
			if (_popup != null)
				_popup.styleItems.get(defaultStyle).setIcon(Icons.TICK.image);
		}
	}

	private class AddStopperPopupMenu extends JPopupMenu
	{
		private static final long serialVersionUID = 1L;

		private final Class<? extends PdfStopper<?>> _stopperClass;
		private final Map<PdfStopperStyle, JMenuItem> styleItems;

		public AddStopperPopupMenu(Class<? extends PdfStopper<?>> stopperClass, List<PdfStopperStyle> supportedStyles)
		{
			this._stopperClass = stopperClass;
			this.styleItems = new HashMap<PdfStopperStyle, JMenuItem>();

			for (final PdfStopperStyle style : supportedStyles)
			{
				final JMenuItem addNewMenuItem = new JMenuItem(new AbstractAction()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						observer.makeNewRequest(_stopperClass, style);
					}
				})
				{
					private static final long serialVersionUID = 1L;

					protected void processMouseEvent(MouseEvent me)
					{
						if (!SwingUtilities.isRightMouseButton(me))
						{
							super.processMouseEvent(me);
						} else if (me.getID() == MouseEvent.MOUSE_RELEASED)
						{

							AddStopperPopupMenu.this.setVisible(false);
							int answer = JOptionPane.showConfirmDialog(AddStopperPopupMenu.this,
									"Impostare lo stile \n" + "\"" + style.getDescrizione() + "\""
											+ "\n come predefinito per questo tipo di stopper?",
									"Conferma stile predefinito", JOptionPane.YES_NO_OPTION);

							if (answer == JOptionPane.YES_OPTION)
								observer.setDefaultStyle(_stopperClass, style);
							AddStopperPopupMenu.this.setVisible(true);
						}
					}
				};
				addNewMenuItem.setText(style.getDescrizione());
				add(addNewMenuItem);
				styleItems.put(style, addNewMenuItem);
			}
		}
	}

	private class GDOshopPopupMenu extends JPopupMenu
	{
		private static final long serialVersionUID = 1L;

		private JMenu fromGDOshopMenu;
		private JMenu fromTerminalino;

		public GDOshopPopupMenu()
		{
			fromGDOshopMenu = new JMenu("Periodo completo");
			fromGDOshopMenu.setIcon(Icons.CALENDAR.image);
			add(fromGDOshopMenu);

			fromTerminalino = new JMenu("Lettore laser");
			fromTerminalino.setIcon(Icons.SCANNING.image);
			add(fromTerminalino);

			addPopupMenuListener(new PopupMenuListener()
			{

				@Override
				public void popupMenuCanceled(PopupMenuEvent arg0)
				{
					// TODO Auto-generated method stub

				}

				@Override
				public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0)
				{
					fromGDOshopMenu.removeAll();
					fromTerminalino.removeAll();
				}

				@Override
				public void popupMenuWillBecomeVisible(PopupMenuEvent arg0)
				{

					JMenuItem periodoMenuItem;
					List<PeriodoOfferta> periodi = observer.getAllPeriodi();
					for (final PeriodoOfferta periodo : periodi)
					{
						periodoMenuItem = new JMenuItem(new AbstractAction()
						{
							private static final long serialVersionUID = 1L;

							@Override
							public void actionPerformed(ActionEvent e)
							{
								observer.importRequests(periodo);
							}
						});
						periodoMenuItem.setText(periodo.toString());
						fromGDOshopMenu.add(periodoMenuItem);
					}

					JMenuItem fileLettoreLaserMenuItem;
					List<File> fileLettoreLaser = observer.getAllLettoreLaserFiles();
					for (final File file : fileLettoreLaser)
					{
						fileLettoreLaserMenuItem = new JMenuItem(new AbstractAction()
						{
							private static final long serialVersionUID = 1L;

							@Override
							public void actionPerformed(ActionEvent e)
							{
								observer.importRequests(file);
							}
						});
						fileLettoreLaserMenuItem.setText(
								DateUtils.italianDateTimeFormat.format(file.lastModified()) + " " + file.getName());
						fromTerminalino.add(fileLettoreLaserMenuItem);
					}

				}

			});
		}

	}

	@Override
	public void setDefaultStyle(Class<? extends PdfStopper<?>> stopperClass, PdfStopperStyle style)
	{
		AddStopperSelector selector = addStopperSelectors.get(stopperClass);
		if (selector != null)
			addStopperSelectors.get(stopperClass).setDefaultStyle(style);
	}

}