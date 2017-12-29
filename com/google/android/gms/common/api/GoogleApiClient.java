package com.google.android.gms.common.api;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.view.View;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.ApiOptions.HasOptions;
import com.google.android.gms.common.api.Api.ApiOptions.NotRequiredOptions;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zzd;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.internal.zzba;
import com.google.android.gms.common.api.internal.zzce;
import com.google.android.gms.common.api.internal.zzdg;
import com.google.android.gms.common.api.internal.zzi;
import com.google.android.gms.common.api.internal.zzm;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.internal.zzr;
import com.google.android.gms.common.internal.zzt;
import com.google.android.gms.internal.zzcxa;
import com.google.android.gms.internal.zzcxd;
import com.google.android.gms.internal.zzcxe;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;

public abstract class GoogleApiClient {
    private static final Set<GoogleApiClient> zzfmn = Collections.newSetFromMap(new WeakHashMap());

    public static final class Builder {
        private final Context mContext;
        private Looper zzall;
        private String zzebs;
        private Account zzebz;
        private final Set<Scope> zzfmo = new HashSet();
        private final Set<Scope> zzfmp = new HashSet();
        private int zzfmq;
        private View zzfmr;
        private String zzfms;
        private final Map<Api<?>, zzt> zzfmt = new ArrayMap();
        private final Map<Api<?>, ApiOptions> zzfmu = new ArrayMap();
        private zzce zzfmv;
        private int zzfmw = -1;
        private OnConnectionFailedListener zzfmx;
        private GoogleApiAvailability zzfmy = GoogleApiAvailability.getInstance();
        private zza<? extends zzcxd, zzcxe> zzfmz = zzcxa.zzebg;
        private final ArrayList<ConnectionCallbacks> zzfna = new ArrayList();
        private final ArrayList<OnConnectionFailedListener> zzfnb = new ArrayList();
        private boolean zzfnc = false;

        public Builder(Context context) {
            this.mContext = context;
            this.zzall = context.getMainLooper();
            this.zzebs = context.getPackageName();
            this.zzfms = context.getClass().getName();
        }

        public final Builder addApi(Api<? extends NotRequiredOptions> api) {
            zzbq.checkNotNull(api, "Api must not be null");
            this.zzfmu.put(api, null);
            Collection zzr = api.zzagd().zzr(null);
            this.zzfmp.addAll(zzr);
            this.zzfmo.addAll(zzr);
            return this;
        }

        public final <O extends HasOptions> Builder addApi(Api<O> api, O o) {
            zzbq.checkNotNull(api, "Api must not be null");
            zzbq.checkNotNull(o, "Null options are not permitted for this Api");
            this.zzfmu.put(api, o);
            Collection zzr = api.zzagd().zzr(o);
            this.zzfmp.addAll(zzr);
            this.zzfmo.addAll(zzr);
            return this;
        }

        public final Builder addConnectionCallbacks(ConnectionCallbacks connectionCallbacks) {
            zzbq.checkNotNull(connectionCallbacks, "Listener must not be null");
            this.zzfna.add(connectionCallbacks);
            return this;
        }

        public final Builder addOnConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener) {
            zzbq.checkNotNull(onConnectionFailedListener, "Listener must not be null");
            this.zzfnb.add(onConnectionFailedListener);
            return this;
        }

        public final GoogleApiClient build() {
            zzbq.checkArgument(!this.zzfmu.isEmpty(), "must call addApi() to add at least one API");
            zzr zzagu = zzagu();
            Api api = null;
            Map zzakx = zzagu.zzakx();
            Map arrayMap = new ArrayMap();
            Map arrayMap2 = new ArrayMap();
            ArrayList arrayList = new ArrayList();
            Object obj = null;
            for (Api api2 : this.zzfmu.keySet()) {
                Api api22;
                Object obj2 = this.zzfmu.get(api22);
                boolean z = zzakx.get(api22) != null;
                arrayMap.put(api22, Boolean.valueOf(z));
                ConnectionCallbacks com_google_android_gms_common_api_internal_zzt = new com.google.android.gms.common.api.internal.zzt(api22, z);
                arrayList.add(com_google_android_gms_common_api_internal_zzt);
                zzd zzage = api22.zzage();
                zze zza = zzage.zza(this.mContext, this.zzall, zzagu, obj2, com_google_android_gms_common_api_internal_zzt, com_google_android_gms_common_api_internal_zzt);
                arrayMap2.put(api22.zzagf(), zza);
                Object obj3 = zzage.getPriority() == 1 ? obj2 != null ? 1 : null : obj;
                if (!zza.zzabj()) {
                    api22 = api;
                } else if (api != null) {
                    String name = api22.getName();
                    String name2 = api.getName();
                    throw new IllegalStateException(new StringBuilder((String.valueOf(name).length() + 21) + String.valueOf(name2).length()).append(name).append(" cannot be used with ").append(name2).toString());
                }
                obj = obj3;
                api = api22;
            }
            if (api != null) {
                if (obj != null) {
                    name = api.getName();
                    throw new IllegalStateException(new StringBuilder(String.valueOf(name).length() + 82).append("With using ").append(name).append(", GamesOptions can only be specified within GoogleSignInOptions.Builder").toString());
                }
                zzbq.zza(this.zzebz == null, "Must not set an account in GoogleApiClient.Builder when using %s. Set account in GoogleSignInOptions.Builder instead", api.getName());
                zzbq.zza(this.zzfmo.equals(this.zzfmp), "Must not set scopes in GoogleApiClient.Builder when using %s. Set account in GoogleSignInOptions.Builder instead.", api.getName());
            }
            GoogleApiClient com_google_android_gms_common_api_internal_zzba = new zzba(this.mContext, new ReentrantLock(), this.zzall, zzagu, this.zzfmy, this.zzfmz, arrayMap, this.zzfna, this.zzfnb, arrayMap2, this.zzfmw, zzba.zza(arrayMap2.values(), true), arrayList, false);
            synchronized (GoogleApiClient.zzfmn) {
                GoogleApiClient.zzfmn.add(com_google_android_gms_common_api_internal_zzba);
            }
            if (this.zzfmw >= 0) {
                zzi.zza(this.zzfmv).zza(this.zzfmw, com_google_android_gms_common_api_internal_zzba, this.zzfmx);
            }
            return com_google_android_gms_common_api_internal_zzba;
        }

        public final zzr zzagu() {
            zzcxe com_google_android_gms_internal_zzcxe = zzcxe.zzkbs;
            if (this.zzfmu.containsKey(zzcxa.API)) {
                com_google_android_gms_internal_zzcxe = (zzcxe) this.zzfmu.get(zzcxa.API);
            }
            return new zzr(this.zzebz, this.zzfmo, this.zzfmt, this.zzfmq, this.zzfmr, this.zzebs, this.zzfms, com_google_android_gms_internal_zzcxe);
        }
    }

    public interface ConnectionCallbacks {
        void onConnected(Bundle bundle);

        void onConnectionSuspended(int i);
    }

    public interface OnConnectionFailedListener {
        void onConnectionFailed(ConnectionResult connectionResult);
    }

    public abstract ConnectionResult blockingConnect();

    public abstract void connect();

    public void connect(int i) {
        throw new UnsupportedOperationException();
    }

    public abstract void disconnect();

    public abstract void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

    public Looper getLooper() {
        throw new UnsupportedOperationException();
    }

    public abstract boolean isConnected();

    public abstract void registerConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener);

    public abstract void unregisterConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener);

    public void zza(zzdg com_google_android_gms_common_api_internal_zzdg) {
        throw new UnsupportedOperationException();
    }

    public void zzb(zzdg com_google_android_gms_common_api_internal_zzdg) {
        throw new UnsupportedOperationException();
    }

    public <A extends zzb, R extends Result, T extends zzm<R, A>> T zzd(T t) {
        throw new UnsupportedOperationException();
    }

    public <A extends zzb, T extends zzm<? extends Result, A>> T zze(T t) {
        throw new UnsupportedOperationException();
    }
}
