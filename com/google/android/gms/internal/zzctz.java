package com.google.android.gms.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.WorkSource;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.stats.zzc;
import com.google.android.gms.common.stats.zze;
import com.google.android.gms.common.util.zzt;
import com.google.android.gms.common.util.zzx;

public final class zzctz
{
  private static boolean DEBUG = false;
  private static String TAG = "WakeLock";
  private static String zzbCW = "*gcore*:";
  private final Context mContext;
  private final String zzaJp;
  private final String zzaJr;
  private final PowerManager.WakeLock zzbCX;
  private WorkSource zzbCY;
  private final int zzbCZ;
  private final String zzbDa;
  private boolean zzbDb = true;
  private int zzbDc;
  private int zzbDd;
  
  public zzctz(Context paramContext, int paramInt, String paramString) {}
  
  @SuppressLint({"UnwrappedWakeLock"})
  private zzctz(Context paramContext, int paramInt, String paramString1, String paramString2, String paramString3)
  {
    this(paramContext, 1, paramString1, null, paramString3, null);
  }
  
  @SuppressLint({"UnwrappedWakeLock"})
  private zzctz(Context paramContext, int paramInt, String paramString1, String paramString2, String paramString3, String paramString4)
  {
    zzbo.zzh(paramString1, "Wake lock name can NOT be empty");
    this.zzbCZ = paramInt;
    this.zzbDa = null;
    this.zzaJr = null;
    this.mContext = paramContext.getApplicationContext();
    if (!"com.google.android.gms".equals(paramContext.getPackageName()))
    {
      paramString2 = String.valueOf(zzbCW);
      paramString4 = String.valueOf(paramString1);
      if (paramString4.length() != 0)
      {
        paramString2 = paramString2.concat(paramString4);
        this.zzaJp = paramString2;
        label88:
        this.zzbCX = ((PowerManager)paramContext.getSystemService("power")).newWakeLock(paramInt, paramString1);
        if (zzx.zzaM(this.mContext))
        {
          paramString1 = paramString3;
          if (zzt.zzcL(paramString3)) {
            paramString1 = paramContext.getPackageName();
          }
          this.zzbCY = zzx.zzD(paramContext, paramString1);
          paramContext = this.zzbCY;
          if ((paramContext != null) && (zzx.zzaM(this.mContext)))
          {
            if (this.zzbCY == null) {
              break label212;
            }
            this.zzbCY.add(paramContext);
          }
        }
      }
    }
    for (;;)
    {
      paramContext = this.zzbCY;
      try
      {
        this.zzbCX.setWorkSource(paramContext);
        return;
      }
      catch (IllegalArgumentException paramContext)
      {
        Log.wtf(TAG, paramContext.toString());
      }
      paramString2 = new String(paramString2);
      break;
      this.zzaJp = paramString1;
      break label88;
      label212:
      this.zzbCY = paramContext;
    }
  }
  
  private final boolean zzeV(String paramString)
  {
    if (!TextUtils.isEmpty(null))
    {
      paramString = this.zzbDa;
      throw new NullPointerException();
    }
    return false;
  }
  
  private final String zzi(String paramString, boolean paramBoolean)
  {
    if (this.zzbDb)
    {
      if (paramBoolean) {
        return null;
      }
      return this.zzbDa;
    }
    return this.zzbDa;
  }
  
  public final void acquire(long paramLong)
  {
    boolean bool = zzeV(null);
    String str = zzi(null, bool);
    try
    {
      if (this.zzbDb)
      {
        int i = this.zzbDc;
        this.zzbDc = (i + 1);
        if ((i == 0) || (bool)) {}
      }
      else
      {
        if ((this.zzbDb) || (this.zzbDd != 0)) {
          break label113;
        }
      }
      zze.zzrX();
      zze.zza(this.mContext, zzc.zza(this.zzbCX, str), 7, this.zzaJp, str, null, this.zzbCZ, zzx.zzb(this.zzbCY), 1000L);
      this.zzbDd += 1;
      label113:
      this.zzbCX.acquire(1000L);
      return;
    }
    finally {}
  }
  
  public final boolean isHeld()
  {
    return this.zzbCX.isHeld();
  }
  
  public final void release()
  {
    boolean bool = zzeV(null);
    String str = zzi(null, bool);
    try
    {
      if (this.zzbDb)
      {
        int i = this.zzbDc - 1;
        this.zzbDc = i;
        if ((i == 0) || (bool)) {}
      }
      else
      {
        if ((this.zzbDb) || (this.zzbDd != 1)) {
          break label105;
        }
      }
      zze.zzrX();
      zze.zza(this.mContext, zzc.zza(this.zzbCX, str), 8, this.zzaJp, str, null, this.zzbCZ, zzx.zzb(this.zzbCY));
      this.zzbDd -= 1;
      label105:
      this.zzbCX.release();
      return;
    }
    finally {}
  }
  
  public final void setReferenceCounted(boolean paramBoolean)
  {
    this.zzbCX.setReferenceCounted(false);
    this.zzbDb = false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzctz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */