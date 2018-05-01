package com.google.android.gms.vision.text.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzf
  implements Parcelable.Creator<SymbolBoxParcel>
{
  static void zza(SymbolBoxParcel paramSymbolBoxParcel, Parcel paramParcel, int paramInt)
  {
    paramInt = zzb.zzcs(paramParcel);
    zzb.zzc(paramParcel, 1, paramSymbolBoxParcel.versionCode);
    zzb.zzaj(paramParcel, paramInt);
  }
  
  public SymbolBoxParcel[] zzabq(int paramInt)
  {
    return new SymbolBoxParcel[paramInt];
  }
  
  public SymbolBoxParcel zzsz(Parcel paramParcel)
  {
    int j = zza.zzcr(paramParcel);
    int i = 0;
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
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zza.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new SymbolBoxParcel(i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/text/internal/client/zzf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */