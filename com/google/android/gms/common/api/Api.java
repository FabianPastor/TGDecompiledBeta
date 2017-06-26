package com.google.android.gms.common.api;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzal;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzj;
import com.google.android.gms.common.internal.zzq;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.telegram.tgnet.ConnectionsManager;

public final class Api<O extends ApiOptions> {
    private final String mName;
    private final zzi<?> zzaAA;
    private final zza<?, O> zzaAx;
    private final zzh<?, O> zzaAy = null;
    private final zzf<?> zzaAz;

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

        public List<Scope> zzn(O o) {
            return Collections.emptyList();
        }
    }

    public static abstract class zza<T extends zze, O> extends zzd<T, O> {
        public abstract T zza(Context context, Looper looper, zzq com_google_android_gms_common_internal_zzq, O o, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener);
    }

    public interface zze extends zzb {
        void disconnect();

        void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

        boolean isConnected();

        boolean isConnecting();

        void zza(zzal com_google_android_gms_common_internal_zzal, Set<Scope> set);

        void zza(zzj com_google_android_gms_common_internal_zzj);

        boolean zzmG();

        Intent zzmH();

        boolean zzmv();

        boolean zzpe();
    }

    public static final class zzf<C extends zze> extends zzc<C> {
    }

    public interface zzg<T extends IInterface> extends zzb {
        T zzd(IBinder iBinder);

        String zzdb();

        String zzdc();
    }

    public static abstract class zzh<T extends zzg, O> extends zzd<T, O> {
    }

    public static final class zzi<C extends zzg> extends zzc<C> {
    }

    public <C extends zze> Api(String str, zza<C, O> com_google_android_gms_common_api_Api_zza_C__O, zzf<C> com_google_android_gms_common_api_Api_zzf_C) {
        zzbo.zzb((Object) com_google_android_gms_common_api_Api_zza_C__O, (Object) "Cannot construct an Api with a null ClientBuilder");
        zzbo.zzb((Object) com_google_android_gms_common_api_Api_zzf_C, (Object) "Cannot construct an Api with a null ClientKey");
        this.mName = str;
        this.zzaAx = com_google_android_gms_common_api_Api_zza_C__O;
        this.zzaAz = com_google_android_gms_common_api_Api_zzf_C;
        this.zzaAA = null;
    }

    public final String getName() {
        return this.mName;
    }

    public final zzd<?, O> zzpb() {
        return this.zzaAx;
    }

    public final zza<?, O> zzpc() {
        zzbo.zza(this.zzaAx != null, (Object) "This API was constructed with a SimpleClientBuilder. Use getSimpleClientBuilder");
        return this.zzaAx;
    }

    public final zzc<?> zzpd() {
        if (this.zzaAz != null) {
            return this.zzaAz;
        }
        throw new IllegalStateException("This API was constructed with null client keys. This should not be possible.");
    }
}
