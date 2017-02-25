package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.dynamic.IObjectWrapper;

public class Cap extends zza {
    public static final Creator<Cap> CREATOR = new zzb();
    private static final String TAG = Cap.class.getSimpleName();
    @Nullable
    private final BitmapDescriptor bitmapDescriptor;
    private final int type;
    @Nullable
    private final Float zzbpi;

    protected Cap(int i) {
        this(i, null, null);
    }

    Cap(int i, @Nullable IBinder iBinder, @Nullable Float f) {
        this(i, zzeh(iBinder), f);
    }

    private Cap(int i, @Nullable BitmapDescriptor bitmapDescriptor, @Nullable Float f) {
        boolean z = false;
        boolean z2 = f != null && f.floatValue() > 0.0f;
        if (i != 3 || (bitmapDescriptor != null && z2)) {
            z = true;
        }
        String valueOf = String.valueOf(bitmapDescriptor);
        String valueOf2 = String.valueOf(f);
        zzac.zzb(z, new StringBuilder((String.valueOf(valueOf).length() + 63) + String.valueOf(valueOf2).length()).append("Invalid Cap: type=").append(i).append(" bitmapDescriptor=").append(valueOf).append(" bitmapRefWidth=").append(valueOf2).toString());
        this.type = i;
        this.bitmapDescriptor = bitmapDescriptor;
        this.zzbpi = f;
    }

    protected Cap(@NonNull BitmapDescriptor bitmapDescriptor, float f) {
        this(3, bitmapDescriptor, Float.valueOf(f));
    }

    @Nullable
    private static BitmapDescriptor zzeh(@Nullable IBinder iBinder) {
        return iBinder == null ? null : new BitmapDescriptor(IObjectWrapper.zza.zzcd(iBinder));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Cap)) {
            return false;
        }
        Cap cap = (Cap) obj;
        return this.type == cap.type && zzaa.equal(this.bitmapDescriptor, cap.bitmapDescriptor) && zzaa.equal(this.zzbpi, cap.zzbpi);
    }

    public int getType() {
        return this.type;
    }

    public int hashCode() {
        return zzaa.hashCode(Integer.valueOf(this.type), this.bitmapDescriptor, this.zzbpi);
    }

    public String toString() {
        return "[Cap: type=" + this.type + "]";
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzb.zza(this, parcel, i);
    }

    @Nullable
    public Float zzJG() {
        return this.zzbpi;
    }

    @Nullable
    IBinder zzJH() {
        return this.bitmapDescriptor == null ? null : this.bitmapDescriptor.zzJl().asBinder();
    }

    Cap zzJI() {
        switch (this.type) {
            case 0:
                return new ButtCap();
            case 1:
                return new SquareCap();
            case 2:
                return new RoundCap();
            case 3:
                return new CustomCap(this.bitmapDescriptor, this.zzbpi.floatValue());
            default:
                Log.w(TAG, "Unknown Cap type: " + this.type);
                return this;
        }
    }
}
