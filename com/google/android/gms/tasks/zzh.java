package com.google.android.gms.tasks;

final class zzh implements Runnable {
    private /* synthetic */ Task zzbLR;
    private /* synthetic */ zzg zzbLX;

    zzh(zzg com_google_android_gms_tasks_zzg, Task task) {
        this.zzbLX = com_google_android_gms_tasks_zzg;
        this.zzbLR = task;
    }

    public final void run() {
        synchronized (this.zzbLX.mLock) {
            if (this.zzbLX.zzbLW != null) {
                this.zzbLX.zzbLW.onFailure(this.zzbLR.getException());
            }
        }
    }
}
