package com.google.firebase.iid;

import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.tasks.TaskCompletionSource;

abstract class zzr<T> {
    final int what;
    final TaskCompletionSource<T> zzgrq = new TaskCompletionSource();
    final int zzift;
    final Bundle zznzj;

    zzr(int i, int i2, Bundle bundle) {
        this.zzift = i;
        this.what = i2;
        this.zznzj = bundle;
    }

    final void finish(T t) {
        if (Log.isLoggable("MessengerIpcClient", 3)) {
            String valueOf = String.valueOf(this);
            String valueOf2 = String.valueOf(t);
            Log.d("MessengerIpcClient", new StringBuilder((String.valueOf(valueOf).length() + 16) + String.valueOf(valueOf2).length()).append("Finishing ").append(valueOf).append(" with ").append(valueOf2).toString());
        }
        this.zzgrq.setResult(t);
    }

    public String toString() {
        int i = this.what;
        int i2 = this.zzift;
        return "Request { what=" + i + " id=" + i2 + " oneWay=" + zzcje() + "}";
    }

    abstract void zzac(Bundle bundle);

    final void zzb(zzs com_google_firebase_iid_zzs) {
        if (Log.isLoggable("MessengerIpcClient", 3)) {
            String valueOf = String.valueOf(this);
            String valueOf2 = String.valueOf(com_google_firebase_iid_zzs);
            Log.d("MessengerIpcClient", new StringBuilder((String.valueOf(valueOf).length() + 14) + String.valueOf(valueOf2).length()).append("Failing ").append(valueOf).append(" with ").append(valueOf2).toString());
        }
        this.zzgrq.setException(com_google_firebase_iid_zzs);
    }

    abstract boolean zzcje();
}
