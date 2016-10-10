package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzh
  implements Parcelable.Creator<PointOfInterest>
{
  static void zza(PointOfInterest paramPointOfInterest, Parcel paramParcel, int paramInt)
  {
    int i = zzb.zzcr(paramParcel);
    zzb.zzc(paramParcel, 1, paramPointOfInterest.getVersionCode());
    zzb.zza(paramParcel, 2, paramPointOfInterest.latLng, paramInt, false);
    zzb.zza(paramParcel, 3, paramPointOfInterest.placeId, false);
    zzb.zza(paramParcel, 4, paramPointOfInterest.name, false);
    zzb.zzaj(paramParcel, i);
  }
  
  public PointOfInterest zzou(Parcel paramParcel)
  {
    String str = null;
    int j = zza.zzcq(paramParcel);
    int i = 0;
    Object localObject2 = null;
    Object localObject1 = null;
    if (paramParcel.dataPosition() < j)
    {
      int k = zza.zzcp(paramParcel);
      Object localObject3;
      switch (zza.zzgv(k))
      {
      default: 
        zza.zzb(paramParcel, k);
        localObject3 = localObject2;
        localObject2 = localObject1;
        localObject1 = localObject3;
      }
      for (;;)
      {
        localObject3 = localObject2;
        localObject2 = localObject1;
        localObject1 = localObject3;
        break;
        i = zza.zzg(paramParcel, k);
        localObject3 = localObject1;
        localObject1 = localObject2;
        localObject2 = localObject3;
        continue;
        localObject3 = (LatLng)zza.zza(paramParcel, k, LatLng.CREATOR);
        localObject1 = localObject2;
        localObject2 = localObject3;
        continue;
        localObject3 = zza.zzq(paramParcel, k);
        localObject2 = localObject1;
        localObject1 = localObject3;
        continue;
        str = zza.zzq(paramParcel, k);
        localObject3 = localObject1;
        localObject1 = localObject2;
        localObject2 = localObject3;
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zza.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new PointOfInterest(i, (LatLng)localObject1, (String)localObject2, str);
  }
  
  public PointOfInterest[] zzvz(int paramInt)
  {
    return new PointOfInterest[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/zzh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */