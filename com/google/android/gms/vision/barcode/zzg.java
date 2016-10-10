package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzg
  implements Parcelable.Creator<Barcode.Email>
{
  static void zza(Barcode.Email paramEmail, Parcel paramParcel, int paramInt)
  {
    paramInt = zzb.zzcr(paramParcel);
    zzb.zzc(paramParcel, 1, paramEmail.versionCode);
    zzb.zzc(paramParcel, 2, paramEmail.type);
    zzb.zza(paramParcel, 3, paramEmail.address, false);
    zzb.zza(paramParcel, 4, paramEmail.subject, false);
    zzb.zza(paramParcel, 5, paramEmail.body, false);
    zzb.zzaj(paramParcel, paramInt);
  }
  
  public Barcode.Email[] zzabj(int paramInt)
  {
    return new Barcode.Email[paramInt];
  }
  
  public Barcode.Email zzsu(Parcel paramParcel)
  {
    int i = 0;
    String str1 = null;
    int k = zza.zzcq(paramParcel);
    String str2 = null;
    String str3 = null;
    int j = 0;
    while (paramParcel.dataPosition() < k)
    {
      int m = zza.zzcp(paramParcel);
      switch (zza.zzgv(m))
      {
      default: 
        zza.zzb(paramParcel, m);
        break;
      case 1: 
        j = zza.zzg(paramParcel, m);
        break;
      case 2: 
        i = zza.zzg(paramParcel, m);
        break;
      case 3: 
        str3 = zza.zzq(paramParcel, m);
        break;
      case 4: 
        str2 = zza.zzq(paramParcel, m);
        break;
      case 5: 
        str1 = zza.zzq(paramParcel, m);
      }
    }
    if (paramParcel.dataPosition() != k) {
      throw new zza.zza(37 + "Overread allowed size end=" + k, paramParcel);
    }
    return new Barcode.Email(j, i, str3, str2, str1);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/barcode/zzg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */