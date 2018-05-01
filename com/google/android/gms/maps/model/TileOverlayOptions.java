package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.maps.model.internal.zzaa;
import com.google.android.gms.maps.model.internal.zzz;

public final class TileOverlayOptions
  extends zza
{
  public static final Parcelable.Creator<TileOverlayOptions> CREATOR = new zzt();
  private zzz zzbnY;
  private TileProvider zzbnZ;
  private float zzbnk;
  private boolean zzbnl = true;
  private float zzbnt = 0.0F;
  private boolean zzboa = true;
  
  public TileOverlayOptions() {}
  
  TileOverlayOptions(IBinder paramIBinder, boolean paramBoolean1, float paramFloat1, boolean paramBoolean2, float paramFloat2)
  {
    this.zzbnY = zzaa.zzaj(paramIBinder);
    if (this.zzbnY == null) {}
    for (paramIBinder = null;; paramIBinder = new zzr(this))
    {
      this.zzbnZ = paramIBinder;
      this.zzbnl = paramBoolean1;
      this.zzbnk = paramFloat1;
      this.zzboa = paramBoolean2;
      this.zzbnt = paramFloat2;
      return;
    }
  }
  
  public final TileOverlayOptions fadeIn(boolean paramBoolean)
  {
    this.zzboa = paramBoolean;
    return this;
  }
  
  public final boolean getFadeIn()
  {
    return this.zzboa;
  }
  
  public final TileProvider getTileProvider()
  {
    return this.zzbnZ;
  }
  
  public final float getTransparency()
  {
    return this.zzbnt;
  }
  
  public final float getZIndex()
  {
    return this.zzbnk;
  }
  
  public final boolean isVisible()
  {
    return this.zzbnl;
  }
  
  public final TileOverlayOptions tileProvider(TileProvider paramTileProvider)
  {
    this.zzbnZ = paramTileProvider;
    if (this.zzbnZ == null) {}
    for (paramTileProvider = null;; paramTileProvider = new zzs(this, paramTileProvider))
    {
      this.zzbnY = paramTileProvider;
      return this;
    }
  }
  
  public final TileOverlayOptions transparency(float paramFloat)
  {
    if ((paramFloat >= 0.0F) && (paramFloat <= 1.0F)) {}
    for (boolean bool = true;; bool = false)
    {
      zzbo.zzb(bool, "Transparency must be in the range [0..1]");
      this.zzbnt = paramFloat;
      return this;
    }
  }
  
  public final TileOverlayOptions visible(boolean paramBoolean)
  {
    this.zzbnl = paramBoolean;
    return this;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzd.zze(paramParcel);
    zzd.zza(paramParcel, 2, this.zzbnY.asBinder(), false);
    zzd.zza(paramParcel, 3, isVisible());
    zzd.zza(paramParcel, 4, getZIndex());
    zzd.zza(paramParcel, 5, getFadeIn());
    zzd.zza(paramParcel, 6, getTransparency());
    zzd.zzI(paramParcel, paramInt);
  }
  
  public final TileOverlayOptions zIndex(float paramFloat)
  {
    this.zzbnk = paramFloat;
    return this;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/TileOverlayOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */