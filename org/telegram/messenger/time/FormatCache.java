package org.telegram.messenger.time;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

abstract class FormatCache<F extends Format>
{
  static final int NONE = -1;
  private static final ConcurrentMap<MultipartKey, String> cDateTimeInstanceCache = new ConcurrentHashMap(7);
  private final ConcurrentMap<MultipartKey, F> cInstanceCache = new ConcurrentHashMap(7);
  
  private F getDateTimeInstance(Integer paramInteger1, Integer paramInteger2, TimeZone paramTimeZone, Locale paramLocale)
  {
    Locale localLocale = paramLocale;
    if (paramLocale == null) {
      localLocale = Locale.getDefault();
    }
    return getInstance(getPatternForStyle(paramInteger1, paramInteger2, localLocale), paramTimeZone, localLocale);
  }
  
  static String getPatternForStyle(Integer paramInteger1, Integer paramInteger2, Locale paramLocale)
  {
    MultipartKey localMultipartKey = new MultipartKey(new Object[] { paramInteger1, paramInteger2, paramLocale });
    String str = (String)cDateTimeInstanceCache.get(localMultipartKey);
    Object localObject = str;
    if ((str != null) || (paramInteger1 == null)) {}
    for (;;)
    {
      try
      {
        paramInteger1 = DateFormat.getTimeInstance(paramInteger2.intValue(), paramLocale);
        localObject = ((SimpleDateFormat)paramInteger1).toPattern();
        paramInteger1 = (String)cDateTimeInstanceCache.putIfAbsent(localMultipartKey, localObject);
        if (paramInteger1 != null) {
          localObject = paramInteger1;
        }
        return (String)localObject;
      }
      catch (ClassCastException paramInteger1)
      {
        throw new IllegalArgumentException("No date time pattern for locale: " + paramLocale);
      }
      if (paramInteger2 == null) {
        paramInteger1 = DateFormat.getDateInstance(paramInteger1.intValue(), paramLocale);
      } else {
        paramInteger1 = DateFormat.getDateTimeInstance(paramInteger1.intValue(), paramInteger2.intValue(), paramLocale);
      }
    }
  }
  
  protected abstract F createInstance(String paramString, TimeZone paramTimeZone, Locale paramLocale);
  
  F getDateInstance(int paramInt, TimeZone paramTimeZone, Locale paramLocale)
  {
    return getDateTimeInstance(Integer.valueOf(paramInt), null, paramTimeZone, paramLocale);
  }
  
  F getDateTimeInstance(int paramInt1, int paramInt2, TimeZone paramTimeZone, Locale paramLocale)
  {
    return getDateTimeInstance(Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramTimeZone, paramLocale);
  }
  
  public F getInstance()
  {
    return getDateTimeInstance(3, 3, TimeZone.getDefault(), Locale.getDefault());
  }
  
  public F getInstance(String paramString, TimeZone paramTimeZone, Locale paramLocale)
  {
    if (paramString == null) {
      throw new NullPointerException("pattern must not be null");
    }
    TimeZone localTimeZone = paramTimeZone;
    if (paramTimeZone == null) {
      localTimeZone = TimeZone.getDefault();
    }
    Locale localLocale = paramLocale;
    if (paramLocale == null) {
      localLocale = Locale.getDefault();
    }
    MultipartKey localMultipartKey = new MultipartKey(new Object[] { paramString, localTimeZone, localLocale });
    paramLocale = (Format)this.cInstanceCache.get(localMultipartKey);
    paramTimeZone = paramLocale;
    if (paramLocale == null)
    {
      paramTimeZone = createInstance(paramString, localTimeZone, localLocale);
      paramString = (Format)this.cInstanceCache.putIfAbsent(localMultipartKey, paramTimeZone);
      if (paramString != null) {
        paramTimeZone = paramString;
      }
    }
    return paramTimeZone;
  }
  
  F getTimeInstance(int paramInt, TimeZone paramTimeZone, Locale paramLocale)
  {
    return getDateTimeInstance(null, Integer.valueOf(paramInt), paramTimeZone, paramLocale);
  }
  
  private static class MultipartKey
  {
    private int hashCode;
    private final Object[] keys;
    
    public MultipartKey(Object... paramVarArgs)
    {
      this.keys = paramVarArgs;
    }
    
    public boolean equals(Object paramObject)
    {
      return Arrays.equals(this.keys, ((MultipartKey)paramObject).keys);
    }
    
    public int hashCode()
    {
      if (this.hashCode == 0)
      {
        int i = 0;
        Object[] arrayOfObject = this.keys;
        int j = arrayOfObject.length;
        int k = 0;
        while (k < j)
        {
          Object localObject = arrayOfObject[k];
          int m = i;
          if (localObject != null) {
            m = i * 7 + localObject.hashCode();
          }
          k++;
          i = m;
        }
        this.hashCode = i;
      }
      return this.hashCode;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/time/FormatCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */