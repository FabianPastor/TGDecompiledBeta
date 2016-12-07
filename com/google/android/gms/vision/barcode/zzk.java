package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.vision.barcode.Barcode.Sms;

public class zzk implements Creator<Sms> {
    static void zza(Sms sms, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, sms.versionCode);
        zzb.zza(parcel, 2, sms.message, false);
        zzb.zza(parcel, 3, sms.phoneNumber, false);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzso(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzabd(i);
    }

    public Sms[] zzabd(int i) {
        return new Sms[i];
    }

    public Sms zzso(Parcel parcel) {
        String str = null;
        int zzcr = zza.zzcr(parcel);
        int i = 0;
        String str2 = null;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    str2 = zza.zzq(parcel, zzcq);
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
            return new Sms(i, str2, str);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }
}
