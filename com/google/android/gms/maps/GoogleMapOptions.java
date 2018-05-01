package com.google.android.gms.maps;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Objects.ToStringHelper;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.maps.internal.zza;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CameraPosition.Builder;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public final class GoogleMapOptions
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<GoogleMapOptions> CREATOR = new zzaa();
  private int mapType = -1;
  private Boolean zzaj;
  private Boolean zzak;
  private CameraPosition zzal;
  private Boolean zzam;
  private Boolean zzan;
  private Boolean zzao;
  private Boolean zzap;
  private Boolean zzaq;
  private Boolean zzar;
  private Boolean zzas;
  private Boolean zzat;
  private Boolean zzau;
  private Float zzav = null;
  private Float zzaw = null;
  private LatLngBounds zzax = null;
  
  public GoogleMapOptions() {}
  
  GoogleMapOptions(byte paramByte1, byte paramByte2, int paramInt, CameraPosition paramCameraPosition, byte paramByte3, byte paramByte4, byte paramByte5, byte paramByte6, byte paramByte7, byte paramByte8, byte paramByte9, byte paramByte10, byte paramByte11, Float paramFloat1, Float paramFloat2, LatLngBounds paramLatLngBounds)
  {
    this.zzaj = zza.zza(paramByte1);
    this.zzak = zza.zza(paramByte2);
    this.mapType = paramInt;
    this.zzal = paramCameraPosition;
    this.zzam = zza.zza(paramByte3);
    this.zzan = zza.zza(paramByte4);
    this.zzao = zza.zza(paramByte5);
    this.zzap = zza.zza(paramByte6);
    this.zzaq = zza.zza(paramByte7);
    this.zzar = zza.zza(paramByte8);
    this.zzas = zza.zza(paramByte9);
    this.zzat = zza.zza(paramByte10);
    this.zzau = zza.zza(paramByte11);
    this.zzav = paramFloat1;
    this.zzaw = paramFloat2;
    this.zzax = paramLatLngBounds;
  }
  
  public static GoogleMapOptions createFromAttributes(Context paramContext, AttributeSet paramAttributeSet)
  {
    if ((paramContext == null) || (paramAttributeSet == null)) {}
    GoogleMapOptions localGoogleMapOptions;
    for (paramContext = null;; paramContext = localGoogleMapOptions)
    {
      return paramContext;
      TypedArray localTypedArray = paramContext.getResources().obtainAttributes(paramAttributeSet, R.styleable.MapAttrs);
      localGoogleMapOptions = new GoogleMapOptions();
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
      localGoogleMapOptions.latLngBoundsForCameraTarget(zza(paramContext, paramAttributeSet));
      localGoogleMapOptions.camera(zzb(paramContext, paramAttributeSet));
      localTypedArray.recycle();
    }
  }
  
  public static LatLngBounds zza(Context paramContext, AttributeSet paramAttributeSet)
  {
    Object localObject1 = null;
    Object localObject2 = localObject1;
    if (paramContext != null)
    {
      if (paramAttributeSet == null) {
        localObject2 = localObject1;
      }
    }
    else {
      return (LatLngBounds)localObject2;
    }
    localObject2 = paramContext.getResources().obtainAttributes(paramAttributeSet, R.styleable.MapAttrs);
    if (((TypedArray)localObject2).hasValue(R.styleable.MapAttrs_latLngBoundsSouthWestLatitude)) {}
    for (paramContext = Float.valueOf(((TypedArray)localObject2).getFloat(R.styleable.MapAttrs_latLngBoundsSouthWestLatitude, 0.0F));; paramContext = null)
    {
      if (((TypedArray)localObject2).hasValue(R.styleable.MapAttrs_latLngBoundsSouthWestLongitude)) {}
      for (paramAttributeSet = Float.valueOf(((TypedArray)localObject2).getFloat(R.styleable.MapAttrs_latLngBoundsSouthWestLongitude, 0.0F));; paramAttributeSet = null)
      {
        if (((TypedArray)localObject2).hasValue(R.styleable.MapAttrs_latLngBoundsNorthEastLatitude)) {}
        for (Float localFloat1 = Float.valueOf(((TypedArray)localObject2).getFloat(R.styleable.MapAttrs_latLngBoundsNorthEastLatitude, 0.0F));; localFloat1 = null)
        {
          if (((TypedArray)localObject2).hasValue(R.styleable.MapAttrs_latLngBoundsNorthEastLongitude)) {}
          for (Float localFloat2 = Float.valueOf(((TypedArray)localObject2).getFloat(R.styleable.MapAttrs_latLngBoundsNorthEastLongitude, 0.0F));; localFloat2 = null)
          {
            ((TypedArray)localObject2).recycle();
            localObject2 = localObject1;
            if (paramContext == null) {
              break;
            }
            localObject2 = localObject1;
            if (paramAttributeSet == null) {
              break;
            }
            localObject2 = localObject1;
            if (localFloat1 == null) {
              break;
            }
            localObject2 = localObject1;
            if (localFloat2 == null) {
              break;
            }
            localObject2 = new LatLngBounds(new LatLng(paramContext.floatValue(), paramAttributeSet.floatValue()), new LatLng(localFloat1.floatValue(), localFloat2.floatValue()));
            break;
          }
        }
      }
    }
  }
  
  public static CameraPosition zzb(Context paramContext, AttributeSet paramAttributeSet)
  {
    if ((paramContext == null) || (paramAttributeSet == null))
    {
      paramContext = null;
      return paramContext;
    }
    TypedArray localTypedArray = paramContext.getResources().obtainAttributes(paramAttributeSet, R.styleable.MapAttrs);
    if (localTypedArray.hasValue(R.styleable.MapAttrs_cameraTargetLat)) {}
    for (float f1 = localTypedArray.getFloat(R.styleable.MapAttrs_cameraTargetLat, 0.0F);; f1 = 0.0F)
    {
      if (localTypedArray.hasValue(R.styleable.MapAttrs_cameraTargetLng)) {}
      for (float f2 = localTypedArray.getFloat(R.styleable.MapAttrs_cameraTargetLng, 0.0F);; f2 = 0.0F)
      {
        paramAttributeSet = new LatLng(f1, f2);
        paramContext = CameraPosition.builder();
        paramContext.target(paramAttributeSet);
        if (localTypedArray.hasValue(R.styleable.MapAttrs_cameraZoom)) {
          paramContext.zoom(localTypedArray.getFloat(R.styleable.MapAttrs_cameraZoom, 0.0F));
        }
        if (localTypedArray.hasValue(R.styleable.MapAttrs_cameraBearing)) {
          paramContext.bearing(localTypedArray.getFloat(R.styleable.MapAttrs_cameraBearing, 0.0F));
        }
        if (localTypedArray.hasValue(R.styleable.MapAttrs_cameraTilt)) {
          paramContext.tilt(localTypedArray.getFloat(R.styleable.MapAttrs_cameraTilt, 0.0F));
        }
        localTypedArray.recycle();
        paramContext = paramContext.build();
        break;
      }
    }
  }
  
  public final GoogleMapOptions ambientEnabled(boolean paramBoolean)
  {
    this.zzau = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public final GoogleMapOptions camera(CameraPosition paramCameraPosition)
  {
    this.zzal = paramCameraPosition;
    return this;
  }
  
  public final GoogleMapOptions compassEnabled(boolean paramBoolean)
  {
    this.zzan = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public final CameraPosition getCamera()
  {
    return this.zzal;
  }
  
  public final LatLngBounds getLatLngBoundsForCameraTarget()
  {
    return this.zzax;
  }
  
  public final int getMapType()
  {
    return this.mapType;
  }
  
  public final Float getMaxZoomPreference()
  {
    return this.zzaw;
  }
  
  public final Float getMinZoomPreference()
  {
    return this.zzav;
  }
  
  public final GoogleMapOptions latLngBoundsForCameraTarget(LatLngBounds paramLatLngBounds)
  {
    this.zzax = paramLatLngBounds;
    return this;
  }
  
  public final GoogleMapOptions liteMode(boolean paramBoolean)
  {
    this.zzas = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public final GoogleMapOptions mapToolbarEnabled(boolean paramBoolean)
  {
    this.zzat = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public final GoogleMapOptions mapType(int paramInt)
  {
    this.mapType = paramInt;
    return this;
  }
  
  public final GoogleMapOptions maxZoomPreference(float paramFloat)
  {
    this.zzaw = Float.valueOf(paramFloat);
    return this;
  }
  
  public final GoogleMapOptions minZoomPreference(float paramFloat)
  {
    this.zzav = Float.valueOf(paramFloat);
    return this;
  }
  
  public final GoogleMapOptions rotateGesturesEnabled(boolean paramBoolean)
  {
    this.zzar = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public final GoogleMapOptions scrollGesturesEnabled(boolean paramBoolean)
  {
    this.zzao = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public final GoogleMapOptions tiltGesturesEnabled(boolean paramBoolean)
  {
    this.zzaq = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public final String toString()
  {
    return Objects.toStringHelper(this).add("MapType", Integer.valueOf(this.mapType)).add("LiteMode", this.zzas).add("Camera", this.zzal).add("CompassEnabled", this.zzan).add("ZoomControlsEnabled", this.zzam).add("ScrollGesturesEnabled", this.zzao).add("ZoomGesturesEnabled", this.zzap).add("TiltGesturesEnabled", this.zzaq).add("RotateGesturesEnabled", this.zzar).add("MapToolbarEnabled", this.zzat).add("AmbientEnabled", this.zzau).add("MinZoomPreference", this.zzav).add("MaxZoomPreference", this.zzaw).add("LatLngBoundsForCameraTarget", this.zzax).add("ZOrderOnTop", this.zzaj).add("UseViewLifecycleInFragment", this.zzak).toString();
  }
  
  public final GoogleMapOptions useViewLifecycleInFragment(boolean paramBoolean)
  {
    this.zzak = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeByte(paramParcel, 2, zza.zza(this.zzaj));
    SafeParcelWriter.writeByte(paramParcel, 3, zza.zza(this.zzak));
    SafeParcelWriter.writeInt(paramParcel, 4, getMapType());
    SafeParcelWriter.writeParcelable(paramParcel, 5, getCamera(), paramInt, false);
    SafeParcelWriter.writeByte(paramParcel, 6, zza.zza(this.zzam));
    SafeParcelWriter.writeByte(paramParcel, 7, zza.zza(this.zzan));
    SafeParcelWriter.writeByte(paramParcel, 8, zza.zza(this.zzao));
    SafeParcelWriter.writeByte(paramParcel, 9, zza.zza(this.zzap));
    SafeParcelWriter.writeByte(paramParcel, 10, zza.zza(this.zzaq));
    SafeParcelWriter.writeByte(paramParcel, 11, zza.zza(this.zzar));
    SafeParcelWriter.writeByte(paramParcel, 12, zza.zza(this.zzas));
    SafeParcelWriter.writeByte(paramParcel, 14, zza.zza(this.zzat));
    SafeParcelWriter.writeByte(paramParcel, 15, zza.zza(this.zzau));
    SafeParcelWriter.writeFloatObject(paramParcel, 16, getMinZoomPreference(), false);
    SafeParcelWriter.writeFloatObject(paramParcel, 17, getMaxZoomPreference(), false);
    SafeParcelWriter.writeParcelable(paramParcel, 18, getLatLngBoundsForCameraTarget(), paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
  
  public final GoogleMapOptions zOrderOnTop(boolean paramBoolean)
  {
    this.zzaj = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public final GoogleMapOptions zoomControlsEnabled(boolean paramBoolean)
  {
    this.zzam = Boolean.valueOf(paramBoolean);
    return this;
  }
  
  public final GoogleMapOptions zoomGesturesEnabled(boolean paramBoolean)
  {
    this.zzap = Boolean.valueOf(paramBoolean);
    return this;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/GoogleMapOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */