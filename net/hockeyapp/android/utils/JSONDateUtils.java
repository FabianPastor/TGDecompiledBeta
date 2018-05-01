package net.hockeyapp.android.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.json.JSONException;

public final class JSONDateUtils
{
  private static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal()
  {
    protected DateFormat initialValue()
    {
      SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
      localSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
      return localSimpleDateFormat;
    }
  };
  
  private static void checkNull(Object paramObject)
    throws JSONException
  {
    if (paramObject == null) {
      throw new JSONException("date cannot be null");
    }
  }
  
  public static String toString(Date paramDate)
    throws JSONException
  {
    checkNull(paramDate);
    return ((DateFormat)DATE_FORMAT.get()).format(paramDate);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/utils/JSONDateUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */