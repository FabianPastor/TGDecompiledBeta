package com.google.android.gms.common.stats;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.util.zzj;
import com.google.android.gms.internal.zzrs;
import java.util.List;

public class zzh
{
  private static zzh EH = new zzh();
  private static Boolean EI;
  private static String TAG = "WakeLockTracker";
  
  public static zzh zzaxf()
  {
    return EH;
  }
  
  private static boolean zzcj(Context paramContext)
  {
    if (EI == null) {
      EI = Boolean.valueOf(zzck(paramContext));
    }
    return EI.booleanValue();
  }
  
  private static boolean zzck(Context paramContext)
  {
    boolean bool = false;
    try
    {
      if (com.google.android.gms.common.util.zzd.zzact())
      {
        int i = ((Integer)zzc.zzb.Eh.get()).intValue();
        int j = zzd.LOG_LEVEL_OFF;
        if (i == j) {
          break label34;
        }
      }
      label34:
      for (bool = true;; bool = false) {
        return bool;
      }
      return false;
    }
    catch (SecurityException paramContext) {}
  }
  
  public void zza(Context paramContext, String paramString1, int paramInt1, String paramString2, String paramString3, String paramString4, int paramInt2, List<String> paramList)
  {
    zza(paramContext, paramString1, paramInt1, paramString2, paramString3, paramString4, paramInt2, paramList, 0L);
  }
  
  public void zza(Context paramContext, String paramString1, int paramInt1, String paramString2, String paramString3, String paramString4, int paramInt2, List<String> paramList, long paramLong)
  {
    if (!zzcj(paramContext)) {}
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
    paramString1 = new WakeLockEvent(l, paramInt1, paramString2, paramInt2, zzf.zzz(paramList), paramString1, SystemClock.elapsedRealtime(), zzj.zzcn(paramContext), paramString3, zzf.zzih(paramContext.getPackageName()), zzj.zzco(paramContext), paramLong, paramString4);
    try
    {
      paramContext.startService(new Intent().setComponent(zzd.En).putExtra("com.google.android.gms.common.stats.EXTRA_LOG_EVENT", paramString1));
      return;
    }
    catch (Exception paramContext)
    {
      Log.wtf(TAG, paramContext);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/stats/zzh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */