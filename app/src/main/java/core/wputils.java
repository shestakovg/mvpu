package core;

import android.database.Cursor;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public static Date LoadDate(Cursor cursor, int index) {
        if (cursor.isNull(index)) {
            return null;
        }
        return new Date(cursor.getLong(index));
    }
}
