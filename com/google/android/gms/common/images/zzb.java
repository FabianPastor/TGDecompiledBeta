package com.google.android.gms.common.images;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzb implements Creator<WebImage> {
    static void zza(WebImage webImage, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, webImage.mVersionCode);
        zzc.zza(parcel, 2, webImage.getUrl(), i, false);
        zzc.zzc(parcel, 3, webImage.getWidth());
        zzc.zzc(parcel, 4, webImage.getHeight());
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzaL(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzcJ(i);
    }

    public WebImage zzaL(Parcel parcel) {
        int i = 0;
        int zzaU = com.google.android.gms.common.internal.safeparcel.zzb.zzaU(parcel);
        Uri uri = null;
        int i2 = 0;
        int i3 = 0;
        while (parcel.dataPosition() < zzaU) {
            Uri uri2;
            int zzg;
            int zzaT = com.google.android.gms.common.internal.safeparcel.zzb.zzaT(parcel);
            int i4;
            switch (com.google.android.gms.common.internal.safeparcel.zzb.zzcW(zzaT)) {
                case 1:
                    i4 = i;
                    i = i2;
                    uri2 = uri;
                    zzg = com.google.android.gms.common.internal.safeparcel.zzb.zzg(parcel, zzaT);
                    zzaT = i4;
                    break;
                case 2:
                    zzg = i3;
                    i4 = i2;
                    uri2 = (Uri) com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, zzaT, Uri.CREATOR);
                    zzaT = i;
                    i = i4;
                    break;
                case 3:
                    uri2 = uri;
                    zzg = i3;
                    i4 = i;
                    i = com.google.android.gms.common.internal.safeparcel.zzb.zzg(parcel, zzaT);
                    zzaT = i4;
                    break;
                case 4:
                    zzaT = com.google.android.gms.common.internal.safeparcel.zzb.zzg(parcel, zzaT);
                    i = i2;
                    uri2 = uri;
                    zzg = i3;
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zzb.zzb(parcel, zzaT);
                    zzaT = i;
                    i = i2;
                    uri2 = uri;
                    zzg = i3;
                    break;
            }
            i3 = zzg;
            uri = uri2;
            i2 = i;
            i = zzaT;
        }
        if (parcel.dataPosition() == zzaU) {
            return new WebImage(i3, uri, i2, i);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public WebImage[] zzcJ(int i) {
        return new WebImage[i];
    }
}
