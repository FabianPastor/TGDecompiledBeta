package com.google.android.gms.tasks;

import java.util.concurrent.Callable;

final class zzo implements Runnable {
    private /* synthetic */ Callable zzZo;
    private /* synthetic */ zzn zzbMi;

    zzo(zzn com_google_android_gms_tasks_zzn, Callable callable) {
        this.zzbMi = com_google_android_gms_tasks_zzn;
        this.zzZo = callable;
    }

    public final void run() {
        try {
            this.zzbMi.setResult(this.zzZo.call());
        } catch (Exception e) {
            this.zzbMi.setException(e);
        }
    }
}
