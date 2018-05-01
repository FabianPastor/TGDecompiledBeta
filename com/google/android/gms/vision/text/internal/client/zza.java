package com.google.android.gms.vision.text.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zza
  implements Parcelable.Creator<BoundingBoxParcel>
{
  static void zza(BoundingBoxParcel paramBoundingBoxParcel, Parcel paramParcel, int paramInt)
  {
    paramInt = zzb.zzcs(paramParcel);
    zzb.zzc(paramParcel, 1, paramBoundingBoxParcel.versionCode);
    zzb.zzc(paramParcel, 2, paramBoundingBoxParcel.left);
    zzb.zzc(paramParcel, 3, paramBoundingBoxParcel.top);
    zzb.zzc(paramParcel, 4, paramBoundingBoxParcel.width);
    zzb.zzc(paramParcel, 5, paramBoundingBoxParcel.height);
    zzb.zza(paramParcel, 6, paramBoundingBoxParcel.aOP);
    zzb.zzaj(paramParcel, paramInt);
  }
  
  public BoundingBoxParcel[] zzabn(int paramInt)
  {
    return new BoundingBoxParcel[paramInt];
  }
  
  public BoundingBoxParcel zzsw(Parcel paramParcel)
  {
    int i = 0;
    int i1 = com.google.android.gms.common.internal.safeparcel.zza.zzcr(paramParcel);
    float f = 0.0F;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    while (paramParcel.dataPosition() < i1)
    {
      int i2 = com.google.android.gms.common.internal.safeparcel.zza.zzcq(paramParcel);
      switch (com.google.android.gms.common.internal.safeparcel.zza.zzgu(i2))
      {
      default: 
        com.google.android.gms.common.internal.safeparcel.zza.zzb(paramParcel, i2);
        break;
      case 1: 
        n = com.google.android.gms.common.internal.safeparcel.zza.zzg(paramParcel, i2);
        break;
      case 2: 
        m = com.google.android.gms.common.internal.safeparcel.zza.zzg(paramParcel, i2);
        break;
      case 3: 
        k = com.google.android.gms.common.internal.safeparcel.zza.zzg(paramParcel, i2);
        break;
      case 4: 
        j = com.google.android.gms.common.internal.safeparcel.zza.zzg(paramParcel, i2);
        break;
      case 5: 
        i = com.google.android.gms.common.internal.safeparcel.zza.zzg(paramParcel, i2);
        break;
      case 6: 
        f = com.google.android.gms.common.internal.safeparcel.zza.zzl(paramParcel, i2);
      }
    }
    if (paramParcel.dataPosition() != i1) {
      throw new zza.zza(37 + "Overread allowed size end=" + i1, paramParcel);
    }
    return new BoundingBoxParcel(n, m, k, j, i, f);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/text/internal/client/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */