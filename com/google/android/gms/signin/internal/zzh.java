package com.google.android.gms.signin.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ResolveAccountRequest;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzh
  implements Parcelable.Creator<SignInRequest>
{
  static void zza(SignInRequest paramSignInRequest, Parcel paramParcel, int paramInt)
  {
    int i = zzb.zzcs(paramParcel);
    zzb.zzc(paramParcel, 1, paramSignInRequest.mVersionCode);
    zzb.zza(paramParcel, 2, paramSignInRequest.zzcdm(), paramInt, false);
    zzb.zzaj(paramParcel, i);
  }
  
  public SignInRequest zzsc(Parcel paramParcel)
  {
    int j = zza.zzcr(paramParcel);
    int i = 0;
    ResolveAccountRequest localResolveAccountRequest = null;
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
        localResolveAccountRequest = (ResolveAccountRequest)zza.zza(paramParcel, k, ResolveAccountRequest.CREATOR);
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zza.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new SignInRequest(i, localResolveAccountRequest);
  }
  
  public SignInRequest[] zzzx(int paramInt)
  {
    return new SignInRequest[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/signin/internal/zzh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */