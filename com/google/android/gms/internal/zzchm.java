package com.google.android.gms.internal;

import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.measurement.AppMeasurement;

public final class zzchm
  extends zzcjl
{
  private final String zzgay = (String)zzchc.zzjad.get();
  private final long zzixd = 11910L;
  private final char zzjbt;
  private final zzcho zzjbu;
  private final zzcho zzjbv;
  private final zzcho zzjbw;
  private final zzcho zzjbx;
  private final zzcho zzjby;
  private final zzcho zzjbz;
  private final zzcho zzjca;
  private final zzcho zzjcb;
  private final zzcho zzjcc;
  
  zzchm(zzcim paramzzcim)
  {
    super(paramzzcim);
    if (zzaxa().zzyp()) {}
    for (this.zzjbt = 'C';; this.zzjbt = 'c')
    {
      this.zzjbu = new zzcho(this, 6, false, false);
      this.zzjbv = new zzcho(this, 6, true, false);
      this.zzjbw = new zzcho(this, 6, false, true);
      this.zzjbx = new zzcho(this, 5, false, false);
      this.zzjby = new zzcho(this, 5, true, false);
      this.zzjbz = new zzcho(this, 5, false, true);
      this.zzjca = new zzcho(this, 4, false, false);
      this.zzjcb = new zzcho(this, 3, false, false);
      this.zzjcc = new zzcho(this, 2, false, false);
      return;
    }
  }
  
  private static String zza(boolean paramBoolean, String paramString, Object paramObject1, Object paramObject2, Object paramObject3)
  {
    String str1 = paramString;
    if (paramString == null) {
      str1 = "";
    }
    String str2 = zzb(paramBoolean, paramObject1);
    paramObject2 = zzb(paramBoolean, paramObject2);
    paramObject3 = zzb(paramBoolean, paramObject3);
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
  
  private static String zzb(boolean paramBoolean, Object paramObject)
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
          str1 = zzjl(AppMeasurement.class.getCanonicalName());
          str2 = zzjl(zzcim.class.getCanonicalName());
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
                str3 = zzjl(str3);
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
      if ((paramObject instanceof zzchp)) {
        return zzchp.zza((zzchp)paramObject);
      }
      if (paramBoolean) {
        return "-";
      }
      return String.valueOf(paramObject);
    }
  }
  
  protected static Object zzjk(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return new zzchp(paramString);
  }
  
  private static String zzjl(String paramString)
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
    if ((!paramBoolean1) && (zzae(paramInt))) {
      zzk(paramInt, zza(false, paramString, paramObject1, paramObject2, paramObject3));
    }
    zzcih localzzcih;
    if ((!paramBoolean2) && (paramInt >= 5))
    {
      zzbq.checkNotNull(paramString);
      localzzcih = this.zziwf.zzazy();
      if (localzzcih == null) {
        zzk(6, "Scheduler not set. Not logging error/warn");
      }
    }
    else
    {
      return;
    }
    if (!localzzcih.isInitialized())
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
      char c1 = "01VDIWEA?".charAt(i);
      char c2 = this.zzjbt;
      long l = this.zzixd;
      paramObject1 = zza(true, paramString, paramObject1, paramObject2, paramObject3);
      paramObject2 = String.valueOf("2").length() + 23 + String.valueOf(paramObject1).length() + "2" + c1 + c2 + l + ":" + (String)paramObject1;
      paramObject1 = paramObject2;
      if (((String)paramObject2).length() > 1024) {
        paramObject1 = paramString.substring(0, 1024);
      }
      localzzcih.zzg(new zzchn(this, (String)paramObject1));
      return;
    }
  }
  
  protected final boolean zzae(int paramInt)
  {
    return Log.isLoggable(this.zzgay, paramInt);
  }
  
  protected final boolean zzaxz()
  {
    return false;
  }
  
  public final zzcho zzazd()
  {
    return this.zzjbu;
  }
  
  public final zzcho zzaze()
  {
    return this.zzjbv;
  }
  
  public final zzcho zzazf()
  {
    return this.zzjbx;
  }
  
  public final zzcho zzazg()
  {
    return this.zzjbz;
  }
  
  public final zzcho zzazh()
  {
    return this.zzjca;
  }
  
  public final zzcho zzazi()
  {
    return this.zzjcb;
  }
  
  public final zzcho zzazj()
  {
    return this.zzjcc;
  }
  
  public final String zzazk()
  {
    Object localObject = zzawz().zzjcq.zzaad();
    if ((localObject == null) || (localObject == zzchx.zzjcp)) {
      return null;
    }
    String str = String.valueOf(((Pair)localObject).second);
    localObject = (String)((Pair)localObject).first;
    return String.valueOf(str).length() + 1 + String.valueOf(localObject).length() + str + ":" + (String)localObject;
  }
  
  protected final void zzk(int paramInt, String paramString)
  {
    Log.println(paramInt, this.zzgay, paramString);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzchm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */