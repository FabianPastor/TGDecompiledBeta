package com.google.android.gms.vision.text.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzh implements Creator<TextRecognizerOptions> {
    static void zza(TextRecognizerOptions textRecognizerOptions, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, textRecognizerOptions.versionCode);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzta(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzabr(i);
    }

    public TextRecognizerOptions[] zzabr(int i) {
        return new TextRecognizerOptions[i];
    }

    public TextRecognizerOptions zzta(Parcel parcel) {
        int zzcr = zza.zzcr(parcel);
        int i = 0;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new TextRecognizerOptions(i);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }
}
