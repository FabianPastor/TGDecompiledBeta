package com.google.android.gms.common.api.internal;

final class zzdc implements Runnable {
    private /* synthetic */ String zzat;
    private /* synthetic */ LifecycleCallback zzfuh;
    private /* synthetic */ zzdb zzfuw;

    zzdc(zzdb com_google_android_gms_common_api_internal_zzdb, LifecycleCallback lifecycleCallback, String str) {
        this.zzfuw = com_google_android_gms_common_api_internal_zzdb;
        this.zzfuh = lifecycleCallback;
        this.zzat = str;
    }

    public final void run() {
        if (this.zzfuw.zzcbc > 0) {
            this.zzfuh.onCreate(this.zzfuw.zzfug != null ? this.zzfuw.zzfug.getBundle(this.zzat) : null);
        }
        if (this.zzfuw.zzcbc >= 2) {
            this.zzfuh.onStart();
        }
        if (this.zzfuw.zzcbc >= 3) {
            this.zzfuh.onResume();
        }
        if (this.zzfuw.zzcbc >= 4) {
            this.zzfuh.onStop();
        }
        if (this.zzfuw.zzcbc >= 5) {
            this.zzfuh.onDestroy();
        }
    }
}
