package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.vision.barcode.Barcode.WiFi;

public class zzm implements Creator<WiFi> {
    static void zza(WiFi wiFi, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, wiFi.versionCode);
        zzc.zza(parcel, 2, wiFi.ssid, false);
        zzc.zza(parcel, 3, wiFi.password, false);
        zzc.zzc(parcel, 4, wiFi.encryptionType);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjL(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzof(i);
    }

    public WiFi zzjL(Parcel parcel) {
        String str = null;
        int i = 0;
        int zzaY = zzb.zzaY(parcel);
        String str2 = null;
        int i2 = 0;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i2 = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    str2 = zzb.zzq(parcel, zzaX);
                    break;
                case 3:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                case 4:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new WiFi(i2, str2, str, i);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public WiFi[] zzof(int i) {
        return new WiFi[i];
    }
}
