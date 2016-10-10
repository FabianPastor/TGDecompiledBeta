package com.google.android.gms.internal;

import android.os.Build.VERSION;
import android.util.Log;
import com.google.android.gms.common.internal.zzq;

public class zzse
{
  private final String CR;
  private final zzq Dk;
  private final int bX;
  private final String mTag;
  
  private zzse(String paramString1, String paramString2)
  {
    this.CR = paramString2;
    this.mTag = paramString1;
    this.Dk = new zzq(paramString1);
    this.bX = getLogLevel();
  }
  
  public zzse(String paramString, String... paramVarArgs)
  {
    this(paramString, zzd(paramVarArgs));
  }
  
  private int getLogLevel()
  {
    label30:
    int j;
    if (Build.VERSION.SDK_INT == 23)
    {
      String str = String.valueOf(this.mTag);
      if (str.length() != 0)
      {
        str = "log.tag.".concat(str);
        str = System.getProperty(str);
        if (str == null) {
          break label289;
        }
        i = -1;
        switch (str.hashCode())
        {
        }
      }
      for (;;)
      {
        switch (i)
        {
        default: 
          j = 4;
          return j;
          str = new String("log.tag.");
          break label30;
          if (str.equals("VERBOSE"))
          {
            i = 0;
            continue;
            if (str.equals("DEBUG"))
            {
              i = 1;
              continue;
              if (str.equals("INFO"))
              {
                i = 2;
                continue;
                if (str.equals("WARN"))
                {
                  i = 3;
                  continue;
                  if (str.equals("ERROR"))
                  {
                    i = 4;
                    continue;
                    if (str.equals("ASSERT"))
                    {
                      i = 5;
                      continue;
                      if (str.equals("SUPPRESS")) {
                        i = 6;
                      }
                    }
                  }
                }
              }
            }
          }
          break;
        }
      }
      return 2;
      return 3;
      return 4;
      return 5;
      return 6;
      return 7;
      return Integer.MAX_VALUE;
    }
    label289:
    int i = 2;
    for (;;)
    {
      j = i;
      if (7 < i) {
        break;
      }
      j = i;
      if (Log.isLoggable(this.mTag, i)) {
        break;
      }
      i += 1;
    }
  }
  
  private static String zzd(String... paramVarArgs)
  {
    if (paramVarArgs.length == 0) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append('[');
    int j = paramVarArgs.length;
    int i = 0;
    while (i < j)
    {
      String str = paramVarArgs[i];
      if (localStringBuilder.length() > 1) {
        localStringBuilder.append(",");
      }
      localStringBuilder.append(str);
      i += 1;
    }
    localStringBuilder.append(']').append(' ');
    return localStringBuilder.toString();
  }
  
  protected String format(String paramString, Object... paramVarArgs)
  {
    String str = paramString;
    if (paramVarArgs != null)
    {
      str = paramString;
      if (paramVarArgs.length > 0) {
        str = String.format(paramString, paramVarArgs);
      }
    }
    return this.CR.concat(str);
  }
  
  public void zza(String paramString, Object... paramVarArgs)
  {
    if (zzbf(2)) {
      Log.v(this.mTag, format(paramString, paramVarArgs));
    }
  }
  
  public boolean zzbf(int paramInt)
  {
    return this.bX <= paramInt;
  }
  
  public void zze(String paramString, Object... paramVarArgs)
  {
    Log.i(this.mTag, format(paramString, paramVarArgs));
  }
  
  public void zzf(String paramString, Object... paramVarArgs)
  {
    Log.w(this.mTag, format(paramString, paramVarArgs));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */