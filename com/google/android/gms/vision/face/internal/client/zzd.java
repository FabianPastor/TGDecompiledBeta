package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;

public final class zzd implements Creator<zzc> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        boolean z = false;
        int zzd = zzbfn.zzd(parcel);
        float f = -1.0f;
        boolean z2 = false;
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    i3 = zzbfn.zzg(parcel, readInt);
                    break;
                case 3:
                    i2 = zzbfn.zzg(parcel, readInt);
                    break;
                case 4:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                case 5:
                    z2 = zzbfn.zzc(parcel, readInt);
                    break;
                case 6:
                    z = zzbfn.zzc(parcel, readInt);
                    break;
                case 7:
                    f = zzbfn.zzl(parcel, readInt);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new zzc(i3, i2, i, z2, z, f);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzc[i];
    }
}
