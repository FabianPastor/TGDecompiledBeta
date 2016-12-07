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
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class zzaac implements zzaau {
    private final zzaap zzaxK;
    private ConnectionResult zzazA;
    private final Lock zzazn;
    private final zzg zzazs;
    private final Map<zzc<?>, com.google.android.gms.common.api.zzc<?>> zzazt = new HashMap();
    private final Map<Api<?>, Integer> zzazu;
    private final zzaal zzazv;
    private final com.google.android.gms.common.zzc zzazw;
    private final Condition zzazx;
    private boolean zzazy;
    private Map<zzzs<?>, ConnectionResult> zzazz;
    private final Looper zzrx;

    private class zza implements OnFailureListener, OnSuccessListener<Void> {
        final /* synthetic */ zzaac zzazB;

        private zza(zzaac com_google_android_gms_internal_zzaac) {
            this.zzazB = com_google_android_gms_internal_zzaac;
        }

        @Nullable
        private ConnectionResult zzvs() {
            ConnectionResult connectionResult = null;
            int i = 0;
            for (Api api : this.zzazB.zzazu.keySet()) {
                ConnectionResult connectionResult2 = (ConnectionResult) this.zzazB.zzazz.get(((com.google.android.gms.common.api.zzc) this.zzazB.zzazt.get(api.zzuH())).getApiKey());
                if (!connectionResult2.isSuccess()) {
                    int intValue = ((Integer) this.zzazB.zzazu.get(api)).intValue();
                    if (intValue != 2 && (intValue != 1 || connectionResult2.hasResolution() || this.zzazB.zzazw.isUserResolvableError(connectionResult2.getErrorCode()))) {
                        int priority = api.zzuF().getPriority();
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

        private void zzvt() {
            if (this.zzazB.zzazs == null) {
                this.zzazB.zzazv.zzaAs = Collections.emptySet();
                return;
            }
            Set hashSet = new HashSet(this.zzazB.zzazs.zzxe());
            Map zzxg = this.zzazB.zzazs.zzxg();
            for (Api api : zzxg.keySet()) {
                ConnectionResult connectionResult = (ConnectionResult) this.zzazB.zzazz.get(((com.google.android.gms.common.api.zzc) this.zzazB.zzazt.get(api.zzuH())).getApiKey());
                if (connectionResult != null && connectionResult.isSuccess()) {
                    hashSet.addAll(((com.google.android.gms.common.internal.zzg.zza) zzxg.get(api)).zzajm);
                }
            }
            this.zzazB.zzazv.zzaAs = hashSet;
        }

        public void onFailure(@NonNull Exception exception) {
            zzb com_google_android_gms_common_api_zzb = (zzb) exception;
            this.zzazB.zzazn.lock();
            try {
                this.zzazB.zzazz = com_google_android_gms_common_api_zzb.zzuK();
                this.zzazB.zzazA = zzvs();
                if (this.zzazB.zzazA == null) {
                    zzvt();
                    this.zzazB.zzazv.zzo(null);
                } else {
                    this.zzazB.zzazy = false;
                    this.zzazB.zzazv.zzc(this.zzazB.zzazA);
                }
                this.zzazB.zzazx.signalAll();
            } finally {
                this.zzazB.zzazn.unlock();
            }
        }

        public /* synthetic */ void onSuccess(Object obj) {
            zza((Void) obj);
        }

        public void zza(Void voidR) {
            this.zzazB.zzazn.lock();
            try {
                this.zzazB.zzazz = new ArrayMap(this.zzazB.zzazt.size());
                for (zzc com_google_android_gms_common_api_Api_zzc : this.zzazB.zzazt.keySet()) {
                    this.zzazB.zzazz.put(((com.google.android.gms.common.api.zzc) this.zzazB.zzazt.get(com_google_android_gms_common_api_Api_zzc)).getApiKey(), ConnectionResult.zzawX);
                }
                zzvt();
                this.zzazB.zzazv.zzo(null);
                this.zzazB.zzazx.signalAll();
            } finally {
                this.zzazB.zzazn.unlock();
            }
        }
    }

    public zzaac(Context context, Lock lock, Looper looper, com.google.android.gms.common.zzc com_google_android_gms_common_zzc, Map<zzc<?>, zze> map, zzg com_google_android_gms_common_internal_zzg, Map<Api<?>, Integer> map2, com.google.android.gms.common.api.Api.zza<? extends zzaxn, zzaxo> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzaxn__com_google_android_gms_internal_zzaxo, ArrayList<zzzy> arrayList, zzaal com_google_android_gms_internal_zzaal) {
        this.zzazn = lock;
        this.zzrx = looper;
        this.zzazx = lock.newCondition();
        this.zzazw = com_google_android_gms_common_zzc;
        this.zzazv = com_google_android_gms_internal_zzaal;
        this.zzazu = map2;
        this.zzazs = com_google_android_gms_common_internal_zzg;
        Map hashMap = new HashMap();
        for (Api api : map2.keySet()) {
            hashMap.put(api.zzuH(), api);
        }
        Map hashMap2 = new HashMap();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            zzzy com_google_android_gms_internal_zzzy = (zzzy) it.next();
            hashMap2.put(com_google_android_gms_internal_zzzy.zzawb, com_google_android_gms_internal_zzzy);
        }
        for (Entry entry : map.entrySet()) {
            Api api2 = (Api) hashMap.get(entry.getKey());
            this.zzazt.put((zzc) entry.getKey(), new zzaab(context, api2, looper, (zze) entry.getValue(), (zzzy) hashMap2.get(api2), com_google_android_gms_common_internal_zzg, com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzaxn__com_google_android_gms_internal_zzaxo));
        }
        this.zzaxK = zzaap.zzvS();
    }

    public ConnectionResult blockingConnect() {
        connect();
        while (isConnecting()) {
            try {
                this.zzazx.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return new ConnectionResult(15, null);
            }
        }
        return isConnected() ? ConnectionResult.zzawX : this.zzazA != null ? this.zzazA : new ConnectionResult(13, null);
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
            toNanos = this.zzazx.awaitNanos(toNanos);
        }
        return isConnected() ? ConnectionResult.zzawX : this.zzazA != null ? this.zzazA : new ConnectionResult(13, null);
    }

    public void connect() {
        this.zzazn.lock();
        try {
            if (!this.zzazy) {
                this.zzazy = true;
                this.zzazz = null;
                this.zzazA = null;
                OnFailureListener com_google_android_gms_internal_zzaac_zza = new zza();
                Executor com_google_android_gms_internal_zzact = new zzact(this.zzrx);
                this.zzaxK.zza(this.zzazt.values()).addOnSuccessListener(com_google_android_gms_internal_zzact, (OnSuccessListener) com_google_android_gms_internal_zzaac_zza).addOnFailureListener(com_google_android_gms_internal_zzact, com_google_android_gms_internal_zzaac_zza);
                this.zzazn.unlock();
            }
        } finally {
            this.zzazn.unlock();
        }
    }

    public void disconnect() {
        this.zzazn.lock();
        try {
            this.zzazy = false;
            this.zzazz = null;
            this.zzazA = null;
            this.zzazx.signalAll();
        } finally {
            this.zzazn.unlock();
        }
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }

    @Nullable
    public ConnectionResult getConnectionResult(@NonNull Api<?> api) {
        this.zzazn.lock();
        try {
            ConnectionResult connectionResult;
            if (((zzaab) this.zzazt.get(api.zzuH())).zzvr().isConnected()) {
                connectionResult = ConnectionResult.zzawX;
                return connectionResult;
            } else if (this.zzazz != null) {
                connectionResult = (ConnectionResult) this.zzazz.get(((com.google.android.gms.common.api.zzc) this.zzazt.get(api.zzuH())).getApiKey());
                this.zzazn.unlock();
                return connectionResult;
            } else {
                this.zzazn.unlock();
                return null;
            }
        } finally {
            this.zzazn.unlock();
        }
    }

    public boolean isConnected() {
        this.zzazn.lock();
        try {
            boolean z = this.zzazz != null && this.zzazA == null;
            this.zzazn.unlock();
            return z;
        } catch (Throwable th) {
            this.zzazn.unlock();
        }
    }

    public boolean isConnecting() {
        this.zzazn.lock();
        try {
            boolean z = this.zzazz == null && this.zzazy;
            this.zzazn.unlock();
            return z;
        } catch (Throwable th) {
            this.zzazn.unlock();
        }
    }

    public <A extends Api.zzb, R extends Result, T extends com.google.android.gms.internal.zzzv.zza<R, A>> T zza(@NonNull T t) {
        this.zzazv.zzaAx.zzb(t);
        return ((com.google.android.gms.common.api.zzc) this.zzazt.get(t.zzuH())).doRead((com.google.android.gms.internal.zzzv.zza) t);
    }

    public boolean zza(zzabi com_google_android_gms_internal_zzabi) {
        return false;
    }

    public <A extends Api.zzb, T extends com.google.android.gms.internal.zzzv.zza<? extends Result, A>> T zzb(@NonNull T t) {
        this.zzazv.zzaAx.zzb(t);
        return ((com.google.android.gms.common.api.zzc) this.zzazt.get(t.zzuH())).doWrite((com.google.android.gms.internal.zzzv.zza) t);
    }

    public void zzuN() {
    }

    public void zzvj() {
    }
}
