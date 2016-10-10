package com.google.android.gms.measurement.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzk
  implements Parcelable.Creator<EventParcel>
{
  static void zza(EventParcel paramEventParcel, Parcel paramParcel, int paramInt)
  {
    int i = zzb.zzcr(paramParcel);
    zzb.zzc(paramParcel, 1, paramEventParcel.versionCode);
    zzb.zza(paramParcel, 2, paramEventParcel.name, false);
    zzb.zza(paramParcel, 3, paramEventParcel.aoz, paramInt, false);
    zzb.zza(paramParcel, 4, paramEventParcel.aoA, false);
    zzb.zza(paramParcel, 5, paramEventParcel.aoB);
    zzb.zzaj(paramParcel, i);
  }
  
  public EventParcel zzpg(Parcel paramParcel)
  {
    String str1 = null;
    int j = zza.zzcq(paramParcel);
    int i = 0;
    long l = 0L;
    EventParams localEventParams = null;
    String str2 = null;
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
        str2 = zza.zzq(paramParcel, k);
        break;
      case 3: 
        localEventParams = (EventParams)zza.zza(paramParcel, k, EventParams.CREATOR);
        break;
      case 4: 
        str1 = zza.zzq(paramParcel, k);
        break;
      case 5: 
        l = zza.zzi(paramParcel, k);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zza.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new EventParcel(i, str2, localEventParams, str1, l);
  }
  
  public EventParcel[] zzwm(int paramInt)
  {
    return new EventParcel[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */