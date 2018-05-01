package com.google.android.gms.internal;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbhn
  implements Parcelable.Creator<zzbhm>
{
  static void zza(zzbhm paramzzbhm, Parcel paramParcel, int paramInt)
  {
    int i = zzc.zzaV(paramParcel);
    zzc.zzc(paramParcel, 1, paramzzbhm.versionCode);
    zzc.zza(paramParcel, 2, paramzzbhm.zzbNL, paramInt, false);
    zzc.zzJ(paramParcel, i);
  }
  
  public zzbhm zzjn(Parcel paramParcel)
  {
    int j = zzb.zzaU(paramParcel);
    int i = 0;
    Rect localRect = null;
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
        localRect = (Rect)zzb.zza(paramParcel, k, Rect.CREATOR);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zzb.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new zzbhm(i, localRect);
  }
  
  public zzbhm[] zznE(int paramInt)
  {
    return new zzbhm[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbhn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */