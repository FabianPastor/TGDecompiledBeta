package com.google.android.gms.wearable.internal;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

final class zzer<T>
{
  private final Map<T, zzhk<T>> zzeb = new HashMap();
  
  public final void zza(IBinder paramIBinder)
  {
    Map localMap = this.zzeb;
    if (paramIBinder == null) {
      paramIBinder = null;
    }
    for (;;)
    {
      Object localObject1;
      try
      {
        localObject1 = new com/google/android/gms/wearable/internal/zzgz;
        ((zzgz)localObject1).<init>();
        Iterator localIterator = this.zzeb.entrySet().iterator();
        if (!localIterator.hasNext()) {
          break;
        }
        Object localObject2 = (Map.Entry)localIterator.next();
        Object localObject3 = (zzhk)((Map.Entry)localObject2).getValue();
        try
        {
          Object localObject4 = new com/google/android/gms/wearable/internal/zzd;
          ((zzd)localObject4).<init>((zzhk)localObject3);
          paramIBinder.zza((zzek)localObject1, (zzd)localObject4);
          if (!Log.isLoggable("WearableClient", 3)) {
            continue;
          }
          String str1 = String.valueOf(((Map.Entry)localObject2).getKey());
          String str2 = String.valueOf(localObject3);
          i = String.valueOf(str1).length();
          j = String.valueOf(str2).length();
          localObject4 = new java/lang/StringBuilder;
          ((StringBuilder)localObject4).<init>(i + 27 + j);
          Log.d("WearableClient", "onPostInitHandler: added: " + str1 + "/" + str2);
        }
        catch (RemoteException localRemoteException)
        {
          localObject2 = String.valueOf(((Map.Entry)localObject2).getKey());
          localObject3 = String.valueOf(localObject3);
          int j = String.valueOf(localObject2).length();
          int i = String.valueOf(localObject3).length();
          StringBuilder localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>(j + 32 + i);
          Log.w("WearableClient", "onPostInitHandler: Didn't add: " + (String)localObject2 + "/" + (String)localObject3);
        }
        continue;
        localObject1 = paramIBinder.queryLocalInterface("com.google.android.gms.wearable.internal.IWearableService");
      }
      finally {}
      if ((localObject1 instanceof zzep)) {
        paramIBinder = (zzep)localObject1;
      } else {
        paramIBinder = new zzeq(paramIBinder);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */