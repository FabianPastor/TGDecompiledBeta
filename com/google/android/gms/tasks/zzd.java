package com.google.android.gms.tasks;

final class zzd implements Runnable {
    private /* synthetic */ Task zzbLR;
    private /* synthetic */ zzc zzbLT;

    zzd(zzc com_google_android_gms_tasks_zzc, Task task) {
        this.zzbLT = com_google_android_gms_tasks_zzc;
        this.zzbLR = task;
    }

    public final void run() {
        try {
            Task task = (Task) this.zzbLT.zzbLP.then(this.zzbLR);
            if (task == null) {
                this.zzbLT.onFailure(new NullPointerException("Continuation returned null"));
                return;
            }
            task.addOnSuccessListener(TaskExecutors.zzbMd, this.zzbLT);
            task.addOnFailureListener(TaskExecutors.zzbMd, this.zzbLT);
        } catch (Exception e) {
            if (e.getCause() instanceof Exception) {
                this.zzbLT.zzbLQ.setException((Exception) e.getCause());
            } else {
                this.zzbLT.zzbLQ.setException(e);
            }
        } catch (Exception e2) {
            this.zzbLT.zzbLQ.setException(e2);
        }
    }
}
