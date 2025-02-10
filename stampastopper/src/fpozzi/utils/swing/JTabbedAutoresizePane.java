package fpozzi.utils.swing;

import java.awt.Component;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class JTabbedAutoresizePane extends JTabbedPane
{

	static private JPanel emptyPanel(){ return new JPanel();}
	
	private Vector<Component> tabBody;

	public JTabbedAutoresizePane()
	{
		this(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
	}

	public JTabbedAutoresizePane(int tabPlacement, int tabLayoutPolicy)
	{
		super(tabPlacement, tabLayoutPolicy);
		tabBody = new Vector<Component>();
		
		this.addChangeListener(new ChangeListener()
		{
			int previousIndex = 0;
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				int currentIndex = JTabbedAutoresizePane.this.getSelectedIndex();

					setComponentAt(previousIndex, emptyPanel());
					setComponentAt(currentIndex, tabBody.get(currentIndex));

				previousIndex = currentIndex;
			}
			
		});
		
	}

	public JTabbedAutoresizePane(int tabPlacement)
	{
		this(tabPlacement, JTabbedPane.SCROLL_TAB_LAYOUT);
	}

	@Override
	public void addTab(String title, Component component)
	{
		addTab(title, null, component, null);
	}

	@Override
	public void addTab(String title, Icon icon, Component component, String tip)
	{
		tabBody.add(component);
		super.addTab(title, icon, emptyPanel(), tip);
	}

	@Override
	public void addTab(String title, Icon icon, Component component)
	{
		addTab(title, icon, component, null);
	}
	
	

}
