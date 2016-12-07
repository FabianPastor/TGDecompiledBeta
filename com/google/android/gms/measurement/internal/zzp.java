package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zze;
import com.google.android.gms.common.internal.zze.zzb;
import com.google.android.gms.common.internal.zze.zzc;
import com.google.android.gms.measurement.internal.zzm.zza;

public class zzp extends zze<zzm> {
    public zzp(Context context, Looper looper, zzb com_google_android_gms_common_internal_zze_zzb, zzc com_google_android_gms_common_internal_zze_zzc) {
        super(context, looper, 93, com_google_android_gms_common_internal_zze_zzb, com_google_android_gms_common_internal_zze_zzc, null);
    }

    public /* synthetic */ IInterface zzh(IBinder iBinder) {
        return zzjq(iBinder);
    }

    public zzm zzjq(IBinder iBinder) {
        return zza.zzjp(iBinder);
    }

    @NonNull
    protected String zzjx() {
        return "com.google.android.gms.measurement.START";
    }

    @NonNull
    protected String zzjy() {
        return "com.google.android.gms.measurement.internal.IMeasurementService";
    }
}
