package com.google.android.gms.maps.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.dynamic.ObjectWrapper;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.maps.model.RuntimeRemoteException;

public class zzbz
{
  private static final String TAG = zzbz.class.getSimpleName();
  @SuppressLint({"StaticFieldLeak"})
  private static Context zzcj = null;
  private static zze zzck;
  
  public static zze zza(Context paramContext)
    throws GooglePlayServicesNotAvailableException
  {
    Preconditions.checkNotNull(paramContext);
    if (zzck != null)
    {
      paramContext = zzck;
      return paramContext;
    }
    int i = GooglePlayServicesUtil.isGooglePlayServicesAvailable(paramContext, 12451000);
    switch (i)
    {
    default: 
      throw new GooglePlayServicesNotAvailableException(i);
    }
    Log.i(TAG, "Making Creator dynamically");
    Object localObject = (IBinder)zza(zzb(paramContext).getClassLoader(), "com.google.android.gms.maps.internal.CreatorImpl");
    if (localObject == null) {
      localObject = null;
    }
    for (;;)
    {
      zzck = (zze)localObject;
      try
      {
        zzck.zza(ObjectWrapper.wrap(zzb(paramContext).getResources()), GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE);
        paramContext = zzck;
      }
      catch (RemoteException paramContext)
      {
        IInterface localIInterface;
        throw new RuntimeRemoteException(paramContext);
      }
      localIInterface = ((IBinder)localObject).queryLocalInterface("com.google.android.gms.maps.internal.ICreator");
      if ((localIInterface instanceof zze)) {
        localObject = (zze)localIInterface;
      } else {
        localObject = new zzf((IBinder)localObject);
      }
    }
  }
  
  private static <T> T zza(Class<?> paramClass)
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
  
  private static <T> T zza(ClassLoader paramClassLoader, String paramString)
  {
    try
    {
      paramClassLoader = zza(((ClassLoader)Preconditions.checkNotNull(paramClassLoader)).loadClass(paramString));
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
  
  private static Context zzb(Context paramContext)
  {
    if (zzcj != null) {
      paramContext = zzcj;
    }
    for (;;)
    {
      return paramContext;
      paramContext = zzc(paramContext);
      zzcj = paramContext;
    }
  }
  
  private static Context zzc(Context paramContext)
  {
    try
    {
      Context localContext = DynamiteModule.load(paramContext, DynamiteModule.PREFER_REMOTE, "com.google.android.gms.maps_dynamite").getModuleContext();
      paramContext = localContext;
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        Log.e(TAG, "Failed to load maps module, use legacy", localThrowable);
        paramContext = GooglePlayServicesUtil.getRemoteContext(paramContext);
      }
    }
    return paramContext;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/zzbz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */