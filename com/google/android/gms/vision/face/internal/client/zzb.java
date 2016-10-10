package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;

public class zzb
  implements Parcelable.Creator<FaceParcel>
{
  static void zza(FaceParcel paramFaceParcel, Parcel paramParcel, int paramInt)
  {
    int i = com.google.android.gms.common.internal.safeparcel.zzb.zzcr(paramParcel);
    com.google.android.gms.common.internal.safeparcel.zzb.zzc(paramParcel, 1, paramFaceParcel.versionCode);
    com.google.android.gms.common.internal.safeparcel.zzb.zzc(paramParcel, 2, paramFaceParcel.id);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 3, paramFaceParcel.centerX);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 4, paramFaceParcel.centerY);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 5, paramFaceParcel.width);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 6, paramFaceParcel.height);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 7, paramFaceParcel.aLg);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 8, paramFaceParcel.aLh);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 9, paramFaceParcel.aLi, paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 10, paramFaceParcel.aLj);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 11, paramFaceParcel.aLk);
    com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, 12, paramFaceParcel.aLl);
    com.google.android.gms.common.internal.safeparcel.zzb.zzaj(paramParcel, i);
  }
  
  public FaceParcel[] zzabs(int paramInt)
  {
    return new FaceParcel[paramInt];
  }
  
  public FaceParcel zztc(Parcel paramParcel)
  {
    int k = zza.zzcq(paramParcel);
    int j = 0;
    int i = 0;
    float f9 = 0.0F;
    float f8 = 0.0F;
    float f7 = 0.0F;
    float f6 = 0.0F;
    float f5 = 0.0F;
    float f4 = 0.0F;
    LandmarkParcel[] arrayOfLandmarkParcel = null;
    float f3 = 0.0F;
    float f2 = 0.0F;
    float f1 = 0.0F;
    while (paramParcel.dataPosition() < k)
    {
      int m = zza.zzcp(paramParcel);
      switch (zza.zzgv(m))
      {
      default: 
        zza.zzb(paramParcel, m);
        break;
      case 1: 
        j = zza.zzg(paramParcel, m);
        break;
      case 2: 
        i = zza.zzg(paramParcel, m);
        break;
      case 3: 
        f9 = zza.zzl(paramParcel, m);
        break;
      case 4: 
        f8 = zza.zzl(paramParcel, m);
        break;
      case 5: 
        f7 = zza.zzl(paramParcel, m);
        break;
      case 6: 
        f6 = zza.zzl(paramParcel, m);
        break;
      case 7: 
        f5 = zza.zzl(paramParcel, m);
        break;
      case 8: 
        f4 = zza.zzl(paramParcel, m);
        break;
      case 9: 
        arrayOfLandmarkParcel = (LandmarkParcel[])zza.zzb(paramParcel, m, LandmarkParcel.CREATOR);
        break;
      case 10: 
        f3 = zza.zzl(paramParcel, m);
        break;
      case 11: 
        f2 = zza.zzl(paramParcel, m);
        break;
      case 12: 
        f1 = zza.zzl(paramParcel, m);
      }
    }
    if (paramParcel.dataPosition() != k) {
      throw new zza.zza(37 + "Overread allowed size end=" + k, paramParcel);
    }
    return new FaceParcel(j, i, f9, f8, f7, f6, f5, f4, arrayOfLandmarkParcel, f3, f2, f1);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/face/internal/client/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */