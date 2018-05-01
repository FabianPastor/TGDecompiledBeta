package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzatr
  implements Parcelable.Creator<zzatq>
{
  static void zza(zzatq paramzzatq, Parcel paramParcel, int paramInt)
  {
    int i = zzc.zzaZ(paramParcel);
    zzc.zza(paramParcel, 2, paramzzatq.name, false);
    zzc.zza(paramParcel, 3, paramzzatq.zzbrH, paramInt, false);
    zzc.zza(paramParcel, 4, paramzzatq.zzbqW, false);
    zzc.zza(paramParcel, 5, paramzzatq.zzbrI);
    zzc.zzJ(paramParcel, i);
  }
  
  public zzatq zzhT(Parcel paramParcel)
  {
    String str1 = null;
    int i = zzb.zzaY(paramParcel);
    long l = 0L;
    zzato localzzato = null;
    String str2 = null;
    while (paramParcel.dataPosition() < i)
    {
      int j = zzb.zzaX(paramParcel);
      switch (zzb.zzdc(j))
      {
      default: 
        zzb.zzb(paramParcel, j);
        break;
      case 2: 
        str2 = zzb.zzq(paramParcel, j);
        break;
      case 3: 
        localzzato = (zzato)zzb.zza(paramParcel, j, zzato.CREATOR);
        break;
      case 4: 
        str1 = zzb.zzq(paramParcel, j);
        break;
      case 5: 
        l = zzb.zzi(paramParcel, j);
      }
    }
    if (paramParcel.dataPosition() != i) {
      throw new zzb.zza(37 + "Overread allowed size end=" + i, paramParcel);
    }
    return new zzatq(str2, localzzato, str1, l);
  }
  
  public zzatq[] zzlC(int paramInt)
  {
    return new zzatq[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzatr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */