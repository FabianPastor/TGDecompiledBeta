package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.content.Context;
import android.os.IInterface;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.zzc;
import java.util.Set;

public abstract class zzz<T extends IInterface> extends zzd<T> implements zze, zzad {
    private final zzq zzaCA;
    private final Account zzajb;
    private final Set<Scope> zzame;

    protected zzz(Context context, Looper looper, int i, zzq com_google_android_gms_common_internal_zzq, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        this(context, looper, zzae.zzaC(context), GoogleApiAvailability.getInstance(), i, com_google_android_gms_common_internal_zzq, (ConnectionCallbacks) zzbo.zzu(connectionCallbacks), (OnConnectionFailedListener) zzbo.zzu(onConnectionFailedListener));
    }

    private zzz(Context context, Looper looper, zzae com_google_android_gms_common_internal_zzae, GoogleApiAvailability googleApiAvailability, int i, zzq com_google_android_gms_common_internal_zzq, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        super(context, looper, com_google_android_gms_common_internal_zzae, googleApiAvailability, i, connectionCallbacks == null ? null : new zzaa(connectionCallbacks), onConnectionFailedListener == null ? null : new zzab(onConnectionFailedListener), com_google_android_gms_common_internal_zzq.zzrr());
        this.zzaCA = com_google_android_gms_common_internal_zzq;
        this.zzajb = com_google_android_gms_common_internal_zzq.getAccount();
        Set zzro = com_google_android_gms_common_internal_zzq.zzro();
        Set<Scope> zzb = zzb(zzro);
        for (Scope contains : zzb) {
            if (!zzro.contains(contains)) {
                throw new IllegalStateException("Expanding scopes is not permitted, use implied scopes instead");
            }
        }
        this.zzame = zzb;
    }

    public final Account getAccount() {
        return this.zzajb;
    }

    @NonNull
    protected Set<Scope> zzb(@NonNull Set<Scope> set) {
        return set;
    }

    public zzc[] zzrd() {
        return new zzc[0];
    }

    protected final Set<Scope> zzrh() {
        return this.zzame;
    }

    protected final zzq zzry() {
        return this.zzaCA;
    }
}
