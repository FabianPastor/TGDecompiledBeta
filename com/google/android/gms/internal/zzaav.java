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
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.zze;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class zzaav implements zzaah, zzabc {
    private final Context mContext;
    private final Lock zzaAG;
    final zzg zzaAL;
    final Map<Api<?>, Boolean> zzaAO;
    private final zze zzaAQ;
    final zzaat zzaAw;
    final Map<zzc<?>, Api.zze> zzaBQ;
    private final Condition zzaCd;
    private final zzb zzaCe;
    final Map<zzc<?>, ConnectionResult> zzaCf = new HashMap();
    private volatile zzaau zzaCg;
    private ConnectionResult zzaCh = null;
    int zzaCi;
    final com.google.android.gms.internal.zzabc.zza zzaCj;
    final com.google.android.gms.common.api.Api.zza<? extends zzbai, zzbaj> zzazo;

    static abstract class zza {
        private final zzaau zzaCk;

        protected zza(zzaau com_google_android_gms_internal_zzaau) {
            this.zzaCk = com_google_android_gms_internal_zzaau;
        }

        public final void zzc(zzaav com_google_android_gms_internal_zzaav) {
            com_google_android_gms_internal_zzaav.zzaAG.lock();
            try {
                if (com_google_android_gms_internal_zzaav.zzaCg == this.zzaCk) {
                    zzwe();
                    com_google_android_gms_internal_zzaav.zzaAG.unlock();
                }
            } finally {
                com_google_android_gms_internal_zzaav.zzaAG.unlock();
            }
        }

        protected abstract void zzwe();
    }

    final class zzb extends Handler {
        final /* synthetic */ zzaav zzaCl;

        zzb(zzaav com_google_android_gms_internal_zzaav, Looper looper) {
            this.zzaCl = com_google_android_gms_internal_zzaav;
            super(looper);
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    ((zza) message.obj).zzc(this.zzaCl);
                    return;
                case 2:
                    throw ((RuntimeException) message.obj);
                default:
                    Log.w("GACStateManager", "Unknown message id: " + message.what);
                    return;
            }
        }
    }

    public zzaav(Context context, zzaat com_google_android_gms_internal_zzaat, Lock lock, Looper looper, zze com_google_android_gms_common_zze, Map<zzc<?>, Api.zze> map, zzg com_google_android_gms_common_internal_zzg, Map<Api<?>, Boolean> map2, com.google.android.gms.common.api.Api.zza<? extends zzbai, zzbaj> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzbai__com_google_android_gms_internal_zzbaj, ArrayList<zzaag> arrayList, com.google.android.gms.internal.zzabc.zza com_google_android_gms_internal_zzabc_zza) {
        this.mContext = context;
        this.zzaAG = lock;
        this.zzaAQ = com_google_android_gms_common_zze;
        this.zzaBQ = map;
        this.zzaAL = com_google_android_gms_common_internal_zzg;
        this.zzaAO = map2;
        this.zzazo = com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzbai__com_google_android_gms_internal_zzbaj;
        this.zzaAw = com_google_android_gms_internal_zzaat;
        this.zzaCj = com_google_android_gms_internal_zzabc_zza;
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ((zzaag) it.next()).zza(this);
        }
        this.zzaCe = new zzb(this, looper);
        this.zzaCd = lock.newCondition();
        this.zzaCg = new zzaas(this);
    }

    public ConnectionResult blockingConnect() {
        connect();
        while (isConnecting()) {
            try {
                this.zzaCd.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return new ConnectionResult(15, null);
            }
        }
        return isConnected() ? ConnectionResult.zzayj : this.zzaCh != null ? this.zzaCh : new ConnectionResult(13, null);
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
            toNanos = this.zzaCd.awaitNanos(toNanos);
        }
        return isConnected() ? ConnectionResult.zzayj : this.zzaCh != null ? this.zzaCh : new ConnectionResult(13, null);
    }

    public void connect() {
        this.zzaCg.connect();
    }

    public void disconnect() {
        if (this.zzaCg.disconnect()) {
            this.zzaCf.clear();
        }
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String concat = String.valueOf(str).concat("  ");
        printWriter.append(str).append("mState=").println(this.zzaCg);
        for (Api api : this.zzaAO.keySet()) {
            printWriter.append(str).append(api.getName()).println(":");
            ((Api.zze) this.zzaBQ.get(api.zzvg())).dump(concat, fileDescriptor, printWriter, strArr);
        }
    }

    @Nullable
    public ConnectionResult getConnectionResult(@NonNull Api<?> api) {
        zzc zzvg = api.zzvg();
        if (this.zzaBQ.containsKey(zzvg)) {
            if (((Api.zze) this.zzaBQ.get(zzvg)).isConnected()) {
                return ConnectionResult.zzayj;
            }
            if (this.zzaCf.containsKey(zzvg)) {
                return (ConnectionResult) this.zzaCf.get(zzvg);
            }
        }
        return null;
    }

    public boolean isConnected() {
        return this.zzaCg instanceof zzaaq;
    }

    public boolean isConnecting() {
        return this.zzaCg instanceof zzaar;
    }

    public void onConnected(@Nullable Bundle bundle) {
        this.zzaAG.lock();
        try {
            this.zzaCg.onConnected(bundle);
        } finally {
            this.zzaAG.unlock();
        }
    }

    public void onConnectionSuspended(int i) {
        this.zzaAG.lock();
        try {
            this.zzaCg.onConnectionSuspended(i);
        } finally {
            this.zzaAG.unlock();
        }
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, R extends Result, T extends com.google.android.gms.internal.zzaad.zza<R, A>> T zza(@NonNull T t) {
        t.zzvI();
        return this.zzaCg.zza(t);
    }

    public void zza(@NonNull ConnectionResult connectionResult, @NonNull Api<?> api, boolean z) {
        this.zzaAG.lock();
        try {
            this.zzaCg.zza(connectionResult, api, z);
        } finally {
            this.zzaAG.unlock();
        }
    }

    void zza(zza com_google_android_gms_internal_zzaav_zza) {
        this.zzaCe.sendMessage(this.zzaCe.obtainMessage(1, com_google_android_gms_internal_zzaav_zza));
    }

    void zza(RuntimeException runtimeException) {
        this.zzaCe.sendMessage(this.zzaCe.obtainMessage(2, runtimeException));
    }

    public boolean zza(zzabq com_google_android_gms_internal_zzabq) {
        return false;
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, T extends com.google.android.gms.internal.zzaad.zza<? extends Result, A>> T zzb(@NonNull T t) {
        t.zzvI();
        return this.zzaCg.zzb(t);
    }

    void zzh(ConnectionResult connectionResult) {
        this.zzaAG.lock();
        try {
            this.zzaCh = connectionResult;
            this.zzaCg = new zzaas(this);
            this.zzaCg.begin();
            this.zzaCd.signalAll();
        } finally {
            this.zzaAG.unlock();
        }
    }

    public void zzvM() {
        if (isConnected()) {
            ((zzaaq) this.zzaCg).zzwd();
        }
    }

    public void zzvn() {
    }

    void zzws() {
        this.zzaAG.lock();
        try {
            this.zzaCg = new zzaar(this, this.zzaAL, this.zzaAO, this.zzaAQ, this.zzazo, this.zzaAG, this.mContext);
            this.zzaCg.begin();
            this.zzaCd.signalAll();
        } finally {
            this.zzaAG.unlock();
        }
    }

    void zzwt() {
        this.zzaAG.lock();
        try {
            this.zzaAw.zzwp();
            this.zzaCg = new zzaaq(this);
            this.zzaCg.begin();
            this.zzaCd.signalAll();
        } finally {
            this.zzaAG.unlock();
        }
    }

    void zzwu() {
        for (Api.zze disconnect : this.zzaBQ.values()) {
            disconnect.disconnect();
        }
    }
}
