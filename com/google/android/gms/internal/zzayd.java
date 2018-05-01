package com.google.android.gms.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.WorkSource;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.stats.zze;
import com.google.android.gms.common.stats.zzg;
import com.google.android.gms.common.util.zzs;
import com.google.android.gms.common.util.zzv;
import com.google.android.gms.common.util.zzy;

public class zzayd
{
  private static boolean DEBUG = false;
  private static String TAG = "WakeLock";
  private static String zzbCt = "*gcore*:";
  private final Context mContext;
  private final String zzaGw;
  private final String zzaGy;
  private final PowerManager.WakeLock zzbCu;
  private final int zzbCv;
  private final String zzbCw;
  private boolean zzbCx = true;
  private int zzbCy;
  private int zzbCz;
  private WorkSource zzbiJ;
  
  public zzayd(Context paramContext, int paramInt, String paramString) {}
  
  @SuppressLint({"UnwrappedWakeLock"})
  public zzayd(Context paramContext, int paramInt, String paramString1, String paramString2, String paramString3)
  {
    this(paramContext, paramInt, paramString1, paramString2, paramString3, null);
  }
  
  @SuppressLint({"UnwrappedWakeLock"})
  public zzayd(Context paramContext, int paramInt, String paramString1, String paramString2, String paramString3, String paramString4)
  {
    zzac.zzh(paramString1, "Wake lock name can NOT be empty");
    this.zzbCv = paramInt;
    this.zzbCw = paramString2;
    this.zzaGy = paramString4;
    this.mContext = paramContext.getApplicationContext();
    if (!"com.google.android.gms".equals(paramContext.getPackageName()))
    {
      paramString2 = String.valueOf(zzbCt);
      paramString4 = String.valueOf(paramString1);
      if (paramString4.length() != 0) {
        paramString2 = paramString2.concat(paramString4);
      }
    }
    for (this.zzaGw = paramString2;; this.zzaGw = paramString1)
    {
      this.zzbCu = ((PowerManager)paramContext.getSystemService("power")).newWakeLock(paramInt, paramString1);
      if (zzy.zzaO(this.mContext))
      {
        paramString1 = paramString3;
        if (zzv.zzdD(paramString3)) {
          paramString1 = paramContext.getPackageName();
        }
        this.zzbiJ = zzy.zzy(paramContext, paramString1);
        zzc(this.zzbiJ);
      }
      return;
      paramString2 = new String(paramString2);
      break;
    }
  }
  
  private void zzd(WorkSource paramWorkSource)
  {
    try
    {
      this.zzbCu.setWorkSource(paramWorkSource);
      return;
    }
    catch (IllegalArgumentException paramWorkSource)
    {
      Log.wtf(TAG, paramWorkSource.toString());
    }
  }
  
  private void zzgP(String paramString)
  {
    boolean bool = zzgQ(paramString);
    paramString = zzo(paramString, bool);
    try
    {
      if (this.zzbCx)
      {
        int i = this.zzbCy - 1;
        this.zzbCy = i;
        if ((i == 0) || (bool)) {}
      }
      else
      {
        if ((this.zzbCx) || (this.zzbCz != 1)) {
          break label107;
        }
      }
      zzg.zzyr().zza(this.mContext, zze.zza(this.zzbCu, paramString), 8, this.zzaGw, paramString, this.zzaGy, this.zzbCv, zzy.zzb(this.zzbiJ));
      this.zzbCz -= 1;
      label107:
      return;
    }
    finally {}
  }
  
  private boolean zzgQ(String paramString)
  {
    return (!TextUtils.isEmpty(paramString)) && (!paramString.equals(this.zzbCw));
  }
  
  private void zzm(String paramString, long paramLong)
  {
    boolean bool = zzgQ(paramString);
    paramString = zzo(paramString, bool);
    try
    {
      if (this.zzbCx)
      {
        int i = this.zzbCy;
        this.zzbCy = (i + 1);
        if ((i == 0) || (bool)) {}
      }
      else
      {
        if ((this.zzbCx) || (this.zzbCz != 0)) {
          break label113;
        }
      }
      zzg.zzyr().zza(this.mContext, zze.zza(this.zzbCu, paramString), 7, this.zzaGw, paramString, this.zzaGy, this.zzbCv, zzy.zzb(this.zzbiJ), paramLong);
      this.zzbCz += 1;
      label113:
      return;
    }
    finally {}
  }
  
  private String zzo(String paramString, boolean paramBoolean)
  {
    if (this.zzbCx)
    {
      if (paramBoolean) {
        return paramString;
      }
      return this.zzbCw;
    }
    return this.zzbCw;
  }
  
  public void acquire(long paramLong)
  {
    String str2;
    if ((!zzs.zzyA()) && (this.zzbCx))
    {
      str2 = TAG;
      str1 = String.valueOf(this.zzaGw);
      if (str1.length() == 0) {
        break label62;
      }
    }
    label62:
    for (String str1 = "Do not acquire with timeout on reference counted WakeLocks before ICS. wakelock: ".concat(str1);; str1 = new String("Do not acquire with timeout on reference counted WakeLocks before ICS. wakelock: "))
    {
      Log.wtf(str2, str1);
      zzm(null, paramLong);
      this.zzbCu.acquire(paramLong);
      return;
    }
  }
  
  public boolean isHeld()
  {
    return this.zzbCu.isHeld();
  }
  
  public void release()
  {
    zzgP(null);
    this.zzbCu.release();
  }
  
  public void setReferenceCounted(boolean paramBoolean)
  {
    this.zzbCu.setReferenceCounted(paramBoolean);
    this.zzbCx = paramBoolean;
  }
  
  public void zzc(WorkSource paramWorkSource)
  {
    if ((paramWorkSource != null) && (zzy.zzaO(this.mContext)))
    {
      if (this.zzbiJ == null) {
        break label39;
      }
      this.zzbiJ.add(paramWorkSource);
    }
    for (;;)
    {
      zzd(this.zzbiJ);
      return;
      label39:
      this.zzbiJ = paramWorkSource;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzayd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */