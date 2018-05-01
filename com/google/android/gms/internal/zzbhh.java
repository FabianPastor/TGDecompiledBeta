package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbhh
  implements Parcelable.Creator<zzbhg>
{
  static void zza(zzbhg paramzzbhg, Parcel paramParcel, int paramInt)
  {
    paramInt = zzc.zzaV(paramParcel);
    zzc.zzc(paramParcel, 1, paramzzbhg.versionCode);
    zzc.zzc(paramParcel, 2, paramzzbhg.left);
    zzc.zzc(paramParcel, 3, paramzzbhg.top);
    zzc.zzc(paramParcel, 4, paramzzbhg.width);
    zzc.zzc(paramParcel, 5, paramzzbhg.height);
    zzc.zza(paramParcel, 6, paramzzbhg.zzbNA);
    zzc.zzJ(paramParcel, paramInt);
  }
  
  public zzbhg zzjl(Parcel paramParcel)
  {
    int i = 0;
    int i1 = zzb.zzaU(paramParcel);
    float f = 0.0F;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    while (paramParcel.dataPosition() < i1)
    {
      int i2 = zzb.zzaT(paramParcel);
      switch (zzb.zzcW(i2))
      {
      default: 
        zzb.zzb(paramParcel, i2);
        break;
      case 1: 
        n = zzb.zzg(paramParcel, i2);
        break;
      case 2: 
        m = zzb.zzg(paramParcel, i2);
        break;
      case 3: 
        k = zzb.zzg(paramParcel, i2);
        break;
      case 4: 
        j = zzb.zzg(paramParcel, i2);
        break;
      case 5: 
        i = zzb.zzg(paramParcel, i2);
        break;
      case 6: 
        f = zzb.zzl(paramParcel, i2);
      }
    }
    if (paramParcel.dataPosition() != i1) {
      throw new zzb.zza(37 + "Overread allowed size end=" + i1, paramParcel);
    }
    return new zzbhg(n, m, k, j, i, f);
  }
  
  public zzbhg[] zznC(int paramInt)
  {
    return new zzbhg[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbhh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */