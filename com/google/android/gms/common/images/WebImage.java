package com.google.android.gms.common.images;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzz;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

public final class WebImage extends AbstractSafeParcelable {
    public static final Creator<WebImage> CREATOR = new zzb();
    final int mVersionCode;
    private final Uri sa;
    private final int zzakh;
    private final int zzaki;

    WebImage(int i, Uri uri, int i2, int i3) {
        this.mVersionCode = i;
        this.sa = uri;
        this.zzakh = i2;
        this.zzaki = i3;
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

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof WebImage)) {
            return false;
        }
        WebImage webImage = (WebImage) obj;
        return zzz.equal(this.sa, webImage.sa) && this.zzakh == webImage.zzakh && this.zzaki == webImage.zzaki;
    }

    public int getHeight() {
        return this.zzaki;
    }

    public Uri getUrl() {
        return this.sa;
    }

    public int getWidth() {
        return this.zzakh;
    }

    public int hashCode() {
        return zzz.hashCode(this.sa, Integer.valueOf(this.zzakh), Integer.valueOf(this.zzaki));
    }

    public JSONObject toJson() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("url", this.sa.toString());
            jSONObject.put("width", this.zzakh);
            jSONObject.put("height", this.zzaki);
        } catch (JSONException e) {
        }
        return jSONObject;
    }

    public String toString() {
        return String.format(Locale.US, "Image %dx%d %s", new Object[]{Integer.valueOf(this.zzakh), Integer.valueOf(this.zzaki), this.sa.toString()});
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzb.zza(this, parcel, i);
    }
}
