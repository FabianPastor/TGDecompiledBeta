package com.google.android.gms.vision.text.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzi
  implements Parcelable.Creator<WordBoxParcel>
{
  static void zza(WordBoxParcel paramWordBoxParcel, Parcel paramParcel, int paramInt)
  {
    int i = zzb.zzcs(paramParcel);
    zzb.zzc(paramParcel, 1, paramWordBoxParcel.versionCode);
    zzb.zza(paramParcel, 2, paramWordBoxParcel.aPb, paramInt, false);
    zzb.zza(paramParcel, 3, paramWordBoxParcel.aOR, paramInt, false);
    zzb.zza(paramParcel, 4, paramWordBoxParcel.aOS, paramInt, false);
    zzb.zza(paramParcel, 5, paramWordBoxParcel.aOU, false);
    zzb.zza(paramParcel, 6, paramWordBoxParcel.aOV);
    zzb.zza(paramParcel, 7, paramWordBoxParcel.aOK, false);
    zzb.zza(paramParcel, 8, paramWordBoxParcel.aPc);
    zzb.zzaj(paramParcel, i);
  }
  
  public WordBoxParcel[] zzabs(int paramInt)
  {
    return new WordBoxParcel[paramInt];
  }
  
  public WordBoxParcel zztb(Parcel paramParcel)
  {
    boolean bool = false;
    String str1 = null;
    int j = zza.zzcr(paramParcel);
    float f = 0.0F;
    String str2 = null;
    BoundingBoxParcel localBoundingBoxParcel1 = null;
    BoundingBoxParcel localBoundingBoxParcel2 = null;
    SymbolBoxParcel[] arrayOfSymbolBoxParcel = null;
    int i = 0;
    while (paramParcel.dataPosition() < j)
    {
      int k = zza.zzcq(paramParcel);
      switch (zza.zzgu(k))
      {
      default: 
        zza.zzb(paramParcel, k);
        break;
      case 1: 
        i = zza.zzg(paramParcel, k);
        break;
      case 2: 
        arrayOfSymbolBoxParcel = (SymbolBoxParcel[])zza.zzb(paramParcel, k, SymbolBoxParcel.CREATOR);
        break;
      case 3: 
        localBoundingBoxParcel2 = (BoundingBoxParcel)zza.zza(paramParcel, k, BoundingBoxParcel.CREATOR);
        break;
      case 4: 
        localBoundingBoxParcel1 = (BoundingBoxParcel)zza.zza(paramParcel, k, BoundingBoxParcel.CREATOR);
        break;
      case 5: 
        str2 = zza.zzq(paramParcel, k);
        break;
      case 6: 
        f = zza.zzl(paramParcel, k);
        break;
      case 7: 
        str1 = zza.zzq(paramParcel, k);
        break;
      case 8: 
        bool = zza.zzc(paramParcel, k);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zza.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new WordBoxParcel(i, arrayOfSymbolBoxParcel, localBoundingBoxParcel2, localBoundingBoxParcel1, str2, f, str1, bool);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/text/internal/client/zzi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */