package com.google.android.gms.common.api;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.support.annotation.Nullable;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzr;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.telegram.tgnet.ConnectionsManager;

public final class Api<O extends ApiOptions> {
    private final String mName;
    private final zza<?, O> zzayH;
    private final zzh<?, O> zzayI = null;
    private final zzf<?> zzayJ;
    private final zzi<?> zzayK;

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
        public abstract T zza(Context context, Looper looper, com.google.android.gms.common.internal.zzg com_google_android_gms_common_internal_zzg, O o, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener);
    }

    public interface zze extends zzb {
        void disconnect();

        void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

        boolean isConnected();

        boolean isConnecting();

        void zza(com.google.android.gms.common.internal.zzf.zzf com_google_android_gms_common_internal_zzf_zzf);

        void zza(zzr com_google_android_gms_common_internal_zzr, Set<Scope> set);

        boolean zzrd();

        boolean zzrr();

        Intent zzrs();

        boolean zzvh();

        @Nullable
        IBinder zzvi();
    }

    public static final class zzf<C extends zze> extends zzc<C> {
    }

    public interface zzg<T extends IInterface> extends zzb {
        String zzeA();

        String zzez();

        T zzh(IBinder iBinder);
    }

    public static abstract class zzh<T extends zzg, O> extends zzd<T, O> {
    }

    public static final class zzi<C extends zzg> extends zzc<C> {
    }

    public <C extends zze> Api(String str, zza<C, O> com_google_android_gms_common_api_Api_zza_C__O, zzf<C> com_google_android_gms_common_api_Api_zzf_C) {
        zzac.zzb((Object) com_google_android_gms_common_api_Api_zza_C__O, (Object) "Cannot construct an Api with a null ClientBuilder");
        zzac.zzb((Object) com_google_android_gms_common_api_Api_zzf_C, (Object) "Cannot construct an Api with a null ClientKey");
        this.mName = str;
        this.zzayH = com_google_android_gms_common_api_Api_zza_C__O;
        this.zzayJ = com_google_android_gms_common_api_Api_zzf_C;
        this.zzayK = null;
    }

    public String getName() {
        return this.mName;
    }

    public zzd<?, O> zzve() {
        return this.zzayH;
    }

    public zza<?, O> zzvf() {
        zzac.zza(this.zzayH != null, (Object) "This API was constructed with a SimpleClientBuilder. Use getSimpleClientBuilder");
        return this.zzayH;
    }

    public zzc<?> zzvg() {
        if (this.zzayJ != null) {
            return this.zzayJ;
        }
        throw new IllegalStateException("This API was constructed with null client keys. This should not be possible.");
    }
}
