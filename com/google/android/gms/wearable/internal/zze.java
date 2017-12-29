package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;

public final class zze implements Creator<zzd> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = zzbfn.zzd(parcel);
        String str = null;
        String str2 = null;
        IntentFilter[] intentFilterArr = null;
        IBinder iBinder = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    iBinder = zzbfn.zzr(parcel, readInt);
                    break;
                case 3:
                    intentFilterArr = (IntentFilter[]) zzbfn.zzb(parcel, readInt, IntentFilter.CREATOR);
                    break;
                case 4:
                    str2 = zzbfn.zzq(parcel, readInt);
                    break;
                case 5:
                    str = zzbfn.zzq(parcel, readInt);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new zzd(iBinder, intentFilterArr, str2, str);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzd[i];
    }
}
