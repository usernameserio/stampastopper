package fpozzi.stopper.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class Stopper implements Cloneable
{
	
	public abstract Element makeXMLElement(Document doc);

	public abstract Object clone();
}
