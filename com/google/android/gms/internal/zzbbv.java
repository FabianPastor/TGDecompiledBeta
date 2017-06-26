package com.google.android.gms.internal;

import android.app.Activity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zza;

public class zzbbv extends zzbaz {
    private zzbda zzaAN;
    private final zza<zzbas<?>> zzaCW = new zza();

    private zzbbv(zzbds com_google_android_gms_internal_zzbds) {
        super(com_google_android_gms_internal_zzbds);
        this.zzaEG.zza("ConnectionlessLifecycleHelper", (zzbdr) this);
    }

    public static void zza(Activity activity, zzbda com_google_android_gms_internal_zzbda, zzbas<?> com_google_android_gms_internal_zzbas_) {
        zzbdr.zzn(activity);
        zzbds zzn = zzbdr.zzn(activity);
        zzbbv com_google_android_gms_internal_zzbbv = (zzbbv) zzn.zza("ConnectionlessLifecycleHelper", zzbbv.class);
        if (com_google_android_gms_internal_zzbbv == null) {
            com_google_android_gms_internal_zzbbv = new zzbbv(zzn);
        }
        com_google_android_gms_internal_zzbbv.zzaAN = com_google_android_gms_internal_zzbda;
        zzbo.zzb((Object) com_google_android_gms_internal_zzbas_, (Object) "ApiKey cannot be null");
        com_google_android_gms_internal_zzbbv.zzaCW.add(com_google_android_gms_internal_zzbas_);
        com_google_android_gms_internal_zzbda.zza(com_google_android_gms_internal_zzbbv);
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

    final zza<zzbas<?>> zzpR() {
        return this.zzaCW;
    }

    protected final void zzps() {
        this.zzaAN.zzps();
    }
}
