package com.google.android.gms.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.WorkSource;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.stats.zze;
import com.google.android.gms.common.stats.zzg;
import com.google.android.gms.common.util.zzs;
import com.google.android.gms.common.util.zzv;
import com.google.android.gms.common.util.zzy;

public class zzxr
{
  private static boolean DEBUG = false;
  private static String TAG = "WakeLock";
  private static String aDy = "*gcore*:";
  private final String Gc;
  private final String Ge;
  private final int aDA;
  private final String aDB;
  private boolean aDC = true;
  private int aDD;
  private int aDE;
  private final PowerManager.WakeLock aDz;
  private WorkSource ajz;
  private final Context mContext;
  
  public zzxr(Context paramContext, int paramInt, String paramString) {}
  
  @SuppressLint({"UnwrappedWakeLock"})
  public zzxr(Context paramContext, int paramInt, String paramString1, String paramString2, String paramString3)
  {
    this(paramContext, paramInt, paramString1, paramString2, paramString3, null);
  }
  
  @SuppressLint({"UnwrappedWakeLock"})
  public zzxr(Context paramContext, int paramInt, String paramString1, String paramString2, String paramString3, String paramString4)
  {
    zzaa.zzh(paramString1, "Wake lock name can NOT be empty");
    this.aDA = paramInt;
    this.aDB = paramString2;
    this.Ge = paramString4;
    this.mContext = paramContext.getApplicationContext();
    if (!"com.google.android.gms".equals(paramContext.getPackageName()))
    {
      paramString2 = String.valueOf(aDy);
      paramString4 = String.valueOf(paramString1);
      if (paramString4.length() != 0) {
        paramString2 = paramString2.concat(paramString4);
      }
    }
    for (this.Gc = paramString2;; this.Gc = paramString1)
    {
      this.aDz = ((PowerManager)paramContext.getSystemService("power")).newWakeLock(paramInt, paramString1);
      if (zzy.zzcm(this.mContext))
      {
        paramString1 = paramString3;
        if (zzv.zzij(paramString3)) {
          paramString1 = paramContext.getPackageName();
        }
        this.ajz = zzy.zzy(paramContext, paramString1);
        zzc(this.ajz);
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
      this.aDz.setWorkSource(paramWorkSource);
      return;
    }
    catch (IllegalArgumentException paramWorkSource)
    {
      Log.wtf(TAG, paramWorkSource.toString());
    }
  }
  
  private void zzk(String paramString, long paramLong)
  {
    boolean bool = zzoo(paramString);
    paramString = zzp(paramString, bool);
    try
    {
      if (this.aDC)
      {
        int i = this.aDD;
        this.aDD = (i + 1);
        if ((i == 0) || (bool)) {}
      }
      else
      {
        if ((this.aDC) || (this.aDE != 0)) {
          break label113;
        }
      }
      zzg.zzayg().zza(this.mContext, zze.zza(this.aDz, paramString), 7, this.Gc, paramString, this.Ge, this.aDA, zzy.zzb(this.ajz), paramLong);
      this.aDE += 1;
      label113:
      return;
    }
    finally {}
  }
  
  private void zzon(String paramString)
  {
    boolean bool = zzoo(paramString);
    paramString = zzp(paramString, bool);
    try
    {
      if (this.aDC)
      {
        int i = this.aDD - 1;
        this.aDD = i;
        if ((i == 0) || (bool)) {}
      }
      else
      {
        if ((this.aDC) || (this.aDE != 1)) {
          break label107;
        }
      }
      zzg.zzayg().zza(this.mContext, zze.zza(this.aDz, paramString), 8, this.Gc, paramString, this.Ge, this.aDA, zzy.zzb(this.ajz));
      this.aDE -= 1;
      label107:
      return;
    }
    finally {}
  }
  
  private boolean zzoo(String paramString)
  {
    return (!TextUtils.isEmpty(paramString)) && (!paramString.equals(this.aDB));
  }
  
  private String zzp(String paramString, boolean paramBoolean)
  {
    if (this.aDC)
    {
      if (paramBoolean) {
        return paramString;
      }
      return this.aDB;
    }
    return this.aDB;
  }
  
  public void acquire(long paramLong)
  {
    String str2;
    if ((!zzs.zzayq()) && (this.aDC))
    {
      str2 = TAG;
      str1 = String.valueOf(this.Gc);
      if (str1.length() == 0) {
        break label62;
      }
    }
    label62:
    for (String str1 = "Do not acquire with timeout on reference counted WakeLocks before ICS. wakelock: ".concat(str1);; str1 = new String("Do not acquire with timeout on reference counted WakeLocks before ICS. wakelock: "))
    {
      Log.wtf(str2, str1);
      zzk(null, paramLong);
      this.aDz.acquire(paramLong);
      return;
    }
  }
  
  public boolean isHeld()
  {
    return this.aDz.isHeld();
  }
  
  public void release()
  {
    zzon(null);
    this.aDz.release();
  }
  
  public void setReferenceCounted(boolean paramBoolean)
  {
    this.aDz.setReferenceCounted(paramBoolean);
    this.aDC = paramBoolean;
  }
  
  public void zzc(WorkSource paramWorkSource)
  {
    if ((paramWorkSource != null) && (zzy.zzcm(this.mContext)))
    {
      if (this.ajz == null) {
        break label39;
      }
      this.ajz.add(paramWorkSource);
    }
    for (;;)
    {
      zzd(this.ajz);
      return;
      label39:
      this.ajz = paramWorkSource;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzxr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */