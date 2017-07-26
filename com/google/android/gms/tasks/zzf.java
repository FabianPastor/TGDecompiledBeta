package com.google.android.gms.tasks;

final class zzf implements Runnable {
    private /* synthetic */ Task zzbLT;
    private /* synthetic */ zze zzbLX;

    zzf(zze com_google_android_gms_tasks_zze, Task task) {
        this.zzbLX = com_google_android_gms_tasks_zze;
        this.zzbLT = task;
    }

    public final void run() {
        synchronized (this.zzbLX.mLock) {
            if (this.zzbLX.zzbLW != null) {
                this.zzbLX.zzbLW.onComplete(this.zzbLT);
            }
        }
    }
}
