package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class PolygonOptions
  extends zza
{
  public static final Parcelable.Creator<PolygonOptions> CREATOR = new zzk();
  private int mFillColor = 0;
  private int mStrokeColor = -16777216;
  private float mStrokeWidth = 10.0F;
  private final List<LatLng> zzbnN;
  private final List<List<LatLng>> zzbnO;
  private boolean zzbnP = false;
  private int zzbnQ = 0;
  private float zzbnk = 0.0F;
  private boolean zzbnl = true;
  private boolean zzbnm = false;
  @Nullable
  private List<PatternItem> zzbnn = null;
  
  public PolygonOptions()
  {
    this.zzbnN = new ArrayList();
    this.zzbnO = new ArrayList();
  }
  
  PolygonOptions(List<LatLng> paramList, List paramList1, float paramFloat1, int paramInt1, int paramInt2, float paramFloat2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt3, @Nullable List<PatternItem> paramList2)
  {
    this.zzbnN = paramList;
    this.zzbnO = paramList1;
    this.mStrokeWidth = paramFloat1;
    this.mStrokeColor = paramInt1;
    this.mFillColor = paramInt2;
    this.zzbnk = paramFloat2;
    this.zzbnl = paramBoolean1;
    this.zzbnP = paramBoolean2;
    this.zzbnm = paramBoolean3;
    this.zzbnQ = paramInt3;
    this.zzbnn = paramList2;
  }
  
  public final PolygonOptions add(LatLng paramLatLng)
  {
    this.zzbnN.add(paramLatLng);
    return this;
  }
  
  public final PolygonOptions add(LatLng... paramVarArgs)
  {
    this.zzbnN.addAll(Arrays.asList(paramVarArgs));
    return this;
  }
  
  public final PolygonOptions addAll(Iterable<LatLng> paramIterable)
  {
    paramIterable = paramIterable.iterator();
    while (paramIterable.hasNext())
    {
      LatLng localLatLng = (LatLng)paramIterable.next();
      this.zzbnN.add(localLatLng);
    }
    return this;
  }
  
  public final PolygonOptions addHole(Iterable<LatLng> paramIterable)
  {
    ArrayList localArrayList = new ArrayList();
    paramIterable = paramIterable.iterator();
    while (paramIterable.hasNext()) {
      localArrayList.add((LatLng)paramIterable.next());
    }
    this.zzbnO.add(localArrayList);
    return this;
  }
  
  public final PolygonOptions clickable(boolean paramBoolean)
  {
    this.zzbnm = paramBoolean;
    return this;
  }
  
  public final PolygonOptions fillColor(int paramInt)
  {
    this.mFillColor = paramInt;
    return this;
  }
  
  public final PolygonOptions geodesic(boolean paramBoolean)
  {
    this.zzbnP = paramBoolean;
    return this;
  }
  
  public final int getFillColor()
  {
    return this.mFillColor;
  }
  
  public final List<List<LatLng>> getHoles()
  {
    return this.zzbnO;
  }
  
  public final List<LatLng> getPoints()
  {
    return this.zzbnN;
  }
  
  public final int getStrokeColor()
  {
    return this.mStrokeColor;
  }
  
  public final int getStrokeJointType()
  {
    return this.zzbnQ;
  }
  
  @Nullable
  public final List<PatternItem> getStrokePattern()
  {
    return this.zzbnn;
  }
  
  public final float getStrokeWidth()
  {
    return this.mStrokeWidth;
  }
  
  public final float getZIndex()
  {
    return this.zzbnk;
  }
  
  public final boolean isClickable()
  {
    return this.zzbnm;
  }
  
  public final boolean isGeodesic()
  {
    return this.zzbnP;
  }
  
  public final boolean isVisible()
  {
    return this.zzbnl;
  }
  
  public final PolygonOptions strokeColor(int paramInt)
  {
    this.mStrokeColor = paramInt;
    return this;
  }
  
  public final PolygonOptions strokeJointType(int paramInt)
  {
    this.zzbnQ = paramInt;
    return this;
  }
  
  public final PolygonOptions strokePattern(@Nullable List<PatternItem> paramList)
  {
    this.zzbnn = paramList;
    return this;
  }
  
  public final PolygonOptions strokeWidth(float paramFloat)
  {
    this.mStrokeWidth = paramFloat;
    return this;
  }
  
  public final PolygonOptions visible(boolean paramBoolean)
  {
    this.zzbnl = paramBoolean;
    return this;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 2, getPoints(), false);
    zzd.zzd(paramParcel, 3, this.zzbnO, false);
    zzd.zza(paramParcel, 4, getStrokeWidth());
    zzd.zzc(paramParcel, 5, getStrokeColor());
    zzd.zzc(paramParcel, 6, getFillColor());
    zzd.zza(paramParcel, 7, getZIndex());
    zzd.zza(paramParcel, 8, isVisible());
    zzd.zza(paramParcel, 9, isGeodesic());
    zzd.zza(paramParcel, 10, isClickable());
    zzd.zzc(paramParcel, 11, getStrokeJointType());
    zzd.zzc(paramParcel, 12, getStrokePattern(), false);
    zzd.zzI(paramParcel, paramInt);
  }
  
  public final PolygonOptions zIndex(float paramFloat)
  {
    this.zzbnk = paramFloat;
    return this;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/PolygonOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */