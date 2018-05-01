package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbhs
  implements Parcelable.Creator<zzbhr>
{
  static void zza(zzbhr paramzzbhr, Parcel paramParcel, int paramInt)
  {
    paramInt = zzc.zzaV(paramParcel);
    zzc.zzc(paramParcel, 1, paramzzbhr.versionCode);
    zzc.zzJ(paramParcel, paramInt);
  }
  
  public zzbhr zzjp(Parcel paramParcel)
  {
    int j = zzb.zzaU(paramParcel);
    int i = 0;
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
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zzb.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new zzbhr(i);
  }
  
  public zzbhr[] zznG(int paramInt)
  {
    return new zzbhr[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbhs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */