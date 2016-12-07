package com.google.android.gms.measurement.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzaj implements Creator<UserAttributeParcel> {
    static void zza(UserAttributeParcel userAttributeParcel, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, userAttributeParcel.versionCode);
        zzb.zza(parcel, 2, userAttributeParcel.name, false);
        zzb.zza(parcel, 3, userAttributeParcel.avT);
        zzb.zza(parcel, 4, userAttributeParcel.avU, false);
        zzb.zza(parcel, 5, userAttributeParcel.avV, false);
        zzb.zza(parcel, 6, userAttributeParcel.Fe, false);
        zzb.zza(parcel, 7, userAttributeParcel.arK, false);
        zzb.zza(parcel, 8, userAttributeParcel.avW, false);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzpz(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzxf(i);
    }

    public UserAttributeParcel zzpz(Parcel parcel) {
        Double d = null;
        int zzcr = zza.zzcr(parcel);
        int i = 0;
        long j = 0;
        String str = null;
        String str2 = null;
        Float f = null;
        Long l = null;
        String str3 = null;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    str3 = zza.zzq(parcel, zzcq);
                    break;
                case 3:
                    j = zza.zzi(parcel, zzcq);
                    break;
                case 4:
                    l = zza.zzj(parcel, zzcq);
                    break;
                case 5:
                    f = zza.zzm(parcel, zzcq);
                    break;
                case 6:
                    str2 = zza.zzq(parcel, zzcq);
                    break;
                case 7:
                    str = zza.zzq(parcel, zzcq);
                    break;
                case 8:
                    d = zza.zzo(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new UserAttributeParcel(i, str3, j, l, f, str2, str, d);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public UserAttributeParcel[] zzxf(int i) {
        return new UserAttributeParcel[i];
    }
}
