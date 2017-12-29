package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;

public final class zzax implements Creator<zzaw> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = zzbfn.zzd(parcel);
        int i = 0;
        int i2 = 0;
        zzay com_google_android_gms_wearable_internal_zzay = null;
        int i3 = 0;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    com_google_android_gms_wearable_internal_zzay = (zzay) zzbfn.zza(parcel, readInt, zzay.CREATOR);
                    break;
                case 3:
                    i2 = zzbfn.zzg(parcel, readInt);
                    break;
                case 4:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                case 5:
                    i3 = zzbfn.zzg(parcel, readInt);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new zzaw(com_google_android_gms_wearable_internal_zzay, i2, i, i3);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzaw[i];
    }
}
