package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;

public final class fj
  extends zzed
  implements fi
{
  fj(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.vision.text.internal.client.INativeTextRecognizerCreator");
  }
  
  public final fg zza(IObjectWrapper paramIObjectWrapper, fr paramfr)
    throws RemoteException
  {
    Object localObject = zzZ();
    zzef.zza((Parcel)localObject, paramIObjectWrapper);
    zzef.zza((Parcel)localObject, paramfr);
    paramfr = zza(1, (Parcel)localObject);
    paramIObjectWrapper = paramfr.readStrongBinder();
    if (paramIObjectWrapper == null) {
      paramIObjectWrapper = null;
    }
    for (;;)
    {
      paramfr.recycle();
      return paramIObjectWrapper;
      localObject = paramIObjectWrapper.queryLocalInterface("com.google.android.gms.vision.text.internal.client.INativeTextRecognizer");
      if ((localObject instanceof fg)) {
        paramIObjectWrapper = (fg)localObject;
      } else {
        paramIObjectWrapper = new fh(paramIObjectWrapper);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/fj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */