package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.google.android.gms.auth.api.signin.internal.zzl;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.internal.zzm;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import org.telegram.messenger.exoplayer2.ExoPlayerFactory;

public final class zzaal extends GoogleApiClient implements com.google.android.gms.internal.zzaau.zza {
    private final Context mContext;
    private final zzm zzaAj;
    private zzaau zzaAk = null;
    final Queue<com.google.android.gms.internal.zzzv.zza<?, ?>> zzaAl = new LinkedList();
    private volatile boolean zzaAm;
    private long zzaAn = 120000;
    private long zzaAo = ExoPlayerFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
    private final zza zzaAp;
    zzaar zzaAq;
    final Map<zzc<?>, zze> zzaAr;
    Set<Scope> zzaAs = new HashSet();
    private final zzaba zzaAt = new zzaba();
    private final ArrayList<zzzy> zzaAu;
    private Integer zzaAv = null;
    Set<zzabp> zzaAw = null;
    final zzabq zzaAx;
    private final com.google.android.gms.common.internal.zzm.zza zzaAy = new com.google.android.gms.common.internal.zzm.zza(this) {
        final /* synthetic */ zzaal zzaAz;

        {
            this.zzaAz = r1;
        }

        public boolean isConnected() {
            return this.zzaAz.isConnected();
        }

        public Bundle zzud() {
            return null;
        }
    };
    private final int zzaxV;
    private final GoogleApiAvailability zzaxX;
    final com.google.android.gms.common.api.Api.zza<? extends zzaxn, zzaxo> zzaxY;
    private boolean zzayb;
    private final Lock zzazn;
    final zzg zzazs;
    final Map<Api<?>, Integer> zzazu;
    private final Looper zzrx;

    final class zza extends Handler {
        final /* synthetic */ zzaal zzaAz;

        zza(zzaal com_google_android_gms_internal_zzaal, Looper looper) {
            this.zzaAz = com_google_android_gms_internal_zzaal;
            super(looper);
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    this.zzaAz.zzvJ();
                    return;
                case 2:
                    this.zzaAz.resume();
                    return;
                default:
                    Log.w("GoogleApiClientImpl", "Unknown message id: " + message.what);
                    return;
            }
        }
    }

    static class zzb extends com.google.android.gms.internal.zzaar.zza {
        private WeakReference<zzaal> zzaAD;

        zzb(zzaal com_google_android_gms_internal_zzaal) {
            this.zzaAD = new WeakReference(com_google_android_gms_internal_zzaal);
        }

        public void zzvb() {
            zzaal com_google_android_gms_internal_zzaal = (zzaal) this.zzaAD.get();
            if (com_google_android_gms_internal_zzaal != null) {
                com_google_android_gms_internal_zzaal.resume();
            }
        }
    }

    public zzaal(Context context, Lock lock, Looper looper, zzg com_google_android_gms_common_internal_zzg, GoogleApiAvailability googleApiAvailability, com.google.android.gms.common.api.Api.zza<? extends zzaxn, zzaxo> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzaxn__com_google_android_gms_internal_zzaxo, Map<Api<?>, Integer> map, List<ConnectionCallbacks> list, List<OnConnectionFailedListener> list2, Map<zzc<?>, zze> map2, int i, int i2, ArrayList<zzzy> arrayList, boolean z) {
        this.mContext = context;
        this.zzazn = lock;
        this.zzayb = z;
        this.zzaAj = new zzm(looper, this.zzaAy);
        this.zzrx = looper;
        this.zzaAp = new zza(this, looper);
        this.zzaxX = googleApiAvailability;
        this.zzaxV = i;
        if (this.zzaxV >= 0) {
            this.zzaAv = Integer.valueOf(i2);
        }
        this.zzazu = map;
        this.zzaAr = map2;
        this.zzaAu = arrayList;
        this.zzaAx = new zzabq(this.zzaAr);
        for (ConnectionCallbacks registerConnectionCallbacks : list) {
            this.zzaAj.registerConnectionCallbacks(registerConnectionCallbacks);
        }
        for (OnConnectionFailedListener registerConnectionFailedListener : list2) {
            this.zzaAj.registerConnectionFailedListener(registerConnectionFailedListener);
        }
        this.zzazs = com_google_android_gms_common_internal_zzg;
        this.zzaxY = com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzaxn__com_google_android_gms_internal_zzaxo;
    }

    private void resume() {
        this.zzazn.lock();
        try {
            if (isResuming()) {
                zzvI();
            }
            this.zzazn.unlock();
        } catch (Throwable th) {
            this.zzazn.unlock();
        }
    }

    public static int zza(Iterable<zze> iterable, boolean z) {
        int i = 0;
        int i2 = 0;
        for (zze com_google_android_gms_common_api_Api_zze : iterable) {
            if (com_google_android_gms_common_api_Api_zze.zzqD()) {
                i2 = 1;
            }
            i = com_google_android_gms_common_api_Api_zze.zzqS() ? 1 : i;
        }
        return i2 != 0 ? (i == 0 || !z) ? 1 : 2 : 3;
    }

    private void zza(final GoogleApiClient googleApiClient, final zzabl com_google_android_gms_internal_zzabl, final boolean z) {
        zzabx.zzaFp.zzg(googleApiClient).setResultCallback(new ResultCallback<Status>(this) {
            final /* synthetic */ zzaal zzaAz;

            public /* synthetic */ void onResult(@NonNull Result result) {
                zzp((Status) result);
            }

            public void zzp(@NonNull Status status) {
                zzl.zzaa(this.zzaAz.mContext).zzre();
                if (status.isSuccess() && this.zzaAz.isConnected()) {
                    this.zzaAz.reconnect();
                }
                com_google_android_gms_internal_zzabl.zzb(status);
                if (z) {
                    googleApiClient.disconnect();
                }
            }
        });
    }

    private void zzb(@NonNull zzaav com_google_android_gms_internal_zzaav) {
        if (this.zzaxV >= 0) {
            zzzt.zza(com_google_android_gms_internal_zzaav).zzcu(this.zzaxV);
            return;
        }
        throw new IllegalStateException("Called stopAutoManage but automatic lifecycle management is not enabled.");
    }

    private void zzcx(int i) {
        if (this.zzaAv == null) {
            this.zzaAv = Integer.valueOf(i);
        } else if (this.zzaAv.intValue() != i) {
            String valueOf = String.valueOf(zzcy(i));
            String valueOf2 = String.valueOf(zzcy(this.zzaAv.intValue()));
            throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 51) + String.valueOf(valueOf2).length()).append("Cannot use sign-in mode: ").append(valueOf).append(". Mode was already set to ").append(valueOf2).toString());
        }
        if (this.zzaAk == null) {
            Object obj = null;
            Object obj2 = null;
            for (zze com_google_android_gms_common_api_Api_zze : this.zzaAr.values()) {
                if (com_google_android_gms_common_api_Api_zze.zzqD()) {
                    obj2 = 1;
                }
                obj = com_google_android_gms_common_api_Api_zze.zzqS() ? 1 : obj;
            }
            switch (this.zzaAv.intValue()) {
                case 1:
                    if (obj2 == null) {
                        throw new IllegalStateException("SIGN_IN_MODE_REQUIRED cannot be used on a GoogleApiClient that does not contain any authenticated APIs. Use connect() instead.");
                    } else if (obj != null) {
                        throw new IllegalStateException("Cannot use SIGN_IN_MODE_REQUIRED with GOOGLE_SIGN_IN_API. Use connect(SIGN_IN_MODE_OPTIONAL) instead.");
                    }
                    break;
                case 2:
                    if (obj2 != null) {
                        this.zzaAk = zzaaa.zza(this.mContext, this, this.zzazn, this.zzrx, this.zzaxX, this.zzaAr, this.zzazs, this.zzazu, this.zzaxY, this.zzaAu);
                        return;
                    }
                    break;
            }
            if (this.zzayb && obj == null) {
                this.zzaAk = new zzaac(this.mContext, this.zzazn, this.zzrx, this.zzaxX, this.zzaAr, this.zzazs, this.zzazu, this.zzaxY, this.zzaAu, this);
            } else {
                this.zzaAk = new zzaan(this.mContext, this, this.zzazn, this.zzrx, this.zzaxX, this.zzaAr, this.zzazs, this.zzazu, this.zzaxY, this.zzaAu, this);
            }
        }
    }

    static String zzcy(int i) {
        switch (i) {
            case 1:
                return "SIGN_IN_MODE_REQUIRED";
            case 2:
                return "SIGN_IN_MODE_OPTIONAL";
            case 3:
                return "SIGN_IN_MODE_NONE";
            default:
                return "UNKNOWN";
        }
    }

    private void zzvI() {
        this.zzaAj.zzxr();
        this.zzaAk.connect();
    }

    private void zzvJ() {
        this.zzazn.lock();
        try {
            if (zzvL()) {
                zzvI();
            }
            this.zzazn.unlock();
        } catch (Throwable th) {
            this.zzazn.unlock();
        }
    }

    public ConnectionResult blockingConnect() {
        boolean z = true;
        zzac.zza(Looper.myLooper() != Looper.getMainLooper(), (Object) "blockingConnect must not be called on the UI thread");
        this.zzazn.lock();
        try {
            if (this.zzaxV >= 0) {
                if (this.zzaAv == null) {
                    z = false;
                }
                zzac.zza(z, (Object) "Sign-in mode should have been set explicitly by auto-manage.");
            } else if (this.zzaAv == null) {
                this.zzaAv = Integer.valueOf(zza(this.zzaAr.values(), false));
            } else if (this.zzaAv.intValue() == 2) {
                throw new IllegalStateException("Cannot call blockingConnect() when sign-in mode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
            }
            zzcx(this.zzaAv.intValue());
            this.zzaAj.zzxr();
            ConnectionResult blockingConnect = this.zzaAk.blockingConnect();
            return blockingConnect;
        } finally {
            this.zzazn.unlock();
        }
    }

    public ConnectionResult blockingConnect(long j, @NonNull TimeUnit timeUnit) {
        boolean z = false;
        if (Looper.myLooper() != Looper.getMainLooper()) {
            z = true;
        }
        zzac.zza(z, (Object) "blockingConnect must not be called on the UI thread");
        zzac.zzb((Object) timeUnit, (Object) "TimeUnit must not be null");
        this.zzazn.lock();
        try {
            if (this.zzaAv == null) {
                this.zzaAv = Integer.valueOf(zza(this.zzaAr.values(), false));
            } else if (this.zzaAv.intValue() == 2) {
                throw new IllegalStateException("Cannot call blockingConnect() when sign-in mode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
            }
            zzcx(this.zzaAv.intValue());
            this.zzaAj.zzxr();
            ConnectionResult blockingConnect = this.zzaAk.blockingConnect(j, timeUnit);
            return blockingConnect;
        } finally {
            this.zzazn.unlock();
        }
    }

    public PendingResult<Status> clearDefaultAccountAndReconnect() {
        zzac.zza(isConnected(), (Object) "GoogleApiClient is not connected yet.");
        zzac.zza(this.zzaAv.intValue() != 2, (Object) "Cannot use clearDefaultAccountAndReconnect with GOOGLE_SIGN_IN_API");
        final PendingResult com_google_android_gms_internal_zzabl = new zzabl((GoogleApiClient) this);
        if (this.zzaAr.containsKey(zzabx.zzahc)) {
            zza(this, com_google_android_gms_internal_zzabl, false);
        } else {
            final AtomicReference atomicReference = new AtomicReference();
            GoogleApiClient build = new Builder(this.mContext).addApi(zzabx.API).addConnectionCallbacks(new ConnectionCallbacks(this) {
                final /* synthetic */ zzaal zzaAz;

                public void onConnected(Bundle bundle) {
                    this.zzaAz.zza((GoogleApiClient) atomicReference.get(), com_google_android_gms_internal_zzabl, true);
                }

                public void onConnectionSuspended(int i) {
                }
            }).addOnConnectionFailedListener(new OnConnectionFailedListener(this) {
                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    com_google_android_gms_internal_zzabl.zzb(new Status(8));
                }
            }).setHandler(this.zzaAp).build();
            atomicReference.set(build);
            build.connect();
        }
        return com_google_android_gms_internal_zzabl;
    }

    public void connect() {
        boolean z = false;
        this.zzazn.lock();
        try {
            if (this.zzaxV >= 0) {
                if (this.zzaAv != null) {
                    z = true;
                }
                zzac.zza(z, (Object) "Sign-in mode should have been set explicitly by auto-manage.");
            } else if (this.zzaAv == null) {
                this.zzaAv = Integer.valueOf(zza(this.zzaAr.values(), false));
            } else if (this.zzaAv.intValue() == 2) {
                throw new IllegalStateException("Cannot call connect() when SignInMode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
            }
            connect(this.zzaAv.intValue());
        } finally {
            this.zzazn.unlock();
        }
    }

    public void connect(int i) {
        boolean z = true;
        this.zzazn.lock();
        if (!(i == 3 || i == 1 || i == 2)) {
            z = false;
        }
        try {
            zzac.zzb(z, "Illegal sign-in mode: " + i);
            zzcx(i);
            zzvI();
        } finally {
            this.zzazn.unlock();
        }
    }

    public void disconnect() {
        this.zzazn.lock();
        try {
            this.zzaAx.release();
            if (this.zzaAk != null) {
                this.zzaAk.disconnect();
            }
            this.zzaAt.release();
            for (com.google.android.gms.internal.zzzv.zza com_google_android_gms_internal_zzzv_zza : this.zzaAl) {
                com_google_android_gms_internal_zzzv_zza.zza(null);
                com_google_android_gms_internal_zzzv_zza.cancel();
            }
            this.zzaAl.clear();
            if (this.zzaAk != null) {
                zzvL();
                this.zzaAj.zzxq();
                this.zzazn.unlock();
            }
        } finally {
            this.zzazn.unlock();
        }
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.append(str).append("mContext=").println(this.mContext);
        printWriter.append(str).append("mResuming=").print(this.zzaAm);
        printWriter.append(" mWorkQueue.size()=").print(this.zzaAl.size());
        this.zzaAx.dump(printWriter);
        if (this.zzaAk != null) {
            this.zzaAk.dump(str, fileDescriptor, printWriter, strArr);
        }
    }

    @NonNull
    public ConnectionResult getConnectionResult(@NonNull Api<?> api) {
        this.zzazn.lock();
        try {
            if (!isConnected() && !isResuming()) {
                throw new IllegalStateException("Cannot invoke getConnectionResult unless GoogleApiClient is connected");
            } else if (this.zzaAr.containsKey(api.zzuH())) {
                ConnectionResult connectionResult = this.zzaAk.getConnectionResult(api);
                if (connectionResult != null) {
                    this.zzazn.unlock();
                } else if (isResuming()) {
                    connectionResult = ConnectionResult.zzawX;
                } else {
                    Log.w("GoogleApiClientImpl", zzvN());
                    Log.wtf("GoogleApiClientImpl", String.valueOf(api.getName()).concat(" requested in getConnectionResult is not connected but is not present in the failed  connections map"), new Exception());
                    connectionResult = new ConnectionResult(8, null);
                    this.zzazn.unlock();
                }
                return connectionResult;
            } else {
                throw new IllegalArgumentException(String.valueOf(api.getName()).concat(" was never registered with GoogleApiClient"));
            }
        } finally {
            this.zzazn.unlock();
        }
    }

    public Context getContext() {
        return this.mContext;
    }

    public Looper getLooper() {
        return this.zzrx;
    }

    public int getSessionId() {
        return System.identityHashCode(this);
    }

    public boolean hasConnectedApi(@NonNull Api<?> api) {
        if (!isConnected()) {
            return false;
        }
        zze com_google_android_gms_common_api_Api_zze = (zze) this.zzaAr.get(api.zzuH());
        boolean z = com_google_android_gms_common_api_Api_zze != null && com_google_android_gms_common_api_Api_zze.isConnected();
        return z;
    }

    public boolean isConnected() {
        return this.zzaAk != null && this.zzaAk.isConnected();
    }

    public boolean isConnecting() {
        return this.zzaAk != null && this.zzaAk.isConnecting();
    }

    public boolean isConnectionCallbacksRegistered(@NonNull ConnectionCallbacks connectionCallbacks) {
        return this.zzaAj.isConnectionCallbacksRegistered(connectionCallbacks);
    }

    public boolean isConnectionFailedListenerRegistered(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
        return this.zzaAj.isConnectionFailedListenerRegistered(onConnectionFailedListener);
    }

    boolean isResuming() {
        return this.zzaAm;
    }

    public void reconnect() {
        disconnect();
        connect();
    }

    public void registerConnectionCallbacks(@NonNull ConnectionCallbacks connectionCallbacks) {
        this.zzaAj.registerConnectionCallbacks(connectionCallbacks);
    }

    public void registerConnectionFailedListener(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
        this.zzaAj.registerConnectionFailedListener(onConnectionFailedListener);
    }

    public void stopAutoManage(@NonNull FragmentActivity fragmentActivity) {
        zzb(new zzaav(fragmentActivity));
    }

    public void unregisterConnectionCallbacks(@NonNull ConnectionCallbacks connectionCallbacks) {
        this.zzaAj.unregisterConnectionCallbacks(connectionCallbacks);
    }

    public void unregisterConnectionFailedListener(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
        this.zzaAj.unregisterConnectionFailedListener(onConnectionFailedListener);
    }

    @NonNull
    public <C extends zze> C zza(@NonNull zzc<C> com_google_android_gms_common_api_Api_zzc_C) {
        Object obj = (zze) this.zzaAr.get(com_google_android_gms_common_api_Api_zzc_C);
        zzac.zzb(obj, (Object) "Appropriate Api was not requested.");
        return obj;
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, R extends Result, T extends com.google.android.gms.internal.zzzv.zza<R, A>> T zza(@NonNull T t) {
        zzac.zzb(t.zzuH() != null, (Object) "This task can not be enqueued (it's probably a Batch or malformed)");
        boolean containsKey = this.zzaAr.containsKey(t.zzuH());
        String name = t.getApi() != null ? t.getApi().getName() : "the API";
        zzac.zzb(containsKey, new StringBuilder(String.valueOf(name).length() + 65).append("GoogleApiClient is not configured to use ").append(name).append(" required for this call.").toString());
        this.zzazn.lock();
        try {
            if (this.zzaAk == null) {
                this.zzaAl.add(t);
            } else {
                t = this.zzaAk.zza((com.google.android.gms.internal.zzzv.zza) t);
                this.zzazn.unlock();
            }
            return t;
        } finally {
            this.zzazn.unlock();
        }
    }

    public void zza(zzabp com_google_android_gms_internal_zzabp) {
        this.zzazn.lock();
        try {
            if (this.zzaAw == null) {
                this.zzaAw = new HashSet();
            }
            this.zzaAw.add(com_google_android_gms_internal_zzabp);
        } finally {
            this.zzazn.unlock();
        }
    }

    public boolean zza(@NonNull Api<?> api) {
        return this.zzaAr.containsKey(api.zzuH());
    }

    public boolean zza(zzabi com_google_android_gms_internal_zzabi) {
        return this.zzaAk != null && this.zzaAk.zza(com_google_android_gms_internal_zzabi);
    }

    <C extends zze> C zzb(zzc<?> com_google_android_gms_common_api_Api_zzc_) {
        Object obj = (zze) this.zzaAr.get(com_google_android_gms_common_api_Api_zzc_);
        zzac.zzb(obj, (Object) "Appropriate Api was not requested.");
        return obj;
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, T extends com.google.android.gms.internal.zzzv.zza<? extends Result, A>> T zzb(@NonNull T t) {
        zzac.zzb(t.zzuH() != null, (Object) "This task can not be executed (it's probably a Batch or malformed)");
        boolean containsKey = this.zzaAr.containsKey(t.zzuH());
        String name = t.getApi() != null ? t.getApi().getName() : "the API";
        zzac.zzb(containsKey, new StringBuilder(String.valueOf(name).length() + 65).append("GoogleApiClient is not configured to use ").append(name).append(" required for this call.").toString());
        this.zzazn.lock();
        try {
            if (this.zzaAk == null) {
                throw new IllegalStateException("GoogleApiClient is not connected yet.");
            }
            if (isResuming()) {
                this.zzaAl.add(t);
                while (!this.zzaAl.isEmpty()) {
                    com.google.android.gms.internal.zzzv.zza com_google_android_gms_internal_zzzv_zza = (com.google.android.gms.internal.zzzv.zza) this.zzaAl.remove();
                    this.zzaAx.zzb(com_google_android_gms_internal_zzzv_zza);
                    com_google_android_gms_internal_zzzv_zza.zzA(Status.zzayj);
                }
            } else {
                t = this.zzaAk.zzb(t);
                this.zzazn.unlock();
            }
            return t;
        } finally {
            this.zzazn.unlock();
        }
    }

    public void zzb(zzabp com_google_android_gms_internal_zzabp) {
        this.zzazn.lock();
        try {
            if (this.zzaAw == null) {
                Log.wtf("GoogleApiClientImpl", "Attempted to remove pending transform when no transforms are registered.", new Exception());
            } else if (!this.zzaAw.remove(com_google_android_gms_internal_zzabp)) {
                Log.wtf("GoogleApiClientImpl", "Failed to remove pending transform - this may lead to memory leaks!", new Exception());
            } else if (!zzvM()) {
                this.zzaAk.zzvj();
            }
            this.zzazn.unlock();
        } catch (Throwable th) {
            this.zzazn.unlock();
        }
    }

    public void zzc(int i, boolean z) {
        if (i == 1 && !z) {
            zzvK();
        }
        this.zzaAx.zzww();
        this.zzaAj.zzcP(i);
        this.zzaAj.zzxq();
        if (i == 2) {
            zzvI();
        }
    }

    public void zzc(ConnectionResult connectionResult) {
        if (!this.zzaxX.zzd(this.mContext, connectionResult.getErrorCode())) {
            zzvL();
        }
        if (!isResuming()) {
            this.zzaAj.zzo(connectionResult);
            this.zzaAj.zzxq();
        }
    }

    public void zzo(Bundle bundle) {
        while (!this.zzaAl.isEmpty()) {
            zzb((com.google.android.gms.internal.zzzv.zza) this.zzaAl.remove());
        }
        this.zzaAj.zzq(bundle);
    }

    public <L> zzaaz<L> zzr(@NonNull L l) {
        this.zzazn.lock();
        try {
            zzaaz<L> zzb = this.zzaAt.zzb(l, this.zzrx);
            return zzb;
        } finally {
            this.zzazn.unlock();
        }
    }

    public void zzuN() {
        if (this.zzaAk != null) {
            this.zzaAk.zzuN();
        }
    }

    void zzvK() {
        if (!isResuming()) {
            this.zzaAm = true;
            if (this.zzaAq == null) {
                this.zzaAq = this.zzaxX.zza(this.mContext.getApplicationContext(), new zzb(this));
            }
            this.zzaAp.sendMessageDelayed(this.zzaAp.obtainMessage(1), this.zzaAn);
            this.zzaAp.sendMessageDelayed(this.zzaAp.obtainMessage(2), this.zzaAo);
        }
    }

    boolean zzvL() {
        if (!isResuming()) {
            return false;
        }
        this.zzaAm = false;
        this.zzaAp.removeMessages(2);
        this.zzaAp.removeMessages(1);
        if (this.zzaAq != null) {
            this.zzaAq.unregister();
            this.zzaAq = null;
        }
        return true;
    }

    boolean zzvM() {
        boolean z = false;
        this.zzazn.lock();
        try {
            if (this.zzaAw != null) {
                if (!this.zzaAw.isEmpty()) {
                    z = true;
                }
                this.zzazn.unlock();
            }
            return z;
        } finally {
            this.zzazn.unlock();
        }
    }

    String zzvN() {
        Writer stringWriter = new StringWriter();
        dump("", null, new PrintWriter(stringWriter), null);
        return stringWriter.toString();
    }
}
