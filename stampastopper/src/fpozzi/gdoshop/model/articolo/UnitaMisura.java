package fpozzi.gdoshop.model.articolo;

public enum UnitaMisura {

	KG("kg", "kilogrammi", "kg", "kg"),
	GR("grammo", "grammi", "g", "g", UnitaMisura.KG, 1.0/1000),
	HG("etto", "etti", "hg", "hg", UnitaMisura.KG, 1.0/10),
	MG("milligrammo", "milligrammi", "mg", "mg",  UnitaMisura.KG, 1.0/1000000),
	LT("litro", "litri", "litro", "litri"),
	ML("millilitro", "millilitri", "ml", "ml", UnitaMisura.LT, 1.0/1000),
	CL("centrilitro", "centilitri", "cl", "cl", UnitaMisura.LT, 1.0/100),
	PZ("pezzo", "pezzi", "pz", "pz");
	
	private final String sing, plur, abbrSing, abbrPlur;
	private final UnitaMisura normalizzata;
	private final double fattoreNormalizzazione;
	
	private UnitaMisura(String sing, String plur, String abbrSing, String abbrPlur, UnitaMisura normalizzata, double fattoreNormalizzazione)
	{
		this.sing = sing;
		this.plur = plur;
		this.abbrSing = abbrSing;
		this.abbrPlur = abbrPlur;
		this.normalizzata = normalizzata;
		this.fattoreNormalizzazione = fattoreNormalizzazione;
	}
	
	private UnitaMisura(String sing, String plur, String abbrSing, String abbrPlur)
	{
		this.sing = sing;
		this.plur = plur;
		this.abbrSing = abbrSing;
		this.abbrPlur = abbrPlur;
		this.normalizzata = this;
		this.fattoreNormalizzazione = 1;
	}
	
	public String getAbbrSing()
	{
		return abbrSing;
	}
	
	public String getAbbrPlur()
	{
		return abbrPlur;
	}
	
	public String getSing()
	{
		return sing;
	}

	public String getPlur()
	{
		return plur;
	}
	
	public UnitaMisura getNormalizzata()
	{
		return normalizzata;
	}
	
	public double normalizzaValore(double qta)
	{
		return qta * fattoreNormalizzazione;
	}

	public static UnitaMisura fromString(String um)
	{
		if (um.equalsIgnoreCase("GR")) return GR;
		if (um.equalsIgnoreCase("KG")) return KG;
		if (um.equalsIgnoreCase("MG")) return MG;
		if (um.equalsIgnoreCase("LT")) return LT;
		if (um.equalsIgnoreCase("ML")) return ML;
		if (um.equalsIgnoreCase("CL")) return CL;
		return PZ;
	}
	
	
}
