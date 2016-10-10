package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzh
  implements Parcelable.Creator<Barcode.GeoPoint>
{
  static void zza(Barcode.GeoPoint paramGeoPoint, Parcel paramParcel, int paramInt)
  {
    paramInt = zzb.zzcr(paramParcel);
    zzb.zzc(paramParcel, 1, paramGeoPoint.versionCode);
    zzb.zza(paramParcel, 2, paramGeoPoint.lat);
    zzb.zza(paramParcel, 3, paramGeoPoint.lng);
    zzb.zzaj(paramParcel, paramInt);
  }
  
  public Barcode.GeoPoint[] zzabk(int paramInt)
  {
    return new Barcode.GeoPoint[paramInt];
  }
  
  public Barcode.GeoPoint zzsv(Parcel paramParcel)
  {
    double d1 = 0.0D;
    int j = zza.zzcq(paramParcel);
    int i = 0;
    double d2 = 0.0D;
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
        d2 = zza.zzn(paramParcel, k);
        break;
      case 3: 
        d1 = zza.zzn(paramParcel, k);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zza.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new Barcode.GeoPoint(i, d2, d1);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/barcode/zzh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */