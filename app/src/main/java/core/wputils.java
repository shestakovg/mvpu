package core;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by shestakov.g on 07.06.2015.
 */
public final class wputils {
    public static String getDateTime(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
      //  if (year>3900) year-=1900;
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
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
}
