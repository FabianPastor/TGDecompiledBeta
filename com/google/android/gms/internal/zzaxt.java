package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import java.util.ArrayList;

public class zzaxt
  implements Parcelable.Creator<zzaxs>
{
  static void zza(zzaxs paramzzaxs, Parcel paramParcel, int paramInt)
  {
    paramInt = zzc.zzaV(paramParcel);
    zzc.zzc(paramParcel, 1, paramzzaxs.mVersionCode);
    zzc.zza(paramParcel, 2, paramzzaxs.zzbCn);
    zzc.zzc(paramParcel, 3, paramzzaxs.zzbCo, false);
    zzc.zzJ(paramParcel, paramInt);
  }
  
  public zzaxs zziP(Parcel paramParcel)
  {
    boolean bool = false;
    int j = zzb.zzaU(paramParcel);
    ArrayList localArrayList = null;
    int i = 0;
    while (paramParcel.dataPosition() < j)
    {
      int k = zzb.zzaT(paramParcel);
      switch (zzb.zzcW(k))
      {
      default: 
        zzb.zzb(paramParcel, k);
        break;
      case 1: 
        i = zzb.zzg(paramParcel, k);
        break;
      case 2: 
        bool = zzb.zzc(paramParcel, k);
        break;
      case 3: 
        localArrayList = zzb.zzc(paramParcel, k, Scope.CREATOR);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zzb.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new zzaxs(i, bool, localArrayList);
  }
  
  public zzaxs[] zzmJ(int paramInt)
  {
    return new zzaxs[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaxt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */