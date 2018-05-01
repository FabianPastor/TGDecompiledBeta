package com.google.android.gms.internal;

import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.measurement.AppMeasurement;

public final class zzcfl
  extends zzchj
{
  private final String zzaIb = zzcem.zzxf();
  private final long zzboC = zzcem.zzwP();
  private final char zzbqL;
  private final zzcfn zzbqM;
  private final zzcfn zzbqN;
  private final zzcfn zzbqO;
  private final zzcfn zzbqP;
  private final zzcfn zzbqQ;
  private final zzcfn zzbqR;
  private final zzcfn zzbqS;
  private final zzcfn zzbqT;
  private final zzcfn zzbqU;
  
  zzcfl(zzcgl paramzzcgl)
  {
    super(paramzzcgl);
    if (super.zzwH().zzln()) {
      zzcem.zzxE();
    }
    for (this.zzbqL = 'C';; this.zzbqL = 'c')
    {
      this.zzbqM = new zzcfn(this, 6, false, false);
      this.zzbqN = new zzcfn(this, 6, true, false);
      this.zzbqO = new zzcfn(this, 6, false, true);
      this.zzbqP = new zzcfn(this, 5, false, false);
      this.zzbqQ = new zzcfn(this, 5, true, false);
      this.zzbqR = new zzcfn(this, 5, false, true);
      this.zzbqS = new zzcfn(this, 4, false, false);
      this.zzbqT = new zzcfn(this, 3, false, false);
      this.zzbqU = new zzcfn(this, 2, false, false);
      return;
      zzcem.zzxE();
    }
  }
  
  private static String zza(boolean paramBoolean, String paramString, Object paramObject1, Object paramObject2, Object paramObject3)
  {
    String str1 = paramString;
    if (paramString == null) {
      str1 = "";
    }
    String str2 = zzc(paramBoolean, paramObject1);
    paramObject2 = zzc(paramBoolean, paramObject2);
    paramObject3 = zzc(paramBoolean, paramObject3);
    StringBuilder localStringBuilder = new StringBuilder();
    paramString = "";
    if (!TextUtils.isEmpty(str1))
    {
      localStringBuilder.append(str1);
      paramString = ": ";
    }
    paramObject1 = paramString;
    if (!TextUtils.isEmpty(str2))
    {
      localStringBuilder.append(paramString);
      localStringBuilder.append(str2);
      paramObject1 = ", ";
    }
    paramString = (String)paramObject1;
    if (!TextUtils.isEmpty((CharSequence)paramObject2))
    {
      localStringBuilder.append((String)paramObject1);
      localStringBuilder.append((String)paramObject2);
      paramString = ", ";
    }
    if (!TextUtils.isEmpty((CharSequence)paramObject3))
    {
      localStringBuilder.append(paramString);
      localStringBuilder.append((String)paramObject3);
    }
    return localStringBuilder.toString();
  }
  
  private static String zzc(boolean paramBoolean, Object paramObject)
  {
    if (paramObject == null) {
      return "";
    }
    if ((paramObject instanceof Integer)) {
      paramObject = Long.valueOf(((Integer)paramObject).intValue());
    }
    for (;;)
    {
      String str1;
      if ((paramObject instanceof Long))
      {
        if (!paramBoolean) {
          return String.valueOf(paramObject);
        }
        if (Math.abs(((Long)paramObject).longValue()) < 100L) {
          return String.valueOf(paramObject);
        }
        if (String.valueOf(paramObject).charAt(0) == '-') {}
        for (str1 = "-";; str1 = "")
        {
          paramObject = String.valueOf(Math.abs(((Long)paramObject).longValue()));
          long l1 = Math.round(Math.pow(10.0D, ((String)paramObject).length() - 1));
          long l2 = Math.round(Math.pow(10.0D, ((String)paramObject).length()) - 1.0D);
          return String.valueOf(str1).length() + 43 + String.valueOf(str1).length() + str1 + l1 + "..." + str1 + l2;
        }
      }
      if ((paramObject instanceof Boolean)) {
        return String.valueOf(paramObject);
      }
      if ((paramObject instanceof Throwable))
      {
        Object localObject1 = (Throwable)paramObject;
        String str2;
        int j;
        int i;
        if (paramBoolean)
        {
          paramObject = localObject1.getClass().getName();
          paramObject = new StringBuilder((String)paramObject);
          str1 = zzea(AppMeasurement.class.getCanonicalName());
          str2 = zzea(zzcgl.class.getCanonicalName());
          localObject1 = ((Throwable)localObject1).getStackTrace();
          j = localObject1.length;
          i = 0;
        }
        for (;;)
        {
          if (i < j)
          {
            Object localObject2 = localObject1[i];
            if (!((StackTraceElement)localObject2).isNativeMethod())
            {
              String str3 = ((StackTraceElement)localObject2).getClassName();
              if (str3 != null)
              {
                str3 = zzea(str3);
                if ((str3.equals(str1)) || (str3.equals(str2)))
                {
                  ((StringBuilder)paramObject).append(": ");
                  ((StringBuilder)paramObject).append(localObject2);
                }
              }
            }
          }
          else
          {
            return ((StringBuilder)paramObject).toString();
            paramObject = ((Throwable)localObject1).toString();
            break;
          }
          i += 1;
        }
      }
      if ((paramObject instanceof zzcfo)) {
        return zzcfo.zza((zzcfo)paramObject);
      }
      if (paramBoolean) {
        return "-";
      }
      return String.valueOf(paramObject);
    }
  }
  
  protected static Object zzdZ(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return new zzcfo(paramString);
  }
  
  private static String zzea(String paramString)
  {
    String str;
    if (TextUtils.isEmpty(paramString)) {
      str = "";
    }
    int i;
    do
    {
      return str;
      i = paramString.lastIndexOf('.');
      str = paramString;
    } while (i == -1);
    return paramString.substring(0, i);
  }
  
  protected final void zza(int paramInt, boolean paramBoolean1, boolean paramBoolean2, String paramString, Object paramObject1, Object paramObject2, Object paramObject3)
  {
    if ((!paramBoolean1) && (zzz(paramInt))) {
      zzk(paramInt, zza(false, paramString, paramObject1, paramObject2, paramObject3));
    }
    zzcgg localzzcgg;
    if ((!paramBoolean2) && (paramInt >= 5))
    {
      zzbo.zzu(paramString);
      localzzcgg = this.zzboe.zzyR();
      if (localzzcgg == null) {
        zzk(6, "Scheduler not set. Not logging error/warn");
      }
    }
    else
    {
      return;
    }
    if (!localzzcgg.isInitialized())
    {
      zzk(6, "Scheduler not initialized. Not logging error/warn");
      return;
    }
    if (paramInt < 0) {
      paramInt = 0;
    }
    for (;;)
    {
      int i = paramInt;
      if (paramInt >= 9) {
        i = 8;
      }
      String str = String.valueOf("2");
      char c1 = "01VDIWEA?".charAt(i);
      char c2 = this.zzbqL;
      long l = this.zzboC;
      paramObject1 = String.valueOf(zza(true, paramString, paramObject1, paramObject2, paramObject3));
      paramObject2 = String.valueOf(str).length() + 23 + String.valueOf(paramObject1).length() + str + c1 + c2 + l + ":" + (String)paramObject1;
      paramObject1 = paramObject2;
      if (((String)paramObject2).length() > 1024) {
        paramObject1 = paramString.substring(0, 1024);
      }
      localzzcgg.zzj(new zzcfm(this, (String)paramObject1));
      return;
    }
  }
  
  protected final void zzjD() {}
  
  protected final void zzk(int paramInt, String paramString)
  {
    Log.println(paramInt, this.zzaIb, paramString);
  }
  
  public final zzcfn zzyA()
  {
    return this.zzbqR;
  }
  
  public final zzcfn zzyB()
  {
    return this.zzbqS;
  }
  
  public final zzcfn zzyC()
  {
    return this.zzbqT;
  }
  
  public final zzcfn zzyD()
  {
    return this.zzbqU;
  }
  
  public final String zzyE()
  {
    Object localObject = super.zzwG().zzbrj.zzmb();
    if ((localObject == null) || (localObject == zzcfw.zzbri)) {
      return null;
    }
    String str = String.valueOf(String.valueOf(((Pair)localObject).second));
    localObject = (String)((Pair)localObject).first;
    return String.valueOf(str).length() + 1 + String.valueOf(localObject).length() + str + ":" + (String)localObject;
  }
  
  public final zzcfn zzyx()
  {
    return this.zzbqM;
  }
  
  public final zzcfn zzyy()
  {
    return this.zzbqN;
  }
  
  public final zzcfn zzyz()
  {
    return this.zzbqP;
  }
  
  protected final boolean zzz(int paramInt)
  {
    return Log.isLoggable(this.zzaIb, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcfl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */