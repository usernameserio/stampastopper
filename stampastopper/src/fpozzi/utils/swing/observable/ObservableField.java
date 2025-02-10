package fpozzi.utils.swing.observable;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Observable;

import fpozzi.utils.Utils;

public abstract class ObservableField<FIELD, VALUE> extends Observable
{

	private VALUE initialValue;
	protected final FIELD field;
	
	private boolean revertOnInvalidValue = false;
	
	public ObservableField(FIELD field)
	{
		this.field = field;
	}
	
	public boolean getRevertOnInvalidValue()
	{
		return revertOnInvalidValue;
	}

	public void setRevertOnInvalidValue(boolean revertOnInvalidValue)
	{
		this.revertOnInvalidValue = revertOnInvalidValue;
	}

	protected VALUE getInitialValue()
	{
		return initialValue;
	}
	
	public abstract VALUE getCurrentValue() throws InvalidValueException;
	
	public abstract void revert();
	
	public void beforeEdit()
	{
		try
		{
			initialValue = getCurrentValue();
		}
		catch (InvalidValueException e)
		{
			initialValue = null;
		}
	}
	
	public abstract void whileEdit();
	
	public void afterEdit()
	{
		VALUE currentValue;
		try
		{
			currentValue = getCurrentValue();
			if (!Utils.equalsWithNulls(initialValue, currentValue))
			{
				initialValue = currentValue;
				this.setChanged();
				this.notifyObservers();
			}
		}
		catch (InvalidValueException e)
		{
			if (revertOnInvalidValue)
				revert();
		}

	}

	protected class FieldFocusListener implements FocusListener
	{

		@Override
		public void focusGained(FocusEvent arg0)
		{
			beforeEdit();
		}

		@Override
		public void focusLost(FocusEvent arg0)
		{
			afterEdit();		
		}
		
	}
}
