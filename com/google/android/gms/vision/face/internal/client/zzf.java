package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzf implements Creator<LandmarkParcel> {
    static void zza(LandmarkParcel landmarkParcel, Parcel parcel, int i) {
        int zzcr = zzb.zzcr(parcel);
        zzb.zzc(parcel, 1, landmarkParcel.versionCode);
        zzb.zza(parcel, 2, landmarkParcel.x);
        zzb.zza(parcel, 3, landmarkParcel.y);
        zzb.zzc(parcel, 4, landmarkParcel.type);
        zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzte(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzabu(i);
    }

    public LandmarkParcel[] zzabu(int i) {
        return new LandmarkParcel[i];
    }

    public LandmarkParcel zzte(Parcel parcel) {
        int i = 0;
        float f = 0.0f;
        int zzcq = zza.zzcq(parcel);
        float f2 = 0.0f;
        int i2 = 0;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = zza.zzcp(parcel);
            switch (zza.zzgv(zzcp)) {
                case 1:
                    i2 = zza.zzg(parcel, zzcp);
                    break;
                case 2:
                    f2 = zza.zzl(parcel, zzcp);
                    break;
                case 3:
                    f = zza.zzl(parcel, zzcp);
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
            return new LandmarkParcel(i2, f2, f, i);
        }
        throw new zza.zza("Overread allowed size end=" + zzcq, parcel);
    }
}
