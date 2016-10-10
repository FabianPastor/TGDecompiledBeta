package com.google.android.gms.maps.model;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public final class CircleOptions
  extends AbstractSafeParcelable
{
  public static final zzb CREATOR = new zzb();
  private LatLng amB = null;
  private double amC = 0.0D;
  private float amD = 0.0F;
  private boolean amE = true;
  private boolean amF = false;
  private int mFillColor = 0;
  private int mStrokeColor = -16777216;
  private float mStrokeWidth = 10.0F;
  private final int mVersionCode;
  
  public CircleOptions()
  {
    this.mVersionCode = 1;
  }
  
  CircleOptions(int paramInt1, LatLng paramLatLng, double paramDouble, float paramFloat1, int paramInt2, int paramInt3, float paramFloat2, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mVersionCode = paramInt1;
    this.amB = paramLatLng;
    this.amC = paramDouble;
    this.mStrokeWidth = paramFloat1;
    this.mStrokeColor = paramInt2;
    this.mFillColor = paramInt3;
    this.amD = paramFloat2;
    this.amE = paramBoolean1;
    this.amF = paramBoolean2;
  }
  
  public CircleOptions center(LatLng paramLatLng)
  {
    this.amB = paramLatLng;
    return this;
  }
  
  public CircleOptions clickable(boolean paramBoolean)
  {
    this.amF = paramBoolean;
    return this;
  }
  
  public CircleOptions fillColor(int paramInt)
  {
    this.mFillColor = paramInt;
    return this;
  }
  
  public LatLng getCenter()
  {
    return this.amB;
  }
  
  public int getFillColor()
  {
    return this.mFillColor;
  }
  
  public double getRadius()
  {
    return this.amC;
  }
  
  public int getStrokeColor()
  {
    return this.mStrokeColor;
  }
  
  public float getStrokeWidth()
  {
    return this.mStrokeWidth;
  }
  
  int getVersionCode()
  {
    return this.mVersionCode;
  }
  
  public float getZIndex()
  {
    return this.amD;
  }
  
  public boolean isClickable()
  {
    return this.amF;
  }
  
  public boolean isVisible()
  {
    return this.amE;
  }
  
  public CircleOptions radius(double paramDouble)
  {
    this.amC = paramDouble;
    return this;
  }
  
  public CircleOptions strokeColor(int paramInt)
  {
    this.mStrokeColor = paramInt;
    return this;
  }
  
  public CircleOptions strokeWidth(float paramFloat)
  {
    this.mStrokeWidth = paramFloat;
    return this;
  }
  
  public CircleOptions visible(boolean paramBoolean)
  {
    this.amE = paramBoolean;
    return this;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzb.zza(this, paramParcel, paramInt);
  }
  
  public CircleOptions zIndex(float paramFloat)
  {
    this.amD = paramFloat;
    return this;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/CircleOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */