package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbhu
  implements Parcelable.Creator<zzbht>
{
  static void zza(zzbht paramzzbht, Parcel paramParcel, int paramInt)
  {
    int i = zzc.zzaV(paramParcel);
    zzc.zzc(paramParcel, 1, paramzzbht.versionCode);
    zzc.zza(paramParcel, 2, paramzzbht.zzbNM, paramInt, false);
    zzc.zza(paramParcel, 3, paramzzbht.zzbNC, paramInt, false);
    zzc.zza(paramParcel, 4, paramzzbht.zzbND, paramInt, false);
    zzc.zza(paramParcel, 5, paramzzbht.zzbNF, false);
    zzc.zza(paramParcel, 6, paramzzbht.zzbNG);
    zzc.zza(paramParcel, 7, paramzzbht.zzbNw, false);
    zzc.zza(paramParcel, 8, paramzzbht.zzbNN);
    zzc.zzJ(paramParcel, i);
  }
  
  public zzbht zzjq(Parcel paramParcel)
  {
    boolean bool = false;
    String str1 = null;
    int j = zzb.zzaU(paramParcel);
    float f = 0.0F;
    String str2 = null;
    zzbhg localzzbhg1 = null;
    zzbhg localzzbhg2 = null;
    zzbho[] arrayOfzzbho = null;
    int i = 0;
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
        arrayOfzzbho = (zzbho[])zzb.zzb(paramParcel, k, zzbho.CREATOR);
        break;
      case 3: 
        localzzbhg2 = (zzbhg)zzb.zza(paramParcel, k, zzbhg.CREATOR);
        break;
      case 4: 
        localzzbhg1 = (zzbhg)zzb.zza(paramParcel, k, zzbhg.CREATOR);
        break;
      case 5: 
        str2 = zzb.zzq(paramParcel, k);
        break;
      case 6: 
        f = zzb.zzl(paramParcel, k);
        break;
      case 7: 
        str1 = zzb.zzq(paramParcel, k);
        break;
      case 8: 
        bool = zzb.zzc(paramParcel, k);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zzb.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new zzbht(i, arrayOfzzbho, localzzbhg2, localzzbhg1, str2, f, str1, bool);
  }
  
  public zzbht[] zznH(int paramInt)
  {
    return new zzbht[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbhu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */