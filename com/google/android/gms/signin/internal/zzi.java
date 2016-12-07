package com.google.android.gms.signin.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.ResolveAccountResponse;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzi implements Creator<SignInResponse> {
    static void zza(SignInResponse signInResponse, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, signInResponse.mVersionCode);
        zzb.zza(parcel, 2, signInResponse.zzawn(), i, false);
        zzb.zza(parcel, 3, signInResponse.zzcdn(), i, false);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzsd(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzzy(i);
    }

    public SignInResponse zzsd(Parcel parcel) {
        ResolveAccountResponse resolveAccountResponse = null;
        int zzcr = zza.zzcr(parcel);
        int i = 0;
        ConnectionResult connectionResult = null;
        while (parcel.dataPosition() < zzcr) {
            ConnectionResult connectionResult2;
            int zzg;
            ResolveAccountResponse resolveAccountResponse2;
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    ResolveAccountResponse resolveAccountResponse3 = resolveAccountResponse;
                    connectionResult2 = connectionResult;
                    zzg = zza.zzg(parcel, zzcq);
                    resolveAccountResponse2 = resolveAccountResponse3;
                    break;
                case 2:
                    zzg = i;
                    ConnectionResult connectionResult3 = (ConnectionResult) zza.zza(parcel, zzcq, ConnectionResult.CREATOR);
                    resolveAccountResponse2 = resolveAccountResponse;
                    connectionResult2 = connectionResult3;
                    break;
                case 3:
                    resolveAccountResponse2 = (ResolveAccountResponse) zza.zza(parcel, zzcq, ResolveAccountResponse.CREATOR);
                    connectionResult2 = connectionResult;
                    zzg = i;
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    resolveAccountResponse2 = resolveAccountResponse;
                    connectionResult2 = connectionResult;
                    zzg = i;
                    break;
            }
            i = zzg;
            connectionResult = connectionResult2;
            resolveAccountResponse = resolveAccountResponse2;
        }
        if (parcel.dataPosition() == zzcr) {
            return new SignInResponse(i, connectionResult, resolveAccountResponse);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public SignInResponse[] zzzy(int i) {
        return new SignInResponse[i];
    }
}
