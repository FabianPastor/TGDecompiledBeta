package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzm
  implements Parcelable.Creator<Barcode.WiFi>
{
  static void zza(Barcode.WiFi paramWiFi, Parcel paramParcel, int paramInt)
  {
    paramInt = zzb.zzcr(paramParcel);
    zzb.zzc(paramParcel, 1, paramWiFi.versionCode);
    zzb.zza(paramParcel, 2, paramWiFi.ssid, false);
    zzb.zza(paramParcel, 3, paramWiFi.password, false);
    zzb.zzc(paramParcel, 4, paramWiFi.encryptionType);
    zzb.zzaj(paramParcel, paramInt);
  }
  
  public Barcode.WiFi[] zzabp(int paramInt)
  {
    return new Barcode.WiFi[paramInt];
  }
  
  public Barcode.WiFi zzta(Parcel paramParcel)
  {
    String str2 = null;
    int j = 0;
    int k = zza.zzcq(paramParcel);
    String str1 = null;
    int i = 0;
    while (paramParcel.dataPosition() < k)
    {
      int m = zza.zzcp(paramParcel);
      switch (zza.zzgv(m))
      {
      default: 
        zza.zzb(paramParcel, m);
        break;
      case 1: 
        i = zza.zzg(paramParcel, m);
        break;
      case 2: 
        str1 = zza.zzq(paramParcel, m);
        break;
      case 3: 
        str2 = zza.zzq(paramParcel, m);
        break;
      case 4: 
        j = zza.zzg(paramParcel, m);
      }
    }
    if (paramParcel.dataPosition() != k) {
      throw new zza.zza(37 + "Overread allowed size end=" + k, paramParcel);
    }
    return new Barcode.WiFi(i, str1, str2, j);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/barcode/zzm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */