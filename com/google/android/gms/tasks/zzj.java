package com.google.android.gms.tasks;

final class zzj implements Runnable {
    private /* synthetic */ Task zzkua;
    private /* synthetic */ zzi zzkui;

    zzj(zzi com_google_android_gms_tasks_zzi, Task task) {
        this.zzkui = com_google_android_gms_tasks_zzi;
        this.zzkua = task;
    }

    public final void run() {
        synchronized (this.zzkui.mLock) {
            if (this.zzkui.zzkuh != null) {
                this.zzkui.zzkuh.onSuccess(this.zzkua.getResult());
            }
        }
    }
}
