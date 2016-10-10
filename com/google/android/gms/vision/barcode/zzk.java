package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzk
  implements Parcelable.Creator<Barcode.Sms>
{
  static void zza(Barcode.Sms paramSms, Parcel paramParcel, int paramInt)
  {
    paramInt = zzb.zzcr(paramParcel);
    zzb.zzc(paramParcel, 1, paramSms.versionCode);
    zzb.zza(paramParcel, 2, paramSms.message, false);
    zzb.zza(paramParcel, 3, paramSms.phoneNumber, false);
    zzb.zzaj(paramParcel, paramInt);
  }
  
  public Barcode.Sms[] zzabn(int paramInt)
  {
    return new Barcode.Sms[paramInt];
  }
  
  public Barcode.Sms zzsy(Parcel paramParcel)
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
    return new Barcode.Sms(i, str1, str2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/barcode/zzk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */