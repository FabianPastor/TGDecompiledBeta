package com.google.android.gms.maps.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.maps.model.RuntimeRemoteException;

public class zzai
{
  private static Context amt;
  private static zzc amu;
  
  private static Context getRemoteContext(Context paramContext)
  {
    if (amt == null) {
      if (!zzbse()) {
        break label23;
      }
    }
    label23:
    for (amt = paramContext.getApplicationContext();; amt = GooglePlayServicesUtil.getRemoteContext(paramContext)) {
      return amt;
    }
  }
  
  private static <T> T zza(ClassLoader paramClassLoader, String paramString)
  {
    try
    {
      paramClassLoader = zzf(((ClassLoader)zzac.zzy(paramClassLoader)).loadClass(paramString));
      return paramClassLoader;
    }
    catch (ClassNotFoundException paramClassLoader)
    {
      paramClassLoader = String.valueOf(paramString);
      if (paramClassLoader.length() == 0) {}
    }
    for (paramClassLoader = "Unable to find dynamic class ".concat(paramClassLoader);; paramClassLoader = new String("Unable to find dynamic class ")) {
      throw new IllegalStateException(paramClassLoader);
    }
  }
  
  public static boolean zzbse()
  {
    return false;
  }
  
  private static Class<?> zzbsf()
  {
    try
    {
      Class localClass = Class.forName("com.google.android.gms.maps.internal.CreatorImpl");
      return localClass;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new RuntimeException(localClassNotFoundException);
    }
  }
  
  public static zzc zzdp(Context paramContext)
    throws GooglePlayServicesNotAvailableException
  {
    zzac.zzy(paramContext);
    if (amu != null) {
      return amu;
    }
    zzdq(paramContext);
    amu = zzdr(paramContext);
    try
    {
      amu.zzh(zze.zzac(getRemoteContext(paramContext).getResources()), GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE);
      return amu;
    }
    catch (RemoteException paramContext)
    {
      throw new RuntimeRemoteException(paramContext);
    }
  }
  
  private static void zzdq(Context paramContext)
    throws GooglePlayServicesNotAvailableException
  {
    int i = GooglePlayServicesUtil.isGooglePlayServicesAvailable(paramContext);
    switch (i)
    {
    default: 
      throw new GooglePlayServicesNotAvailableException(i);
    }
  }
  
  private static zzc zzdr(Context paramContext)
  {
    if (zzbse())
    {
      Log.i(zzai.class.getSimpleName(), "Making Creator statically");
      return (zzc)zzf(zzbsf());
    }
    Log.i(zzai.class.getSimpleName(), "Making Creator dynamically");
    return zzc.zza.zzhn((IBinder)zza(getRemoteContext(paramContext).getClassLoader(), "com.google.android.gms.maps.internal.CreatorImpl"));
  }
  
  private static <T> T zzf(Class<?> paramClass)
  {
    try
    {
      Object localObject = paramClass.newInstance();
      return (T)localObject;
    }
    catch (InstantiationException localInstantiationException)
    {
      paramClass = String.valueOf(paramClass.getName());
      if (paramClass.length() != 0) {}
      for (paramClass = "Unable to instantiate the dynamic class ".concat(paramClass);; paramClass = new String("Unable to instantiate the dynamic class ")) {
        throw new IllegalStateException(paramClass);
      }
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      paramClass = String.valueOf(paramClass.getName());
      if (paramClass.length() == 0) {}
    }
    for (paramClass = "Unable to call the default constructor of ".concat(paramClass);; paramClass = new String("Unable to call the default constructor of ")) {
      throw new IllegalStateException(paramClass);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/zzai.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */