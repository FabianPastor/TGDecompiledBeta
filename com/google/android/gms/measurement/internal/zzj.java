package com.google.android.gms.measurement.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzj
  implements Parcelable.Creator<EventParams>
{
  static void zza(EventParams paramEventParams, Parcel paramParcel, int paramInt)
  {
    paramInt = zzb.zzcs(paramParcel);
    zzb.zzc(paramParcel, 1, paramEventParams.versionCode);
    zzb.zza(paramParcel, 2, paramEventParams.zzbww(), false);
    zzb.zzaj(paramParcel, paramInt);
  }
  
  public EventParams zzpx(Parcel paramParcel)
  {
    int j = zza.zzcr(paramParcel);
    int i = 0;
    Bundle localBundle = null;
    while (paramParcel.dataPosition() < j)
    {
      int k = zza.zzcq(paramParcel);
      switch (zza.zzgu(k))
      {
      default: 
        zza.zzb(paramParcel, k);
        break;
      case 1: 
        i = zza.zzg(paramParcel, k);
        break;
      case 2: 
        localBundle = zza.zzs(paramParcel, k);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zza.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new EventParams(i, localBundle);
  }
  
  public EventParams[] zzxc(int paramInt)
  {
    return new EventParams[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */