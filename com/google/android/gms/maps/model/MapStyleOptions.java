package com.google.android.gms.maps.model;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.util.zzo;
import java.io.IOException;

public final class MapStyleOptions extends zza {
    public static final Creator<MapStyleOptions> CREATOR = new zzf();
    private static final String TAG = MapStyleOptions.class.getSimpleName();
    private final int mVersionCode;
    private String zzbpa;

    MapStyleOptions(int i, String str) {
        this.mVersionCode = i;
        this.zzbpa = str;
    }

    public MapStyleOptions(String str) {
        this.mVersionCode = 1;
        this.zzbpa = str;
    }

    public static MapStyleOptions loadRawResourceStyle(Context context, int i) throws NotFoundException {
        try {
            return new MapStyleOptions(new String(zzo.zzk(context.getResources().openRawResource(i)), "UTF-8"));
        } catch (IOException e) {
            String valueOf = String.valueOf(e);
            throw new NotFoundException(new StringBuilder(String.valueOf(valueOf).length() + 37).append("Failed to read resource ").append(i).append(": ").append(valueOf).toString());
        }
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzf.zza(this, parcel, i);
    }

    public String zzIU() {
        return this.zzbpa;
    }
}
