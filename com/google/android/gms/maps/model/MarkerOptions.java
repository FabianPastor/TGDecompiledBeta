package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamic.zzd.zza;

public final class MarkerOptions
  extends AbstractSafeParcelable
{
  public static final zzg CREATOR = new zzg();
  private String HP;
  private float amD;
  private boolean amE = true;
  private float amM = 0.5F;
  private float amN = 1.0F;
  private String amW;
  private BitmapDescriptor amX;
  private boolean amY;
  private boolean amZ = false;
  private LatLng ame;
  private float ana = 0.0F;
  private float anb = 0.5F;
  private float anc = 0.0F;
  private float mAlpha = 1.0F;
  private final int mVersionCode;
  
  public MarkerOptions()
  {
    this.mVersionCode = 1;
  }
  
  MarkerOptions(int paramInt, LatLng paramLatLng, String paramString1, String paramString2, IBinder paramIBinder, float paramFloat1, float paramFloat2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7)
  {
    this.mVersionCode = paramInt;
    this.ame = paramLatLng;
    this.HP = paramString1;
    this.amW = paramString2;
    if (paramIBinder == null) {}
    for (paramLatLng = null;; paramLatLng = new BitmapDescriptor(zzd.zza.zzfe(paramIBinder)))
    {
      this.amX = paramLatLng;
      this.amM = paramFloat1;
      this.amN = paramFloat2;
      this.amY = paramBoolean1;
      this.amE = paramBoolean2;
      this.amZ = paramBoolean3;
      this.ana = paramFloat3;
      this.anb = paramFloat4;
      this.anc = paramFloat5;
      this.mAlpha = paramFloat6;
      this.amD = paramFloat7;
      return;
    }
  }
  
  public MarkerOptions alpha(float paramFloat)
  {
    this.mAlpha = paramFloat;
    return this;
  }
  
  public MarkerOptions anchor(float paramFloat1, float paramFloat2)
  {
    this.amM = paramFloat1;
    this.amN = paramFloat2;
    return this;
  }
  
  public MarkerOptions draggable(boolean paramBoolean)
  {
    this.amY = paramBoolean;
    return this;
  }
  
  public MarkerOptions flat(boolean paramBoolean)
  {
    this.amZ = paramBoolean;
    return this;
  }
  
  public float getAlpha()
  {
    return this.mAlpha;
  }
  
  public float getAnchorU()
  {
    return this.amM;
  }
  
  public float getAnchorV()
  {
    return this.amN;
  }
  
  public BitmapDescriptor getIcon()
  {
    return this.amX;
  }
  
  public float getInfoWindowAnchorU()
  {
    return this.anb;
  }
  
  public float getInfoWindowAnchorV()
  {
    return this.anc;
  }
  
  public LatLng getPosition()
  {
    return this.ame;
  }
  
  public float getRotation()
  {
    return this.ana;
  }
  
  public String getSnippet()
  {
    return this.amW;
  }
  
  public String getTitle()
  {
    return this.HP;
  }
  
  int getVersionCode()
  {
    return this.mVersionCode;
  }
  
  public float getZIndex()
  {
    return this.amD;
  }
  
  public MarkerOptions icon(@Nullable BitmapDescriptor paramBitmapDescriptor)
  {
    this.amX = paramBitmapDescriptor;
    return this;
  }
  
  public MarkerOptions infoWindowAnchor(float paramFloat1, float paramFloat2)
  {
    this.anb = paramFloat1;
    this.anc = paramFloat2;
    return this;
  }
  
  public boolean isDraggable()
  {
    return this.amY;
  }
  
  public boolean isFlat()
  {
    return this.amZ;
  }
  
  public boolean isVisible()
  {
    return this.amE;
  }
  
  public MarkerOptions position(@NonNull LatLng paramLatLng)
  {
    if (paramLatLng == null) {
      throw new IllegalArgumentException("latlng cannot be null - a position is required.");
    }
    this.ame = paramLatLng;
    return this;
  }
  
  public MarkerOptions rotation(float paramFloat)
  {
    this.ana = paramFloat;
    return this;
  }
  
  public MarkerOptions snippet(@Nullable String paramString)
  {
    this.amW = paramString;
    return this;
  }
  
  public MarkerOptions title(@Nullable String paramString)
  {
    this.HP = paramString;
    return this;
  }
  
  public MarkerOptions visible(boolean paramBoolean)
  {
    this.amE = paramBoolean;
    return this;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzg.zza(this, paramParcel, paramInt);
  }
  
  public MarkerOptions zIndex(float paramFloat)
  {
    this.amD = paramFloat;
    return this;
  }
  
  IBinder zzbsj()
  {
    if (this.amX == null) {
      return null;
    }
    return this.amX.zzbrh().asBinder();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/MarkerOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */