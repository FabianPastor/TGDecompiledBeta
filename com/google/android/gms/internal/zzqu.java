package com.google.android.gms.internal;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.zzb;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class zzqu implements zzrm {
    private final zzrh xy;
    private final Lock zg;
    private final Map<zzc<?>, com.google.android.gms.common.api.zzc<?>> zj = new HashMap();
    private final Map<Api<?>, Integer> zk;
    private final zzrd zl;
    private final com.google.android.gms.common.zzc zm;
    private final Condition zn;
    private boolean zo;
    private Map<zzql<?>, ConnectionResult> zp;
    private ConnectionResult zq;
    private final Looper zzajy;

    private class zza implements OnFailureListener, OnSuccessListener<Void> {
        final /* synthetic */ zzqu zr;

        private zza(zzqu com_google_android_gms_internal_zzqu) {
            this.zr = com_google_android_gms_internal_zzqu;
        }

        @Nullable
        private ConnectionResult zzash() {
            ConnectionResult connectionResult = null;
            int i = 0;
            for (Api api : this.zr.zk.keySet()) {
                ConnectionResult connectionResult2 = (ConnectionResult) this.zr.zp.get(((com.google.android.gms.common.api.zzc) this.zr.zj.get(api.zzaqv())).getApiKey());
                if (!connectionResult2.isSuccess()) {
                    int intValue = ((Integer) this.zr.zk.get(api)).intValue();
                    if (intValue != 2 && (intValue != 1 || connectionResult2.hasResolution() || this.zr.zm.isUserResolvableError(connectionResult2.getErrorCode()))) {
                        int priority = api.zzaqs().getPriority();
                        if (connectionResult != null && i <= priority) {
                            priority = i;
                            connectionResult2 = connectionResult;
                        }
                        i = priority;
                        connectionResult = connectionResult2;
                    }
                }
            }
            return connectionResult;
        }

        public void onFailure(@NonNull Exception exception) {
            zzb com_google_android_gms_common_api_zzb = (zzb) exception;
            this.zr.zg.lock();
            try {
                this.zr.zp = com_google_android_gms_common_api_zzb.zzara();
                this.zr.zq = zzash();
                if (this.zr.zq == null) {
                    this.zr.zl.zzn(null);
                } else {
                    this.zr.zo = false;
                    this.zr.zl.zzc(this.zr.zq);
                }
                this.zr.zn.signalAll();
            } finally {
                this.zr.zg.unlock();
            }
        }

        public /* synthetic */ void onSuccess(Object obj) {
            zza((Void) obj);
        }

        public void zza(Void voidR) {
            this.zr.zg.lock();
            try {
                this.zr.zp = new ArrayMap(this.zr.zj.size());
                for (zzc com_google_android_gms_common_api_Api_zzc : this.zr.zj.keySet()) {
                    this.zr.zp.put(((com.google.android.gms.common.api.zzc) this.zr.zj.get(com_google_android_gms_common_api_Api_zzc)).getApiKey(), ConnectionResult.wO);
                }
                this.zr.zl.zzn(null);
                this.zr.zn.signalAll();
            } finally {
                this.zr.zg.unlock();
            }
        }
    }

    public zzqu(Context context, Lock lock, Looper looper, com.google.android.gms.common.zzc com_google_android_gms_common_zzc, Map<zzc<?>, zze> map, Map<Api<?>, Integer> map2, ArrayList<zzqr> arrayList, zzrd com_google_android_gms_internal_zzrd) {
        this.zg = lock;
        this.zzajy = looper;
        this.zn = lock.newCondition();
        this.zm = com_google_android_gms_common_zzc;
        this.zl = com_google_android_gms_internal_zzrd;
        this.zk = map2;
        Map hashMap = new HashMap();
        for (Api api : map2.keySet()) {
            hashMap.put(api.zzaqv(), api);
        }
        Map hashMap2 = new HashMap();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            zzqr com_google_android_gms_internal_zzqr = (zzqr) it.next();
            hashMap2.put(com_google_android_gms_internal_zzqr.vS, com_google_android_gms_internal_zzqr);
        }
        for (Entry entry : map.entrySet()) {
            Api api2 = (Api) hashMap.get(entry.getKey());
            this.zj.put((zzc) entry.getKey(), new com.google.android.gms.common.api.zzc(this, context, api2, looper, (zze) entry.getValue(), (zzqr) hashMap2.get(api2)) {
                final /* synthetic */ zzqu zr;
            });
        }
        this.xy = zzrh.zzatg();
    }

    public ConnectionResult blockingConnect() {
        connect();
        while (isConnecting()) {
            try {
                this.zn.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return new ConnectionResult(15, null);
            }
        }
        return isConnected() ? ConnectionResult.wO : this.zq != null ? this.zq : new ConnectionResult(13, null);
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
            toNanos = this.zn.awaitNanos(toNanos);
        }
        return isConnected() ? ConnectionResult.wO : this.zq != null ? this.zq : new ConnectionResult(13, null);
    }

    public void connect() {
        this.zg.lock();
        try {
            if (!this.zo) {
                this.zo = true;
                this.zp = null;
                this.zq = null;
                OnFailureListener com_google_android_gms_internal_zzqu_zza = new zza();
                Executor com_google_android_gms_internal_zzsv = new zzsv(this.zzajy);
                this.xy.zza(this.zj.values()).addOnSuccessListener(com_google_android_gms_internal_zzsv, (OnSuccessListener) com_google_android_gms_internal_zzqu_zza).addOnFailureListener(com_google_android_gms_internal_zzsv, com_google_android_gms_internal_zzqu_zza);
                this.zg.unlock();
            }
        } finally {
            this.zg.unlock();
        }
    }

    public void disconnect() {
        this.zg.lock();
        try {
            this.zo = false;
            this.zp = null;
            this.zq = null;
            this.zn.signalAll();
        } finally {
            this.zg.unlock();
        }
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }

    @Nullable
    public ConnectionResult getConnectionResult(@NonNull Api<?> api) {
        this.zg.lock();
        try {
            ConnectionResult connectionResult;
            if (((com.google.android.gms.common.api.zzc) this.zj.get(api.zzaqv())).getClient().isConnected()) {
                connectionResult = ConnectionResult.wO;
                return connectionResult;
            } else if (this.zp != null) {
                connectionResult = (ConnectionResult) this.zp.get(((com.google.android.gms.common.api.zzc) this.zj.get(api.zzaqv())).getApiKey());
                this.zg.unlock();
                return connectionResult;
            } else {
                this.zg.unlock();
                return null;
            }
        } finally {
            this.zg.unlock();
        }
    }

    public boolean isConnected() {
        this.zg.lock();
        try {
            boolean z = this.zp != null && this.zq == null;
            this.zg.unlock();
            return z;
        } catch (Throwable th) {
            this.zg.unlock();
        }
    }

    public boolean isConnecting() {
        this.zg.lock();
        try {
            boolean z = this.zp == null && this.zo;
            this.zg.unlock();
            return z;
        } catch (Throwable th) {
            this.zg.unlock();
        }
    }

    public <A extends Api.zzb, R extends Result, T extends com.google.android.gms.internal.zzqo.zza<R, A>> T zza(@NonNull T t) {
        this.zl.Ap.zzb(t);
        return ((com.google.android.gms.common.api.zzc) this.zj.get(t.zzaqv())).doRead((com.google.android.gms.internal.zzqo.zza) t);
    }

    public boolean zza(zzsa com_google_android_gms_internal_zzsa) {
        throw new UnsupportedOperationException();
    }

    public void zzard() {
        throw new UnsupportedOperationException();
    }

    public void zzarz() {
    }

    public <A extends Api.zzb, T extends com.google.android.gms.internal.zzqo.zza<? extends Result, A>> T zzb(@NonNull T t) {
        this.zl.Ap.zzb(t);
        return ((com.google.android.gms.common.api.zzc) this.zj.get(t.zzaqv())).doWrite((com.google.android.gms.internal.zzqo.zza) t);
    }
}
