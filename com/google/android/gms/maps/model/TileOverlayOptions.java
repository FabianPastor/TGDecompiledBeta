package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.maps.model.internal.zzi;
import com.google.android.gms.maps.model.internal.zzi.zza;

public final class TileOverlayOptions
  extends AbstractSafeParcelable
{
  public static final zzp CREATOR = new zzp();
  private float amD;
  private boolean amE = true;
  private float amL = 0.0F;
  private zzi ank;
  private TileProvider anl;
  private boolean anm = true;
  private final int mVersionCode;
  
  public TileOverlayOptions()
  {
    this.mVersionCode = 1;
  }
  
  TileOverlayOptions(int paramInt, IBinder paramIBinder, boolean paramBoolean1, float paramFloat1, boolean paramBoolean2, float paramFloat2)
  {
    this.mVersionCode = paramInt;
    this.ank = zzi.zza.zzjk(paramIBinder);
    if (this.ank == null) {}
    for (paramIBinder = null;; paramIBinder = new TileProvider()
        {
          private final zzi ann = TileOverlayOptions.zza(TileOverlayOptions.this);
          
          public Tile getTile(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
          {
            try
            {
              Tile localTile = this.ann.getTile(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3);
              return localTile;
            }
            catch (RemoteException localRemoteException) {}
            return null;
          }
        })
    {
      this.anl = paramIBinder;
      this.amE = paramBoolean1;
      this.amD = paramFloat1;
      this.anm = paramBoolean2;
      this.amL = paramFloat2;
      return;
    }
  }
  
  public TileOverlayOptions fadeIn(boolean paramBoolean)
  {
    this.anm = paramBoolean;
    return this;
  }
  
  public boolean getFadeIn()
  {
    return this.anm;
  }
  
  public TileProvider getTileProvider()
  {
    return this.anl;
  }
  
  public float getTransparency()
  {
    return this.amL;
  }
  
  int getVersionCode()
  {
    return this.mVersionCode;
  }
  
  public float getZIndex()
  {
    return this.amD;
  }
  
  public boolean isVisible()
  {
    return this.amE;
  }
  
  public TileOverlayOptions tileProvider(final TileProvider paramTileProvider)
  {
    this.anl = paramTileProvider;
    if (this.anl == null) {}
    for (paramTileProvider = null;; paramTileProvider = new zzi.zza()
        {
          public Tile getTile(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
          {
            return paramTileProvider.getTile(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3);
          }
        })
    {
      this.ank = paramTileProvider;
      return this;
    }
  }
  
  public TileOverlayOptions transparency(float paramFloat)
  {
    if ((paramFloat >= 0.0F) && (paramFloat <= 1.0F)) {}
    for (boolean bool = true;; bool = false)
    {
      zzac.zzb(bool, "Transparency must be in the range [0..1]");
      this.amL = paramFloat;
      return this;
    }
  }
  
  public TileOverlayOptions visible(boolean paramBoolean)
  {
    this.amE = paramBoolean;
    return this;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzp.zza(this, paramParcel, paramInt);
  }
  
  public TileOverlayOptions zIndex(float paramFloat)
  {
    this.amD = paramFloat;
    return this;
  }
  
  IBinder zzbsl()
  {
    return this.ank.asBinder();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/TileOverlayOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */