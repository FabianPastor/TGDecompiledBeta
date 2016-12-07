package com.google.android.gms.internal;

import android.app.Activity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zza;

public class zzaae extends zzzw {
    private zzaap zzaxK;
    private final zza<zzzs<?>> zzazH = new zza();

    private zzaae(zzaax com_google_android_gms_internal_zzaax) {
        super(com_google_android_gms_internal_zzaax);
        this.zzaBs.zza("ConnectionlessLifecycleHelper", (zzaaw) this);
    }

    public static void zza(Activity activity, zzaap com_google_android_gms_internal_zzaap, zzzs<?> com_google_android_gms_internal_zzzs_) {
        zzaax zzs = zzaaw.zzs(activity);
        zzaae com_google_android_gms_internal_zzaae = (zzaae) zzs.zza("ConnectionlessLifecycleHelper", zzaae.class);
        if (com_google_android_gms_internal_zzaae == null) {
            com_google_android_gms_internal_zzaae = new zzaae(zzs);
        }
        com_google_android_gms_internal_zzaae.zzaxK = com_google_android_gms_internal_zzaap;
        com_google_android_gms_internal_zzaae.zza(com_google_android_gms_internal_zzzs_);
        com_google_android_gms_internal_zzaap.zza(com_google_android_gms_internal_zzaae);
    }

    private void zza(zzzs<?> com_google_android_gms_internal_zzzs_) {
        zzac.zzb((Object) com_google_android_gms_internal_zzzs_, (Object) "ApiKey cannot be null");
        this.zzazH.add(com_google_android_gms_internal_zzzs_);
    }

    public void onStart() {
        super.onStart();
        if (!this.zzazH.isEmpty()) {
            this.zzaxK.zza(this);
        }
    }

    public void onStop() {
        super.onStop();
        this.zzaxK.zzb(this);
    }

    protected void zza(ConnectionResult connectionResult, int i) {
        this.zzaxK.zza(connectionResult, i);
    }

    protected void zzuW() {
        this.zzaxK.zzuW();
    }

    zza<zzzs<?>> zzvx() {
        return this.zzazH;
    }
}
