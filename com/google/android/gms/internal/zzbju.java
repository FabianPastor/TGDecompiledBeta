package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbju
  implements Parcelable.Creator<zzbjt>
{
  static void zza(zzbjt paramzzbjt, Parcel paramParcel, int paramInt)
  {
    paramInt = zzc.zzaZ(paramParcel);
    zzc.zzc(paramParcel, 1, paramzzbjt.versionCode);
    zzc.zzc(paramParcel, 2, paramzzbjt.mode);
    zzc.zzc(paramParcel, 3, paramzzbjt.zzbPh);
    zzc.zzc(paramParcel, 4, paramzzbjt.zzbPi);
    zzc.zza(paramParcel, 5, paramzzbjt.zzbPj);
    zzc.zza(paramParcel, 6, paramzzbjt.zzbPk);
    zzc.zza(paramParcel, 7, paramzzbjt.zzbPl);
    zzc.zzJ(paramParcel, paramInt);
  }
  
  public zzbjt zzjO(Parcel paramParcel)
  {
    boolean bool1 = false;
    int n = zzb.zzaY(paramParcel);
    float f = -1.0F;
    boolean bool2 = false;
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    while (paramParcel.dataPosition() < n)
    {
      int i1 = zzb.zzaX(paramParcel);
      switch (zzb.zzdc(i1))
      {
      default: 
        zzb.zzb(paramParcel, i1);
        break;
      case 1: 
        m = zzb.zzg(paramParcel, i1);
        break;
      case 2: 
        k = zzb.zzg(paramParcel, i1);
        break;
      case 3: 
        j = zzb.zzg(paramParcel, i1);
        break;
      case 4: 
        i = zzb.zzg(paramParcel, i1);
        break;
      case 5: 
        bool2 = zzb.zzc(paramParcel, i1);
        break;
      case 6: 
        bool1 = zzb.zzc(paramParcel, i1);
        break;
      case 7: 
        f = zzb.zzl(paramParcel, i1);
      }
    }
    if (paramParcel.dataPosition() != n) {
      throw new zzb.zza(37 + "Overread allowed size end=" + n, paramParcel);
    }
    return new zzbjt(m, k, j, i, bool2, bool1, f);
  }
  
  public zzbjt[] zzoj(int paramInt)
  {
    return new zzbjt[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbju.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */