package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.vision.barcode.Barcode.Address;
import com.google.android.gms.vision.barcode.Barcode.ContactInfo;
import com.google.android.gms.vision.barcode.Barcode.Email;
import com.google.android.gms.vision.barcode.Barcode.PersonName;
import com.google.android.gms.vision.barcode.Barcode.Phone;

public class zze implements Creator<ContactInfo> {
    static void zza(ContactInfo contactInfo, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, contactInfo.versionCode);
        zzc.zza(parcel, 2, contactInfo.name, i, false);
        zzc.zza(parcel, 3, contactInfo.organization, false);
        zzc.zza(parcel, 4, contactInfo.title, false);
        zzc.zza(parcel, 5, contactInfo.phones, i, false);
        zzc.zza(parcel, 6, contactInfo.emails, i, false);
        zzc.zza(parcel, 7, contactInfo.urls, false);
        zzc.zza(parcel, 8, contactInfo.addresses, i, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjD(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zznX(i);
    }

    public ContactInfo zzjD(Parcel parcel) {
        Address[] addressArr = null;
        int zzaY = zzb.zzaY(parcel);
        int i = 0;
        String[] strArr = null;
        Email[] emailArr = null;
        Phone[] phoneArr = null;
        String str = null;
        String str2 = null;
        PersonName personName = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    personName = (PersonName) zzb.zza(parcel, zzaX, PersonName.CREATOR);
                    break;
                case 3:
                    str2 = zzb.zzq(parcel, zzaX);
                    break;
                case 4:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                case 5:
                    phoneArr = (Phone[]) zzb.zzb(parcel, zzaX, Phone.CREATOR);
                    break;
                case 6:
                    emailArr = (Email[]) zzb.zzb(parcel, zzaX, Email.CREATOR);
                    break;
                case 7:
                    strArr = zzb.zzC(parcel, zzaX);
                    break;
                case 8:
                    addressArr = (Address[]) zzb.zzb(parcel, zzaX, Address.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new ContactInfo(i, personName, str2, str, phoneArr, emailArr, strArr, addressArr);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public ContactInfo[] zznX(int i) {
        return new ContactInfo[i];
    }
}
