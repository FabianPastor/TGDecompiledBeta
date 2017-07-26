package com.google.android.gms.tasks;

final class zzh implements Runnable {
    private /* synthetic */ Task zzbLT;
    private /* synthetic */ zzg zzbLZ;

    zzh(zzg com_google_android_gms_tasks_zzg, Task task) {
        this.zzbLZ = com_google_android_gms_tasks_zzg;
        this.zzbLT = task;
    }

    public final void run() {
        synchronized (this.zzbLZ.mLock) {
            if (this.zzbLZ.zzbLY != null) {
                this.zzbLZ.zzbLY.onFailure(this.zzbLT.getException());
            }
        }
    }
}
