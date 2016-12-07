package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.vision.barcode.Barcode.Address;
import com.google.android.gms.vision.barcode.Barcode.ContactInfo;
import com.google.android.gms.vision.barcode.Barcode.Email;
import com.google.android.gms.vision.barcode.Barcode.PersonName;
import com.google.android.gms.vision.barcode.Barcode.Phone;

public class zze implements Creator<ContactInfo> {
    static void zza(ContactInfo contactInfo, Parcel parcel, int i) {
        int zzcr = zzb.zzcr(parcel);
        zzb.zzc(parcel, 1, contactInfo.versionCode);
        zzb.zza(parcel, 2, contactInfo.name, i, false);
        zzb.zza(parcel, 3, contactInfo.organization, false);
        zzb.zza(parcel, 4, contactInfo.title, false);
        zzb.zza(parcel, 5, contactInfo.phones, i, false);
        zzb.zza(parcel, 6, contactInfo.emails, i, false);
        zzb.zza(parcel, 7, contactInfo.urls, false);
        zzb.zza(parcel, 8, contactInfo.addresses, i, false);
        zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzss(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzabh(i);
    }

    public ContactInfo[] zzabh(int i) {
        return new ContactInfo[i];
    }

    public ContactInfo zzss(Parcel parcel) {
        Address[] addressArr = null;
        int zzcq = zza.zzcq(parcel);
        int i = 0;
        String[] strArr = null;
        Email[] emailArr = null;
        Phone[] phoneArr = null;
        String str = null;
        String str2 = null;
        PersonName personName = null;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = zza.zzcp(parcel);
            switch (zza.zzgv(zzcp)) {
                case 1:
                    i = zza.zzg(parcel, zzcp);
                    break;
                case 2:
                    personName = (PersonName) zza.zza(parcel, zzcp, PersonName.CREATOR);
                    break;
                case 3:
                    str2 = zza.zzq(parcel, zzcp);
                    break;
                case 4:
                    str = zza.zzq(parcel, zzcp);
                    break;
                case 5:
                    phoneArr = (Phone[]) zza.zzb(parcel, zzcp, Phone.CREATOR);
                    break;
                case 6:
                    emailArr = (Email[]) zza.zzb(parcel, zzcp, Email.CREATOR);
                    break;
                case 7:
                    strArr = zza.zzac(parcel, zzcp);
                    break;
                case 8:
                    addressArr = (Address[]) zza.zzb(parcel, zzcp, Address.CREATOR);
                    break;
                default:
                    zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new ContactInfo(i, personName, str2, str, phoneArr, emailArr, strArr, addressArr);
        }
        throw new zza.zza("Overread allowed size end=" + zzcq, parcel);
    }
}
