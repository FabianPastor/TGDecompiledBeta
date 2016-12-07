package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.vision.barcode.Barcode.DriverLicense;

public class zzf implements Creator<DriverLicense> {
    static void zza(DriverLicense driverLicense, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, driverLicense.versionCode);
        zzb.zza(parcel, 2, driverLicense.documentType, false);
        zzb.zza(parcel, 3, driverLicense.firstName, false);
        zzb.zza(parcel, 4, driverLicense.middleName, false);
        zzb.zza(parcel, 5, driverLicense.lastName, false);
        zzb.zza(parcel, 6, driverLicense.gender, false);
        zzb.zza(parcel, 7, driverLicense.addressStreet, false);
        zzb.zza(parcel, 8, driverLicense.addressCity, false);
        zzb.zza(parcel, 9, driverLicense.addressState, false);
        zzb.zza(parcel, 10, driverLicense.addressZip, false);
        zzb.zza(parcel, 11, driverLicense.licenseNumber, false);
        zzb.zza(parcel, 12, driverLicense.issueDate, false);
        zzb.zza(parcel, 13, driverLicense.expiryDate, false);
        zzb.zza(parcel, 14, driverLicense.birthDate, false);
        zzb.zza(parcel, 15, driverLicense.issuingCountry, false);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzsj(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzaay(i);
    }

    public DriverLicense[] zzaay(int i) {
        return new DriverLicense[i];
    }

    public DriverLicense zzsj(Parcel parcel) {
        int zzcr = zza.zzcr(parcel);
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
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    str = zza.zzq(parcel, zzcq);
                    break;
                case 3:
                    str2 = zza.zzq(parcel, zzcq);
                    break;
                case 4:
                    str3 = zza.zzq(parcel, zzcq);
                    break;
                case 5:
                    str4 = zza.zzq(parcel, zzcq);
                    break;
                case 6:
                    str5 = zza.zzq(parcel, zzcq);
                    break;
                case 7:
                    str6 = zza.zzq(parcel, zzcq);
                    break;
                case 8:
                    str7 = zza.zzq(parcel, zzcq);
                    break;
                case 9:
                    str8 = zza.zzq(parcel, zzcq);
                    break;
                case 10:
                    str9 = zza.zzq(parcel, zzcq);
                    break;
                case 11:
                    str10 = zza.zzq(parcel, zzcq);
                    break;
                case 12:
                    str11 = zza.zzq(parcel, zzcq);
                    break;
                case 13:
                    str12 = zza.zzq(parcel, zzcq);
                    break;
                case 14:
                    str13 = zza.zzq(parcel, zzcq);
                    break;
                case 15:
                    str14 = zza.zzq(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new DriverLicense(i, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, str13, str14);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }
}
