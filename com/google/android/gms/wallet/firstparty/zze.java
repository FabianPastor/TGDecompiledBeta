package com.google.android.gms.wallet.firstparty;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zze
  implements Parcelable.Creator<zzd>
{
  static void zza(zzd paramzzd, Parcel paramParcel, int paramInt)
  {
    paramInt = zzc.zzaZ(paramParcel);
    zzc.zza(paramParcel, 2, paramzzd.zzbRH, false);
    zzc.zza(paramParcel, 3, paramzzd.zzbRI, false);
    zzc.zzJ(paramParcel, paramInt);
  }
  
  public zzd zzks(Parcel paramParcel)
  {
    byte[] arrayOfByte2 = null;
    int i = zzb.zzaY(paramParcel);
    byte[] arrayOfByte1 = null;
    while (paramParcel.dataPosition() < i)
    {
      int j = zzb.zzaX(paramParcel);
      switch (zzb.zzdc(j))
      {
      default: 
        zzb.zzb(paramParcel, j);
        break;
      case 2: 
        arrayOfByte1 = zzb.zzt(paramParcel, j);
        break;
      case 3: 
        arrayOfByte2 = zzb.zzt(paramParcel, j);
      }
    }
    if (paramParcel.dataPosition() != i) {
      throw new zzb.zza(37 + "Overread allowed size end=" + i, paramParcel);
    }
    return new zzd(arrayOfByte1, arrayOfByte2);
  }
  
  public zzd[] zzoO(int paramInt)
  {
    return new zzd[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/firstparty/zze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */