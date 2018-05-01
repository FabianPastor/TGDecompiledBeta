package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzasr
  implements Parcelable.Creator<zzasq>
{
  static void zza(zzasq paramzzasq, Parcel paramParcel, int paramInt)
  {
    paramInt = zzc.zzaV(paramParcel);
    zzc.zzc(paramParcel, 1, paramzzasq.versionCode);
    zzc.zza(paramParcel, 2, paramzzasq.packageName, false);
    zzc.zza(paramParcel, 3, paramzzasq.zzbqf, false);
    zzc.zza(paramParcel, 4, paramzzasq.zzbhg, false);
    zzc.zza(paramParcel, 5, paramzzasq.zzbqg, false);
    zzc.zza(paramParcel, 6, paramzzasq.zzbqh);
    zzc.zza(paramParcel, 7, paramzzasq.zzbqi);
    zzc.zza(paramParcel, 8, paramzzasq.zzbqj, false);
    zzc.zza(paramParcel, 9, paramzzasq.zzbqk);
    zzc.zza(paramParcel, 10, paramzzasq.zzbql);
    zzc.zza(paramParcel, 11, paramzzasq.zzbqm);
    zzc.zza(paramParcel, 12, paramzzasq.zzbqn, false);
    zzc.zzJ(paramParcel, paramInt);
  }
  
  public zzasq zzhK(Parcel paramParcel)
  {
    int j = zzb.zzaU(paramParcel);
    int i = 0;
    String str6 = null;
    String str5 = null;
    String str4 = null;
    String str3 = null;
    long l3 = 0L;
    long l2 = 0L;
    String str2 = null;
    boolean bool2 = false;
    boolean bool1 = false;
    long l1 = 0L;
    String str1 = null;
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
        str6 = zzb.zzq(paramParcel, k);
        break;
      case 3: 
        str5 = zzb.zzq(paramParcel, k);
        break;
      case 4: 
        str4 = zzb.zzq(paramParcel, k);
        break;
      case 5: 
        str3 = zzb.zzq(paramParcel, k);
        break;
      case 6: 
        l3 = zzb.zzi(paramParcel, k);
        break;
      case 7: 
        l2 = zzb.zzi(paramParcel, k);
        break;
      case 8: 
        str2 = zzb.zzq(paramParcel, k);
        break;
      case 9: 
        bool2 = zzb.zzc(paramParcel, k);
        break;
      case 10: 
        bool1 = zzb.zzc(paramParcel, k);
        break;
      case 11: 
        l1 = zzb.zzi(paramParcel, k);
        break;
      case 12: 
        str1 = zzb.zzq(paramParcel, k);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zzb.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new zzasq(i, str6, str5, str4, str3, l3, l2, str2, bool2, bool1, l1, str1);
  }
  
  public zzasq[] zzlp(int paramInt)
  {
    return new zzasq[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzasr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */