package fpozzi.utils.misc;

import java.util.Observable;

public class ObservableWrapper<T> extends Observable
{
	private T value;
	
	public ObservableWrapper(T initialValue)
	{
		value = initialValue;
	}
	
	public ObservableWrapper()
	{
		this(null);
	}
	
	public T getValue()
	{
		return value;
	}

	public void setValue(T value) 
	{
		this.value = value;
		this.setChanged();
	}
	
	public boolean hasChanged() 
	{
		boolean hasChanged = super.hasChanged();
		this.clearChanged();
		return hasChanged;
	}
	
}