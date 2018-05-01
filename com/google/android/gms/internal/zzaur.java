package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzaur
  implements Parcelable.Creator<zzauq>
{
  static void zza(zzauq paramzzauq, Parcel paramParcel, int paramInt)
  {
    paramInt = zzc.zzaZ(paramParcel);
    zzc.zzc(paramParcel, 1, paramzzauq.versionCode);
    zzc.zza(paramParcel, 2, paramzzauq.name, false);
    zzc.zza(paramParcel, 3, paramzzauq.zzbwf);
    zzc.zza(paramParcel, 4, paramzzauq.zzbwg, false);
    zzc.zza(paramParcel, 5, paramzzauq.zzbwh, false);
    zzc.zza(paramParcel, 6, paramzzauq.zzaGV, false);
    zzc.zza(paramParcel, 7, paramzzauq.zzbqW, false);
    zzc.zza(paramParcel, 8, paramzzauq.zzbwi, false);
    zzc.zzJ(paramParcel, paramInt);
  }
  
  public zzauq zzhU(Parcel paramParcel)
  {
    Double localDouble = null;
    int j = zzb.zzaY(paramParcel);
    int i = 0;
    long l = 0L;
    String str1 = null;
    String str2 = null;
    Float localFloat = null;
    Long localLong = null;
    String str3 = null;
    while (paramParcel.dataPosition() < j)
    {
      int k = zzb.zzaX(paramParcel);
      switch (zzb.zzdc(k))
      {
      default: 
        zzb.zzb(paramParcel, k);
        break;
      case 1: 
        i = zzb.zzg(paramParcel, k);
        break;
      case 2: 
        str3 = zzb.zzq(paramParcel, k);
        break;
      case 3: 
        l = zzb.zzi(paramParcel, k);
        break;
      case 4: 
        localLong = zzb.zzj(paramParcel, k);
        break;
      case 5: 
        localFloat = zzb.zzm(paramParcel, k);
        break;
      case 6: 
        str2 = zzb.zzq(paramParcel, k);
        break;
      case 7: 
        str1 = zzb.zzq(paramParcel, k);
        break;
      case 8: 
        localDouble = zzb.zzo(paramParcel, k);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zzb.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new zzauq(i, str3, l, localLong, localFloat, str2, str1, localDouble);
  }
  
  public zzauq[] zzlE(int paramInt)
  {
    return new zzauq[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaur.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */