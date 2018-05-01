package com.google.android.gms.maps;

import android.graphics.Bitmap;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.zzn;
import com.google.android.gms.maps.internal.zzbr;

final class zzq
  extends zzbr
{
  zzq(GoogleMap paramGoogleMap, GoogleMap.SnapshotReadyCallback paramSnapshotReadyCallback) {}
  
  public final void onSnapshotReady(Bitmap paramBitmap)
    throws RemoteException
  {
    this.zzblP.onSnapshotReady(paramBitmap);
  }
  
  public final void zzG(IObjectWrapper paramIObjectWrapper)
    throws RemoteException
  {
    this.zzblP.onSnapshotReady((Bitmap)zzn.zzE(paramIObjectWrapper));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/zzq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */