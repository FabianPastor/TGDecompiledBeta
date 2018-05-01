package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.vision.barcode.Barcode;

public final class ey
  extends zzed
  implements ex
{
  ey(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetector");
  }
  
  public final void zzDP()
    throws RemoteException
  {
    zzb(3, zzZ());
  }
  
  public final Barcode[] zza(IObjectWrapper paramIObjectWrapper, fc paramfc)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramIObjectWrapper);
    zzef.zza(localParcel, paramfc);
    paramIObjectWrapper = zza(1, localParcel);
    paramfc = (Barcode[])paramIObjectWrapper.createTypedArray(Barcode.CREATOR);
    paramIObjectWrapper.recycle();
    return paramfc;
  }
  
  public final Barcode[] zzb(IObjectWrapper paramIObjectWrapper, fc paramfc)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramIObjectWrapper);
    zzef.zza(localParcel, paramfc);
    paramIObjectWrapper = zza(2, localParcel);
    paramfc = (Barcode[])paramIObjectWrapper.createTypedArray(Barcode.CREATOR);
    paramIObjectWrapper.recycle();
    return paramfc;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/ey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */