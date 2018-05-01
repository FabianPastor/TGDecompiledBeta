package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.common.internal.zzaf;

public class zzayc
  implements Parcelable.Creator<zzayb>
{
  static void zza(zzayb paramzzayb, Parcel paramParcel, int paramInt)
  {
    int i = zzc.zzaV(paramParcel);
    zzc.zzc(paramParcel, 1, paramzzayb.mVersionCode);
    zzc.zza(paramParcel, 2, paramzzayb.zzxA(), paramInt, false);
    zzc.zza(paramParcel, 3, paramzzayb.zzOp(), paramInt, false);
    zzc.zzJ(paramParcel, i);
  }
  
  public zzayb zziS(Parcel paramParcel)
  {
    zzaf localzzaf = null;
    int j = zzb.zzaU(paramParcel);
    int i = 0;
    ConnectionResult localConnectionResult = null;
    if (paramParcel.dataPosition() < j)
    {
      int k = zzb.zzaT(paramParcel);
      switch (zzb.zzcW(k))
      {
      default: 
        zzb.zzb(paramParcel, k);
      }
      for (;;)
      {
        break;
        i = zzb.zzg(paramParcel, k);
        continue;
        localConnectionResult = (ConnectionResult)zzb.zza(paramParcel, k, ConnectionResult.CREATOR);
        continue;
        localzzaf = (zzaf)zzb.zza(paramParcel, k, zzaf.CREATOR);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zzb.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new zzayb(i, localConnectionResult, localzzaf);
  }
  
  public zzayb[] zzmN(int paramInt)
  {
    return new zzayb[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzayc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */