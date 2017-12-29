package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.internal.zzbfn;

public final class zzbu implements Creator<zzbt> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        ConnectionResult connectionResult = null;
        boolean z = false;
        int zzd = zzbfn.zzd(parcel);
        boolean z2 = false;
        IBinder iBinder = null;
        int i = 0;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                case 2:
                    iBinder = zzbfn.zzr(parcel, readInt);
                    break;
                case 3:
                    connectionResult = (ConnectionResult) zzbfn.zza(parcel, readInt, ConnectionResult.CREATOR);
                    break;
                case 4:
                    z2 = zzbfn.zzc(parcel, readInt);
                    break;
                case 5:
                    z = zzbfn.zzc(parcel, readInt);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new zzbt(i, iBinder, connectionResult, z2, z);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzbt[i];
    }
}
