package fpozzi.gdoshop.model.articolo;

import fpozzi.utils.Utils;

public class Quantita
{

	public static Double getPrezzoPerUM(Quantita qta, double prezzo)
	{
		if (qta.valore==null)
			return prezzo / qta.um.normalizzaValore(1);
		
		if (qta.valore == 0)
			return null;
		
		return prezzo / (qta.valoreNormalizzato * qta.moltiplicatore);
	}

	private final UnitaMisura um;
	private final Integer moltiplicatore;
	private final Double valore, valoreNormalizzato;
	
	public Quantita(UnitaMisura um, Double valore, Integer moltiplicatore)
	{
		this.um = um;
		this.valore = valore;
		this.moltiplicatore = moltiplicatore;
		this.valoreNormalizzato = valore==null ? null : um.normalizzaValore(valore);
	}
	
	public Quantita(UnitaMisura um, Double valore)
	{
		this(um, valore, 1);
	}

	public Quantita(UnitaMisura um)
	{
		this(um, null, null);
	}

	public UnitaMisura getUnitaMisura()
	{
		return um;
	}

	public Double getValore()
	{
		return valore;
	}

	public Double getValoreNormalizzato()
	{
		return valoreNormalizzato;
	}
	
	public Integer getMoltiplicatore()
	{
		return moltiplicatore;
	}

	@Override
	public boolean equals(Object otherObject)
	{
		if (otherObject instanceof Quantita)
		{
			Quantita otherGrammatura = (Quantita) otherObject;

			if (otherGrammatura.um != this.um)
				return false;
			
			if (!Utils.equalsWithNulls(otherGrammatura.valore, this.valore))
				return false;

			if (!Utils.equalsWithNulls(otherGrammatura.moltiplicatore, this.moltiplicatore))
				return false;

			return true;
		}
		return false;
	}
	
	
	public String toString()
	{
		return um + (valore==null ? "" : valore + (moltiplicatore==null ? "" : "x" + moltiplicatore));
	}
}
