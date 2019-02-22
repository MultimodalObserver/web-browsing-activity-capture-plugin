package mo.capture.webactivity.utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {

    private static final String MO_FORMAT = "yyyy-MM-dd_HH.mm.ss.SSS";

    private static String nowToFormat(String format){
        Calendar calendar = Calendar.getInstance();
        Date date=calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static String now(){
        return nowToFormat(MO_FORMAT);
    }

    public static Date nowDate(){
        Calendar calendar = Calendar.getInstance();
        Date date=calendar.getTime();
        return date;
    }

    public static  long nowMilliseconds(){
        return nowDate().getTime();
    }
}
