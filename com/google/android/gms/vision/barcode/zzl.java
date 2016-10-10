package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzl
  implements Parcelable.Creator<Barcode.UrlBookmark>
{
  static void zza(Barcode.UrlBookmark paramUrlBookmark, Parcel paramParcel, int paramInt)
  {
    paramInt = zzb.zzcr(paramParcel);
    zzb.zzc(paramParcel, 1, paramUrlBookmark.versionCode);
    zzb.zza(paramParcel, 2, paramUrlBookmark.title, false);
    zzb.zza(paramParcel, 3, paramUrlBookmark.url, false);
    zzb.zzaj(paramParcel, paramInt);
  }
  
  public Barcode.UrlBookmark[] zzabo(int paramInt)
  {
    return new Barcode.UrlBookmark[paramInt];
  }
  
  public Barcode.UrlBookmark zzsz(Parcel paramParcel)
  {
    String str2 = null;
    int j = zza.zzcq(paramParcel);
    int i = 0;
    String str1 = null;
    while (paramParcel.dataPosition() < j)
    {
      int k = zza.zzcp(paramParcel);
      switch (zza.zzgv(k))
      {
      default: 
        zza.zzb(paramParcel, k);
        break;
      case 1: 
        i = zza.zzg(paramParcel, k);
        break;
      case 2: 
        str1 = zza.zzq(paramParcel, k);
        break;
      case 3: 
        str2 = zza.zzq(paramParcel, k);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zza.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new Barcode.UrlBookmark(i, str1, str2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/barcode/zzl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */