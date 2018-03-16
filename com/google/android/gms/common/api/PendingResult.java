package com.google.android.gms.common.api;

import java.util.concurrent.TimeUnit;

public abstract class PendingResult<R extends Result> {

    public interface zza {
        void zzr(Status status);
    }

    public abstract R await();

    public abstract R await(long j, TimeUnit timeUnit);

    public abstract void cancel();

    public abstract boolean isCanceled();

    public abstract void setResultCallback(ResultCallback<? super R> resultCallback);

    public void zza(zza com_google_android_gms_common_api_PendingResult_zza) {
        throw new UnsupportedOperationException();
    }

    public Integer zzagv() {
        throw new UnsupportedOperationException();
    }
}
