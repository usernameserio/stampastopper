package fpozzi.stopper.model;

import java.util.Comparator;

import fpozzi.utils.format.FormatUtils;

public class Prezzo
{
	
	protected final double value;

	public Prezzo(Double value)
	{
		super();
		this.value = Math.round(value*100) / 100.0;
	}

	public double getValue()
	{
		return value;
	}

	@Override
	public boolean equals(Object otherObject)
	{
		return (otherObject instanceof Prezzo && value == ((Prezzo)otherObject).value);
	}

	@Override
	public String toString()
	{
		return FormatUtils.twoDecimalDigitsFormat.format(value);
	}
	
	public static final Comparator<Prezzo> defaultComparator = new Comparator<Prezzo>(){

		@Override
		public int compare(Prezzo p1, Prezzo p2)
		{
			if (p1==null)
				return -1;
			if (p2==null) 
				return 1;
			return Double.compare(p1.value, p2.value);
		}
		
	};

}
