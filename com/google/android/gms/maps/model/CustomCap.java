package com.google.android.gms.maps.model;

import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;

public final class CustomCap extends Cap {
    public final BitmapDescriptor bitmapDescriptor;
    public final float refWidth;

    public CustomCap(@NonNull BitmapDescriptor bitmapDescriptor) {
        this(bitmapDescriptor, 10.0f);
    }

    public CustomCap(@NonNull BitmapDescriptor bitmapDescriptor, float f) {
        super((BitmapDescriptor) zzac.zzb((Object) bitmapDescriptor, (Object) "bitmapDescriptor must not be null"), zza(f, "refWidth must be positive"));
        this.bitmapDescriptor = bitmapDescriptor;
        this.refWidth = f;
    }

    private static float zza(float f, String str) {
        if (f > 0.0f) {
            return f;
        }
        throw new IllegalArgumentException(str);
    }

    public String toString() {
        String valueOf = String.valueOf(this.bitmapDescriptor);
        return new StringBuilder(String.valueOf(valueOf).length() + 55).append("[CustomCap: bitmapDescriptor=").append(valueOf).append(" refWidth=").append(this.refWidth).append("]").toString();
    }
}
