package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbhl
  implements Parcelable.Creator<zzbhk>
{
  static void zza(zzbhk paramzzbhk, Parcel paramParcel, int paramInt)
  {
    int i = zzc.zzaV(paramParcel);
    zzc.zzc(paramParcel, 1, paramzzbhk.versionCode);
    zzc.zza(paramParcel, 2, paramzzbhk.zzbNB, paramInt, false);
    zzc.zza(paramParcel, 3, paramzzbhk.zzbNC, paramInt, false);
    zzc.zza(paramParcel, 4, paramzzbhk.zzbND, paramInt, false);
    zzc.zza(paramParcel, 5, paramzzbhk.zzbNE, paramInt, false);
    zzc.zza(paramParcel, 6, paramzzbhk.zzbNF, false);
    zzc.zza(paramParcel, 7, paramzzbhk.zzbNG);
    zzc.zza(paramParcel, 8, paramzzbhk.zzbNw, false);
    zzc.zzc(paramParcel, 9, paramzzbhk.zzbNH);
    zzc.zza(paramParcel, 10, paramzzbhk.zzbNI);
    zzc.zzc(paramParcel, 11, paramzzbhk.zzbNJ);
    zzc.zzc(paramParcel, 12, paramzzbhk.zzbNK);
    zzc.zzJ(paramParcel, i);
  }
  
  public zzbhk zzjm(Parcel paramParcel)
  {
    int n = zzb.zzaU(paramParcel);
    int m = 0;
    zzbht[] arrayOfzzbht = null;
    zzbhg localzzbhg3 = null;
    zzbhg localzzbhg2 = null;
    zzbhg localzzbhg1 = null;
    String str2 = null;
    float f = 0.0F;
    String str1 = null;
    int k = 0;
    boolean bool = false;
    int j = 0;
    int i = 0;
    while (paramParcel.dataPosition() < n)
    {
      int i1 = zzb.zzaT(paramParcel);
      switch (zzb.zzcW(i1))
      {
      default: 
        zzb.zzb(paramParcel, i1);
        break;
      case 1: 
        m = zzb.zzg(paramParcel, i1);
        break;
      case 2: 
        arrayOfzzbht = (zzbht[])zzb.zzb(paramParcel, i1, zzbht.CREATOR);
        break;
      case 3: 
        localzzbhg3 = (zzbhg)zzb.zza(paramParcel, i1, zzbhg.CREATOR);
        break;
      case 4: 
        localzzbhg2 = (zzbhg)zzb.zza(paramParcel, i1, zzbhg.CREATOR);
        break;
      case 5: 
        localzzbhg1 = (zzbhg)zzb.zza(paramParcel, i1, zzbhg.CREATOR);
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
    return new zzbhk(m, arrayOfzzbht, localzzbhg3, localzzbhg2, localzzbhg1, str2, f, str1, k, bool, j, i);
  }
  
  public zzbhk[] zznD(int paramInt)
  {
    return new zzbhk[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbhl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */