package com.google.android.gms.maps;

import android.os.Parcel;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.maps.internal.zza;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

public final class StreetViewPanoramaOptions
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  public static final zzb CREATOR = new zzb();
  private Boolean aln;
  private Boolean alt = Boolean.valueOf(true);
  private StreetViewPanoramaCamera amc;
  private String amd;
  private LatLng ame;
  private Integer amf;
  private Boolean amg = Boolean.valueOf(true);
  private Boolean amh = Boolean.valueOf(true);
  private Boolean ami = Boolean.valueOf(true);
  private final int mVersionCode;
  
  public StreetViewPanoramaOptions()
  {
    this.mVersionCode = 1;
  }
  
  StreetViewPanoramaOptions(int paramInt, StreetViewPanoramaCamera paramStreetViewPanoramaCamera, String paramString, LatLng paramLatLng, Integer paramInteger, byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4, byte paramByte5)
  {
    this.mVersionCode = paramInt;
    this.amc = paramStreetViewPanoramaCamera;
    this.ame = paramLatLng;
    this.amf = paramInteger;
    this.amd = paramString;
    this.amg = zza.zza(paramByte1);
    this.alt = zza.zza(paramByte2);
    this.amh = zza.zza(paramByte3);
    this.ami = zza.zza(paramByte4);
    this.aln = zza.zza(paramByte5);
  }
  
  public Boolean getPanningGesturesEnabled()
  {
    return this.amh;
  }
  
  public String getPanoramaId()
  {
    return this.amd;
  }
  
  public LatLng getPosition()
  {
    return this.ame;
  }
  
  public Integer getRadius()
  {
    return this.amf;
  }
  
  public Boolean getStreetNamesEnabled()
  {
    return this.ami;
  }
  
  public StreetViewPanoramaCamera getStreetViewPanoramaCamera()
  {
    return this.amc;
  }
  
  public Boolean getUseViewLifecycleInFragment()
  {
    return this.aln;
  }
  
  public Boolean getUserNavigationEnabled()
  {
    return this.amg;
  }
  
  int getVersionCode()
  {
    return this.mVersionCode;
  }
  
  public Boolean getZoomGesturesEnabled()
  {
    return this.alt;
  }
  
  public StreetViewPanoramaOptions panningGesturesEnabled(boolean paramBoolean)
  {
    this.amh = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public StreetViewPanoramaOptions panoramaCamera(StreetViewPanoramaCamera paramStreetViewPanoramaCamera)
  {
    this.amc = paramStreetViewPanoramaCamera;
    return this;
  }
  
  public StreetViewPanoramaOptions panoramaId(String paramString)
  {
    this.amd = paramString;
    return this;
  }
  
  public StreetViewPanoramaOptions position(LatLng paramLatLng)
  {
    this.ame = paramLatLng;
    return this;
  }
  
  public StreetViewPanoramaOptions position(LatLng paramLatLng, Integer paramInteger)
  {
    this.ame = paramLatLng;
    this.amf = paramInteger;
    return this;
  }
  
  public StreetViewPanoramaOptions streetNamesEnabled(boolean paramBoolean)
  {
    this.ami = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public StreetViewPanoramaOptions useViewLifecycleInFragment(boolean paramBoolean)
  {
    this.aln = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public StreetViewPanoramaOptions userNavigationEnabled(boolean paramBoolean)
  {
    this.amg = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzb.zza(this, paramParcel, paramInt);
  }
  
  public StreetViewPanoramaOptions zoomGesturesEnabled(boolean paramBoolean)
  {
    this.alt = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  byte zzbrk()
  {
    return zza.zze(this.aln);
  }
  
  byte zzbro()
  {
    return zza.zze(this.alt);
  }
  
  byte zzbry()
  {
    return zza.zze(this.amg);
  }
  
  byte zzbrz()
  {
    return zza.zze(this.amh);
  }
  
  byte zzbsa()
  {
    return zza.zze(this.ami);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/StreetViewPanoramaOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */