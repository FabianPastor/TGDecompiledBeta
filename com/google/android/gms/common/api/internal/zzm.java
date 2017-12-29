package com.google.android.gms.common.api.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.internal.zzbz;

public abstract class zzm<R extends Result, A extends zzb> extends BasePendingResult<R> implements zzn<R> {
    private final Api<?> zzfin;
    private final zzc<A> zzfok;

    protected zzm(Api<?> api, GoogleApiClient googleApiClient) {
        super((GoogleApiClient) zzbq.checkNotNull(googleApiClient, "GoogleApiClient must not be null"));
        zzbq.checkNotNull(api, "Api must not be null");
        this.zzfok = api.zzagf();
        this.zzfin = api;
    }

    private final void zzc(RemoteException remoteException) {
        zzu(new Status(8, remoteException.getLocalizedMessage(), null));
    }

    public /* bridge */ /* synthetic */ void setResult(Object obj) {
        super.setResult((Result) obj);
    }

    protected abstract void zza(A a) throws RemoteException;

    public final zzc<A> zzagf() {
        return this.zzfok;
    }

    public final Api<?> zzagl() {
        return this.zzfin;
    }

    public final void zzb(A a) throws DeadObjectException {
        if (a instanceof zzbz) {
            zzb zzals = zzbz.zzals();
        }
        try {
            zza(zzals);
        } catch (RemoteException e) {
            zzc(e);
            throw e;
        } catch (RemoteException e2) {
            zzc(e2);
        }
    }

    public final void zzu(Status status) {
        zzbq.checkArgument(!status.isSuccess(), "Failed result must not be success");
        setResult(zzb(status));
    }
}
