package com.google.android.gms.vision.text.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzi implements Creator<WordBoxParcel> {
    static void zza(WordBoxParcel wordBoxParcel, Parcel parcel, int i) {
        int zzcr = zzb.zzcr(parcel);
        zzb.zzc(parcel, 1, wordBoxParcel.versionCode);
        zzb.zza(parcel, 2, wordBoxParcel.aLQ, i, false);
        zzb.zza(parcel, 3, wordBoxParcel.aLG, i, false);
        zzb.zza(parcel, 4, wordBoxParcel.aLH, i, false);
        zzb.zza(parcel, 5, wordBoxParcel.aLJ, false);
        zzb.zza(parcel, 6, wordBoxParcel.aLK);
        zzb.zza(parcel, 7, wordBoxParcel.aLz, false);
        zzb.zza(parcel, 8, wordBoxParcel.aLR);
        zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zztl(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzacc(i);
    }

    public WordBoxParcel[] zzacc(int i) {
        return new WordBoxParcel[i];
    }

    public WordBoxParcel zztl(Parcel parcel) {
        boolean z = false;
        String str = null;
        int zzcq = zza.zzcq(parcel);
        float f = 0.0f;
        String str2 = null;
        BoundingBoxParcel boundingBoxParcel = null;
        BoundingBoxParcel boundingBoxParcel2 = null;
        SymbolBoxParcel[] symbolBoxParcelArr = null;
        int i = 0;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = zza.zzcp(parcel);
            switch (zza.zzgv(zzcp)) {
                case 1:
                    i = zza.zzg(parcel, zzcp);
                    break;
                case 2:
                    symbolBoxParcelArr = (SymbolBoxParcel[]) zza.zzb(parcel, zzcp, SymbolBoxParcel.CREATOR);
                    break;
                case 3:
                    boundingBoxParcel2 = (BoundingBoxParcel) zza.zza(parcel, zzcp, BoundingBoxParcel.CREATOR);
                    break;
                case 4:
                    boundingBoxParcel = (BoundingBoxParcel) zza.zza(parcel, zzcp, BoundingBoxParcel.CREATOR);
                    break;
                case 5:
                    str2 = zza.zzq(parcel, zzcp);
                    break;
                case 6:
                    f = zza.zzl(parcel, zzcp);
                    break;
                case 7:
                    str = zza.zzq(parcel, zzcp);
                    break;
                case 8:
                    z = zza.zzc(parcel, zzcp);
                    break;
                default:
                    zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new WordBoxParcel(i, symbolBoxParcelArr, boundingBoxParcel2, boundingBoxParcel, str2, f, str, z);
        }
        throw new zza.zza("Overread allowed size end=" + zzcq, parcel);
    }
}
