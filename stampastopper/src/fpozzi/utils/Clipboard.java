package fpozzi.utils;

import java.util.LinkedList;
import java.util.List;

public class Clipboard<T>
{

	public List<ClipboardObserver<T>> observers;
	public T value;

	public Clipboard()
	{
		super();
		this.observers = new LinkedList<ClipboardObserver<T>>();
		this.value = null;
	}

	public void attachObserver(ClipboardObserver<T> observer)
	{
		observers.add(observer);
	}

	public void detachObserver(ClipboardObserver<T> observer)
	{
		observers.remove(observer);
	}

	public T getValue()
	{
		return value;
	}

	public void setValue(T value)
	{
		this.value = value;
		for (ClipboardObserver<T> observer : observers)
			observer.valueChanged(value);
	}

}
