package com.google.android.gms.internal;

final class zzbdu implements Runnable {
    private /* synthetic */ String zzO;
    private /* synthetic */ zzbdr zzaEK;
    private /* synthetic */ zzbdt zzaEL;

    zzbdu(zzbdt com_google_android_gms_internal_zzbdt, zzbdr com_google_android_gms_internal_zzbdr, String str) {
        this.zzaEL = com_google_android_gms_internal_zzbdt;
        this.zzaEK = com_google_android_gms_internal_zzbdr;
        this.zzO = str;
    }

    public final void run() {
        if (this.zzaEL.zzLg > 0) {
            this.zzaEK.onCreate(this.zzaEL.zzaEJ != null ? this.zzaEL.zzaEJ.getBundle(this.zzO) : null);
        }
        if (this.zzaEL.zzLg >= 2) {
            this.zzaEK.onStart();
        }
        if (this.zzaEL.zzLg >= 3) {
            this.zzaEK.onResume();
        }
        if (this.zzaEL.zzLg >= 4) {
            this.zzaEK.onStop();
        }
        if (this.zzaEL.zzLg >= 5) {
            this.zzaEK.onDestroy();
        }
    }
}
