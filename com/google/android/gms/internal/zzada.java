package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzada
  implements Parcelable.Creator<zzacz>
{
  static void zza(zzacz paramzzacz, Parcel paramParcel, int paramInt)
  {
    int i = zzc.zzaZ(paramParcel);
    zzc.zzc(paramParcel, 1, paramzzacz.getVersionCode());
    zzc.zza(paramParcel, 2, paramzzacz.zzyH(), false);
    zzc.zza(paramParcel, 3, paramzzacz.zzyI(), paramInt, false);
    zzc.zzJ(paramParcel, i);
  }
  
  public zzacz zzbi(Parcel paramParcel)
  {
    zzacw localzzacw = null;
    int j = zzb.zzaY(paramParcel);
    int i = 0;
    Parcel localParcel = null;
    while (paramParcel.dataPosition() < j)
    {
      int k = zzb.zzaX(paramParcel);
      switch (zzb.zzdc(k))
      {
      default: 
        zzb.zzb(paramParcel, k);
        break;
      case 1: 
        i = zzb.zzg(paramParcel, k);
        break;
      case 2: 
        localParcel = zzb.zzF(paramParcel, k);
        break;
      case 3: 
        localzzacw = (zzacw)zzb.zza(paramParcel, k, zzacw.CREATOR);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zzb.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new zzacz(i, localParcel, localzzacw);
  }
  
  public zzacz[] zzdm(int paramInt)
  {
    return new zzacz[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzada.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */