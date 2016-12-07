package com.google.android.gms.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzf;
import com.google.android.gms.common.internal.zzf.zzb;
import com.google.android.gms.common.internal.zzf.zzc;
import com.google.android.gms.internal.zzate.zza;

public class zzath extends zzf<zzate> {
    public zzath(Context context, Looper looper, zzb com_google_android_gms_common_internal_zzf_zzb, zzc com_google_android_gms_common_internal_zzf_zzc) {
        super(context, looper, 93, com_google_android_gms_common_internal_zzf_zzb, com_google_android_gms_common_internal_zzf_zzc, null);
    }

    public zzate zzes(IBinder iBinder) {
        return zza.zzer(iBinder);
    }

    @NonNull
    protected String zzeu() {
        return "com.google.android.gms.measurement.START";
    }

    @NonNull
    protected String zzev() {
        return "com.google.android.gms.measurement.internal.IMeasurementService";
    }

    public /* synthetic */ IInterface zzh(IBinder iBinder) {
        return zzes(iBinder);
    }
}
