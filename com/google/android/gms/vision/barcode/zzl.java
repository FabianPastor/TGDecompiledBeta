package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.vision.barcode.Barcode.UrlBookmark;

public class zzl implements Creator<UrlBookmark> {
    static void zza(UrlBookmark urlBookmark, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, urlBookmark.versionCode);
        zzc.zza(parcel, 2, urlBookmark.title, false);
        zzc.zza(parcel, 3, urlBookmark.url, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjK(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzoe(i);
    }

    public UrlBookmark zzjK(Parcel parcel) {
        String str = null;
        int zzaY = zzb.zzaY(parcel);
        int i = 0;
        String str2 = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    str2 = zzb.zzq(parcel, zzaX);
                    break;
                case 3:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new UrlBookmark(i, str2, str);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public UrlBookmark[] zzoe(int i) {
        return new UrlBookmark[i];
    }
}
