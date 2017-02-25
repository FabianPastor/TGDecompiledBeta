package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

public class zza implements Creator<CommonWalletObject> {
    static void zza(CommonWalletObject commonWalletObject, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, commonWalletObject.zzkl, false);
        zzc.zza(parcel, 3, commonWalletObject.zzbQH, false);
        zzc.zza(parcel, 4, commonWalletObject.name, false);
        zzc.zza(parcel, 5, commonWalletObject.zzbQB, false);
        zzc.zza(parcel, 6, commonWalletObject.zzbQD, false);
        zzc.zza(parcel, 7, commonWalletObject.zzbQE, false);
        zzc.zza(parcel, 8, commonWalletObject.zzbQF, false);
        zzc.zza(parcel, 9, commonWalletObject.zzbQG, false);
        zzc.zzc(parcel, 10, commonWalletObject.state);
        zzc.zzc(parcel, 11, commonWalletObject.zzbQI, false);
        zzc.zza(parcel, 12, commonWalletObject.zzbQJ, i, false);
        zzc.zzc(parcel, 13, commonWalletObject.zzbQK, false);
        zzc.zza(parcel, 14, commonWalletObject.zzbQL, false);
        zzc.zza(parcel, 15, commonWalletObject.zzbQM, false);
        zzc.zzc(parcel, 16, commonWalletObject.zzbQN, false);
        zzc.zza(parcel, 17, commonWalletObject.zzbQO);
        zzc.zzc(parcel, 18, commonWalletObject.zzbQP, false);
        zzc.zzc(parcel, 19, commonWalletObject.zzbQQ, false);
        zzc.zzc(parcel, 20, commonWalletObject.zzbQR, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzkB(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzpa(i);
    }

    public CommonWalletObject zzkB(Parcel parcel) {
        int zzaY = zzb.zzaY(parcel);
        String str = null;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        String str5 = null;
        String str6 = null;
        String str7 = null;
        String str8 = null;
        int i = 0;
        ArrayList zzyY = com.google.android.gms.common.util.zzb.zzyY();
        zzl com_google_android_gms_wallet_wobs_zzl = null;
        ArrayList zzyY2 = com.google.android.gms.common.util.zzb.zzyY();
        String str9 = null;
        String str10 = null;
        ArrayList zzyY3 = com.google.android.gms.common.util.zzb.zzyY();
        boolean z = false;
        ArrayList zzyY4 = com.google.android.gms.common.util.zzb.zzyY();
        ArrayList zzyY5 = com.google.android.gms.common.util.zzb.zzyY();
        ArrayList zzyY6 = com.google.android.gms.common.util.zzb.zzyY();
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                case 3:
                    str2 = zzb.zzq(parcel, zzaX);
                    break;
                case 4:
                    str3 = zzb.zzq(parcel, zzaX);
                    break;
                case 5:
                    str4 = zzb.zzq(parcel, zzaX);
                    break;
                case 6:
                    str5 = zzb.zzq(parcel, zzaX);
                    break;
                case 7:
                    str6 = zzb.zzq(parcel, zzaX);
                    break;
                case 8:
                    str7 = zzb.zzq(parcel, zzaX);
                    break;
                case 9:
                    str8 = zzb.zzq(parcel, zzaX);
                    break;
                case 10:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 11:
                    zzyY = zzb.zzc(parcel, zzaX, zzp.CREATOR);
                    break;
                case 12:
                    com_google_android_gms_wallet_wobs_zzl = (zzl) zzb.zza(parcel, zzaX, zzl.CREATOR);
                    break;
                case 13:
                    zzyY2 = zzb.zzc(parcel, zzaX, LatLng.CREATOR);
                    break;
                case 14:
                    str9 = zzb.zzq(parcel, zzaX);
                    break;
                case 15:
                    str10 = zzb.zzq(parcel, zzaX);
                    break;
                case 16:
                    zzyY3 = zzb.zzc(parcel, zzaX, zzd.CREATOR);
                    break;
                case 17:
                    z = zzb.zzc(parcel, zzaX);
                    break;
                case 18:
                    zzyY4 = zzb.zzc(parcel, zzaX, zzn.CREATOR);
                    break;
                case 19:
                    zzyY5 = zzb.zzc(parcel, zzaX, zzj.CREATOR);
                    break;
                case 20:
                    zzyY6 = zzb.zzc(parcel, zzaX, zzn.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new CommonWalletObject(str, str2, str3, str4, str5, str6, str7, str8, i, zzyY, com_google_android_gms_wallet_wobs_zzl, zzyY2, str9, str10, zzyY3, z, zzyY4, zzyY5, zzyY6);
        }
        throw new com.google.android.gms.common.internal.safeparcel.zzb.zza("Overread allowed size end=" + zzaY, parcel);
    }

    public CommonWalletObject[] zzpa(int i) {
        return new CommonWalletObject[i];
    }
}
