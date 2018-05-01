package com.google.android.gms.internal.config;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.measurement.AppMeasurement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class zzn
{
  private static AppMeasurement zza(Context paramContext)
  {
    try
    {
      paramContext = AppMeasurement.getInstance(paramContext);
      return paramContext;
    }
    catch (NoClassDefFoundError paramContext)
    {
      for (;;)
      {
        paramContext = null;
      }
    }
  }
  
  public static List<zzl> zzb(Context paramContext)
  {
    Iterator localIterator = null;
    paramContext = zza(paramContext);
    Object localObject;
    if (paramContext == null)
    {
      localObject = localIterator;
      if (Log.isLoggable("FRCAnalytics", 3))
      {
        Log.d("FRCAnalytics", "Unable to get user properties: analytics library is missing.");
        localObject = localIterator;
      }
    }
    for (;;)
    {
      return (List<zzl>)localObject;
      try
      {
        paramContext = paramContext.getUserProperties(false);
        localObject = localIterator;
        if (paramContext != null)
        {
          localObject = new ArrayList();
          localIterator = paramContext.entrySet().iterator();
          while (localIterator.hasNext())
          {
            paramContext = (Map.Entry)localIterator.next();
            if (paramContext.getValue() != null) {
              ((List)localObject).add(new zzl((String)paramContext.getKey(), paramContext.getValue().toString()));
            }
          }
        }
      }
      catch (NullPointerException paramContext)
      {
        for (;;)
        {
          if (Log.isLoggable("FRCAnalytics", 3)) {
            Log.d("FRCAnalytics", "Unable to get user properties.", paramContext);
          }
          paramContext = null;
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */