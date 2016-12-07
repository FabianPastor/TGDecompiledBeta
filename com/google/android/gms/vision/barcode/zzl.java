package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.vision.barcode.Barcode.UrlBookmark;

public class zzl implements Creator<UrlBookmark> {
    static void zza(UrlBookmark urlBookmark, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, urlBookmark.versionCode);
        zzb.zza(parcel, 2, urlBookmark.title, false);
        zzb.zza(parcel, 3, urlBookmark.url, false);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzsp(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzabe(i);
    }

    public UrlBookmark[] zzabe(int i) {
        return new UrlBookmark[i];
    }

    public UrlBookmark zzsp(Parcel parcel) {
        String str = null;
        int zzcr = zza.zzcr(parcel);
        int i = 0;
        String str2 = null;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    str2 = zza.zzq(parcel, zzcq);
                    break;
                case 3:
                    str = zza.zzq(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new UrlBookmark(i, str2, str);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }
}
