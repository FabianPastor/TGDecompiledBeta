package com.google.android.gms.common.images;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzb implements Creator<WebImage> {
    static void zza(WebImage webImage, Parcel parcel, int i) {
        int zzcs = com.google.android.gms.common.internal.safeparcel.zzb.zzcs(parcel);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, webImage.mVersionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 2, webImage.getUrl(), i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 3, webImage.getWidth());
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 4, webImage.getHeight());
        com.google.android.gms.common.internal.safeparcel.zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzci(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzgh(i);
    }

    public WebImage zzci(Parcel parcel) {
        int i = 0;
        int zzcr = zza.zzcr(parcel);
        Uri uri = null;
        int i2 = 0;
        int i3 = 0;
        while (parcel.dataPosition() < zzcr) {
            Uri uri2;
            int zzg;
            int zzcq = zza.zzcq(parcel);
            int i4;
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i4 = i;
                    i = i2;
                    uri2 = uri;
                    zzg = zza.zzg(parcel, zzcq);
                    zzcq = i4;
                    break;
                case 2:
                    zzg = i3;
                    i4 = i2;
                    uri2 = (Uri) zza.zza(parcel, zzcq, Uri.CREATOR);
                    zzcq = i;
                    i = i4;
                    break;
                case 3:
                    uri2 = uri;
                    zzg = i3;
                    i4 = i;
                    i = zza.zzg(parcel, zzcq);
                    zzcq = i4;
                    break;
                case 4:
                    zzcq = zza.zzg(parcel, zzcq);
                    i = i2;
                    uri2 = uri;
                    zzg = i3;
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    zzcq = i;
                    i = i2;
                    uri2 = uri;
                    zzg = i3;
                    break;
            }
            i3 = zzg;
            uri = uri2;
            i2 = i;
            i = zzcq;
        }
        if (parcel.dataPosition() == zzcr) {
            return new WebImage(i3, uri, i2, i);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public WebImage[] zzgh(int i) {
        return new WebImage[i];
    }
}
