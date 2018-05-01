package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;

public final class fh
  extends zzed
  implements fg
{
  fh(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
  }
  
  public final void zzDS()
    throws RemoteException
  {
    zzb(2, zzZ());
  }
  
  public final fk[] zza(IObjectWrapper paramIObjectWrapper, fc paramfc, fm paramfm)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramIObjectWrapper);
    zzef.zza(localParcel, paramfc);
    zzef.zza(localParcel, paramfm);
    paramIObjectWrapper = zza(3, localParcel);
    paramfc = (fk[])paramIObjectWrapper.createTypedArray(fk.CREATOR);
    paramIObjectWrapper.recycle();
    return paramfc;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/fh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */