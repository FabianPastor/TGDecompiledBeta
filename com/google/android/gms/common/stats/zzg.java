package com.google.android.gms.common.stats;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.util.zzj;
import java.util.List;

public class zzg
{
  private static String TAG = "WakeLockTracker";
  private static zzg zzaGH = new zzg();
  private static Boolean zzaGI;
  
  private static boolean zzaG(Context paramContext)
  {
    if (zzaGI == null) {
      zzaGI = Boolean.valueOf(zzaH(paramContext));
    }
    return zzaGI.booleanValue();
  }
  
  private static boolean zzaH(Context paramContext)
  {
    return false;
  }
  
  public static zzg zzyr()
  {
    return zzaGH;
  }
  
  public void zza(Context paramContext, String paramString1, int paramInt1, String paramString2, String paramString3, String paramString4, int paramInt2, List<String> paramList)
  {
    zza(paramContext, paramString1, paramInt1, paramString2, paramString3, paramString4, paramInt2, paramList, 0L);
  }
  
  public void zza(Context paramContext, String paramString1, int paramInt1, String paramString2, String paramString3, String paramString4, int paramInt2, List<String> paramList, long paramLong)
  {
    if (!zzaG(paramContext)) {}
    long l;
    do
    {
      return;
      if (TextUtils.isEmpty(paramString1))
      {
        paramString2 = TAG;
        paramContext = String.valueOf(paramString1);
        if (paramContext.length() != 0) {}
        for (paramContext = "missing wakeLock key. ".concat(paramContext);; paramContext = new String("missing wakeLock key. "))
        {
          Log.e(paramString2, paramContext);
          return;
        }
      }
      l = System.currentTimeMillis();
    } while ((7 != paramInt1) && (8 != paramInt1) && (10 != paramInt1) && (11 != paramInt1));
    paramString1 = new WakeLockEvent(l, paramInt1, paramString2, paramInt2, zze.zzz(paramList), paramString1, SystemClock.elapsedRealtime(), zzj.zzaM(paramContext), paramString3, zze.zzdB(paramContext.getPackageName()), zzj.zzaN(paramContext), paramLong, paramString4);
    try
    {
      paramContext.startService(new Intent().setComponent(zzc.zzaGj).putExtra("com.google.android.gms.common.stats.EXTRA_LOG_EVENT", paramString1));
      return;
    }
    catch (Exception paramContext)
    {
      Log.wtf(TAG, paramContext);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/stats/zzg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */