package com.google.android.gms.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzaa;

public class zzqo {

    public interface zzb<R> {
        void setResult(R r);

        void zzaa(Status status);
    }

    public static abstract class zza<R extends Result, A extends com.google.android.gms.common.api.Api.zzb> extends zzqq<R> implements zzb<R> {
        private final Api<?> vS;
        private final zzc<A> yy;

        @Deprecated
        protected zza(zzc<A> com_google_android_gms_common_api_Api_zzc_A, GoogleApiClient googleApiClient) {
            super((GoogleApiClient) zzaa.zzb((Object) googleApiClient, (Object) "GoogleApiClient must not be null"));
            this.yy = (zzc) zzaa.zzy(com_google_android_gms_common_api_Api_zzc_A);
            this.vS = null;
        }

        protected zza(Api<?> api, GoogleApiClient googleApiClient) {
            super((GoogleApiClient) zzaa.zzb((Object) googleApiClient, (Object) "GoogleApiClient must not be null"));
            this.yy = api.zzaqv();
            this.vS = api;
        }

        private void zza(RemoteException remoteException) {
            zzaa(new Status(8, remoteException.getLocalizedMessage(), null));
        }

        public final Api<?> getApi() {
            return this.vS;
        }

        public /* synthetic */ void setResult(Object obj) {
            super.zzc((Result) obj);
        }

        protected abstract void zza(A a) throws RemoteException;

        public final void zzaa(Status status) {
            zzaa.zzb(!status.isSuccess(), (Object) "Failed result must not be success");
            Result zzc = zzc(status);
            zzc(zzc);
            zzb(zzc);
        }

        public final zzc<A> zzaqv() {
            return this.yy;
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
    }
}
