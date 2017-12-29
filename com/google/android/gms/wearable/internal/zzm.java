package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;

public final class zzm implements Creator<zzl> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = zzbfn.zzd(parcel);
        int i = 0;
        String str = null;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        String str5 = null;
        String str6 = null;
        byte b = (byte) 0;
        byte b2 = (byte) 0;
        byte b3 = (byte) 0;
        byte b4 = (byte) 0;
        String str7 = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                case 3:
                    str = zzbfn.zzq(parcel, readInt);
                    break;
                case 4:
                    str2 = zzbfn.zzq(parcel, readInt);
                    break;
                case 5:
                    str3 = zzbfn.zzq(parcel, readInt);
                    break;
                case 6:
                    str4 = zzbfn.zzq(parcel, readInt);
                    break;
                case 7:
                    str5 = zzbfn.zzq(parcel, readInt);
                    break;
                case 8:
                    str6 = zzbfn.zzq(parcel, readInt);
                    break;
                case 9:
                    b = zzbfn.zze(parcel, readInt);
                    break;
                case 10:
                    b2 = zzbfn.zze(parcel, readInt);
                    break;
                case 11:
                    b3 = zzbfn.zze(parcel, readInt);
                    break;
                case 12:
                    b4 = zzbfn.zze(parcel, readInt);
                    break;
                case 13:
                    str7 = zzbfn.zzq(parcel, readInt);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new zzl(i, str, str2, str3, str4, str5, str6, b, b2, b3, b4, str7);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzl[i];
    }
}
