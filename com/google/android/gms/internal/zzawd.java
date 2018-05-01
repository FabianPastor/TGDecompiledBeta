package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzawd
  implements Parcelable.Creator<zzawc>
{
  static void zza(zzawc paramzzawc, Parcel paramParcel, int paramInt)
  {
    paramInt = zzc.zzaV(paramParcel);
    zzc.zzc(paramParcel, 1, paramzzawc.mVersionCode);
    zzc.zza(paramParcel, 2, paramzzawc.name, false);
    zzc.zza(paramParcel, 3, paramzzawc.zzbzt);
    zzc.zza(paramParcel, 4, paramzzawc.zzbgG);
    zzc.zza(paramParcel, 5, paramzzawc.zzbgI);
    zzc.zza(paramParcel, 6, paramzzawc.zzaFy, false);
    zzc.zza(paramParcel, 7, paramzzawc.zzbzu, false);
    zzc.zzc(paramParcel, 8, paramzzawc.zzbzv);
    zzc.zzc(paramParcel, 9, paramzzawc.zzbzw);
    zzc.zzJ(paramParcel, paramInt);
  }
  
  public zzawc zziu(Parcel paramParcel)
  {
    byte[] arrayOfByte = null;
    int i = 0;
    int m = zzb.zzaU(paramParcel);
    long l = 0L;
    double d = 0.0D;
    int j = 0;
    String str1 = null;
    boolean bool = false;
    String str2 = null;
    int k = 0;
    while (paramParcel.dataPosition() < m)
    {
      int n = zzb.zzaT(paramParcel);
      switch (zzb.zzcW(n))
      {
      default: 
        zzb.zzb(paramParcel, n);
        break;
      case 1: 
        k = zzb.zzg(paramParcel, n);
        break;
      case 2: 
        str2 = zzb.zzq(paramParcel, n);
        break;
      case 3: 
        l = zzb.zzi(paramParcel, n);
        break;
      case 4: 
        bool = zzb.zzc(paramParcel, n);
        break;
      case 5: 
        d = zzb.zzn(paramParcel, n);
        break;
      case 6: 
        str1 = zzb.zzq(paramParcel, n);
        break;
      case 7: 
        arrayOfByte = zzb.zzt(paramParcel, n);
        break;
      case 8: 
        j = zzb.zzg(paramParcel, n);
        break;
      case 9: 
        i = zzb.zzg(paramParcel, n);
      }
    }
    if (paramParcel.dataPosition() != m) {
      throw new zzb.zza(37 + "Overread allowed size end=" + m, paramParcel);
    }
    return new zzawc(k, str2, l, bool, d, str1, arrayOfByte, j, i);
  }
  
  public zzawc[] zzmm(int paramInt)
  {
    return new zzawc[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzawd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */