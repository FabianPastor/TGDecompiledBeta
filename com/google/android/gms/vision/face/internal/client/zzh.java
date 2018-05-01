package com.google.android.gms.vision.face.internal.client;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.internal.vision.zza;
import com.google.android.gms.internal.vision.zzb;

public final class zzh
  extends zza
  implements zzg
{
  zzh(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.vision.face.internal.client.INativeFaceDetectorCreator");
  }
  
  public final zze zza(IObjectWrapper paramIObjectWrapper, zzc paramzzc)
    throws RemoteException
  {
    Object localObject = obtainAndWriteInterfaceToken();
    zzb.zza((Parcel)localObject, paramIObjectWrapper);
    zzb.zza((Parcel)localObject, paramzzc);
    paramzzc = transactAndReadException(1, (Parcel)localObject);
    localObject = paramzzc.readStrongBinder();
    if (localObject == null) {
      paramIObjectWrapper = null;
    }
    for (;;)
    {
      paramzzc.recycle();
      return paramIObjectWrapper;
      paramIObjectWrapper = ((IBinder)localObject).queryLocalInterface("com.google.android.gms.vision.face.internal.client.INativeFaceDetector");
      if ((paramIObjectWrapper instanceof zze)) {
        paramIObjectWrapper = (zze)paramIObjectWrapper;
      } else {
        paramIObjectWrapper = new zzf((IBinder)localObject);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/face/internal/client/zzh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */