package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzk implements Creator<GetServiceRequest> {
    static void zza(GetServiceRequest getServiceRequest, Parcel parcel, int i) {
        int zzcr = zzb.zzcr(parcel);
        zzb.zzc(parcel, 1, getServiceRequest.version);
        zzb.zzc(parcel, 2, getServiceRequest.Ci);
        zzb.zzc(parcel, 3, getServiceRequest.Cj);
        zzb.zza(parcel, 4, getServiceRequest.Ck, false);
        zzb.zza(parcel, 5, getServiceRequest.Cl, false);
        zzb.zza(parcel, 6, getServiceRequest.Cm, i, false);
        zzb.zza(parcel, 7, getServiceRequest.Cn, false);
        zzb.zza(parcel, 8, getServiceRequest.Co, i, false);
        zzb.zza(parcel, 9, getServiceRequest.Cp);
        zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzck(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzgn(i);
    }

    public GetServiceRequest zzck(Parcel parcel) {
        int i = 0;
        Account account = null;
        int zzcq = zza.zzcq(parcel);
        long j = 0;
        Bundle bundle = null;
        Scope[] scopeArr = null;
        IBinder iBinder = null;
        String str = null;
        int i2 = 0;
        int i3 = 0;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = zza.zzcp(parcel);
            switch (zza.zzgv(zzcp)) {
                case 1:
                    i3 = zza.zzg(parcel, zzcp);
                    break;
                case 2:
                    i2 = zza.zzg(parcel, zzcp);
                    break;
                case 3:
                    i = zza.zzg(parcel, zzcp);
                    break;
                case 4:
                    str = zza.zzq(parcel, zzcp);
                    break;
                case 5:
                    iBinder = zza.zzr(parcel, zzcp);
                    break;
                case 6:
                    scopeArr = (Scope[]) zza.zzb(parcel, zzcp, Scope.CREATOR);
                    break;
                case 7:
                    bundle = zza.zzs(parcel, zzcp);
                    break;
                case 8:
                    account = (Account) zza.zza(parcel, zzcp, Account.CREATOR);
                    break;
                case 9:
                    j = zza.zzi(parcel, zzcp);
                    break;
                default:
                    zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new GetServiceRequest(i3, i2, i, str, iBinder, scopeArr, bundle, account, j);
        }
        throw new zza.zza("Overread allowed size end=" + zzcq, parcel);
    }

    public GetServiceRequest[] zzgn(int i) {
        return new GetServiceRequest[i];
    }
}
