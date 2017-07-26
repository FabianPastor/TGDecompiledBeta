package com.google.android.gms.tasks;

final class zzd implements Runnable {
    private /* synthetic */ Task zzbLT;
    private /* synthetic */ zzc zzbLV;

    zzd(zzc com_google_android_gms_tasks_zzc, Task task) {
        this.zzbLV = com_google_android_gms_tasks_zzc;
        this.zzbLT = task;
    }

    public final void run() {
        try {
            Task task = (Task) this.zzbLV.zzbLR.then(this.zzbLT);
            if (task == null) {
                this.zzbLV.onFailure(new NullPointerException("Continuation returned null"));
                return;
            }
            task.addOnSuccessListener(TaskExecutors.zzbMf, this.zzbLV);
            task.addOnFailureListener(TaskExecutors.zzbMf, this.zzbLV);
        } catch (Exception e) {
            if (e.getCause() instanceof Exception) {
                this.zzbLV.zzbLS.setException((Exception) e.getCause());
            } else {
                this.zzbLV.zzbLS.setException(e);
            }
        } catch (Exception e2) {
            this.zzbLV.zzbLS.setException(e2);
        }
    }
}
