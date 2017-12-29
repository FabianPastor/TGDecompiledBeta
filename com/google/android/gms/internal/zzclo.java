package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public final class zzclo implements Creator<zzcln> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        Double d = null;
        int zzd = zzbfn.zzd(parcel);
        int i = 0;
        long j = 0;
        String str = null;
        String str2 = null;
        Float f = null;
        Long l = null;
        String str3 = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                case 2:
                    str3 = zzbfn.zzq(parcel, readInt);
                    break;
                case 3:
                    j = zzbfn.zzi(parcel, readInt);
                    break;
                case 4:
                    l = zzbfn.zzj(parcel, readInt);
                    break;
                case 5:
                    f = zzbfn.zzm(parcel, readInt);
                    break;
                case 6:
                    str2 = zzbfn.zzq(parcel, readInt);
                    break;
                case 7:
                    str = zzbfn.zzq(parcel, readInt);
                    break;
                case 8:
                    d = zzbfn.zzo(parcel, readInt);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new zzcln(i, str3, j, l, f, str2, str, d);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzcln[i];
    }
}
