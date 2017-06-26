package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.internal.view.SupportMenu;
import com.google.android.gms.common.internal.safeparcel.zzb;

public final class ft implements Creator<fs> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        String str = null;
        int zzd = zzb.zzd(parcel);
        float f = 0.0f;
        boolean z = false;
        String str2 = null;
        fd fdVar = null;
        fd fdVar2 = null;
        fn[] fnVarArr = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (SupportMenu.USER_MASK & readInt) {
                case 2:
                    fnVarArr = (fn[]) zzb.zzb(parcel, readInt, fn.CREATOR);
                    break;
                case 3:
                    fdVar2 = (fd) zzb.zza(parcel, readInt, fd.CREATOR);
                    break;
                case 4:
                    fdVar = (fd) zzb.zza(parcel, readInt, fd.CREATOR);
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
        return new fs(fnVarArr, fdVar2, fdVar, str2, f, str, z);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new fs[i];
    }
}
