package core;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by shestakov.g on 07.06.2015.
 */
public class wputils {
    public static String getDateTime(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
      //  if (year>3900) year-=1900;
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        //Date date = new Date();
        return dateFormat.format(new Date(year - 1900,
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
    }
}
