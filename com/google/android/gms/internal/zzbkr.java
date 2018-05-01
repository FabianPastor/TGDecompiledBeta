package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbkr
  implements Parcelable.Creator<zzbkq>
{
  static void zza(zzbkq paramzzbkq, Parcel paramParcel, int paramInt)
  {
    int i = zzc.zzaZ(paramParcel);
    zzc.zzc(paramParcel, 1, paramzzbkq.versionCode);
    zzc.zza(paramParcel, 2, paramzzbkq.zzbPK, paramInt, false);
    zzc.zza(paramParcel, 3, paramzzbkq.zzbPA, paramInt, false);
    zzc.zza(paramParcel, 4, paramzzbkq.zzbPB, paramInt, false);
    zzc.zza(paramParcel, 5, paramzzbkq.zzbPD, false);
    zzc.zza(paramParcel, 6, paramzzbkq.zzbPE);
    zzc.zza(paramParcel, 7, paramzzbkq.zzbPu, false);
    zzc.zza(paramParcel, 8, paramzzbkq.zzbPL);
    zzc.zzJ(paramParcel, i);
  }
  
  public zzbkq zzjW(Parcel paramParcel)
  {
    boolean bool = false;
    String str1 = null;
    int j = zzb.zzaY(paramParcel);
    float f = 0.0F;
    String str2 = null;
    zzbkd localzzbkd1 = null;
    zzbkd localzzbkd2 = null;
    zzbkl[] arrayOfzzbkl = null;
    int i = 0;
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
        arrayOfzzbkl = (zzbkl[])zzb.zzb(paramParcel, k, zzbkl.CREATOR);
        break;
      case 3: 
        localzzbkd2 = (zzbkd)zzb.zza(paramParcel, k, zzbkd.CREATOR);
        break;
      case 4: 
        localzzbkd1 = (zzbkd)zzb.zza(paramParcel, k, zzbkd.CREATOR);
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
    return new zzbkq(i, arrayOfzzbkl, localzzbkd2, localzzbkd1, str2, f, str1, bool);
  }
  
  public zzbkq[] zzos(int paramInt)
  {
    return new zzbkq[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbkr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */