package com.google.android.gms.common.api;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.support.annotation.Nullable;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzp;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.telegram.tgnet.ConnectionsManager;

public final class Api<O extends ApiOptions> {
    private final String mName;
    private final zza<?, O> xk;
    private final zzh<?, O> xl = null;
    private final zzf<?> xm;
    private final zzi<?> xn;

    public interface ApiOptions {

        public interface HasOptions extends ApiOptions {
        }

        public interface NotRequiredOptions extends ApiOptions {
        }

        public static final class NoOptions implements NotRequiredOptions {
            private NoOptions() {
            }
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

        public List<Scope> zzp(O o) {
            return Collections.emptyList();
        }
    }

    public static abstract class zza<T extends zze, O> extends zzd<T, O> {
        public abstract T zza(Context context, Looper looper, com.google.android.gms.common.internal.zzf com_google_android_gms_common_internal_zzf, O o, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener);
    }

    public interface zze extends zzb {
        void disconnect();

        void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

        boolean isConnected();

        boolean isConnecting();

        void zza(com.google.android.gms.common.internal.zze.zzf com_google_android_gms_common_internal_zze_zzf);

        void zza(zzp com_google_android_gms_common_internal_zzp, Set<Scope> set);

        boolean zzain();

        boolean zzajc();

        Intent zzajd();

        boolean zzaqx();

        @Nullable
        IBinder zzaqy();
    }

    public static final class zzf<C extends zze> extends zzc<C> {
    }

    public interface zzg<T extends IInterface> extends zzb {
        void zza(int i, T t);

        T zzh(IBinder iBinder);

        String zzjx();

        String zzjy();
    }

    public static abstract class zzh<T extends zzg, O> extends zzd<T, O> {
        public abstract int zzaqz();

        public abstract T zzr(O o);
    }

    public static final class zzi<C extends zzg> extends zzc<C> {
    }

    public <C extends zze> Api(String str, zza<C, O> com_google_android_gms_common_api_Api_zza_C__O, zzf<C> com_google_android_gms_common_api_Api_zzf_C) {
        zzaa.zzb((Object) com_google_android_gms_common_api_Api_zza_C__O, (Object) "Cannot construct an Api with a null ClientBuilder");
        zzaa.zzb((Object) com_google_android_gms_common_api_Api_zzf_C, (Object) "Cannot construct an Api with a null ClientKey");
        this.mName = str;
        this.xk = com_google_android_gms_common_api_Api_zza_C__O;
        this.xm = com_google_android_gms_common_api_Api_zzf_C;
        this.xn = null;
    }

    public String getName() {
        return this.mName;
    }

    public zzd<?, O> zzaqs() {
        return zzaqw() ? null : this.xk;
    }

    public zza<?, O> zzaqt() {
        zzaa.zza(this.xk != null, (Object) "This API was constructed with a SimpleClientBuilder. Use getSimpleClientBuilder");
        return this.xk;
    }

    public zzh<?, O> zzaqu() {
        zzaa.zza(false, (Object) "This API was constructed with a ClientBuilder. Use getClientBuilder");
        return null;
    }

    public zzc<?> zzaqv() {
        if (this.xm != null) {
            return this.xm;
        }
        throw new IllegalStateException("This API was constructed with null client keys. This should not be possible.");
    }

    public boolean zzaqw() {
        return false;
    }
}
