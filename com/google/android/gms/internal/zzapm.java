package com.google.android.gms.internal;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class zzapm
  extends zzaot<Date>
{
  public static final zzaou bmp = new zzaou()
  {
    public <T> zzaot<T> zza(zzaob paramAnonymouszzaob, zzapx<T> paramAnonymouszzapx)
    {
      if (paramAnonymouszzapx.by() == Date.class) {
        return new zzapm();
      }
      return null;
    }
  };
  private final DateFormat bkA = DateFormat.getDateTimeInstance(2, 2);
  private final DateFormat bkB = bm();
  private final DateFormat bkz = DateFormat.getDateTimeInstance(2, 2, Locale.US);
  
  private static DateFormat bm()
  {
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
    localSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    return localSimpleDateFormat;
  }
  
  private Date zzur(String paramString)
  {
    try
    {
      Date localDate1 = this.bkA.parse(paramString);
      paramString = localDate1;
    }
    catch (ParseException localParseException1)
    {
      try
      {
        Date localDate2 = this.bkz.parse(paramString);
        paramString = localDate2;
      }
      catch (ParseException localParseException2)
      {
        try
        {
          Date localDate3 = this.bkB.parse(paramString);
          paramString = localDate3;
        }
        catch (ParseException localParseException3)
        {
          throw new zzaoq(paramString, localParseException3);
        }
      }
    }
    finally {}
    return paramString;
  }
  
  public void zza(zzaqa paramzzaqa, Date paramDate)
    throws IOException
  {
    if (paramDate == null) {}
    for (;;)
    {
      try
      {
        paramzzaqa.bx();
        return;
      }
      finally {}
      paramzzaqa.zzut(this.bkz.format(paramDate));
    }
  }
  
  public Date zzk(zzapy paramzzapy)
    throws IOException
  {
    if (paramzzapy.bn() == zzapz.bos)
    {
      paramzzapy.nextNull();
      return null;
    }
    return zzur(paramzzapy.nextString());
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzapm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */