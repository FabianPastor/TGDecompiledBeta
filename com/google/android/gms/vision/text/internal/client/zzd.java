package com.google.android.gms.vision.text.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzd implements Creator<LineBoxParcel> {
    static void zza(LineBoxParcel lineBoxParcel, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, lineBoxParcel.versionCode);
        zzb.zza(parcel, 2, lineBoxParcel.aOQ, i, false);
        zzb.zza(parcel, 3, lineBoxParcel.aOR, i, false);
        zzb.zza(parcel, 4, lineBoxParcel.aOS, i, false);
        zzb.zza(parcel, 5, lineBoxParcel.aOT, i, false);
        zzb.zza(parcel, 6, lineBoxParcel.aOU, false);
        zzb.zza(parcel, 7, lineBoxParcel.aOV);
        zzb.zza(parcel, 8, lineBoxParcel.aOK, false);
        zzb.zzc(parcel, 9, lineBoxParcel.aOW);
        zzb.zza(parcel, 10, lineBoxParcel.aOX);
        zzb.zzc(parcel, 11, lineBoxParcel.aOY);
        zzb.zzc(parcel, 12, lineBoxParcel.aOZ);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzsx(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzabo(i);
    }

    public LineBoxParcel[] zzabo(int i) {
        return new LineBoxParcel[i];
    }

    public LineBoxParcel zzsx(Parcel parcel) {
        int zzcr = zza.zzcr(parcel);
        int i = 0;
        WordBoxParcel[] wordBoxParcelArr = null;
        BoundingBoxParcel boundingBoxParcel = null;
        BoundingBoxParcel boundingBoxParcel2 = null;
        BoundingBoxParcel boundingBoxParcel3 = null;
        String str = null;
        float f = 0.0f;
        String str2 = null;
        int i2 = 0;
        boolean z = false;
        int i3 = 0;
        int i4 = 0;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    wordBoxParcelArr = (WordBoxParcel[]) zza.zzb(parcel, zzcq, WordBoxParcel.CREATOR);
                    break;
                case 3:
                    boundingBoxParcel = (BoundingBoxParcel) zza.zza(parcel, zzcq, BoundingBoxParcel.CREATOR);
                    break;
                case 4:
                    boundingBoxParcel2 = (BoundingBoxParcel) zza.zza(parcel, zzcq, BoundingBoxParcel.CREATOR);
                    break;
                case 5:
                    boundingBoxParcel3 = (BoundingBoxParcel) zza.zza(parcel, zzcq, BoundingBoxParcel.CREATOR);
                    break;
                case 6:
                    str = zza.zzq(parcel, zzcq);
                    break;
                case 7:
                    f = zza.zzl(parcel, zzcq);
                    break;
                case 8:
                    str2 = zza.zzq(parcel, zzcq);
                    break;
                case 9:
                    i2 = zza.zzg(parcel, zzcq);
                    break;
                case 10:
                    z = zza.zzc(parcel, zzcq);
                    break;
                case 11:
                    i3 = zza.zzg(parcel, zzcq);
                    break;
                case 12:
                    i4 = zza.zzg(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new LineBoxParcel(i, wordBoxParcelArr, boundingBoxParcel, boundingBoxParcel2, boundingBoxParcel3, str, f, str2, i2, z, i3, i4);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }
}
