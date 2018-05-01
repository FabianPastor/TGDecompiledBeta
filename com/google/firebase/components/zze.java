package com.google.firebase.components;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

final class zze
  implements zzf
{
  private static Bundle zzd(Context paramContext)
  {
    localObject = null;
    for (;;)
    {
      try
      {
        localPackageManager = paramContext.getPackageManager();
        if (localPackageManager != null) {
          continue;
        }
        Log.w("ComponentDiscovery", "Context has no PackageManager.");
        paramContext = (Context)localObject;
      }
      catch (PackageManager.NameNotFoundException paramContext)
      {
        PackageManager localPackageManager;
        ComponentName localComponentName;
        Log.w("ComponentDiscovery", "Application info not found.");
        paramContext = (Context)localObject;
        continue;
        paramContext = paramContext.metaData;
        continue;
      }
      return paramContext;
      localComponentName = new android/content/ComponentName;
      localComponentName.<init>(paramContext, ComponentDiscoveryService.class);
      paramContext = localPackageManager.getServiceInfo(localComponentName, 128);
      if (paramContext != null) {
        continue;
      }
      Log.w("ComponentDiscovery", "ComponentDiscoveryService has no service info.");
      paramContext = (Context)localObject;
    }
  }
  
  public final List<String> zzc(Context paramContext)
  {
    Bundle localBundle = zzd(paramContext);
    if (localBundle == null)
    {
      Log.w("ComponentDiscovery", "Could not retrieve metadata, returning empty list of registrars.");
      paramContext = Collections.emptyList();
    }
    for (;;)
    {
      return paramContext;
      paramContext = new ArrayList();
      Iterator localIterator = localBundle.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if (("com.google.firebase.components.ComponentRegistrar".equals(localBundle.get(str))) && (str.startsWith("com.google.firebase.components:"))) {
          paramContext.add(str.substring(31));
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/components/zze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */