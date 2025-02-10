package fpozzi.utils.logging;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import fpozzi.utils.date.DateUtils;



public class JPanelLoggerHandler extends Handler
{

    private JPanel panel;
    private Calendar previousMessageCalendar = DateUtils.PAST;
    private static final DateFormat defaultTimeFormat = 
    		new SimpleDateFormat("HH:mm:ss");
    
    @SuppressWarnings("serial")
	private static final DateFormat defaultDateTimeFormat = 
    		new DateFormat(){

				@Override
				public StringBuffer format(Date date, StringBuffer arg1,
						FieldPosition arg2)
				{
					return new StringBuffer("<html>" +
							DateUtils.italianDateFormat.format(date) + 
							"<br/>" + defaultTimeFormat.format(date)
							+"</html>");
				}

				@Override
				public Date parse(String arg0, ParsePosition arg1)
				{
					return null;
				}
    	
    		};


    public JPanelLoggerHandler(JPanel panel)
    {
    	this.panel = panel;
    	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    	panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    	panel.setBackground(Color.WHITE);
    	panel.setAlignmentY(Component.TOP_ALIGNMENT );
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
		    	JPanel messagePanel = new JPanel(new BorderLayout(5, 5));
		    	messagePanel.setBackground(Color.WHITE);
		    	JLabel label = new JLabel(dateFormat.format(messageCalendar.getTime()));
		    	label.setAlignmentY(Component.TOP_ALIGNMENT);
		    	messagePanel.add(label, BorderLayout.WEST); 
		    	label = new JLabel(record.getMessage());
		    	label.setAlignmentY(Component.TOP_ALIGNMENT);
		    	messagePanel.add(label, BorderLayout.CENTER);
		    	panel.add(messagePanel);
		    }
		});
    }

    public void close() throws SecurityException {}

    public void flush() { }

}
