package com.google.android.gms.measurement.internal;

import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.google.android.gms.measurement.AppMeasurement;

public class zzq
  extends zzaa
{
  private final String EC = zzbwd().zzbub();
  private final long aqN = zzbwd().zzbto();
  private final char asA;
  private final zza asB;
  private final zza asC;
  private final zza asD;
  private final zza asE;
  private final zza asF;
  private final zza asG;
  private final zza asH;
  private final zza asI;
  private final zza asJ;
  
  zzq(zzx paramzzx)
  {
    super(paramzzx);
    if (zzbwd().zzaef()) {
      zzbwd().zzayi();
    }
    for (this.asA = 'C';; this.asA = 'c')
    {
      this.asB = new zza(6, false, false);
      this.asC = new zza(6, true, false);
      this.asD = new zza(6, false, true);
      this.asE = new zza(5, false, false);
      this.asF = new zza(5, true, false);
      this.asG = new zza(5, false, true);
      this.asH = new zza(4, false, false);
      this.asI = new zza(3, false, false);
      this.asJ = new zza(2, false, false);
      return;
      zzbwd().zzayi();
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
    if ((!paramBoolean1) && (zzbi(paramInt))) {
      zzn(paramInt, zza(false, paramString, paramObject1, paramObject2, paramObject3));
    }
    if ((!paramBoolean2) && (paramInt >= 5)) {
      zzb(paramInt, paramString, paramObject1, paramObject2, paramObject3);
    }
  }
  
  public void zzb(int paramInt, String paramString, final Object paramObject1, Object paramObject2, Object paramObject3)
  {
    com.google.android.gms.common.internal.zzaa.zzy(paramString);
    zzw localzzw = this.aqw.zzbxs();
    if (localzzw == null)
    {
      zzn(6, "Scheduler not set. Not logging error/warn.");
      return;
    }
    if (!localzzw.isInitialized())
    {
      zzn(6, "Scheduler not initialized. Not logging error/warn.");
      return;
    }
    if (localzzw.zzbyn())
    {
      zzn(6, "Scheduler shutdown. Not logging error/warn.");
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
    char c2 = this.asA;
    long l = this.aqN;
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
        zzt localzzt = zzq.this.aqw.zzbwc();
        if ((!localzzt.isInitialized()) || (localzzt.zzbyn()))
        {
          zzq.this.zzn(6, "Persisted config not initialized . Not logging error/warn.");
          return;
        }
        localzzt.asY.zzfg(paramObject1);
      }
    });
  }
  
  protected boolean zzbi(int paramInt)
  {
    return Log.isLoggable(this.EC, paramInt);
  }
  
  public zza zzbwy()
  {
    return this.asB;
  }
  
  public zza zzbwz()
  {
    return this.asC;
  }
  
  public zza zzbxa()
  {
    return this.asE;
  }
  
  public zza zzbxb()
  {
    return this.asG;
  }
  
  public zza zzbxc()
  {
    return this.asH;
  }
  
  public zza zzbxd()
  {
    return this.asI;
  }
  
  public zza zzbxe()
  {
    return this.asJ;
  }
  
  public String zzbxf()
  {
    Object localObject = zzbwc().asY.zzagw();
    if ((localObject == null) || (localObject == zzt.asX)) {
      return null;
    }
    String str = String.valueOf(String.valueOf(((Pair)localObject).second));
    localObject = (String)((Pair)localObject).first;
    return String.valueOf(str).length() + 1 + String.valueOf(localObject).length() + str + ":" + (String)localObject;
  }
  
  protected void zzn(int paramInt, String paramString)
  {
    Log.println(paramInt, this.EC, paramString);
  }
  
  protected void zzzy() {}
  
  public class zza
  {
    private final boolean asM;
    private final boolean asN;
    private final int mPriority;
    
    zza(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    {
      this.mPriority = paramInt;
      this.asM = paramBoolean1;
      this.asN = paramBoolean2;
    }
    
    public void log(String paramString)
    {
      zzq.this.zza(this.mPriority, this.asM, this.asN, paramString, null, null, null);
    }
    
    public void zzd(String paramString, Object paramObject1, Object paramObject2, Object paramObject3)
    {
      zzq.this.zza(this.mPriority, this.asM, this.asN, paramString, paramObject1, paramObject2, paramObject3);
    }
    
    public void zze(String paramString, Object paramObject1, Object paramObject2)
    {
      zzq.this.zza(this.mPriority, this.asM, this.asN, paramString, paramObject1, paramObject2, null);
    }
    
    public void zzj(String paramString, Object paramObject)
    {
      zzq.this.zza(this.mPriority, this.asM, this.asN, paramString, paramObject, null, null);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */