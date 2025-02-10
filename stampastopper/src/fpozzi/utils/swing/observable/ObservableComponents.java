package fpozzi.utils.swing.observable;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;

import javax.swing.JComponent;

public abstract class ObservableComponents<VALUE> extends ObservableField<List<JComponent>, VALUE>
{
	private final FocusListener focusListener = new FocusListener()
	{

		@Override
		public void focusGained(FocusEvent e)
		{
			focusChanged(e.getOppositeComponent(), e.getComponent());
		}

		@Override
		public void focusLost(FocusEvent e)
		{
			focusChanged(e.getComponent(), e.getOppositeComponent());
		}

	};

	protected ObservableComponents(List<JComponent> fields)
	{
		super(fields);
		for (JComponent c : fields)
			c.addFocusListener(focusListener);
	}

	public void focusChanged(Object fromComponent, Object toComponent)
	{
		if (!field.contains(fromComponent))
			beforeEdit();
		else if (!field.contains(toComponent))
			afterEdit();
		else
			whileEdit();
	}

}
