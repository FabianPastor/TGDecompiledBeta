package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.content.Context;
import android.os.IInterface;
import android.os.Looper;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.zzc;
import java.util.Set;

public abstract class zzab<T extends IInterface> extends zzd<T> implements zze, zzaf {
    private final Account zzebz;
    private final Set<Scope> zzehs;
    private final zzr zzfpx;

    protected zzab(Context context, Looper looper, int i, zzr com_google_android_gms_common_internal_zzr, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        this(context, looper, zzag.zzco(context), GoogleApiAvailability.getInstance(), i, com_google_android_gms_common_internal_zzr, (ConnectionCallbacks) zzbq.checkNotNull(connectionCallbacks), (OnConnectionFailedListener) zzbq.checkNotNull(onConnectionFailedListener));
    }

    private zzab(Context context, Looper looper, zzag com_google_android_gms_common_internal_zzag, GoogleApiAvailability googleApiAvailability, int i, zzr com_google_android_gms_common_internal_zzr, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        super(context, looper, com_google_android_gms_common_internal_zzag, googleApiAvailability, i, connectionCallbacks == null ? null : new zzac(connectionCallbacks), onConnectionFailedListener == null ? null : new zzad(onConnectionFailedListener), com_google_android_gms_common_internal_zzr.zzakz());
        this.zzfpx = com_google_android_gms_common_internal_zzr;
        this.zzebz = com_google_android_gms_common_internal_zzr.getAccount();
        Set zzakw = com_google_android_gms_common_internal_zzr.zzakw();
        Set<Scope> zzb = zzb(zzakw);
        for (Scope contains : zzb) {
            if (!zzakw.contains(contains)) {
                throw new IllegalStateException("Expanding scopes is not permitted, use implied scopes instead");
            }
        }
        this.zzehs = zzb;
    }

    public final Account getAccount() {
        return this.zzebz;
    }

    public zzc[] zzakl() {
        return new zzc[0];
    }

    protected final Set<Scope> zzakp() {
        return this.zzehs;
    }

    protected Set<Scope> zzb(Set<Scope> set) {
        return set;
    }
}
