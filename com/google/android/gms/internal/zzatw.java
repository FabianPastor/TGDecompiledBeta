package com.google.android.gms.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzf;
import com.google.android.gms.common.internal.zzf.zzb;
import com.google.android.gms.common.internal.zzf.zzc;
import com.google.android.gms.internal.zzatt.zza;

public class zzatw extends zzf<zzatt> {
    public zzatw(Context context, Looper looper, zzb com_google_android_gms_common_internal_zzf_zzb, zzc com_google_android_gms_common_internal_zzf_zzc) {
        super(context, looper, 93, com_google_android_gms_common_internal_zzf_zzb, com_google_android_gms_common_internal_zzf_zzc, null);
    }

    @NonNull
    protected String zzeA() {
        return "com.google.android.gms.measurement.internal.IMeasurementService";
    }

    public zzatt zzet(IBinder iBinder) {
        return zza.zzes(iBinder);
    }

    @NonNull
    protected String zzez() {
        return "com.google.android.gms.measurement.START";
    }

    public /* synthetic */ IInterface zzh(IBinder iBinder) {
        return zzet(iBinder);
    }
}
