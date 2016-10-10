package com.google.android.gms.measurement.internal;

import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.measurement.AppMeasurement;

public class zzp
  extends zzaa
{
  private final String CQ = zzbvi().zzbtl();
  private final long anE = zzbvi().zzbsy();
  private final char apm;
  private final zza apn;
  private final zza apo;
  private final zza app;
  private final zza apq;
  private final zza apr;
  private final zza aps;
  private final zza apt;
  private final zza apu;
  private final zza apv;
  
  zzp(zzx paramzzx)
  {
    super(paramzzx);
    if (zzbvi().zzacu())
    {
      if (zzbvi().zzact()) {}
      for (c = 'P';; c = 'C')
      {
        this.apm = c;
        this.apn = new zza(6, false, false);
        this.apo = new zza(6, true, false);
        this.app = new zza(6, false, true);
        this.apq = new zza(5, false, false);
        this.apr = new zza(5, true, false);
        this.aps = new zza(5, false, true);
        this.apt = new zza(4, false, false);
        this.apu = new zza(3, false, false);
        this.apv = new zza(2, false, false);
        return;
      }
    }
    if (zzbvi().zzact()) {}
    for (char c = 'p';; c = 'c')
    {
      this.apm = c;
      break;
    }
  }
  
  static String zza(boolean paramBoolean, String paramString, Object paramObject1, Object paramObject2, Object paramObject3)
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
  
  static String zzc(boolean paramBoolean, Object paramObject)
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
        int i;
        label274:
        Object localObject2;
        if (paramBoolean)
        {
          paramObject = localObject1.getClass().getName();
          paramObject = new StringBuilder((String)paramObject);
          str1 = zzmj(AppMeasurement.class.getCanonicalName());
          str2 = zzmj(zzx.class.getCanonicalName());
          localObject1 = ((Throwable)localObject1).getStackTrace();
          int j = localObject1.length;
          i = 0;
          if (i >= j) {
            break label362;
          }
          localObject2 = localObject1[i];
          if (!((StackTraceElement)localObject2).isNativeMethod()) {
            break label309;
          }
        }
        label309:
        String str3;
        do
        {
          do
          {
            i += 1;
            break label274;
            paramObject = ((Throwable)localObject1).toString();
            break;
            str3 = ((StackTraceElement)localObject2).getClassName();
          } while (str3 == null);
          str3 = zzmj(str3);
        } while ((!str3.equals(str1)) && (!str3.equals(str2)));
        ((StringBuilder)paramObject).append(": ");
        ((StringBuilder)paramObject).append(localObject2);
        label362:
        return ((StringBuilder)paramObject).toString();
      }
      if (paramBoolean) {
        return "-";
      }
      return String.valueOf(paramObject);
    }
  }
  
  private static String zzmj(String paramString)
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
  
  protected void zza(int paramInt, boolean paramBoolean1, boolean paramBoolean2, String paramString, Object paramObject1, Object paramObject2, Object paramObject3)
  {
    if ((!paramBoolean1) && (zzbf(paramInt))) {
      zzo(paramInt, zza(false, paramString, paramObject1, paramObject2, paramObject3));
    }
    if ((!paramBoolean2) && (paramInt >= 5)) {
      zzb(paramInt, paramString, paramObject1, paramObject2, paramObject3);
    }
  }
  
  public void zzb(int paramInt, String paramString, final Object paramObject1, Object paramObject2, Object paramObject3)
  {
    zzac.zzy(paramString);
    zzw localzzw = this.anq.zzbwx();
    if (localzzw == null)
    {
      zzo(6, "Scheduler not set. Not logging error/warn.");
      return;
    }
    if (!localzzw.isInitialized())
    {
      zzo(6, "Scheduler not initialized. Not logging error/warn.");
      return;
    }
    if (localzzw.zzbxt())
    {
      zzo(6, "Scheduler shutdown. Not logging error/warn.");
      return;
    }
    int i = paramInt;
    if (paramInt < 0) {
      i = 0;
    }
    paramInt = i;
    if (i >= "01VDIWEA?".length()) {
      paramInt = "01VDIWEA?".length() - 1;
    }
    String str = String.valueOf("1");
    char c1 = "01VDIWEA?".charAt(paramInt);
    char c2 = this.apm;
    long l = this.anE;
    paramObject1 = String.valueOf(zza(true, paramString, paramObject1, paramObject2, paramObject3));
    paramObject2 = String.valueOf(str).length() + 23 + String.valueOf(paramObject1).length() + str + c1 + c2 + l + ":" + (String)paramObject1;
    paramObject1 = paramObject2;
    if (((String)paramObject2).length() > 1024) {
      paramObject1 = paramString.substring(0, 1024);
    }
    localzzw.zzm(new Runnable()
    {
      public void run()
      {
        zzt localzzt = zzp.this.anq.zzbvh();
        if ((!localzzt.isInitialized()) || (localzzt.zzbxt()))
        {
          zzp.this.zzo(6, "Persisted config not initialized . Not logging error/warn.");
          return;
        }
        localzzt.apP.zzfd(paramObject1);
      }
    });
  }
  
  protected boolean zzbf(int paramInt)
  {
    return Log.isLoggable(this.CQ, paramInt);
  }
  
  public zza zzbwc()
  {
    return this.apn;
  }
  
  public zza zzbwd()
  {
    return this.apo;
  }
  
  public zza zzbwe()
  {
    return this.apq;
  }
  
  public zza zzbwf()
  {
    return this.apr;
  }
  
  public zza zzbwg()
  {
    return this.aps;
  }
  
  public zza zzbwh()
  {
    return this.apt;
  }
  
  public zza zzbwi()
  {
    return this.apu;
  }
  
  public zza zzbwj()
  {
    return this.apv;
  }
  
  public String zzbwk()
  {
    Object localObject = zzbvh().apP.zzafm();
    if ((localObject == null) || (localObject == zzt.apO)) {
      return null;
    }
    String str = String.valueOf(String.valueOf(((Pair)localObject).second));
    localObject = (String)((Pair)localObject).first;
    return String.valueOf(str).length() + 1 + String.valueOf(localObject).length() + str + ":" + (String)localObject;
  }
  
  protected void zzo(int paramInt, String paramString)
  {
    Log.println(paramInt, this.CQ, paramString);
  }
  
  protected void zzym() {}
  
  public class zza
  {
    private final boolean apy;
    private final boolean apz;
    private final int mPriority;
    
    zza(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    {
      this.mPriority = paramInt;
      this.apy = paramBoolean1;
      this.apz = paramBoolean2;
    }
    
    public void log(String paramString)
    {
      zzp.this.zza(this.mPriority, this.apy, this.apz, paramString, null, null, null);
    }
    
    public void zzd(String paramString, Object paramObject1, Object paramObject2, Object paramObject3)
    {
      zzp.this.zza(this.mPriority, this.apy, this.apz, paramString, paramObject1, paramObject2, paramObject3);
    }
    
    public void zze(String paramString, Object paramObject1, Object paramObject2)
    {
      zzp.this.zza(this.mPriority, this.apy, this.apz, paramString, paramObject1, paramObject2, null);
    }
    
    public void zzj(String paramString, Object paramObject)
    {
      zzp.this.zza(this.mPriority, this.apy, this.apz, paramString, paramObject, null, null);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */