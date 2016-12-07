package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzf implements Creator<LandmarkParcel> {
    static void zza(LandmarkParcel landmarkParcel, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, landmarkParcel.versionCode);
        zzb.zza(parcel, 2, landmarkParcel.x);
        zzb.zza(parcel, 3, landmarkParcel.y);
        zzb.zzc(parcel, 4, landmarkParcel.type);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzsu(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzabk(i);
    }

    public LandmarkParcel[] zzabk(int i) {
        return new LandmarkParcel[i];
    }

    public LandmarkParcel zzsu(Parcel parcel) {
        int i = 0;
        float f = 0.0f;
        int zzcr = zza.zzcr(parcel);
        float f2 = 0.0f;
        int i2 = 0;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i2 = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    f2 = zza.zzl(parcel, zzcq);
                    break;
                case 3:
                    f = zza.zzl(parcel, zzcq);
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
            return new LandmarkParcel(i2, f2, f, i);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }
}
