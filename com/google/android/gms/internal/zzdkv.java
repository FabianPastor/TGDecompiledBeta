package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.widget.RemoteViews;

public final class zzdkv implements Creator<zzdku> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = zzbfn.zzd(parcel);
        byte[] bArr = null;
        RemoteViews remoteViews = null;
        int[] iArr = null;
        String[] strArr = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    strArr = zzbfn.zzaa(parcel, readInt);
                    break;
                case 2:
                    iArr = zzbfn.zzw(parcel, readInt);
                    break;
                case 3:
                    remoteViews = (RemoteViews) zzbfn.zza(parcel, readInt, RemoteViews.CREATOR);
                    break;
                case 4:
                    bArr = zzbfn.zzt(parcel, readInt);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new zzdku(strArr, iArr, remoteViews, bArr);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzdku[i];
    }
}
