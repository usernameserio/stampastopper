package fpozzi.stopper.model;

public class PrezzoPromo extends Prezzo
{

	protected final int percentualeSconto;

	public PrezzoPromo(double value, int percentualeSconto)
	{
		super(value);
		this.percentualeSconto = percentualeSconto;
	}

	public PrezzoPromo(Prezzo prezzoNormale, double value)
	{
		this(value, (int) Math.round((1 - value / prezzoNormale.getValue()) * 100));
	}

	public PrezzoPromo(Prezzo prezzoNormale, int percentualeSconto)
	{
		this(prezzoNormale.getValue() * (100 - percentualeSconto) / 100.0, percentualeSconto);
	}

	public PrezzoPromo(double prezzo)
	{
		this(prezzo, 0);
	}

	public int getPercentualeSconto()
	{
		return percentualeSconto;
	}

	@Override
	public boolean equals(Object otherObject)
	{
		return (otherObject instanceof PrezzoPromo && value == ((PrezzoPromo) otherObject).value && percentualeSconto == ((PrezzoPromo) otherObject).percentualeSconto);
	}
}