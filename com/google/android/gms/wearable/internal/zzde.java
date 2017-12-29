package com.google.android.gms.wearable.internal;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;

public final class zzde implements Creator<zzdd> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = zzbfn.zzd(parcel);
        byte[] bArr = null;
        Bundle bundle = null;
        Uri uri = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    uri = (Uri) zzbfn.zza(parcel, readInt, Uri.CREATOR);
                    break;
                case 4:
                    bundle = zzbfn.zzs(parcel, readInt);
                    break;
                case 5:
                    bArr = zzbfn.zzt(parcel, readInt);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new zzdd(uri, bundle, bArr);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzdd[i];
    }
}
