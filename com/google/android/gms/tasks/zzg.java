package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.ArrayDeque;
import java.util.Queue;

class zzg<TResult> {
    private Queue<zzf<TResult>> zzbLD;
    private boolean zzbLE;
    private final Object zzrN = new Object();

    zzg() {
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void zza(@NonNull Task<TResult> task) {
        synchronized (this.zzrN) {
            if (this.zzbLD == null || this.zzbLE) {
            } else {
                this.zzbLE = true;
            }
        }
    }

    public void zza(@NonNull zzf<TResult> com_google_android_gms_tasks_zzf_TResult) {
        synchronized (this.zzrN) {
            if (this.zzbLD == null) {
                this.zzbLD = new ArrayDeque();
            }
            this.zzbLD.add(com_google_android_gms_tasks_zzf_TResult);
        }
    }
}
