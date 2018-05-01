package com.google.firebase.components;

import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class zzc
{
  private final Context mContext;
  private final zzf zzag;
  
  public zzc(Context paramContext)
  {
    this(paramContext, new zze(null));
  }
  
  private zzc(Context paramContext, zzf paramzzf)
  {
    this.mContext = paramContext;
    this.zzag = paramzzf;
  }
  
  private static List<ComponentRegistrar> zza(List<String> paramList)
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      paramList = (String)localIterator.next();
      try
      {
        Class localClass = Class.forName(paramList);
        if (!ComponentRegistrar.class.isAssignableFrom(localClass)) {
          Log.w("ComponentDiscovery", String.format("Class %s is not an instance of %s", new Object[] { paramList, "com.google.firebase.components.ComponentRegistrar" }));
        }
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        Log.w("ComponentDiscovery", String.format("Class %s is not an found.", new Object[] { paramList }), localClassNotFoundException);
        continue;
        localArrayList.add((ComponentRegistrar)localClassNotFoundException.newInstance());
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        Log.w("ComponentDiscovery", String.format("Could not instantiate %s.", new Object[] { paramList }), localIllegalAccessException);
      }
      catch (InstantiationException localInstantiationException)
      {
        Log.w("ComponentDiscovery", String.format("Could not instantiate %s.", new Object[] { paramList }), localInstantiationException);
      }
    }
    return localArrayList;
  }
  
  public final List<ComponentRegistrar> zzj()
  {
    return zza(this.zzag.zzc(this.mContext));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/components/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */