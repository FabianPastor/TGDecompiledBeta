package com.google.android.gms.tasks;

import java.util.concurrent.Callable;

final class zzo implements Runnable {
    private /* synthetic */ Callable val$callable;
    private /* synthetic */ zzn zzkur;

    zzo(zzn com_google_android_gms_tasks_zzn, Callable callable) {
        this.zzkur = com_google_android_gms_tasks_zzn;
        this.val$callable = callable;
    }

    public final void run() {
        try {
            this.zzkur.setResult(this.val$callable.call());
        } catch (Exception e) {
            this.zzkur.setException(e);
        }
    }
}
