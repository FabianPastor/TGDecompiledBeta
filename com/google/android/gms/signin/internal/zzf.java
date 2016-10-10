package com.google.android.gms.signin.internal;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zza.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzf
  implements Parcelable.Creator<RecordConsentRequest>
{
  static void zza(RecordConsentRequest paramRecordConsentRequest, Parcel paramParcel, int paramInt)
  {
    int i = zzb.zzcr(paramParcel);
    zzb.zzc(paramParcel, 1, paramRecordConsentRequest.mVersionCode);
    zzb.zza(paramParcel, 2, paramRecordConsentRequest.getAccount(), paramInt, false);
    zzb.zza(paramParcel, 3, paramRecordConsentRequest.zzcdi(), paramInt, false);
    zzb.zza(paramParcel, 4, paramRecordConsentRequest.zzahn(), false);
    zzb.zzaj(paramParcel, i);
  }
  
  public RecordConsentRequest[] zzaag(int paramInt)
  {
    return new RecordConsentRequest[paramInt];
  }
  
  public RecordConsentRequest zzsl(Parcel paramParcel)
  {
    String str = null;
    int j = zza.zzcq(paramParcel);
    int i = 0;
    Object localObject2 = null;
    Object localObject1 = null;
    if (paramParcel.dataPosition() < j)
    {
      int k = zza.zzcp(paramParcel);
      Object localObject3;
      switch (zza.zzgv(k))
      {
      default: 
        zza.zzb(paramParcel, k);
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
        i = zza.zzg(paramParcel, k);
        localObject3 = localObject1;
        localObject1 = localObject2;
        localObject2 = localObject3;
        continue;
        localObject3 = (Account)zza.zza(paramParcel, k, Account.CREATOR);
        localObject1 = localObject2;
        localObject2 = localObject3;
        continue;
        localObject3 = (Scope[])zza.zzb(paramParcel, k, Scope.CREATOR);
        localObject2 = localObject1;
        localObject1 = localObject3;
        continue;
        str = zza.zzq(paramParcel, k);
        localObject3 = localObject1;
        localObject1 = localObject2;
        localObject2 = localObject3;
      }
    }
    if (paramParcel.dataPosition() != j) {
      throw new zza.zza(37 + "Overread allowed size end=" + j, paramParcel);
    }
    return new RecordConsentRequest(i, (Account)localObject1, (Scope[])localObject2, str);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/signin/internal/zzf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */