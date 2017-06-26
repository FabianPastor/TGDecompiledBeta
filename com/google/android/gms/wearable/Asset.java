package com.google.android.gms.wearable;

import android.net.Uri;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbe;
import java.util.Arrays;

public class Asset extends zza implements ReflectedParcelable {
    public static final Creator<Asset> CREATOR = new zze();
    private Uri uri;
    private String zzbQV;
    private ParcelFileDescriptor zzbQW;
    private byte[] zzbdY;

    Asset(byte[] bArr, String str, ParcelFileDescriptor parcelFileDescriptor, Uri uri) {
        this.zzbdY = bArr;
        this.zzbQV = str;
        this.zzbQW = parcelFileDescriptor;
        this.uri = uri;
    }

    public static Asset createFromBytes(byte[] bArr) {
        if (bArr != null) {
            return new Asset(bArr, null, null, null);
        }
        throw new IllegalArgumentException("Asset data cannot be null");
    }

    public static Asset createFromFd(ParcelFileDescriptor parcelFileDescriptor) {
        if (parcelFileDescriptor != null) {
            return new Asset(null, null, parcelFileDescriptor, null);
        }
        throw new IllegalArgumentException("Asset fd cannot be null");
    }

    public static Asset createFromRef(String str) {
        if (str != null) {
            return new Asset(null, str, null, null);
        }
        throw new IllegalArgumentException("Asset digest cannot be null");
    }

    public static Asset createFromUri(Uri uri) {
        if (uri != null) {
            return new Asset(null, null, null, uri);
        }
        throw new IllegalArgumentException("Asset uri cannot be null");
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Asset)) {
            return false;
        }
        Asset asset = (Asset) obj;
        return Arrays.equals(this.zzbdY, asset.zzbdY) && zzbe.equal(this.zzbQV, asset.zzbQV) && zzbe.equal(this.zzbQW, asset.zzbQW) && zzbe.equal(this.uri, asset.uri);
    }

    public final byte[] getData() {
        return this.zzbdY;
    }

    public String getDigest() {
        return this.zzbQV;
    }

    public ParcelFileDescriptor getFd() {
        return this.zzbQW;
    }

    public Uri getUri() {
        return this.uri;
    }

    public int hashCode() {
        return Arrays.deepHashCode(new Object[]{this.zzbdY, this.zzbQV, this.zzbQW, this.uri});
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Asset[@");
        stringBuilder.append(Integer.toHexString(hashCode()));
        if (this.zzbQV == null) {
            stringBuilder.append(", nodigest");
        } else {
            stringBuilder.append(", ");
            stringBuilder.append(this.zzbQV);
        }
        if (this.zzbdY != null) {
            stringBuilder.append(", size=");
            stringBuilder.append(this.zzbdY.length);
        }
        if (this.zzbQW != null) {
            stringBuilder.append(", fd=");
            stringBuilder.append(this.zzbQW);
        }
        if (this.uri != null) {
            stringBuilder.append(", uri=");
            stringBuilder.append(this.uri);
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        int i2 = i | 1;
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbdY, false);
        zzd.zza(parcel, 3, getDigest(), false);
        zzd.zza(parcel, 4, this.zzbQW, i2, false);
        zzd.zza(parcel, 5, this.uri, i2, false);
        zzd.zzI(parcel, zze);
    }
}
