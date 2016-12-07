package com.google.android.gms.vision.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzb implements Creator<FrameMetadataParcel> {
    static void zza(FrameMetadataParcel frameMetadataParcel, Parcel parcel, int i) {
        int zzcr = com.google.android.gms.common.internal.safeparcel.zzb.zzcr(parcel);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, frameMetadataParcel.versionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 2, frameMetadataParcel.width);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 3, frameMetadataParcel.height);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 4, frameMetadataParcel.id);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 5, frameMetadataParcel.aLt);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 6, frameMetadataParcel.rotation);
        com.google.android.gms.common.internal.safeparcel.zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zztf(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzabv(i);
    }

    public FrameMetadataParcel[] zzabv(int i) {
        return new FrameMetadataParcel[i];
    }

    public FrameMetadataParcel zztf(Parcel parcel) {
        int i = 0;
        int zzcq = zza.zzcq(parcel);
        long j = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = zza.zzcp(parcel);
            switch (zza.zzgv(zzcp)) {
                case 1:
                    i5 = zza.zzg(parcel, zzcp);
                    break;
                case 2:
                    i4 = zza.zzg(parcel, zzcp);
                    break;
                case 3:
                    i3 = zza.zzg(parcel, zzcp);
                    break;
                case 4:
                    i2 = zza.zzg(parcel, zzcp);
                    break;
                case 5:
                    j = zza.zzi(parcel, zzcp);
                    break;
                case 6:
                    i = zza.zzg(parcel, zzcp);
                    break;
                default:
                    zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new FrameMetadataParcel(i5, i4, i3, i2, j, i);
        }
        throw new zza.zza("Overread allowed size end=" + zzcq, parcel);
    }
}
