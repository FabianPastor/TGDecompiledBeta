package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzai;
import com.google.android.gms.common.internal.zze.zzf;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.exoplayer.chunk.FormatEvaluator.AdaptiveEvaluator;
import org.telegram.messenger.exoplayer.hls.HlsChunkSource;

public class zzqt implements Callback {
    private static zzqt yu;
    private static final Object zzaok = new Object();
    private final Context mContext;
    private final Handler mHandler;
    private final GoogleApiAvailability vP;
    private long xS;
    private long xT;
    private final Set<zzpz<?>> yA;
    private final ReferenceQueue<com.google.android.gms.common.api.zzd<?>> yB;
    private final SparseArray<zza> yC;
    private zzb yD;
    private long yt;
    private int yv;
    private final AtomicInteger yw;
    private final SparseArray<zzc<?>> yx;
    private final Map<zzpz<?>, zzc<?>> yy;
    private zzqi yz;

    private final class zza extends PhantomReference<com.google.android.gms.common.api.zzd<?>> {
        private final int wf;
        final /* synthetic */ zzqt yE;

        public zza(zzqt com_google_android_gms_internal_zzqt, com.google.android.gms.common.api.zzd com_google_android_gms_common_api_zzd, int i, ReferenceQueue<com.google.android.gms.common.api.zzd<?>> referenceQueue) {
            this.yE = com_google_android_gms_internal_zzqt;
            super(com_google_android_gms_common_api_zzd, referenceQueue);
            this.wf = i;
        }

        public void zzasd() {
            this.yE.mHandler.sendMessage(this.yE.mHandler.obtainMessage(2, this.wf, 2));
        }
    }

    private static final class zzb extends Thread {
        private final ReferenceQueue<com.google.android.gms.common.api.zzd<?>> yB;
        private final SparseArray<zza> yC;
        private final AtomicBoolean yF = new AtomicBoolean();

        public zzb(ReferenceQueue<com.google.android.gms.common.api.zzd<?>> referenceQueue, SparseArray<zza> sparseArray) {
            super("GoogleApiCleanup");
            this.yB = referenceQueue;
            this.yC = sparseArray;
        }

        public void run() {
            this.yF.set(true);
            Process.setThreadPriority(10);
            while (this.yF.get()) {
                try {
                    zza com_google_android_gms_internal_zzqt_zza = (zza) this.yB.remove();
                    this.yC.remove(com_google_android_gms_internal_zzqt_zza.wf);
                    com_google_android_gms_internal_zzqt_zza.zzasd();
                } catch (InterruptedException e) {
                } finally {
                    this.yF.set(false);
                }
            }
        }
    }

    private class zzd implements zzf {
        private final zze vC;
        private final zzpz<?> vx;
        final /* synthetic */ zzqt yE;

        public zzd(zzqt com_google_android_gms_internal_zzqt, zze com_google_android_gms_common_api_Api_zze, zzpz<?> com_google_android_gms_internal_zzpz_) {
            this.yE = com_google_android_gms_internal_zzqt;
            this.vC = com_google_android_gms_common_api_Api_zze;
            this.vx = com_google_android_gms_internal_zzpz_;
        }

        @WorkerThread
        public void zzh(@NonNull ConnectionResult connectionResult) {
            if (connectionResult.isSuccess()) {
                this.vC.zza(null, Collections.emptySet());
            } else {
                ((zzc) this.yE.yy.get(this.vx)).onConnectionFailed(connectionResult);
            }
        }
    }

    private class zzc<O extends ApiOptions> implements ConnectionCallbacks, OnConnectionFailedListener, zzqg {
        private final zze vC;
        private final zzpz<O> vx;
        private final SparseArray<Map<com.google.android.gms.internal.zzrd.zzb<?>, zzri>> wg = new SparseArray();
        private boolean xR;
        final /* synthetic */ zzqt yE;
        private final Queue<zzpy> yG = new LinkedList();
        private final com.google.android.gms.common.api.Api.zzb yH;
        private final SparseArray<zzrq> yI = new SparseArray();
        private final Set<zzqb> yJ = new HashSet();
        private ConnectionResult yK = null;

        @WorkerThread
        public zzc(zzqt com_google_android_gms_internal_zzqt, com.google.android.gms.common.api.zzd<O> com_google_android_gms_common_api_zzd_O) {
            this.yE = com_google_android_gms_internal_zzqt;
            this.vC = com_google_android_gms_common_api_zzd_O.zza(com_google_android_gms_internal_zzqt.mHandler.getLooper(), this, this);
            if (this.vC instanceof zzai) {
                this.yH = ((zzai) this.vC).zzavk();
            } else {
                this.yH = this.vC;
            }
            this.vx = com_google_android_gms_common_api_zzd_O.zzapx();
        }

        @WorkerThread
        private void connect() {
            if (!this.vC.isConnected() && !this.vC.isConnecting()) {
                if (this.vC.zzapr() && this.yE.yv != 0) {
                    this.yE.yv = this.yE.vP.isGooglePlayServicesAvailable(this.yE.mContext);
                    if (this.yE.yv != 0) {
                        onConnectionFailed(new ConnectionResult(this.yE.yv, null));
                        return;
                    }
                }
                this.vC.zza(new zzd(this.yE, this.vC, this.vx));
            }
        }

        @WorkerThread
        private void resume() {
            if (this.xR) {
                connect();
            }
        }

        @WorkerThread
        private void zzab(Status status) {
            for (zzpy zzx : this.yG) {
                zzx.zzx(status);
            }
            this.yG.clear();
        }

        @WorkerThread
        private void zzarr() {
            if (this.xR) {
                zzash();
                zzab(this.yE.vP.isGooglePlayServicesAvailable(this.yE.mContext) == 18 ? new Status(8, "Connection timed out while waiting for Google Play services update to complete.") : new Status(8, "API failed to connect while resuming due to an unknown error."));
                this.vC.disconnect();
            }
        }

        @WorkerThread
        private void zzash() {
            if (this.xR) {
                this.yE.mHandler.removeMessages(10, this.vx);
                this.yE.mHandler.removeMessages(9, this.vx);
                this.xR = false;
            }
        }

        private void zzasi() {
            this.yE.mHandler.removeMessages(11, this.vx);
            this.yE.mHandler.sendMessageDelayed(this.yE.mHandler.obtainMessage(11, this.vx), this.yE.yt);
        }

        private void zzasj() {
            if (this.vC.isConnected() && this.wg.size() == 0) {
                for (int i = 0; i < this.yI.size(); i++) {
                    if (((zzrq) this.yI.get(this.yI.keyAt(i))).zzasx()) {
                        zzasi();
                        return;
                    }
                }
                this.vC.disconnect();
            }
        }

        @WorkerThread
        private void zzc(zzpy com_google_android_gms_internal_zzpy) {
            com_google_android_gms_internal_zzpy.zza(this.yI);
            try {
                com_google_android_gms_internal_zzpy.zzb(this.yH);
            } catch (DeadObjectException e) {
                this.vC.disconnect();
                onConnectionSuspended(1);
            }
        }

        @WorkerThread
        private void zzj(ConnectionResult connectionResult) {
            for (zzqb zza : this.yJ) {
                zza.zza(this.vx, connectionResult);
            }
            this.yJ.clear();
        }

        boolean isConnected() {
            return this.vC.isConnected();
        }

        @WorkerThread
        public void onConnected(@Nullable Bundle bundle) {
            zzasf();
            zzj(ConnectionResult.uJ);
            zzash();
            for (int i = 0; i < this.wg.size(); i++) {
                for (zzri com_google_android_gms_internal_zzri : ((Map) this.wg.get(this.wg.keyAt(i))).values()) {
                    try {
                        com_google_android_gms_internal_zzri.wj.zza(this.yH, new TaskCompletionSource());
                    } catch (DeadObjectException e) {
                        this.vC.disconnect();
                        onConnectionSuspended(1);
                    }
                }
            }
            zzase();
            zzasi();
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        @WorkerThread
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            zzasf();
            this.yE.yv = -1;
            zzj(connectionResult);
            int keyAt = this.yI.keyAt(0);
            if (this.yG.isEmpty()) {
                this.yK = connectionResult;
                return;
            }
            synchronized (zzqt.zzaok) {
                if (null != null && this.yE.yA.contains(this.vx)) {
                    null.zzb(connectionResult, keyAt);
                }
            }
        }

        @WorkerThread
        public void onConnectionSuspended(int i) {
            zzasf();
            this.xR = true;
            this.yE.mHandler.sendMessageDelayed(Message.obtain(this.yE.mHandler, 9, this.vx), this.yE.xT);
            this.yE.mHandler.sendMessageDelayed(Message.obtain(this.yE.mHandler, 10, this.vx), this.yE.xS);
            this.yE.yv = -1;
        }

        public void zza(ConnectionResult connectionResult, Api<?> api, int i) {
            onConnectionFailed(connectionResult);
        }

        @WorkerThread
        public void zzase() {
            while (this.vC.isConnected() && !this.yG.isEmpty()) {
                zzc((zzpy) this.yG.remove());
            }
        }

        @WorkerThread
        public void zzasf() {
            this.yK = null;
        }

        ConnectionResult zzasg() {
            return this.yK;
        }

        @WorkerThread
        public void zzb(int i, @NonNull com.google.android.gms.internal.zzrd.zzb<?> com_google_android_gms_internal_zzrd_zzb_, @NonNull TaskCompletionSource<Void> taskCompletionSource) {
            Map map = (Map) this.wg.get(i);
            if (map == null || map.get(com_google_android_gms_internal_zzrd_zzb_) == null) {
                taskCompletionSource.setException(new com.google.android.gms.common.api.zza(Status.wa));
                Log.wtf("GoogleApiManager", "Received call to unregister a listener without a matching registration call.", new Exception());
                return;
            }
            zzb(new zzpy.zze(i, ((zzri) map.get(com_google_android_gms_internal_zzrd_zzb_)).wk, taskCompletionSource, this.wg));
        }

        @WorkerThread
        public void zzb(int i, @NonNull zzri com_google_android_gms_internal_zzri, @NonNull TaskCompletionSource<Void> taskCompletionSource) {
            zzb(new com.google.android.gms.internal.zzpy.zzc(i, com_google_android_gms_internal_zzri, taskCompletionSource, this.wg));
        }

        @WorkerThread
        public void zzb(zzpy com_google_android_gms_internal_zzpy) {
            if (this.vC.isConnected()) {
                zzc(com_google_android_gms_internal_zzpy);
                zzasi();
                return;
            }
            this.yG.add(com_google_android_gms_internal_zzpy);
            if (this.yK == null || !this.yK.hasResolution()) {
                connect();
            } else {
                onConnectionFailed(this.yK);
            }
        }

        @WorkerThread
        public void zzb(zzqb com_google_android_gms_internal_zzqb) {
            this.yJ.add(com_google_android_gms_internal_zzqb);
        }

        @WorkerThread
        public void zzf(int i, boolean z) {
            Iterator it = this.yG.iterator();
            while (it.hasNext()) {
                zzpy com_google_android_gms_internal_zzpy = (zzpy) it.next();
                if (com_google_android_gms_internal_zzpy.wf == i && com_google_android_gms_internal_zzpy.lN != 1 && com_google_android_gms_internal_zzpy.cancel()) {
                    it.remove();
                }
            }
            ((zzrq) this.yI.get(i)).release();
            this.wg.delete(i);
            if (!z) {
                this.yI.remove(i);
                this.yE.yC.remove(i);
                if (this.yI.size() == 0 && this.yG.isEmpty()) {
                    zzash();
                    this.vC.disconnect();
                    this.yE.yy.remove(this.vx);
                    synchronized (zzqt.zzaok) {
                        this.yE.yA.remove(this.vx);
                    }
                }
            }
        }

        @WorkerThread
        public void zzfw(int i) {
            this.yI.put(i, new zzrq(this.vC));
        }

        @WorkerThread
        public void zzfx(final int i) {
            ((zzrq) this.yI.get(i)).zza(new zzc(this) {
                final /* synthetic */ zzc yM;

                public void zzask() {
                    if (this.yM.yG.isEmpty()) {
                        this.yM.zzf(i, false);
                    }
                }
            });
        }
    }

    private zzqt(Context context) {
        this(context, GoogleApiAvailability.getInstance());
    }

    private zzqt(Context context, GoogleApiAvailability googleApiAvailability) {
        this.xT = HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS;
        this.xS = 120000;
        this.yt = 10000;
        this.yv = -1;
        this.yw = new AtomicInteger(1);
        this.yx = new SparseArray();
        this.yy = new ConcurrentHashMap(5, AdaptiveEvaluator.DEFAULT_BANDWIDTH_FRACTION, 1);
        this.yz = null;
        this.yA = new com.google.android.gms.common.util.zza();
        this.yB = new ReferenceQueue();
        this.yC = new SparseArray();
        this.mContext = context;
        HandlerThread handlerThread = new HandlerThread("GoogleApiHandler", 9);
        handlerThread.start();
        this.mHandler = new Handler(handlerThread.getLooper(), this);
        this.vP = googleApiAvailability;
    }

    private int zza(com.google.android.gms.common.api.zzd<?> com_google_android_gms_common_api_zzd_) {
        int andIncrement = this.yw.getAndIncrement();
        this.mHandler.sendMessage(this.mHandler.obtainMessage(6, andIncrement, 0, com_google_android_gms_common_api_zzd_));
        return andIncrement;
    }

    public static Pair<zzqt, Integer> zza(Context context, com.google.android.gms.common.api.zzd<?> com_google_android_gms_common_api_zzd_) {
        Pair<zzqt, Integer> create;
        synchronized (zzaok) {
            if (yu == null) {
                yu = new zzqt(context.getApplicationContext());
            }
            create = Pair.create(yu, Integer.valueOf(yu.zza((com.google.android.gms.common.api.zzd) com_google_android_gms_common_api_zzd_)));
        }
        return create;
    }

    @WorkerThread
    private void zza(int i, com.google.android.gms.internal.zzrd.zzb<?> com_google_android_gms_internal_zzrd_zzb_, TaskCompletionSource<Void> taskCompletionSource) {
        ((zzc) this.yx.get(i)).zzb(i, (com.google.android.gms.internal.zzrd.zzb) com_google_android_gms_internal_zzrd_zzb_, (TaskCompletionSource) taskCompletionSource);
    }

    @WorkerThread
    private void zza(int i, zzri com_google_android_gms_internal_zzri, TaskCompletionSource<Void> taskCompletionSource) {
        ((zzc) this.yx.get(i)).zzb(i, com_google_android_gms_internal_zzri, (TaskCompletionSource) taskCompletionSource);
    }

    @WorkerThread
    private void zza(com.google.android.gms.common.api.zzd<?> com_google_android_gms_common_api_zzd_, int i) {
        zzpz zzapx = com_google_android_gms_common_api_zzd_.zzapx();
        if (!this.yy.containsKey(zzapx)) {
            this.yy.put(zzapx, new zzc(this, com_google_android_gms_common_api_zzd_));
        }
        zzc com_google_android_gms_internal_zzqt_zzc = (zzc) this.yy.get(zzapx);
        com_google_android_gms_internal_zzqt_zzc.zzfw(i);
        this.yx.put(i, com_google_android_gms_internal_zzqt_zzc);
        com_google_android_gms_internal_zzqt_zzc.connect();
        this.yC.put(i, new zza(this, com_google_android_gms_common_api_zzd_, i, this.yB));
        if (this.yD == null || !this.yD.yF.get()) {
            this.yD = new zzb(this.yB, this.yC);
            this.yD.start();
        }
    }

    @WorkerThread
    private void zza(zzpy com_google_android_gms_internal_zzpy) {
        ((zzc) this.yx.get(com_google_android_gms_internal_zzpy.wf)).zzb(com_google_android_gms_internal_zzpy);
    }

    public static zzqt zzasa() {
        zzqt com_google_android_gms_internal_zzqt;
        synchronized (zzaok) {
            com_google_android_gms_internal_zzqt = yu;
        }
        return com_google_android_gms_internal_zzqt;
    }

    @WorkerThread
    private void zzasb() {
        for (zzc com_google_android_gms_internal_zzqt_zzc : this.yy.values()) {
            com_google_android_gms_internal_zzqt_zzc.zzasf();
            com_google_android_gms_internal_zzqt_zzc.connect();
        }
    }

    @WorkerThread
    private void zze(int i, boolean z) {
        zzc com_google_android_gms_internal_zzqt_zzc = (zzc) this.yx.get(i);
        if (com_google_android_gms_internal_zzqt_zzc != null) {
            if (!z) {
                this.yx.delete(i);
            }
            com_google_android_gms_internal_zzqt_zzc.zzf(i, z);
            return;
        }
        Log.wtf("GoogleApiManager", "onRelease received for unknown instance: " + i, new Exception());
    }

    @WorkerThread
    private void zzfv(int i) {
        zzc com_google_android_gms_internal_zzqt_zzc = (zzc) this.yx.get(i);
        if (com_google_android_gms_internal_zzqt_zzc != null) {
            this.yx.delete(i);
            com_google_android_gms_internal_zzqt_zzc.zzfx(i);
            return;
        }
        Log.wtf("GoogleApiManager", "onCleanupLeakInternal received for unknown instance: " + i, new Exception());
    }

    @WorkerThread
    public boolean handleMessage(Message message) {
        boolean z = false;
        Pair pair;
        switch (message.what) {
            case 1:
                zza((zzqb) message.obj);
                break;
            case 2:
                zzfv(message.arg1);
                break;
            case 3:
                zzasb();
                break;
            case 4:
                zza((zzpy) message.obj);
                break;
            case 5:
                if (this.yx.get(message.arg1) != null) {
                    ((zzc) this.yx.get(message.arg1)).zzab(new Status(17, "Error resolution was canceled by the user."));
                    break;
                }
                break;
            case 6:
                zza((com.google.android.gms.common.api.zzd) message.obj, message.arg1);
                break;
            case 7:
                pair = (Pair) message.obj;
                zza(message.arg1, (zzri) pair.first, (TaskCompletionSource) pair.second);
                break;
            case 8:
                int i = message.arg1;
                if (message.arg2 == 1) {
                    z = true;
                }
                zze(i, z);
                break;
            case 9:
                if (this.yy.containsKey(message.obj)) {
                    ((zzc) this.yy.get(message.obj)).resume();
                    break;
                }
                break;
            case 10:
                if (this.yy.containsKey(message.obj)) {
                    ((zzc) this.yy.get(message.obj)).zzarr();
                    break;
                }
                break;
            case 11:
                if (this.yy.containsKey(message.obj)) {
                    ((zzc) this.yy.get(message.obj)).zzasj();
                    break;
                }
                break;
            case 12:
                pair = (Pair) message.obj;
                zza(message.arg1, (com.google.android.gms.internal.zzrd.zzb) pair.first, (TaskCompletionSource) pair.second);
                break;
            default:
                Log.w("GoogleApiManager", "Unknown message id: " + message.what);
                return false;
        }
        return true;
    }

    public void zza(ConnectionResult connectionResult, int i) {
        if (!zzc(connectionResult, i)) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(5, i, 0));
        }
    }

    public <O extends ApiOptions> void zza(com.google.android.gms.common.api.zzd<O> com_google_android_gms_common_api_zzd_O, int i, com.google.android.gms.internal.zzqc.zza<? extends Result, com.google.android.gms.common.api.Api.zzb> com_google_android_gms_internal_zzqc_zza__extends_com_google_android_gms_common_api_Result__com_google_android_gms_common_api_Api_zzb) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(4, new com.google.android.gms.internal.zzpy.zzb(com_google_android_gms_common_api_zzd_O.getInstanceId(), i, com_google_android_gms_internal_zzqc_zza__extends_com_google_android_gms_common_api_Result__com_google_android_gms_common_api_Api_zzb)));
    }

    public <O extends ApiOptions, TResult> void zza(com.google.android.gms.common.api.zzd<O> com_google_android_gms_common_api_zzd_O, int i, zzro<com.google.android.gms.common.api.Api.zzb, TResult> com_google_android_gms_internal_zzro_com_google_android_gms_common_api_Api_zzb__TResult, TaskCompletionSource<TResult> taskCompletionSource) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(4, new com.google.android.gms.internal.zzpy.zzd(com_google_android_gms_common_api_zzd_O.getInstanceId(), i, com_google_android_gms_internal_zzro_com_google_android_gms_common_api_Api_zzb__TResult, taskCompletionSource)));
    }

    @WorkerThread
    public void zza(zzqb com_google_android_gms_internal_zzqb) {
        for (zzpz com_google_android_gms_internal_zzpz : com_google_android_gms_internal_zzqb.zzaqm()) {
            zzc com_google_android_gms_internal_zzqt_zzc = (zzc) this.yy.get(com_google_android_gms_internal_zzpz);
            if (com_google_android_gms_internal_zzqt_zzc == null) {
                com_google_android_gms_internal_zzqb.cancel();
                return;
            } else if (com_google_android_gms_internal_zzqt_zzc.isConnected()) {
                com_google_android_gms_internal_zzqb.zza(com_google_android_gms_internal_zzpz, ConnectionResult.uJ);
            } else if (com_google_android_gms_internal_zzqt_zzc.zzasg() != null) {
                com_google_android_gms_internal_zzqb.zza(com_google_android_gms_internal_zzpz, com_google_android_gms_internal_zzqt_zzc.zzasg());
            } else {
                com_google_android_gms_internal_zzqt_zzc.zzb(com_google_android_gms_internal_zzqb);
            }
        }
    }

    public void zza(zzqi com_google_android_gms_internal_zzqi) {
        synchronized (zzaok) {
            if (com_google_android_gms_internal_zzqi == null) {
                this.yz = null;
                this.yA.clear();
            }
        }
    }

    public void zzaqk() {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3));
    }

    boolean zzc(ConnectionResult connectionResult, int i) {
        if (!connectionResult.hasResolution() && !this.vP.isUserResolvableError(connectionResult.getErrorCode())) {
            return false;
        }
        this.vP.zza(this.mContext, connectionResult, i);
        return true;
    }

    public void zzd(int i, boolean z) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(8, i, z ? 1 : 2));
    }
}
