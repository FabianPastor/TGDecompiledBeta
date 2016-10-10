package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzd
  implements Parcelable.Creator<LatLngBounds>
{
  static void zza(LatLngBounds paramLatLngBounds, Parcel paramParcel, int paramInt)
  {
    int i = zzb.zzcr(paramParcel);
    zzb.zzc(paramParcel, 1, paramLatLngBounds.getVersionCode());
    zzb.zza(paramParcel, 2, paramLatLngBounds.southwest, paramInt, false);
    zzb.zza(paramParcel, 3, paramLatLngBounds.northeast, paramInt, false);
    zzb.zzaj(paramParcel, i);
  }
  
  public LatLngBounds zzoq(Parcel paramParcel)
  {
    LatLng localLatLng1 = null;
    int j = zza.zzcq(paramParcel);
    int i = 0;
    LatLng localLatLng2 = null;
    if (paramParcel.dataPosition() < j)
    {
      int k = zza.zzcp(paramParcel);
      switch (zza.zzgv(k))
      {
      default: 
        zza.zzb(paramParcel, k);
      }
      for (;;)
      {
        break;
        i = zza.zzg(paramParcel, k);
        continue;
        localLatLng2 = (LatLng)zza.zza(paramParcel, k, LatLng.CREATOR);
        continue;
        localLatLng1 = (LatLng)zza.zza(paramParcel, k, LatLng.CREATOR);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zza.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new LatLngBounds(i, localLatLng2, localLatLng1);
  }
  
  public LatLngBounds[] zzvv(int paramInt)
  {
    return new LatLngBounds[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */