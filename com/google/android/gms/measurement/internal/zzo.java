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

public class zzo extends zze<zzm> {
    public zzo(Context context, Looper looper, zzb com_google_android_gms_common_internal_zze_zzb, zzc com_google_android_gms_common_internal_zze_zzc) {
        super(context, looper, 93, com_google_android_gms_common_internal_zze_zzb, com_google_android_gms_common_internal_zze_zzc, null);
    }

    public /* synthetic */ IInterface zzh(IBinder iBinder) {
        return zzjm(iBinder);
    }

    @NonNull
    protected String zzix() {
        return "com.google.android.gms.measurement.START";
    }

    @NonNull
    protected String zziy() {
        return "com.google.android.gms.measurement.internal.IMeasurementService";
    }

    public zzm zzjm(IBinder iBinder) {
        return zza.zzjl(iBinder);
    }
}
