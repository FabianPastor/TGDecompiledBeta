package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class PolylineOptions
  extends zza
{
  public static final Parcelable.Creator<PolylineOptions> CREATOR = new zzl();
  private int mColor = -16777216;
  private final List<LatLng> zzbnN;
  private boolean zzbnP = false;
  @NonNull
  private Cap zzbnS = new ButtCap();
  @NonNull
  private Cap zzbnT = new ButtCap();
  private int zzbnU = 0;
  @Nullable
  private List<PatternItem> zzbnV = null;
  private float zzbnk = 0.0F;
  private boolean zzbnl = true;
  private boolean zzbnm = false;
  private float zzbnr = 10.0F;
  
  public PolylineOptions()
  {
    this.zzbnN = new ArrayList();
  }
  
  PolylineOptions(List paramList, float paramFloat1, int paramInt1, float paramFloat2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, @Nullable Cap paramCap1, @Nullable Cap paramCap2, int paramInt2, @Nullable List<PatternItem> paramList1)
  {
    this.zzbnN = paramList;
    this.zzbnr = paramFloat1;
    this.mColor = paramInt1;
    this.zzbnk = paramFloat2;
    this.zzbnl = paramBoolean1;
    this.zzbnP = paramBoolean2;
    this.zzbnm = paramBoolean3;
    if (paramCap1 != null) {
      this.zzbnS = paramCap1;
    }
    if (paramCap2 != null) {
      this.zzbnT = paramCap2;
    }
    this.zzbnU = paramInt2;
    this.zzbnV = paramList1;
  }
  
  public final PolylineOptions add(LatLng paramLatLng)
  {
    this.zzbnN.add(paramLatLng);
    return this;
  }
  
  public final PolylineOptions add(LatLng... paramVarArgs)
  {
    this.zzbnN.addAll(Arrays.asList(paramVarArgs));
    return this;
  }
  
  public final PolylineOptions addAll(Iterable<LatLng> paramIterable)
  {
    paramIterable = paramIterable.iterator();
    while (paramIterable.hasNext())
    {
      LatLng localLatLng = (LatLng)paramIterable.next();
      this.zzbnN.add(localLatLng);
    }
    return this;
  }
  
  public final PolylineOptions clickable(boolean paramBoolean)
  {
    this.zzbnm = paramBoolean;
    return this;
  }
  
  public final PolylineOptions color(int paramInt)
  {
    this.mColor = paramInt;
    return this;
  }
  
  public final PolylineOptions endCap(@NonNull Cap paramCap)
  {
    this.zzbnT = ((Cap)zzbo.zzb(paramCap, "endCap must not be null"));
    return this;
  }
  
  public final PolylineOptions geodesic(boolean paramBoolean)
  {
    this.zzbnP = paramBoolean;
    return this;
  }
  
  public final int getColor()
  {
    return this.mColor;
  }
  
  @NonNull
  public final Cap getEndCap()
  {
    return this.zzbnT;
  }
  
  public final int getJointType()
  {
    return this.zzbnU;
  }
  
  @Nullable
  public final List<PatternItem> getPattern()
  {
    return this.zzbnV;
  }
  
  public final List<LatLng> getPoints()
  {
    return this.zzbnN;
  }
  
  @NonNull
  public final Cap getStartCap()
  {
    return this.zzbnS;
  }
  
  public final float getWidth()
  {
    return this.zzbnr;
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
  
  public final PolylineOptions jointType(int paramInt)
  {
    this.zzbnU = paramInt;
    return this;
  }
  
  public final PolylineOptions pattern(@Nullable List<PatternItem> paramList)
  {
    this.zzbnV = paramList;
    return this;
  }
  
  public final PolylineOptions startCap(@NonNull Cap paramCap)
  {
    this.zzbnS = ((Cap)zzbo.zzb(paramCap, "startCap must not be null"));
    return this;
  }
  
  public final PolylineOptions visible(boolean paramBoolean)
  {
    this.zzbnl = paramBoolean;
    return this;
  }
  
  public final PolylineOptions width(float paramFloat)
  {
    this.zzbnr = paramFloat;
    return this;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 2, getPoints(), false);
    zzd.zza(paramParcel, 3, getWidth());
    zzd.zzc(paramParcel, 4, getColor());
    zzd.zza(paramParcel, 5, getZIndex());
    zzd.zza(paramParcel, 6, isVisible());
    zzd.zza(paramParcel, 7, isGeodesic());
    zzd.zza(paramParcel, 8, isClickable());
    zzd.zza(paramParcel, 9, getStartCap(), paramInt, false);
    zzd.zza(paramParcel, 10, getEndCap(), paramInt, false);
    zzd.zzc(paramParcel, 11, getJointType());
    zzd.zzc(paramParcel, 12, getPattern(), false);
    zzd.zzI(paramParcel, i);
  }
  
  public final PolylineOptions zIndex(float paramFloat)
  {
    this.zzbnk = paramFloat;
    return this;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/PolylineOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */