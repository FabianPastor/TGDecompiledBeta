package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzc
  implements Parcelable.Creator<FaceSettingsParcel>
{
  static void zza(FaceSettingsParcel paramFaceSettingsParcel, Parcel paramParcel, int paramInt)
  {
    paramInt = zzb.zzcr(paramParcel);
    zzb.zzc(paramParcel, 1, paramFaceSettingsParcel.versionCode);
    zzb.zzc(paramParcel, 2, paramFaceSettingsParcel.mode);
    zzb.zzc(paramParcel, 3, paramFaceSettingsParcel.aLm);
    zzb.zzc(paramParcel, 4, paramFaceSettingsParcel.aLn);
    zzb.zza(paramParcel, 5, paramFaceSettingsParcel.aLo);
    zzb.zza(paramParcel, 6, paramFaceSettingsParcel.aLp);
    zzb.zza(paramParcel, 7, paramFaceSettingsParcel.aLq);
    zzb.zzaj(paramParcel, paramInt);
  }
  
  public FaceSettingsParcel[] zzabt(int paramInt)
  {
    return new FaceSettingsParcel[paramInt];
  }
  
  public FaceSettingsParcel zztd(Parcel paramParcel)
  {
    boolean bool1 = false;
    int n = zza.zzcq(paramParcel);
    float f = -1.0F;
    boolean bool2 = false;
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    while (paramParcel.dataPosition() < n)
    {
      int i1 = zza.zzcp(paramParcel);
      switch (zza.zzgv(i1))
      {
      default: 
        zza.zzb(paramParcel, i1);
        break;
      case 1: 
        m = zza.zzg(paramParcel, i1);
        break;
      case 2: 
        k = zza.zzg(paramParcel, i1);
        break;
      case 3: 
        j = zza.zzg(paramParcel, i1);
        break;
      case 4: 
        i = zza.zzg(paramParcel, i1);
        break;
      case 5: 
        bool2 = zza.zzc(paramParcel, i1);
        break;
      case 6: 
        bool1 = zza.zzc(paramParcel, i1);
        break;
      case 7: 
        f = zza.zzl(paramParcel, i1);
      }
    }
    if (paramParcel.dataPosition() != n) {
      throw new zza.zza(37 + "Overread allowed size end=" + n, paramParcel);
    }
    return new FaceSettingsParcel(m, k, j, i, bool2, bool1, f);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/face/internal/client/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */