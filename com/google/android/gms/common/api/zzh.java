package com.google.android.gms.common.api;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzh
  implements Parcelable.Creator<Status>
{
  static void zza(Status paramStatus, Parcel paramParcel, int paramInt)
  {
    int i = zzc.zzaZ(paramParcel);
    zzc.zzc(paramParcel, 1, paramStatus.getStatusCode());
    zzc.zza(paramParcel, 2, paramStatus.getStatusMessage(), false);
    zzc.zza(paramParcel, 3, paramStatus.zzvu(), paramInt, false);
    zzc.zzc(paramParcel, 1000, paramStatus.zzaiI);
    zzc.zzJ(paramParcel, i);
  }
  
  public Status zzaM(Parcel paramParcel)
  {
    PendingIntent localPendingIntent = null;
    int j = 0;
    int k = zzb.zzaY(paramParcel);
    String str = null;
    int i = 0;
    while (paramParcel.dataPosition() < k)
    {
      int m = zzb.zzaX(paramParcel);
      switch (zzb.zzdc(m))
      {
      default: 
        zzb.zzb(paramParcel, m);
        break;
      case 1: 
        j = zzb.zzg(paramParcel, m);
        break;
      case 2: 
        str = zzb.zzq(paramParcel, m);
        break;
      case 3: 
        localPendingIntent = (PendingIntent)zzb.zza(paramParcel, m, PendingIntent.CREATOR);
        break;
      case 1000: 
        i = zzb.zzg(paramParcel, m);
      }
    }
    if (paramParcel.dataPosition() != k) {
      throw new zzb.zza(37 + "Overread allowed size end=" + k, paramParcel);
    }
    return new Status(i, j, str, localPendingIntent);
  }
  
  public Status[] zzcz(int paramInt)
  {
    return new Status[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/zzh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */