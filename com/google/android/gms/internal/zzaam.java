package com.google.android.gms.internal;

import android.app.Activity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zza;

public class zzaam extends zzaae {
    private final zza<zzzz<?>> zzaBh = new zza();
    private zzaax zzayX;

    private zzaam(zzabf com_google_android_gms_internal_zzabf) {
        super(com_google_android_gms_internal_zzabf);
        this.zzaCR.zza("ConnectionlessLifecycleHelper", (zzabe) this);
    }

    public static void zza(Activity activity, zzaax com_google_android_gms_internal_zzaax, zzzz<?> com_google_android_gms_internal_zzzz_) {
        zzabf zzs = zzabe.zzs(activity);
        zzaam com_google_android_gms_internal_zzaam = (zzaam) zzs.zza("ConnectionlessLifecycleHelper", zzaam.class);
        if (com_google_android_gms_internal_zzaam == null) {
            com_google_android_gms_internal_zzaam = new zzaam(zzs);
        }
        com_google_android_gms_internal_zzaam.zzayX = com_google_android_gms_internal_zzaax;
        com_google_android_gms_internal_zzaam.zza(com_google_android_gms_internal_zzzz_);
        com_google_android_gms_internal_zzaax.zza(com_google_android_gms_internal_zzaam);
    }

    private void zza(zzzz<?> com_google_android_gms_internal_zzzz_) {
        zzac.zzb((Object) com_google_android_gms_internal_zzzz_, (Object) "ApiKey cannot be null");
        this.zzaBh.add(com_google_android_gms_internal_zzzz_);
    }

    public void onStart() {
        super.onStart();
        if (!this.zzaBh.isEmpty()) {
            this.zzayX.zza(this);
        }
    }

    public void onStop() {
        super.onStop();
        this.zzayX.zzb(this);
    }

    protected void zza(ConnectionResult connectionResult, int i) {
        this.zzayX.zza(connectionResult, i);
    }

    protected void zzvx() {
        this.zzayX.zzvx();
    }

    zza<zzzz<?>> zzwb() {
        return this.zzaBh;
    }
}
