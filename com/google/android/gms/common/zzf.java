package com.google.android.gms.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.util.Log;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzt;
import com.google.android.gms.internal.zzsh;
import com.google.android.gms.internal.zzsi;
import java.util.Iterator;
import java.util.Set;

public class zzf
{
  private static zzf vf;
  private final Context mContext;
  
  private zzf(Context paramContext)
  {
    this.mContext = paramContext.getApplicationContext();
  }
  
  private boolean zzb(PackageInfo paramPackageInfo, boolean paramBoolean)
  {
    if (paramPackageInfo.signatures.length != 1)
    {
      Log.w("GoogleSignatureVerifier", "Package has more than one signature.");
      return false;
    }
    zzd.zzb localzzb = new zzd.zzb(paramPackageInfo.signatures[0].toByteArray());
    if (paramBoolean) {}
    for (paramPackageInfo = zzd.zzapf();; paramPackageInfo = zzd.zzapg())
    {
      paramPackageInfo = paramPackageInfo.iterator();
      do
      {
        if (!paramPackageInfo.hasNext()) {
          break;
        }
      } while (!localzzb.equals((zzt)paramPackageInfo.next()));
      return true;
    }
    return false;
  }
  
  public static zzf zzbz(Context paramContext)
  {
    zzac.zzy(paramContext);
    try
    {
      if (vf == null)
      {
        zzd.zzbr(paramContext);
        vf = new zzf(paramContext);
      }
      return vf;
    }
    finally {}
  }
  
  zzd.zza zza(PackageInfo paramPackageInfo, zzd.zza... paramVarArgs)
  {
    int i = 0;
    if (paramPackageInfo.signatures == null) {
      return null;
    }
    if (paramPackageInfo.signatures.length != 1)
    {
      Log.w("GoogleSignatureVerifier", "Package has more than one signature.");
      return null;
    }
    paramPackageInfo = new zzd.zzb(paramPackageInfo.signatures[0].toByteArray());
    while (i < paramVarArgs.length)
    {
      if (paramVarArgs[i].equals(paramPackageInfo)) {
        return paramVarArgs[i];
      }
      i += 1;
    }
    return null;
  }
  
  public boolean zza(PackageInfo paramPackageInfo, boolean paramBoolean)
  {
    if ((paramPackageInfo != null) && (paramPackageInfo.signatures != null))
    {
      if (paramBoolean) {}
      for (paramPackageInfo = zza(paramPackageInfo, zzd.zzd.uW); paramPackageInfo != null; paramPackageInfo = zza(paramPackageInfo, new zzd.zza[] { zzd.zzd.uW[0] })) {
        return true;
      }
    }
    return false;
  }
  
  public boolean zza(PackageManager paramPackageManager, PackageInfo paramPackageInfo)
  {
    boolean bool1 = false;
    if (paramPackageInfo == null) {}
    boolean bool2;
    do
    {
      do
      {
        return bool1;
        if (zze.zzbv(this.mContext)) {
          return zzb(paramPackageInfo, true);
        }
        bool2 = zzb(paramPackageInfo, false);
        bool1 = bool2;
      } while (bool2);
      bool1 = bool2;
    } while (!zzb(paramPackageInfo, true));
    Log.w("GoogleSignatureVerifier", "Test-keys aren't accepted on this build.");
    return bool2;
  }
  
  public boolean zzb(PackageManager paramPackageManager, PackageInfo paramPackageInfo)
  {
    if (paramPackageInfo == null) {}
    do
    {
      return false;
      if (zza(paramPackageInfo, false)) {
        return true;
      }
    } while (!zza(paramPackageInfo, true));
    if (zze.zzbv(this.mContext)) {
      return true;
    }
    Log.w("GoogleSignatureVerifier", "Test-keys aren't accepted on this build.");
    return false;
  }
  
  public boolean zzb(PackageManager paramPackageManager, String paramString)
  {
    try
    {
      paramString = zzsi.zzcr(this.mContext).getPackageInfo(paramString, 64);
      return zza(paramPackageManager, paramString);
    }
    catch (PackageManager.NameNotFoundException paramPackageManager) {}
    return false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/zzf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */