package com.google.android.gms.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.WorkSource;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.stats.zzf;
import com.google.android.gms.common.stats.zzh;
import com.google.android.gms.common.util.zzs;
import com.google.android.gms.common.util.zzw;
import com.google.android.gms.common.util.zzz;

public class zzxb
{
  private static boolean DEBUG = false;
  private static String TAG = "WakeLock";
  private static String aAo = "*gcore*:";
  private final String EA;
  private final String Ey;
  private final PowerManager.WakeLock aAp;
  private final int aAq;
  private final String aAr;
  private boolean aAs = true;
  private int aAt;
  private int aAu;
  private WorkSource agC;
  private final Context mContext;
  
  public zzxb(Context paramContext, int paramInt, String paramString) {}
  
  @SuppressLint({"UnwrappedWakeLock"})
  public zzxb(Context paramContext, int paramInt, String paramString1, String paramString2, String paramString3)
  {
    this(paramContext, paramInt, paramString1, paramString2, paramString3, null);
  }
  
  @SuppressLint({"UnwrappedWakeLock"})
  public zzxb(Context paramContext, int paramInt, String paramString1, String paramString2, String paramString3, String paramString4)
  {
    zzac.zzh(paramString1, "Wake lock name can NOT be empty");
    this.aAq = paramInt;
    this.aAr = paramString2;
    this.EA = paramString4;
    this.mContext = paramContext.getApplicationContext();
    if (!"com.google.android.gms".equals(paramContext.getPackageName()))
    {
      paramString2 = String.valueOf(aAo);
      paramString4 = String.valueOf(paramString1);
      if (paramString4.length() != 0) {
        paramString2 = paramString2.concat(paramString4);
      }
    }
    for (this.Ey = paramString2;; this.Ey = paramString1)
    {
      this.aAp = ((PowerManager)paramContext.getSystemService("power")).newWakeLock(paramInt, paramString1);
      if (zzz.zzcp(this.mContext))
      {
        paramString1 = paramString3;
        if (zzw.zzij(paramString3)) {
          paramString1 = paramContext.getPackageName();
        }
        this.agC = zzz.zzy(paramContext, paramString1);
        zzc(this.agC);
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
      this.aAp.setWorkSource(paramWorkSource);
      return;
    }
    catch (IllegalArgumentException paramWorkSource)
    {
      Log.wtf(TAG, paramWorkSource.toString());
    }
  }
  
  private void zzm(String paramString, long paramLong)
  {
    boolean bool = zzop(paramString);
    paramString = zzp(paramString, bool);
    try
    {
      if (this.aAs)
      {
        int i = this.aAt;
        this.aAt = (i + 1);
        if ((i == 0) || (bool)) {}
      }
      else
      {
        if ((this.aAs) || (this.aAu != 0)) {
          break label113;
        }
      }
      zzh.zzaxf().zza(this.mContext, zzf.zza(this.aAp, paramString), 7, this.Ey, paramString, this.EA, this.aAq, zzz.zzb(this.agC), paramLong);
      this.aAu += 1;
      label113:
      return;
    }
    finally {}
  }
  
  private void zzoo(String paramString)
  {
    boolean bool = zzop(paramString);
    paramString = zzp(paramString, bool);
    try
    {
      if (this.aAs)
      {
        int i = this.aAt - 1;
        this.aAt = i;
        if ((i == 0) || (bool)) {}
      }
      else
      {
        if ((this.aAs) || (this.aAu != 1)) {
          break label107;
        }
      }
      zzh.zzaxf().zza(this.mContext, zzf.zza(this.aAp, paramString), 8, this.Ey, paramString, this.EA, this.aAq, zzz.zzb(this.agC));
      this.aAu -= 1;
      label107:
      return;
    }
    finally {}
  }
  
  private boolean zzop(String paramString)
  {
    return (!TextUtils.isEmpty(paramString)) && (!paramString.equals(this.aAr));
  }
  
  private String zzp(String paramString, boolean paramBoolean)
  {
    if (this.aAs)
    {
      if (paramBoolean) {
        return paramString;
      }
      return this.aAr;
    }
    return this.aAr;
  }
  
  public void acquire(long paramLong)
  {
    String str2;
    if ((!zzs.zzaxn()) && (this.aAs))
    {
      str2 = TAG;
      str1 = String.valueOf(this.Ey);
      if (str1.length() == 0) {
        break label62;
      }
    }
    label62:
    for (String str1 = "Do not acquire with timeout on reference counted WakeLocks before ICS. wakelock: ".concat(str1);; str1 = new String("Do not acquire with timeout on reference counted WakeLocks before ICS. wakelock: "))
    {
      Log.wtf(str2, str1);
      zzm(null, paramLong);
      this.aAp.acquire(paramLong);
      return;
    }
  }
  
  public boolean isHeld()
  {
    return this.aAp.isHeld();
  }
  
  public void release()
  {
    zzoo(null);
    this.aAp.release();
  }
  
  public void setReferenceCounted(boolean paramBoolean)
  {
    this.aAp.setReferenceCounted(paramBoolean);
    this.aAs = paramBoolean;
  }
  
  public void zzc(WorkSource paramWorkSource)
  {
    if ((paramWorkSource != null) && (zzz.zzcp(this.mContext)))
    {
      if (this.agC == null) {
        break label39;
      }
      this.agC.add(paramWorkSource);
    }
    for (;;)
    {
      zzd(this.agC);
      return;
      label39:
      this.agC = paramWorkSource;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzxb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */