package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class PointOfInterest
  extends zza
{
  public static final Parcelable.Creator<PointOfInterest> CREATOR = new zzj();
  public final LatLng latLng;
  public final String name;
  public final String placeId;
  
  public PointOfInterest(LatLng paramLatLng, String paramString1, String paramString2)
  {
    this.latLng = paramLatLng;
    this.placeId = paramString1;
    this.name = paramString2;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzd.zze(paramParcel);
    zzd.zza(paramParcel, 2, this.latLng, paramInt, false);
    zzd.zza(paramParcel, 3, this.placeId, false);
    zzd.zza(paramParcel, 4, this.name, false);
    zzd.zzI(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/PointOfInterest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */