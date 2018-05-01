package com.google.android.gms.measurement.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzaj
  implements Parcelable.Creator<UserAttributeParcel>
{
  static void zza(UserAttributeParcel paramUserAttributeParcel, Parcel paramParcel, int paramInt)
  {
    paramInt = zzb.zzcs(paramParcel);
    zzb.zzc(paramParcel, 1, paramUserAttributeParcel.versionCode);
    zzb.zza(paramParcel, 2, paramUserAttributeParcel.name, false);
    zzb.zza(paramParcel, 3, paramUserAttributeParcel.avT);
    zzb.zza(paramParcel, 4, paramUserAttributeParcel.avU, false);
    zzb.zza(paramParcel, 5, paramUserAttributeParcel.avV, false);
    zzb.zza(paramParcel, 6, paramUserAttributeParcel.Fe, false);
    zzb.zza(paramParcel, 7, paramUserAttributeParcel.arK, false);
    zzb.zza(paramParcel, 8, paramUserAttributeParcel.avW, false);
    zzb.zzaj(paramParcel, paramInt);
  }
  
  public UserAttributeParcel zzpz(Parcel paramParcel)
  {
    Double localDouble = null;
    int j = zza.zzcr(paramParcel);
    int i = 0;
    long l = 0L;
    String str1 = null;
    String str2 = null;
    Float localFloat = null;
    Long localLong = null;
    String str3 = null;
    while (paramParcel.dataPosition() < j)
    {
      int k = zza.zzcq(paramParcel);
      switch (zza.zzgu(k))
      {
      default: 
        zza.zzb(paramParcel, k);
        break;
      case 1: 
        i = zza.zzg(paramParcel, k);
        break;
      case 2: 
        str3 = zza.zzq(paramParcel, k);
        break;
      case 3: 
        l = zza.zzi(paramParcel, k);
        break;
      case 4: 
        localLong = zza.zzj(paramParcel, k);
        break;
      case 5: 
        localFloat = zza.zzm(paramParcel, k);
        break;
      case 6: 
        str2 = zza.zzq(paramParcel, k);
        break;
      case 7: 
        str1 = zza.zzq(paramParcel, k);
        break;
      case 8: 
        localDouble = zza.zzo(paramParcel, k);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zza.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new UserAttributeParcel(i, str3, l, localLong, localFloat, str2, str1, localDouble);
  }
  
  public UserAttributeParcel[] zzxf(int paramInt)
  {
    return new UserAttributeParcel[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzaj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */