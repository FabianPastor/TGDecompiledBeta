package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.os.IInterface;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.zze.zzb;
import com.google.android.gms.common.internal.zze.zzc;
import com.google.android.gms.common.internal.zzm.zza;
import java.util.Set;

public abstract class zzl<T extends IInterface> extends zze<T> implements zze, zza {
    private final Account ec;
    private final Set<Scope> hm;
    private final zzh xB;

    class AnonymousClass1 implements zzb {
        final /* synthetic */ ConnectionCallbacks Cq;

        AnonymousClass1(ConnectionCallbacks connectionCallbacks) {
            this.Cq = connectionCallbacks;
        }

        public void onConnected(@Nullable Bundle bundle) {
            this.Cq.onConnected(bundle);
        }

        public void onConnectionSuspended(int i) {
            this.Cq.onConnectionSuspended(i);
        }
    }

    class AnonymousClass2 implements zzc {
        final /* synthetic */ OnConnectionFailedListener Cr;

        AnonymousClass2(OnConnectionFailedListener onConnectionFailedListener) {
            this.Cr = onConnectionFailedListener;
        }

        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            this.Cr.onConnectionFailed(connectionResult);
        }
    }

    protected zzl(Context context, Looper looper, int i, zzh com_google_android_gms_common_internal_zzh, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        this(context, looper, zzn.zzcf(context), GoogleApiAvailability.getInstance(), i, com_google_android_gms_common_internal_zzh, (ConnectionCallbacks) zzac.zzy(connectionCallbacks), (OnConnectionFailedListener) zzac.zzy(onConnectionFailedListener));
    }

    protected zzl(Context context, Looper looper, zzn com_google_android_gms_common_internal_zzn, GoogleApiAvailability googleApiAvailability, int i, zzh com_google_android_gms_common_internal_zzh, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        super(context, looper, com_google_android_gms_common_internal_zzn, googleApiAvailability, i, zza(connectionCallbacks), zza(onConnectionFailedListener), com_google_android_gms_common_internal_zzh.zzauk());
        this.xB = com_google_android_gms_common_internal_zzh;
        this.ec = com_google_android_gms_common_internal_zzh.getAccount();
        this.hm = zzb(com_google_android_gms_common_internal_zzh.zzauh());
    }

    @Nullable
    private static zzb zza(ConnectionCallbacks connectionCallbacks) {
        return connectionCallbacks == null ? null : new AnonymousClass1(connectionCallbacks);
    }

    @Nullable
    private static zzc zza(OnConnectionFailedListener onConnectionFailedListener) {
        return onConnectionFailedListener == null ? null : new AnonymousClass2(onConnectionFailedListener);
    }

    private Set<Scope> zzb(@NonNull Set<Scope> set) {
        Set<Scope> zzc = zzc(set);
        for (Scope contains : zzc) {
            if (!set.contains(contains)) {
                throw new IllegalStateException("Expanding scopes is not permitted, use implied scopes instead");
            }
        }
        return zzc;
    }

    public final Account getAccount() {
        return this.ec;
    }

    protected final Set<Scope> zzatz() {
        return this.hm;
    }

    protected final zzh zzaus() {
        return this.xB;
    }

    @NonNull
    protected Set<Scope> zzc(@NonNull Set<Scope> set) {
        return set;
    }
}
