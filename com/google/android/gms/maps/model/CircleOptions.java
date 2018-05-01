package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import java.util.List;

public final class CircleOptions
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<CircleOptions> CREATOR = new zzc();
  private int fillColor = 0;
  private int strokeColor = -16777216;
  private LatLng zzco = null;
  private double zzcp = 0.0D;
  private float zzcq = 10.0F;
  private float zzcr = 0.0F;
  private boolean zzcs = true;
  private boolean zzct = false;
  private List<PatternItem> zzcu = null;
  
  public CircleOptions() {}
  
  CircleOptions(LatLng paramLatLng, double paramDouble, float paramFloat1, int paramInt1, int paramInt2, float paramFloat2, boolean paramBoolean1, boolean paramBoolean2, List<PatternItem> paramList)
  {
    this.zzco = paramLatLng;
    this.zzcp = paramDouble;
    this.zzcq = paramFloat1;
    this.strokeColor = paramInt1;
    this.fillColor = paramInt2;
    this.zzcr = paramFloat2;
    this.zzcs = paramBoolean1;
    this.zzct = paramBoolean2;
    this.zzcu = paramList;
  }
  
  public final LatLng getCenter()
  {
    return this.zzco;
  }
  
  public final int getFillColor()
  {
    return this.fillColor;
  }
  
  public final double getRadius()
  {
    return this.zzcp;
  }
  
  public final int getStrokeColor()
  {
    return this.strokeColor;
  }
  
  public final List<PatternItem> getStrokePattern()
  {
    return this.zzcu;
  }
  
  public final float getStrokeWidth()
  {
    return this.zzcq;
  }
  
  public final float getZIndex()
  {
    return this.zzcr;
  }
  
  public final boolean isClickable()
  {
    return this.zzct;
  }
  
  public final boolean isVisible()
  {
    return this.zzcs;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeParcelable(paramParcel, 2, getCenter(), paramInt, false);
    SafeParcelWriter.writeDouble(paramParcel, 3, getRadius());
    SafeParcelWriter.writeFloat(paramParcel, 4, getStrokeWidth());
    SafeParcelWriter.writeInt(paramParcel, 5, getStrokeColor());
    SafeParcelWriter.writeInt(paramParcel, 6, getFillColor());
    SafeParcelWriter.writeFloat(paramParcel, 7, getZIndex());
    SafeParcelWriter.writeBoolean(paramParcel, 8, isVisible());
    SafeParcelWriter.writeBoolean(paramParcel, 9, isClickable());
    SafeParcelWriter.writeTypedList(paramParcel, 10, getStrokePattern(), false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/CircleOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */