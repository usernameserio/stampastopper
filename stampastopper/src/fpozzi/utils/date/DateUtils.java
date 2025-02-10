package fpozzi.utils.date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils
{

	static final public Calendar PAST;
	static final public Calendar FUTURE;

	static final public Calendar TODAY;
	
	static {
		TODAY = GregorianCalendar.getInstance();
		TODAY.set(Calendar.HOUR_OF_DAY, 0);
		
		PAST = GregorianCalendar.getInstance();
		PAST.setTimeInMillis(0);

		FUTURE = GregorianCalendar.getInstance();
		FUTURE.setTimeInMillis(Long.MAX_VALUE);
	}
	
	static final public DateFormat verboseMonthFormat = 
			new SimpleDateFormat("MMMM yyyy");
	
	static final public DateFormat italianDateFormat = 
			new SimpleDateFormat("dd/MM/yy");
	
	static final public DateFormat italianDateFormatNoYear = 
			new SimpleDateFormat("dd/MM");
	
	static final public DateFormat italianDateTimeFormat = 
			new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	static final public DateFormat americanDateFormat = 
			new SimpleDateFormat("yyyy-MM-dd");
	
	static final public long millisInADay = 1000 * 60 * 60 * 24;
	static final public long millisInAMonth = millisInADay*30;
	static final public long millisInAYear = millisInADay*365;
	
	static public Calendar now()
	{
		return GregorianCalendar.getInstance();
	}
	
	static public long daysBetween(Date date, Date otherDate)
	{
		return Math.abs((date.getTime() - otherDate.getTime())/millisInADay); 
	}
	
	static public long daysFrom(Date olderDate)
	{
		return daysBetween(now().getTime(), olderDate);
	}
	
	public static void main(String[] args)
	{
		
		Calendar cMonthsAgo = DateUtils.now();
		cMonthsAgo.add(Calendar.MONTH, -3);
		cMonthsAgo.set(Calendar.DAY_OF_MONTH, 1);

		Date d1 = cMonthsAgo.getTime();
		
		cMonthsAgo.add(Calendar.MONTH, -3);
		
		Date d2 = cMonthsAgo.getTime();
		
		System.out.println(DateUtils.italianDateFormat.format(d1));
		
		System.out.println(DateUtils.italianDateFormat.format(d2));
	}
	
}
