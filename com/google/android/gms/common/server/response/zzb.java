package com.google.android.gms.common.server.response;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;

public class zzb
  implements Parcelable.Creator<FieldMappingDictionary.FieldMapPair>
{
  static void zza(FieldMappingDictionary.FieldMapPair paramFieldMapPair, Parcel paramParcel, int paramInt)
  {
    int i = com.google.android.gms.common.internal.safeparcel.zzb.zzcs(paramParcel);
    com.google.android.gms.common.internal.safeparcel.zzb.zzc(paramParcel, 1, paramFieldMapPair.versionCode);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 2, paramFieldMapPair.zzcb, false);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 3, paramFieldMapPair.Fu, paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzb.zzaj(paramParcel, i);
  }
  
  public FieldMappingDictionary.FieldMapPair zzcy(Parcel paramParcel)
  {
    FastJsonResponse.Field localField = null;
    int j = zza.zzcr(paramParcel);
    int i = 0;
    String str = null;
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
        str = zza.zzq(paramParcel, k);
        break;
      case 3: 
        localField = (FastJsonResponse.Field)zza.zza(paramParcel, k, FastJsonResponse.Field.CREATOR);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zza.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new FieldMappingDictionary.FieldMapPair(i, str, localField);
  }
  
  public FieldMappingDictionary.FieldMapPair[] zzhb(int paramInt)
  {
    return new FieldMappingDictionary.FieldMapPair[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/server/response/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */