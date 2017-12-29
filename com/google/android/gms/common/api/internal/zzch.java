package com.google.android.gms.common.api.internal;

final class zzch implements Runnable {
    private /* synthetic */ String zzat;
    private /* synthetic */ LifecycleCallback zzfuh;
    private /* synthetic */ zzcg zzfui;

    zzch(zzcg com_google_android_gms_common_api_internal_zzcg, LifecycleCallback lifecycleCallback, String str) {
        this.zzfui = com_google_android_gms_common_api_internal_zzcg;
        this.zzfuh = lifecycleCallback;
        this.zzat = str;
    }

    public final void run() {
        if (this.zzfui.zzcbc > 0) {
            this.zzfuh.onCreate(this.zzfui.zzfug != null ? this.zzfui.zzfug.getBundle(this.zzat) : null);
        }
        if (this.zzfui.zzcbc >= 2) {
            this.zzfuh.onStart();
        }
        if (this.zzfui.zzcbc >= 3) {
            this.zzfuh.onResume();
        }
        if (this.zzfui.zzcbc >= 4) {
            this.zzfuh.onStop();
        }
        if (this.zzfui.zzcbc >= 5) {
            this.zzfuh.onDestroy();
        }
    }
}
