package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;

public class zzb
  implements Parcelable.Creator<CircleOptions>
{
  static void zza(CircleOptions paramCircleOptions, Parcel paramParcel, int paramInt)
  {
    int i = com.google.android.gms.common.internal.safeparcel.zzb.zzcr(paramParcel);
    com.google.android.gms.common.internal.safeparcel.zzb.zzc(paramParcel, 1, paramCircleOptions.getVersionCode());
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 2, paramCircleOptions.getCenter(), paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 3, paramCircleOptions.getRadius());
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 4, paramCircleOptions.getStrokeWidth());
    com.google.android.gms.common.internal.safeparcel.zzb.zzc(paramParcel, 5, paramCircleOptions.getStrokeColor());
    com.google.android.gms.common.internal.safeparcel.zzb.zzc(paramParcel, 6, paramCircleOptions.getFillColor());
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 7, paramCircleOptions.getZIndex());
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 8, paramCircleOptions.isVisible());
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 9, paramCircleOptions.isClickable());
    com.google.android.gms.common.internal.safeparcel.zzb.zzaj(paramParcel, i);
  }
  
  public CircleOptions zzoo(Parcel paramParcel)
  {
    float f1 = 0.0F;
    boolean bool1 = false;
    int m = zza.zzcq(paramParcel);
    LatLng localLatLng = null;
    double d = 0.0D;
    boolean bool2 = false;
    int i = 0;
    int j = 0;
    float f2 = 0.0F;
    int k = 0;
    while (paramParcel.dataPosition() < m)
    {
      int n = zza.zzcp(paramParcel);
      switch (zza.zzgv(n))
      {
      default: 
        zza.zzb(paramParcel, n);
        break;
      case 1: 
        k = zza.zzg(paramParcel, n);
        break;
      case 2: 
        localLatLng = (LatLng)zza.zza(paramParcel, n, LatLng.CREATOR);
        break;
      case 3: 
        d = zza.zzn(paramParcel, n);
        break;
      case 4: 
        f2 = zza.zzl(paramParcel, n);
        break;
      case 5: 
        j = zza.zzg(paramParcel, n);
        break;
      case 6: 
        i = zza.zzg(paramParcel, n);
        break;
      case 7: 
        f1 = zza.zzl(paramParcel, n);
        break;
      case 8: 
        bool2 = zza.zzc(paramParcel, n);
        break;
      case 9: 
        bool1 = zza.zzc(paramParcel, n);
      }
    }
    if (paramParcel.dataPosition() != m) {
      throw new zza.zza(37 + "Overread allowed size end=" + m, paramParcel);
    }
    return new CircleOptions(k, localLatLng, d, f2, j, i, f1, bool2, bool1);
  }
  
  public CircleOptions[] zzvt(int paramInt)
  {
    return new CircleOptions[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */