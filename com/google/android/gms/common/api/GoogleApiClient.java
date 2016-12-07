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
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.internal.zzg.zza;
import com.google.android.gms.internal.zzaal;
import com.google.android.gms.internal.zzaav;
import com.google.android.gms.internal.zzaaz;
import com.google.android.gms.internal.zzabi;
import com.google.android.gms.internal.zzabp;
import com.google.android.gms.internal.zzaxm;
import com.google.android.gms.internal.zzaxn;
import com.google.android.gms.internal.zzaxo;
import com.google.android.gms.internal.zzzt;
import com.google.android.gms.internal.zzzv;
import com.google.android.gms.internal.zzzy;
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
    private static final Set<GoogleApiClient> zzaxM = Collections.newSetFromMap(new WeakHashMap());

    public static final class Builder {
        private final Context mContext;
        private Account zzagg;
        private String zzahp;
        private final Set<Scope> zzaxN;
        private final Set<Scope> zzaxO;
        private int zzaxP;
        private View zzaxQ;
        private String zzaxR;
        private final Map<Api<?>, zza> zzaxS;
        private final Map<Api<?>, ApiOptions> zzaxT;
        private zzaav zzaxU;
        private int zzaxV;
        private OnConnectionFailedListener zzaxW;
        private GoogleApiAvailability zzaxX;
        private Api.zza<? extends zzaxn, zzaxo> zzaxY;
        private final ArrayList<ConnectionCallbacks> zzaxZ;
        private final ArrayList<OnConnectionFailedListener> zzaya;
        private boolean zzayb;
        private Looper zzrx;

        public Builder(@NonNull Context context) {
            this.zzaxN = new HashSet();
            this.zzaxO = new HashSet();
            this.zzaxS = new ArrayMap();
            this.zzaxT = new ArrayMap();
            this.zzaxV = -1;
            this.zzaxX = GoogleApiAvailability.getInstance();
            this.zzaxY = zzaxm.zzahd;
            this.zzaxZ = new ArrayList();
            this.zzaya = new ArrayList();
            this.zzayb = false;
            this.mContext = context;
            this.zzrx = context.getMainLooper();
            this.zzahp = context.getPackageName();
            this.zzaxR = context.getClass().getName();
        }

        public Builder(@NonNull Context context, @NonNull ConnectionCallbacks connectionCallbacks, @NonNull OnConnectionFailedListener onConnectionFailedListener) {
            this(context);
            zzac.zzb((Object) connectionCallbacks, (Object) "Must provide a connected listener");
            this.zzaxZ.add(connectionCallbacks);
            zzac.zzb((Object) onConnectionFailedListener, (Object) "Must provide a connection failed listener");
            this.zzaya.add(onConnectionFailedListener);
        }

        private static <C extends zze, O> C zza(Api.zza<C, O> com_google_android_gms_common_api_Api_zza_C__O, Object obj, Context context, Looper looper, zzg com_google_android_gms_common_internal_zzg, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
            return com_google_android_gms_common_api_Api_zza_C__O.zza(context, looper, com_google_android_gms_common_internal_zzg, obj, connectionCallbacks, onConnectionFailedListener);
        }

        private Builder zza(@NonNull zzaav com_google_android_gms_internal_zzaav, int i, @Nullable OnConnectionFailedListener onConnectionFailedListener) {
            zzac.zzb(i >= 0, (Object) "clientId must be non-negative");
            this.zzaxV = i;
            this.zzaxW = onConnectionFailedListener;
            this.zzaxU = com_google_android_gms_internal_zzaav;
            return this;
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
            Set hashSet = new HashSet(api.zzuF().zzp(o));
            int length = scopeArr.length;
            while (i2 < length) {
                hashSet.add(scopeArr[i2]);
                i2++;
            }
            this.zzaxS.put(api, new zza(hashSet, z));
        }

        private void zzf(GoogleApiClient googleApiClient) {
            zzzt.zza(this.zzaxU).zza(this.zzaxV, googleApiClient, this.zzaxW);
        }

        private GoogleApiClient zzuQ() {
            zzg zzuP = zzuP();
            Api api = null;
            Map zzxg = zzuP.zzxg();
            Map arrayMap = new ArrayMap();
            Map arrayMap2 = new ArrayMap();
            ArrayList arrayList = new ArrayList();
            Api api2 = null;
            for (Api api3 : this.zzaxT.keySet()) {
                Api api32;
                Object obj = this.zzaxT.get(api32);
                int i = 0;
                if (zzxg.get(api32) != null) {
                    i = ((zza) zzxg.get(api32)).zzaEf ? 1 : 2;
                }
                arrayMap.put(api32, Integer.valueOf(i));
                ConnectionCallbacks com_google_android_gms_internal_zzzy = new zzzy(api32, i);
                arrayList.add(com_google_android_gms_internal_zzzy);
                Api.zza zzuG = api32.zzuG();
                Api api4 = zzuG.getPriority() == 1 ? api32 : api2;
                zze zza = zza(zzuG, obj, this.mContext, this.zzrx, zzuP, com_google_android_gms_internal_zzzy, com_google_android_gms_internal_zzzy);
                arrayMap2.put(api32.zzuH(), zza);
                if (!zza.zzqS()) {
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
                zzac.zza(this.zzagg == null, "Must not set an account in GoogleApiClient.Builder when using %s. Set account in GoogleSignInOptions.Builder instead", api.getName());
                zzac.zza(this.zzaxN.equals(this.zzaxO), "Must not set scopes in GoogleApiClient.Builder when using %s. Set account in GoogleSignInOptions.Builder instead.", api.getName());
            }
            return new zzaal(this.mContext, new ReentrantLock(), this.zzrx, zzuP, this.zzaxX, this.zzaxY, arrayMap, this.zzaxZ, this.zzaya, arrayMap2, this.zzaxV, zzaal.zza(arrayMap2.values(), true), arrayList, false);
        }

        public Builder addApi(@NonNull Api<? extends NotRequiredOptions> api) {
            zzac.zzb((Object) api, (Object) "Api must not be null");
            this.zzaxT.put(api, null);
            Collection zzp = api.zzuF().zzp(null);
            this.zzaxO.addAll(zzp);
            this.zzaxN.addAll(zzp);
            return this;
        }

        public <O extends HasOptions> Builder addApi(@NonNull Api<O> api, @NonNull O o) {
            zzac.zzb((Object) api, (Object) "Api must not be null");
            zzac.zzb((Object) o, (Object) "Null options are not permitted for this Api");
            this.zzaxT.put(api, o);
            Collection zzp = api.zzuF().zzp(o);
            this.zzaxO.addAll(zzp);
            this.zzaxN.addAll(zzp);
            return this;
        }

        public <O extends HasOptions> Builder addApiIfAvailable(@NonNull Api<O> api, @NonNull O o, Scope... scopeArr) {
            zzac.zzb((Object) api, (Object) "Api must not be null");
            zzac.zzb((Object) o, (Object) "Null options are not permitted for this Api");
            this.zzaxT.put(api, o);
            zza(api, o, 1, scopeArr);
            return this;
        }

        public Builder addApiIfAvailable(@NonNull Api<? extends NotRequiredOptions> api, Scope... scopeArr) {
            zzac.zzb((Object) api, (Object) "Api must not be null");
            this.zzaxT.put(api, null);
            zza(api, null, 1, scopeArr);
            return this;
        }

        public Builder addConnectionCallbacks(@NonNull ConnectionCallbacks connectionCallbacks) {
            zzac.zzb((Object) connectionCallbacks, (Object) "Listener must not be null");
            this.zzaxZ.add(connectionCallbacks);
            return this;
        }

        public Builder addOnConnectionFailedListener(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
            zzac.zzb((Object) onConnectionFailedListener, (Object) "Listener must not be null");
            this.zzaya.add(onConnectionFailedListener);
            return this;
        }

        public Builder addScope(@NonNull Scope scope) {
            zzac.zzb((Object) scope, (Object) "Scope must not be null");
            this.zzaxN.add(scope);
            return this;
        }

        public GoogleApiClient build() {
            zzac.zzb(!this.zzaxT.isEmpty(), (Object) "must call addApi() to add at least one API");
            GoogleApiClient zzuQ = zzuQ();
            synchronized (GoogleApiClient.zzaxM) {
                GoogleApiClient.zzaxM.add(zzuQ);
            }
            if (this.zzaxV >= 0) {
                zzf(zzuQ);
            }
            return zzuQ;
        }

        public Builder enableAutoManage(@NonNull FragmentActivity fragmentActivity, int i, @Nullable OnConnectionFailedListener onConnectionFailedListener) {
            return zza(new zzaav(fragmentActivity), i, onConnectionFailedListener);
        }

        public Builder enableAutoManage(@NonNull FragmentActivity fragmentActivity, @Nullable OnConnectionFailedListener onConnectionFailedListener) {
            return enableAutoManage(fragmentActivity, 0, onConnectionFailedListener);
        }

        public Builder setAccountName(String str) {
            this.zzagg = str == null ? null : new Account(str, "com.google");
            return this;
        }

        public Builder setGravityForPopups(int i) {
            this.zzaxP = i;
            return this;
        }

        public Builder setHandler(@NonNull Handler handler) {
            zzac.zzb((Object) handler, (Object) "Handler must not be null");
            this.zzrx = handler.getLooper();
            return this;
        }

        public Builder setViewForPopups(@NonNull View view) {
            zzac.zzb((Object) view, (Object) "View must not be null");
            this.zzaxQ = view;
            return this;
        }

        public Builder useDefaultAccount() {
            return setAccountName("<<default account>>");
        }

        public zzg zzuP() {
            zzaxo com_google_android_gms_internal_zzaxo = zzaxo.zzbCg;
            if (this.zzaxT.containsKey(zzaxm.API)) {
                com_google_android_gms_internal_zzaxo = (zzaxo) this.zzaxT.get(zzaxm.API);
            }
            return new zzg(this.zzagg, this.zzaxN, this.zzaxS, this.zzaxP, this.zzaxQ, this.zzahp, this.zzaxR, com_google_android_gms_internal_zzaxo);
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
        synchronized (zzaxM) {
            String concat = String.valueOf(str).concat("  ");
            int i = 0;
            for (GoogleApiClient googleApiClient : zzaxM) {
                int i2 = i + 1;
                printWriter.append(str).append("GoogleApiClient#").println(i);
                googleApiClient.dump(concat, fileDescriptor, printWriter, strArr);
                i = i2;
            }
        }
    }

    public static Set<GoogleApiClient> zzuM() {
        Set<GoogleApiClient> set;
        synchronized (zzaxM) {
            set = zzaxM;
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

    public <A extends zzb, R extends Result, T extends zzzv.zza<R, A>> T zza(@NonNull T t) {
        throw new UnsupportedOperationException();
    }

    public void zza(zzabp com_google_android_gms_internal_zzabp) {
        throw new UnsupportedOperationException();
    }

    public boolean zza(@NonNull Api<?> api) {
        throw new UnsupportedOperationException();
    }

    public boolean zza(zzabi com_google_android_gms_internal_zzabi) {
        throw new UnsupportedOperationException();
    }

    public <A extends zzb, T extends zzzv.zza<? extends Result, A>> T zzb(@NonNull T t) {
        throw new UnsupportedOperationException();
    }

    public void zzb(zzabp com_google_android_gms_internal_zzabp) {
        throw new UnsupportedOperationException();
    }

    public <L> zzaaz<L> zzr(@NonNull L l) {
        throw new UnsupportedOperationException();
    }

    public void zzuN() {
        throw new UnsupportedOperationException();
    }
}
