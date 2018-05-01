package com.google.android.gms.security;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.zze;
import com.google.android.gms.common.zzo;
import java.lang.reflect.Method;

public class ProviderInstaller
{
  public static final String PROVIDER_NAME = "GmsCore_OpenSSL";
  private static final zze zzbCG = ;
  private static Method zzbCH = null;
  private static final Object zzuF = new Object();
  
  public static void installIfNeeded(Context paramContext)
    throws GooglePlayServicesRepairableException, GooglePlayServicesNotAvailableException
  {
    zzbo.zzb(paramContext, "Context must not be null");
    zze.zzas(paramContext);
    paramContext = zzo.getRemoteContext(paramContext);
    if (paramContext == null)
    {
      Log.e("ProviderInstaller", "Failed to get remote context");
      throw new GooglePlayServicesNotAvailableException(8);
    }
    for (;;)
    {
      synchronized (zzuF)
      {
        try
        {
          if (zzbCH == null) {
            zzbCH = paramContext.getClassLoader().loadClass("com.google.android.gms.common.security.ProviderInstallerImpl").getMethod("insertProvider", new Class[] { Context.class });
          }
          zzbCH.invoke(null, new Object[] { paramContext });
          return;
        }
        catch (Exception paramContext)
        {
          paramContext = String.valueOf(paramContext.getMessage());
          if (paramContext.length() == 0) {
            break label140;
          }
        }
        paramContext = "Failed to install provider: ".concat(paramContext);
        Log.e("ProviderInstaller", paramContext);
        throw new GooglePlayServicesNotAvailableException(8);
      }
      label140:
      paramContext = new String("Failed to install provider: ");
    }
  }
  
  public static void installIfNeededAsync(Context paramContext, ProviderInstallListener paramProviderInstallListener)
  {
    zzbo.zzb(paramContext, "Context must not be null");
    zzbo.zzb(paramProviderInstallListener, "Listener must not be null");
    zzbo.zzcz("Must be called on the UI thread");
    new zza(paramContext, paramProviderInstallListener).execute(new Void[0]);
  }
  
  public static abstract interface ProviderInstallListener
  {
    public abstract void onProviderInstallFailed(int paramInt, Intent paramIntent);
    
    public abstract void onProviderInstalled();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/security/ProviderInstaller.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */