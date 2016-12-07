package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.ArrayDeque;
import java.util.Queue;

class zzg<TResult> {
    private Queue<zzf<TResult>> aMQ;
    private boolean aMR;
    private final Object zzako = new Object();

    zzg() {
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void zza(@NonNull Task<TResult> task) {
        synchronized (this.zzako) {
            if (this.aMQ == null || this.aMR) {
            } else {
                this.aMR = true;
            }
        }
    }

    public void zza(@NonNull zzf<TResult> com_google_android_gms_tasks_zzf_TResult) {
        synchronized (this.zzako) {
            if (this.aMQ == null) {
                this.aMQ = new ArrayDeque();
            }
            this.aMQ.add(com_google_android_gms_tasks_zzf_TResult);
        }
    }
}
