package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.internal.zzf;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class zzrf implements zzqs, zzrm {
    private ConnectionResult AA = null;
    int AB;
    final com.google.android.gms.internal.zzrm.zza AC;
    final Map<zzc<?>, zze> Aj;
    private final Condition Aw;
    private final zzb Ax;
    final Map<zzc<?>, ConnectionResult> Ay = new HashMap();
    private volatile zzre Az;
    private final Context mContext;
    final com.google.android.gms.common.api.Api.zza<? extends zzxp, zzxq> xQ;
    final zzrd yW;
    final zzf zP;
    private final Lock zg;
    final Map<Api<?>, Integer> zk;
    private final com.google.android.gms.common.zzc zm;

    static abstract class zza {
        private final zzre AD;

        protected zza(zzre com_google_android_gms_internal_zzre) {
            this.AD = com_google_android_gms_internal_zzre;
        }

        protected abstract void zzaso();

        public final void zzc(zzrf com_google_android_gms_internal_zzrf) {
            com_google_android_gms_internal_zzrf.zg.lock();
            try {
                if (com_google_android_gms_internal_zzrf.Az == this.AD) {
                    zzaso();
                    com_google_android_gms_internal_zzrf.zg.unlock();
                }
            } finally {
                com_google_android_gms_internal_zzrf.zg.unlock();
            }
        }
    }

    final class zzb extends Handler {
        final /* synthetic */ zzrf AE;

        zzb(zzrf com_google_android_gms_internal_zzrf, Looper looper) {
            this.AE = com_google_android_gms_internal_zzrf;
            super(looper);
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    ((zza) message.obj).zzc(this.AE);
                    return;
                case 2:
                    throw ((RuntimeException) message.obj);
                default:
                    Log.w("GACStateManager", "Unknown message id: " + message.what);
                    return;
            }
        }
    }

    public zzrf(Context context, zzrd com_google_android_gms_internal_zzrd, Lock lock, Looper looper, com.google.android.gms.common.zzc com_google_android_gms_common_zzc, Map<zzc<?>, zze> map, zzf com_google_android_gms_common_internal_zzf, Map<Api<?>, Integer> map2, com.google.android.gms.common.api.Api.zza<? extends zzxp, zzxq> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzxp__com_google_android_gms_internal_zzxq, ArrayList<zzqr> arrayList, com.google.android.gms.internal.zzrm.zza com_google_android_gms_internal_zzrm_zza) {
        this.mContext = context;
        this.zg = lock;
        this.zm = com_google_android_gms_common_zzc;
        this.Aj = map;
        this.zP = com_google_android_gms_common_internal_zzf;
        this.zk = map2;
        this.xQ = com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzxp__com_google_android_gms_internal_zzxq;
        this.yW = com_google_android_gms_internal_zzrd;
        this.AC = com_google_android_gms_internal_zzrm_zza;
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ((zzqr) it.next()).zza(this);
        }
        this.Ax = new zzb(this, looper);
        this.Aw = lock.newCondition();
        this.Az = new zzrc(this);
    }

    public ConnectionResult blockingConnect() {
        connect();
        while (isConnecting()) {
            try {
                this.Aw.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return new ConnectionResult(15, null);
            }
        }
        return isConnected() ? ConnectionResult.wO : this.AA != null ? this.AA : new ConnectionResult(13, null);
    }

    public ConnectionResult blockingConnect(long j, TimeUnit timeUnit) {
        connect();
        long toNanos = timeUnit.toNanos(j);
        while (isConnecting()) {
            if (toNanos <= 0) {
                try {
                    disconnect();
                    return new ConnectionResult(14, null);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return new ConnectionResult(15, null);
                }
            }
            toNanos = this.Aw.awaitNanos(toNanos);
        }
        return isConnected() ? ConnectionResult.wO : this.AA != null ? this.AA : new ConnectionResult(13, null);
    }

    public void connect() {
        this.Az.connect();
    }

    public void disconnect() {
        if (this.Az.disconnect()) {
            this.Ay.clear();
        }
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String concat = String.valueOf(str).concat("  ");
        printWriter.append(str).append("mState=").println(this.Az);
        for (Api api : this.zk.keySet()) {
            printWriter.append(str).append(api.getName()).println(":");
            ((zze) this.Aj.get(api.zzaqv())).dump(concat, fileDescriptor, printWriter, strArr);
        }
    }

    @Nullable
    public ConnectionResult getConnectionResult(@NonNull Api<?> api) {
        zzc zzaqv = api.zzaqv();
        if (this.Aj.containsKey(zzaqv)) {
            if (((zze) this.Aj.get(zzaqv)).isConnected()) {
                return ConnectionResult.wO;
            }
            if (this.Ay.containsKey(zzaqv)) {
                return (ConnectionResult) this.Ay.get(zzaqv);
            }
        }
        return null;
    }

    public boolean isConnected() {
        return this.Az instanceof zzra;
    }

    public boolean isConnecting() {
        return this.Az instanceof zzrb;
    }

    public void onConnected(@Nullable Bundle bundle) {
        this.zg.lock();
        try {
            this.Az.onConnected(bundle);
        } finally {
            this.zg.unlock();
        }
    }

    public void onConnectionSuspended(int i) {
        this.zg.lock();
        try {
            this.Az.onConnectionSuspended(i);
        } finally {
            this.zg.unlock();
        }
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, R extends Result, T extends com.google.android.gms.internal.zzqo.zza<R, A>> T zza(@NonNull T t) {
        t.zzarv();
        return this.Az.zza(t);
    }

    public void zza(@NonNull ConnectionResult connectionResult, @NonNull Api<?> api, int i) {
        this.zg.lock();
        try {
            this.Az.zza(connectionResult, api, i);
        } finally {
            this.zg.unlock();
        }
    }

    void zza(zza com_google_android_gms_internal_zzrf_zza) {
        this.Ax.sendMessage(this.Ax.obtainMessage(1, com_google_android_gms_internal_zzrf_zza));
    }

    void zza(RuntimeException runtimeException) {
        this.Ax.sendMessage(this.Ax.obtainMessage(2, runtimeException));
    }

    public boolean zza(zzsa com_google_android_gms_internal_zzsa) {
        return false;
    }

    public void zzard() {
    }

    public void zzarz() {
        if (isConnected()) {
            ((zzra) this.Az).zzasn();
        }
    }

    void zzatc() {
        this.zg.lock();
        try {
            this.Az = new zzrb(this, this.zP, this.zk, this.zm, this.xQ, this.zg, this.mContext);
            this.Az.begin();
            this.Aw.signalAll();
        } finally {
            this.zg.unlock();
        }
    }

    void zzatd() {
        this.zg.lock();
        try {
            this.yW.zzasz();
            this.Az = new zzra(this);
            this.Az.begin();
            this.Aw.signalAll();
        } finally {
            this.zg.unlock();
        }
    }

    void zzate() {
        for (zze disconnect : this.Aj.values()) {
            disconnect.disconnect();
        }
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, T extends com.google.android.gms.internal.zzqo.zza<? extends Result, A>> T zzb(@NonNull T t) {
        t.zzarv();
        return this.Az.zzb(t);
    }

    void zzh(ConnectionResult connectionResult) {
        this.zg.lock();
        try {
            this.AA = connectionResult;
            this.Az = new zzrc(this);
            this.Az.begin();
            this.Aw.signalAll();
        } finally {
            this.zg.unlock();
        }
    }
}
