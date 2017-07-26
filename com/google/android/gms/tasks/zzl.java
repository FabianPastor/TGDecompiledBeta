package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.ArrayDeque;
import java.util.Queue;

final class zzl<TResult> {
    private final Object mLock = new Object();
    private Queue<zzk<TResult>> zzbMc;
    private boolean zzbMd;

    zzl() {
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void zza(@NonNull Task<TResult> task) {
        synchronized (this.mLock) {
            if (this.zzbMc == null || this.zzbMd) {
            } else {
                this.zzbMd = true;
            }
        }
    }

    public final void zza(@NonNull zzk<TResult> com_google_android_gms_tasks_zzk_TResult) {
        synchronized (this.mLock) {
            if (this.zzbMc == null) {
                this.zzbMc = new ArrayDeque();
            }
            this.zzbMc.add(com_google_android_gms_tasks_zzk_TResult);
        }
    }
}
