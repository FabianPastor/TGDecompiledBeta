package com.google.android.gms.internal;

final class zzbeo implements Runnable {
    private /* synthetic */ String zzO;
    private /* synthetic */ zzbdr zzaEK;
    private /* synthetic */ zzben zzaEZ;

    zzbeo(zzben com_google_android_gms_internal_zzben, zzbdr com_google_android_gms_internal_zzbdr, String str) {
        this.zzaEZ = com_google_android_gms_internal_zzben;
        this.zzaEK = com_google_android_gms_internal_zzbdr;
        this.zzO = str;
    }

    public final void run() {
        if (this.zzaEZ.zzLg > 0) {
            this.zzaEK.onCreate(this.zzaEZ.zzaEJ != null ? this.zzaEZ.zzaEJ.getBundle(this.zzO) : null);
        }
        if (this.zzaEZ.zzLg >= 2) {
            this.zzaEK.onStart();
        }
        if (this.zzaEZ.zzLg >= 3) {
            this.zzaEK.onResume();
        }
        if (this.zzaEZ.zzLg >= 4) {
            this.zzaEK.onStop();
        }
        if (this.zzaEZ.zzLg >= 5) {
            this.zzaEK.onDestroy();
        }
    }
}
