package fpozzi.utils.logging;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import fpozzi.utils.date.DateUtils;



public class TextAreaLoggerHandler extends Handler
{

    private JTextArea textArea;
    private Calendar previousMessageCalendar = DateUtils.PAST;
    private static final DateFormat defaultTimeFormat = 
    		new SimpleDateFormat("HH:mm:ss");
    
    private static final DateFormat defaultDateTimeFormat = 
    		new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public TextAreaLoggerHandler(JTextArea textArea)
    {
	this.textArea = textArea;
	textArea.setEditable(false);
    }

    public void publish(final LogRecord record)
    {
	SwingUtilities.invokeLater(new Runnable()
	{
		Calendar messageCalendar;
		DateFormat dateFormat = defaultTimeFormat;
		
	    public void run()
	    {
	    	messageCalendar = GregorianCalendar.getInstance();
	    	messageCalendar.setTimeInMillis(record.getMillis());
	    	if (messageCalendar.get(Calendar.DAY_OF_YEAR)!=
	    			previousMessageCalendar.get(Calendar.DAY_OF_YEAR))
	    		dateFormat = defaultDateTimeFormat;
	    	previousMessageCalendar = messageCalendar;
			textArea.append("[" + dateFormat.format(messageCalendar.getTime()) + "] " 
			+ record.getMessage() + "\n");
			textArea.setCaretPosition(textArea.getDocument().getLength());
	    }
	});
    }

    public JTextArea getTextArea()
    {
	return this.textArea;
    }

    public void close() throws SecurityException {}

    public void flush() { }

}
