package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.ArrayDeque;
import java.util.Queue;

class zzg<TResult> {
    private Queue<zzf<TResult>> zzbNE;
    private boolean zzbNF;
    private final Object zzrJ = new Object();

    zzg() {
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void zza(@NonNull Task<TResult> task) {
        synchronized (this.zzrJ) {
            if (this.zzbNE == null || this.zzbNF) {
            } else {
                this.zzbNF = true;
            }
        }
    }

    public void zza(@NonNull zzf<TResult> com_google_android_gms_tasks_zzf_TResult) {
        synchronized (this.zzrJ) {
            if (this.zzbNE == null) {
                this.zzbNE = new ArrayDeque();
            }
            this.zzbNE.add(com_google_android_gms_tasks_zzf_TResult);
        }
    }
}
