package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.internal.view.SupportMenu;
import com.google.android.gms.common.internal.safeparcel.zzb;

public final class fu implements Creator<ft> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        String str = null;
        int zzd = zzb.zzd(parcel);
        float f = 0.0f;
        boolean z = false;
        String str2 = null;
        fe feVar = null;
        fe feVar2 = null;
        fo[] foVarArr = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (SupportMenu.USER_MASK & readInt) {
                case 2:
                    foVarArr = (fo[]) zzb.zzb(parcel, readInt, fo.CREATOR);
                    break;
                case 3:
                    feVar2 = (fe) zzb.zza(parcel, readInt, fe.CREATOR);
                    break;
                case 4:
                    feVar = (fe) zzb.zza(parcel, readInt, fe.CREATOR);
                    break;
                case 5:
                    str2 = zzb.zzq(parcel, readInt);
                    break;
                case 6:
                    f = zzb.zzl(parcel, readInt);
                    break;
                case 7:
                    str = zzb.zzq(parcel, readInt);
                    break;
                case 8:
                    z = zzb.zzc(parcel, readInt);
                    break;
                default:
                    zzb.zzb(parcel, readInt);
                    break;
            }
        }
        zzb.zzF(parcel, zzd);
        return new ft(foVarArr, feVar2, feVar, str2, f, str, z);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new ft[i];
    }
}
