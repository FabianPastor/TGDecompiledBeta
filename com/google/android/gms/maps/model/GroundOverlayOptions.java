package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamic.zzd.zza;

public final class GroundOverlayOptions
  extends AbstractSafeParcelable
{
  public static final zzc CREATOR = new zzc();
  public static final float NO_DIMENSION = -1.0F;
  private LatLngBounds akB;
  private float amD;
  private boolean amE = true;
  private boolean amF = false;
  private BitmapDescriptor amH;
  private LatLng amI;
  private float amJ;
  private float amK;
  private float amL = 0.0F;
  private float amM = 0.5F;
  private float amN = 0.5F;
  private float amz;
  private final int mVersionCode;
  
  public GroundOverlayOptions()
  {
    this.mVersionCode = 1;
  }
  
  GroundOverlayOptions(int paramInt, IBinder paramIBinder, LatLng paramLatLng, float paramFloat1, float paramFloat2, LatLngBounds paramLatLngBounds, float paramFloat3, float paramFloat4, boolean paramBoolean1, float paramFloat5, float paramFloat6, float paramFloat7, boolean paramBoolean2)
  {
    this.mVersionCode = paramInt;
    this.amH = new BitmapDescriptor(zzd.zza.zzfe(paramIBinder));
    this.amI = paramLatLng;
    this.amJ = paramFloat1;
    this.amK = paramFloat2;
    this.akB = paramLatLngBounds;
    this.amz = paramFloat3;
    this.amD = paramFloat4;
    this.amE = paramBoolean1;
    this.amL = paramFloat5;
    this.amM = paramFloat6;
    this.amN = paramFloat7;
    this.amF = paramBoolean2;
  }
  
  private GroundOverlayOptions zza(LatLng paramLatLng, float paramFloat1, float paramFloat2)
  {
    this.amI = paramLatLng;
    this.amJ = paramFloat1;
    this.amK = paramFloat2;
    return this;
  }
  
  public GroundOverlayOptions anchor(float paramFloat1, float paramFloat2)
  {
    this.amM = paramFloat1;
    this.amN = paramFloat2;
    return this;
  }
  
  public GroundOverlayOptions bearing(float paramFloat)
  {
    this.amz = ((paramFloat % 360.0F + 360.0F) % 360.0F);
    return this;
  }
  
  public GroundOverlayOptions clickable(boolean paramBoolean)
  {
    this.amF = paramBoolean;
    return this;
  }
  
  public float getAnchorU()
  {
    return this.amM;
  }
  
  public float getAnchorV()
  {
    return this.amN;
  }
  
  public float getBearing()
  {
    return this.amz;
  }
  
  public LatLngBounds getBounds()
  {
    return this.akB;
  }
  
  public float getHeight()
  {
    return this.amK;
  }
  
  public BitmapDescriptor getImage()
  {
    return this.amH;
  }
  
  public LatLng getLocation()
  {
    return this.amI;
  }
  
  public float getTransparency()
  {
    return this.amL;
  }
  
  int getVersionCode()
  {
    return this.mVersionCode;
  }
  
  public float getWidth()
  {
    return this.amJ;
  }
  
  public float getZIndex()
  {
    return this.amD;
  }
  
  public GroundOverlayOptions image(BitmapDescriptor paramBitmapDescriptor)
  {
    this.amH = paramBitmapDescriptor;
    return this;
  }
  
  public boolean isClickable()
  {
    return this.amF;
  }
  
  public boolean isVisible()
  {
    return this.amE;
  }
  
  public GroundOverlayOptions position(LatLng paramLatLng, float paramFloat)
  {
    boolean bool2 = true;
    if (this.akB == null)
    {
      bool1 = true;
      zzac.zza(bool1, "Position has already been set using positionFromBounds");
      if (paramLatLng == null) {
        break label59;
      }
      bool1 = true;
      label24:
      zzac.zzb(bool1, "Location must be specified");
      if (paramFloat < 0.0F) {
        break label64;
      }
    }
    label59:
    label64:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      zzac.zzb(bool1, "Width must be non-negative");
      return zza(paramLatLng, paramFloat, -1.0F);
      bool1 = false;
      break;
      bool1 = false;
      break label24;
    }
  }
  
  public GroundOverlayOptions position(LatLng paramLatLng, float paramFloat1, float paramFloat2)
  {
    boolean bool2 = true;
    if (this.akB == null)
    {
      bool1 = true;
      zzac.zza(bool1, "Position has already been set using positionFromBounds");
      if (paramLatLng == null) {
        break label81;
      }
      bool1 = true;
      label27:
      zzac.zzb(bool1, "Location must be specified");
      if (paramFloat1 < 0.0F) {
        break label87;
      }
      bool1 = true;
      label43:
      zzac.zzb(bool1, "Width must be non-negative");
      if (paramFloat2 < 0.0F) {
        break label93;
      }
    }
    label81:
    label87:
    label93:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      zzac.zzb(bool1, "Height must be non-negative");
      return zza(paramLatLng, paramFloat1, paramFloat2);
      bool1 = false;
      break;
      bool1 = false;
      break label27;
      bool1 = false;
      break label43;
    }
  }
  
  public GroundOverlayOptions positionFromBounds(LatLngBounds paramLatLngBounds)
  {
    if (this.amI == null) {}
    for (boolean bool = true;; bool = false)
    {
      String str = String.valueOf(this.amI);
      zzac.zza(bool, String.valueOf(str).length() + 46 + "Position has already been set using position: " + str);
      this.akB = paramLatLngBounds;
      return this;
    }
  }
  
  public GroundOverlayOptions transparency(float paramFloat)
  {
    if ((paramFloat >= 0.0F) && (paramFloat <= 1.0F)) {}
    for (boolean bool = true;; bool = false)
    {
      zzac.zzb(bool, "Transparency must be in the range [0..1]");
      this.amL = paramFloat;
      return this;
    }
  }
  
  public GroundOverlayOptions visible(boolean paramBoolean)
  {
    this.amE = paramBoolean;
    return this;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzc.zza(this, paramParcel, paramInt);
  }
  
  public GroundOverlayOptions zIndex(float paramFloat)
  {
    this.amD = paramFloat;
    return this;
  }
  
  IBinder zzbsh()
  {
    return this.amH.zzbrh().asBinder();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/GroundOverlayOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */