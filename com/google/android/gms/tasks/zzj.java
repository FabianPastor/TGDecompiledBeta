package com.google.android.gms.tasks;

final class zzj implements Runnable {
    private /* synthetic */ Task zzbLT;
    private /* synthetic */ zzi zzbMb;

    zzj(zzi com_google_android_gms_tasks_zzi, Task task) {
        this.zzbMb = com_google_android_gms_tasks_zzi;
        this.zzbLT = task;
    }

    public final void run() {
        synchronized (this.zzbMb.mLock) {
            if (this.zzbMb.zzbMa != null) {
                this.zzbMb.zzbMa.onSuccess(this.zzbLT.getResult());
            }
        }
    }
}
