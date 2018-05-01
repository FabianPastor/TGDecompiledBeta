package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbe;
import com.google.android.gms.common.internal.zzbg;
import java.util.Arrays;

public class StreetViewPanoramaLocation
  extends zza
{
  public static final Parcelable.Creator<StreetViewPanoramaLocation> CREATOR = new zzo();
  public final StreetViewPanoramaLink[] links;
  public final String panoId;
  public final LatLng position;
  
  public StreetViewPanoramaLocation(StreetViewPanoramaLink[] paramArrayOfStreetViewPanoramaLink, LatLng paramLatLng, String paramString)
  {
    this.links = paramArrayOfStreetViewPanoramaLink;
    this.position = paramLatLng;
    this.panoId = paramString;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if (!(paramObject instanceof StreetViewPanoramaLocation)) {
        return false;
      }
      paramObject = (StreetViewPanoramaLocation)paramObject;
    } while ((this.panoId.equals(((StreetViewPanoramaLocation)paramObject).panoId)) && (this.position.equals(((StreetViewPanoramaLocation)paramObject).position)));
    return false;
  }
  
  public int hashCode()
  {
    return Arrays.hashCode(new Object[] { this.position, this.panoId });
  }
  
  public String toString()
  {
    return zzbe.zzt(this).zzg("panoId", this.panoId).zzg("position", this.position.toString()).toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzd.zze(paramParcel);
    zzd.zza(paramParcel, 2, this.links, paramInt, false);
    zzd.zza(paramParcel, 3, this.position, paramInt, false);
    zzd.zza(paramParcel, 4, this.panoId, false);
    zzd.zzI(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/StreetViewPanoramaLocation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */