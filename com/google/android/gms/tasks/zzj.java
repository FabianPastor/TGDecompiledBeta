package com.google.android.gms.tasks;

final class zzj implements Runnable {
    private /* synthetic */ Task zzbLR;
    private /* synthetic */ zzi zzbLZ;

    zzj(zzi com_google_android_gms_tasks_zzi, Task task) {
        this.zzbLZ = com_google_android_gms_tasks_zzi;
        this.zzbLR = task;
    }

    public final void run() {
        synchronized (this.zzbLZ.mLock) {
            if (this.zzbLZ.zzbLY != null) {
                this.zzbLZ.zzbLY.onSuccess(this.zzbLR.getResult());
            }
        }
    }
}
