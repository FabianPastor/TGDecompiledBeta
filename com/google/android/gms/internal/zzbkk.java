package com.google.android.gms.internal;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbkk
  implements Parcelable.Creator<zzbkj>
{
  static void zza(zzbkj paramzzbkj, Parcel paramParcel, int paramInt)
  {
    int i = zzc.zzaZ(paramParcel);
    zzc.zzc(paramParcel, 1, paramzzbkj.versionCode);
    zzc.zza(paramParcel, 2, paramzzbkj.zzbPJ, paramInt, false);
    zzc.zzJ(paramParcel, i);
  }
  
  public zzbkj zzjT(Parcel paramParcel)
  {
    int j = zzb.zzaY(paramParcel);
    int i = 0;
    Rect localRect = null;
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
        localRect = (Rect)zzb.zza(paramParcel, k, Rect.CREATOR);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zzb.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new zzbkj(i, localRect);
  }
  
  public zzbkj[] zzop(int paramInt)
  {
    return new zzbkj[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbkk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */