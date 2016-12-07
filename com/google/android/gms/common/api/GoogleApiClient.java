package com.google.android.gms.common.api;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.ArrayMap;
import android.view.View;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.ApiOptions.HasOptions;
import com.google.android.gms.common.api.Api.ApiOptions.NotRequiredOptions;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Api.zzg;
import com.google.android.gms.common.api.Api.zzh;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzag;
import com.google.android.gms.common.internal.zzf;
import com.google.android.gms.common.internal.zzf.zza;
import com.google.android.gms.internal.zzqm;
import com.google.android.gms.internal.zzqo;
import com.google.android.gms.internal.zzqr;
import com.google.android.gms.internal.zzrd;
import com.google.android.gms.internal.zzrn;
import com.google.android.gms.internal.zzrr;
import com.google.android.gms.internal.zzsa;
import com.google.android.gms.internal.zzsf;
import com.google.android.gms.internal.zzxo;
import com.google.android.gms.internal.zzxp;
import com.google.android.gms.internal.zzxq;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public abstract class GoogleApiClient {
    public static final int SIGN_IN_MODE_OPTIONAL = 2;
    public static final int SIGN_IN_MODE_REQUIRED = 1;
    private static final Set<GoogleApiClient> xE = Collections.newSetFromMap(new WeakHashMap());

    public static final class Builder {
        private Account gj;
        private String hu;
        private final Context mContext;
        private final Set<Scope> xF;
        private final Set<Scope> xG;
        private int xH;
        private View xI;
        private String xJ;
        private final Map<Api<?>, zza> xK;
        private final Map<Api<?>, ApiOptions> xL;
        private zzrn xM;
        private int xN;
        private OnConnectionFailedListener xO;
        private GoogleApiAvailability xP;
        private Api.zza<? extends zzxp, zzxq> xQ;
        private final ArrayList<ConnectionCallbacks> xR;
        private final ArrayList<OnConnectionFailedListener> xS;
        private boolean xT;
        private Looper zzajy;

        public Builder(@NonNull Context context) {
            this.xF = new HashSet();
            this.xG = new HashSet();
            this.xK = new ArrayMap();
            this.xL = new ArrayMap();
            this.xN = -1;
            this.xP = GoogleApiAvailability.getInstance();
            this.xQ = zzxo.hh;
            this.xR = new ArrayList();
            this.xS = new ArrayList();
            this.xT = false;
            this.mContext = context;
            this.zzajy = context.getMainLooper();
            this.hu = context.getPackageName();
            this.xJ = context.getClass().getName();
        }

        public Builder(@NonNull Context context, @NonNull ConnectionCallbacks connectionCallbacks, @NonNull OnConnectionFailedListener onConnectionFailedListener) {
            this(context);
            zzaa.zzb((Object) connectionCallbacks, (Object) "Must provide a connected listener");
            this.xR.add(connectionCallbacks);
            zzaa.zzb((Object) onConnectionFailedListener, (Object) "Must provide a connection failed listener");
            this.xS.add(onConnectionFailedListener);
        }

        private static <C extends zze, O> C zza(Api.zza<C, O> com_google_android_gms_common_api_Api_zza_C__O, Object obj, Context context, Looper looper, zzf com_google_android_gms_common_internal_zzf, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
            return com_google_android_gms_common_api_Api_zza_C__O.zza(context, looper, com_google_android_gms_common_internal_zzf, obj, connectionCallbacks, onConnectionFailedListener);
        }

        private Builder zza(@NonNull zzrn com_google_android_gms_internal_zzrn, int i, @Nullable OnConnectionFailedListener onConnectionFailedListener) {
            zzaa.zzb(i >= 0, (Object) "clientId must be non-negative");
            this.xN = i;
            this.xO = onConnectionFailedListener;
            this.xM = com_google_android_gms_internal_zzrn;
            return this;
        }

        private static <C extends zzg, O> zzag zza(zzh<C, O> com_google_android_gms_common_api_Api_zzh_C__O, Object obj, Context context, Looper looper, zzf com_google_android_gms_common_internal_zzf, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
            return new zzag(context, looper, com_google_android_gms_common_api_Api_zzh_C__O.zzaqz(), connectionCallbacks, onConnectionFailedListener, com_google_android_gms_common_internal_zzf, com_google_android_gms_common_api_Api_zzh_C__O.zzr(obj));
        }

        private <O extends ApiOptions> void zza(Api<O> api, O o, int i, Scope... scopeArr) {
            boolean z = true;
            int i2 = 0;
            if (i != 1) {
                if (i == 2) {
                    z = false;
                } else {
                    throw new IllegalArgumentException("Invalid resolution mode: '" + i + "', use a constant from GoogleApiClient.ResolutionMode");
                }
            }
            Set hashSet = new HashSet(api.zzaqs().zzp(o));
            int length = scopeArr.length;
            while (i2 < length) {
                hashSet.add(scopeArr[i2]);
                i2++;
            }
            this.xK.put(api, new zza(hashSet, z));
        }

        private GoogleApiClient zzarg() {
            zzf zzarf = zzarf();
            Api api = null;
            Map zzavr = zzarf.zzavr();
            Map arrayMap = new ArrayMap();
            Map arrayMap2 = new ArrayMap();
            ArrayList arrayList = new ArrayList();
            Api api2 = null;
            for (Api api3 : this.xL.keySet()) {
                Api api32;
                zze zza;
                Api api4;
                Object obj = this.xL.get(api32);
                int i = 0;
                if (zzavr.get(api32) != null) {
                    i = ((zza) zzavr.get(api32)).DN ? 1 : 2;
                }
                arrayMap.put(api32, Integer.valueOf(i));
                ConnectionCallbacks com_google_android_gms_internal_zzqr = new zzqr(api32, i);
                arrayList.add(com_google_android_gms_internal_zzqr);
                Api api5;
                if (api32.zzaqw()) {
                    zzh zzaqu = api32.zzaqu();
                    api5 = zzaqu.getPriority() == 1 ? api32 : api2;
                    zza = zza(zzaqu, obj, this.mContext, this.zzajy, zzarf, com_google_android_gms_internal_zzqr, (OnConnectionFailedListener) com_google_android_gms_internal_zzqr);
                    api4 = api5;
                } else {
                    Api.zza zzaqt = api32.zzaqt();
                    api5 = zzaqt.getPriority() == 1 ? api32 : api2;
                    zza = zza(zzaqt, obj, this.mContext, this.zzajy, zzarf, com_google_android_gms_internal_zzqr, (OnConnectionFailedListener) com_google_android_gms_internal_zzqr);
                    api4 = api5;
                }
                arrayMap2.put(api32.zzaqv(), zza);
                if (!zza.zzajc()) {
                    api32 = api;
                } else if (api != null) {
                    String valueOf = String.valueOf(api32.getName());
                    String valueOf2 = String.valueOf(api.getName());
                    throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 21) + String.valueOf(valueOf2).length()).append(valueOf).append(" cannot be used with ").append(valueOf2).toString());
                }
                api2 = api4;
                api = api32;
            }
            if (api != null) {
                if (api2 != null) {
                    valueOf = String.valueOf(api.getName());
                    valueOf2 = String.valueOf(api2.getName());
                    throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 21) + String.valueOf(valueOf2).length()).append(valueOf).append(" cannot be used with ").append(valueOf2).toString());
                }
                zzaa.zza(this.gj == null, "Must not set an account in GoogleApiClient.Builder when using %s. Set account in GoogleSignInOptions.Builder instead", api.getName());
                zzaa.zza(this.xF.equals(this.xG), "Must not set scopes in GoogleApiClient.Builder when using %s. Set account in GoogleSignInOptions.Builder instead.", api.getName());
            }
            return new zzrd(this.mContext, new ReentrantLock(), this.zzajy, zzarf, this.xP, this.xQ, arrayMap, this.xR, this.xS, arrayMap2, this.xN, zzrd.zza(arrayMap2.values(), true), arrayList, false);
        }

        private void zzf(GoogleApiClient googleApiClient) {
            zzqm.zza(this.xM).zza(this.xN, googleApiClient, this.xO);
        }

        public Builder addApi(@NonNull Api<? extends NotRequiredOptions> api) {
            zzaa.zzb((Object) api, (Object) "Api must not be null");
            this.xL.put(api, null);
            Collection zzp = api.zzaqs().zzp(null);
            this.xG.addAll(zzp);
            this.xF.addAll(zzp);
            return this;
        }

        public <O extends HasOptions> Builder addApi(@NonNull Api<O> api, @NonNull O o) {
            zzaa.zzb((Object) api, (Object) "Api must not be null");
            zzaa.zzb((Object) o, (Object) "Null options are not permitted for this Api");
            this.xL.put(api, o);
            Collection zzp = api.zzaqs().zzp(o);
            this.xG.addAll(zzp);
            this.xF.addAll(zzp);
            return this;
        }

        public <O extends HasOptions> Builder addApiIfAvailable(@NonNull Api<O> api, @NonNull O o, Scope... scopeArr) {
            zzaa.zzb((Object) api, (Object) "Api must not be null");
            zzaa.zzb((Object) o, (Object) "Null options are not permitted for this Api");
            this.xL.put(api, o);
            zza(api, o, 1, scopeArr);
            return this;
        }

        public Builder addApiIfAvailable(@NonNull Api<? extends NotRequiredOptions> api, Scope... scopeArr) {
            zzaa.zzb((Object) api, (Object) "Api must not be null");
            this.xL.put(api, null);
            zza(api, null, 1, scopeArr);
            return this;
        }

        public Builder addConnectionCallbacks(@NonNull ConnectionCallbacks connectionCallbacks) {
            zzaa.zzb((Object) connectionCallbacks, (Object) "Listener must not be null");
            this.xR.add(connectionCallbacks);
            return this;
        }

        public Builder addOnConnectionFailedListener(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
            zzaa.zzb((Object) onConnectionFailedListener, (Object) "Listener must not be null");
            this.xS.add(onConnectionFailedListener);
            return this;
        }

        public Builder addScope(@NonNull Scope scope) {
            zzaa.zzb((Object) scope, (Object) "Scope must not be null");
            this.xF.add(scope);
            return this;
        }

        public GoogleApiClient build() {
            zzaa.zzb(!this.xL.isEmpty(), (Object) "must call addApi() to add at least one API");
            GoogleApiClient zzarg = zzarg();
            synchronized (GoogleApiClient.xE) {
                GoogleApiClient.xE.add(zzarg);
            }
            if (this.xN >= 0) {
                zzf(zzarg);
            }
            return zzarg;
        }

        public Builder enableAutoManage(@NonNull FragmentActivity fragmentActivity, int i, @Nullable OnConnectionFailedListener onConnectionFailedListener) {
            return zza(new zzrn(fragmentActivity), i, onConnectionFailedListener);
        }

        public Builder enableAutoManage(@NonNull FragmentActivity fragmentActivity, @Nullable OnConnectionFailedListener onConnectionFailedListener) {
            return enableAutoManage(fragmentActivity, 0, onConnectionFailedListener);
        }

        public Builder setAccountName(String str) {
            this.gj = str == null ? null : new Account(str, "com.google");
            return this;
        }

        public Builder setGravityForPopups(int i) {
            this.xH = i;
            return this;
        }

        public Builder setHandler(@NonNull Handler handler) {
            zzaa.zzb((Object) handler, (Object) "Handler must not be null");
            this.zzajy = handler.getLooper();
            return this;
        }

        public Builder setViewForPopups(@NonNull View view) {
            zzaa.zzb((Object) view, (Object) "View must not be null");
            this.xI = view;
            return this;
        }

        public Builder useDefaultAccount() {
            return setAccountName("<<default account>>");
        }

        public zzf zzarf() {
            zzxq com_google_android_gms_internal_zzxq = zzxq.aDl;
            if (this.xL.containsKey(zzxo.API)) {
                com_google_android_gms_internal_zzxq = (zzxq) this.xL.get(zzxo.API);
            }
            return new zzf(this.gj, this.xF, this.xK, this.xH, this.xI, this.hu, this.xJ, com_google_android_gms_internal_zzxq);
        }
    }

    public interface ConnectionCallbacks {
        public static final int CAUSE_NETWORK_LOST = 2;
        public static final int CAUSE_SERVICE_DISCONNECTED = 1;

        void onConnected(@Nullable Bundle bundle);

        void onConnectionSuspended(int i);
    }

    public interface OnConnectionFailedListener {
        void onConnectionFailed(@NonNull ConnectionResult connectionResult);
    }

    public static void dumpAll(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        synchronized (xE) {
            String concat = String.valueOf(str).concat("  ");
            int i = 0;
            for (GoogleApiClient googleApiClient : xE) {
                int i2 = i + 1;
                printWriter.append(str).append("GoogleApiClient#").println(i);
                googleApiClient.dump(concat, fileDescriptor, printWriter, strArr);
                i = i2;
            }
        }
    }

    public static Set<GoogleApiClient> zzarc() {
        Set<GoogleApiClient> set;
        synchronized (xE) {
            set = xE;
        }
        return set;
    }

    public abstract ConnectionResult blockingConnect();

    public abstract ConnectionResult blockingConnect(long j, @NonNull TimeUnit timeUnit);

    public abstract PendingResult<Status> clearDefaultAccountAndReconnect();

    public abstract void connect();

    public void connect(int i) {
        throw new UnsupportedOperationException();
    }

    public abstract void disconnect();

    public abstract void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

    @NonNull
    public abstract ConnectionResult getConnectionResult(@NonNull Api<?> api);

    public Context getContext() {
        throw new UnsupportedOperationException();
    }

    public Looper getLooper() {
        throw new UnsupportedOperationException();
    }

    public abstract boolean hasConnectedApi(@NonNull Api<?> api);

    public abstract boolean isConnected();

    public abstract boolean isConnecting();

    public abstract boolean isConnectionCallbacksRegistered(@NonNull ConnectionCallbacks connectionCallbacks);

    public abstract boolean isConnectionFailedListenerRegistered(@NonNull OnConnectionFailedListener onConnectionFailedListener);

    public abstract void reconnect();

    public abstract void registerConnectionCallbacks(@NonNull ConnectionCallbacks connectionCallbacks);

    public abstract void registerConnectionFailedListener(@NonNull OnConnectionFailedListener onConnectionFailedListener);

    public abstract void stopAutoManage(@NonNull FragmentActivity fragmentActivity);

    public abstract void unregisterConnectionCallbacks(@NonNull ConnectionCallbacks connectionCallbacks);

    public abstract void unregisterConnectionFailedListener(@NonNull OnConnectionFailedListener onConnectionFailedListener);

    @NonNull
    public <C extends zze> C zza(@NonNull zzc<C> com_google_android_gms_common_api_Api_zzc_C) {
        throw new UnsupportedOperationException();
    }

    public <A extends zzb, R extends Result, T extends zzqo.zza<R, A>> T zza(@NonNull T t) {
        throw new UnsupportedOperationException();
    }

    public void zza(zzsf com_google_android_gms_internal_zzsf) {
        throw new UnsupportedOperationException();
    }

    public boolean zza(@NonNull Api<?> api) {
        throw new UnsupportedOperationException();
    }

    public boolean zza(zzsa com_google_android_gms_internal_zzsa) {
        throw new UnsupportedOperationException();
    }

    public void zzard() {
        throw new UnsupportedOperationException();
    }

    public <A extends zzb, T extends zzqo.zza<? extends Result, A>> T zzb(@NonNull T t) {
        throw new UnsupportedOperationException();
    }

    public void zzb(zzsf com_google_android_gms_internal_zzsf) {
        throw new UnsupportedOperationException();
    }

    public <L> zzrr<L> zzs(@NonNull L l) {
        throw new UnsupportedOperationException();
    }
}
