package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzath
  implements Parcelable.Creator<zzatg>
{
  static void zza(zzatg paramzzatg, Parcel paramParcel, int paramInt)
  {
    int i = zzc.zzaZ(paramParcel);
    zzc.zzc(paramParcel, 1, paramzzatg.versionCode);
    zzc.zza(paramParcel, 2, paramzzatg.packageName, false);
    zzc.zza(paramParcel, 3, paramzzatg.zzbqW, false);
    zzc.zza(paramParcel, 4, paramzzatg.zzbqX, paramInt, false);
    zzc.zza(paramParcel, 5, paramzzatg.zzbqY);
    zzc.zza(paramParcel, 6, paramzzatg.zzbqZ);
    zzc.zza(paramParcel, 7, paramzzatg.zzbra, false);
    zzc.zza(paramParcel, 8, paramzzatg.zzbrb, paramInt, false);
    zzc.zza(paramParcel, 9, paramzzatg.zzbrc);
    zzc.zza(paramParcel, 10, paramzzatg.zzbrd, paramInt, false);
    zzc.zza(paramParcel, 11, paramzzatg.zzbre);
    zzc.zza(paramParcel, 12, paramzzatg.zzbrf, paramInt, false);
    zzc.zzJ(paramParcel, i);
  }
  
  public zzatg zzhR(Parcel paramParcel)
  {
    int j = zzb.zzaY(paramParcel);
    int i = 0;
    String str3 = null;
    String str2 = null;
    zzauq localzzauq = null;
    long l3 = 0L;
    boolean bool = false;
    String str1 = null;
    zzatq localzzatq3 = null;
    long l2 = 0L;
    zzatq localzzatq2 = null;
    long l1 = 0L;
    zzatq localzzatq1 = null;
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
        str2 = zzb.zzq(paramParcel, k);
        break;
      case 4: 
        localzzauq = (zzauq)zzb.zza(paramParcel, k, zzauq.CREATOR);
        break;
      case 5: 
        l3 = zzb.zzi(paramParcel, k);
        break;
      case 6: 
        bool = zzb.zzc(paramParcel, k);
        break;
      case 7: 
        str1 = zzb.zzq(paramParcel, k);
        break;
      case 8: 
        localzzatq3 = (zzatq)zzb.zza(paramParcel, k, zzatq.CREATOR);
        break;
      case 9: 
        l2 = zzb.zzi(paramParcel, k);
        break;
      case 10: 
        localzzatq2 = (zzatq)zzb.zza(paramParcel, k, zzatq.CREATOR);
        break;
      case 11: 
        l1 = zzb.zzi(paramParcel, k);
        break;
      case 12: 
        localzzatq1 = (zzatq)zzb.zza(paramParcel, k, zzatq.CREATOR);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zzb.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new zzatg(i, str3, str2, localzzauq, l3, bool, str1, localzzatq3, l2, localzzatq2, l1, localzzatq1);
  }
  
  public zzatg[] zzlA(int paramInt)
  {
    return new zzatg[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzath.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */