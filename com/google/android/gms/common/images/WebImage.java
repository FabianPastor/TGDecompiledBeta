package com.google.android.gms.common.images;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbe;
import java.util.Arrays;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

public final class WebImage extends zza {
    public static final Creator<WebImage> CREATOR = new zze();
    private int zzaku;
    private final Uri zzauQ;
    private final int zzrY;
    private final int zzrZ;

    WebImage(int i, Uri uri, int i2, int i3) {
        this.zzaku = i;
        this.zzauQ = uri;
        this.zzrY = i2;
        this.zzrZ = i3;
    }

    public WebImage(Uri uri) throws IllegalArgumentException {
        this(uri, 0, 0);
    }

    public WebImage(Uri uri, int i, int i2) throws IllegalArgumentException {
        this(1, uri, i, i2);
        if (uri == null) {
            throw new IllegalArgumentException("url cannot be null");
        } else if (i < 0 || i2 < 0) {
            throw new IllegalArgumentException("width and height must not be negative");
        }
    }

    public WebImage(JSONObject jSONObject) throws IllegalArgumentException {
        this(zzp(jSONObject), jSONObject.optInt("width", 0), jSONObject.optInt("height", 0));
    }

    private static Uri zzp(JSONObject jSONObject) {
        Uri uri = null;
        if (jSONObject.has("url")) {
            try {
                uri = Uri.parse(jSONObject.getString("url"));
            } catch (JSONException e) {
            }
        }
        return uri;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof WebImage)) {
            return false;
        }
        WebImage webImage = (WebImage) obj;
        return zzbe.equal(this.zzauQ, webImage.zzauQ) && this.zzrY == webImage.zzrY && this.zzrZ == webImage.zzrZ;
    }

    public final int getHeight() {
        return this.zzrZ;
    }

    public final Uri getUrl() {
        return this.zzauQ;
    }

    public final int getWidth() {
        return this.zzrY;
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{this.zzauQ, Integer.valueOf(this.zzrY), Integer.valueOf(this.zzrZ)});
    }

    public final JSONObject toJson() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("url", this.zzauQ.toString());
            jSONObject.put("width", this.zzrY);
            jSONObject.put("height", this.zzrZ);
        } catch (JSONException e) {
        }
        return jSONObject;
    }

    public final String toString() {
        return String.format(Locale.US, "Image %dx%d %s", new Object[]{Integer.valueOf(this.zzrY), Integer.valueOf(this.zzrZ), this.zzauQ.toString()});
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.zzaku);
        zzd.zza(parcel, 2, getUrl(), i, false);
        zzd.zzc(parcel, 3, getWidth());
        zzd.zzc(parcel, 4, getHeight());
        zzd.zzI(parcel, zze);
    }
}
