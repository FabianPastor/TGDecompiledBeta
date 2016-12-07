package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.vision.barcode.Barcode.Email;

public class zzg implements Creator<Email> {
    static void zza(Email email, Parcel parcel, int i) {
        int zzcr = zzb.zzcr(parcel);
        zzb.zzc(parcel, 1, email.versionCode);
        zzb.zzc(parcel, 2, email.type);
        zzb.zza(parcel, 3, email.address, false);
        zzb.zza(parcel, 4, email.subject, false);
        zzb.zza(parcel, 5, email.body, false);
        zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzsu(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzabj(i);
    }

    public Email[] zzabj(int i) {
        return new Email[i];
    }

    public Email zzsu(Parcel parcel) {
        int i = 0;
        String str = null;
        int zzcq = zza.zzcq(parcel);
        String str2 = null;
        String str3 = null;
        int i2 = 0;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = zza.zzcp(parcel);
            switch (zza.zzgv(zzcp)) {
                case 1:
                    i2 = zza.zzg(parcel, zzcp);
                    break;
                case 2:
                    i = zza.zzg(parcel, zzcp);
                    break;
                case 3:
                    str3 = zza.zzq(parcel, zzcp);
                    break;
                case 4:
                    str2 = zza.zzq(parcel, zzcp);
                    break;
                case 5:
                    str = zza.zzq(parcel, zzcp);
                    break;
                default:
                    zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new Email(i2, i, str3, str2, str);
        }
        throw new zza.zza("Overread allowed size end=" + zzcq, parcel);
    }
}
