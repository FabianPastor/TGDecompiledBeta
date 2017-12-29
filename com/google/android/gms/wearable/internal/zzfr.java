package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;

public final class zzfr implements Creator<zzfq> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = zzbfn.zzd(parcel);
        int i = 0;
        zzay com_google_android_gms_wearable_internal_zzay = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                case 3:
                    com_google_android_gms_wearable_internal_zzay = (zzay) zzbfn.zza(parcel, readInt, zzay.CREATOR);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new zzfq(i, com_google_android_gms_wearable_internal_zzay);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzfq[i];
    }
}
