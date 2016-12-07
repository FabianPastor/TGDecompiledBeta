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
import com.google.android.gms.common.internal.zzh;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class zzqr implements zzqg, zzqy {
    private final Context mContext;
    final com.google.android.gms.common.api.Api.zza<? extends zzwz, zzxa> vQ;
    final zzqp wV;
    final zzh xB;
    final Map<Api<?>, Integer> xC;
    final Map<zzc<?>, zze> xW;
    private final Lock xf;
    private final com.google.android.gms.common.zzc xn;
    private final Condition yj;
    private final zzb yk;
    final Map<zzc<?>, ConnectionResult> yl = new HashMap();
    private volatile zzqq ym;
    private ConnectionResult yn = null;
    int yo;
    final com.google.android.gms.internal.zzqy.zza yp;

    static abstract class zza {
        private final zzqq yq;

        protected zza(zzqq com_google_android_gms_internal_zzqq) {
            this.yq = com_google_android_gms_internal_zzqq;
        }

        protected abstract void zzari();

        public final void zzc(zzqr com_google_android_gms_internal_zzqr) {
            com_google_android_gms_internal_zzqr.xf.lock();
            try {
                if (com_google_android_gms_internal_zzqr.ym == this.yq) {
                    zzari();
                    com_google_android_gms_internal_zzqr.xf.unlock();
                }
            } finally {
                com_google_android_gms_internal_zzqr.xf.unlock();
            }
        }
    }

    final class zzb extends Handler {
        final /* synthetic */ zzqr yr;

        zzb(zzqr com_google_android_gms_internal_zzqr, Looper looper) {
            this.yr = com_google_android_gms_internal_zzqr;
            super(looper);
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    ((zza) message.obj).zzc(this.yr);
                    return;
                case 2:
                    throw ((RuntimeException) message.obj);
                default:
                    Log.w("GACStateManager", "Unknown message id: " + message.what);
                    return;
            }
        }
    }

    public zzqr(Context context, zzqp com_google_android_gms_internal_zzqp, Lock lock, Looper looper, com.google.android.gms.common.zzc com_google_android_gms_common_zzc, Map<zzc<?>, zze> map, zzh com_google_android_gms_common_internal_zzh, Map<Api<?>, Integer> map2, com.google.android.gms.common.api.Api.zza<? extends zzwz, zzxa> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzwz__com_google_android_gms_internal_zzxa, ArrayList<zzqf> arrayList, com.google.android.gms.internal.zzqy.zza com_google_android_gms_internal_zzqy_zza) {
        this.mContext = context;
        this.xf = lock;
        this.xn = com_google_android_gms_common_zzc;
        this.xW = map;
        this.xB = com_google_android_gms_common_internal_zzh;
        this.xC = map2;
        this.vQ = com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzwz__com_google_android_gms_internal_zzxa;
        this.wV = com_google_android_gms_internal_zzqp;
        this.yp = com_google_android_gms_internal_zzqy_zza;
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ((zzqf) it.next()).zza(this);
        }
        this.yk = new zzb(this, looper);
        this.yj = lock.newCondition();
        this.ym = new zzqo(this);
    }

    public ConnectionResult blockingConnect() {
        connect();
        while (isConnecting()) {
            try {
                this.yj.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return new ConnectionResult(15, null);
            }
        }
        return isConnected() ? ConnectionResult.uJ : this.yn != null ? this.yn : new ConnectionResult(13, null);
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
            toNanos = this.yj.awaitNanos(toNanos);
        }
        return isConnected() ? ConnectionResult.uJ : this.yn != null ? this.yn : new ConnectionResult(13, null);
    }

    public void connect() {
        this.ym.connect();
    }

    public void disconnect() {
        if (this.ym.disconnect()) {
            this.yl.clear();
        }
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String concat = String.valueOf(str).concat("  ");
        printWriter.append(str).append("mState=").println(this.ym);
        for (Api api : this.xC.keySet()) {
            printWriter.append(str).append(api.getName()).println(":");
            ((zze) this.xW.get(api.zzapp())).dump(concat, fileDescriptor, printWriter, strArr);
        }
    }

    @Nullable
    public ConnectionResult getConnectionResult(@NonNull Api<?> api) {
        zzc zzapp = api.zzapp();
        if (this.xW.containsKey(zzapp)) {
            if (((zze) this.xW.get(zzapp)).isConnected()) {
                return ConnectionResult.uJ;
            }
            if (this.yl.containsKey(zzapp)) {
                return (ConnectionResult) this.yl.get(zzapp);
            }
        }
        return null;
    }

    public boolean isConnected() {
        return this.ym instanceof zzqm;
    }

    public boolean isConnecting() {
        return this.ym instanceof zzqn;
    }

    public void onConnected(@Nullable Bundle bundle) {
        this.xf.lock();
        try {
            this.ym.onConnected(bundle);
        } finally {
            this.xf.unlock();
        }
    }

    public void onConnectionSuspended(int i) {
        this.xf.lock();
        try {
            this.ym.onConnectionSuspended(i);
        } finally {
            this.xf.unlock();
        }
    }

    public void zza(@NonNull ConnectionResult connectionResult, @NonNull Api<?> api, int i) {
        this.xf.lock();
        try {
            this.ym.zza(connectionResult, api, i);
        } finally {
            this.xf.unlock();
        }
    }

    void zza(zza com_google_android_gms_internal_zzqr_zza) {
        this.yk.sendMessage(this.yk.obtainMessage(1, com_google_android_gms_internal_zzqr_zza));
    }

    void zza(RuntimeException runtimeException) {
        this.yk.sendMessage(this.yk.obtainMessage(2, runtimeException));
    }

    public boolean zza(zzrl com_google_android_gms_internal_zzrl) {
        return false;
    }

    public void zzaqb() {
    }

    public void zzaqy() {
        if (isConnected()) {
            ((zzqm) this.ym).zzarh();
        }
    }

    void zzarw() {
        this.xf.lock();
        try {
            this.ym = new zzqn(this, this.xB, this.xC, this.xn, this.vQ, this.xf, this.mContext);
            this.ym.begin();
            this.yj.signalAll();
        } finally {
            this.xf.unlock();
        }
    }

    void zzarx() {
        this.xf.lock();
        try {
            this.wV.zzart();
            this.ym = new zzqm(this);
            this.ym.begin();
            this.yj.signalAll();
        } finally {
            this.xf.unlock();
        }
    }

    void zzary() {
        for (zze disconnect : this.xW.values()) {
            disconnect.disconnect();
        }
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, R extends Result, T extends com.google.android.gms.internal.zzqc.zza<R, A>> T zzc(@NonNull T t) {
        t.zzaqt();
        return this.ym.zzc(t);
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, T extends com.google.android.gms.internal.zzqc.zza<? extends Result, A>> T zzd(@NonNull T t) {
        t.zzaqt();
        return this.ym.zzd(t);
    }

    void zzi(ConnectionResult connectionResult) {
        this.xf.lock();
        try {
            this.yn = connectionResult;
            this.ym = new zzqo(this);
            this.ym.begin();
            this.yj.signalAll();
        } finally {
            this.xf.unlock();
        }
    }
}
