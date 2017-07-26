package com.google.android.gms.internal;

import android.app.Activity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zza;

public class zzbbw extends zzbba {
    private zzbdb zzaAN;
    private final zza<zzbat<?>> zzaCW = new zza();

    private zzbbw(zzbdt com_google_android_gms_internal_zzbdt) {
        super(com_google_android_gms_internal_zzbdt);
        this.zzaEG.zza("ConnectionlessLifecycleHelper", (zzbds) this);
    }

    public static void zza(Activity activity, zzbdb com_google_android_gms_internal_zzbdb, zzbat<?> com_google_android_gms_internal_zzbat_) {
        zzbds.zzn(activity);
        zzbdt zzn = zzbds.zzn(activity);
        zzbbw com_google_android_gms_internal_zzbbw = (zzbbw) zzn.zza("ConnectionlessLifecycleHelper", zzbbw.class);
        if (com_google_android_gms_internal_zzbbw == null) {
            com_google_android_gms_internal_zzbbw = new zzbbw(zzn);
        }
        com_google_android_gms_internal_zzbbw.zzaAN = com_google_android_gms_internal_zzbdb;
        zzbo.zzb((Object) com_google_android_gms_internal_zzbat_, (Object) "ApiKey cannot be null");
        com_google_android_gms_internal_zzbbw.zzaCW.add(com_google_android_gms_internal_zzbat_);
        com_google_android_gms_internal_zzbdb.zza(com_google_android_gms_internal_zzbbw);
    }

    private final void zzpS() {
        if (!this.zzaCW.isEmpty()) {
            this.zzaAN.zza(this);
        }
    }

    public final void onResume() {
        super.onResume();
        zzpS();
    }

    public final void onStart() {
        super.onStart();
        zzpS();
    }

    public final void onStop() {
        super.onStop();
        this.zzaAN.zzb(this);
    }

    protected final void zza(ConnectionResult connectionResult, int i) {
        this.zzaAN.zza(connectionResult, i);
    }

    final zza<zzbat<?>> zzpR() {
        return this.zzaCW;
    }

    protected final void zzps() {
        this.zzaAN.zzps();
    }
}
