package com.google.android.gms.maps;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbe;
import com.google.android.gms.common.internal.zzbg;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

public final class StreetViewPanoramaOptions
  extends com.google.android.gms.common.internal.safeparcel.zza
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<StreetViewPanoramaOptions> CREATOR = new zzah();
  private StreetViewPanoramaCamera zzbmL;
  private String zzbmM;
  private LatLng zzbmN;
  private Integer zzbmO;
  private Boolean zzbmP = Boolean.valueOf(true);
  private Boolean zzbmQ = Boolean.valueOf(true);
  private Boolean zzbmR = Boolean.valueOf(true);
  private Boolean zzbma;
  private Boolean zzbmg = Boolean.valueOf(true);
  
  public StreetViewPanoramaOptions() {}
  
  StreetViewPanoramaOptions(StreetViewPanoramaCamera paramStreetViewPanoramaCamera, String paramString, LatLng paramLatLng, Integer paramInteger, byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4, byte paramByte5)
  {
    this.zzbmL = paramStreetViewPanoramaCamera;
    this.zzbmN = paramLatLng;
    this.zzbmO = paramInteger;
    this.zzbmM = paramString;
    this.zzbmP = com.google.android.gms.maps.internal.zza.zza(paramByte1);
    this.zzbmg = com.google.android.gms.maps.internal.zza.zza(paramByte2);
    this.zzbmQ = com.google.android.gms.maps.internal.zza.zza(paramByte3);
    this.zzbmR = com.google.android.gms.maps.internal.zza.zza(paramByte4);
    this.zzbma = com.google.android.gms.maps.internal.zza.zza(paramByte5);
  }
  
  public final Boolean getPanningGesturesEnabled()
  {
    return this.zzbmQ;
  }
  
  public final String getPanoramaId()
  {
    return this.zzbmM;
  }
  
  public final LatLng getPosition()
  {
    return this.zzbmN;
  }
  
  public final Integer getRadius()
  {
    return this.zzbmO;
  }
  
  public final Boolean getStreetNamesEnabled()
  {
    return this.zzbmR;
  }
  
  public final StreetViewPanoramaCamera getStreetViewPanoramaCamera()
  {
    return this.zzbmL;
  }
  
  public final Boolean getUseViewLifecycleInFragment()
  {
    return this.zzbma;
  }
  
  public final Boolean getUserNavigationEnabled()
  {
    return this.zzbmP;
  }
  
  public final Boolean getZoomGesturesEnabled()
  {
    return this.zzbmg;
  }
  
  public final StreetViewPanoramaOptions panningGesturesEnabled(boolean paramBoolean)
  {
    this.zzbmQ = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public final StreetViewPanoramaOptions panoramaCamera(StreetViewPanoramaCamera paramStreetViewPanoramaCamera)
  {
    this.zzbmL = paramStreetViewPanoramaCamera;
    return this;
  }
  
  public final StreetViewPanoramaOptions panoramaId(String paramString)
  {
    this.zzbmM = paramString;
    return this;
  }
  
  public final StreetViewPanoramaOptions position(LatLng paramLatLng)
  {
    this.zzbmN = paramLatLng;
    return this;
  }
  
  public final StreetViewPanoramaOptions position(LatLng paramLatLng, Integer paramInteger)
  {
    this.zzbmN = paramLatLng;
    this.zzbmO = paramInteger;
    return this;
  }
  
  public final StreetViewPanoramaOptions streetNamesEnabled(boolean paramBoolean)
  {
    this.zzbmR = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public final String toString()
  {
    return zzbe.zzt(this).zzg("PanoramaId", this.zzbmM).zzg("Position", this.zzbmN).zzg("Radius", this.zzbmO).zzg("StreetViewPanoramaCamera", this.zzbmL).zzg("UserNavigationEnabled", this.zzbmP).zzg("ZoomGesturesEnabled", this.zzbmg).zzg("PanningGesturesEnabled", this.zzbmQ).zzg("StreetNamesEnabled", this.zzbmR).zzg("UseViewLifecycleInFragment", this.zzbma).toString();
  }
  
  public final StreetViewPanoramaOptions useViewLifecycleInFragment(boolean paramBoolean)
  {
    this.zzbma = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public final StreetViewPanoramaOptions userNavigationEnabled(boolean paramBoolean)
  {
    this.zzbmP = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzd.zze(paramParcel);
    zzd.zza(paramParcel, 2, getStreetViewPanoramaCamera(), paramInt, false);
    zzd.zza(paramParcel, 3, getPanoramaId(), false);
    zzd.zza(paramParcel, 4, getPosition(), paramInt, false);
    zzd.zza(paramParcel, 5, getRadius(), false);
    zzd.zza(paramParcel, 6, com.google.android.gms.maps.internal.zza.zzb(this.zzbmP));
    zzd.zza(paramParcel, 7, com.google.android.gms.maps.internal.zza.zzb(this.zzbmg));
    zzd.zza(paramParcel, 8, com.google.android.gms.maps.internal.zza.zzb(this.zzbmQ));
    zzd.zza(paramParcel, 9, com.google.android.gms.maps.internal.zza.zzb(this.zzbmR));
    zzd.zza(paramParcel, 10, com.google.android.gms.maps.internal.zza.zzb(this.zzbma));
    zzd.zzI(paramParcel, i);
  }
  
  public final StreetViewPanoramaOptions zoomGesturesEnabled(boolean paramBoolean)
  {
    this.zzbmg = Boolean.valueOf(paramBoolean);
    return this;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/StreetViewPanoramaOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */