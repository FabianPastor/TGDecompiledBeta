package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.Stub;

public final class MarkerOptions
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<MarkerOptions> CREATOR = new zzh();
  private float alpha = 1.0F;
  private LatLng position;
  private float zzcr;
  private boolean zzcs = true;
  private float zzda = 0.5F;
  private float zzdb = 1.0F;
  private String zzdm;
  private String zzdn;
  private BitmapDescriptor zzdo;
  private boolean zzdp;
  private boolean zzdq = false;
  private float zzdr = 0.0F;
  private float zzds = 0.5F;
  private float zzdt = 0.0F;
  
  public MarkerOptions() {}
  
  MarkerOptions(LatLng paramLatLng, String paramString1, String paramString2, IBinder paramIBinder, float paramFloat1, float paramFloat2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7)
  {
    this.position = paramLatLng;
    this.zzdm = paramString1;
    this.zzdn = paramString2;
    if (paramIBinder == null) {}
    for (this.zzdo = null;; this.zzdo = new BitmapDescriptor(IObjectWrapper.Stub.asInterface(paramIBinder)))
    {
      this.zzda = paramFloat1;
      this.zzdb = paramFloat2;
      this.zzdp = paramBoolean1;
      this.zzcs = paramBoolean2;
      this.zzdq = paramBoolean3;
      this.zzdr = paramFloat3;
      this.zzds = paramFloat4;
      this.zzdt = paramFloat5;
      this.alpha = paramFloat6;
      this.zzcr = paramFloat7;
      return;
    }
  }
  
  public final MarkerOptions anchor(float paramFloat1, float paramFloat2)
  {
    this.zzda = paramFloat1;
    this.zzdb = paramFloat2;
    return this;
  }
  
  public final float getAlpha()
  {
    return this.alpha;
  }
  
  public final float getAnchorU()
  {
    return this.zzda;
  }
  
  public final float getAnchorV()
  {
    return this.zzdb;
  }
  
  public final float getInfoWindowAnchorU()
  {
    return this.zzds;
  }
  
  public final float getInfoWindowAnchorV()
  {
    return this.zzdt;
  }
  
  public final LatLng getPosition()
  {
    return this.position;
  }
  
  public final float getRotation()
  {
    return this.zzdr;
  }
  
  public final String getSnippet()
  {
    return this.zzdn;
  }
  
  public final String getTitle()
  {
    return this.zzdm;
  }
  
  public final float getZIndex()
  {
    return this.zzcr;
  }
  
  public final MarkerOptions icon(BitmapDescriptor paramBitmapDescriptor)
  {
    this.zzdo = paramBitmapDescriptor;
    return this;
  }
  
  public final boolean isDraggable()
  {
    return this.zzdp;
  }
  
  public final boolean isFlat()
  {
    return this.zzdq;
  }
  
  public final boolean isVisible()
  {
    return this.zzcs;
  }
  
  public final MarkerOptions position(LatLng paramLatLng)
  {
    if (paramLatLng == null) {
      throw new IllegalArgumentException("latlng cannot be null - a position is required.");
    }
    this.position = paramLatLng;
    return this;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeParcelable(paramParcel, 2, getPosition(), paramInt, false);
    SafeParcelWriter.writeString(paramParcel, 3, getTitle(), false);
    SafeParcelWriter.writeString(paramParcel, 4, getSnippet(), false);
    if (this.zzdo == null) {}
    for (IBinder localIBinder = null;; localIBinder = this.zzdo.zza().asBinder())
    {
      SafeParcelWriter.writeIBinder(paramParcel, 5, localIBinder, false);
      SafeParcelWriter.writeFloat(paramParcel, 6, getAnchorU());
      SafeParcelWriter.writeFloat(paramParcel, 7, getAnchorV());
      SafeParcelWriter.writeBoolean(paramParcel, 8, isDraggable());
      SafeParcelWriter.writeBoolean(paramParcel, 9, isVisible());
      SafeParcelWriter.writeBoolean(paramParcel, 10, isFlat());
      SafeParcelWriter.writeFloat(paramParcel, 11, getRotation());
      SafeParcelWriter.writeFloat(paramParcel, 12, getInfoWindowAnchorU());
      SafeParcelWriter.writeFloat(paramParcel, 13, getInfoWindowAnchorV());
      SafeParcelWriter.writeFloat(paramParcel, 14, getAlpha());
      SafeParcelWriter.writeFloat(paramParcel, 15, getZIndex());
      SafeParcelWriter.finishObjectHeader(paramParcel, i);
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/MarkerOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */