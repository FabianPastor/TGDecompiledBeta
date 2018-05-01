package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzawb
  implements Parcelable.Creator<zzawa>
{
  static void zza(zzawa paramzzawa, Parcel paramParcel, int paramInt)
  {
    int i = zzc.zzaV(paramParcel);
    zzc.zzc(paramParcel, 1, paramzzawa.mVersionCode);
    zzc.zzc(paramParcel, 2, paramzzawa.zzbzp);
    zzc.zza(paramParcel, 3, paramzzawa.zzbzq, paramInt, false);
    zzc.zza(paramParcel, 4, paramzzawa.zzbzr, false);
    zzc.zzJ(paramParcel, i);
  }
  
  public zzawa zzit(Parcel paramParcel)
  {
    String[] arrayOfString = null;
    int j = 0;
    int m = zzb.zzaU(paramParcel);
    zzawc[] arrayOfzzawc = null;
    int i = 0;
    if (paramParcel.dataPosition() < m)
    {
      int k = zzb.zzaT(paramParcel);
      switch (zzb.zzcW(k))
      {
      default: 
        zzb.zzb(paramParcel, k);
        k = j;
        j = i;
        i = k;
      }
      for (;;)
      {
        k = j;
        j = i;
        i = k;
        break;
        k = zzb.zzg(paramParcel, k);
        i = j;
        j = k;
        continue;
        k = zzb.zzg(paramParcel, k);
        j = i;
        i = k;
        continue;
        arrayOfzzawc = (zzawc[])zzb.zzb(paramParcel, k, zzawc.CREATOR);
        k = i;
        i = j;
        j = k;
        continue;
        arrayOfString = zzb.zzC(paramParcel, k);
        k = i;
        i = j;
        j = k;
      }
    }
    if (paramParcel.dataPosition() != m) {
      throw new zzb.zza(37 + "Overread allowed size end=" + m, paramParcel);
    }
    return new zzawa(i, j, arrayOfzzawc, arrayOfString);
  }
  
  public zzawa[] zzml(int paramInt)
  {
    return new zzawa[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzawb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */