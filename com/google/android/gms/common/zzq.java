package com.google.android.gms.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.util.Log;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.internal.zzbhe;
import com.google.android.gms.internal.zzbhf;

public class zzq
{
  private static zzq zzflp;
  private final Context mContext;
  
  private zzq(Context paramContext)
  {
    this.mContext = paramContext.getApplicationContext();
  }
  
  static zzh zza(PackageInfo paramPackageInfo, zzh... paramVarArgs)
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
    paramPackageInfo = new zzi(paramPackageInfo.signatures[0].toByteArray());
    while (i < paramVarArgs.length)
    {
      if (paramVarArgs[i].equals(paramPackageInfo)) {
        return paramVarArgs[i];
      }
      i += 1;
    }
    return null;
  }
  
  private static boolean zza(PackageInfo paramPackageInfo, boolean paramBoolean)
  {
    if ((paramPackageInfo != null) && (paramPackageInfo.signatures != null))
    {
      if (paramBoolean) {}
      for (paramPackageInfo = zza(paramPackageInfo, zzk.zzflf); paramPackageInfo != null; paramPackageInfo = zza(paramPackageInfo, new zzh[] { zzk.zzflf[0] })) {
        return true;
      }
    }
    return false;
  }
  
  private static boolean zzb(PackageInfo paramPackageInfo, boolean paramBoolean)
  {
    boolean bool2 = false;
    if (paramPackageInfo.signatures.length != 1)
    {
      Log.w("GoogleSignatureVerifier", "Package has more than one signature.");
      return bool2;
    }
    zzi localzzi = new zzi(paramPackageInfo.signatures[0].toByteArray());
    paramPackageInfo = paramPackageInfo.packageName;
    if (paramBoolean) {}
    for (boolean bool1 = zzg.zzb(paramPackageInfo, localzzi);; bool1 = zzg.zza(paramPackageInfo, localzzi))
    {
      bool2 = bool1;
      if (bool1) {
        break;
      }
      Log.d("GoogleSignatureVerifier", 27 + "Cert not in list. atk=" + paramBoolean);
      return bool1;
    }
  }
  
  public static zzq zzci(Context paramContext)
  {
    zzbq.checkNotNull(paramContext);
    try
    {
      if (zzflp == null)
      {
        zzg.zzcg(paramContext);
        zzflp = new zzq(paramContext);
      }
      return zzflp;
    }
    finally {}
  }
  
  private final boolean zzfy(String paramString)
  {
    try
    {
      paramString = zzbhf.zzdb(this.mContext).getPackageInfo(paramString, 64);
      if (paramString == null) {
        return false;
      }
      if (zzp.zzch(this.mContext)) {
        return zzb(paramString, true);
      }
      boolean bool = zzb(paramString, false);
      if ((!bool) && (zzb(paramString, true))) {
        Log.w("GoogleSignatureVerifier", "Test-keys aren't accepted on this build.");
      }
      return bool;
    }
    catch (PackageManager.NameNotFoundException paramString) {}
    return false;
  }
  
  public final boolean zza(PackageInfo paramPackageInfo)
  {
    if (paramPackageInfo == null) {}
    do
    {
      return false;
      if (zza(paramPackageInfo, false)) {
        return true;
      }
    } while (!zza(paramPackageInfo, true));
    if (zzp.zzch(this.mContext)) {
      return true;
    }
    Log.w("GoogleSignatureVerifier", "Test-keys aren't accepted on this build.");
    return false;
  }
  
  public final boolean zzbq(int paramInt)
  {
    String[] arrayOfString = zzbhf.zzdb(this.mContext).getPackagesForUid(paramInt);
    if ((arrayOfString == null) || (arrayOfString.length == 0)) {}
    for (;;)
    {
      return false;
      int i = arrayOfString.length;
      paramInt = 0;
      while (paramInt < i)
      {
        if (zzfy(arrayOfString[paramInt])) {
          return true;
        }
        paramInt += 1;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/zzq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */