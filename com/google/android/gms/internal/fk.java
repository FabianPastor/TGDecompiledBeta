package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.internal.view.SupportMenu;
import com.google.android.gms.common.internal.safeparcel.zzb;

public final class fk implements Creator<fj> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int i = 0;
        String str = null;
        int zzd = zzb.zzd(parcel);
        float f = 0.0f;
        int i2 = 0;
        boolean z = false;
        int i3 = 0;
        String str2 = null;
        fd fdVar = null;
        fd fdVar2 = null;
        fd fdVar3 = null;
        fs[] fsVarArr = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (SupportMenu.USER_MASK & readInt) {
                case 2:
                    fsVarArr = (fs[]) zzb.zzb(parcel, readInt, fs.CREATOR);
                    break;
                case 3:
                    fdVar3 = (fd) zzb.zza(parcel, readInt, fd.CREATOR);
                    break;
                case 4:
                    fdVar2 = (fd) zzb.zza(parcel, readInt, fd.CREATOR);
                    break;
                case 5:
                    fdVar = (fd) zzb.zza(parcel, readInt, fd.CREATOR);
                    break;
                case 6:
                    str2 = zzb.zzq(parcel, readInt);
                    break;
                case 7:
                    f = zzb.zzl(parcel, readInt);
                    break;
                case 8:
                    str = zzb.zzq(parcel, readInt);
                    break;
                case 9:
                    i3 = zzb.zzg(parcel, readInt);
                    break;
                case 10:
                    z = zzb.zzc(parcel, readInt);
                    break;
                case 11:
                    i2 = zzb.zzg(parcel, readInt);
                    break;
                case 12:
                    i = zzb.zzg(parcel, readInt);
                    break;
                default:
                    zzb.zzb(parcel, readInt);
                    break;
            }
        }
        zzb.zzF(parcel, zzd);
        return new fj(fsVarArr, fdVar3, fdVar2, fdVar, str2, f, str, i3, z, i2, i);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new fj[i];
    }
}
