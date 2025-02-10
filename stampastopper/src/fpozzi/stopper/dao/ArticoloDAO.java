package fpozzi.stopper.dao;

import fpozzi.gdoshop.model.articolo.Articolo;
import fpozzi.gdoshop.model.articolo.CodiceEan;
import fpozzi.gdoshop.model.articolo.CodiceInterno;

public interface ArticoloDAO
{
	public Articolo getArticoloByCodiceInterno(CodiceInterno codice) throws Exception;	
	public Articolo getArticoloByCodiceEan(CodiceEan ean) throws Exception;
	public CodiceInterno getCodiceInternoFromCodiceEAN(CodiceEan ean) throws Exception;
}
