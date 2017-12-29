package com.google.android.gms.common.data;

import android.database.CursorWindow;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;

public final class zzf implements Creator<DataHolder> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int i = 0;
        Bundle bundle = null;
        int zzd = zzbfn.zzd(parcel);
        CursorWindow[] cursorWindowArr = null;
        String[] strArr = null;
        int i2 = 0;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    strArr = zzbfn.zzaa(parcel, readInt);
                    break;
                case 2:
                    cursorWindowArr = (CursorWindow[]) zzbfn.zzb(parcel, readInt, CursorWindow.CREATOR);
                    break;
                case 3:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                case 4:
                    bundle = zzbfn.zzs(parcel, readInt);
                    break;
                case 1000:
                    i2 = zzbfn.zzg(parcel, readInt);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        DataHolder dataHolder = new DataHolder(i2, strArr, cursorWindowArr, i, bundle);
        dataHolder.zzajz();
        return dataHolder;
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new DataHolder[i];
    }
}
