package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.ArrayDeque;
import java.util.Queue;

class zzg<TResult> {
    private Queue<zzf<TResult>> aJF;
    private boolean aJG;
    private final Object zzakd = new Object();

    zzg() {
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void zza(@NonNull Task<TResult> task) {
        synchronized (this.zzakd) {
            if (this.aJF == null || this.aJG) {
            } else {
                this.aJG = true;
            }
        }
    }

    public void zza(@NonNull zzf<TResult> com_google_android_gms_tasks_zzf_TResult) {
        synchronized (this.zzakd) {
            if (this.aJF == null) {
                this.aJF = new ArrayDeque();
            }
            this.aJF.add(com_google_android_gms_tasks_zzf_TResult);
        }
    }
}
