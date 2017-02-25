package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.vision.barcode.Barcode.Email;

public class zzg implements Creator<Email> {
    static void zza(Email email, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, email.versionCode);
        zzc.zzc(parcel, 2, email.type);
        zzc.zza(parcel, 3, email.address, false);
        zzc.zza(parcel, 4, email.subject, false);
        zzc.zza(parcel, 5, email.body, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjF(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zznZ(i);
    }

    public Email zzjF(Parcel parcel) {
        int i = 0;
        String str = null;
        int zzaY = zzb.zzaY(parcel);
        String str2 = null;
        String str3 = null;
        int i2 = 0;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i2 = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 3:
                    str3 = zzb.zzq(parcel, zzaX);
                    break;
                case 4:
                    str2 = zzb.zzq(parcel, zzaX);
                    break;
                case 5:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new Email(i2, i, str3, str2, str);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public Email[] zznZ(int i) {
        return new Email[i];
    }
}
