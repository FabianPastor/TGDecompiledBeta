package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.vision.barcode.Barcode.PersonName;

public class zzi implements Creator<PersonName> {
    static void zza(PersonName personName, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, personName.versionCode);
        zzc.zza(parcel, 2, personName.formattedName, false);
        zzc.zza(parcel, 3, personName.pronunciation, false);
        zzc.zza(parcel, 4, personName.prefix, false);
        zzc.zza(parcel, 5, personName.first, false);
        zzc.zza(parcel, 6, personName.middle, false);
        zzc.zza(parcel, 7, personName.last, false);
        zzc.zza(parcel, 8, personName.suffix, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjH(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzob(i);
    }

    public PersonName zzjH(Parcel parcel) {
        String str = null;
        int zzaY = zzb.zzaY(parcel);
        int i = 0;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        String str5 = null;
        String str6 = null;
        String str7 = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    str7 = zzb.zzq(parcel, zzaX);
                    break;
                case 3:
                    str6 = zzb.zzq(parcel, zzaX);
                    break;
                case 4:
                    str5 = zzb.zzq(parcel, zzaX);
                    break;
                case 5:
                    str4 = zzb.zzq(parcel, zzaX);
                    break;
                case 6:
                    str3 = zzb.zzq(parcel, zzaX);
                    break;
                case 7:
                    str2 = zzb.zzq(parcel, zzaX);
                    break;
                case 8:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new PersonName(i, str7, str6, str5, str4, str3, str2, str);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public PersonName[] zzob(int i) {
        return new PersonName[i];
    }
}
