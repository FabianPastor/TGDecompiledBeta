package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbke
  implements Parcelable.Creator<zzbkd>
{
  static void zza(zzbkd paramzzbkd, Parcel paramParcel, int paramInt)
  {
    paramInt = zzc.zzaZ(paramParcel);
    zzc.zzc(paramParcel, 1, paramzzbkd.versionCode);
    zzc.zzc(paramParcel, 2, paramzzbkd.left);
    zzc.zzc(paramParcel, 3, paramzzbkd.top);
    zzc.zzc(paramParcel, 4, paramzzbkd.width);
    zzc.zzc(paramParcel, 5, paramzzbkd.height);
    zzc.zza(paramParcel, 6, paramzzbkd.zzbPy);
    zzc.zzJ(paramParcel, paramInt);
  }
  
  public zzbkd zzjR(Parcel paramParcel)
  {
    int i = 0;
    int i1 = zzb.zzaY(paramParcel);
    float f = 0.0F;
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
        i = zzb.zzg(paramParcel, i2);
        break;
      case 6: 
        f = zzb.zzl(paramParcel, i2);
      }
    }
    if (paramParcel.dataPosition() != i1) {
      throw new zzb.zza(37 + "Overread allowed size end=" + i1, paramParcel);
    }
    return new zzbkd(n, m, k, j, i, f);
  }
  
  public zzbkd[] zzon(int paramInt)
  {
    return new zzbkd[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbke.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */