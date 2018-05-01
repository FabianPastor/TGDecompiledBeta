package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;

public final class fa
  extends zzed
  implements ez
{
  fa(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetectorCreator");
  }
  
  public final ex zza(IObjectWrapper paramIObjectWrapper, eu parameu)
    throws RemoteException
  {
    Object localObject = zzZ();
    zzef.zza((Parcel)localObject, paramIObjectWrapper);
    zzef.zza((Parcel)localObject, parameu);
    parameu = zza(1, (Parcel)localObject);
    paramIObjectWrapper = parameu.readStrongBinder();
    if (paramIObjectWrapper == null) {
      paramIObjectWrapper = null;
    }
    for (;;)
    {
      parameu.recycle();
      return paramIObjectWrapper;
      localObject = paramIObjectWrapper.queryLocalInterface("com.google.android.gms.vision.barcode.internal.client.INativeBarcodeDetector");
      if ((localObject instanceof ex)) {
        paramIObjectWrapper = (ex)localObject;
      } else {
        paramIObjectWrapper = new ey(paramIObjectWrapper);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/fa.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */