package com.google.android.gms.maps;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.util.AttributeSet;
import com.google.android.gms.R.styleable;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;

public final class GoogleMapOptions
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  public static final zza CREATOR = new zza();
  private Float alA = null;
  private LatLngBounds alB = null;
  private Boolean alm;
  private Boolean aln;
  private int alo = -1;
  private CameraPosition alp;
  private Boolean alq;
  private Boolean alr;
  private Boolean als;
  private Boolean alt;
  private Boolean alu;
  private Boolean alv;
  private Boolean alw;
  private Boolean alx;
  private Boolean aly;
  private Float alz = null;
  private final int mVersionCode;
  
  public GoogleMapOptions()
  {
    this.mVersionCode = 1;
  }
  
  GoogleMapOptions(int paramInt1, byte paramByte1, byte paramByte2, int paramInt2, CameraPosition paramCameraPosition, byte paramByte3, byte paramByte4, byte paramByte5, byte paramByte6, byte paramByte7, byte paramByte8, byte paramByte9, byte paramByte10, byte paramByte11, Float paramFloat1, Float paramFloat2, LatLngBounds paramLatLngBounds)
  {
    this.mVersionCode = paramInt1;
    this.alm = com.google.android.gms.maps.internal.zza.zza(paramByte1);
    this.aln = com.google.android.gms.maps.internal.zza.zza(paramByte2);
    this.alo = paramInt2;
    this.alp = paramCameraPosition;
    this.alq = com.google.android.gms.maps.internal.zza.zza(paramByte3);
    this.alr = com.google.android.gms.maps.internal.zza.zza(paramByte4);
    this.als = com.google.android.gms.maps.internal.zza.zza(paramByte5);
    this.alt = com.google.android.gms.maps.internal.zza.zza(paramByte6);
    this.alu = com.google.android.gms.maps.internal.zza.zza(paramByte7);
    this.alv = com.google.android.gms.maps.internal.zza.zza(paramByte8);
    this.alw = com.google.android.gms.maps.internal.zza.zza(paramByte9);
    this.alx = com.google.android.gms.maps.internal.zza.zza(paramByte10);
    this.aly = com.google.android.gms.maps.internal.zza.zza(paramByte11);
    this.alz = paramFloat1;
    this.alA = paramFloat2;
    this.alB = paramLatLngBounds;
  }
  
  public static GoogleMapOptions createFromAttributes(Context paramContext, AttributeSet paramAttributeSet)
  {
    if (paramAttributeSet == null) {
      return null;
    }
    TypedArray localTypedArray = paramContext.getResources().obtainAttributes(paramAttributeSet, R.styleable.MapAttrs);
    GoogleMapOptions localGoogleMapOptions = new GoogleMapOptions();
    if (localTypedArray.hasValue(R.styleable.MapAttrs_mapType)) {
      localGoogleMapOptions.mapType(localTypedArray.getInt(R.styleable.MapAttrs_mapType, -1));
    }
    if (localTypedArray.hasValue(R.styleable.MapAttrs_zOrderOnTop)) {
      localGoogleMapOptions.zOrderOnTop(localTypedArray.getBoolean(R.styleable.MapAttrs_zOrderOnTop, false));
    }
    if (localTypedArray.hasValue(R.styleable.MapAttrs_useViewLifecycle)) {
      localGoogleMapOptions.useViewLifecycleInFragment(localTypedArray.getBoolean(R.styleable.MapAttrs_useViewLifecycle, false));
    }
    if (localTypedArray.hasValue(R.styleable.MapAttrs_uiCompass)) {
      localGoogleMapOptions.compassEnabled(localTypedArray.getBoolean(R.styleable.MapAttrs_uiCompass, true));
    }
    if (localTypedArray.hasValue(R.styleable.MapAttrs_uiRotateGestures)) {
      localGoogleMapOptions.rotateGesturesEnabled(localTypedArray.getBoolean(R.styleable.MapAttrs_uiRotateGestures, true));
    }
    if (localTypedArray.hasValue(R.styleable.MapAttrs_uiScrollGestures)) {
      localGoogleMapOptions.scrollGesturesEnabled(localTypedArray.getBoolean(R.styleable.MapAttrs_uiScrollGestures, true));
    }
    if (localTypedArray.hasValue(R.styleable.MapAttrs_uiTiltGestures)) {
      localGoogleMapOptions.tiltGesturesEnabled(localTypedArray.getBoolean(R.styleable.MapAttrs_uiTiltGestures, true));
    }
    if (localTypedArray.hasValue(R.styleable.MapAttrs_uiZoomGestures)) {
      localGoogleMapOptions.zoomGesturesEnabled(localTypedArray.getBoolean(R.styleable.MapAttrs_uiZoomGestures, true));
    }
    if (localTypedArray.hasValue(R.styleable.MapAttrs_uiZoomControls)) {
      localGoogleMapOptions.zoomControlsEnabled(localTypedArray.getBoolean(R.styleable.MapAttrs_uiZoomControls, true));
    }
    if (localTypedArray.hasValue(R.styleable.MapAttrs_liteMode)) {
      localGoogleMapOptions.liteMode(localTypedArray.getBoolean(R.styleable.MapAttrs_liteMode, false));
    }
    if (localTypedArray.hasValue(R.styleable.MapAttrs_uiMapToolbar)) {
      localGoogleMapOptions.mapToolbarEnabled(localTypedArray.getBoolean(R.styleable.MapAttrs_uiMapToolbar, true));
    }
    if (localTypedArray.hasValue(R.styleable.MapAttrs_ambientEnabled)) {
      localGoogleMapOptions.ambientEnabled(localTypedArray.getBoolean(R.styleable.MapAttrs_ambientEnabled, false));
    }
    if (localTypedArray.hasValue(R.styleable.MapAttrs_cameraMinZoomPreference)) {
      localGoogleMapOptions.minZoomPreference(localTypedArray.getFloat(R.styleable.MapAttrs_cameraMinZoomPreference, Float.NEGATIVE_INFINITY));
    }
    if (localTypedArray.hasValue(R.styleable.MapAttrs_cameraMinZoomPreference)) {
      localGoogleMapOptions.maxZoomPreference(localTypedArray.getFloat(R.styleable.MapAttrs_cameraMaxZoomPreference, Float.POSITIVE_INFINITY));
    }
    localGoogleMapOptions.latLngBoundsForCameraTarget(LatLngBounds.createFromAttributes(paramContext, paramAttributeSet));
    localGoogleMapOptions.camera(CameraPosition.createFromAttributes(paramContext, paramAttributeSet));
    localTypedArray.recycle();
    return localGoogleMapOptions;
  }
  
  public GoogleMapOptions ambientEnabled(boolean paramBoolean)
  {
    this.aly = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public GoogleMapOptions camera(CameraPosition paramCameraPosition)
  {
    this.alp = paramCameraPosition;
    return this;
  }
  
  public GoogleMapOptions compassEnabled(boolean paramBoolean)
  {
    this.alr = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public Boolean getAmbientEnabled()
  {
    return this.aly;
  }
  
  public CameraPosition getCamera()
  {
    return this.alp;
  }
  
  public Boolean getCompassEnabled()
  {
    return this.alr;
  }
  
  public LatLngBounds getLatLngBoundsForCameraTarget()
  {
    return this.alB;
  }
  
  public Boolean getLiteMode()
  {
    return this.alw;
  }
  
  public Boolean getMapToolbarEnabled()
  {
    return this.alx;
  }
  
  public int getMapType()
  {
    return this.alo;
  }
  
  public Float getMaxZoomPreference()
  {
    return this.alA;
  }
  
  public Float getMinZoomPreference()
  {
    return this.alz;
  }
  
  public Boolean getRotateGesturesEnabled()
  {
    return this.alv;
  }
  
  public Boolean getScrollGesturesEnabled()
  {
    return this.als;
  }
  
  public Boolean getTiltGesturesEnabled()
  {
    return this.alu;
  }
  
  public Boolean getUseViewLifecycleInFragment()
  {
    return this.aln;
  }
  
  int getVersionCode()
  {
    return this.mVersionCode;
  }
  
  public Boolean getZOrderOnTop()
  {
    return this.alm;
  }
  
  public Boolean getZoomControlsEnabled()
  {
    return this.alq;
  }
  
  public Boolean getZoomGesturesEnabled()
  {
    return this.alt;
  }
  
  public GoogleMapOptions latLngBoundsForCameraTarget(LatLngBounds paramLatLngBounds)
  {
    this.alB = paramLatLngBounds;
    return this;
  }
  
  public GoogleMapOptions liteMode(boolean paramBoolean)
  {
    this.alw = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public GoogleMapOptions mapToolbarEnabled(boolean paramBoolean)
  {
    this.alx = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public GoogleMapOptions mapType(int paramInt)
  {
    this.alo = paramInt;
    return this;
  }
  
  public GoogleMapOptions maxZoomPreference(float paramFloat)
  {
    this.alA = Float.valueOf(paramFloat);
    return this;
  }
  
  public GoogleMapOptions minZoomPreference(float paramFloat)
  {
    this.alz = Float.valueOf(paramFloat);
    return this;
  }
  
  public GoogleMapOptions rotateGesturesEnabled(boolean paramBoolean)
  {
    this.alv = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public GoogleMapOptions scrollGesturesEnabled(boolean paramBoolean)
  {
    this.als = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public GoogleMapOptions tiltGesturesEnabled(boolean paramBoolean)
  {
    this.alu = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public GoogleMapOptions useViewLifecycleInFragment(boolean paramBoolean)
  {
    this.aln = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zza.zza(this, paramParcel, paramInt);
  }
  
  public GoogleMapOptions zOrderOnTop(boolean paramBoolean)
  {
    this.alm = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public GoogleMapOptions zoomControlsEnabled(boolean paramBoolean)
  {
    this.alq = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public GoogleMapOptions zoomGesturesEnabled(boolean paramBoolean)
  {
    this.alt = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  byte zzbrj()
  {
    return com.google.android.gms.maps.internal.zza.zze(this.alm);
  }
  
  byte zzbrk()
  {
    return com.google.android.gms.maps.internal.zza.zze(this.aln);
  }
  
  byte zzbrl()
  {
    return com.google.android.gms.maps.internal.zza.zze(this.alq);
  }
  
  byte zzbrm()
  {
    return com.google.android.gms.maps.internal.zza.zze(this.alr);
  }
  
  byte zzbrn()
  {
    return com.google.android.gms.maps.internal.zza.zze(this.als);
  }
  
  byte zzbro()
  {
    return com.google.android.gms.maps.internal.zza.zze(this.alt);
  }
  
  byte zzbrp()
  {
    return com.google.android.gms.maps.internal.zza.zze(this.alu);
  }
  
  byte zzbrq()
  {
    return com.google.android.gms.maps.internal.zza.zze(this.alv);
  }
  
  byte zzbrr()
  {
    return com.google.android.gms.maps.internal.zza.zze(this.alw);
  }
  
  byte zzbrs()
  {
    return com.google.android.gms.maps.internal.zza.zze(this.alx);
  }
  
  byte zzbrt()
  {
    return com.google.android.gms.maps.internal.zza.zze(this.aly);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/GoogleMapOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */