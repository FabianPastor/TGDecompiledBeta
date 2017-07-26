package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.internal.view.SupportMenu;
import com.google.android.gms.common.internal.safeparcel.zzb;

public final class zzcel implements Creator<zzcek> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = zzb.zzd(parcel);
        int i = 0;
        String str = null;
        String str2 = null;
        zzcji com_google_android_gms_internal_zzcji = null;
        long j = 0;
        boolean z = false;
        String str3 = null;
        zzcez com_google_android_gms_internal_zzcez = null;
        long j2 = 0;
        zzcez com_google_android_gms_internal_zzcez2 = null;
        long j3 = 0;
        zzcez com_google_android_gms_internal_zzcez3 = null;
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
                    com_google_android_gms_internal_zzcji = (zzcji) zzb.zza(parcel, readInt, zzcji.CREATOR);
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
                    com_google_android_gms_internal_zzcez = (zzcez) zzb.zza(parcel, readInt, zzcez.CREATOR);
                    break;
                case 9:
                    j2 = zzb.zzi(parcel, readInt);
                    break;
                case 10:
                    com_google_android_gms_internal_zzcez2 = (zzcez) zzb.zza(parcel, readInt, zzcez.CREATOR);
                    break;
                case 11:
                    j3 = zzb.zzi(parcel, readInt);
                    break;
                case 12:
                    com_google_android_gms_internal_zzcez3 = (zzcez) zzb.zza(parcel, readInt, zzcez.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, readInt);
                    break;
            }
        }
        zzb.zzF(parcel, zzd);
        return new zzcek(i, str, str2, com_google_android_gms_internal_zzcji, j, z, str3, com_google_android_gms_internal_zzcez, j2, com_google_android_gms_internal_zzcez2, j3, com_google_android_gms_internal_zzcez3);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzcek[i];
    }
}
