package com.google.android.gms.vision.text.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzi implements Creator<WordBoxParcel> {
    static void zza(WordBoxParcel wordBoxParcel, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, wordBoxParcel.versionCode);
        zzb.zza(parcel, 2, wordBoxParcel.aPb, i, false);
        zzb.zza(parcel, 3, wordBoxParcel.aOR, i, false);
        zzb.zza(parcel, 4, wordBoxParcel.aOS, i, false);
        zzb.zza(parcel, 5, wordBoxParcel.aOU, false);
        zzb.zza(parcel, 6, wordBoxParcel.aOV);
        zzb.zza(parcel, 7, wordBoxParcel.aOK, false);
        zzb.zza(parcel, 8, wordBoxParcel.aPc);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zztb(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzabs(i);
    }

    public WordBoxParcel[] zzabs(int i) {
        return new WordBoxParcel[i];
    }

    public WordBoxParcel zztb(Parcel parcel) {
        boolean z = false;
        String str = null;
        int zzcr = zza.zzcr(parcel);
        float f = 0.0f;
        String str2 = null;
        BoundingBoxParcel boundingBoxParcel = null;
        BoundingBoxParcel boundingBoxParcel2 = null;
        SymbolBoxParcel[] symbolBoxParcelArr = null;
        int i = 0;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    symbolBoxParcelArr = (SymbolBoxParcel[]) zza.zzb(parcel, zzcq, SymbolBoxParcel.CREATOR);
                    break;
                case 3:
                    boundingBoxParcel2 = (BoundingBoxParcel) zza.zza(parcel, zzcq, BoundingBoxParcel.CREATOR);
                    break;
                case 4:
                    boundingBoxParcel = (BoundingBoxParcel) zza.zza(parcel, zzcq, BoundingBoxParcel.CREATOR);
                    break;
                case 5:
                    str2 = zza.zzq(parcel, zzcq);
                    break;
                case 6:
                    f = zza.zzl(parcel, zzcq);
                    break;
                case 7:
                    str = zza.zzq(parcel, zzcq);
                    break;
                case 8:
                    z = zza.zzc(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new WordBoxParcel(i, symbolBoxParcelArr, boundingBoxParcel2, boundingBoxParcel, str2, f, str, z);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }
}
