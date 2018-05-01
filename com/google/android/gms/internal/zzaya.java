package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.common.internal.zzad;

public class zzaya
  implements Parcelable.Creator<zzaxz>
{
  static void zza(zzaxz paramzzaxz, Parcel paramParcel, int paramInt)
  {
    int i = zzc.zzaV(paramParcel);
    zzc.zzc(paramParcel, 1, paramzzaxz.mVersionCode);
    zzc.zza(paramParcel, 2, paramzzaxz.zzOo(), paramInt, false);
    zzc.zzJ(paramParcel, i);
  }
  
  public zzaxz zziR(Parcel paramParcel)
  {
    int j = zzb.zzaU(paramParcel);
    int i = 0;
    zzad localzzad = null;
    while (paramParcel.dataPosition() < j)
    {
      int k = zzb.zzaT(paramParcel);
      switch (zzb.zzcW(k))
      {
      default: 
        zzb.zzb(paramParcel, k);
        break;
      case 1: 
        i = zzb.zzg(paramParcel, k);
        break;
      case 2: 
        localzzad = (zzad)zzb.zza(paramParcel, k, zzad.CREATOR);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zzb.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new zzaxz(i, localzzad);
  }
  
  public zzaxz[] zzmM(int paramInt)
  {
    return new zzaxz[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaya.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */