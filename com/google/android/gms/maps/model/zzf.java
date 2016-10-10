package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzf
  implements Parcelable.Creator<MapStyleOptions>
{
  static void zza(MapStyleOptions paramMapStyleOptions, Parcel paramParcel, int paramInt)
  {
    paramInt = zzb.zzcr(paramParcel);
    zzb.zzc(paramParcel, 1, paramMapStyleOptions.getVersionCode());
    zzb.zza(paramParcel, 2, paramMapStyleOptions.zzbsi(), false);
    zzb.zzaj(paramParcel, paramInt);
  }
  
  public MapStyleOptions zzos(Parcel paramParcel)
  {
    int j = zza.zzcq(paramParcel);
    int i = 0;
    String str = null;
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
        str = zza.zzq(paramParcel, k);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zza.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new MapStyleOptions(i, str);
  }
  
  public MapStyleOptions[] zzvx(int paramInt)
  {
    return new MapStyleOptions[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/zzf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */