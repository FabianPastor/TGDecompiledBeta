package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.google.android.gms.auth.api.signin.internal.zzn;
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
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;

public final class zzaat extends GoogleApiClient implements com.google.android.gms.internal.zzabc.zza {
    private final Context mContext;
    private final Lock zzaAG;
    final zzg zzaAL;
    final Map<Api<?>, Boolean> zzaAO;
    final Queue<com.google.android.gms.internal.zzaad.zza<?, ?>> zzaAU = new LinkedList();
    private final zzm zzaBJ;
    private zzabc zzaBK = null;
    private volatile boolean zzaBL;
    private long zzaBM = 120000;
    private long zzaBN = DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
    private final zza zzaBO;
    zzaaz zzaBP;
    final Map<zzc<?>, zze> zzaBQ;
    Set<Scope> zzaBR = new HashSet();
    private final zzabi zzaBS = new zzabi();
    private final ArrayList<zzaag> zzaBT;
    private Integer zzaBU = null;
    Set<zzabx> zzaBV = null;
    final zzaby zzaBW;
    private final com.google.android.gms.common.internal.zzm.zza zzaBX = new com.google.android.gms.common.internal.zzm.zza(this) {
        final /* synthetic */ zzaat zzaBY;

        {
            this.zzaBY = r1;
        }

        public boolean isConnected() {
            return this.zzaBY.isConnected();
        }

        public Bundle zzuB() {
            return null;
        }
    };
    private final int zzazl;
    private final GoogleApiAvailability zzazn;
    final com.google.android.gms.common.api.Api.zza<? extends zzbai, zzbaj> zzazo;
    private boolean zzazr;
    private final Looper zzrs;

    final class zza extends Handler {
        final /* synthetic */ zzaat zzaBY;

        zza(zzaat com_google_android_gms_internal_zzaat, Looper looper) {
            this.zzaBY = com_google_android_gms_internal_zzaat;
            super(looper);
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    this.zzaBY.zzwn();
                    return;
                case 2:
                    this.zzaBY.resume();
                    return;
                default:
                    Log.w("GoogleApiClientImpl", "Unknown message id: " + message.what);
                    return;
            }
        }
    }

    static class zzb extends com.google.android.gms.internal.zzaaz.zza {
        private WeakReference<zzaat> zzaCc;

        zzb(zzaat com_google_android_gms_internal_zzaat) {
            this.zzaCc = new WeakReference(com_google_android_gms_internal_zzaat);
        }

        public void zzvE() {
            zzaat com_google_android_gms_internal_zzaat = (zzaat) this.zzaCc.get();
            if (com_google_android_gms_internal_zzaat != null) {
                com_google_android_gms_internal_zzaat.resume();
            }
        }
    }

    public zzaat(Context context, Lock lock, Looper looper, zzg com_google_android_gms_common_internal_zzg, GoogleApiAvailability googleApiAvailability, com.google.android.gms.common.api.Api.zza<? extends zzbai, zzbaj> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzbai__com_google_android_gms_internal_zzbaj, Map<Api<?>, Boolean> map, List<ConnectionCallbacks> list, List<OnConnectionFailedListener> list2, Map<zzc<?>, zze> map2, int i, int i2, ArrayList<zzaag> arrayList, boolean z) {
        this.mContext = context;
        this.zzaAG = lock;
        this.zzazr = z;
        this.zzaBJ = new zzm(looper, this.zzaBX);
        this.zzrs = looper;
        this.zzaBO = new zza(this, looper);
        this.zzazn = googleApiAvailability;
        this.zzazl = i;
        if (this.zzazl >= 0) {
            this.zzaBU = Integer.valueOf(i2);
        }
        this.zzaAO = map;
        this.zzaBQ = map2;
        this.zzaBT = arrayList;
        this.zzaBW = new zzaby(this.zzaBQ);
        for (ConnectionCallbacks registerConnectionCallbacks : list) {
            this.zzaBJ.registerConnectionCallbacks(registerConnectionCallbacks);
        }
        for (OnConnectionFailedListener registerConnectionFailedListener : list2) {
            this.zzaBJ.registerConnectionFailedListener(registerConnectionFailedListener);
        }
        this.zzaAL = com_google_android_gms_common_internal_zzg;
        this.zzazo = com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzbai__com_google_android_gms_internal_zzbaj;
    }

    private void resume() {
        this.zzaAG.lock();
        try {
            if (isResuming()) {
                zzwm();
            }
            this.zzaAG.unlock();
        } catch (Throwable th) {
            this.zzaAG.unlock();
        }
    }

    public static int zza(Iterable<zze> iterable, boolean z) {
        int i = 0;
        int i2 = 0;
        for (zze com_google_android_gms_common_api_Api_zze : iterable) {
            if (com_google_android_gms_common_api_Api_zze.zzrd()) {
                i2 = 1;
            }
            i = com_google_android_gms_common_api_Api_zze.zzrr() ? 1 : i;
        }
        return i2 != 0 ? (i == 0 || !z) ? 1 : 2 : 3;
    }

    private void zza(final GoogleApiClient googleApiClient, final zzabt com_google_android_gms_internal_zzabt, final boolean z) {
        zzacf.zzaGM.zzg(googleApiClient).setResultCallback(new ResultCallback<Status>(this) {
            final /* synthetic */ zzaat zzaBY;

            public /* synthetic */ void onResult(@NonNull Result result) {
                zzp((Status) result);
            }

            public void zzp(@NonNull Status status) {
                zzn.zzas(this.zzaBY.mContext).zzrD();
                if (status.isSuccess() && this.zzaBY.isConnected()) {
                    this.zzaBY.reconnect();
                }
                com_google_android_gms_internal_zzabt.zzb(status);
                if (z) {
                    googleApiClient.disconnect();
                }
            }
        });
    }

    private void zzb(@NonNull zzabd com_google_android_gms_internal_zzabd) {
        if (this.zzazl >= 0) {
            zzaaa.zza(com_google_android_gms_internal_zzabd).zzcA(this.zzazl);
            return;
        }
        throw new IllegalStateException("Called stopAutoManage but automatic lifecycle management is not enabled.");
    }

    private void zzcD(int i) {
        if (this.zzaBU == null) {
            this.zzaBU = Integer.valueOf(i);
        } else if (this.zzaBU.intValue() != i) {
            String valueOf = String.valueOf(zzcE(i));
            String valueOf2 = String.valueOf(zzcE(this.zzaBU.intValue()));
            throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 51) + String.valueOf(valueOf2).length()).append("Cannot use sign-in mode: ").append(valueOf).append(". Mode was already set to ").append(valueOf2).toString());
        }
        if (this.zzaBK == null) {
            boolean z = false;
            boolean z2 = false;
            for (zze com_google_android_gms_common_api_Api_zze : this.zzaBQ.values()) {
                if (com_google_android_gms_common_api_Api_zze.zzrd()) {
                    z2 = true;
                }
                z = com_google_android_gms_common_api_Api_zze.zzrr() ? true : z;
            }
            switch (this.zzaBU.intValue()) {
                case 1:
                    if (!z2) {
                        throw new IllegalStateException("SIGN_IN_MODE_REQUIRED cannot be used on a GoogleApiClient that does not contain any authenticated APIs. Use connect() instead.");
                    } else if (z) {
                        throw new IllegalStateException("Cannot use SIGN_IN_MODE_REQUIRED with GOOGLE_SIGN_IN_API. Use connect(SIGN_IN_MODE_OPTIONAL) instead.");
                    }
                    break;
                case 2:
                    if (z2) {
                        if (this.zzazr) {
                            this.zzaBK = new zzaak(this.mContext, this.zzaAG, this.zzrs, this.zzazn, this.zzaBQ, this.zzaAL, this.zzaAO, this.zzazo, this.zzaBT, this, true);
                            return;
                        } else {
                            this.zzaBK = zzaai.zza(this.mContext, this, this.zzaAG, this.zzrs, this.zzazn, this.zzaBQ, this.zzaAL, this.zzaAO, this.zzazo, this.zzaBT);
                            return;
                        }
                    }
                    break;
            }
            if (!this.zzazr || z) {
                this.zzaBK = new zzaav(this.mContext, this, this.zzaAG, this.zzrs, this.zzazn, this.zzaBQ, this.zzaAL, this.zzaAO, this.zzazo, this.zzaBT, this);
            } else {
                this.zzaBK = new zzaak(this.mContext, this.zzaAG, this.zzrs, this.zzazn, this.zzaBQ, this.zzaAL, this.zzaAO, this.zzazo, this.zzaBT, this, false);
            }
        }
    }

    static String zzcE(int i) {
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

    private void zzwm() {
        this.zzaBJ.zzxY();
        this.zzaBK.connect();
    }

    private void zzwn() {
        this.zzaAG.lock();
        try {
            if (zzwp()) {
                zzwm();
            }
            this.zzaAG.unlock();
        } catch (Throwable th) {
            this.zzaAG.unlock();
        }
    }

    public ConnectionResult blockingConnect() {
        boolean z = true;
        zzac.zza(Looper.myLooper() != Looper.getMainLooper(), (Object) "blockingConnect must not be called on the UI thread");
        this.zzaAG.lock();
        try {
            if (this.zzazl >= 0) {
                if (this.zzaBU == null) {
                    z = false;
                }
                zzac.zza(z, (Object) "Sign-in mode should have been set explicitly by auto-manage.");
            } else if (this.zzaBU == null) {
                this.zzaBU = Integer.valueOf(zza(this.zzaBQ.values(), false));
            } else if (this.zzaBU.intValue() == 2) {
                throw new IllegalStateException("Cannot call blockingConnect() when sign-in mode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
            }
            zzcD(this.zzaBU.intValue());
            this.zzaBJ.zzxY();
            ConnectionResult blockingConnect = this.zzaBK.blockingConnect();
            return blockingConnect;
        } finally {
            this.zzaAG.unlock();
        }
    }

    public ConnectionResult blockingConnect(long j, @NonNull TimeUnit timeUnit) {
        boolean z = false;
        if (Looper.myLooper() != Looper.getMainLooper()) {
            z = true;
        }
        zzac.zza(z, (Object) "blockingConnect must not be called on the UI thread");
        zzac.zzb((Object) timeUnit, (Object) "TimeUnit must not be null");
        this.zzaAG.lock();
        try {
            if (this.zzaBU == null) {
                this.zzaBU = Integer.valueOf(zza(this.zzaBQ.values(), false));
            } else if (this.zzaBU.intValue() == 2) {
                throw new IllegalStateException("Cannot call blockingConnect() when sign-in mode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
            }
            zzcD(this.zzaBU.intValue());
            this.zzaBJ.zzxY();
            ConnectionResult blockingConnect = this.zzaBK.blockingConnect(j, timeUnit);
            return blockingConnect;
        } finally {
            this.zzaAG.unlock();
        }
    }

    public PendingResult<Status> clearDefaultAccountAndReconnect() {
        zzac.zza(isConnected(), (Object) "GoogleApiClient is not connected yet.");
        zzac.zza(this.zzaBU.intValue() != 2, (Object) "Cannot use clearDefaultAccountAndReconnect with GOOGLE_SIGN_IN_API");
        final PendingResult com_google_android_gms_internal_zzabt = new zzabt((GoogleApiClient) this);
        if (this.zzaBQ.containsKey(zzacf.zzaid)) {
            zza(this, com_google_android_gms_internal_zzabt, false);
        } else {
            final AtomicReference atomicReference = new AtomicReference();
            GoogleApiClient build = new Builder(this.mContext).addApi(zzacf.API).addConnectionCallbacks(new ConnectionCallbacks(this) {
                final /* synthetic */ zzaat zzaBY;

                public void onConnected(Bundle bundle) {
                    this.zzaBY.zza((GoogleApiClient) atomicReference.get(), com_google_android_gms_internal_zzabt, true);
                }

                public void onConnectionSuspended(int i) {
                }
            }).addOnConnectionFailedListener(new OnConnectionFailedListener(this) {
                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    com_google_android_gms_internal_zzabt.zzb(new Status(8));
                }
            }).setHandler(this.zzaBO).build();
            atomicReference.set(build);
            build.connect();
        }
        return com_google_android_gms_internal_zzabt;
    }

    public void connect() {
        boolean z = false;
        this.zzaAG.lock();
        try {
            if (this.zzazl >= 0) {
                if (this.zzaBU != null) {
                    z = true;
                }
                zzac.zza(z, (Object) "Sign-in mode should have been set explicitly by auto-manage.");
            } else if (this.zzaBU == null) {
                this.zzaBU = Integer.valueOf(zza(this.zzaBQ.values(), false));
            } else if (this.zzaBU.intValue() == 2) {
                throw new IllegalStateException("Cannot call connect() when SignInMode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
            }
            connect(this.zzaBU.intValue());
        } finally {
            this.zzaAG.unlock();
        }
    }

    public void connect(int i) {
        boolean z = true;
        this.zzaAG.lock();
        if (!(i == 3 || i == 1 || i == 2)) {
            z = false;
        }
        try {
            zzac.zzb(z, "Illegal sign-in mode: " + i);
            zzcD(i);
            zzwm();
        } finally {
            this.zzaAG.unlock();
        }
    }

    public void disconnect() {
        this.zzaAG.lock();
        try {
            this.zzaBW.release();
            if (this.zzaBK != null) {
                this.zzaBK.disconnect();
            }
            this.zzaBS.release();
            for (com.google.android.gms.internal.zzaad.zza com_google_android_gms_internal_zzaad_zza : this.zzaAU) {
                com_google_android_gms_internal_zzaad_zza.zza(null);
                com_google_android_gms_internal_zzaad_zza.cancel();
            }
            this.zzaAU.clear();
            if (this.zzaBK != null) {
                zzwp();
                this.zzaBJ.zzxX();
                this.zzaAG.unlock();
            }
        } finally {
            this.zzaAG.unlock();
        }
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.append(str).append("mContext=").println(this.mContext);
        printWriter.append(str).append("mResuming=").print(this.zzaBL);
        printWriter.append(" mWorkQueue.size()=").print(this.zzaAU.size());
        this.zzaBW.dump(printWriter);
        if (this.zzaBK != null) {
            this.zzaBK.dump(str, fileDescriptor, printWriter, strArr);
        }
    }

    @NonNull
    public ConnectionResult getConnectionResult(@NonNull Api<?> api) {
        this.zzaAG.lock();
        try {
            if (!isConnected() && !isResuming()) {
                throw new IllegalStateException("Cannot invoke getConnectionResult unless GoogleApiClient is connected");
            } else if (this.zzaBQ.containsKey(api.zzvg())) {
                ConnectionResult connectionResult = this.zzaBK.getConnectionResult(api);
                if (connectionResult != null) {
                    this.zzaAG.unlock();
                } else if (isResuming()) {
                    connectionResult = ConnectionResult.zzayj;
                } else {
                    Log.w("GoogleApiClientImpl", zzwr());
                    Log.wtf("GoogleApiClientImpl", String.valueOf(api.getName()).concat(" requested in getConnectionResult is not connected but is not present in the failed  connections map"), new Exception());
                    connectionResult = new ConnectionResult(8, null);
                    this.zzaAG.unlock();
                }
                return connectionResult;
            } else {
                throw new IllegalArgumentException(String.valueOf(api.getName()).concat(" was never registered with GoogleApiClient"));
            }
        } finally {
            this.zzaAG.unlock();
        }
    }

    public Context getContext() {
        return this.mContext;
    }

    public Looper getLooper() {
        return this.zzrs;
    }

    public int getSessionId() {
        return System.identityHashCode(this);
    }

    public boolean hasConnectedApi(@NonNull Api<?> api) {
        if (!isConnected()) {
            return false;
        }
        zze com_google_android_gms_common_api_Api_zze = (zze) this.zzaBQ.get(api.zzvg());
        boolean z = com_google_android_gms_common_api_Api_zze != null && com_google_android_gms_common_api_Api_zze.isConnected();
        return z;
    }

    public boolean isConnected() {
        return this.zzaBK != null && this.zzaBK.isConnected();
    }

    public boolean isConnecting() {
        return this.zzaBK != null && this.zzaBK.isConnecting();
    }

    public boolean isConnectionCallbacksRegistered(@NonNull ConnectionCallbacks connectionCallbacks) {
        return this.zzaBJ.isConnectionCallbacksRegistered(connectionCallbacks);
    }

    public boolean isConnectionFailedListenerRegistered(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
        return this.zzaBJ.isConnectionFailedListenerRegistered(onConnectionFailedListener);
    }

    boolean isResuming() {
        return this.zzaBL;
    }

    public void reconnect() {
        disconnect();
        connect();
    }

    public void registerConnectionCallbacks(@NonNull ConnectionCallbacks connectionCallbacks) {
        this.zzaBJ.registerConnectionCallbacks(connectionCallbacks);
    }

    public void registerConnectionFailedListener(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
        this.zzaBJ.registerConnectionFailedListener(onConnectionFailedListener);
    }

    public void stopAutoManage(@NonNull FragmentActivity fragmentActivity) {
        zzb(new zzabd(fragmentActivity));
    }

    public void unregisterConnectionCallbacks(@NonNull ConnectionCallbacks connectionCallbacks) {
        this.zzaBJ.unregisterConnectionCallbacks(connectionCallbacks);
    }

    public void unregisterConnectionFailedListener(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
        this.zzaBJ.unregisterConnectionFailedListener(onConnectionFailedListener);
    }

    @NonNull
    public <C extends zze> C zza(@NonNull zzc<C> com_google_android_gms_common_api_Api_zzc_C) {
        Object obj = (zze) this.zzaBQ.get(com_google_android_gms_common_api_Api_zzc_C);
        zzac.zzb(obj, (Object) "Appropriate Api was not requested.");
        return obj;
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, R extends Result, T extends com.google.android.gms.internal.zzaad.zza<R, A>> T zza(@NonNull T t) {
        zzac.zzb(t.zzvg() != null, (Object) "This task can not be enqueued (it's probably a Batch or malformed)");
        boolean containsKey = this.zzaBQ.containsKey(t.zzvg());
        String name = t.getApi() != null ? t.getApi().getName() : "the API";
        zzac.zzb(containsKey, new StringBuilder(String.valueOf(name).length() + 65).append("GoogleApiClient is not configured to use ").append(name).append(" required for this call.").toString());
        this.zzaAG.lock();
        try {
            if (this.zzaBK == null) {
                this.zzaAU.add(t);
            } else {
                t = this.zzaBK.zza((com.google.android.gms.internal.zzaad.zza) t);
                this.zzaAG.unlock();
            }
            return t;
        } finally {
            this.zzaAG.unlock();
        }
    }

    public void zza(zzabx com_google_android_gms_internal_zzabx) {
        this.zzaAG.lock();
        try {
            if (this.zzaBV == null) {
                this.zzaBV = new HashSet();
            }
            this.zzaBV.add(com_google_android_gms_internal_zzabx);
        } finally {
            this.zzaAG.unlock();
        }
    }

    public boolean zza(@NonNull Api<?> api) {
        return this.zzaBQ.containsKey(api.zzvg());
    }

    public boolean zza(zzabq com_google_android_gms_internal_zzabq) {
        return this.zzaBK != null && this.zzaBK.zza(com_google_android_gms_internal_zzabq);
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, T extends com.google.android.gms.internal.zzaad.zza<? extends Result, A>> T zzb(@NonNull T t) {
        zzac.zzb(t.zzvg() != null, (Object) "This task can not be executed (it's probably a Batch or malformed)");
        boolean containsKey = this.zzaBQ.containsKey(t.zzvg());
        String name = t.getApi() != null ? t.getApi().getName() : "the API";
        zzac.zzb(containsKey, new StringBuilder(String.valueOf(name).length() + 65).append("GoogleApiClient is not configured to use ").append(name).append(" required for this call.").toString());
        this.zzaAG.lock();
        try {
            if (this.zzaBK == null) {
                throw new IllegalStateException("GoogleApiClient is not connected yet.");
            }
            if (isResuming()) {
                this.zzaAU.add(t);
                while (!this.zzaAU.isEmpty()) {
                    com.google.android.gms.internal.zzaad.zza com_google_android_gms_internal_zzaad_zza = (com.google.android.gms.internal.zzaad.zza) this.zzaAU.remove();
                    this.zzaBW.zzb(com_google_android_gms_internal_zzaad_zza);
                    com_google_android_gms_internal_zzaad_zza.zzB(Status.zzazz);
                }
            } else {
                t = this.zzaBK.zzb(t);
                this.zzaAG.unlock();
            }
            return t;
        } finally {
            this.zzaAG.unlock();
        }
    }

    public void zzb(zzabx com_google_android_gms_internal_zzabx) {
        this.zzaAG.lock();
        try {
            if (this.zzaBV == null) {
                Log.wtf("GoogleApiClientImpl", "Attempted to remove pending transform when no transforms are registered.", new Exception());
            } else if (!this.zzaBV.remove(com_google_android_gms_internal_zzabx)) {
                Log.wtf("GoogleApiClientImpl", "Failed to remove pending transform - this may lead to memory leaks!", new Exception());
            } else if (!zzwq()) {
                this.zzaBK.zzvM();
            }
            this.zzaAG.unlock();
        } catch (Throwable th) {
            this.zzaAG.unlock();
        }
    }

    <C extends zze> C zzc(zzc<?> com_google_android_gms_common_api_Api_zzc_) {
        Object obj = (zze) this.zzaBQ.get(com_google_android_gms_common_api_Api_zzc_);
        zzac.zzb(obj, (Object) "Appropriate Api was not requested.");
        return obj;
    }

    public void zzc(int i, boolean z) {
        if (i == 1 && !z) {
            zzwo();
        }
        this.zzaBW.zzxd();
        this.zzaBJ.zzcV(i);
        this.zzaBJ.zzxX();
        if (i == 2) {
            zzwm();
        }
    }

    public void zzc(ConnectionResult connectionResult) {
        if (!this.zzazn.zzd(this.mContext, connectionResult.getErrorCode())) {
            zzwp();
        }
        if (!isResuming()) {
            this.zzaBJ.zzn(connectionResult);
            this.zzaBJ.zzxX();
        }
    }

    public void zzo(Bundle bundle) {
        while (!this.zzaAU.isEmpty()) {
            zzb((com.google.android.gms.internal.zzaad.zza) this.zzaAU.remove());
        }
        this.zzaBJ.zzq(bundle);
    }

    public <L> zzabh<L> zzr(@NonNull L l) {
        this.zzaAG.lock();
        try {
            zzabh<L> zzb = this.zzaBS.zzb(l, this.zzrs);
            return zzb;
        } finally {
            this.zzaAG.unlock();
        }
    }

    public void zzvn() {
        if (this.zzaBK != null) {
            this.zzaBK.zzvn();
        }
    }

    void zzwo() {
        if (!isResuming()) {
            this.zzaBL = true;
            if (this.zzaBP == null) {
                this.zzaBP = this.zzazn.zza(this.mContext.getApplicationContext(), new zzb(this));
            }
            this.zzaBO.sendMessageDelayed(this.zzaBO.obtainMessage(1), this.zzaBM);
            this.zzaBO.sendMessageDelayed(this.zzaBO.obtainMessage(2), this.zzaBN);
        }
    }

    boolean zzwp() {
        if (!isResuming()) {
            return false;
        }
        this.zzaBL = false;
        this.zzaBO.removeMessages(2);
        this.zzaBO.removeMessages(1);
        if (this.zzaBP != null) {
            this.zzaBP.unregister();
            this.zzaBP = null;
        }
        return true;
    }

    boolean zzwq() {
        boolean z = false;
        this.zzaAG.lock();
        try {
            if (this.zzaBV != null) {
                if (!this.zzaBV.isEmpty()) {
                    z = true;
                }
                this.zzaAG.unlock();
            }
            return z;
        } finally {
            this.zzaAG.unlock();
        }
    }

    String zzwr() {
        Writer stringWriter = new StringWriter();
        dump("", null, new PrintWriter(stringWriter), null);
        return stringWriter.toString();
    }
}
