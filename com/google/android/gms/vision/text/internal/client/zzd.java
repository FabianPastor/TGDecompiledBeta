package com.google.android.gms.vision.text.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzd
  implements Parcelable.Creator<LineBoxParcel>
{
  static void zza(LineBoxParcel paramLineBoxParcel, Parcel paramParcel, int paramInt)
  {
    int i = zzb.zzcr(paramParcel);
    zzb.zzc(paramParcel, 1, paramLineBoxParcel.versionCode);
    zzb.zza(paramParcel, 2, paramLineBoxParcel.aLF, paramInt, false);
    zzb.zza(paramParcel, 3, paramLineBoxParcel.aLG, paramInt, false);
    zzb.zza(paramParcel, 4, paramLineBoxParcel.aLH, paramInt, false);
    zzb.zza(paramParcel, 5, paramLineBoxParcel.aLI, paramInt, false);
    zzb.zza(paramParcel, 6, paramLineBoxParcel.aLJ, false);
    zzb.zza(paramParcel, 7, paramLineBoxParcel.aLK);
    zzb.zza(paramParcel, 8, paramLineBoxParcel.aLz, false);
    zzb.zzc(paramParcel, 9, paramLineBoxParcel.aLL);
    zzb.zza(paramParcel, 10, paramLineBoxParcel.aLM);
    zzb.zzc(paramParcel, 11, paramLineBoxParcel.aLN);
    zzb.zzc(paramParcel, 12, paramLineBoxParcel.aLO);
    zzb.zzaj(paramParcel, i);
  }
  
  public LineBoxParcel[] zzaby(int paramInt)
  {
    return new LineBoxParcel[paramInt];
  }
  
  public LineBoxParcel zzth(Parcel paramParcel)
  {
    int n = zza.zzcq(paramParcel);
    int m = 0;
    WordBoxParcel[] arrayOfWordBoxParcel = null;
    BoundingBoxParcel localBoundingBoxParcel3 = null;
    BoundingBoxParcel localBoundingBoxParcel2 = null;
    BoundingBoxParcel localBoundingBoxParcel1 = null;
    String str2 = null;
    float f = 0.0F;
    String str1 = null;
    int k = 0;
    boolean bool = false;
    int j = 0;
    int i = 0;
    while (paramParcel.dataPosition() < n)
    {
      int i1 = zza.zzcp(paramParcel);
      switch (zza.zzgv(i1))
      {
      default: 
        zza.zzb(paramParcel, i1);
        break;
      case 1: 
        m = zza.zzg(paramParcel, i1);
        break;
      case 2: 
        arrayOfWordBoxParcel = (WordBoxParcel[])zza.zzb(paramParcel, i1, WordBoxParcel.CREATOR);
        break;
      case 3: 
        localBoundingBoxParcel3 = (BoundingBoxParcel)zza.zza(paramParcel, i1, BoundingBoxParcel.CREATOR);
        break;
      case 4: 
        localBoundingBoxParcel2 = (BoundingBoxParcel)zza.zza(paramParcel, i1, BoundingBoxParcel.CREATOR);
        break;
      case 5: 
        localBoundingBoxParcel1 = (BoundingBoxParcel)zza.zza(paramParcel, i1, BoundingBoxParcel.CREATOR);
        break;
      case 6: 
        str2 = zza.zzq(paramParcel, i1);
        break;
      case 7: 
        f = zza.zzl(paramParcel, i1);
        break;
      case 8: 
        str1 = zza.zzq(paramParcel, i1);
        break;
      case 9: 
        k = zza.zzg(paramParcel, i1);
        break;
      case 10: 
        bool = zza.zzc(paramParcel, i1);
        break;
      case 11: 
        j = zza.zzg(paramParcel, i1);
        break;
      case 12: 
        i = zza.zzg(paramParcel, i1);
      }
    }
    if (paramParcel.dataPosition() != n) {
      throw new zza.zza(37 + "Overread allowed size end=" + n, paramParcel);
    }
    return new LineBoxParcel(m, arrayOfWordBoxParcel, localBoundingBoxParcel3, localBoundingBoxParcel2, localBoundingBoxParcel1, str2, f, str1, k, bool, j, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/text/internal/client/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */