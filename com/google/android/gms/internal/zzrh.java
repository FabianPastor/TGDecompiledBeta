package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.zzc;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzag;
import com.google.android.gms.common.internal.zze.zzf;
import com.google.android.gms.internal.zzqj.zzd;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.exoplayer.chunk.FormatEvaluator.AdaptiveEvaluator;
import org.telegram.messenger.exoplayer.hls.HlsChunkSource;

public class zzrh implements Callback {
    public static final Status AG = new Status(4, "Sign-out occurred while this API call was in progress.");
    private static final Status AH = new Status(4, "The user must be signed in to make this API call.");
    private static zzrh AJ;
    private static final Object zzaox = new Object();
    private long AI;
    private int AK;
    private final AtomicInteger AL;
    private final AtomicInteger AM;
    private zzqw AN;
    private final Set<zzql<?>> AO;
    private final Set<zzql<?>> AP;
    private long Af;
    private long Ag;
    private final Context mContext;
    private final Handler mHandler;
    private final GoogleApiAvailability xP;
    private final Map<zzql<?>, zza<?>> zj;

    private class zzb implements zzf {
        final /* synthetic */ zzrh AX;
        private final zze xB;
        private final zzql<?> xx;

        public zzb(zzrh com_google_android_gms_internal_zzrh, zze com_google_android_gms_common_api_Api_zze, zzql<?> com_google_android_gms_internal_zzql_) {
            this.AX = com_google_android_gms_internal_zzrh;
            this.xB = com_google_android_gms_common_api_Api_zze;
            this.xx = com_google_android_gms_internal_zzql_;
        }

        @WorkerThread
        public void zzg(@NonNull ConnectionResult connectionResult) {
            if (!connectionResult.isSuccess()) {
                ((zza) this.AX.zj.get(this.xx)).onConnectionFailed(connectionResult);
            } else if (!this.xB.zzain()) {
                this.xB.zza(null, Collections.emptySet());
            }
        }
    }

    class zza<O extends ApiOptions> implements ConnectionCallbacks, OnConnectionFailedListener, zzqs {
        private final Queue<zzqj> AQ = new LinkedList();
        private final com.google.android.gms.common.api.Api.zzb AR;
        private final zzqv AS;
        private final Set<zzqn> AT = new HashSet();
        private final Map<com.google.android.gms.internal.zzrr.zzb<?>, zzrx> AU = new HashMap();
        private final int AV;
        private ConnectionResult AW = null;
        final /* synthetic */ zzrh AX;
        private boolean Ae;
        private final zze xB;
        private final zzql<O> xx;

        @WorkerThread
        public zza(zzrh com_google_android_gms_internal_zzrh, zzc<O> com_google_android_gms_common_api_zzc_O) {
            this.AX = com_google_android_gms_internal_zzrh;
            if (com_google_android_gms_common_api_zzc_O.isConnectionlessGoogleApiClient()) {
                this.xB = com_google_android_gms_common_api_zzc_O.getClient();
                com_google_android_gms_common_api_zzc_O.getClientCallbacks().zza(this);
            } else {
                this.xB = com_google_android_gms_common_api_zzc_O.buildApiClient(com_google_android_gms_internal_zzrh.mHandler.getLooper(), this, this);
            }
            if (this.xB instanceof zzag) {
                this.AR = ((zzag) this.xB).zzawt();
            } else {
                this.AR = this.xB;
            }
            this.xx = com_google_android_gms_common_api_zzc_O.getApiKey();
            this.AS = new zzqv();
            this.AV = com_google_android_gms_common_api_zzc_O.getInstanceId();
        }

        @WorkerThread
        private void connect() {
            if (!this.xB.isConnected() && !this.xB.isConnecting()) {
                if (this.xB.zzaqx() && this.AX.AK != 0) {
                    this.AX.AK = this.AX.xP.isGooglePlayServicesAvailable(this.AX.mContext);
                    if (this.AX.AK != 0) {
                        onConnectionFailed(new ConnectionResult(this.AX.AK, null));
                        return;
                    }
                }
                if (this.xB.zzain()) {
                    this.xB.zza(new zzb(this.AX, this.xB, this.xx));
                } else {
                    this.xB.zza(new zzb(this.AX, this.xB, this.xx));
                }
            }
        }

        @WorkerThread
        private void resume() {
            if (this.Ae) {
                connect();
            }
        }

        @WorkerThread
        private void zzac(Status status) {
            for (zzqj zzy : this.AQ) {
                zzy.zzy(status);
            }
            this.AQ.clear();
        }

        @WorkerThread
        private void zzasx() {
            if (this.Ae) {
                zzatq();
                zzac(this.AX.xP.isGooglePlayServicesAvailable(this.AX.mContext) == 18 ? new Status(8, "Connection timed out while waiting for Google Play services update to complete.") : new Status(8, "API failed to connect while resuming due to an unknown error."));
                this.xB.disconnect();
            }
        }

        @WorkerThread
        private void zzatq() {
            if (this.Ae) {
                this.AX.mHandler.removeMessages(9, this.xx);
                this.AX.mHandler.removeMessages(7, this.xx);
                this.Ae = false;
            }
        }

        private void zzatr() {
            this.AX.mHandler.removeMessages(10, this.xx);
            this.AX.mHandler.sendMessageDelayed(this.AX.mHandler.obtainMessage(10, this.xx), this.AX.AI);
        }

        private void zzats() {
            if (!this.xB.isConnected() || this.AU.size() != 0) {
                return;
            }
            if (this.AS.zzasi()) {
                zzatr();
            } else {
                this.xB.disconnect();
            }
        }

        @WorkerThread
        private void zzb(zzqj com_google_android_gms_internal_zzqj) {
            com_google_android_gms_internal_zzqj.zza(this.AS, zzain());
            try {
                com_google_android_gms_internal_zzqj.zza(this);
            } catch (DeadObjectException e) {
                onConnectionSuspended(1);
                this.xB.disconnect();
            }
        }

        @WorkerThread
        private void zzi(ConnectionResult connectionResult) {
            for (zzqn zza : this.AT) {
                zza.zza(this.xx, connectionResult);
            }
            this.AT.clear();
        }

        public zze getClient() {
            return this.xB;
        }

        public int getInstanceId() {
            return this.AV;
        }

        boolean isConnected() {
            return this.xB.isConnected();
        }

        @WorkerThread
        public void onConnected(@Nullable Bundle bundle) {
            zzato();
            zzi(ConnectionResult.wO);
            zzatq();
            for (zzrx com_google_android_gms_internal_zzrx : this.AU.values()) {
                try {
                    com_google_android_gms_internal_zzrx.yi.zza(this.AR, new TaskCompletionSource());
                } catch (DeadObjectException e) {
                    onConnectionSuspended(1);
                    this.xB.disconnect();
                }
            }
            zzatm();
            zzatr();
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        @WorkerThread
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            zzato();
            this.AX.AK = -1;
            zzi(connectionResult);
            if (connectionResult.getErrorCode() == 4) {
                zzac(zzrh.AH);
            } else if (this.AQ.isEmpty()) {
                this.AW = connectionResult;
            } else {
                synchronized (zzrh.zzaox) {
                    if (this.AX.AN != null && this.AX.AO.contains(this.xx)) {
                        this.AX.AN.zzb(connectionResult, this.AV);
                    }
                }
            }
        }

        @WorkerThread
        public void onConnectionSuspended(int i) {
            zzato();
            this.Ae = true;
            this.AS.zzask();
            this.AX.mHandler.sendMessageDelayed(Message.obtain(this.AX.mHandler, 7, this.xx), this.AX.Ag);
            this.AX.mHandler.sendMessageDelayed(Message.obtain(this.AX.mHandler, 9, this.xx), this.AX.Af);
            this.AX.AK = -1;
        }

        @WorkerThread
        public void signOut() {
            zzac(zzrh.AG);
            this.AS.zzasj();
            for (com.google.android.gms.internal.zzrr.zzb com_google_android_gms_internal_zzqj_zze : this.AU.keySet()) {
                zza(new zzqj.zze(com_google_android_gms_internal_zzqj_zze, new TaskCompletionSource()));
            }
            this.xB.disconnect();
        }

        public void zza(ConnectionResult connectionResult, Api<?> api, int i) {
            onConnectionFailed(connectionResult);
        }

        @WorkerThread
        public void zza(zzqj com_google_android_gms_internal_zzqj) {
            if (this.xB.isConnected()) {
                zzb(com_google_android_gms_internal_zzqj);
                zzatr();
                return;
            }
            this.AQ.add(com_google_android_gms_internal_zzqj);
            if (this.AW == null || !this.AW.hasResolution()) {
                connect();
            } else {
                onConnectionFailed(this.AW);
            }
        }

        public boolean zzain() {
            return this.xB.zzain();
        }

        @WorkerThread
        public void zzatm() {
            while (this.xB.isConnected() && !this.AQ.isEmpty()) {
                zzb((zzqj) this.AQ.remove());
            }
        }

        public Map<com.google.android.gms.internal.zzrr.zzb<?>, zzrx> zzatn() {
            return this.AU;
        }

        @WorkerThread
        public void zzato() {
            this.AW = null;
        }

        ConnectionResult zzatp() {
            return this.AW;
        }

        @WorkerThread
        public void zzb(zzqn com_google_android_gms_internal_zzqn) {
            this.AT.add(com_google_android_gms_internal_zzqn);
        }
    }

    private zzrh(Context context) {
        this(context, GoogleApiAvailability.getInstance());
    }

    private zzrh(Context context, GoogleApiAvailability googleApiAvailability) {
        this.Ag = HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS;
        this.Af = 120000;
        this.AI = 10000;
        this.AK = -1;
        this.AL = new AtomicInteger(1);
        this.AM = new AtomicInteger(0);
        this.zj = new ConcurrentHashMap(5, AdaptiveEvaluator.DEFAULT_BANDWIDTH_FRACTION, 1);
        this.AN = null;
        this.AO = new com.google.android.gms.common.util.zza();
        this.AP = new com.google.android.gms.common.util.zza();
        this.mContext = context;
        HandlerThread handlerThread = new HandlerThread("GoogleApiHandler", 9);
        handlerThread.start();
        this.mHandler = new Handler(handlerThread.getLooper(), this);
        this.xP = googleApiAvailability;
    }

    @WorkerThread
    private void zza(int i, ConnectionResult connectionResult) {
        for (zza com_google_android_gms_internal_zzrh_zza : this.zj.values()) {
            if (com_google_android_gms_internal_zzrh_zza.getInstanceId() == i) {
                break;
            }
        }
        zza com_google_android_gms_internal_zzrh_zza2 = null;
        if (com_google_android_gms_internal_zzrh_zza2 != null) {
            String valueOf = String.valueOf(this.xP.getErrorString(connectionResult.getErrorCode()));
            String valueOf2 = String.valueOf(connectionResult.getErrorMessage());
            com_google_android_gms_internal_zzrh_zza2.zzac(new Status(17, new StringBuilder((String.valueOf(valueOf).length() + 69) + String.valueOf(valueOf2).length()).append("Error resolution was canceled by the user, original error message: ").append(valueOf).append(": ").append(valueOf2).toString()));
            return;
        }
        Log.wtf("GoogleApiManager", "Could not find API instance " + i + " while trying to fail enqueued calls.", new Exception());
    }

    @WorkerThread
    private void zza(zzrv com_google_android_gms_internal_zzrv) {
        zza com_google_android_gms_internal_zzrh_zza = (zza) this.zj.get(com_google_android_gms_internal_zzrv.Bs.getApiKey());
        if (com_google_android_gms_internal_zzrh_zza == null) {
            zzb(com_google_android_gms_internal_zzrv.Bs);
            com_google_android_gms_internal_zzrh_zza = (zza) this.zj.get(com_google_android_gms_internal_zzrv.Bs.getApiKey());
        }
        if (!com_google_android_gms_internal_zzrh_zza.zzain() || this.AM.get() == com_google_android_gms_internal_zzrv.Br) {
            com_google_android_gms_internal_zzrh_zza.zza(com_google_android_gms_internal_zzrv.Bq);
            return;
        }
        com_google_android_gms_internal_zzrv.Bq.zzy(AG);
        com_google_android_gms_internal_zzrh_zza.signOut();
    }

    public static zzrh zzatg() {
        zzrh com_google_android_gms_internal_zzrh;
        synchronized (zzaox) {
            zzaa.zzb(AJ, (Object) "Must guarantee manager is non-null before using getInstance");
            com_google_android_gms_internal_zzrh = AJ;
        }
        return com_google_android_gms_internal_zzrh;
    }

    @WorkerThread
    private void zzati() {
        for (zza com_google_android_gms_internal_zzrh_zza : this.zj.values()) {
            com_google_android_gms_internal_zzrh_zza.zzato();
            com_google_android_gms_internal_zzrh_zza.connect();
        }
    }

    @WorkerThread
    private void zzb(zzc<?> com_google_android_gms_common_api_zzc_) {
        zzql apiKey = com_google_android_gms_common_api_zzc_.getApiKey();
        if (!this.zj.containsKey(apiKey)) {
            this.zj.put(apiKey, new zza(this, com_google_android_gms_common_api_zzc_));
        }
        zza com_google_android_gms_internal_zzrh_zza = (zza) this.zj.get(apiKey);
        if (com_google_android_gms_internal_zzrh_zza.zzain()) {
            this.AP.add(apiKey);
        }
        com_google_android_gms_internal_zzrh_zza.connect();
    }

    public static zzrh zzbx(Context context) {
        zzrh com_google_android_gms_internal_zzrh;
        synchronized (zzaox) {
            if (AJ == null) {
                AJ = new zzrh(context.getApplicationContext());
            }
            com_google_android_gms_internal_zzrh = AJ;
        }
        return com_google_android_gms_internal_zzrh;
    }

    @WorkerThread
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case 1:
                zza((zzqn) message.obj);
                break;
            case 2:
                zzati();
                break;
            case 3:
            case 6:
            case 11:
                zza((zzrv) message.obj);
                break;
            case 4:
                zza(message.arg1, (ConnectionResult) message.obj);
                break;
            case 5:
                zzb((zzc) message.obj);
                break;
            case 7:
                if (this.zj.containsKey(message.obj)) {
                    ((zza) this.zj.get(message.obj)).resume();
                    break;
                }
                break;
            case 8:
                zzatj();
                break;
            case 9:
                if (this.zj.containsKey(message.obj)) {
                    ((zza) this.zj.get(message.obj)).zzasx();
                    break;
                }
                break;
            case 10:
                if (this.zj.containsKey(message.obj)) {
                    ((zza) this.zj.get(message.obj)).zzats();
                    break;
                }
                break;
            default:
                Log.w("GoogleApiManager", "Unknown message id: " + message.what);
                return false;
        }
        return true;
    }

    public <O extends ApiOptions> Task<Void> zza(@NonNull zzc<O> com_google_android_gms_common_api_zzc_O, @NonNull com.google.android.gms.internal.zzrr.zzb<?> com_google_android_gms_internal_zzrr_zzb_) {
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        this.mHandler.sendMessage(this.mHandler.obtainMessage(11, new zzrv(new zzqj.zze(com_google_android_gms_internal_zzrr_zzb_, taskCompletionSource), this.AM.get(), com_google_android_gms_common_api_zzc_O)));
        return taskCompletionSource.getTask();
    }

    public <O extends ApiOptions> Task<Void> zza(@NonNull zzc<O> com_google_android_gms_common_api_zzc_O, @NonNull zzrw<com.google.android.gms.common.api.Api.zzb> com_google_android_gms_internal_zzrw_com_google_android_gms_common_api_Api_zzb, @NonNull zzsh<com.google.android.gms.common.api.Api.zzb> com_google_android_gms_internal_zzsh_com_google_android_gms_common_api_Api_zzb) {
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        this.mHandler.sendMessage(this.mHandler.obtainMessage(6, new zzrv(new zzqj.zzc(new zzrx(com_google_android_gms_internal_zzrw_com_google_android_gms_common_api_Api_zzb, com_google_android_gms_internal_zzsh_com_google_android_gms_common_api_Api_zzb), taskCompletionSource), this.AM.get(), com_google_android_gms_common_api_zzc_O)));
        return taskCompletionSource.getTask();
    }

    public Task<Void> zza(Iterable<zzc<?>> iterable) {
        zzqn com_google_android_gms_internal_zzqn = new zzqn(iterable);
        for (zzc apiKey : iterable) {
            zza com_google_android_gms_internal_zzrh_zza = (zza) this.zj.get(apiKey.getApiKey());
            if (com_google_android_gms_internal_zzrh_zza != null) {
                if (!com_google_android_gms_internal_zzrh_zza.isConnected()) {
                }
            }
            this.mHandler.sendMessage(this.mHandler.obtainMessage(1, com_google_android_gms_internal_zzqn));
            return com_google_android_gms_internal_zzqn.getTask();
        }
        com_google_android_gms_internal_zzqn.zzarp();
        return com_google_android_gms_internal_zzqn.getTask();
    }

    public void zza(ConnectionResult connectionResult, int i) {
        if (!zzc(connectionResult, i)) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(4, i, 0, connectionResult));
        }
    }

    public void zza(zzc<?> com_google_android_gms_common_api_zzc_) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(5, com_google_android_gms_common_api_zzc_));
    }

    public <O extends ApiOptions> void zza(zzc<O> com_google_android_gms_common_api_zzc_O, int i, com.google.android.gms.internal.zzqo.zza<? extends Result, com.google.android.gms.common.api.Api.zzb> com_google_android_gms_internal_zzqo_zza__extends_com_google_android_gms_common_api_Result__com_google_android_gms_common_api_Api_zzb) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3, new zzrv(new com.google.android.gms.internal.zzqj.zzb(i, com_google_android_gms_internal_zzqo_zza__extends_com_google_android_gms_common_api_Result__com_google_android_gms_common_api_Api_zzb), this.AM.get(), com_google_android_gms_common_api_zzc_O)));
    }

    public <O extends ApiOptions, TResult> void zza(zzc<O> com_google_android_gms_common_api_zzc_O, int i, zzse<com.google.android.gms.common.api.Api.zzb, TResult> com_google_android_gms_internal_zzse_com_google_android_gms_common_api_Api_zzb__TResult, TaskCompletionSource<TResult> taskCompletionSource, zzsb com_google_android_gms_internal_zzsb) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3, new zzrv(new zzd(i, com_google_android_gms_internal_zzse_com_google_android_gms_common_api_Api_zzb__TResult, taskCompletionSource, com_google_android_gms_internal_zzsb), this.AM.get(), com_google_android_gms_common_api_zzc_O)));
    }

    @WorkerThread
    public void zza(zzqn com_google_android_gms_internal_zzqn) {
        for (zzql com_google_android_gms_internal_zzql : com_google_android_gms_internal_zzqn.zzaro()) {
            zza com_google_android_gms_internal_zzrh_zza = (zza) this.zj.get(com_google_android_gms_internal_zzql);
            if (com_google_android_gms_internal_zzrh_zza == null) {
                com_google_android_gms_internal_zzqn.zza(com_google_android_gms_internal_zzql, new ConnectionResult(13));
                return;
            } else if (com_google_android_gms_internal_zzrh_zza.isConnected()) {
                com_google_android_gms_internal_zzqn.zza(com_google_android_gms_internal_zzql, ConnectionResult.wO);
            } else if (com_google_android_gms_internal_zzrh_zza.zzatp() != null) {
                com_google_android_gms_internal_zzqn.zza(com_google_android_gms_internal_zzql, com_google_android_gms_internal_zzrh_zza.zzatp());
            } else {
                com_google_android_gms_internal_zzrh_zza.zzb(com_google_android_gms_internal_zzqn);
            }
        }
    }

    public void zza(@NonNull zzqw com_google_android_gms_internal_zzqw) {
        synchronized (zzaox) {
            if (this.AN != com_google_android_gms_internal_zzqw) {
                this.AN = com_google_android_gms_internal_zzqw;
                this.AO.clear();
                this.AO.addAll(com_google_android_gms_internal_zzqw.zzasl());
            }
        }
    }

    public void zzarm() {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(2));
    }

    public int zzath() {
        return this.AL.getAndIncrement();
    }

    @WorkerThread
    public void zzatj() {
        for (zzql remove : this.AP) {
            ((zza) this.zj.remove(remove)).signOut();
        }
        this.AP.clear();
    }

    void zzb(@NonNull zzqw com_google_android_gms_internal_zzqw) {
        synchronized (zzaox) {
            if (this.AN == com_google_android_gms_internal_zzqw) {
                this.AN = null;
                this.AO.clear();
            }
        }
    }

    boolean zzc(ConnectionResult connectionResult, int i) {
        if (!connectionResult.hasResolution() && !this.xP.isUserResolvableError(connectionResult.getErrorCode())) {
            return false;
        }
        this.xP.zza(this.mContext, connectionResult, i);
        return true;
    }
}
