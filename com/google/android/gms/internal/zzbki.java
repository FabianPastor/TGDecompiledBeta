package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbki
  implements Parcelable.Creator<zzbkh>
{
  static void zza(zzbkh paramzzbkh, Parcel paramParcel, int paramInt)
  {
    int i = zzc.zzaZ(paramParcel);
    zzc.zzc(paramParcel, 1, paramzzbkh.versionCode);
    zzc.zza(paramParcel, 2, paramzzbkh.zzbPz, paramInt, false);
    zzc.zza(paramParcel, 3, paramzzbkh.zzbPA, paramInt, false);
    zzc.zza(paramParcel, 4, paramzzbkh.zzbPB, paramInt, false);
    zzc.zza(paramParcel, 5, paramzzbkh.zzbPC, paramInt, false);
    zzc.zza(paramParcel, 6, paramzzbkh.zzbPD, false);
    zzc.zza(paramParcel, 7, paramzzbkh.zzbPE);
    zzc.zza(paramParcel, 8, paramzzbkh.zzbPu, false);
    zzc.zzc(paramParcel, 9, paramzzbkh.zzbPF);
    zzc.zza(paramParcel, 10, paramzzbkh.zzbPG);
    zzc.zzc(paramParcel, 11, paramzzbkh.zzbPH);
    zzc.zzc(paramParcel, 12, paramzzbkh.zzbPI);
    zzc.zzJ(paramParcel, i);
  }
  
  public zzbkh zzjS(Parcel paramParcel)
  {
    int n = zzb.zzaY(paramParcel);
    int m = 0;
    zzbkq[] arrayOfzzbkq = null;
    zzbkd localzzbkd3 = null;
    zzbkd localzzbkd2 = null;
    zzbkd localzzbkd1 = null;
    String str2 = null;
    float f = 0.0F;
    String str1 = null;
    int k = 0;
    boolean bool = false;
    int j = 0;
    int i = 0;
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
        arrayOfzzbkq = (zzbkq[])zzb.zzb(paramParcel, i1, zzbkq.CREATOR);
        break;
      case 3: 
        localzzbkd3 = (zzbkd)zzb.zza(paramParcel, i1, zzbkd.CREATOR);
        break;
      case 4: 
        localzzbkd2 = (zzbkd)zzb.zza(paramParcel, i1, zzbkd.CREATOR);
        break;
      case 5: 
        localzzbkd1 = (zzbkd)zzb.zza(paramParcel, i1, zzbkd.CREATOR);
        break;
      case 6: 
        str2 = zzb.zzq(paramParcel, i1);
        break;
      case 7: 
        f = zzb.zzl(paramParcel, i1);
        break;
      case 8: 
        str1 = zzb.zzq(paramParcel, i1);
        break;
      case 9: 
        k = zzb.zzg(paramParcel, i1);
        break;
      case 10: 
        bool = zzb.zzc(paramParcel, i1);
        break;
      case 11: 
        j = zzb.zzg(paramParcel, i1);
        break;
      case 12: 
        i = zzb.zzg(paramParcel, i1);
      }
    }
    if (paramParcel.dataPosition() != n) {
      throw new zzb.zza(37 + "Overread allowed size end=" + n, paramParcel);
    }
    return new zzbkh(m, arrayOfzzbkq, localzzbkd3, localzzbkd2, localzzbkd1, str2, f, str1, k, bool, j, i);
  }
  
  public zzbkh[] zzoo(int paramInt)
  {
    return new zzbkh[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbki.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */