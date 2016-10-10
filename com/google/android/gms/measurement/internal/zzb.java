package com.google.android.gms.measurement.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;

public class zzb
  implements Parcelable.Creator<AppMetadata>
{
  static void zza(AppMetadata paramAppMetadata, Parcel paramParcel, int paramInt)
  {
    paramInt = com.google.android.gms.common.internal.safeparcel.zzb.zzcr(paramParcel);
    com.google.android.gms.common.internal.safeparcel.zzb.zzc(paramParcel, 1, paramAppMetadata.versionCode);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 2, paramAppMetadata.packageName, false);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 3, paramAppMetadata.anQ, false);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 4, paramAppMetadata.afY, false);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 5, paramAppMetadata.anR, false);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 6, paramAppMetadata.anS);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 7, paramAppMetadata.anT);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 8, paramAppMetadata.anU, false);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 9, paramAppMetadata.anV);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 10, paramAppMetadata.anW);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 11, paramAppMetadata.anX);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 12, paramAppMetadata.anY, false);
    com.google.android.gms.common.internal.safeparcel.zzb.zzaj(paramParcel, paramInt);
  }
  
  public AppMetadata zzpe(Parcel paramParcel)
  {
    int j = zza.zzcq(paramParcel);
    int i = 0;
    String str6 = null;
    String str5 = null;
    String str4 = null;
    String str3 = null;
    long l3 = 0L;
    long l2 = 0L;
    String str2 = null;
    boolean bool2 = false;
    boolean bool1 = false;
    long l1 = 0L;
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
        str6 = zza.zzq(paramParcel, k);
        break;
      case 3: 
        str5 = zza.zzq(paramParcel, k);
        break;
      case 4: 
        str4 = zza.zzq(paramParcel, k);
        break;
      case 5: 
        str3 = zza.zzq(paramParcel, k);
        break;
      case 6: 
        l3 = zza.zzi(paramParcel, k);
        break;
      case 7: 
        l2 = zza.zzi(paramParcel, k);
        break;
      case 8: 
        str2 = zza.zzq(paramParcel, k);
        break;
      case 9: 
        bool2 = zza.zzc(paramParcel, k);
        break;
      case 10: 
        bool1 = zza.zzc(paramParcel, k);
        break;
      case 11: 
        l1 = zza.zzi(paramParcel, k);
        break;
      case 12: 
        str1 = zza.zzq(paramParcel, k);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zza.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new AppMetadata(i, str6, str5, str4, str3, l3, l2, str2, bool2, bool1, l1, str1);
  }
  
  public AppMetadata[] zzwk(int paramInt)
  {
    return new AppMetadata[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */