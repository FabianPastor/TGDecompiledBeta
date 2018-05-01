package com.google.android.gms.security;

import android.content.Context;
import android.os.AsyncTask;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

final class zza
  extends AsyncTask<Void, Void, Integer>
{
  zza(Context paramContext, ProviderInstaller.ProviderInstallListener paramProviderInstallListener) {}
  
  private final Integer zzb(Void... paramVarArgs)
  {
    try
    {
      ProviderInstaller.installIfNeeded(this.zztF);
      return Integer.valueOf(0);
    }
    catch (GooglePlayServicesRepairableException paramVarArgs)
    {
      return Integer.valueOf(paramVarArgs.getConnectionStatusCode());
    }
    catch (GooglePlayServicesNotAvailableException paramVarArgs) {}
    return Integer.valueOf(paramVarArgs.errorCode);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/security/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */