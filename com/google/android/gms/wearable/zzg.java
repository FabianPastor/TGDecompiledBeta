package com.google.android.gms.wearable;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;

public final class zzg implements Creator<ConnectionConfiguration> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        String str = null;
        boolean z = false;
        int zzd = zzbfn.zzd(parcel);
        String str2 = null;
        boolean z2 = false;
        boolean z3 = false;
        int i = 0;
        int i2 = 0;
        String str3 = null;
        String str4 = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    str4 = zzbfn.zzq(parcel, readInt);
                    break;
                case 3:
                    str3 = zzbfn.zzq(parcel, readInt);
                    break;
                case 4:
                    i2 = zzbfn.zzg(parcel, readInt);
                    break;
                case 5:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                case 6:
                    z3 = zzbfn.zzc(parcel, readInt);
                    break;
                case 7:
                    z2 = zzbfn.zzc(parcel, readInt);
                    break;
                case 8:
                    str2 = zzbfn.zzq(parcel, readInt);
                    break;
                case 9:
                    z = zzbfn.zzc(parcel, readInt);
                    break;
                case 10:
                    str = zzbfn.zzq(parcel, readInt);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new ConnectionConfiguration(str4, str3, i2, i, z3, z2, str2, z, str);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new ConnectionConfiguration[i];
    }
}
