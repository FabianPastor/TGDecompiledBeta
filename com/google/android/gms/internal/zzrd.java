package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
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
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzf;
import com.google.android.gms.common.internal.zzk;
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
import org.telegram.messenger.exoplayer.hls.HlsChunkSource;

public final class zzrd extends GoogleApiClient implements com.google.android.gms.internal.zzrm.zza {
    private final zzk Ab;
    private zzrm Ac = null;
    final Queue<com.google.android.gms.internal.zzqo.zza<?, ?>> Ad = new LinkedList();
    private volatile boolean Ae;
    private long Af = 120000;
    private long Ag = HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS;
    private final zza Ah;
    zzrj Ai;
    final Map<zzc<?>, zze> Aj;
    Set<Scope> Ak = new HashSet();
    private final zzrs Al = new zzrs();
    private final ArrayList<zzqr> Am;
    private Integer An = null;
    Set<zzsf> Ao = null;
    final zzsg Ap;
    private final com.google.android.gms.common.internal.zzk.zza Aq = new com.google.android.gms.common.internal.zzk.zza(this) {
        final /* synthetic */ zzrd Ar;

        {
            this.Ar = r1;
        }

        public boolean isConnected() {
            return this.Ar.isConnected();
        }

        public Bundle zzapn() {
            return null;
        }
    };
    private final Context mContext;
    private final int xN;
    private final GoogleApiAvailability xP;
    final com.google.android.gms.common.api.Api.zza<? extends zzxp, zzxq> xQ;
    private boolean xT;
    final zzf zP;
    private final Lock zg;
    final Map<Api<?>, Integer> zk;
    private final Looper zzajy;

    final class zza extends Handler {
        final /* synthetic */ zzrd Ar;

        zza(zzrd com_google_android_gms_internal_zzrd, Looper looper) {
            this.Ar = com_google_android_gms_internal_zzrd;
            super(looper);
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    this.Ar.zzasx();
                    return;
                case 2:
                    this.Ar.resume();
                    return;
                default:
                    Log.w("GoogleApiClientImpl", "Unknown message id: " + message.what);
                    return;
            }
        }
    }

    static class zzb extends com.google.android.gms.internal.zzrj.zza {
        private WeakReference<zzrd> Av;

        zzb(zzrd com_google_android_gms_internal_zzrd) {
            this.Av = new WeakReference(com_google_android_gms_internal_zzrd);
        }

        public void zzarr() {
            zzrd com_google_android_gms_internal_zzrd = (zzrd) this.Av.get();
            if (com_google_android_gms_internal_zzrd != null) {
                com_google_android_gms_internal_zzrd.resume();
            }
        }
    }

    public zzrd(Context context, Lock lock, Looper looper, zzf com_google_android_gms_common_internal_zzf, GoogleApiAvailability googleApiAvailability, com.google.android.gms.common.api.Api.zza<? extends zzxp, zzxq> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzxp__com_google_android_gms_internal_zzxq, Map<Api<?>, Integer> map, List<ConnectionCallbacks> list, List<OnConnectionFailedListener> list2, Map<zzc<?>, zze> map2, int i, int i2, ArrayList<zzqr> arrayList, boolean z) {
        this.mContext = context;
        this.zg = lock;
        this.xT = z;
        this.Ab = new zzk(looper, this.Aq);
        this.zzajy = looper;
        this.Ah = new zza(this, looper);
        this.xP = googleApiAvailability;
        this.xN = i;
        if (this.xN >= 0) {
            this.An = Integer.valueOf(i2);
        }
        this.zk = map;
        this.Aj = map2;
        this.Am = arrayList;
        this.Ap = new zzsg(this.Aj);
        for (ConnectionCallbacks registerConnectionCallbacks : list) {
            this.Ab.registerConnectionCallbacks(registerConnectionCallbacks);
        }
        for (OnConnectionFailedListener registerConnectionFailedListener : list2) {
            this.Ab.registerConnectionFailedListener(registerConnectionFailedListener);
        }
        this.zP = com_google_android_gms_common_internal_zzf;
        this.xQ = com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzxp__com_google_android_gms_internal_zzxq;
    }

    private void resume() {
        this.zg.lock();
        try {
            if (isResuming()) {
                zzasw();
            }
            this.zg.unlock();
        } catch (Throwable th) {
            this.zg.unlock();
        }
    }

    public static int zza(Iterable<zze> iterable, boolean z) {
        int i = 0;
        int i2 = 0;
        for (zze com_google_android_gms_common_api_Api_zze : iterable) {
            if (com_google_android_gms_common_api_Api_zze.zzain()) {
                i2 = 1;
            }
            i = com_google_android_gms_common_api_Api_zze.zzajc() ? 1 : i;
        }
        return i2 != 0 ? (i == 0 || !z) ? 1 : 2 : 3;
    }

    private void zza(final GoogleApiClient googleApiClient, final zzsc com_google_android_gms_internal_zzsc, final boolean z) {
        zzsn.EU.zzg(googleApiClient).setResultCallback(new ResultCallback<Status>(this) {
            final /* synthetic */ zzrd Ar;

            public /* synthetic */ void onResult(@NonNull Result result) {
                zzp((Status) result);
            }

            public void zzp(@NonNull Status status) {
                com.google.android.gms.auth.api.signin.internal.zzk.zzba(this.Ar.mContext).zzajo();
                if (status.isSuccess() && this.Ar.isConnected()) {
                    this.Ar.reconnect();
                }
                com_google_android_gms_internal_zzsc.zzc((Result) status);
                if (z) {
                    googleApiClient.disconnect();
                }
            }
        });
    }

    private void zzasw() {
        this.Ab.zzawd();
        this.Ac.connect();
    }

    private void zzasx() {
        this.zg.lock();
        try {
            if (zzasz()) {
                zzasw();
            }
            this.zg.unlock();
        } catch (Throwable th) {
            this.zg.unlock();
        }
    }

    private void zzb(@NonNull zzrn com_google_android_gms_internal_zzrn) {
        if (this.xN >= 0) {
            zzqm.zza(com_google_android_gms_internal_zzrn).zzfs(this.xN);
            return;
        }
        throw new IllegalStateException("Called stopAutoManage but automatic lifecycle management is not enabled.");
    }

    private void zzfv(int i) {
        if (this.An == null) {
            this.An = Integer.valueOf(i);
        } else if (this.An.intValue() != i) {
            String valueOf = String.valueOf(zzfw(i));
            String valueOf2 = String.valueOf(zzfw(this.An.intValue()));
            throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 51) + String.valueOf(valueOf2).length()).append("Cannot use sign-in mode: ").append(valueOf).append(". Mode was already set to ").append(valueOf2).toString());
        }
        if (this.Ac == null) {
            Object obj = null;
            Object obj2 = null;
            for (zze com_google_android_gms_common_api_Api_zze : this.Aj.values()) {
                if (com_google_android_gms_common_api_Api_zze.zzain()) {
                    obj2 = 1;
                }
                obj = com_google_android_gms_common_api_Api_zze.zzajc() ? 1 : obj;
            }
            switch (this.An.intValue()) {
                case 1:
                    if (obj2 == null) {
                        throw new IllegalStateException("SIGN_IN_MODE_REQUIRED cannot be used on a GoogleApiClient that does not contain any authenticated APIs. Use connect() instead.");
                    } else if (obj != null) {
                        throw new IllegalStateException("Cannot use SIGN_IN_MODE_REQUIRED with GOOGLE_SIGN_IN_API. Use connect(SIGN_IN_MODE_OPTIONAL) instead.");
                    }
                    break;
                case 2:
                    if (obj2 != null) {
                        this.Ac = zzqt.zza(this.mContext, this, this.zg, this.zzajy, this.xP, this.Aj, this.zP, this.zk, this.xQ, this.Am);
                        return;
                    }
                    break;
            }
            if (this.xT && obj2 == null && obj == null) {
                this.Ac = new zzqu(this.mContext, this.zg, this.zzajy, this.xP, this.Aj, this.zk, this.Am, this);
            } else {
                this.Ac = new zzrf(this.mContext, this, this.zg, this.zzajy, this.xP, this.Aj, this.zP, this.zk, this.xQ, this.Am, this);
            }
        }
    }

    static String zzfw(int i) {
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

    public ConnectionResult blockingConnect() {
        boolean z = true;
        zzaa.zza(Looper.myLooper() != Looper.getMainLooper(), (Object) "blockingConnect must not be called on the UI thread");
        this.zg.lock();
        try {
            if (this.xN >= 0) {
                if (this.An == null) {
                    z = false;
                }
                zzaa.zza(z, (Object) "Sign-in mode should have been set explicitly by auto-manage.");
            } else if (this.An == null) {
                this.An = Integer.valueOf(zza(this.Aj.values(), false));
            } else if (this.An.intValue() == 2) {
                throw new IllegalStateException("Cannot call blockingConnect() when sign-in mode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
            }
            zzfv(this.An.intValue());
            this.Ab.zzawd();
            ConnectionResult blockingConnect = this.Ac.blockingConnect();
            return blockingConnect;
        } finally {
            this.zg.unlock();
        }
    }

    public ConnectionResult blockingConnect(long j, @NonNull TimeUnit timeUnit) {
        boolean z = false;
        if (Looper.myLooper() != Looper.getMainLooper()) {
            z = true;
        }
        zzaa.zza(z, (Object) "blockingConnect must not be called on the UI thread");
        zzaa.zzb((Object) timeUnit, (Object) "TimeUnit must not be null");
        this.zg.lock();
        try {
            if (this.An == null) {
                this.An = Integer.valueOf(zza(this.Aj.values(), false));
            } else if (this.An.intValue() == 2) {
                throw new IllegalStateException("Cannot call blockingConnect() when sign-in mode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
            }
            zzfv(this.An.intValue());
            this.Ab.zzawd();
            ConnectionResult blockingConnect = this.Ac.blockingConnect(j, timeUnit);
            return blockingConnect;
        } finally {
            this.zg.unlock();
        }
    }

    public PendingResult<Status> clearDefaultAccountAndReconnect() {
        zzaa.zza(isConnected(), (Object) "GoogleApiClient is not connected yet.");
        zzaa.zza(this.An.intValue() != 2, (Object) "Cannot use clearDefaultAccountAndReconnect with GOOGLE_SIGN_IN_API");
        final PendingResult com_google_android_gms_internal_zzsc = new zzsc((GoogleApiClient) this);
        if (this.Aj.containsKey(zzsn.hg)) {
            zza(this, com_google_android_gms_internal_zzsc, false);
        } else {
            final AtomicReference atomicReference = new AtomicReference();
            GoogleApiClient build = new Builder(this.mContext).addApi(zzsn.API).addConnectionCallbacks(new ConnectionCallbacks(this) {
                final /* synthetic */ zzrd Ar;

                public void onConnected(Bundle bundle) {
                    this.Ar.zza((GoogleApiClient) atomicReference.get(), com_google_android_gms_internal_zzsc, true);
                }

                public void onConnectionSuspended(int i) {
                }
            }).addOnConnectionFailedListener(new OnConnectionFailedListener(this) {
                final /* synthetic */ zzrd Ar;

                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    com_google_android_gms_internal_zzsc.zzc(new Status(8));
                }
            }).setHandler(this.Ah).build();
            atomicReference.set(build);
            build.connect();
        }
        return com_google_android_gms_internal_zzsc;
    }

    public void connect() {
        boolean z = false;
        this.zg.lock();
        try {
            if (this.xN >= 0) {
                if (this.An != null) {
                    z = true;
                }
                zzaa.zza(z, (Object) "Sign-in mode should have been set explicitly by auto-manage.");
            } else if (this.An == null) {
                this.An = Integer.valueOf(zza(this.Aj.values(), false));
            } else if (this.An.intValue() == 2) {
                throw new IllegalStateException("Cannot call connect() when SignInMode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
            }
            connect(this.An.intValue());
        } finally {
            this.zg.unlock();
        }
    }

    public void connect(int i) {
        boolean z = true;
        this.zg.lock();
        if (!(i == 3 || i == 1 || i == 2)) {
            z = false;
        }
        try {
            zzaa.zzb(z, "Illegal sign-in mode: " + i);
            zzfv(i);
            zzasw();
        } finally {
            this.zg.unlock();
        }
    }

    public void disconnect() {
        this.zg.lock();
        try {
            this.Ap.release();
            if (this.Ac != null) {
                this.Ac.disconnect();
            }
            this.Al.release();
            for (com.google.android.gms.internal.zzqo.zza com_google_android_gms_internal_zzqo_zza : this.Ad) {
                com_google_android_gms_internal_zzqo_zza.zza(null);
                com_google_android_gms_internal_zzqo_zza.cancel();
            }
            this.Ad.clear();
            if (this.Ac != null) {
                zzasz();
                this.Ab.zzawc();
                this.zg.unlock();
            }
        } finally {
            this.zg.unlock();
        }
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.append(str).append("mContext=").println(this.mContext);
        printWriter.append(str).append("mResuming=").print(this.Ae);
        printWriter.append(" mWorkQueue.size()=").print(this.Ad.size());
        this.Ap.dump(printWriter);
        if (this.Ac != null) {
            this.Ac.dump(str, fileDescriptor, printWriter, strArr);
        }
    }

    @NonNull
    public ConnectionResult getConnectionResult(@NonNull Api<?> api) {
        this.zg.lock();
        try {
            if (!isConnected() && !isResuming()) {
                throw new IllegalStateException("Cannot invoke getConnectionResult unless GoogleApiClient is connected");
            } else if (this.Aj.containsKey(api.zzaqv())) {
                ConnectionResult connectionResult = this.Ac.getConnectionResult(api);
                if (connectionResult != null) {
                    this.zg.unlock();
                } else if (isResuming()) {
                    connectionResult = ConnectionResult.wO;
                } else {
                    Log.w("GoogleApiClientImpl", zzatb());
                    Log.wtf("GoogleApiClientImpl", String.valueOf(api.getName()).concat(" requested in getConnectionResult is not connected but is not present in the failed  connections map"), new Exception());
                    connectionResult = new ConnectionResult(8, null);
                    this.zg.unlock();
                }
                return connectionResult;
            } else {
                throw new IllegalArgumentException(String.valueOf(api.getName()).concat(" was never registered with GoogleApiClient"));
            }
        } finally {
            this.zg.unlock();
        }
    }

    public Context getContext() {
        return this.mContext;
    }

    public Looper getLooper() {
        return this.zzajy;
    }

    public int getSessionId() {
        return System.identityHashCode(this);
    }

    public boolean hasConnectedApi(@NonNull Api<?> api) {
        if (!isConnected()) {
            return false;
        }
        zze com_google_android_gms_common_api_Api_zze = (zze) this.Aj.get(api.zzaqv());
        boolean z = com_google_android_gms_common_api_Api_zze != null && com_google_android_gms_common_api_Api_zze.isConnected();
        return z;
    }

    public boolean isConnected() {
        return this.Ac != null && this.Ac.isConnected();
    }

    public boolean isConnecting() {
        return this.Ac != null && this.Ac.isConnecting();
    }

    public boolean isConnectionCallbacksRegistered(@NonNull ConnectionCallbacks connectionCallbacks) {
        return this.Ab.isConnectionCallbacksRegistered(connectionCallbacks);
    }

    public boolean isConnectionFailedListenerRegistered(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
        return this.Ab.isConnectionFailedListenerRegistered(onConnectionFailedListener);
    }

    boolean isResuming() {
        return this.Ae;
    }

    public void reconnect() {
        disconnect();
        connect();
    }

    public void registerConnectionCallbacks(@NonNull ConnectionCallbacks connectionCallbacks) {
        this.Ab.registerConnectionCallbacks(connectionCallbacks);
    }

    public void registerConnectionFailedListener(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
        this.Ab.registerConnectionFailedListener(onConnectionFailedListener);
    }

    public void stopAutoManage(@NonNull FragmentActivity fragmentActivity) {
        zzb(new zzrn(fragmentActivity));
    }

    public void unregisterConnectionCallbacks(@NonNull ConnectionCallbacks connectionCallbacks) {
        this.Ab.unregisterConnectionCallbacks(connectionCallbacks);
    }

    public void unregisterConnectionFailedListener(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
        this.Ab.unregisterConnectionFailedListener(onConnectionFailedListener);
    }

    @NonNull
    public <C extends zze> C zza(@NonNull zzc<C> com_google_android_gms_common_api_Api_zzc_C) {
        Object obj = (zze) this.Aj.get(com_google_android_gms_common_api_Api_zzc_C);
        zzaa.zzb(obj, (Object) "Appropriate Api was not requested.");
        return obj;
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, R extends Result, T extends com.google.android.gms.internal.zzqo.zza<R, A>> T zza(@NonNull T t) {
        zzaa.zzb(t.zzaqv() != null, (Object) "This task can not be enqueued (it's probably a Batch or malformed)");
        boolean containsKey = this.Aj.containsKey(t.zzaqv());
        String name = t.getApi() != null ? t.getApi().getName() : "the API";
        zzaa.zzb(containsKey, new StringBuilder(String.valueOf(name).length() + 65).append("GoogleApiClient is not configured to use ").append(name).append(" required for this call.").toString());
        this.zg.lock();
        try {
            if (this.Ac == null) {
                this.Ad.add(t);
            } else {
                t = this.Ac.zza((com.google.android.gms.internal.zzqo.zza) t);
                this.zg.unlock();
            }
            return t;
        } finally {
            this.zg.unlock();
        }
    }

    public void zza(zzsf com_google_android_gms_internal_zzsf) {
        this.zg.lock();
        try {
            if (this.Ao == null) {
                this.Ao = new HashSet();
            }
            this.Ao.add(com_google_android_gms_internal_zzsf);
        } finally {
            this.zg.unlock();
        }
    }

    public boolean zza(@NonNull Api<?> api) {
        return this.Aj.containsKey(api.zzaqv());
    }

    public boolean zza(zzsa com_google_android_gms_internal_zzsa) {
        return this.Ac != null && this.Ac.zza(com_google_android_gms_internal_zzsa);
    }

    public void zzard() {
        if (this.Ac != null) {
            this.Ac.zzard();
        }
    }

    void zzasy() {
        if (!isResuming()) {
            this.Ae = true;
            if (this.Ai == null) {
                this.Ai = this.xP.zza(this.mContext.getApplicationContext(), new zzb(this));
            }
            this.Ah.sendMessageDelayed(this.Ah.obtainMessage(1), this.Af);
            this.Ah.sendMessageDelayed(this.Ah.obtainMessage(2), this.Ag);
        }
    }

    boolean zzasz() {
        if (!isResuming()) {
            return false;
        }
        this.Ae = false;
        this.Ah.removeMessages(2);
        this.Ah.removeMessages(1);
        if (this.Ai != null) {
            this.Ai.unregister();
            this.Ai = null;
        }
        return true;
    }

    boolean zzata() {
        boolean z = false;
        this.zg.lock();
        try {
            if (this.Ao != null) {
                if (!this.Ao.isEmpty()) {
                    z = true;
                }
                this.zg.unlock();
            }
            return z;
        } finally {
            this.zg.unlock();
        }
    }

    String zzatb() {
        Writer stringWriter = new StringWriter();
        dump("", null, new PrintWriter(stringWriter), null);
        return stringWriter.toString();
    }

    <C extends zze> C zzb(zzc<?> com_google_android_gms_common_api_Api_zzc_) {
        Object obj = (zze) this.Aj.get(com_google_android_gms_common_api_Api_zzc_);
        zzaa.zzb(obj, (Object) "Appropriate Api was not requested.");
        return obj;
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, T extends com.google.android.gms.internal.zzqo.zza<? extends Result, A>> T zzb(@NonNull T t) {
        zzaa.zzb(t.zzaqv() != null, (Object) "This task can not be executed (it's probably a Batch or malformed)");
        boolean containsKey = this.Aj.containsKey(t.zzaqv());
        String name = t.getApi() != null ? t.getApi().getName() : "the API";
        zzaa.zzb(containsKey, new StringBuilder(String.valueOf(name).length() + 65).append("GoogleApiClient is not configured to use ").append(name).append(" required for this call.").toString());
        this.zg.lock();
        try {
            if (this.Ac == null) {
                throw new IllegalStateException("GoogleApiClient is not connected yet.");
            }
            if (isResuming()) {
                this.Ad.add(t);
                while (!this.Ad.isEmpty()) {
                    com.google.android.gms.internal.zzqo.zza com_google_android_gms_internal_zzqo_zza = (com.google.android.gms.internal.zzqo.zza) this.Ad.remove();
                    this.Ap.zzb(com_google_android_gms_internal_zzqo_zza);
                    com_google_android_gms_internal_zzqo_zza.zzaa(Status.yb);
                }
            } else {
                t = this.Ac.zzb(t);
                this.zg.unlock();
            }
            return t;
        } finally {
            this.zg.unlock();
        }
    }

    public void zzb(zzsf com_google_android_gms_internal_zzsf) {
        this.zg.lock();
        try {
            if (this.Ao == null) {
                Log.wtf("GoogleApiClientImpl", "Attempted to remove pending transform when no transforms are registered.", new Exception());
            } else if (!this.Ao.remove(com_google_android_gms_internal_zzsf)) {
                Log.wtf("GoogleApiClientImpl", "Failed to remove pending transform - this may lead to memory leaks!", new Exception());
            } else if (!zzata()) {
                this.Ac.zzarz();
            }
            this.zg.unlock();
        } catch (Throwable th) {
            this.zg.unlock();
        }
    }

    public void zzc(int i, boolean z) {
        if (i == 1 && !z) {
            zzasy();
        }
        this.Ap.zzauf();
        this.Ab.zzgn(i);
        this.Ab.zzawc();
        if (i == 2) {
            zzasw();
        }
    }

    public void zzc(ConnectionResult connectionResult) {
        if (!this.xP.zzd(this.mContext, connectionResult.getErrorCode())) {
            zzasz();
        }
        if (!isResuming()) {
            this.Ab.zzn(connectionResult);
            this.Ab.zzawc();
        }
    }

    public void zzn(Bundle bundle) {
        while (!this.Ad.isEmpty()) {
            zzb((com.google.android.gms.internal.zzqo.zza) this.Ad.remove());
        }
        this.Ab.zzp(bundle);
    }

    public <L> zzrr<L> zzs(@NonNull L l) {
        this.zg.lock();
        try {
            zzrr<L> zzb = this.Al.zzb(l, this.zzajy);
            return zzb;
        } finally {
            this.zg.unlock();
        }
    }
}
