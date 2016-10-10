package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzf
  implements Parcelable.Creator<Barcode.DriverLicense>
{
  static void zza(Barcode.DriverLicense paramDriverLicense, Parcel paramParcel, int paramInt)
  {
    paramInt = zzb.zzcr(paramParcel);
    zzb.zzc(paramParcel, 1, paramDriverLicense.versionCode);
    zzb.zza(paramParcel, 2, paramDriverLicense.documentType, false);
    zzb.zza(paramParcel, 3, paramDriverLicense.firstName, false);
    zzb.zza(paramParcel, 4, paramDriverLicense.middleName, false);
    zzb.zza(paramParcel, 5, paramDriverLicense.lastName, false);
    zzb.zza(paramParcel, 6, paramDriverLicense.gender, false);
    zzb.zza(paramParcel, 7, paramDriverLicense.addressStreet, false);
    zzb.zza(paramParcel, 8, paramDriverLicense.addressCity, false);
    zzb.zza(paramParcel, 9, paramDriverLicense.addressState, false);
    zzb.zza(paramParcel, 10, paramDriverLicense.addressZip, false);
    zzb.zza(paramParcel, 11, paramDriverLicense.licenseNumber, false);
    zzb.zza(paramParcel, 12, paramDriverLicense.issueDate, false);
    zzb.zza(paramParcel, 13, paramDriverLicense.expiryDate, false);
    zzb.zza(paramParcel, 14, paramDriverLicense.birthDate, false);
    zzb.zza(paramParcel, 15, paramDriverLicense.issuingCountry, false);
    zzb.zzaj(paramParcel, paramInt);
  }
  
  public Barcode.DriverLicense[] zzabi(int paramInt)
  {
    return new Barcode.DriverLicense[paramInt];
  }
  
  public Barcode.DriverLicense zzst(Parcel paramParcel)
  {
    int j = zza.zzcq(paramParcel);
    int i = 0;
    String str14 = null;
    String str13 = null;
    String str12 = null;
    String str11 = null;
    String str10 = null;
    String str9 = null;
    String str8 = null;
    String str7 = null;
    String str6 = null;
    String str5 = null;
    String str4 = null;
    String str3 = null;
    String str2 = null;
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
        str14 = zza.zzq(paramParcel, k);
        break;
      case 3: 
        str13 = zza.zzq(paramParcel, k);
        break;
      case 4: 
        str12 = zza.zzq(paramParcel, k);
        break;
      case 5: 
        str11 = zza.zzq(paramParcel, k);
        break;
      case 6: 
        str10 = zza.zzq(paramParcel, k);
        break;
      case 7: 
        str9 = zza.zzq(paramParcel, k);
        break;
      case 8: 
        str8 = zza.zzq(paramParcel, k);
        break;
      case 9: 
        str7 = zza.zzq(paramParcel, k);
        break;
      case 10: 
        str6 = zza.zzq(paramParcel, k);
        break;
      case 11: 
        str5 = zza.zzq(paramParcel, k);
        break;
      case 12: 
        str4 = zza.zzq(paramParcel, k);
        break;
      case 13: 
        str3 = zza.zzq(paramParcel, k);
        break;
      case 14: 
        str2 = zza.zzq(paramParcel, k);
        break;
      case 15: 
        str1 = zza.zzq(paramParcel, k);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zza.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new Barcode.DriverLicense(i, str14, str13, str12, str11, str10, str9, str8, str7, str6, str5, str4, str3, str2, str1);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/barcode/zzf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */