package fpozzi.utils.misc;


public class ObservableProgress extends ObservableWrapper<Float>
{

	public ObservableProgress()
	{
		super(0f);
	}

	@Override
	public void setValue(Float value)
	{
		super.setValue(value);		
		this.notifyObservers();
	}
	
	public void incrementValue(Float increment)
	{
		setValue(getValue()+increment);
	}
	
}
