package com.google.android.gms.maps.model;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class PolygonOptions
  extends AbstractSafeParcelable
{
  public static final zzi CREATOR = new zzi();
  private float amD = 0.0F;
  private boolean amE = true;
  private boolean amF = false;
  private final List<LatLng> ane;
  private final List<List<LatLng>> anf;
  private boolean ang = false;
  private int mFillColor = 0;
  private int mStrokeColor = -16777216;
  private float mStrokeWidth = 10.0F;
  private final int mVersionCode;
  
  public PolygonOptions()
  {
    this.mVersionCode = 1;
    this.ane = new ArrayList();
    this.anf = new ArrayList();
  }
  
  PolygonOptions(int paramInt1, List<LatLng> paramList, List paramList1, float paramFloat1, int paramInt2, int paramInt3, float paramFloat2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    this.mVersionCode = paramInt1;
    this.ane = paramList;
    this.anf = paramList1;
    this.mStrokeWidth = paramFloat1;
    this.mStrokeColor = paramInt2;
    this.mFillColor = paramInt3;
    this.amD = paramFloat2;
    this.amE = paramBoolean1;
    this.ang = paramBoolean2;
    this.amF = paramBoolean3;
  }
  
  public PolygonOptions add(LatLng paramLatLng)
  {
    this.ane.add(paramLatLng);
    return this;
  }
  
  public PolygonOptions add(LatLng... paramVarArgs)
  {
    this.ane.addAll(Arrays.asList(paramVarArgs));
    return this;
  }
  
  public PolygonOptions addAll(Iterable<LatLng> paramIterable)
  {
    paramIterable = paramIterable.iterator();
    while (paramIterable.hasNext())
    {
      LatLng localLatLng = (LatLng)paramIterable.next();
      this.ane.add(localLatLng);
    }
    return this;
  }
  
  public PolygonOptions addHole(Iterable<LatLng> paramIterable)
  {
    ArrayList localArrayList = new ArrayList();
    paramIterable = paramIterable.iterator();
    while (paramIterable.hasNext()) {
      localArrayList.add((LatLng)paramIterable.next());
    }
    this.anf.add(localArrayList);
    return this;
  }
  
  public PolygonOptions clickable(boolean paramBoolean)
  {
    this.amF = paramBoolean;
    return this;
  }
  
  public PolygonOptions fillColor(int paramInt)
  {
    this.mFillColor = paramInt;
    return this;
  }
  
  public PolygonOptions geodesic(boolean paramBoolean)
  {
    this.ang = paramBoolean;
    return this;
  }
  
  public int getFillColor()
  {
    return this.mFillColor;
  }
  
  public List<List<LatLng>> getHoles()
  {
    return this.anf;
  }
  
  public List<LatLng> getPoints()
  {
    return this.ane;
  }
  
  public int getStrokeColor()
  {
    return this.mStrokeColor;
  }
  
  public float getStrokeWidth()
  {
    return this.mStrokeWidth;
  }
  
  int getVersionCode()
  {
    return this.mVersionCode;
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
  
  public PolygonOptions strokeColor(int paramInt)
  {
    this.mStrokeColor = paramInt;
    return this;
  }
  
  public PolygonOptions strokeWidth(float paramFloat)
  {
    this.mStrokeWidth = paramFloat;
    return this;
  }
  
  public PolygonOptions visible(boolean paramBoolean)
  {
    this.amE = paramBoolean;
    return this;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzi.zza(this, paramParcel, paramInt);
  }
  
  public PolygonOptions zIndex(float paramFloat)
  {
    this.amD = paramFloat;
    return this;
  }
  
  List zzbsk()
  {
    return this.anf;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/PolygonOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */