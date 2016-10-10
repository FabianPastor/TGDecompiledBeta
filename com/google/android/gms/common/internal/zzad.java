package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzad
  implements Parcelable.Creator<ResolveAccountRequest>
{
  static void zza(ResolveAccountRequest paramResolveAccountRequest, Parcel paramParcel, int paramInt)
  {
    int i = zzb.zzcr(paramParcel);
    zzb.zzc(paramParcel, 1, paramResolveAccountRequest.mVersionCode);
    zzb.zza(paramParcel, 2, paramResolveAccountRequest.getAccount(), paramInt, false);
    zzb.zzc(paramParcel, 3, paramResolveAccountRequest.getSessionId());
    zzb.zza(paramParcel, 4, paramResolveAccountRequest.zzavc(), paramInt, false);
    zzb.zzaj(paramParcel, i);
  }
  
  public ResolveAccountRequest zzcl(Parcel paramParcel)
  {
    GoogleSignInAccount localGoogleSignInAccount = null;
    int j = 0;
    int m = zza.zzcq(paramParcel);
    Account localAccount = null;
    int i = 0;
    if (paramParcel.dataPosition() < m)
    {
      int k = zza.zzcp(paramParcel);
      switch (zza.zzgv(k))
      {
      default: 
        zza.zzb(paramParcel, k);
        k = j;
        j = i;
        i = k;
      }
      for (;;)
      {
        k = j;
        j = i;
        i = k;
        break;
        k = zza.zzg(paramParcel, k);
        i = j;
        j = k;
        continue;
        localAccount = (Account)zza.zza(paramParcel, k, Account.CREATOR);
        k = i;
        i = j;
        j = k;
        continue;
        k = zza.zzg(paramParcel, k);
        j = i;
        i = k;
        continue;
        localGoogleSignInAccount = (GoogleSignInAccount)zza.zza(paramParcel, k, GoogleSignInAccount.CREATOR);
        k = i;
        i = j;
        j = k;
      }
    }
    if (paramParcel.dataPosition() != m) {
      throw new zza.zza(37 + "Overread allowed size end=" + m, paramParcel);
    }
    return new ResolveAccountRequest(i, localAccount, j, localGoogleSignInAccount);
  }
  
  public ResolveAccountRequest[] zzgr(int paramInt)
  {
    return new ResolveAccountRequest[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzad.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */