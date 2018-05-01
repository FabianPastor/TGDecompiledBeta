package com.google.android.gms.maps.model;

import android.os.RemoteException;
import com.google.android.gms.maps.model.internal.zzz;

final class zzr
  implements TileProvider
{
  private final zzz zzbob = TileOverlayOptions.zza(this.zzboc);
  
  zzr(TileOverlayOptions paramTileOverlayOptions) {}
  
  public final Tile getTile(int paramInt1, int paramInt2, int paramInt3)
  {
    try
    {
      Tile localTile = this.zzbob.getTile(paramInt1, paramInt2, paramInt3);
      return localTile;
    }
    catch (RemoteException localRemoteException) {}
    return null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/zzr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */