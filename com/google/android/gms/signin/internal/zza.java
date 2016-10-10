package com.google.android.gms.signin.internal;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zza
  implements Parcelable.Creator<AuthAccountResult>
{
  static void zza(AuthAccountResult paramAuthAccountResult, Parcel paramParcel, int paramInt)
  {
    int i = zzb.zzcr(paramParcel);
    zzb.zzc(paramParcel, 1, paramAuthAccountResult.mVersionCode);
    zzb.zzc(paramParcel, 2, paramAuthAccountResult.zzcdg());
    zzb.zza(paramParcel, 3, paramAuthAccountResult.zzcdh(), paramInt, false);
    zzb.zzaj(paramParcel, i);
  }
  
  public AuthAccountResult[] zzaad(int paramInt)
  {
    return new AuthAccountResult[paramInt];
  }
  
  public AuthAccountResult zzsj(Parcel paramParcel)
  {
    int j = 0;
    int k = com.google.android.gms.common.internal.safeparcel.zza.zzcq(paramParcel);
    Intent localIntent = null;
    int i = 0;
    while (paramParcel.dataPosition() < k)
    {
      int m = com.google.android.gms.common.internal.safeparcel.zza.zzcp(paramParcel);
      switch (com.google.android.gms.common.internal.safeparcel.zza.zzgv(m))
      {
      default: 
        com.google.android.gms.common.internal.safeparcel.zza.zzb(paramParcel, m);
        break;
      case 1: 
        i = com.google.android.gms.common.internal.safeparcel.zza.zzg(paramParcel, m);
        break;
      case 2: 
        j = com.google.android.gms.common.internal.safeparcel.zza.zzg(paramParcel, m);
        break;
      case 3: 
        localIntent = (Intent)com.google.android.gms.common.internal.safeparcel.zza.zza(paramParcel, m, Intent.CREATOR);
      }
    }
    if (paramParcel.dataPosition() != k) {
      throw new zza.zza(37 + "Overread allowed size end=" + k, paramParcel);
    }
    return new AuthAccountResult(i, j, localIntent);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/signin/internal/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */