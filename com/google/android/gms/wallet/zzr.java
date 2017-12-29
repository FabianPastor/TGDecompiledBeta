package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;
import java.util.ArrayList;

public final class zzr implements Creator<IsReadyToPayRequest> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        ArrayList arrayList = null;
        int zzd = zzbfn.zzd(parcel);
        boolean z = false;
        String str = null;
        String str2 = null;
        ArrayList arrayList2 = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    arrayList2 = zzbfn.zzab(parcel, readInt);
                    break;
                case 4:
                    str2 = zzbfn.zzq(parcel, readInt);
                    break;
                case 5:
                    str = zzbfn.zzq(parcel, readInt);
                    break;
                case 6:
                    arrayList = zzbfn.zzab(parcel, readInt);
                    break;
                case 7:
                    z = zzbfn.zzc(parcel, readInt);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new IsReadyToPayRequest(arrayList2, str2, str, arrayList, z);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new IsReadyToPayRequest[i];
    }
}
