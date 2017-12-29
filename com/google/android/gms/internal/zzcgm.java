package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public final class zzcgm implements Creator<zzcgl> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = zzbfn.zzd(parcel);
        int i = 0;
        String str = null;
        String str2 = null;
        zzcln com_google_android_gms_internal_zzcln = null;
        long j = 0;
        boolean z = false;
        String str3 = null;
        zzcha com_google_android_gms_internal_zzcha = null;
        long j2 = 0;
        zzcha com_google_android_gms_internal_zzcha2 = null;
        long j3 = 0;
        zzcha com_google_android_gms_internal_zzcha3 = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                case 2:
                    str = zzbfn.zzq(parcel, readInt);
                    break;
                case 3:
                    str2 = zzbfn.zzq(parcel, readInt);
                    break;
                case 4:
                    com_google_android_gms_internal_zzcln = (zzcln) zzbfn.zza(parcel, readInt, zzcln.CREATOR);
                    break;
                case 5:
                    j = zzbfn.zzi(parcel, readInt);
                    break;
                case 6:
                    z = zzbfn.zzc(parcel, readInt);
                    break;
                case 7:
                    str3 = zzbfn.zzq(parcel, readInt);
                    break;
                case 8:
                    com_google_android_gms_internal_zzcha = (zzcha) zzbfn.zza(parcel, readInt, zzcha.CREATOR);
                    break;
                case 9:
                    j2 = zzbfn.zzi(parcel, readInt);
                    break;
                case 10:
                    com_google_android_gms_internal_zzcha2 = (zzcha) zzbfn.zza(parcel, readInt, zzcha.CREATOR);
                    break;
                case 11:
                    j3 = zzbfn.zzi(parcel, readInt);
                    break;
                case 12:
                    com_google_android_gms_internal_zzcha3 = (zzcha) zzbfn.zza(parcel, readInt, zzcha.CREATOR);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new zzcgl(i, str, str2, com_google_android_gms_internal_zzcln, j, z, str3, com_google_android_gms_internal_zzcha, j2, com_google_android_gms_internal_zzcha2, j3, com_google_android_gms_internal_zzcha3);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzcgl[i];
    }
}
