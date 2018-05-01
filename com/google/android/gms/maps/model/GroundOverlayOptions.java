package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.zza;

public final class GroundOverlayOptions
  extends zza
{
  public static final Parcelable.Creator<GroundOverlayOptions> CREATOR = new zzd();
  public static final float NO_DIMENSION = -1.0F;
  private LatLngBounds zzblq;
  private float zzbnf;
  private float zzbnk;
  private boolean zzbnl = true;
  private boolean zzbnm = false;
  @NonNull
  private BitmapDescriptor zzbnp;
  private LatLng zzbnq;
  private float zzbnr;
  private float zzbns;
  private float zzbnt = 0.0F;
  private float zzbnu = 0.5F;
  private float zzbnv = 0.5F;
  
  public GroundOverlayOptions() {}
  
  GroundOverlayOptions(IBinder paramIBinder, LatLng paramLatLng, float paramFloat1, float paramFloat2, LatLngBounds paramLatLngBounds, float paramFloat3, float paramFloat4, boolean paramBoolean1, float paramFloat5, float paramFloat6, float paramFloat7, boolean paramBoolean2)
  {
    this.zzbnp = new BitmapDescriptor(IObjectWrapper.zza.zzM(paramIBinder));
    this.zzbnq = paramLatLng;
    this.zzbnr = paramFloat1;
    this.zzbns = paramFloat2;
    this.zzblq = paramLatLngBounds;
    this.zzbnf = paramFloat3;
    this.zzbnk = paramFloat4;
    this.zzbnl = paramBoolean1;
    this.zzbnt = paramFloat5;
    this.zzbnu = paramFloat6;
    this.zzbnv = paramFloat7;
    this.zzbnm = paramBoolean2;
  }
  
  private final GroundOverlayOptions zza(LatLng paramLatLng, float paramFloat1, float paramFloat2)
  {
    this.zzbnq = paramLatLng;
    this.zzbnr = paramFloat1;
    this.zzbns = paramFloat2;
    return this;
  }
  
  public final GroundOverlayOptions anchor(float paramFloat1, float paramFloat2)
  {
    this.zzbnu = paramFloat1;
    this.zzbnv = paramFloat2;
    return this;
  }
  
  public final GroundOverlayOptions bearing(float paramFloat)
  {
    this.zzbnf = ((paramFloat % 360.0F + 360.0F) % 360.0F);
    return this;
  }
  
  public final GroundOverlayOptions clickable(boolean paramBoolean)
  {
    this.zzbnm = paramBoolean;
    return this;
  }
  
  public final float getAnchorU()
  {
    return this.zzbnu;
  }
  
  public final float getAnchorV()
  {
    return this.zzbnv;
  }
  
  public final float getBearing()
  {
    return this.zzbnf;
  }
  
  public final LatLngBounds getBounds()
  {
    return this.zzblq;
  }
  
  public final float getHeight()
  {
    return this.zzbns;
  }
  
  public final BitmapDescriptor getImage()
  {
    return this.zzbnp;
  }
  
  public final LatLng getLocation()
  {
    return this.zzbnq;
  }
  
  public final float getTransparency()
  {
    return this.zzbnt;
  }
  
  public final float getWidth()
  {
    return this.zzbnr;
  }
  
  public final float getZIndex()
  {
    return this.zzbnk;
  }
  
  public final GroundOverlayOptions image(@NonNull BitmapDescriptor paramBitmapDescriptor)
  {
    zzbo.zzb(paramBitmapDescriptor, "imageDescriptor must not be null");
    this.zzbnp = paramBitmapDescriptor;
    return this;
  }
  
  public final boolean isClickable()
  {
    return this.zzbnm;
  }
  
  public final boolean isVisible()
  {
    return this.zzbnl;
  }
  
  public final GroundOverlayOptions position(LatLng paramLatLng, float paramFloat)
  {
    boolean bool2 = true;
    if (this.zzblq == null)
    {
      bool1 = true;
      zzbo.zza(bool1, "Position has already been set using positionFromBounds");
      if (paramLatLng == null) {
        break label59;
      }
      bool1 = true;
      label24:
      zzbo.zzb(bool1, "Location must be specified");
      if (paramFloat < 0.0F) {
        break label64;
      }
    }
    label59:
    label64:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      zzbo.zzb(bool1, "Width must be non-negative");
      return zza(paramLatLng, paramFloat, -1.0F);
      bool1 = false;
      break;
      bool1 = false;
      break label24;
    }
  }
  
  public final GroundOverlayOptions position(LatLng paramLatLng, float paramFloat1, float paramFloat2)
  {
    boolean bool2 = true;
    if (this.zzblq == null)
    {
      bool1 = true;
      zzbo.zza(bool1, "Position has already been set using positionFromBounds");
      if (paramLatLng == null) {
        break label81;
      }
      bool1 = true;
      label27:
      zzbo.zzb(bool1, "Location must be specified");
      if (paramFloat1 < 0.0F) {
        break label87;
      }
      bool1 = true;
      label43:
      zzbo.zzb(bool1, "Width must be non-negative");
      if (paramFloat2 < 0.0F) {
        break label93;
      }
    }
    label81:
    label87:
    label93:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      zzbo.zzb(bool1, "Height must be non-negative");
      return zza(paramLatLng, paramFloat1, paramFloat2);
      bool1 = false;
      break;
      bool1 = false;
      break label27;
      bool1 = false;
      break label43;
    }
  }
  
  public final GroundOverlayOptions positionFromBounds(LatLngBounds paramLatLngBounds)
  {
    if (this.zzbnq == null) {}
    for (boolean bool = true;; bool = false)
    {
      String str = String.valueOf(this.zzbnq);
      zzbo.zza(bool, String.valueOf(str).length() + 46 + "Position has already been set using position: " + str);
      this.zzblq = paramLatLngBounds;
      return this;
    }
  }
  
  public final GroundOverlayOptions transparency(float paramFloat)
  {
    if ((paramFloat >= 0.0F) && (paramFloat <= 1.0F)) {}
    for (boolean bool = true;; bool = false)
    {
      zzbo.zzb(bool, "Transparency must be in the range [0..1]");
      this.zzbnt = paramFloat;
      return this;
    }
  }
  
  public final GroundOverlayOptions visible(boolean paramBoolean)
  {
    this.zzbnl = paramBoolean;
    return this;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = com.google.android.gms.common.internal.safeparcel.zzd.zze(paramParcel);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 2, this.zzbnp.zzwe().asBinder(), false);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 3, getLocation(), paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 4, getWidth());
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 5, getHeight());
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 6, getBounds(), paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 7, getBearing());
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 8, getZIndex());
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 9, isVisible());
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 10, getTransparency());
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 11, getAnchorU());
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 12, getAnchorV());
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 13, isClickable());
    com.google.android.gms.common.internal.safeparcel.zzd.zzI(paramParcel, i);
  }
  
  public final GroundOverlayOptions zIndex(float paramFloat)
  {
    this.zzbnk = paramFloat;
    return this;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/GroundOverlayOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */