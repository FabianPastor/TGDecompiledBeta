package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzayy
  implements Parcelable.Creator<zzayx>
{
  static void zza(zzayx paramzzayx, Parcel paramParcel, int paramInt)
  {
    int i = zzc.zzaZ(paramParcel);
    zzc.zzc(paramParcel, 2, paramzzayx.zzbBB);
    zzc.zza(paramParcel, 3, paramzzayx.zzbBC, paramInt, false);
    zzc.zza(paramParcel, 4, paramzzayx.zzbBD, false);
    zzc.zzJ(paramParcel, i);
  }
  
  public zzayx zzja(Parcel paramParcel)
  {
    String[] arrayOfString = null;
    int j = zzb.zzaY(paramParcel);
    int i = 0;
    zzayz[] arrayOfzzayz = null;
    if (paramParcel.dataPosition() < j)
    {
      int k = zzb.zzaX(paramParcel);
      switch (zzb.zzdc(k))
      {
      default: 
        zzb.zzb(paramParcel, k);
      }
      for (;;)
      {
        break;
        i = zzb.zzg(paramParcel, k);
        continue;
        arrayOfzzayz = (zzayz[])zzb.zzb(paramParcel, k, zzayz.CREATOR);
        continue;
        arrayOfString = zzb.zzC(paramParcel, k);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zzb.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new zzayx(i, arrayOfzzayz, arrayOfString);
  }
  
  public zzayx[] zzmX(int paramInt)
  {
    return new zzayx[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzayy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */