package com.google.android.gms.vision.text.internal.client;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zze implements Creator<RecognitionOptions> {
    static void zza(RecognitionOptions recognitionOptions, Parcel parcel, int i) {
        int zzcr = zzb.zzcr(parcel);
        zzb.zzc(parcel, 1, recognitionOptions.versionCode);
        zzb.zza(parcel, 2, recognitionOptions.aLP, i, false);
        zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzti(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzabz(i);
    }

    public RecognitionOptions[] zzabz(int i) {
        return new RecognitionOptions[i];
    }

    public RecognitionOptions zzti(Parcel parcel) {
        int zzcq = zza.zzcq(parcel);
        int i = 0;
        Rect rect = null;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = zza.zzcp(parcel);
            switch (zza.zzgv(zzcp)) {
                case 1:
                    i = zza.zzg(parcel, zzcp);
                    break;
                case 2:
                    rect = (Rect) zza.zza(parcel, zzcp, Rect.CREATOR);
                    break;
                default:
                    zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new RecognitionOptions(i, rect);
        }
        throw new zza.zza("Overread allowed size end=" + zzcq, parcel);
    }
}
