package com.google.android.gms.maps.model;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class PolylineOptions
  extends AbstractSafeParcelable
{
  public static final zzj CREATOR = new zzj();
  private float amD = 0.0F;
  private boolean amE = true;
  private boolean amF = false;
  private float amJ = 10.0F;
  private final List<LatLng> ane;
  private boolean ang = false;
  private int mColor = -16777216;
  private final int mVersionCode;
  
  public PolylineOptions()
  {
    this.mVersionCode = 1;
    this.ane = new ArrayList();
  }
  
  PolylineOptions(int paramInt1, List paramList, float paramFloat1, int paramInt2, float paramFloat2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    this.mVersionCode = paramInt1;
    this.ane = paramList;
    this.amJ = paramFloat1;
    this.mColor = paramInt2;
    this.amD = paramFloat2;
    this.amE = paramBoolean1;
    this.ang = paramBoolean2;
    this.amF = paramBoolean3;
  }
  
  public PolylineOptions add(LatLng paramLatLng)
  {
    this.ane.add(paramLatLng);
    return this;
  }
  
  public PolylineOptions add(LatLng... paramVarArgs)
  {
    this.ane.addAll(Arrays.asList(paramVarArgs));
    return this;
  }
  
  public PolylineOptions addAll(Iterable<LatLng> paramIterable)
  {
    paramIterable = paramIterable.iterator();
    while (paramIterable.hasNext())
    {
      LatLng localLatLng = (LatLng)paramIterable.next();
      this.ane.add(localLatLng);
    }
    return this;
  }
  
  public PolylineOptions clickable(boolean paramBoolean)
  {
    this.amF = paramBoolean;
    return this;
  }
  
  public PolylineOptions color(int paramInt)
  {
    this.mColor = paramInt;
    return this;
  }
  
  public PolylineOptions geodesic(boolean paramBoolean)
  {
    this.ang = paramBoolean;
    return this;
  }
  
  public int getColor()
  {
    return this.mColor;
  }
  
  public List<LatLng> getPoints()
  {
    return this.ane;
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
  
  public boolean isClickable()
  {
    return this.amF;
  }
  
  public boolean isGeodesic()
  {
    return this.ang;
  }
  
  public boolean isVisible()
  {
    return this.amE;
  }
  
  public PolylineOptions visible(boolean paramBoolean)
  {
    this.amE = paramBoolean;
    return this;
  }
  
  public PolylineOptions width(float paramFloat)
  {
    this.amJ = paramFloat;
    return this;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzj.zza(this, paramParcel, paramInt);
  }
  
  public PolylineOptions zIndex(float paramFloat)
  {
    this.amD = paramFloat;
    return this;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/PolylineOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */