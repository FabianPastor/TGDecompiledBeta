package com.google.android.gms.internal;

import android.app.Activity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.util.zza;

public class zzqw extends zzqp {
    private zzrh xy;
    private final zza<zzql<?>> zx = new zza();

    private zzqw(zzrp com_google_android_gms_internal_zzrp) {
        super(com_google_android_gms_internal_zzrp);
        this.Bf.zza("ConnectionlessLifecycleHelper", (zzro) this);
    }

    public static void zza(Activity activity, zzrh com_google_android_gms_internal_zzrh, zzql<?> com_google_android_gms_internal_zzql_) {
        zzrp zzs = zzro.zzs(activity);
        zzqw com_google_android_gms_internal_zzqw = (zzqw) zzs.zza("ConnectionlessLifecycleHelper", zzqw.class);
        if (com_google_android_gms_internal_zzqw == null) {
            com_google_android_gms_internal_zzqw = new zzqw(zzs);
        }
        com_google_android_gms_internal_zzqw.xy = com_google_android_gms_internal_zzrh;
        com_google_android_gms_internal_zzqw.zza(com_google_android_gms_internal_zzql_);
        com_google_android_gms_internal_zzrh.zza(com_google_android_gms_internal_zzqw);
    }

    private void zza(zzql<?> com_google_android_gms_internal_zzql_) {
        zzaa.zzb((Object) com_google_android_gms_internal_zzql_, (Object) "ApiKey cannot be null");
        this.zx.add(com_google_android_gms_internal_zzql_);
    }

    public void onStart() {
        super.onStart();
        if (!this.zx.isEmpty()) {
            this.xy.zza(this);
        }
    }

    public void onStop() {
        super.onStop();
        this.xy.zzb(this);
    }

    protected void zza(ConnectionResult connectionResult, int i) {
        this.xy.zza(connectionResult, i);
    }

    protected void zzarm() {
        this.xy.zzarm();
    }

    zza<zzql<?>> zzasl() {
        return this.zx;
    }
}
