package com.google.android.gms.tasks;

final class zzb implements Runnable {
    private /* synthetic */ Task zzbLT;
    private /* synthetic */ zza zzbLU;

    zzb(zza com_google_android_gms_tasks_zza, Task task) {
        this.zzbLU = com_google_android_gms_tasks_zza;
        this.zzbLT = task;
    }

    public final void run() {
        try {
            this.zzbLU.zzbLS.setResult(this.zzbLU.zzbLR.then(this.zzbLT));
        } catch (Exception e) {
            if (e.getCause() instanceof Exception) {
                this.zzbLU.zzbLS.setException((Exception) e.getCause());
            } else {
                this.zzbLU.zzbLS.setException(e);
            }
        } catch (Exception e2) {
            this.zzbLU.zzbLS.setException(e2);
        }
    }
}
