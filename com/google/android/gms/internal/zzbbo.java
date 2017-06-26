package com.google.android.gms.internal;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzq;
import com.google.android.gms.common.internal.zzr;
import com.google.android.gms.common.zze;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public final class zzbbo implements zzbdo {
    private final zzbda zzaAN;
    private final zzq zzaCA;
    private final Map<zzc<?>, zzbbn<?>> zzaCB = new HashMap();
    private final Map<zzc<?>, zzbbn<?>> zzaCC = new HashMap();
    private final Map<Api<?>, Boolean> zzaCD;
    private final zzbco zzaCE;
    private final zze zzaCF;
    private final Condition zzaCG;
    private final boolean zzaCH;
    private final boolean zzaCI;
    private final Queue<zzbax<?, ?>> zzaCJ = new LinkedList();
    private boolean zzaCK;
    private Map<zzbas<?>, ConnectionResult> zzaCL;
    private Map<zzbas<?>, ConnectionResult> zzaCM;
    private zzbbr zzaCN;
    private ConnectionResult zzaCO;
    private final Lock zzaCv;
    private final Looper zzrO;

    public zzbbo(Context context, Lock lock, Looper looper, zze com_google_android_gms_common_zze, Map<zzc<?>, Api.zze> map, zzq com_google_android_gms_common_internal_zzq, Map<Api<?>, Boolean> map2, zza<? extends zzctj, zzctk> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzctj__com_google_android_gms_internal_zzctk, ArrayList<zzbbh> arrayList, zzbco com_google_android_gms_internal_zzbco, boolean z) {
        this.zzaCv = lock;
        this.zzrO = looper;
        this.zzaCG = lock.newCondition();
        this.zzaCF = com_google_android_gms_common_zze;
        this.zzaCE = com_google_android_gms_internal_zzbco;
        this.zzaCD = map2;
        this.zzaCA = com_google_android_gms_common_internal_zzq;
        this.zzaCH = z;
        Map hashMap = new HashMap();
        for (Api api : map2.keySet()) {
            hashMap.put(api.zzpd(), api);
        }
        Map hashMap2 = new HashMap();
        ArrayList arrayList2 = arrayList;
        int size = arrayList2.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList2.get(i);
            i++;
            zzbbh com_google_android_gms_internal_zzbbh = (zzbbh) obj;
            hashMap2.put(com_google_android_gms_internal_zzbbh.zzayW, com_google_android_gms_internal_zzbbh);
        }
        Object obj2 = 1;
        Object obj3 = null;
        Object obj4 = null;
        for (Entry entry : map.entrySet()) {
            Object obj5;
            Object obj6;
            Object obj7;
            Api api2 = (Api) hashMap.get(entry.getKey());
            Api.zze com_google_android_gms_common_api_Api_zze = (Api.zze) entry.getValue();
            if (com_google_android_gms_common_api_Api_zze.zzpe()) {
                obj5 = 1;
                if (((Boolean) this.zzaCD.get(api2)).booleanValue()) {
                    obj6 = obj2;
                    obj7 = obj3;
                } else {
                    obj6 = obj2;
                    obj7 = 1;
                }
            } else {
                obj5 = obj4;
                obj6 = null;
                obj7 = obj3;
            }
            zzbbn com_google_android_gms_internal_zzbbn = new zzbbn(context, api2, looper, com_google_android_gms_common_api_Api_zze, (zzbbh) hashMap2.get(api2), com_google_android_gms_common_internal_zzq, com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzctj__com_google_android_gms_internal_zzctk);
            this.zzaCB.put((zzc) entry.getKey(), com_google_android_gms_internal_zzbbn);
            if (com_google_android_gms_common_api_Api_zze.zzmv()) {
                this.zzaCC.put((zzc) entry.getKey(), com_google_android_gms_internal_zzbbn);
            }
            obj4 = obj5;
            obj2 = obj6;
            obj3 = obj7;
        }
        boolean z2 = obj4 != null && obj2 == null && obj3 == null;
        this.zzaCI = z2;
        this.zzaAN = zzbda.zzqk();
    }

    private final boolean zza(zzbbn<?> com_google_android_gms_internal_zzbbn_, ConnectionResult connectionResult) {
        return !connectionResult.isSuccess() && !connectionResult.hasResolution() && ((Boolean) this.zzaCD.get(com_google_android_gms_internal_zzbbn_.zzpg())).booleanValue() && com_google_android_gms_internal_zzbbn_.zzpJ().zzpe() && this.zzaCF.isUserResolvableError(connectionResult.getErrorCode());
    }

    @Nullable
    private final ConnectionResult zzb(@NonNull zzc<?> com_google_android_gms_common_api_Api_zzc_) {
        this.zzaCv.lock();
        try {
            zzbbn com_google_android_gms_internal_zzbbn = (zzbbn) this.zzaCB.get(com_google_android_gms_common_api_Api_zzc_);
            if (this.zzaCL == null || com_google_android_gms_internal_zzbbn == null) {
                this.zzaCv.unlock();
                return null;
            }
            ConnectionResult connectionResult = (ConnectionResult) this.zzaCL.get(com_google_android_gms_internal_zzbbn.zzph());
            return connectionResult;
        } finally {
            this.zzaCv.unlock();
        }
    }

    private final <T extends zzbax<? extends Result, ? extends zzb>> boolean zzg(@NonNull T t) {
        zzc zzpd = t.zzpd();
        ConnectionResult zzb = zzb(zzpd);
        if (zzb == null || zzb.getErrorCode() != 4) {
            return false;
        }
        t.zzr(new Status(4, null, this.zzaAN.zza(((zzbbn) this.zzaCB.get(zzpd)).zzph(), System.identityHashCode(this.zzaCE))));
        return true;
    }

    private final boolean zzpK() {
        this.zzaCv.lock();
        try {
            if (this.zzaCK && this.zzaCH) {
                for (zzc zzb : this.zzaCC.keySet()) {
                    ConnectionResult zzb2 = zzb(zzb);
                    if (zzb2 != null) {
                        if (!zzb2.isSuccess()) {
                        }
                    }
                    this.zzaCv.unlock();
                    return false;
                }
                this.zzaCv.unlock();
                return true;
            }
            this.zzaCv.unlock();
            return false;
        } catch (Throwable th) {
            this.zzaCv.unlock();
        }
    }

    private final void zzpL() {
        if (this.zzaCA == null) {
            this.zzaCE.zzaDG = Collections.emptySet();
            return;
        }
        Set hashSet = new HashSet(this.zzaCA.zzrn());
        Map zzrp = this.zzaCA.zzrp();
        for (Api api : zzrp.keySet()) {
            ConnectionResult connectionResult = getConnectionResult(api);
            if (connectionResult != null && connectionResult.isSuccess()) {
                hashSet.addAll(((zzr) zzrp.get(api)).zzame);
            }
        }
        this.zzaCE.zzaDG = hashSet;
    }

    private final void zzpM() {
        while (!this.zzaCJ.isEmpty()) {
            zze((zzbax) this.zzaCJ.remove());
        }
        this.zzaCE.zzm(null);
    }

    @Nullable
    private final ConnectionResult zzpN() {
        int i = 0;
        ConnectionResult connectionResult = null;
        int i2 = 0;
        ConnectionResult connectionResult2 = null;
        for (zzbbn com_google_android_gms_internal_zzbbn : this.zzaCB.values()) {
            Api zzpg = com_google_android_gms_internal_zzbbn.zzpg();
            ConnectionResult connectionResult3 = (ConnectionResult) this.zzaCL.get(com_google_android_gms_internal_zzbbn.zzph());
            if (!connectionResult3.isSuccess() && (!((Boolean) this.zzaCD.get(zzpg)).booleanValue() || connectionResult3.hasResolution() || this.zzaCF.isUserResolvableError(connectionResult3.getErrorCode()))) {
                int priority;
                if (connectionResult3.getErrorCode() == 4 && this.zzaCH) {
                    priority = zzpg.zzpb().getPriority();
                    if (connectionResult == null || i > priority) {
                        i = priority;
                        connectionResult = connectionResult3;
                    }
                } else {
                    ConnectionResult connectionResult4;
                    int i3;
                    priority = zzpg.zzpb().getPriority();
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

    public final ConnectionResult blockingConnect() {
        connect();
        while (isConnecting()) {
            try {
                this.zzaCG.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return new ConnectionResult(15, null);
            }
        }
        return isConnected() ? ConnectionResult.zzazX : this.zzaCO != null ? this.zzaCO : new ConnectionResult(13, null);
    }

    public final ConnectionResult blockingConnect(long j, TimeUnit timeUnit) {
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
            toNanos = this.zzaCG.awaitNanos(toNanos);
        }
        return isConnected() ? ConnectionResult.zzazX : this.zzaCO != null ? this.zzaCO : new ConnectionResult(13, null);
    }

    public final void connect() {
        this.zzaCv.lock();
        try {
            if (!this.zzaCK) {
                this.zzaCK = true;
                this.zzaCL = null;
                this.zzaCM = null;
                this.zzaCN = null;
                this.zzaCO = null;
                this.zzaAN.zzps();
                this.zzaAN.zza(this.zzaCB.values()).addOnCompleteListener(new zzbgu(this.zzrO), new zzbbq());
                this.zzaCv.unlock();
            }
        } finally {
            this.zzaCv.unlock();
        }
    }

    public final void disconnect() {
        this.zzaCv.lock();
        try {
            this.zzaCK = false;
            this.zzaCL = null;
            this.zzaCM = null;
            if (this.zzaCN != null) {
                this.zzaCN.cancel();
                this.zzaCN = null;
            }
            this.zzaCO = null;
            while (!this.zzaCJ.isEmpty()) {
                zzbax com_google_android_gms_internal_zzbax = (zzbax) this.zzaCJ.remove();
                com_google_android_gms_internal_zzbax.zza(null);
                com_google_android_gms_internal_zzbax.cancel();
            }
            this.zzaCG.signalAll();
        } finally {
            this.zzaCv.unlock();
        }
    }

    public final void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }

    @Nullable
    public final ConnectionResult getConnectionResult(@NonNull Api<?> api) {
        return zzb(api.zzpd());
    }

    public final boolean isConnected() {
        this.zzaCv.lock();
        try {
            boolean z = this.zzaCL != null && this.zzaCO == null;
            this.zzaCv.unlock();
            return z;
        } catch (Throwable th) {
            this.zzaCv.unlock();
        }
    }

    public final boolean isConnecting() {
        this.zzaCv.lock();
        try {
            boolean z = this.zzaCL == null && this.zzaCK;
            this.zzaCv.unlock();
            return z;
        } catch (Throwable th) {
            this.zzaCv.unlock();
        }
    }

    public final boolean zza(zzbeh com_google_android_gms_internal_zzbeh) {
        this.zzaCv.lock();
        try {
            if (!this.zzaCK || zzpK()) {
                this.zzaCv.unlock();
                return false;
            }
            this.zzaAN.zzps();
            this.zzaCN = new zzbbr(this, com_google_android_gms_internal_zzbeh);
            this.zzaAN.zza(this.zzaCC.values()).addOnCompleteListener(new zzbgu(this.zzrO), this.zzaCN);
            return true;
        } finally {
            this.zzaCv.unlock();
        }
    }

    public final <A extends zzb, R extends Result, T extends zzbax<R, A>> T zzd(@NonNull T t) {
        if (this.zzaCH && zzg((zzbax) t)) {
            return t;
        }
        if (isConnected()) {
            this.zzaCE.zzaDL.zzb(t);
            return ((zzbbn) this.zzaCB.get(t.zzpd())).zza((zzbax) t);
        }
        this.zzaCJ.add(t);
        return t;
    }

    public final <A extends zzb, T extends zzbax<? extends Result, A>> T zze(@NonNull T t) {
        zzc zzpd = t.zzpd();
        if (this.zzaCH && zzg((zzbax) t)) {
            return t;
        }
        this.zzaCE.zzaDL.zzb(t);
        return ((zzbbn) this.zzaCB.get(zzpd)).zzb((zzbax) t);
    }

    public final void zzpE() {
    }

    public final void zzpl() {
        this.zzaCv.lock();
        try {
            this.zzaAN.zzpl();
            if (this.zzaCN != null) {
                this.zzaCN.cancel();
                this.zzaCN = null;
            }
            if (this.zzaCM == null) {
                this.zzaCM = new ArrayMap(this.zzaCC.size());
            }
            ConnectionResult connectionResult = new ConnectionResult(4);
            for (zzbbn zzph : this.zzaCC.values()) {
                this.zzaCM.put(zzph.zzph(), connectionResult);
            }
            if (this.zzaCL != null) {
                this.zzaCL.putAll(this.zzaCM);
            }
            this.zzaCv.unlock();
        } catch (Throwable th) {
            this.zzaCv.unlock();
        }
    }
}
