package com.google.android.gms.tasks;

final class zzh implements Runnable {
    private /* synthetic */ Task zzkua;
    private /* synthetic */ zzg zzkug;

    zzh(zzg com_google_android_gms_tasks_zzg, Task task) {
        this.zzkug = com_google_android_gms_tasks_zzg;
        this.zzkua = task;
    }

    public final void run() {
        synchronized (this.zzkug.mLock) {
            if (this.zzkug.zzkuf != null) {
                this.zzkug.zzkuf.onFailure(this.zzkua.getException());
            }
        }
    }
}
