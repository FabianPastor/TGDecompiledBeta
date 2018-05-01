package com.google.android.gms.internal;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzaxx
  implements Parcelable.Creator<zzaxw>
{
  static void zza(zzaxw paramzzaxw, Parcel paramParcel, int paramInt)
  {
    int i = zzc.zzaV(paramParcel);
    zzc.zzc(paramParcel, 1, paramzzaxw.mVersionCode);
    zzc.zza(paramParcel, 2, paramzzaxw.getAccount(), paramInt, false);
    zzc.zza(paramParcel, 3, paramzzaxw.zzOm(), paramInt, false);
    zzc.zza(paramParcel, 4, paramzzaxw.zzqN(), false);
    zzc.zzJ(paramParcel, i);
  }
  
  public zzaxw zziQ(Parcel paramParcel)
  {
    String str = null;
    int j = zzb.zzaU(paramParcel);
    int i = 0;
    Object localObject2 = null;
    Object localObject1 = null;
    if (paramParcel.dataPosition() < j)
    {
      int k = zzb.zzaT(paramParcel);
      Object localObject3;
      switch (zzb.zzcW(k))
      {
      default: 
        zzb.zzb(paramParcel, k);
        localObject3 = localObject2;
        localObject2 = localObject1;
        localObject1 = localObject3;
      }
      for (;;)
      {
        localObject3 = localObject2;
        localObject2 = localObject1;
        localObject1 = localObject3;
        break;
        i = zzb.zzg(paramParcel, k);
        localObject3 = localObject1;
        localObject1 = localObject2;
        localObject2 = localObject3;
        continue;
        localObject3 = (Account)zzb.zza(paramParcel, k, Account.CREATOR);
        localObject1 = localObject2;
        localObject2 = localObject3;
        continue;
        localObject3 = (Scope[])zzb.zzb(paramParcel, k, Scope.CREATOR);
        localObject2 = localObject1;
        localObject1 = localObject3;
        continue;
        str = zzb.zzq(paramParcel, k);
        localObject3 = localObject1;
        localObject1 = localObject2;
        localObject2 = localObject3;
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zzb.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new zzaxw(i, (Account)localObject1, (Scope[])localObject2, str);
  }
  
  public zzaxw[] zzmL(int paramInt)
  {
    return new zzaxw[paramInt];
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaxx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */