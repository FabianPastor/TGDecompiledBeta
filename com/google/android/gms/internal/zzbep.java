package com.google.android.gms.internal;

final class zzbep implements Runnable {
    private /* synthetic */ String zzO;
    private /* synthetic */ zzbds zzaEK;
    private /* synthetic */ zzbeo zzaEZ;

    zzbep(zzbeo com_google_android_gms_internal_zzbeo, zzbds com_google_android_gms_internal_zzbds, String str) {
        this.zzaEZ = com_google_android_gms_internal_zzbeo;
        this.zzaEK = com_google_android_gms_internal_zzbds;
        this.zzO = str;
    }

    public final void run() {
        if (this.zzaEZ.zzLe > 0) {
            this.zzaEK.onCreate(this.zzaEZ.zzaEJ != null ? this.zzaEZ.zzaEJ.getBundle(this.zzO) : null);
        }
        if (this.zzaEZ.zzLe >= 2) {
            this.zzaEK.onStart();
        }
        if (this.zzaEZ.zzLe >= 3) {
            this.zzaEK.onResume();
        }
        if (this.zzaEZ.zzLe >= 4) {
            this.zzaEK.onStop();
        }
        if (this.zzaEZ.zzLe >= 5) {
            this.zzaEK.onDestroy();
        }
    }
}
