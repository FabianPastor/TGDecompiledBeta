package com.google.android.gms.wallet.firstparty;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzi
  implements Parcelable.Creator<zzh>
{
  static void zza(zzh paramzzh, Parcel paramParcel, int paramInt)
  {
    int i = zzc.zzaZ(paramParcel);
    zzc.zza(paramParcel, 2, paramzzh.zzbRK, paramInt, false);
    zzc.zza(paramParcel, 3, paramzzh.zzbRL);
    zzc.zzJ(paramParcel, i);
  }
  
  public zzh zzku(Parcel paramParcel)
  {
    int i = zzb.zzaY(paramParcel);
    zzm localzzm = null;
    boolean bool = false;
    if (paramParcel.dataPosition() < i)
    {
      int j = zzb.zzaX(paramParcel);
      switch (zzb.zzdc(j))
      {
      default: 
        zzb.zzb(paramParcel, j);
      }
      for (;;)
      {
        break;
        localzzm = (zzm)zzb.zza(paramParcel, j, zzm.CREATOR);
        continue;
        bool = zzb.zzc(paramParcel, j);
      }
    }
    if (paramParcel.dataPosition() != i) {
      throw new zzb.zza(37 + "Overread allowed size end=" + i, paramParcel);
    }
    return new zzh(localzzm, bool);
  }
  
  public zzh[] zzoQ(int paramInt)
  {
    return new zzh[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/firstparty/zzi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */