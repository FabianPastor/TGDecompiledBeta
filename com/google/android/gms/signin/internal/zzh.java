package com.google.android.gms.signin.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ResolveAccountRequest;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzh implements Creator<SignInRequest> {
    static void zza(SignInRequest signInRequest, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, signInRequest.mVersionCode);
        zzb.zza(parcel, 2, signInRequest.zzcdm(), i, false);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzsc(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzzx(i);
    }

    public SignInRequest zzsc(Parcel parcel) {
        int zzcr = zza.zzcr(parcel);
        int i = 0;
        ResolveAccountRequest resolveAccountRequest = null;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    resolveAccountRequest = (ResolveAccountRequest) zza.zza(parcel, zzcq, ResolveAccountRequest.CREATOR);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new SignInRequest(i, resolveAccountRequest);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public SignInRequest[] zzzx(int i) {
        return new SignInRequest[i];
    }
}
