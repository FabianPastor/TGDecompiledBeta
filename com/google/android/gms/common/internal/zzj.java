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
import com.google.android.gms.common.internal.zzk.zza;
import java.util.Set;

public abstract class zzj<T extends IInterface> extends zze<T> implements zze, zza {
    private final Account gj;
    private final Set<Scope> jw;
    private final zzf zP;

    class AnonymousClass1 implements zzb {
        final /* synthetic */ ConnectionCallbacks Ec;

        AnonymousClass1(ConnectionCallbacks connectionCallbacks) {
            this.Ec = connectionCallbacks;
        }

        public void onConnected(@Nullable Bundle bundle) {
            this.Ec.onConnected(bundle);
        }

        public void onConnectionSuspended(int i) {
            this.Ec.onConnectionSuspended(i);
        }
    }

    class AnonymousClass2 implements zzc {
        final /* synthetic */ OnConnectionFailedListener Ed;

        AnonymousClass2(OnConnectionFailedListener onConnectionFailedListener) {
            this.Ed = onConnectionFailedListener;
        }

        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            this.Ed.onConnectionFailed(connectionResult);
        }
    }

    protected zzj(Context context, Looper looper, int i, zzf com_google_android_gms_common_internal_zzf, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        this(context, looper, zzl.zzcc(context), GoogleApiAvailability.getInstance(), i, com_google_android_gms_common_internal_zzf, (ConnectionCallbacks) zzaa.zzy(connectionCallbacks), (OnConnectionFailedListener) zzaa.zzy(onConnectionFailedListener));
    }

    protected zzj(Context context, Looper looper, zzl com_google_android_gms_common_internal_zzl, GoogleApiAvailability googleApiAvailability, int i, zzf com_google_android_gms_common_internal_zzf, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        super(context, looper, com_google_android_gms_common_internal_zzl, googleApiAvailability, i, zza(connectionCallbacks), zza(onConnectionFailedListener), com_google_android_gms_common_internal_zzf.zzavt());
        this.zP = com_google_android_gms_common_internal_zzf;
        this.gj = com_google_android_gms_common_internal_zzf.getAccount();
        this.jw = zzb(com_google_android_gms_common_internal_zzf.zzavq());
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
        return this.gj;
    }

    protected final Set<Scope> zzavi() {
        return this.jw;
    }

    protected final zzf zzawb() {
        return this.zP;
    }

    @NonNull
    protected Set<Scope> zzc(@NonNull Set<Scope> set) {
        return set;
    }
}
