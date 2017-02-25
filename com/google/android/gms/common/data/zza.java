package com.google.android.gms.common.data;

import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zza implements Creator<BitmapTeleporter> {
    static void zza(BitmapTeleporter bitmapTeleporter, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, bitmapTeleporter.zzaiI);
        zzc.zza(parcel, 2, bitmapTeleporter.zzSQ, i, false);
        zzc.zzc(parcel, 3, bitmapTeleporter.zzakD);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzaN(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzcF(i);
    }

    public BitmapTeleporter zzaN(Parcel parcel) {
        int i = 0;
        int zzaY = zzb.zzaY(parcel);
        ParcelFileDescriptor parcelFileDescriptor = null;
        int i2 = 0;
        while (parcel.dataPosition() < zzaY) {
            ParcelFileDescriptor parcelFileDescriptor2;
            int zzg;
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    int i3 = i;
                    parcelFileDescriptor2 = parcelFileDescriptor;
                    zzg = zzb.zzg(parcel, zzaX);
                    zzaX = i3;
                    break;
                case 2:
                    zzg = i2;
                    ParcelFileDescriptor parcelFileDescriptor3 = (ParcelFileDescriptor) zzb.zza(parcel, zzaX, ParcelFileDescriptor.CREATOR);
                    zzaX = i;
                    parcelFileDescriptor2 = parcelFileDescriptor3;
                    break;
                case 3:
                    zzaX = zzb.zzg(parcel, zzaX);
                    parcelFileDescriptor2 = parcelFileDescriptor;
                    zzg = i2;
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    zzaX = i;
                    parcelFileDescriptor2 = parcelFileDescriptor;
                    zzg = i2;
                    break;
            }
            i2 = zzg;
            parcelFileDescriptor = parcelFileDescriptor2;
            i = zzaX;
        }
        if (parcel.dataPosition() == zzaY) {
            return new BitmapTeleporter(i2, parcelFileDescriptor, i);
        }
        throw new com.google.android.gms.common.internal.safeparcel.zzb.zza("Overread allowed size end=" + zzaY, parcel);
    }

    public BitmapTeleporter[] zzcF(int i) {
        return new BitmapTeleporter[i];
    }
}
