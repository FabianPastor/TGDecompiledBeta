package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbkb
  implements Parcelable.Creator<zzbka>
{
  static void zza(zzbka paramzzbka, Parcel paramParcel, int paramInt)
  {
    paramInt = zzc.zzaZ(paramParcel);
    zzc.zzc(paramParcel, 1, paramzzbka.versionCode);
    zzc.zzc(paramParcel, 2, paramzzbka.width);
    zzc.zzc(paramParcel, 3, paramzzbka.height);
    zzc.zzc(paramParcel, 4, paramzzbka.id);
    zzc.zza(paramParcel, 5, paramzzbka.zzbPo);
    zzc.zzc(paramParcel, 6, paramzzbka.rotation);
    zzc.zzJ(paramParcel, paramInt);
  }
  
  public zzbka zzjQ(Parcel paramParcel)
  {
    int i = 0;
    int i1 = zzb.zzaY(paramParcel);
    long l = 0L;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    while (paramParcel.dataPosition() < i1)
    {
      int i2 = zzb.zzaX(paramParcel);
      switch (zzb.zzdc(i2))
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
        l = zzb.zzi(paramParcel, i2);
        break;
      case 6: 
        i = zzb.zzg(paramParcel, i2);
      }
    }
    if (paramParcel.dataPosition() != i1) {
      throw new zzb.zza(37 + "Overread allowed size end=" + i1, paramParcel);
    }
    return new zzbka(n, m, k, j, l, i);
  }
  
  public zzbka[] zzol(int paramInt)
  {
    return new zzbka[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbkb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */