package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzbt;

public final class zzcxr implements Creator<zzcxq> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = zzbfn.zzd(parcel);
        ConnectionResult connectionResult = null;
        int i = 0;
        zzbt com_google_android_gms_common_internal_zzbt = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                case 2:
                    connectionResult = (ConnectionResult) zzbfn.zza(parcel, readInt, ConnectionResult.CREATOR);
                    break;
                case 3:
                    com_google_android_gms_common_internal_zzbt = (zzbt) zzbfn.zza(parcel, readInt, zzbt.CREATOR);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new zzcxq(i, connectionResult, com_google_android_gms_common_internal_zzbt);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzcxq[i];
    }
}
