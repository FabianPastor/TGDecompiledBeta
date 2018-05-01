package com.google.android.gms.internal.measurement;

import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.measurement.AppMeasurement;

public final class zzfg
  extends zzhk
{
  private long zzadp = -1L;
  private char zzaim = (char)0;
  private String zzain;
  private final zzfi zzaio = new zzfi(this, 6, false, false);
  private final zzfi zzaip = new zzfi(this, 6, true, false);
  private final zzfi zzaiq = new zzfi(this, 6, false, true);
  private final zzfi zzair = new zzfi(this, 5, false, false);
  private final zzfi zzais = new zzfi(this, 5, true, false);
  private final zzfi zzait = new zzfi(this, 5, false, true);
  private final zzfi zzaiu = new zzfi(this, 4, false, false);
  private final zzfi zzaiv = new zzfi(this, 3, false, false);
  private final zzfi zzaiw = new zzfi(this, 2, false, false);
  
  zzfg(zzgl paramzzgl)
  {
    super(paramzzgl);
  }
  
  private static String zza(boolean paramBoolean, Object paramObject)
  {
    if (paramObject == null)
    {
      paramObject = "";
      return (String)paramObject;
    }
    if ((paramObject instanceof Integer)) {
      paramObject = Long.valueOf(((Integer)paramObject).intValue());
    }
    for (;;)
    {
      Object localObject1;
      if ((paramObject instanceof Long))
      {
        if (!paramBoolean)
        {
          paramObject = String.valueOf(paramObject);
          break;
        }
        if (Math.abs(((Long)paramObject).longValue()) < 100L)
        {
          paramObject = String.valueOf(paramObject);
          break;
        }
        if (String.valueOf(paramObject).charAt(0) == '-') {}
        for (localObject1 = "-";; localObject1 = "")
        {
          paramObject = String.valueOf(Math.abs(((Long)paramObject).longValue()));
          long l1 = Math.round(Math.pow(10.0D, ((String)paramObject).length() - 1));
          long l2 = Math.round(Math.pow(10.0D, ((String)paramObject).length()) - 1.0D);
          paramObject = String.valueOf(localObject1).length() + 43 + String.valueOf(localObject1).length() + (String)localObject1 + l1 + "..." + (String)localObject1 + l2;
          break;
        }
      }
      if ((paramObject instanceof Boolean))
      {
        paramObject = String.valueOf(paramObject);
        break;
      }
      if ((paramObject instanceof Throwable))
      {
        Object localObject2 = (Throwable)paramObject;
        label238:
        String str1;
        int i;
        if (paramBoolean)
        {
          paramObject = localObject2.getClass().getName();
          localObject1 = new StringBuilder((String)paramObject);
          str1 = zzbi(AppMeasurement.class.getCanonicalName());
          paramObject = zzbi(zzgl.class.getCanonicalName());
          localObject2 = ((Throwable)localObject2).getStackTrace();
          i = localObject2.length;
        }
        for (int j = 0;; j++) {
          if (j < i)
          {
            Object localObject3 = localObject2[j];
            if (!((StackTraceElement)localObject3).isNativeMethod())
            {
              String str2 = ((StackTraceElement)localObject3).getClassName();
              if (str2 != null)
              {
                str2 = zzbi(str2);
                if ((str2.equals(str1)) || (str2.equals(paramObject)))
                {
                  ((StringBuilder)localObject1).append(": ");
                  ((StringBuilder)localObject1).append(localObject3);
                }
              }
            }
          }
          else
          {
            paramObject = ((StringBuilder)localObject1).toString();
            break;
            paramObject = ((Throwable)localObject2).toString();
            break label238;
          }
        }
      }
      if ((paramObject instanceof zzfj))
      {
        paramObject = zzfj.zza((zzfj)paramObject);
        break;
      }
      if (paramBoolean)
      {
        paramObject = "-";
        break;
      }
      paramObject = String.valueOf(paramObject);
      break;
    }
  }
  
  static String zza(boolean paramBoolean, String paramString, Object paramObject1, Object paramObject2, Object paramObject3)
  {
    String str1 = paramString;
    if (paramString == null) {
      str1 = "";
    }
    String str2 = zza(paramBoolean, paramObject1);
    String str3 = zza(paramBoolean, paramObject2);
    paramObject3 = zza(paramBoolean, paramObject3);
    paramObject2 = new StringBuilder();
    paramString = "";
    if (!TextUtils.isEmpty(str1))
    {
      ((StringBuilder)paramObject2).append(str1);
      paramString = ": ";
    }
    paramObject1 = paramString;
    if (!TextUtils.isEmpty(str2))
    {
      ((StringBuilder)paramObject2).append(paramString);
      ((StringBuilder)paramObject2).append(str2);
      paramObject1 = ", ";
    }
    paramString = (String)paramObject1;
    if (!TextUtils.isEmpty(str3))
    {
      ((StringBuilder)paramObject2).append((String)paramObject1);
      ((StringBuilder)paramObject2).append(str3);
      paramString = ", ";
    }
    if (!TextUtils.isEmpty((CharSequence)paramObject3))
    {
      ((StringBuilder)paramObject2).append(paramString);
      ((StringBuilder)paramObject2).append((String)paramObject3);
    }
    return ((StringBuilder)paramObject2).toString();
  }
  
  protected static Object zzbh(String paramString)
  {
    if (paramString == null) {}
    for (paramString = null;; paramString = new zzfj(paramString)) {
      return paramString;
    }
  }
  
  private static String zzbi(String paramString)
  {
    String str;
    if (TextUtils.isEmpty(paramString)) {
      str = "";
    }
    for (;;)
    {
      return str;
      int i = paramString.lastIndexOf('.');
      str = paramString;
      if (i != -1) {
        str = paramString.substring(0, i);
      }
    }
  }
  
  private final String zzis()
  {
    try
    {
      if (this.zzain == null) {
        this.zzain = ((String)zzew.zzagl.get());
      }
      String str = this.zzain;
      return str;
    }
    finally {}
  }
  
  protected final boolean isLoggable(int paramInt)
  {
    return Log.isLoggable(zzis(), paramInt);
  }
  
  protected final void zza(int paramInt, String paramString)
  {
    Log.println(paramInt, zzis(), paramString);
  }
  
  protected final void zza(int paramInt, boolean paramBoolean1, boolean paramBoolean2, String paramString, Object paramObject1, Object paramObject2, Object paramObject3)
  {
    int i = 0;
    if ((!paramBoolean1) && (isLoggable(paramInt))) {
      zza(paramInt, zza(false, paramString, paramObject1, paramObject2, paramObject3));
    }
    zzgg localzzgg;
    if ((!paramBoolean2) && (paramInt >= 5))
    {
      Preconditions.checkNotNull(paramString);
      localzzgg = this.zzacr.zzjn();
      if (localzzgg != null) {
        break label71;
      }
      zza(6, "Scheduler not set. Not logging error/warn");
    }
    for (;;)
    {
      return;
      label71:
      if (localzzgg.isInitialized()) {
        break;
      }
      zza(6, "Scheduler not initialized. Not logging error/warn");
    }
    if (paramInt < 0) {
      paramInt = i;
    }
    for (;;)
    {
      i = paramInt;
      if (paramInt >= 9) {
        i = 8;
      }
      localzzgg.zzc(new zzfh(this, i, paramString, paramObject1, paramObject2, paramObject3));
      break;
    }
  }
  
  protected final boolean zzhh()
  {
    return false;
  }
  
  public final zzfi zzil()
  {
    return this.zzaio;
  }
  
  public final zzfi zzim()
  {
    return this.zzaip;
  }
  
  public final zzfi zzin()
  {
    return this.zzair;
  }
  
  public final zzfi zzio()
  {
    return this.zzait;
  }
  
  public final zzfi zzip()
  {
    return this.zzaiu;
  }
  
  public final zzfi zziq()
  {
    return this.zzaiv;
  }
  
  public final zzfi zzir()
  {
    return this.zzaiw;
  }
  
  public final String zzit()
  {
    Object localObject = zzgh().zzajs.zzfh();
    if ((localObject == null) || (localObject == zzfr.zzajr)) {}
    for (String str = null;; str = String.valueOf(str).length() + 1 + String.valueOf(localObject).length() + str + ":" + (String)localObject)
    {
      return str;
      str = String.valueOf(((Pair)localObject).second);
      localObject = (String)((Pair)localObject).first;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzfg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */