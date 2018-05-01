package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Objects.ToStringHelper;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class LatLngBounds
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<LatLngBounds> CREATOR = new zze();
  public final LatLng northeast;
  public final LatLng southwest;
  
  public LatLngBounds(LatLng paramLatLng1, LatLng paramLatLng2)
  {
    Preconditions.checkNotNull(paramLatLng1, "null southwest");
    Preconditions.checkNotNull(paramLatLng2, "null northeast");
    if (paramLatLng2.latitude >= paramLatLng1.latitude) {}
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkArgument(bool, "southern latitude exceeds northern latitude (%s > %s)", new Object[] { Double.valueOf(paramLatLng1.latitude), Double.valueOf(paramLatLng2.latitude) });
      this.southwest = paramLatLng1;
      this.northeast = paramLatLng2;
      return;
    }
  }
  
  private static double zza(double paramDouble1, double paramDouble2)
  {
    return (paramDouble1 - paramDouble2 + 360.0D) % 360.0D;
  }
  
  private static double zzb(double paramDouble1, double paramDouble2)
  {
    return (paramDouble2 - paramDouble1 + 360.0D) % 360.0D;
  }
  
  public final boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {}
    for (;;)
    {
      return bool;
      if (!(paramObject instanceof LatLngBounds))
      {
        bool = false;
      }
      else
      {
        paramObject = (LatLngBounds)paramObject;
        if ((!this.southwest.equals(((LatLngBounds)paramObject).southwest)) || (!this.northeast.equals(((LatLngBounds)paramObject).northeast))) {
          bool = false;
        }
      }
    }
  }
  
  public final int hashCode()
  {
    return Objects.hashCode(new Object[] { this.southwest, this.northeast });
  }
  
  public final String toString()
  {
    return Objects.toStringHelper(this).add("southwest", this.southwest).add("northeast", this.northeast).toString();
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeParcelable(paramParcel, 2, this.southwest, paramInt, false);
    SafeParcelWriter.writeParcelable(paramParcel, 3, this.northeast, paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
  
  public static final class Builder
  {
    private double zzdg = Double.POSITIVE_INFINITY;
    private double zzdh = Double.NEGATIVE_INFINITY;
    private double zzdi = NaN.0D;
    private double zzdj = NaN.0D;
    
    public final LatLngBounds build()
    {
      if (!Double.isNaN(this.zzdi)) {}
      for (boolean bool = true;; bool = false)
      {
        Preconditions.checkState(bool, "no included points");
        return new LatLngBounds(new LatLng(this.zzdg, this.zzdi), new LatLng(this.zzdh, this.zzdj));
      }
    }
    
    public final Builder include(LatLng paramLatLng)
    {
      int i = 1;
      this.zzdg = Math.min(this.zzdg, paramLatLng.latitude);
      this.zzdh = Math.max(this.zzdh, paramLatLng.latitude);
      double d = paramLatLng.longitude;
      if (Double.isNaN(this.zzdi))
      {
        this.zzdi = d;
        this.zzdj = d;
      }
      label57:
      label159:
      for (;;)
      {
        return this;
        int j;
        if (this.zzdi <= this.zzdj) {
          if ((this.zzdi <= d) && (d <= this.zzdj)) {
            j = i;
          }
        }
        for (;;)
        {
          if (j != 0) {
            break label159;
          }
          if (LatLngBounds.zzc(this.zzdi, d) >= LatLngBounds.zzd(this.zzdj, d)) {
            break;
          }
          this.zzdi = d;
          break label57;
          j = 0;
          continue;
          j = i;
          if (this.zzdi > d)
          {
            j = i;
            if (d > this.zzdj) {
              j = 0;
            }
          }
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/LatLngBounds.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */