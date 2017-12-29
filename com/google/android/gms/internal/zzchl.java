package com.google.android.gms.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import com.google.android.gms.common.internal.zzd;
import com.google.android.gms.common.internal.zzf;
import com.google.android.gms.common.internal.zzg;

public final class zzchl extends zzd<zzche> {
    public zzchl(Context context, Looper looper, zzf com_google_android_gms_common_internal_zzf, zzg com_google_android_gms_common_internal_zzg) {
        super(context, looper, 93, com_google_android_gms_common_internal_zzf, com_google_android_gms_common_internal_zzg, null);
    }

    public final /* synthetic */ IInterface zzd(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.measurement.internal.IMeasurementService");
        return queryLocalInterface instanceof zzche ? (zzche) queryLocalInterface : new zzchg(iBinder);
    }

    protected final String zzhi() {
        return "com.google.android.gms.measurement.START";
    }

    protected final String zzhj() {
        return "com.google.android.gms.measurement.internal.IMeasurementService";
    }
}
