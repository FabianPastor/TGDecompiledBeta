package com.google.android.gms.common.api;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzan;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.internal.zzj;
import com.google.android.gms.common.internal.zzp;
import com.google.android.gms.common.internal.zzr;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.telegram.tgnet.ConnectionsManager;

public final class Api<O extends ApiOptions> {
    private final String mName;
    private final zza<?, O> zzfls;
    private final zzh<?, O> zzflt = null;
    private final zzf<?> zzflu;
    private final zzi<?> zzflv;

    public interface ApiOptions {

        public interface HasOptions extends ApiOptions {
        }

        public interface NotRequiredOptions extends ApiOptions {
        }

        public interface HasAccountOptions extends HasOptions, NotRequiredOptions {
            Account getAccount();
        }

        public interface HasGoogleSignInAccountOptions extends HasOptions {
            GoogleSignInAccount getGoogleSignInAccount();
        }

        public interface Optional extends HasOptions, NotRequiredOptions {
        }
    }

    public interface zzb {
    }

    public static class zzc<C extends zzb> {
    }

    public static abstract class zzd<T extends zzb, O> {
        public int getPriority() {
            return ConnectionsManager.DEFAULT_DATACENTER_ID;
        }

        public List<Scope> zzr(O o) {
            return Collections.emptyList();
        }
    }

    public static abstract class zza<T extends zze, O> extends zzd<T, O> {
        public abstract T zza(Context context, Looper looper, zzr com_google_android_gms_common_internal_zzr, O o, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener);
    }

    public interface zze extends zzb {
        void disconnect();

        void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

        Intent getSignInIntent();

        boolean isConnected();

        boolean isConnecting();

        void zza(zzan com_google_android_gms_common_internal_zzan, Set<Scope> set);

        void zza(zzj com_google_android_gms_common_internal_zzj);

        void zza(zzp com_google_android_gms_common_internal_zzp);

        boolean zzaay();

        boolean zzabj();

        boolean zzagg();

        IBinder zzagh();

        String zzagi();
    }

    public static final class zzf<C extends zze> extends zzc<C> {
    }

    public interface zzg<T extends IInterface> extends zzb {
    }

    public static abstract class zzh<T extends zzg, O> extends zzd<T, O> {
    }

    public static final class zzi<C extends zzg> extends zzc<C> {
    }

    public <C extends zze> Api(String str, zza<C, O> com_google_android_gms_common_api_Api_zza_C__O, zzf<C> com_google_android_gms_common_api_Api_zzf_C) {
        zzbq.checkNotNull(com_google_android_gms_common_api_Api_zza_C__O, "Cannot construct an Api with a null ClientBuilder");
        zzbq.checkNotNull(com_google_android_gms_common_api_Api_zzf_C, "Cannot construct an Api with a null ClientKey");
        this.mName = str;
        this.zzfls = com_google_android_gms_common_api_Api_zza_C__O;
        this.zzflu = com_google_android_gms_common_api_Api_zzf_C;
        this.zzflv = null;
    }

    public final String getName() {
        return this.mName;
    }

    public final zzd<?, O> zzagd() {
        return this.zzfls;
    }

    public final zza<?, O> zzage() {
        zzbq.zza(this.zzfls != null, "This API was constructed with a SimpleClientBuilder. Use getSimpleClientBuilder");
        return this.zzfls;
    }

    public final zzc<?> zzagf() {
        if (this.zzflu != null) {
            return this.zzflu;
        }
        throw new IllegalStateException("This API was constructed with null client keys. This should not be possible.");
    }
}
