package fpozzi.utils;

public class Range
{

	private Number min, max;

	private boolean minInclusive, maxInclusive;

	public Range(Number minValue, boolean minInclusive, Number maxValue, boolean maxInclusive)
	{
		this.setMin(minValue, minInclusive);
		this.setMax(maxValue, maxInclusive);
	}

	public Number getMin()
	{
		return min;
	}

	public void setMin(Number min, boolean inclusive)
	{
		this.min = min;
		this.minInclusive = inclusive;
	}

	public Number getMax()
	{
		return max;
	}

	public void setMax(Number max, boolean inclusive)
	{
		this.max = max;
		this.maxInclusive = inclusive;
	}

	public boolean isMinInclusive()
	{
		return minInclusive;
	}

	public boolean isMaxInclusive()
	{
		return maxInclusive;
	}

	public boolean contains(Number n)
	{
		if (minInclusive && maxInclusive)
			return n.doubleValue() >= min.doubleValue() && n.doubleValue() <= max.doubleValue();

		if (minInclusive)
			return n.doubleValue() >= min.doubleValue() && n.doubleValue() < max.doubleValue();

		if (maxInclusive)
			return n.doubleValue() > min.doubleValue() && n.doubleValue() <= max.doubleValue();

		return n.doubleValue() > min.doubleValue() && n.doubleValue() < max.doubleValue();
	}

}
