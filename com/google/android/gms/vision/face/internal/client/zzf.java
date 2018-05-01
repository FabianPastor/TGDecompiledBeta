package com.google.android.gms.vision.face.internal.client;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.internal.vision.zza;
import com.google.android.gms.internal.vision.zzb;
import com.google.android.gms.internal.vision.zzk;

public final class zzf
  extends zza
  implements zze
{
  zzf(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.vision.face.internal.client.INativeFaceDetector");
  }
  
  public final FaceParcel[] zzc(IObjectWrapper paramIObjectWrapper, zzk paramzzk)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzb.zza(localParcel, paramIObjectWrapper);
    zzb.zza(localParcel, paramzzk);
    paramIObjectWrapper = transactAndReadException(1, localParcel);
    paramzzk = (FaceParcel[])paramIObjectWrapper.createTypedArray(FaceParcel.CREATOR);
    paramIObjectWrapper.recycle();
    return paramzzk;
  }
  
  public final void zzf()
    throws RemoteException
  {
    transactAndReadExceptionReturnVoid(3, obtainAndWriteInterfaceToken());
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/face/internal/client/zzf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */