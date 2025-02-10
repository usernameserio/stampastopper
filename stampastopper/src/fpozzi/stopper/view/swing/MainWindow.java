package fpozzi.stopper.view.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fpozzi.stopper.model.pdf.CodaStampa;
import fpozzi.stopper.view.CodaStampaView;
import fpozzi.stopper.view.EditorView;
import fpozzi.stopper.view.MainView;
import fpozzi.stopper.view.swing.codastampa.CodaStampaPanel;

public class MainWindow extends JFrame implements MainView
{

	private static final long serialVersionUID = 1L;

	private MainViewObserver observer;

	private final MainMenu mainMenu;

	private final EditorPanel editorPanel;

	private final JTextArea logTextArea;

	private final JTabbedPane codaStampaTabbedPane;

	private CodaStampaPanel activeCodaStampaPanel;

	public MainWindow(String title, int x, int y, int w, int h) throws Exception
	{
		observer = null;

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle(title);

		setPreferredSize(new Dimension(w, h));
		setMinimumSize(new Dimension(720, 640));

		mainMenu = new MainMenu(this);

		setJMenuBar(mainMenu.getMenuBar());

		JPanel mainPanel = new JPanel(new BorderLayout());

		mainPanel.add(mainMenu.getMenuPanel(), BorderLayout.NORTH);

		JPanel operationPanel = new JPanel(new BorderLayout());

		codaStampaTabbedPane = new JTabbedPane();
		codaStampaTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		codaStampaTabbedPane.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent evt)
			{
				CodaStampa codaStampa = null;
				if (codaStampaTabbedPane.getSelectedIndex() == codaStampaTabbedPane.getTabCount() - 1)
				{

				}
				else if (codaStampaTabbedPane.getSelectedIndex() >= 0)
				{
					codaStampa = ((CodaStampaPanel) codaStampaTabbedPane.getComponentAt(codaStampaTabbedPane.getSelectedIndex())).getCodaStampa();

					observer.codaStampaSelected(codaStampa);

				}
			}

		});

		codaStampaTabbedPane.addTab("Nuovo", new JPanel());
		codaStampaTabbedPane.setTabComponentAt(0, new JLabel(Icons.TAB_ADD.image));
		
		final MouseListener ml = codaStampaTabbedPane.getMouseListeners()[0];
		codaStampaTabbedPane.removeMouseListener(ml);
		codaStampaTabbedPane.addMouseListener(new MouseListener()
		{

			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (codaStampaTabbedPane.indexAtLocation(e.getX(), e.getY()) == codaStampaTabbedPane.getTabCount() - 1)
					observer.newCodaStampa();
				ml.mouseReleased(e);
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (codaStampaTabbedPane.indexAtLocation(e.getX(), e.getY()) != codaStampaTabbedPane.getTabCount() - 1)
					ml.mouseClicked(e);

			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				ml.mouseEntered(e);

			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				ml.mouseExited(e);

			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				if (codaStampaTabbedPane.indexAtLocation(e.getX(), e.getY()) != codaStampaTabbedPane.getTabCount() - 1)
					ml.mousePressed(e);

			}

		});
		operationPanel.add(codaStampaTabbedPane, BorderLayout.CENTER);

		editorPanel = new EditorPanel();
		operationPanel.add(editorPanel, BorderLayout.EAST);

		mainPanel.add(operationPanel, BorderLayout.CENTER);

		logTextArea = new JTextArea(5, 30);
		logTextArea.setMargin(new Insets(5, 5, 5, 5));
		logTextArea.setLineWrap(true);
		logTextArea.setWrapStyleWord(true);
		logTextArea.setFont(logTextArea.getFont().deriveFont(11.0F));

		JScrollPane logScrollPane = new JScrollPane(logTextArea);
		logScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		mainPanel.add(logScrollPane, BorderLayout.SOUTH);

		getContentPane().add(mainPanel);

		setLocation(x, y);

	}

	@Override
	public void setObserver(MainViewObserver observer)
	{
		this.observer = observer;
	}

	public MainViewObserver getObserver()
	{
		return observer;
	}

	public JTextArea getLogTextArea()
	{
		return logTextArea;
	}

	@Override
	public EditorView getEditorView()
	{
		return editorPanel;
	}

	public CodaStampaPanel getActiveCodaStampaPanel()
	{
		return activeCodaStampaPanel;
	}

	@Override
	public CodaStampaView makeCodaStampaView()
	{
		return new CodaStampaPanel();
	}

	@Override
	public void addCodaStampaView(CodaStampaView view)
	{
		final CodaStampaPanel codaStampaPanel = (CodaStampaPanel) view;
		codaStampaTabbedPane.insertTab("", null, codaStampaPanel, null, codaStampaTabbedPane.getTabCount() - 1);
		codaStampaTabbedPane.setTabComponentAt(codaStampaTabbedPane.getTabCount() - 2, codaStampaPanel.getTab());
		codaStampaPanel.getTab().getCloseButton().addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					observer.close(codaStampaPanel.getCodaStampa());
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
			}

		});
	}

	@Override
	public void removeCodaStampaView(CodaStampaView view)
	{
		boolean found;
		int i;
		for (found = false, i = 0; !found && i < codaStampaTabbedPane.getTabCount(); i++)
			found = (codaStampaTabbedPane.getComponentAt(i) == view);
		if (found)
			codaStampaTabbedPane.remove(i - 1);
	}

	@Override
	public void setActiveCodaStampaView(CodaStampaView view)
	{
		CodaStampaPanel activatedCodaStampaPanel = (CodaStampaPanel) view;
		for (Component csPanel : codaStampaTabbedPane.getComponents())
			if (csPanel == view)
			{
				this.activeCodaStampaPanel = activatedCodaStampaPanel;
				codaStampaTabbedPane.setSelectedComponent(activatedCodaStampaPanel);
				mainMenu.setActiveCodaStampaPanel(activatedCodaStampaPanel);
				return;
			}

	}

}
