package fpozzi.stopper.view.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import fpozzi.stopper.view.swing.codastampa.CodaStampaPanel;

public class MainMenu
{
	private final MainWindow mainWindow;

	private final JMenuBar menuBar;
	private final JMenuItem saveMenuItem, saveAsMenuItem, saveAllMenuItem, refreshMenuItem, esciMenuItem;

	private final JPanel menuPanel;
	private final JButton newButton, openButton, refreshButton, saveButton, saveAsButton, saveAllButton, closeAllButton;//, settingsButton;

	private final static ImageIcon newIcon, openIcon, refreshIcon, saveIcon, saveAsIcon, settingsIcon, closeAllIcon, saveAllIcon, closeIcon;
	static public final JFileChooser fileChooser;

	static
	{
		newIcon = Icons.TAB_ADD.image;
		openIcon = new ImageIcon(MainMenu.class.getResource("/img/folder.png"));
		refreshIcon = new ImageIcon(MainMenu.class.getResource("/img/arrow_refresh.png"));
		saveIcon = new ImageIcon(MainMenu.class.getResource("/img/disk.png"));
		saveAsIcon = new ImageIcon(MainMenu.class.getResource("/img/drive_disk.png"));
		settingsIcon = new ImageIcon(MainMenu.class.getResource("/img/wrench.png"));
		saveAllIcon = new ImageIcon(MainMenu.class.getResource("/img/disk_multiple.png"));
		closeAllIcon = new ImageIcon(MainMenu.class.getResource("/img/table_multiple_close.png"));
		closeIcon = new ImageIcon(MainMenu.class.getResource("/img/door_in.png"));

		fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Seleziona un file");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(new FileFilter()
		{

			@Override
			public boolean accept(File file)
			{
				return file.isDirectory() || file.getName().endsWith(".xml");
			}

			@Override
			public String getDescription()
			{
				return "XML files";
			}
		});
	}

	public MainMenu(MainWindow mainWindow)
	{
		this.mainWindow = mainWindow;

		// BAR

		menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File ");
		fileMenu.setMnemonic('F');
		fileMenu.setAlignmentX(Component.CENTER_ALIGNMENT);

		JMenuItem openMenuitem = new MainMenuItem("Apri", openIcon, openAction);
		fileMenu.add(openMenuitem);

		refreshMenuItem = new MainMenuItem("Ricarica", refreshIcon, refreshAction);
		refreshMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		fileMenu.add(refreshMenuItem);

		saveMenuItem = new MainMenuItem("Salva", saveIcon, saveAction);
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		fileMenu.add(saveMenuItem);

		saveAsMenuItem = new MainMenuItem("Salva con nome", saveAsIcon, saveAsAction);
		fileMenu.add(saveAsMenuItem);
		
		saveAllMenuItem = new MainMenuItem("Salva tutto", saveAllIcon, saveAllAction);
		fileMenu.add(saveAllMenuItem);
		
		fileMenu.addSeparator();
		
		esciMenuItem = new MainMenuItem("Esci", closeIcon, exitAction);
		fileMenu.add(esciMenuItem);

		getMenuBar().add(fileMenu);

		// PANEL

		menuPanel = new JPanel(new BorderLayout());

		JPanel tabMenuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tabMenuPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		newButton = new MainMenuButton("Nuovo", newIcon, newAction);
		newButton.setAction(newAction);
		newButton.setBorder(BorderFactory.createEmptyBorder());
		newButton.setPreferredSize(new Dimension(20, 20));
		newAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control T"));
		newButton.getActionMap().put("newAction", newAction);
		newButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put((KeyStroke) newAction.getValue(Action.ACCELERATOR_KEY), "newAction");


		openButton = new MainMenuButton("Apri", openIcon, openAction);
		tabMenuPanel.add(openButton);

		refreshButton = new MainMenuButton("Ricarica", refreshIcon, refreshAction);
		tabMenuPanel.add(refreshButton);

		saveButton = new MainMenuButton("Salva", saveIcon, saveAction);
		tabMenuPanel.add(saveButton);

		saveAsButton = new MainMenuButton("Salva con nome", saveAsIcon, saveAsAction);
		tabMenuPanel.add(saveAsButton);

		saveAllButton = new MainMenuButton("Salva tutto", saveAllIcon, saveAllAction);
		tabMenuPanel.add(saveAllButton);

		closeAllButton = new MainMenuButton("Chiudi tutto", closeAllIcon, closeAllAction);
		tabMenuPanel.add(closeAllButton);

		menuPanel.add(tabMenuPanel, BorderLayout.CENTER);

		JPanel extraMenuPanel = new JPanel();
		/*
		 * extraMenuPanel.add(makeSeparator());
		 * 
		 * settingsButton = new MainMenuButton(settingsIcon);
		 * settingsButton.setToolTipText("Impostazioni");
		 * settingsButton.addAction(settingsAction);
		 * extraMenuPanel.add(settingsButton);
		 */
		menuPanel.add(extraMenuPanel, BorderLayout.EAST);

	}

	public JMenuBar getMenuBar()
	{
		return menuBar;
	}

	public Component getMenuPanel()
	{
		return menuPanel;
	}
	

	private final Action newAction = new AbstractAction()
	{

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			try
			{
				mainWindow.getObserver().newCodaStampa();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	};

	private final Action openAction = new AbstractAction()
	{
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed(ActionEvent evt)
		{
			int returnVal = fileChooser.showDialog(mainWindow, "Apri");

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
					try
					{
						mainWindow.getObserver().open(fileChooser.getSelectedFile());
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
			}
		}

	};

	private final Action saveAction = new AbstractAction()
	{
		private static final long serialVersionUID = 1L;
		
		@Override
		public void actionPerformed(ActionEvent evt)
		{
				try
				{
					mainWindow.getObserver().save();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
		}

	};

	private final Action saveAsAction = new AbstractAction()
	{
		private static final long serialVersionUID = 1L;
		
		@Override
		public void actionPerformed(ActionEvent evt)
		{
			if (mainWindow.getActiveCodaStampaPanel().getFile() != null)
				fileChooser.setSelectedFile(mainWindow.getActiveCodaStampaPanel().getFile());
			int returnVal = fileChooser.showDialog(mainWindow, "Salva");

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
					try
					{
						mainWindow.getObserver().saveAs(fileChooser.getSelectedFile());
						setActiveCodaStampaFile(mainWindow.getActiveCodaStampaPanel().getFile());
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
			}
		}

	};

	private final Action saveAllAction = new AbstractAction()
	{
		private static final long serialVersionUID = 1L;
		@Override
		public void actionPerformed(ActionEvent evt)
		{
				try
				{
					mainWindow.getObserver().saveAll();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
		}

	};

	private final Action closeAllAction = new AbstractAction()
	{
		private static final long serialVersionUID = 1L;
		@Override
		public void actionPerformed(ActionEvent evt)
		{
				try
				{
					mainWindow.getObserver().closeAll();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
		}

	};
	
	private final Action exitAction = new AbstractAction()
	{
		private static final long serialVersionUID = 1L;
		@Override
		public void actionPerformed(ActionEvent evt)
		{
			mainWindow.dispatchEvent(new WindowEvent(mainWindow, WindowEvent.WINDOW_CLOSING));
		}

	};

	private final Action refreshAction = new AbstractAction()
	{
		private static final long serialVersionUID = 1L;
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
				try
				{
					mainWindow.getObserver().refresh();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
		}

	};

	private final Action settingsAction = new AbstractAction()
	{
		
		private static final long serialVersionUID = 1L;
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			// TODO
		}

	};

	public void setActiveCodaStampaPanel(CodaStampaPanel activateCodaStampaPanel)
	{
		setActiveCodaStampaFile(activateCodaStampaPanel.getFile());
	}

	private void setActiveCodaStampaFile(File file)
	{
		boolean enableButton = (file != null);
		saveMenuItem.setEnabled(enableButton);
		saveButton.setEnabled(enableButton);

		refreshMenuItem.setEnabled(enableButton);
		refreshButton.setEnabled(enableButton);
		
		if (file != null)
			fileChooser.setCurrentDirectory(file.getParentFile());
	}

	private class MainMenuButton extends JButton
	{
		private static final long serialVersionUID = 1L;
		final private static int insetX = 7, insetY = 5;

		public MainMenuButton(String description, Icon icon, Action action)
		{
			super(action);

			setFocusPainted(false);
			//setRolloverEnabled(false);
			//setBorderPainted(false);
			setMargin(new Insets(insetY, insetX, insetY, insetX));

			setText(description);
			//setToolTipText(description);
			setIcon(icon);
		}

	}
	
	private class MainMenuItem extends JMenuItem
	{
		private static final long serialVersionUID = 1L;
		public MainMenuItem(String description, Icon icon, Action action)
		{
			super(action);

			setText(description);
			//setToolTipText(description);
			setIcon(icon);
		}

	}
}
