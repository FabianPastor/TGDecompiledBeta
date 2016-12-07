package com.google.android.gms.vision.text.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzh implements Creator<TextRecognizerOptions> {
    static void zza(TextRecognizerOptions textRecognizerOptions, Parcel parcel, int i) {
        int zzcr = zzb.zzcr(parcel);
        zzb.zzc(parcel, 1, textRecognizerOptions.versionCode);
        zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zztk(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzacb(i);
    }

    public TextRecognizerOptions[] zzacb(int i) {
        return new TextRecognizerOptions[i];
    }

    public TextRecognizerOptions zztk(Parcel parcel) {
        int zzcq = zza.zzcq(parcel);
        int i = 0;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = zza.zzcp(parcel);
            switch (zza.zzgv(zzcp)) {
                case 1:
                    i = zza.zzg(parcel, zzcp);
                    break;
                default:
                    zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new TextRecognizerOptions(i);
        }
        throw new zza.zza("Overread allowed size end=" + zzcq, parcel);
    }
}
