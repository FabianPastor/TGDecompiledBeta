package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzb implements Creator<zza> {
    static void zza(zza com_google_android_gms_wallet_zza, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, com_google_android_gms_wallet_zza.name, false);
        zzc.zza(parcel, 3, com_google_android_gms_wallet_zza.zzbhu, false);
        zzc.zza(parcel, 4, com_google_android_gms_wallet_zza.zzbhv, false);
        zzc.zza(parcel, 5, com_google_android_gms_wallet_zza.zzbhw, false);
        zzc.zza(parcel, 6, com_google_android_gms_wallet_zza.zzUI, false);
        zzc.zza(parcel, 7, com_google_android_gms_wallet_zza.zzbPN, false);
        zzc.zza(parcel, 8, com_google_android_gms_wallet_zza.zzbPO, false);
        zzc.zza(parcel, 9, com_google_android_gms_wallet_zza.zzbhB, false);
        zzc.zza(parcel, 10, com_google_android_gms_wallet_zza.phoneNumber, false);
        zzc.zza(parcel, 11, com_google_android_gms_wallet_zza.zzbhD);
        zzc.zza(parcel, 12, com_google_android_gms_wallet_zza.zzbhE, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjX(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzot(i);
    }

    public zza zzjX(Parcel parcel) {
        String str = null;
        int zzaY = com.google.android.gms.common.internal.safeparcel.zzb.zzaY(parcel);
        boolean z = false;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        String str5 = null;
        String str6 = null;
        String str7 = null;
        String str8 = null;
        String str9 = null;
        String str10 = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = com.google.android.gms.common.internal.safeparcel.zzb.zzaX(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.zzb.zzdc(zzaX)) {
                case 2:
                    str10 = com.google.android.gms.common.internal.safeparcel.zzb.zzq(parcel, zzaX);
                    break;
                case 3:
                    str9 = com.google.android.gms.common.internal.safeparcel.zzb.zzq(parcel, zzaX);
                    break;
                case 4:
                    str8 = com.google.android.gms.common.internal.safeparcel.zzb.zzq(parcel, zzaX);
                    break;
                case 5:
                    str7 = com.google.android.gms.common.internal.safeparcel.zzb.zzq(parcel, zzaX);
                    break;
                case 6:
                    str6 = com.google.android.gms.common.internal.safeparcel.zzb.zzq(parcel, zzaX);
                    break;
                case 7:
                    str5 = com.google.android.gms.common.internal.safeparcel.zzb.zzq(parcel, zzaX);
                    break;
                case 8:
                    str4 = com.google.android.gms.common.internal.safeparcel.zzb.zzq(parcel, zzaX);
                    break;
                case 9:
                    str3 = com.google.android.gms.common.internal.safeparcel.zzb.zzq(parcel, zzaX);
                    break;
                case 10:
                    str2 = com.google.android.gms.common.internal.safeparcel.zzb.zzq(parcel, zzaX);
                    break;
                case 11:
                    z = com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, zzaX);
                    break;
                case 12:
                    str = com.google.android.gms.common.internal.safeparcel.zzb.zzq(parcel, zzaX);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zza(str10, str9, str8, str7, str6, str5, str4, str3, str2, z, str);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zza[] zzot(int i) {
        return new zza[i];
    }
}
