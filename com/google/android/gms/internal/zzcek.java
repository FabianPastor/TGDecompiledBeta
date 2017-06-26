package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.internal.view.SupportMenu;
import com.google.android.gms.common.internal.safeparcel.zzb;

public final class zzcek implements Creator<zzcej> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = zzb.zzd(parcel);
        int i = 0;
        String str = null;
        String str2 = null;
        zzcjh com_google_android_gms_internal_zzcjh = null;
        long j = 0;
        boolean z = false;
        String str3 = null;
        zzcey com_google_android_gms_internal_zzcey = null;
        long j2 = 0;
        zzcey com_google_android_gms_internal_zzcey2 = null;
        long j3 = 0;
        zzcey com_google_android_gms_internal_zzcey3 = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (SupportMenu.USER_MASK & readInt) {
                case 1:
                    i = zzb.zzg(parcel, readInt);
                    break;
                case 2:
                    str = zzb.zzq(parcel, readInt);
                    break;
                case 3:
                    str2 = zzb.zzq(parcel, readInt);
                    break;
                case 4:
                    com_google_android_gms_internal_zzcjh = (zzcjh) zzb.zza(parcel, readInt, zzcjh.CREATOR);
                    break;
                case 5:
                    j = zzb.zzi(parcel, readInt);
                    break;
                case 6:
                    z = zzb.zzc(parcel, readInt);
                    break;
                case 7:
                    str3 = zzb.zzq(parcel, readInt);
                    break;
                case 8:
                    com_google_android_gms_internal_zzcey = (zzcey) zzb.zza(parcel, readInt, zzcey.CREATOR);
                    break;
                case 9:
                    j2 = zzb.zzi(parcel, readInt);
                    break;
                case 10:
                    com_google_android_gms_internal_zzcey2 = (zzcey) zzb.zza(parcel, readInt, zzcey.CREATOR);
                    break;
                case 11:
                    j3 = zzb.zzi(parcel, readInt);
                    break;
                case 12:
                    com_google_android_gms_internal_zzcey3 = (zzcey) zzb.zza(parcel, readInt, zzcey.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, readInt);
                    break;
            }
        }
        zzb.zzF(parcel, zzd);
        return new zzcej(i, str, str2, com_google_android_gms_internal_zzcjh, j, z, str3, com_google_android_gms_internal_zzcey, j2, com_google_android_gms_internal_zzcey2, j3, com_google_android_gms_internal_zzcey3);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzcej[i];
    }
}
