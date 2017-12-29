package com.google.android.gms.tasks;

final class zzf implements Runnable {
    private /* synthetic */ Task zzkua;
    private /* synthetic */ zze zzkue;

    zzf(zze com_google_android_gms_tasks_zze, Task task) {
        this.zzkue = com_google_android_gms_tasks_zze;
        this.zzkua = task;
    }

    public final void run() {
        synchronized (this.zzkue.mLock) {
            if (this.zzkue.zzkud != null) {
                this.zzkue.zzkud.onComplete(this.zzkua);
            }
        }
    }
}
