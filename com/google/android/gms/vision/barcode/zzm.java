package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.vision.barcode.Barcode.WiFi;

public class zzm implements Creator<WiFi> {
    static void zza(WiFi wiFi, Parcel parcel, int i) {
        int zzcr = zzb.zzcr(parcel);
        zzb.zzc(parcel, 1, wiFi.versionCode);
        zzb.zza(parcel, 2, wiFi.ssid, false);
        zzb.zza(parcel, 3, wiFi.password, false);
        zzb.zzc(parcel, 4, wiFi.encryptionType);
        zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzta(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzabp(i);
    }

    public WiFi[] zzabp(int i) {
        return new WiFi[i];
    }

    public WiFi zzta(Parcel parcel) {
        String str = null;
        int i = 0;
        int zzcq = zza.zzcq(parcel);
        String str2 = null;
        int i2 = 0;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = zza.zzcp(parcel);
            switch (zza.zzgv(zzcp)) {
                case 1:
                    i2 = zza.zzg(parcel, zzcp);
                    break;
                case 2:
                    str2 = zza.zzq(parcel, zzcp);
                    break;
                case 3:
                    str = zza.zzq(parcel, zzcp);
                    break;
                case 4:
                    i = zza.zzg(parcel, zzcp);
                    break;
                default:
                    zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new WiFi(i2, str2, str, i);
        }
        throw new zza.zza("Overread allowed size end=" + zzcq, parcel);
    }
}
