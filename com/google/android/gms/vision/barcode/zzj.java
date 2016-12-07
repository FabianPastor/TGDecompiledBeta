package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.vision.barcode.Barcode.Phone;

public class zzj implements Creator<Phone> {
    static void zza(Phone phone, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, phone.versionCode);
        zzb.zzc(parcel, 2, phone.type);
        zzb.zza(parcel, 3, phone.number, false);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzsn(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzabc(i);
    }

    public Phone[] zzabc(int i) {
        return new Phone[i];
    }

    public Phone zzsn(Parcel parcel) {
        int i = 0;
        int zzcr = zza.zzcr(parcel);
        String str = null;
        int i2 = 0;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i2 = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 3:
                    str = zza.zzq(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new Phone(i2, i, str);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }
}
