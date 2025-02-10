package fpozzi.utils.xml;

import org.w3c.dom.Node;

public class XMLException extends Exception
{

	private final Node node;
	
	public XMLException(Node node, String msg, Throwable cause)
	{
		super("Problema alla linea " + node.getUserData("lineNumber") + ": " + msg, cause);
		this.node = node;
	}

	public XMLException(Node node, String msg)
	{
		this(node, msg, null);
	}
	
	

}
