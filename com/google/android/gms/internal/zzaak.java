package com.google.android.gms.internal;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.zze;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class zzaak implements zzabc {
    private final Lock zzaAG;
    private final zzg zzaAL;
    private final Map<zzc<?>, zzaaj<?>> zzaAM = new HashMap();
    private final Map<zzc<?>, zzaaj<?>> zzaAN = new HashMap();
    private final Map<Api<?>, Boolean> zzaAO;
    private final zzaat zzaAP;
    private final zze zzaAQ;
    private final Condition zzaAR;
    private final boolean zzaAS;
    private final boolean zzaAT;
    private final Queue<com.google.android.gms.internal.zzaad.zza<?, ?>> zzaAU = new LinkedList();
    private boolean zzaAV;
    private Map<zzzz<?>, ConnectionResult> zzaAW;
    private Map<zzzz<?>, ConnectionResult> zzaAX;
    private zzb zzaAY;
    private ConnectionResult zzaAZ;
    private final zzaax zzayX;
    private final Looper zzrs;

    private class zza implements OnCompleteListener<Void> {
        final /* synthetic */ zzaak zzaBa;

        private zza(zzaak com_google_android_gms_internal_zzaak) {
            this.zzaBa = com_google_android_gms_internal_zzaak;
        }

        public void onComplete(@NonNull Task<Void> task) {
            this.zzaBa.zzaAG.lock();
            try {
                if (this.zzaBa.zzaAV) {
                    if (task.isSuccessful()) {
                        this.zzaBa.zzaAW = new ArrayMap(this.zzaBa.zzaAM.size());
                        for (zzaaj apiKey : this.zzaBa.zzaAM.values()) {
                            this.zzaBa.zzaAW.put(apiKey.getApiKey(), ConnectionResult.zzayj);
                        }
                    } else if (task.getException() instanceof com.google.android.gms.common.api.zzb) {
                        com.google.android.gms.common.api.zzb com_google_android_gms_common_api_zzb = (com.google.android.gms.common.api.zzb) task.getException();
                        if (this.zzaBa.zzaAT) {
                            this.zzaBa.zzaAW = new ArrayMap(this.zzaBa.zzaAM.size());
                            for (zzaaj com_google_android_gms_internal_zzaaj : this.zzaBa.zzaAM.values()) {
                                zzzz apiKey2 = com_google_android_gms_internal_zzaaj.getApiKey();
                                ConnectionResult zza = com_google_android_gms_common_api_zzb.zza(com_google_android_gms_internal_zzaaj);
                                if (this.zzaBa.zza(com_google_android_gms_internal_zzaaj, zza)) {
                                    this.zzaBa.zzaAW.put(apiKey2, new ConnectionResult(16));
                                } else {
                                    this.zzaBa.zzaAW.put(apiKey2, zza);
                                }
                            }
                        } else {
                            this.zzaBa.zzaAW = com_google_android_gms_common_api_zzb.zzvj();
                        }
                        this.zzaBa.zzaAZ = this.zzaBa.zzvX();
                    } else {
                        Log.e("ConnectionlessGAC", "Unexpected availability exception", task.getException());
                        this.zzaBa.zzaAW = Collections.emptyMap();
                        this.zzaBa.zzaAZ = new ConnectionResult(8);
                    }
                    if (this.zzaBa.zzaAX != null) {
                        this.zzaBa.zzaAW.putAll(this.zzaBa.zzaAX);
                        this.zzaBa.zzaAZ = this.zzaBa.zzvX();
                    }
                    if (this.zzaBa.zzaAZ == null) {
                        this.zzaBa.zzvV();
                        this.zzaBa.zzvW();
                    } else {
                        this.zzaBa.zzaAV = false;
                        this.zzaBa.zzaAP.zzc(this.zzaBa.zzaAZ);
                    }
                    this.zzaBa.zzaAR.signalAll();
                    this.zzaBa.zzaAG.unlock();
                }
            } finally {
                this.zzaBa.zzaAG.unlock();
            }
        }
    }

    private class zzb implements OnCompleteListener<Void> {
        final /* synthetic */ zzaak zzaBa;
        private zzabq zzaBb;

        zzb(zzaak com_google_android_gms_internal_zzaak, zzabq com_google_android_gms_internal_zzabq) {
            this.zzaBa = com_google_android_gms_internal_zzaak;
            this.zzaBb = com_google_android_gms_internal_zzabq;
        }

        void cancel() {
            this.zzaBb.zzrq();
        }

        public void onComplete(@NonNull Task<Void> task) {
            this.zzaBa.zzaAG.lock();
            try {
                if (this.zzaBa.zzaAV) {
                    if (task.isSuccessful()) {
                        this.zzaBa.zzaAX = new ArrayMap(this.zzaBa.zzaAN.size());
                        for (zzaaj apiKey : this.zzaBa.zzaAN.values()) {
                            this.zzaBa.zzaAX.put(apiKey.getApiKey(), ConnectionResult.zzayj);
                        }
                    } else if (task.getException() instanceof com.google.android.gms.common.api.zzb) {
                        com.google.android.gms.common.api.zzb com_google_android_gms_common_api_zzb = (com.google.android.gms.common.api.zzb) task.getException();
                        if (this.zzaBa.zzaAT) {
                            this.zzaBa.zzaAX = new ArrayMap(this.zzaBa.zzaAN.size());
                            for (zzaaj com_google_android_gms_internal_zzaaj : this.zzaBa.zzaAN.values()) {
                                zzzz apiKey2 = com_google_android_gms_internal_zzaaj.getApiKey();
                                ConnectionResult zza = com_google_android_gms_common_api_zzb.zza(com_google_android_gms_internal_zzaaj);
                                if (this.zzaBa.zza(com_google_android_gms_internal_zzaaj, zza)) {
                                    this.zzaBa.zzaAX.put(apiKey2, new ConnectionResult(16));
                                } else {
                                    this.zzaBa.zzaAX.put(apiKey2, zza);
                                }
                            }
                        } else {
                            this.zzaBa.zzaAX = com_google_android_gms_common_api_zzb.zzvj();
                        }
                    } else {
                        Log.e("ConnectionlessGAC", "Unexpected availability exception", task.getException());
                        this.zzaBa.zzaAX = Collections.emptyMap();
                    }
                    if (this.zzaBa.isConnected()) {
                        this.zzaBa.zzaAW.putAll(this.zzaBa.zzaAX);
                        if (this.zzaBa.zzvX() == null) {
                            this.zzaBa.zzvV();
                            this.zzaBa.zzvW();
                            this.zzaBa.zzaAR.signalAll();
                        }
                    }
                    this.zzaBb.zzrq();
                    this.zzaBa.zzaAG.unlock();
                    return;
                }
                this.zzaBb.zzrq();
            } finally {
                this.zzaBa.zzaAG.unlock();
            }
        }
    }

    public zzaak(Context context, Lock lock, Looper looper, zze com_google_android_gms_common_zze, Map<zzc<?>, Api.zze> map, zzg com_google_android_gms_common_internal_zzg, Map<Api<?>, Boolean> map2, com.google.android.gms.common.api.Api.zza<? extends zzbai, zzbaj> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzbai__com_google_android_gms_internal_zzbaj, ArrayList<zzaag> arrayList, zzaat com_google_android_gms_internal_zzaat, boolean z) {
        this.zzaAG = lock;
        this.zzrs = looper;
        this.zzaAR = lock.newCondition();
        this.zzaAQ = com_google_android_gms_common_zze;
        this.zzaAP = com_google_android_gms_internal_zzaat;
        this.zzaAO = map2;
        this.zzaAL = com_google_android_gms_common_internal_zzg;
        this.zzaAS = z;
        Map hashMap = new HashMap();
        for (Api api : map2.keySet()) {
            hashMap.put(api.zzvg(), api);
        }
        Map hashMap2 = new HashMap();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            zzaag com_google_android_gms_internal_zzaag = (zzaag) it.next();
            hashMap2.put(com_google_android_gms_internal_zzaag.zzaxf, com_google_android_gms_internal_zzaag);
        }
        Object obj = 1;
        Object obj2 = null;
        Object obj3 = null;
        for (Entry entry : map.entrySet()) {
            Object obj4;
            Object obj5;
            Object obj6;
            Api api2 = (Api) hashMap.get(entry.getKey());
            Api.zze com_google_android_gms_common_api_Api_zze = (Api.zze) entry.getValue();
            if (com_google_android_gms_common_api_Api_zze.zzvh()) {
                obj4 = 1;
                if (((Boolean) this.zzaAO.get(api2)).booleanValue()) {
                    obj5 = obj;
                    obj6 = obj2;
                } else {
                    obj5 = obj;
                    obj6 = 1;
                }
            } else {
                obj4 = obj3;
                obj5 = null;
                obj6 = obj2;
            }
            zzaaj com_google_android_gms_internal_zzaaj = new zzaaj(context, api2, looper, com_google_android_gms_common_api_Api_zze, (zzaag) hashMap2.get(api2), com_google_android_gms_common_internal_zzg, com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzbai__com_google_android_gms_internal_zzbaj);
            this.zzaAM.put((zzc) entry.getKey(), com_google_android_gms_internal_zzaaj);
            if (com_google_android_gms_common_api_Api_zze.zzrd()) {
                this.zzaAN.put((zzc) entry.getKey(), com_google_android_gms_internal_zzaaj);
            }
            obj3 = obj4;
            obj = obj5;
            obj2 = obj6;
        }
        boolean z2 = obj3 != null && obj == null && obj2 == null;
        this.zzaAT = z2;
        this.zzayX = zzaax.zzww();
    }

    private boolean zza(zzaaj<?> com_google_android_gms_internal_zzaaj_, ConnectionResult connectionResult) {
        return !connectionResult.isSuccess() && !connectionResult.hasResolution() && ((Boolean) this.zzaAO.get(com_google_android_gms_internal_zzaaj_.getApi())).booleanValue() && com_google_android_gms_internal_zzaaj_.zzvU().zzvh() && this.zzaAQ.isUserResolvableError(connectionResult.getErrorCode());
    }

    @Nullable
    private ConnectionResult zzb(@NonNull zzc<?> com_google_android_gms_common_api_Api_zzc_) {
        this.zzaAG.lock();
        try {
            zzaaj com_google_android_gms_internal_zzaaj = (zzaaj) this.zzaAM.get(com_google_android_gms_common_api_Api_zzc_);
            if (this.zzaAW == null || com_google_android_gms_internal_zzaaj == null) {
                this.zzaAG.unlock();
                return null;
            }
            ConnectionResult connectionResult = (ConnectionResult) this.zzaAW.get(com_google_android_gms_internal_zzaaj.getApiKey());
            return connectionResult;
        } finally {
            this.zzaAG.unlock();
        }
    }

    private <T extends com.google.android.gms.internal.zzaad.zza<? extends Result, ? extends com.google.android.gms.common.api.Api.zzb>> boolean zzd(@NonNull T t) {
        zzc zzvg = t.zzvg();
        ConnectionResult zzb = zzb(zzvg);
        if (zzb == null || zzb.getErrorCode() != 4) {
            return false;
        }
        t.zzB(new Status(4, null, this.zzayX.zza(((zzaaj) this.zzaAM.get(zzvg)).getApiKey(), this.zzaAP.getSessionId())));
        return true;
    }

    private void zzvV() {
        if (this.zzaAL == null) {
            this.zzaAP.zzaBR = Collections.emptySet();
            return;
        }
        Set hashSet = new HashSet(this.zzaAL.zzxL());
        Map zzxN = this.zzaAL.zzxN();
        for (Api api : zzxN.keySet()) {
            ConnectionResult connectionResult = getConnectionResult(api);
            if (connectionResult != null && connectionResult.isSuccess()) {
                hashSet.addAll(((com.google.android.gms.common.internal.zzg.zza) zzxN.get(api)).zzakq);
            }
        }
        this.zzaAP.zzaBR = hashSet;
    }

    private void zzvW() {
        while (!this.zzaAU.isEmpty()) {
            zzb((com.google.android.gms.internal.zzaad.zza) this.zzaAU.remove());
        }
        this.zzaAP.zzo(null);
    }

    @Nullable
    private ConnectionResult zzvX() {
        int i = 0;
        ConnectionResult connectionResult = null;
        int i2 = 0;
        ConnectionResult connectionResult2 = null;
        for (zzaaj com_google_android_gms_internal_zzaaj : this.zzaAM.values()) {
            Api api = com_google_android_gms_internal_zzaaj.getApi();
            ConnectionResult connectionResult3 = (ConnectionResult) this.zzaAW.get(com_google_android_gms_internal_zzaaj.getApiKey());
            if (!connectionResult3.isSuccess() && (!((Boolean) this.zzaAO.get(api)).booleanValue() || connectionResult3.hasResolution() || this.zzaAQ.isUserResolvableError(connectionResult3.getErrorCode()))) {
                int priority;
                if (connectionResult3.getErrorCode() == 4 && this.zzaAS) {
                    priority = api.zzve().getPriority();
                    if (connectionResult == null || i > priority) {
                        i = priority;
                        connectionResult = connectionResult3;
                    }
                } else {
                    ConnectionResult connectionResult4;
                    int i3;
                    priority = api.zzve().getPriority();
                    if (connectionResult2 == null || i2 > priority) {
                        int i4 = priority;
                        connectionResult4 = connectionResult3;
                        i3 = i4;
                    } else {
                        i3 = i2;
                        connectionResult4 = connectionResult2;
                    }
                    i2 = i3;
                    connectionResult2 = connectionResult4;
                }
            }
        }
        return (connectionResult2 == null || connectionResult == null || i2 <= i) ? connectionResult2 : connectionResult;
    }

    public ConnectionResult blockingConnect() {
        connect();
        while (isConnecting()) {
            try {
                this.zzaAR.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return new ConnectionResult(15, null);
            }
        }
        return isConnected() ? ConnectionResult.zzayj : this.zzaAZ != null ? this.zzaAZ : new ConnectionResult(13, null);
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
            toNanos = this.zzaAR.awaitNanos(toNanos);
        }
        return isConnected() ? ConnectionResult.zzayj : this.zzaAZ != null ? this.zzaAZ : new ConnectionResult(13, null);
    }

    public void connect() {
        this.zzaAG.lock();
        try {
            if (!this.zzaAV) {
                this.zzaAV = true;
                this.zzaAW = null;
                this.zzaAX = null;
                this.zzaAY = null;
                this.zzaAZ = null;
                this.zzayX.zzvx();
                this.zzayX.zza(this.zzaAM.values()).addOnCompleteListener(new zzadb(this.zzrs), new zza());
                this.zzaAG.unlock();
            }
        } finally {
            this.zzaAG.unlock();
        }
    }

    public void disconnect() {
        this.zzaAG.lock();
        try {
            this.zzaAV = false;
            this.zzaAW = null;
            this.zzaAX = null;
            if (this.zzaAY != null) {
                this.zzaAY.cancel();
                this.zzaAY = null;
            }
            this.zzaAZ = null;
            while (!this.zzaAU.isEmpty()) {
                com.google.android.gms.internal.zzaad.zza com_google_android_gms_internal_zzaad_zza = (com.google.android.gms.internal.zzaad.zza) this.zzaAU.remove();
                com_google_android_gms_internal_zzaad_zza.zza(null);
                com_google_android_gms_internal_zzaad_zza.cancel();
            }
            this.zzaAR.signalAll();
        } finally {
            this.zzaAG.unlock();
        }
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }

    @Nullable
    public ConnectionResult getConnectionResult(@NonNull Api<?> api) {
        return zzb(api.zzvg());
    }

    public boolean isConnected() {
        this.zzaAG.lock();
        try {
            boolean z = this.zzaAW != null && this.zzaAZ == null;
            this.zzaAG.unlock();
            return z;
        } catch (Throwable th) {
            this.zzaAG.unlock();
        }
    }

    public boolean isConnecting() {
        this.zzaAG.lock();
        try {
            boolean z = this.zzaAW == null && this.zzaAV;
            this.zzaAG.unlock();
            return z;
        } catch (Throwable th) {
            this.zzaAG.unlock();
        }
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, R extends Result, T extends com.google.android.gms.internal.zzaad.zza<R, A>> T zza(@NonNull T t) {
        if (this.zzaAS && zzd((com.google.android.gms.internal.zzaad.zza) t)) {
            return t;
        }
        if (isConnected()) {
            this.zzaAP.zzaBW.zzb(t);
            return ((zzaaj) this.zzaAM.get(t.zzvg())).doRead((com.google.android.gms.internal.zzaad.zza) t);
        }
        this.zzaAU.add(t);
        return t;
    }

    public boolean zza(zzabq com_google_android_gms_internal_zzabq) {
        this.zzaAG.lock();
        try {
            if (!this.zzaAV || zzvN()) {
                this.zzaAG.unlock();
                return false;
            }
            this.zzayX.zzvx();
            this.zzaAY = new zzb(this, com_google_android_gms_internal_zzabq);
            this.zzayX.zza(this.zzaAN.values()).addOnCompleteListener(new zzadb(this.zzrs), this.zzaAY);
            return true;
        } finally {
            this.zzaAG.unlock();
        }
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, T extends com.google.android.gms.internal.zzaad.zza<? extends Result, A>> T zzb(@NonNull T t) {
        zzc zzvg = t.zzvg();
        if (this.zzaAS && zzd((com.google.android.gms.internal.zzaad.zza) t)) {
            return t;
        }
        this.zzaAP.zzaBW.zzb(t);
        return ((zzaaj) this.zzaAM.get(zzvg)).doWrite((com.google.android.gms.internal.zzaad.zza) t);
    }

    public void zzvM() {
    }

    public boolean zzvN() {
        this.zzaAG.lock();
        try {
            if (this.zzaAV && this.zzaAS) {
                for (zzc zzb : this.zzaAN.keySet()) {
                    ConnectionResult zzb2 = zzb(zzb);
                    if (zzb2 != null) {
                        if (!zzb2.isSuccess()) {
                        }
                    }
                    this.zzaAG.unlock();
                    return false;
                }
                this.zzaAG.unlock();
                return true;
            }
            this.zzaAG.unlock();
            return false;
        } catch (Throwable th) {
            this.zzaAG.unlock();
        }
    }

    public void zzvn() {
        this.zzaAG.lock();
        try {
            this.zzayX.zzvn();
            if (this.zzaAY != null) {
                this.zzaAY.cancel();
                this.zzaAY = null;
            }
            if (this.zzaAX == null) {
                this.zzaAX = new ArrayMap(this.zzaAN.size());
            }
            ConnectionResult connectionResult = new ConnectionResult(4);
            for (zzaaj apiKey : this.zzaAN.values()) {
                this.zzaAX.put(apiKey.getApiKey(), connectionResult);
            }
            if (this.zzaAW != null) {
                this.zzaAW.putAll(this.zzaAX);
            }
            this.zzaAG.unlock();
        } catch (Throwable th) {
            this.zzaAG.unlock();
        }
    }
}
