package com.google.android.gms.wallet.firstparty;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzb
  implements Parcelable.Creator<zza>
{
  static void zza(zza paramzza, Parcel paramParcel, int paramInt)
  {
    int i = zzc.zzaZ(paramParcel);
    zzc.zza(paramParcel, 2, paramzza.zzbRE, false);
    zzc.zza(paramParcel, 3, paramzza.zzbRF, false);
    zzc.zza(paramParcel, 4, paramzza.zzbRG, paramInt, false);
    zzc.zzJ(paramParcel, i);
  }
  
  public zza zzkr(Parcel paramParcel)
  {
    zzm localzzm = null;
    int i = com.google.android.gms.common.internal.safeparcel.zzb.zzaY(paramParcel);
    byte[] arrayOfByte2 = null;
    byte[] arrayOfByte1 = null;
    while (paramParcel.dataPosition() < i)
    {
      int j = com.google.android.gms.common.internal.safeparcel.zzb.zzaX(paramParcel);
      switch (com.google.android.gms.common.internal.safeparcel.zzb.zzdc(j))
      {
      default: 
        com.google.android.gms.common.internal.safeparcel.zzb.zzb(paramParcel, j);
        break;
      case 2: 
        arrayOfByte1 = com.google.android.gms.common.internal.safeparcel.zzb.zzt(paramParcel, j);
        break;
      case 3: 
        arrayOfByte2 = com.google.android.gms.common.internal.safeparcel.zzb.zzt(paramParcel, j);
        break;
      case 4: 
        localzzm = (zzm)com.google.android.gms.common.internal.safeparcel.zzb.zza(paramParcel, j, zzm.CREATOR);
      }
    }
    if (paramParcel.dataPosition() != i) {
      throw new zzb.zza(37 + "Overread allowed size end=" + i, paramParcel);
    }
    return new zza(arrayOfByte1, arrayOfByte2, localzzm);
  }
  
  public zza[] zzoN(int paramInt)
  {
    return new zza[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/firstparty/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */