package com.google.android.gms.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.util.Log;
import com.google.android.gms.common.internal.Preconditions;
import javax.annotation.CheckReturnValue;

@CheckReturnValue
public class GoogleSignatureVerifier
{
  private static GoogleSignatureVerifier zzbv;
  private final Context mContext;
  
  private GoogleSignatureVerifier(Context paramContext)
  {
    this.mContext = paramContext.getApplicationContext();
  }
  
  public static GoogleSignatureVerifier getInstance(Context paramContext)
  {
    Preconditions.checkNotNull(paramContext);
    try
    {
      if (zzbv == null)
      {
        GoogleCertificates.init(paramContext);
        GoogleSignatureVerifier localGoogleSignatureVerifier = new com/google/android/gms/common/GoogleSignatureVerifier;
        localGoogleSignatureVerifier.<init>(paramContext);
        zzbv = localGoogleSignatureVerifier;
      }
      return zzbv;
    }
    finally {}
  }
  
  private static GoogleCertificates.CertData zza(PackageInfo paramPackageInfo, GoogleCertificates.CertData... paramVarArgs)
  {
    int i = 0;
    if (paramPackageInfo.signatures == null) {
      paramPackageInfo = null;
    }
    for (;;)
    {
      return paramPackageInfo;
      if (paramPackageInfo.signatures.length != 1)
      {
        Log.w("GoogleSignatureVerifier", "Package has more than one signature.");
        paramPackageInfo = null;
      }
      else
      {
        paramPackageInfo = new zzb(paramPackageInfo.signatures[0].toByteArray());
        for (;;)
        {
          if (i >= paramVarArgs.length) {
            break label81;
          }
          if (paramVarArgs[i].equals(paramPackageInfo))
          {
            paramPackageInfo = paramVarArgs[i];
            break;
          }
          i++;
        }
        label81:
        paramPackageInfo = null;
      }
    }
  }
  
  public boolean isGooglePublicSignedPackage(PackageInfo paramPackageInfo)
  {
    boolean bool1 = false;
    boolean bool2;
    if (paramPackageInfo == null) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      if (isGooglePublicSignedPackage(paramPackageInfo, false))
      {
        bool2 = true;
      }
      else
      {
        bool2 = bool1;
        if (isGooglePublicSignedPackage(paramPackageInfo, true)) {
          if (GooglePlayServicesUtilLight.honorsDebugCertificates(this.mContext))
          {
            bool2 = true;
          }
          else
          {
            Log.w("GoogleSignatureVerifier", "Test-keys aren't accepted on this build.");
            bool2 = bool1;
          }
        }
      }
    }
  }
  
  public boolean isGooglePublicSignedPackage(PackageInfo paramPackageInfo, boolean paramBoolean)
  {
    boolean bool = true;
    if ((paramPackageInfo != null) && (paramPackageInfo.signatures != null)) {
      if (paramBoolean)
      {
        paramPackageInfo = zza(paramPackageInfo, zzd.zzbg);
        if (paramPackageInfo == null) {
          break label53;
        }
      }
    }
    label53:
    for (paramBoolean = bool;; paramBoolean = false)
    {
      return paramBoolean;
      paramPackageInfo = zza(paramPackageInfo, new GoogleCertificates.CertData[] { zzd.zzbg[0] });
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/GoogleSignatureVerifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */