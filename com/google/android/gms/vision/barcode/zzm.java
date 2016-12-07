package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.vision.barcode.Barcode.WiFi;

public class zzm implements Creator<WiFi> {
    static void zza(WiFi wiFi, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, wiFi.versionCode);
        zzb.zza(parcel, 2, wiFi.ssid, false);
        zzb.zza(parcel, 3, wiFi.password, false);
        zzb.zzc(parcel, 4, wiFi.encryptionType);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzsq(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzabf(i);
    }

    public WiFi[] zzabf(int i) {
        return new WiFi[i];
    }

    public WiFi zzsq(Parcel parcel) {
        String str = null;
        int i = 0;
        int zzcr = zza.zzcr(parcel);
        String str2 = null;
        int i2 = 0;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i2 = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    str2 = zza.zzq(parcel, zzcq);
                    break;
                case 3:
                    str = zza.zzq(parcel, zzcq);
                    break;
                case 4:
                    i = zza.zzg(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new WiFi(i2, str2, str, i);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }
}
