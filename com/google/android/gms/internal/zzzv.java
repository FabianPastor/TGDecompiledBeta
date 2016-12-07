package com.google.android.gms.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzac;

public class zzzv {

    public interface zzb<R> {
        void setResult(R r);

        void zzA(Status status);
    }

    public static abstract class zza<R extends Result, A extends com.google.android.gms.common.api.Api.zzb> extends zzzx<R> implements zzb<R> {
        private final Api<?> zzawb;
        private final zzc<A> zzayF;

        @Deprecated
        protected zza(zzc<A> com_google_android_gms_common_api_Api_zzc_A, GoogleApiClient googleApiClient) {
            super((GoogleApiClient) zzac.zzb((Object) googleApiClient, (Object) "GoogleApiClient must not be null"));
            this.zzayF = (zzc) zzac.zzw(com_google_android_gms_common_api_Api_zzc_A);
            this.zzawb = null;
        }

        protected zza(Api<?> api, GoogleApiClient googleApiClient) {
            super((GoogleApiClient) zzac.zzb((Object) googleApiClient, (Object) "GoogleApiClient must not be null"));
            this.zzayF = api.zzuH();
            this.zzawb = api;
        }

        private void zzc(RemoteException remoteException) {
            zzA(new Status(8, remoteException.getLocalizedMessage(), null));
        }

        public final Api<?> getApi() {
            return this.zzawb;
        }

        public /* synthetic */ void setResult(Object obj) {
            super.zzb((Result) obj);
        }

        public final void zzA(Status status) {
            zzac.zzb(!status.isSuccess(), (Object) "Failed result must not be success");
            zzb(zzc(status));
        }

        protected abstract void zza(A a) throws RemoteException;

        public final void zzb(A a) throws DeadObjectException {
            try {
                zza(a);
            } catch (RemoteException e) {
                zzc(e);
                throw e;
            } catch (RemoteException e2) {
                zzc(e2);
            }
        }

        public final zzc<A> zzuH() {
            return this.zzayF;
        }
    }
}
