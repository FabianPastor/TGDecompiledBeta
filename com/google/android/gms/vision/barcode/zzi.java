package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.vision.barcode.Barcode.PersonName;

public class zzi implements Creator<PersonName> {
    static void zza(PersonName personName, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, personName.versionCode);
        zzc.zza(parcel, 2, personName.formattedName, false);
        zzc.zza(parcel, 3, personName.pronunciation, false);
        zzc.zza(parcel, 4, personName.prefix, false);
        zzc.zza(parcel, 5, personName.first, false);
        zzc.zza(parcel, 6, personName.middle, false);
        zzc.zza(parcel, 7, personName.last, false);
        zzc.zza(parcel, 8, personName.suffix, false);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjb(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zznq(i);
    }

    public PersonName zzjb(Parcel parcel) {
        String str = null;
        int zzaU = zzb.zzaU(parcel);
        int i = 0;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        String str5 = null;
        String str6 = null;
        String str7 = null;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    i = zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    str7 = zzb.zzq(parcel, zzaT);
                    break;
                case 3:
                    str6 = zzb.zzq(parcel, zzaT);
                    break;
                case 4:
                    str5 = zzb.zzq(parcel, zzaT);
                    break;
                case 5:
                    str4 = zzb.zzq(parcel, zzaT);
                    break;
                case 6:
                    str3 = zzb.zzq(parcel, zzaT);
                    break;
                case 7:
                    str2 = zzb.zzq(parcel, zzaT);
                    break;
                case 8:
                    str = zzb.zzq(parcel, zzaT);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new PersonName(i, str7, str6, str5, str4, str3, str2, str);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public PersonName[] zznq(int i) {
        return new PersonName[i];
    }
}
