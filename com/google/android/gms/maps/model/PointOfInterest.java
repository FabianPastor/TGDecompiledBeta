package com.google.android.gms.maps.model;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public final class PointOfInterest
  extends AbstractSafeParcelable
{
  public static final zzh CREATOR = new zzh();
  public final LatLng latLng;
  private final int mVersionCode;
  public final String name;
  public final String placeId;
  
  PointOfInterest(int paramInt, LatLng paramLatLng, String paramString1, String paramString2)
  {
    this.mVersionCode = paramInt;
    this.latLng = paramLatLng;
    this.placeId = paramString1;
    this.name = paramString2;
  }
  
  public PointOfInterest(LatLng paramLatLng, String paramString1, String paramString2)
  {
    this(1, paramLatLng, paramString1, paramString2);
  }
  
  int getVersionCode()
  {
    return this.mVersionCode;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzh.zza(this, paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/PointOfInterest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */