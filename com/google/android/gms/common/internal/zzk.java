package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzk
  implements Parcelable.Creator<GetServiceRequest>
{
  static void zza(GetServiceRequest paramGetServiceRequest, Parcel paramParcel, int paramInt)
  {
    int i = zzb.zzcr(paramParcel);
    zzb.zzc(paramParcel, 1, paramGetServiceRequest.version);
    zzb.zzc(paramParcel, 2, paramGetServiceRequest.Ci);
    zzb.zzc(paramParcel, 3, paramGetServiceRequest.Cj);
    zzb.zza(paramParcel, 4, paramGetServiceRequest.Ck, false);
    zzb.zza(paramParcel, 5, paramGetServiceRequest.Cl, false);
    zzb.zza(paramParcel, 6, paramGetServiceRequest.Cm, paramInt, false);
    zzb.zza(paramParcel, 7, paramGetServiceRequest.Cn, false);
    zzb.zza(paramParcel, 8, paramGetServiceRequest.Co, paramInt, false);
    zzb.zza(paramParcel, 9, paramGetServiceRequest.Cp);
    zzb.zzaj(paramParcel, i);
  }
  
  public GetServiceRequest zzck(Parcel paramParcel)
  {
    int i = 0;
    Account localAccount = null;
    int m = zza.zzcq(paramParcel);
    long l = 0L;
    Bundle localBundle = null;
    Scope[] arrayOfScope = null;
    IBinder localIBinder = null;
    String str = null;
    int j = 0;
    int k = 0;
    while (paramParcel.dataPosition() < m)
    {
      int n = zza.zzcp(paramParcel);
      switch (zza.zzgv(n))
      {
      default: 
        zza.zzb(paramParcel, n);
        break;
      case 1: 
        k = zza.zzg(paramParcel, n);
        break;
      case 2: 
        j = zza.zzg(paramParcel, n);
        break;
      case 3: 
        i = zza.zzg(paramParcel, n);
        break;
      case 4: 
        str = zza.zzq(paramParcel, n);
        break;
      case 5: 
        localIBinder = zza.zzr(paramParcel, n);
        break;
      case 6: 
        arrayOfScope = (Scope[])zza.zzb(paramParcel, n, Scope.CREATOR);
        break;
      case 7: 
        localBundle = zza.zzs(paramParcel, n);
        break;
      case 8: 
        localAccount = (Account)zza.zza(paramParcel, n, Account.CREATOR);
        break;
      case 9: 
        l = zza.zzi(paramParcel, n);
      }
    }
    if (paramParcel.dataPosition() != m) {
      throw new zza.zza(37 + "Overread allowed size end=" + m, paramParcel);
    }
    return new GetServiceRequest(k, j, i, str, localIBinder, arrayOfScope, localBundle, localAccount, l);
  }
  
  public GetServiceRequest[] zzgn(int paramInt)
  {
    return new GetServiceRequest[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */