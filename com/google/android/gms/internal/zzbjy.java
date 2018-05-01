package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbjy
  implements Parcelable.Creator<zzbjx>
{
  static void zza(zzbjx paramzzbjx, Parcel paramParcel, int paramInt)
  {
    paramInt = zzc.zzaZ(paramParcel);
    zzc.zzc(paramParcel, 1, paramzzbjx.versionCode);
    zzc.zza(paramParcel, 2, paramzzbjx.x);
    zzc.zza(paramParcel, 3, paramzzbjx.y);
    zzc.zzc(paramParcel, 4, paramzzbjx.type);
    zzc.zzJ(paramParcel, paramInt);
  }
  
  public zzbjx zzjP(Parcel paramParcel)
  {
    int j = 0;
    float f2 = 0.0F;
    int k = zzb.zzaY(paramParcel);
    float f1 = 0.0F;
    int i = 0;
    while (paramParcel.dataPosition() < k)
    {
      int m = zzb.zzaX(paramParcel);
      switch (zzb.zzdc(m))
      {
      default: 
        zzb.zzb(paramParcel, m);
        break;
      case 1: 
        i = zzb.zzg(paramParcel, m);
        break;
      case 2: 
        f1 = zzb.zzl(paramParcel, m);
        break;
      case 3: 
        f2 = zzb.zzl(paramParcel, m);
        break;
      case 4: 
        j = zzb.zzg(paramParcel, m);
      }
    }
    if (paramParcel.dataPosition() != k) {
      throw new zzb.zza(37 + "Overread allowed size end=" + k, paramParcel);
    }
    return new zzbjx(i, f1, f2, j);
  }
  
  public zzbjx[] zzok(int paramInt)
  {
    return new zzbjx[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbjy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */