package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.vision.barcode.Barcode.DriverLicense;

public class zzf implements Creator<DriverLicense> {
    static void zza(DriverLicense driverLicense, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, driverLicense.versionCode);
        zzc.zza(parcel, 2, driverLicense.documentType, false);
        zzc.zza(parcel, 3, driverLicense.firstName, false);
        zzc.zza(parcel, 4, driverLicense.middleName, false);
        zzc.zza(parcel, 5, driverLicense.lastName, false);
        zzc.zza(parcel, 6, driverLicense.gender, false);
        zzc.zza(parcel, 7, driverLicense.addressStreet, false);
        zzc.zza(parcel, 8, driverLicense.addressCity, false);
        zzc.zza(parcel, 9, driverLicense.addressState, false);
        zzc.zza(parcel, 10, driverLicense.addressZip, false);
        zzc.zza(parcel, 11, driverLicense.licenseNumber, false);
        zzc.zza(parcel, 12, driverLicense.issueDate, false);
        zzc.zza(parcel, 13, driverLicense.expiryDate, false);
        zzc.zza(parcel, 14, driverLicense.birthDate, false);
        zzc.zza(parcel, 15, driverLicense.issuingCountry, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjE(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zznY(i);
    }

    public DriverLicense zzjE(Parcel parcel) {
        int zzaY = zzb.zzaY(parcel);
        int i = 0;
        String str = null;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        String str5 = null;
        String str6 = null;
        String str7 = null;
        String str8 = null;
        String str9 = null;
        String str10 = null;
        String str11 = null;
        String str12 = null;
        String str13 = null;
        String str14 = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                case 3:
                    str2 = zzb.zzq(parcel, zzaX);
                    break;
                case 4:
                    str3 = zzb.zzq(parcel, zzaX);
                    break;
                case 5:
                    str4 = zzb.zzq(parcel, zzaX);
                    break;
                case 6:
                    str5 = zzb.zzq(parcel, zzaX);
                    break;
                case 7:
                    str6 = zzb.zzq(parcel, zzaX);
                    break;
                case 8:
                    str7 = zzb.zzq(parcel, zzaX);
                    break;
                case 9:
                    str8 = zzb.zzq(parcel, zzaX);
                    break;
                case 10:
                    str9 = zzb.zzq(parcel, zzaX);
                    break;
                case 11:
                    str10 = zzb.zzq(parcel, zzaX);
                    break;
                case 12:
                    str11 = zzb.zzq(parcel, zzaX);
                    break;
                case 13:
                    str12 = zzb.zzq(parcel, zzaX);
                    break;
                case 14:
                    str13 = zzb.zzq(parcel, zzaX);
                    break;
                case 15:
                    str14 = zzb.zzq(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new DriverLicense(i, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, str13, str14);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public DriverLicense[] zznY(int i) {
        return new DriverLicense[i];
    }
}
