package com.google.android.gms.vision.text.internal.client;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zze
  implements Parcelable.Creator<RecognitionOptions>
{
  static void zza(RecognitionOptions paramRecognitionOptions, Parcel paramParcel, int paramInt)
  {
    int i = zzb.zzcr(paramParcel);
    zzb.zzc(paramParcel, 1, paramRecognitionOptions.versionCode);
    zzb.zza(paramParcel, 2, paramRecognitionOptions.aLP, paramInt, false);
    zzb.zzaj(paramParcel, i);
  }
  
  public RecognitionOptions[] zzabz(int paramInt)
  {
    return new RecognitionOptions[paramInt];
  }
  
  public RecognitionOptions zzti(Parcel paramParcel)
  {
    int j = zza.zzcq(paramParcel);
    int i = 0;
    Rect localRect = null;
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
        localRect = (Rect)zza.zza(paramParcel, k, Rect.CREATOR);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zza.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new RecognitionOptions(i, localRect);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/text/internal/client/zze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */