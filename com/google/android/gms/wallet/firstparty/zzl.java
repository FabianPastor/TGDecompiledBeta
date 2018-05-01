package com.google.android.gms.wallet.firstparty;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzl
  implements Parcelable.Creator<InitializeBuyFlowRequest>
{
  static void zza(InitializeBuyFlowRequest paramInitializeBuyFlowRequest, Parcel paramParcel, int paramInt)
  {
    paramInt = zzc.zzaZ(paramParcel);
    zzc.zzc(paramParcel, 1, paramInitializeBuyFlowRequest.getVersionCode());
    zzc.zza(paramParcel, 2, paramInitializeBuyFlowRequest.zzbRN, false);
    zzc.zzJ(paramParcel, paramInt);
  }
  
  public InitializeBuyFlowRequest zzkw(Parcel paramParcel)
  {
    int j = zzb.zzaY(paramParcel);
    int i = 0;
    byte[][] arrayOfByte = null;
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
        arrayOfByte = zzb.zzu(paramParcel, k);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zzb.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new InitializeBuyFlowRequest(i, arrayOfByte);
  }
  
  public InitializeBuyFlowRequest[] zzoS(int paramInt)
  {
    return new InitializeBuyFlowRequest[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/firstparty/zzl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */