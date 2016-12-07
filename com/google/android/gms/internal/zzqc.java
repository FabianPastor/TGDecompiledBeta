package com.google.android.gms.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzac;

public class zzqc {

    public interface zzb<R> {
        void setResult(R r);

        void zzz(Status status);
    }

    public static abstract class zza<R extends Result, A extends com.google.android.gms.common.api.Api.zzb> extends zzqe<R> implements zzb<R> {
        private final Api<?> tv;
        private final zzc<A> wx;

        @Deprecated
        protected zza(zzc<A> com_google_android_gms_common_api_Api_zzc_A, GoogleApiClient googleApiClient) {
            super((GoogleApiClient) zzac.zzb((Object) googleApiClient, (Object) "GoogleApiClient must not be null"));
            this.wx = (zzc) zzac.zzy(com_google_android_gms_common_api_Api_zzc_A);
            this.tv = null;
        }

        protected zza(Api<?> api, GoogleApiClient googleApiClient) {
            super((GoogleApiClient) zzac.zzb((Object) googleApiClient, (Object) "GoogleApiClient must not be null"));
            this.wx = api.zzapp();
            this.tv = api;
        }

        private void zza(RemoteException remoteException) {
            zzz(new Status(8, remoteException.getLocalizedMessage(), null));
        }

        public /* synthetic */ void setResult(Object obj) {
            super.zzc((Result) obj);
        }

        protected abstract void zza(A a) throws RemoteException;

        public final zzc<A> zzapp() {
            return this.wx;
        }

        public final Api<?> zzaqn() {
            return this.tv;
        }

        public final void zzb(A a) throws DeadObjectException {
            try {
                zza((com.google.android.gms.common.api.Api.zzb) a);
            } catch (RemoteException e) {
                zza(e);
                throw e;
            } catch (RemoteException e2) {
                zza(e2);
            }
        }

        protected void zzb(R r) {
        }

        public final void zzz(Status status) {
            zzac.zzb(!status.isSuccess(), (Object) "Failed result must not be success");
            Result zzc = zzc(status);
            zzc(zzc);
            zzb(zzc);
        }
    }
}
