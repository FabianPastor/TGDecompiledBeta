package com.google.android.gms.maps.model.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.zzed;
import com.google.android.gms.internal.zzef;
import com.google.android.gms.maps.model.Tile;

public final class zzab
  extends zzed
  implements zzz
{
  zzab(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.maps.model.internal.ITileProviderDelegate");
  }
  
  public final Tile getTile(int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeInt(paramInt1);
    localParcel.writeInt(paramInt2);
    localParcel.writeInt(paramInt3);
    localParcel = zza(1, localParcel);
    Tile localTile = (Tile)zzef.zza(localParcel, Tile.CREATOR);
    localParcel.recycle();
    return localTile;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/internal/zzab.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */