package com.google.android.gms.measurement.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzaj implements Creator<UserAttributeParcel> {
    static void zza(UserAttributeParcel userAttributeParcel, Parcel parcel, int i) {
        int zzcr = zzb.zzcr(parcel);
        zzb.zzc(parcel, 1, userAttributeParcel.versionCode);
        zzb.zza(parcel, 2, userAttributeParcel.name, false);
        zzb.zza(parcel, 3, userAttributeParcel.asu);
        zzb.zza(parcel, 4, userAttributeParcel.asv, false);
        zzb.zza(parcel, 5, userAttributeParcel.asw, false);
        zzb.zza(parcel, 6, userAttributeParcel.Dr, false);
        zzb.zza(parcel, 7, userAttributeParcel.aoA, false);
        zzb.zza(parcel, 8, userAttributeParcel.asx, false);
        zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzph(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzwn(i);
    }

    public UserAttributeParcel zzph(Parcel parcel) {
        Double d = null;
        int zzcq = zza.zzcq(parcel);
        int i = 0;
        long j = 0;
        String str = null;
        String str2 = null;
        Float f = null;
        Long l = null;
        String str3 = null;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = zza.zzcp(parcel);
            switch (zza.zzgv(zzcp)) {
                case 1:
                    i = zza.zzg(parcel, zzcp);
                    break;
                case 2:
                    str3 = zza.zzq(parcel, zzcp);
                    break;
                case 3:
                    j = zza.zzi(parcel, zzcp);
                    break;
                case 4:
                    l = zza.zzj(parcel, zzcp);
                    break;
                case 5:
                    f = zza.zzm(parcel, zzcp);
                    break;
                case 6:
                    str2 = zza.zzq(parcel, zzcp);
                    break;
                case 7:
                    str = zza.zzq(parcel, zzcp);
                    break;
                case 8:
                    d = zza.zzo(parcel, zzcp);
                    break;
                default:
                    zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new UserAttributeParcel(i, str3, j, l, f, str2, str, d);
        }
        throw new zza.zza("Overread allowed size end=" + zzcq, parcel);
    }

    public UserAttributeParcel[] zzwn(int i) {
        return new UserAttributeParcel[i];
    }
}
