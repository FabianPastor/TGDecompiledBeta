package com.google.android.gms.internal;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.measurement.AppMeasurement;

public class zzatx
  extends zzauh
{
  private final String zzaGr = zzKn().zzKK();
  private final long zzbqw = zzKn().zzKv();
  private final char zzbsC;
  private final zza zzbsD;
  private final zza zzbsE;
  private final zza zzbsF;
  private final zza zzbsG;
  private final zza zzbsH;
  private final zza zzbsI;
  private final zza zzbsJ;
  private final zza zzbsK;
  private final zza zzbsL;
  
  zzatx(zzaue paramzzaue)
  {
    super(paramzzaue);
    if (zzKn().zzoW()) {
      zzKn().zzLh();
    }
    for (this.zzbsC = 'C';; this.zzbsC = 'c')
    {
      this.zzbsD = new zza(6, false, false);
      this.zzbsE = new zza(6, true, false);
      this.zzbsF = new zza(6, false, true);
      this.zzbsG = new zza(5, false, false);
      this.zzbsH = new zza(5, true, false);
      this.zzbsI = new zza(5, false, true);
      this.zzbsJ = new zza(4, false, false);
      this.zzbsK = new zza(3, false, false);
      this.zzbsL = new zza(2, false, false);
      return;
      zzKn().zzLh();
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
          str1 = zzfF(AppMeasurement.class.getCanonicalName());
          str2 = zzfF(zzaue.class.getCanonicalName());
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
          str3 = zzfF(str3);
        } while ((!str3.equals(str1)) && (!str3.equals(str2)));
        ((StringBuilder)paramObject).append(": ");
        ((StringBuilder)paramObject).append(localObject2);
        label362:
        return ((StringBuilder)paramObject).toString();
      }
      if ((paramObject instanceof zzb)) {
        return zzb.zza((zzb)paramObject);
      }
      if (paramBoolean) {
        return "-";
      }
      return String.valueOf(paramObject);
    }
  }
  
  protected static Object zzfE(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return new zzb(paramString);
  }
  
  private static String zzfF(String paramString)
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
  
  public zza zzLZ()
  {
    return this.zzbsD;
  }
  
  public zza zzMa()
  {
    return this.zzbsE;
  }
  
  public zza zzMb()
  {
    return this.zzbsG;
  }
  
  public zza zzMc()
  {
    return this.zzbsI;
  }
  
  public zza zzMd()
  {
    return this.zzbsJ;
  }
  
  public zza zzMe()
  {
    return this.zzbsK;
  }
  
  public zza zzMf()
  {
    return this.zzbsL;
  }
  
  public String zzMg()
  {
    Object localObject = zzKm().zzbta.zzqm();
    if ((localObject == null) || (localObject == zzaua.zzbsZ)) {
      return null;
    }
    String str = String.valueOf(String.valueOf(((Pair)localObject).second));
    localObject = (String)((Pair)localObject).first;
    return String.valueOf(str).length() + 1 + String.valueOf(localObject).length() + str + ":" + (String)localObject;
  }
  
  protected void zza(int paramInt, boolean paramBoolean1, boolean paramBoolean2, String paramString, Object paramObject1, Object paramObject2, Object paramObject3)
  {
    if ((!paramBoolean1) && (zzak(paramInt))) {
      zzn(paramInt, zza(false, paramString, paramObject1, paramObject2, paramObject3));
    }
    if ((!paramBoolean2) && (paramInt >= 5)) {
      zzb(paramInt, paramString, paramObject1, paramObject2, paramObject3);
    }
  }
  
  protected boolean zzak(int paramInt)
  {
    return Log.isLoggable(this.zzaGr, paramInt);
  }
  
  public void zzb(int paramInt, String paramString, final Object paramObject1, Object paramObject2, Object paramObject3)
  {
    zzac.zzw(paramString);
    zzaud localzzaud = this.zzbqb.zzMw();
    if (localzzaud == null)
    {
      zzn(6, "Scheduler not set. Not logging error/warn");
      return;
    }
    if (!localzzaud.isInitialized())
    {
      zzn(6, "Scheduler not initialized. Not logging error/warn");
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
    String str = String.valueOf("2");
    char c1 = "01VDIWEA?".charAt(paramInt);
    char c2 = this.zzbsC;
    long l = this.zzbqw;
    paramObject1 = String.valueOf(zza(true, paramString, paramObject1, paramObject2, paramObject3));
    paramObject2 = String.valueOf(str).length() + 23 + String.valueOf(paramObject1).length() + str + c1 + c2 + l + ":" + (String)paramObject1;
    paramObject1 = paramObject2;
    if (((String)paramObject2).length() > 1024) {
      paramObject1 = paramString.substring(0, 1024);
    }
    localzzaud.zzm(new Runnable()
    {
      public void run()
      {
        zzaua localzzaua = zzatx.this.zzbqb.zzKm();
        if (localzzaua.isInitialized())
        {
          localzzaua.zzbta.zzcc(paramObject1);
          return;
        }
        zzatx.this.zzn(6, "Persisted config not initialized. Not logging error/warn");
      }
    });
  }
  
  protected void zzmS() {}
  
  protected void zzn(int paramInt, String paramString)
  {
    Log.println(paramInt, this.zzaGr, paramString);
  }
  
  public class zza
  {
    private final int mPriority;
    private final boolean zzbsO;
    private final boolean zzbsP;
    
    zza(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    {
      this.mPriority = paramInt;
      this.zzbsO = paramBoolean1;
      this.zzbsP = paramBoolean2;
    }
    
    public void log(String paramString)
    {
      zzatx.this.zza(this.mPriority, this.zzbsO, this.zzbsP, paramString, null, null, null);
    }
    
    public void zzd(String paramString, Object paramObject1, Object paramObject2, Object paramObject3)
    {
      zzatx.this.zza(this.mPriority, this.zzbsO, this.zzbsP, paramString, paramObject1, paramObject2, paramObject3);
    }
    
    public void zze(String paramString, Object paramObject1, Object paramObject2)
    {
      zzatx.this.zza(this.mPriority, this.zzbsO, this.zzbsP, paramString, paramObject1, paramObject2, null);
    }
    
    public void zzj(String paramString, Object paramObject)
    {
      zzatx.this.zza(this.mPriority, this.zzbsO, this.zzbsP, paramString, paramObject, null, null);
    }
  }
  
  private static class zzb
  {
    private final String zzbsQ;
    
    public zzb(@NonNull String paramString)
    {
      this.zzbsQ = paramString;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzatx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */