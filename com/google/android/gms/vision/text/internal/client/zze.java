package com.google.android.gms.vision.text.internal.client;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zze implements Creator<RecognitionOptions> {
    static void zza(RecognitionOptions recognitionOptions, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, recognitionOptions.versionCode);
        zzb.zza(parcel, 2, recognitionOptions.aPa, i, false);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzsy(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzabp(i);
    }

    public RecognitionOptions[] zzabp(int i) {
        return new RecognitionOptions[i];
    }

    public RecognitionOptions zzsy(Parcel parcel) {
        int zzcr = zza.zzcr(parcel);
        int i = 0;
        Rect rect = null;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    rect = (Rect) zza.zza(parcel, zzcq, Rect.CREATOR);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new RecognitionOptions(i, rect);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }
}
