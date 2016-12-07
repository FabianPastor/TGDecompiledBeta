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
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzai;
import com.google.android.gms.common.internal.zzh;
import com.google.android.gms.common.internal.zzh.zza;
import com.google.android.gms.internal.zzqa;
import com.google.android.gms.internal.zzqc;
import com.google.android.gms.internal.zzqf;
import com.google.android.gms.internal.zzqp;
import com.google.android.gms.internal.zzqz;
import com.google.android.gms.internal.zzrd;
import com.google.android.gms.internal.zzrl;
import com.google.android.gms.internal.zzrp;
import com.google.android.gms.internal.zzwy;
import com.google.android.gms.internal.zzwz;
import com.google.android.gms.internal.zzxa;
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
    private static final Set<GoogleApiClient> vE = Collections.newSetFromMap(new WeakHashMap());

    public static final class Builder {
        private Account ec;
        private String fo;
        private final Context mContext;
        private final Set<Scope> vF;
        private final Set<Scope> vG;
        private int vH;
        private View vI;
        private String vJ;
        private final Map<Api<?>, zza> vK;
        private final Map<Api<?>, ApiOptions> vL;
        private zzqz vM;
        private int vN;
        private OnConnectionFailedListener vO;
        private GoogleApiAvailability vP;
        private Api.zza<? extends zzwz, zzxa> vQ;
        private final ArrayList<ConnectionCallbacks> vR;
        private final ArrayList<OnConnectionFailedListener> vS;
        private Looper zzajn;

        public Builder(@NonNull Context context) {
            this.vF = new HashSet();
            this.vG = new HashSet();
            this.vK = new ArrayMap();
            this.vL = new ArrayMap();
            this.vN = -1;
            this.vP = GoogleApiAvailability.getInstance();
            this.vQ = zzwy.fb;
            this.vR = new ArrayList();
            this.vS = new ArrayList();
            this.mContext = context;
            this.zzajn = context.getMainLooper();
            this.fo = context.getPackageName();
            this.vJ = context.getClass().getName();
        }

        public Builder(@NonNull Context context, @NonNull ConnectionCallbacks connectionCallbacks, @NonNull OnConnectionFailedListener onConnectionFailedListener) {
            this(context);
            zzac.zzb((Object) connectionCallbacks, (Object) "Must provide a connected listener");
            this.vR.add(connectionCallbacks);
            zzac.zzb((Object) onConnectionFailedListener, (Object) "Must provide a connection failed listener");
            this.vS.add(onConnectionFailedListener);
        }

        private static <C extends zze, O> C zza(Api.zza<C, O> com_google_android_gms_common_api_Api_zza_C__O, Object obj, Context context, Looper looper, zzh com_google_android_gms_common_internal_zzh, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
            return com_google_android_gms_common_api_Api_zza_C__O.zza(context, looper, com_google_android_gms_common_internal_zzh, obj, connectionCallbacks, onConnectionFailedListener);
        }

        private Builder zza(@NonNull zzqz com_google_android_gms_internal_zzqz, int i, @Nullable OnConnectionFailedListener onConnectionFailedListener) {
            zzac.zzb(i >= 0, (Object) "clientId must be non-negative");
            this.vN = i;
            this.vO = onConnectionFailedListener;
            this.vM = com_google_android_gms_internal_zzqz;
            return this;
        }

        private static <C extends zzg, O> zzai zza(Api.zzh<C, O> com_google_android_gms_common_api_Api_zzh_C__O, Object obj, Context context, Looper looper, zzh com_google_android_gms_common_internal_zzh, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
            return new zzai(context, looper, com_google_android_gms_common_api_Api_zzh_C__O.zzapt(), connectionCallbacks, onConnectionFailedListener, com_google_android_gms_common_internal_zzh, com_google_android_gms_common_api_Api_zzh_C__O.zzr(obj));
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
            Set hashSet = new HashSet(api.zzapm().zzp(o));
            int length = scopeArr.length;
            while (i2 < length) {
                hashSet.add(scopeArr[i2]);
                i2++;
            }
            this.vK.put(api, new zza(hashSet, z));
        }

        private GoogleApiClient zzaqe() {
            zzh zzaqd = zzaqd();
            Api api = null;
            Map zzaui = zzaqd.zzaui();
            Map arrayMap = new ArrayMap();
            Map arrayMap2 = new ArrayMap();
            ArrayList arrayList = new ArrayList();
            Api api2 = null;
            for (Api api3 : this.vL.keySet()) {
                Api api32;
                zze zza;
                Api api4;
                Object obj = this.vL.get(api32);
                int i = 0;
                if (zzaui.get(api32) != null) {
                    i = ((zza) zzaui.get(api32)).Cb ? 1 : 2;
                }
                arrayMap.put(api32, Integer.valueOf(i));
                ConnectionCallbacks com_google_android_gms_internal_zzqf = new zzqf(api32, i);
                arrayList.add(com_google_android_gms_internal_zzqf);
                Api api5;
                if (api32.zzapq()) {
                    Api.zzh zzapo = api32.zzapo();
                    api5 = zzapo.getPriority() == 1 ? api32 : api2;
                    zza = zza(zzapo, obj, this.mContext, this.zzajn, zzaqd, com_google_android_gms_internal_zzqf, (OnConnectionFailedListener) com_google_android_gms_internal_zzqf);
                    api4 = api5;
                } else {
                    Api.zza zzapn = api32.zzapn();
                    api5 = zzapn.getPriority() == 1 ? api32 : api2;
                    zza = zza(zzapn, obj, this.mContext, this.zzajn, zzaqd, com_google_android_gms_internal_zzqf, (OnConnectionFailedListener) com_google_android_gms_internal_zzqf);
                    api4 = api5;
                }
                arrayMap2.put(api32.zzapp(), zza);
                if (!zza.zzahs()) {
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
                zzac.zza(this.ec == null, "Must not set an account in GoogleApiClient.Builder when using %s. Set account in GoogleSignInOptions.Builder instead", api.getName());
                zzac.zza(this.vF.equals(this.vG), "Must not set scopes in GoogleApiClient.Builder when using %s. Set account in GoogleSignInOptions.Builder instead.", api.getName());
            }
            return new zzqp(this.mContext, new ReentrantLock(), this.zzajn, zzaqd, this.vP, this.vQ, arrayMap, this.vR, this.vS, arrayMap2, this.vN, zzqp.zza(arrayMap2.values(), true), arrayList);
        }

        private void zzf(GoogleApiClient googleApiClient) {
            zzqa.zza(this.vM).zza(this.vN, googleApiClient, this.vO);
        }

        public Builder addApi(@NonNull Api<? extends NotRequiredOptions> api) {
            zzac.zzb((Object) api, (Object) "Api must not be null");
            this.vL.put(api, null);
            Collection zzp = api.zzapm().zzp(null);
            this.vG.addAll(zzp);
            this.vF.addAll(zzp);
            return this;
        }

        public <O extends HasOptions> Builder addApi(@NonNull Api<O> api, @NonNull O o) {
            zzac.zzb((Object) api, (Object) "Api must not be null");
            zzac.zzb((Object) o, (Object) "Null options are not permitted for this Api");
            this.vL.put(api, o);
            Collection zzp = api.zzapm().zzp(o);
            this.vG.addAll(zzp);
            this.vF.addAll(zzp);
            return this;
        }

        public <O extends HasOptions> Builder addApiIfAvailable(@NonNull Api<O> api, @NonNull O o, Scope... scopeArr) {
            zzac.zzb((Object) api, (Object) "Api must not be null");
            zzac.zzb((Object) o, (Object) "Null options are not permitted for this Api");
            this.vL.put(api, o);
            zza(api, o, 1, scopeArr);
            return this;
        }

        public Builder addApiIfAvailable(@NonNull Api<? extends NotRequiredOptions> api, Scope... scopeArr) {
            zzac.zzb((Object) api, (Object) "Api must not be null");
            this.vL.put(api, null);
            zza(api, null, 1, scopeArr);
            return this;
        }

        public Builder addConnectionCallbacks(@NonNull ConnectionCallbacks connectionCallbacks) {
            zzac.zzb((Object) connectionCallbacks, (Object) "Listener must not be null");
            this.vR.add(connectionCallbacks);
            return this;
        }

        public Builder addOnConnectionFailedListener(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
            zzac.zzb((Object) onConnectionFailedListener, (Object) "Listener must not be null");
            this.vS.add(onConnectionFailedListener);
            return this;
        }

        public Builder addScope(@NonNull Scope scope) {
            zzac.zzb((Object) scope, (Object) "Scope must not be null");
            this.vF.add(scope);
            return this;
        }

        public GoogleApiClient build() {
            zzac.zzb(!this.vL.isEmpty(), (Object) "must call addApi() to add at least one API");
            GoogleApiClient zzaqe = zzaqe();
            synchronized (GoogleApiClient.vE) {
                GoogleApiClient.vE.add(zzaqe);
            }
            if (this.vN >= 0) {
                zzf(zzaqe);
            }
            return zzaqe;
        }

        public Builder enableAutoManage(@NonNull FragmentActivity fragmentActivity, int i, @Nullable OnConnectionFailedListener onConnectionFailedListener) {
            return zza(new zzqz(fragmentActivity), i, onConnectionFailedListener);
        }

        public Builder enableAutoManage(@NonNull FragmentActivity fragmentActivity, @Nullable OnConnectionFailedListener onConnectionFailedListener) {
            return enableAutoManage(fragmentActivity, 0, onConnectionFailedListener);
        }

        public Builder setAccountName(String str) {
            this.ec = str == null ? null : new Account(str, "com.google");
            return this;
        }

        public Builder setGravityForPopups(int i) {
            this.vH = i;
            return this;
        }

        public Builder setHandler(@NonNull Handler handler) {
            zzac.zzb((Object) handler, (Object) "Handler must not be null");
            this.zzajn = handler.getLooper();
            return this;
        }

        public Builder setViewForPopups(@NonNull View view) {
            zzac.zzb((Object) view, (Object) "View must not be null");
            this.vI = view;
            return this;
        }

        public Builder useDefaultAccount() {
            return setAccountName("<<default account>>");
        }

        public zzh zzaqd() {
            zzxa com_google_android_gms_internal_zzxa = zzxa.aAa;
            if (this.vL.containsKey(zzwy.API)) {
                com_google_android_gms_internal_zzxa = (zzxa) this.vL.get(zzwy.API);
            }
            return new zzh(this.ec, this.vF, this.vK, this.vH, this.vI, this.fo, this.vJ, com_google_android_gms_internal_zzxa);
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
        synchronized (vE) {
            String concat = String.valueOf(str).concat("  ");
            int i = 0;
            for (GoogleApiClient googleApiClient : vE) {
                int i2 = i + 1;
                printWriter.append(str).append("GoogleApiClient#").println(i);
                googleApiClient.dump(concat, fileDescriptor, printWriter, strArr);
                i = i2;
            }
        }
    }

    public static Set<GoogleApiClient> zzaqa() {
        Set<GoogleApiClient> set;
        synchronized (vE) {
            set = vE;
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

    public void zza(zzrp com_google_android_gms_internal_zzrp) {
        throw new UnsupportedOperationException();
    }

    public boolean zza(@NonNull Api<?> api) {
        throw new UnsupportedOperationException();
    }

    public boolean zza(zzrl com_google_android_gms_internal_zzrl) {
        throw new UnsupportedOperationException();
    }

    public void zzaqb() {
        throw new UnsupportedOperationException();
    }

    public void zzb(zzrp com_google_android_gms_internal_zzrp) {
        throw new UnsupportedOperationException();
    }

    public <A extends zzb, R extends Result, T extends zzqc.zza<R, A>> T zzc(@NonNull T t) {
        throw new UnsupportedOperationException();
    }

    public <A extends zzb, T extends zzqc.zza<? extends Result, A>> T zzd(@NonNull T t) {
        throw new UnsupportedOperationException();
    }

    public <L> zzrd<L> zzs(@NonNull L l) {
        throw new UnsupportedOperationException();
    }
}
