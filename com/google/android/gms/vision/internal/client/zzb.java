package com.google.android.gms.vision.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;

public class zzb
  implements Parcelable.Creator<FrameMetadataParcel>
{
  static void zza(FrameMetadataParcel paramFrameMetadataParcel, Parcel paramParcel, int paramInt)
  {
    paramInt = com.google.android.gms.common.internal.safeparcel.zzb.zzcr(paramParcel);
    com.google.android.gms.common.internal.safeparcel.zzb.zzc(paramParcel, 1, paramFrameMetadataParcel.versionCode);
    com.google.android.gms.common.internal.safeparcel.zzb.zzc(paramParcel, 2, paramFrameMetadataParcel.width);
    com.google.android.gms.common.internal.safeparcel.zzb.zzc(paramParcel, 3, paramFrameMetadataParcel.height);
    com.google.android.gms.common.internal.safeparcel.zzb.zzc(paramParcel, 4, paramFrameMetadataParcel.id);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 5, paramFrameMetadataParcel.aLt);
    com.google.android.gms.common.internal.safeparcel.zzb.zzc(paramParcel, 6, paramFrameMetadataParcel.rotation);
    com.google.android.gms.common.internal.safeparcel.zzb.zzaj(paramParcel, paramInt);
  }
  
  public FrameMetadataParcel[] zzabv(int paramInt)
  {
    return new FrameMetadataParcel[paramInt];
  }
  
  public FrameMetadataParcel zztf(Parcel paramParcel)
  {
    int i = 0;
    int i1 = zza.zzcq(paramParcel);
    long l = 0L;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    while (paramParcel.dataPosition() < i1)
    {
      int i2 = zza.zzcp(paramParcel);
      switch (zza.zzgv(i2))
      {
      default: 
        zza.zzb(paramParcel, i2);
        break;
      case 1: 
        n = zza.zzg(paramParcel, i2);
        break;
      case 2: 
        m = zza.zzg(paramParcel, i2);
        break;
      case 3: 
        k = zza.zzg(paramParcel, i2);
        break;
      case 4: 
        j = zza.zzg(paramParcel, i2);
        break;
      case 5: 
        l = zza.zzi(paramParcel, i2);
        break;
      case 6: 
        i = zza.zzg(paramParcel, i2);
      }
    }
    if (paramParcel.dataPosition() != i1) {
      throw new zza.zza(37 + "Overread allowed size end=" + i1, paramParcel);
    }
    return new FrameMetadataParcel(n, m, k, j, l, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/internal/client/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */