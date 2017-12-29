package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;

public final class zzeh implements Creator<zzeg> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = zzbfn.zzd(parcel);
        int i = 0;
        zzfo com_google_android_gms_wearable_internal_zzfo = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                case 3:
                    com_google_android_gms_wearable_internal_zzfo = (zzfo) zzbfn.zza(parcel, readInt, zzfo.CREATOR);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new zzeg(i, com_google_android_gms_wearable_internal_zzfo);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzeg[i];
    }
}
