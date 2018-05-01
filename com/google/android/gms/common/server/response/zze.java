package com.google.android.gms.common.server.response;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zze
  implements Parcelable.Creator<SafeParcelResponse>
{
  static void zza(SafeParcelResponse paramSafeParcelResponse, Parcel paramParcel, int paramInt)
  {
    int i = zzb.zzcs(paramParcel);
    zzb.zzc(paramParcel, 1, paramSafeParcelResponse.getVersionCode());
    zzb.zza(paramParcel, 2, paramSafeParcelResponse.zzaxp(), false);
    zzb.zza(paramParcel, 3, paramSafeParcelResponse.zzaxq(), paramInt, false);
    zzb.zzaj(paramParcel, i);
  }
  
  public SafeParcelResponse zzdb(Parcel paramParcel)
  {
    FieldMappingDictionary localFieldMappingDictionary = null;
    int j = zza.zzcr(paramParcel);
    int i = 0;
    Parcel localParcel = null;
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
        localParcel = zza.zzaf(paramParcel, k);
        break;
      case 3: 
        localFieldMappingDictionary = (FieldMappingDictionary)zza.zza(paramParcel, k, FieldMappingDictionary.CREATOR);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zza.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new SafeParcelResponse(i, localParcel, localFieldMappingDictionary);
  }
  
  public SafeParcelResponse[] zzhe(int paramInt)
  {
    return new SafeParcelResponse[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/server/response/zze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */