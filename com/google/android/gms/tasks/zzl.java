package com.google.android.gms.tasks;

import java.util.ArrayDeque;
import java.util.Queue;

final class zzl<TResult> {
    private final Object mLock = new Object();
    private Queue<zzk<TResult>> zzkuj;
    private boolean zzkuk;

    zzl() {
    }

    public final void zza(zzk<TResult> com_google_android_gms_tasks_zzk_TResult) {
        synchronized (this.mLock) {
            if (this.zzkuj == null) {
                this.zzkuj = new ArrayDeque();
            }
            this.zzkuj.add(com_google_android_gms_tasks_zzk_TResult);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void zzb(Task<TResult> task) {
        synchronized (this.mLock) {
            if (this.zzkuj == null || this.zzkuk) {
            } else {
                this.zzkuk = true;
            }
        }
    }
}
