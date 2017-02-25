package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.vision.barcode.Barcode.Address;

public class zza implements Creator<Address> {
    static void zza(Address address, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, address.versionCode);
        zzc.zzc(parcel, 2, address.type);
        zzc.zza(parcel, 3, address.addressLines, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjz(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zznT(i);
    }

    public Address zzjz(Parcel parcel) {
        int i = 0;
        int zzaY = zzb.zzaY(parcel);
        String[] strArr = null;
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
                    strArr = zzb.zzC(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new Address(i2, i, strArr);
        }
        throw new com.google.android.gms.common.internal.safeparcel.zzb.zza("Overread allowed size end=" + zzaY, parcel);
    }

    public Address[] zznT(int i) {
        return new Address[i];
    }
}
