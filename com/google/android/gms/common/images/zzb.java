package com.google.android.gms.common.images;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzb implements Creator<WebImage> {
    static void zza(WebImage webImage, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, webImage.zzaiI);
        zzc.zza(parcel, 2, webImage.getUrl(), i, false);
        zzc.zzc(parcel, 3, webImage.getWidth());
        zzc.zzc(parcel, 4, webImage.getHeight());
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzaP(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzcP(i);
    }

    public WebImage zzaP(Parcel parcel) {
        int i = 0;
        int zzaY = com.google.android.gms.common.internal.safeparcel.zzb.zzaY(parcel);
        Uri uri = null;
        int i2 = 0;
        int i3 = 0;
        while (parcel.dataPosition() < zzaY) {
            Uri uri2;
            int zzg;
            int zzaX = com.google.android.gms.common.internal.safeparcel.zzb.zzaX(parcel);
            int i4;
            switch (com.google.android.gms.common.internal.safeparcel.zzb.zzdc(zzaX)) {
                case 1:
                    i4 = i;
                    i = i2;
                    uri2 = uri;
                    zzg = com.google.android.gms.common.internal.safeparcel.zzb.zzg(parcel, zzaX);
                    zzaX = i4;
                    break;
                case 2:
                    zzg = i3;
                    i4 = i2;
                    uri2 = (Uri) com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, zzaX, Uri.CREATOR);
                    zzaX = i;
                    i = i4;
                    break;
                case 3:
                    uri2 = uri;
                    zzg = i3;
                    i4 = i;
                    i = com.google.android.gms.common.internal.safeparcel.zzb.zzg(parcel, zzaX);
                    zzaX = i4;
                    break;
                case 4:
                    zzaX = com.google.android.gms.common.internal.safeparcel.zzb.zzg(parcel, zzaX);
                    i = i2;
                    uri2 = uri;
                    zzg = i3;
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zzb.zzb(parcel, zzaX);
                    zzaX = i;
                    i = i2;
                    uri2 = uri;
                    zzg = i3;
                    break;
            }
            i3 = zzg;
            uri = uri2;
            i2 = i;
            i = zzaX;
        }
        if (parcel.dataPosition() == zzaY) {
            return new WebImage(i3, uri, i2, i);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public WebImage[] zzcP(int i) {
        return new WebImage[i];
    }
}
