package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzc implements Creator<FaceSettingsParcel> {
    static void zza(FaceSettingsParcel faceSettingsParcel, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, faceSettingsParcel.versionCode);
        zzb.zzc(parcel, 2, faceSettingsParcel.mode);
        zzb.zzc(parcel, 3, faceSettingsParcel.aOx);
        zzb.zzc(parcel, 4, faceSettingsParcel.aOy);
        zzb.zza(parcel, 5, faceSettingsParcel.aOz);
        zzb.zza(parcel, 6, faceSettingsParcel.aOA);
        zzb.zza(parcel, 7, faceSettingsParcel.aOB);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzst(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzabj(i);
    }

    public FaceSettingsParcel[] zzabj(int i) {
        return new FaceSettingsParcel[i];
    }

    public FaceSettingsParcel zzst(Parcel parcel) {
        boolean z = false;
        int zzcr = zza.zzcr(parcel);
        float f = -1.0f;
        boolean z2 = false;
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i4 = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    i3 = zza.zzg(parcel, zzcq);
                    break;
                case 3:
                    i2 = zza.zzg(parcel, zzcq);
                    break;
                case 4:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 5:
                    z2 = zza.zzc(parcel, zzcq);
                    break;
                case 6:
                    z = zza.zzc(parcel, zzcq);
                    break;
                case 7:
                    f = zza.zzl(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new FaceSettingsParcel(i4, i3, i2, i, z2, z, f);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }
}
