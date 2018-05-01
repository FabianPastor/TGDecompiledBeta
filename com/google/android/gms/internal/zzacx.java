package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import java.util.ArrayList;

public class zzacx
  implements Parcelable.Creator<zzacw>
{
  static void zza(zzacw paramzzacw, Parcel paramParcel, int paramInt)
  {
    paramInt = zzc.zzaZ(paramParcel);
    zzc.zzc(paramParcel, 1, paramzzacw.zzaiI);
    zzc.zzc(paramParcel, 2, paramzzacw.zzyE(), false);
    zzc.zza(paramParcel, 3, paramzzacw.zzyF(), false);
    zzc.zzJ(paramParcel, paramInt);
  }
  
  public zzacw zzbg(Parcel paramParcel)
  {
    String str = null;
    int j = zzb.zzaY(paramParcel);
    int i = 0;
    ArrayList localArrayList = null;
    while (paramParcel.dataPosition() < j)
    {
      int k = zzb.zzaX(paramParcel);
      switch (zzb.zzdc(k))
      {
      default: 
        zzb.zzb(paramParcel, k);
        break;
      case 1: 
        i = zzb.zzg(paramParcel, k);
        break;
      case 2: 
        localArrayList = zzb.zzc(paramParcel, k, zzacw.zza.CREATOR);
        break;
      case 3: 
        str = zzb.zzq(paramParcel, k);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zzb.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new zzacw(i, localArrayList, str);
  }
  
  public zzacw[] zzdk(int paramInt)
  {
    return new zzacw[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzacx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */