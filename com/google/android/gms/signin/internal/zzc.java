package com.google.android.gms.signin.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import java.util.List;

public class zzc implements Creator<CheckServerAuthResult> {
    static void zza(CheckServerAuthResult checkServerAuthResult, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, checkServerAuthResult.mVersionCode);
        zzb.zza(parcel, 2, checkServerAuthResult.aDs);
        zzb.zzc(parcel, 3, checkServerAuthResult.aDt, false);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzsa(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzzu(i);
    }

    public CheckServerAuthResult zzsa(Parcel parcel) {
        boolean z = false;
        int zzcr = zza.zzcr(parcel);
        List list = null;
        int i = 0;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    z = zza.zzc(parcel, zzcq);
                    break;
                case 3:
                    list = zza.zzc(parcel, zzcq, Scope.CREATOR);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new CheckServerAuthResult(i, z, list);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public CheckServerAuthResult[] zzzu(int i) {
        return new CheckServerAuthResult[i];
    }
}
