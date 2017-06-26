package com.google.android.gms.location.places;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.internal.view.SupportMenu;
import com.google.android.gms.common.internal.safeparcel.zzb;

public final class zzl implements Creator<PlaceReport> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        String str = null;
        int zzd = zzb.zzd(parcel);
        String str2 = null;
        int i = 0;
        String str3 = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (SupportMenu.USER_MASK & readInt) {
                case 1:
                    i = zzb.zzg(parcel, readInt);
                    break;
                case 2:
                    str2 = zzb.zzq(parcel, readInt);
                    break;
                case 3:
                    str3 = zzb.zzq(parcel, readInt);
                    break;
                case 4:
                    str = zzb.zzq(parcel, readInt);
                    break;
                default:
                    zzb.zzb(parcel, readInt);
                    break;
            }
        }
        zzb.zzF(parcel, zzd);
        return new PlaceReport(i, str2, str3, str);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new PlaceReport[i];
    }
}
