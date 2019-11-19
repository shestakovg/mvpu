package core;

import android.database.Cursor;
import android.text.format.DateFormat;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by shestakov.g on 07.06.2015.
 */
public final class wputils {

    public static Calendar getCalendarFromString(String dateStr) throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        cal.setTime(sdf.parse(dateStr));
        return cal;
    }

    public static String getDateTime(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
      //  if (year>3900) year-=1900;
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        //Date date = new Date();
        return dateFormat.format(new Date(year - 1900,
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
    }

    public static String getDateStringFromLong(Long longDate)
    {
        Calendar cal = Calendar.getInstance();
        int offset = cal.getTimeZone().getOffset(cal.getTimeInMillis());
        Date da = new Date();
        da = new Date(longDate);//-(long)offset);
        cal.setTime(da);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        String time =format.format(cal.getTime());//.toLocaleString();
        return time;// cal.getTime().toLocaleString();// getDateTimeString2(cal);
    }


    public static String convertDate(String dateInMilliseconds,String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    public static String formatDate(Date date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd.MM.yyyy", Locale.getDefault());
        return dateFormat.format(date );
    }
    public static String getDateTimeString(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        //  if (year>3900) year-=1900;
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd.MM.yyyy", Locale.getDefault());
        //Date date = new Date();
        return dateFormat.format(new Date(year - 1900,
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
    }

    public static String getDateTimeString2(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        //  if (year>3900) year-=1900;
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd.MM.yyyy hh:mm:ss.SSS", Locale.getDefault());
        //Date date = new Date();
        return dateFormat.format(new Date(year - 1900,
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
    }
//    public static String getDateTime(Date date) {
//
//        SimpleDateFormat dateFormat = new SimpleDateFormat(
//                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//        //Date date = new Date();
//        return dateFormat.format(date);
//    }

    public static String decodeCyrilicString(String s)
    {
        if (s.length()==0) s="без комментариев";
        char[] ch_array = s.toCharArray();
        CharBuffer charbuf = CharBuffer.wrap(ch_array);
        ByteBuffer bytebuf = Charset.forName("Cp1251").encode(charbuf);
        byte[] byte_array = bytebuf.array();
        String res="";
        for (int j=0; j < byte_array.length; j++)
        {
            Formatter fmt = new Formatter();
            fmt.format("%X",byte_array[j]);
            res=res+fmt.toString()+" ";
            //System.out.print(fmt+" ");
        }
        return res;
    }

    public static Calendar getCalendarFromDate(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static  Calendar  getCurrentDate()
    {
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date());
        return currentDate;
    }

    public static Calendar getDateFromString(String dateInString) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        Date date = formatter.parse(dateInString);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return  cal;
    }

    public static int getTimeDifferenceInMonth(Calendar startCalendar, Calendar endCalendar) {
        return endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
    }

    public static Date LoadDate(Cursor cursor, int index) {
        if (cursor.isNull(index)) {
            return null;
        }
        return new Date(cursor.getLong(index));
    }

    public static String formatFloatWithSeparator(float value)
    {
//        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.GERMANY);
//        format.setCurrency(Currency.getInstance("uk_"));
//        return format.format(value);
        DecimalFormat decimalFormat = new DecimalFormat("#.");
        return decimalFormat.format(value);
    }

    public static String formatFloat(float value)
    {
//        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.GERMANY);
//        format.setCurrency(Currency.getInstance("uk_"));
//        return format.format(value);
       // NumberFormat formatter = new DecimalFormat("#0.00");
        //DecimalFormat decimalFormat = new DecimalFormat("##.##");
        //return formatter.format(value);
        return  String.format(Locale.CANADA, "%.2f", value);
    }
}
