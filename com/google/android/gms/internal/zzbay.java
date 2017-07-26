package com.google.android.gms.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;

public abstract class zzbay<R extends Result, A extends zzb> extends zzbbe<R> implements zzbaz<R> {
    private final zzc<A> zzaBM;
    private final Api<?> zzayW;

    @Deprecated
    protected zzbay(zzc<A> com_google_android_gms_common_api_Api_zzc_A, GoogleApiClient googleApiClient) {
        super((GoogleApiClient) zzbo.zzb((Object) googleApiClient, (Object) "GoogleApiClient must not be null"));
        this.zzaBM = (zzc) zzbo.zzu(com_google_android_gms_common_api_Api_zzc_A);
        this.zzayW = null;
    }

    protected zzbay(Api<?> api, GoogleApiClient googleApiClient) {
        super((GoogleApiClient) zzbo.zzb((Object) googleApiClient, (Object) "GoogleApiClient must not be null"));
        this.zzaBM = api.zzpd();
        this.zzayW = api;
    }

    private final void zzc(RemoteException remoteException) {
        zzr(new Status(8, remoteException.getLocalizedMessage(), null));
    }

    public /* bridge */ /* synthetic */ void setResult(Object obj) {
        super.setResult((Result) obj);
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

    public final zzc<A> zzpd() {
        return this.zzaBM;
    }

    public final Api<?> zzpg() {
        return this.zzayW;
    }

    public final void zzr(Status status) {
        zzbo.zzb(!status.isSuccess(), (Object) "Failed result must not be success");
        setResult(zzb(status));
    }
}
