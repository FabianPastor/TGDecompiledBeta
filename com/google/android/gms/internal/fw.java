package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.internal.view.SupportMenu;
import com.google.android.gms.common.internal.safeparcel.zzb;

public final class fw implements Creator<fv> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = zzb.zzd(parcel);
        byte[] bArr = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (SupportMenu.USER_MASK & readInt) {
                case 2:
                    bArr = zzb.zzt(parcel, readInt);
                    break;
                default:
                    zzb.zzb(parcel, readInt);
                    break;
            }
        }
        zzb.zzF(parcel, zzd);
        return new fv(bArr);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new fv[i];
    }
}