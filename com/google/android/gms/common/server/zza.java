package com.google.android.gms.common.server;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zza
  implements Parcelable.Creator<FavaDiagnosticsEntity>
{
  static void zza(FavaDiagnosticsEntity paramFavaDiagnosticsEntity, Parcel paramParcel, int paramInt)
  {
    paramInt = zzc.zzaZ(paramParcel);
    zzc.zzc(paramParcel, 1, paramFavaDiagnosticsEntity.zzaiI);
    zzc.zza(paramParcel, 2, paramFavaDiagnosticsEntity.zzaGP, false);
    zzc.zzc(paramParcel, 3, paramFavaDiagnosticsEntity.zzaGQ);
    zzc.zzJ(paramParcel, paramInt);
  }
  
  public FavaDiagnosticsEntity zzba(Parcel paramParcel)
  {
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
        i = zzb.zzg(paramParcel, m);
        break;
      case 2: 
        str = zzb.zzq(paramParcel, m);
        break;
      case 3: 
        j = zzb.zzg(paramParcel, m);
      }
    }
    if (paramParcel.dataPosition() != k) {
      throw new zzb.zza(37 + "Overread allowed size end=" + k, paramParcel);
    }
    return new FavaDiagnosticsEntity(i, str, j);
  }
  
  public FavaDiagnosticsEntity[] zzde(int paramInt)
  {
    return new FavaDiagnosticsEntity[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/server/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */