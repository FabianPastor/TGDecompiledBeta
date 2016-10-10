package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzf
  implements Parcelable.Creator<LandmarkParcel>
{
  static void zza(LandmarkParcel paramLandmarkParcel, Parcel paramParcel, int paramInt)
  {
    paramInt = zzb.zzcr(paramParcel);
    zzb.zzc(paramParcel, 1, paramLandmarkParcel.versionCode);
    zzb.zza(paramParcel, 2, paramLandmarkParcel.x);
    zzb.zza(paramParcel, 3, paramLandmarkParcel.y);
    zzb.zzc(paramParcel, 4, paramLandmarkParcel.type);
    zzb.zzaj(paramParcel, paramInt);
  }
  
  public LandmarkParcel[] zzabu(int paramInt)
  {
    return new LandmarkParcel[paramInt];
  }
  
  public LandmarkParcel zzte(Parcel paramParcel)
  {
    int j = 0;
    float f2 = 0.0F;
    int k = zza.zzcq(paramParcel);
    float f1 = 0.0F;
    int i = 0;
    while (paramParcel.dataPosition() < k)
    {
      int m = zza.zzcp(paramParcel);
      switch (zza.zzgv(m))
      {
      default: 
        zza.zzb(paramParcel, m);
        break;
      case 1: 
        i = zza.zzg(paramParcel, m);
        break;
      case 2: 
        f1 = zza.zzl(paramParcel, m);
        break;
      case 3: 
        f2 = zza.zzl(paramParcel, m);
        break;
      case 4: 
        j = zza.zzg(paramParcel, m);
      }
    }
    if (paramParcel.dataPosition() != k) {
      throw new zza.zza(37 + "Overread allowed size end=" + k, paramParcel);
    }
    return new LandmarkParcel(i, f1, f2, j);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/face/internal/client/zzf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */