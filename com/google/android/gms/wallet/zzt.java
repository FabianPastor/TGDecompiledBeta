package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;

public final class zzt implements Creator<LineItem> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        String str = null;
        int zzd = zzbfn.zzd(parcel);
        int i = 0;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        String str5 = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    str5 = zzbfn.zzq(parcel, readInt);
                    break;
                case 3:
                    str4 = zzbfn.zzq(parcel, readInt);
                    break;
                case 4:
                    str3 = zzbfn.zzq(parcel, readInt);
                    break;
                case 5:
                    str2 = zzbfn.zzq(parcel, readInt);
                    break;
                case 6:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                case 7:
                    str = zzbfn.zzq(parcel, readInt);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new LineItem(str5, str4, str3, str2, i, str);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new LineItem[i];
    }
}
