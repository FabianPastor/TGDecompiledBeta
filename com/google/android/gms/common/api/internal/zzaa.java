package com.google.android.gms.common.api.internal;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzr;
import com.google.android.gms.common.internal.zzt;
import com.google.android.gms.common.zzf;
import com.google.android.gms.internal.zzbha;
import com.google.android.gms.internal.zzcxd;
import com.google.android.gms.internal.zzcxe;
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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public final class zzaa implements zzcc {
    private final Looper zzall;
    private final zzbm zzfmi;
    private final Lock zzfps;
    private final zzr zzfpx;
    private final Map<zzc<?>, zzz<?>> zzfpy = new HashMap();
    private final Map<zzc<?>, zzz<?>> zzfpz = new HashMap();
    private final Map<Api<?>, Boolean> zzfqa;
    private final zzba zzfqb;
    private final zzf zzfqc;
    private final Condition zzfqd;
    private final boolean zzfqe;
    private final boolean zzfqf;
    private final Queue<zzm<?, ?>> zzfqg = new LinkedList();
    private boolean zzfqh;
    private Map<zzh<?>, ConnectionResult> zzfqi;
    private Map<zzh<?>, ConnectionResult> zzfqj;
    private zzad zzfqk;
    private ConnectionResult zzfql;

    public zzaa(Context context, Lock lock, Looper looper, zzf com_google_android_gms_common_zzf, Map<zzc<?>, zze> map, zzr com_google_android_gms_common_internal_zzr, Map<Api<?>, Boolean> map2, zza<? extends zzcxd, zzcxe> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzcxd__com_google_android_gms_internal_zzcxe, ArrayList<zzt> arrayList, zzba com_google_android_gms_common_api_internal_zzba, boolean z) {
        this.zzfps = lock;
        this.zzall = looper;
        this.zzfqd = lock.newCondition();
        this.zzfqc = com_google_android_gms_common_zzf;
        this.zzfqb = com_google_android_gms_common_api_internal_zzba;
        this.zzfqa = map2;
        this.zzfpx = com_google_android_gms_common_internal_zzr;
        this.zzfqe = z;
        Map hashMap = new HashMap();
        for (Api api : map2.keySet()) {
            hashMap.put(api.zzagf(), api);
        }
        Map hashMap2 = new HashMap();
        ArrayList arrayList2 = arrayList;
        int size = arrayList2.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList2.get(i);
            i++;
            zzt com_google_android_gms_common_api_internal_zzt = (zzt) obj;
            hashMap2.put(com_google_android_gms_common_api_internal_zzt.zzfin, com_google_android_gms_common_api_internal_zzt);
        }
        Object obj2 = 1;
        Object obj3 = null;
        Object obj4 = null;
        for (Entry entry : map.entrySet()) {
            Object obj5;
            Object obj6;
            Object obj7;
            Api api2 = (Api) hashMap.get(entry.getKey());
            zze com_google_android_gms_common_api_Api_zze = (zze) entry.getValue();
            if (com_google_android_gms_common_api_Api_zze.zzagg()) {
                obj5 = 1;
                if (((Boolean) this.zzfqa.get(api2)).booleanValue()) {
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
            zzz com_google_android_gms_common_api_internal_zzz = new zzz(context, api2, looper, com_google_android_gms_common_api_Api_zze, (zzt) hashMap2.get(api2), com_google_android_gms_common_internal_zzr, com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzcxd__com_google_android_gms_internal_zzcxe);
            this.zzfpy.put((zzc) entry.getKey(), com_google_android_gms_common_api_internal_zzz);
            if (com_google_android_gms_common_api_Api_zze.zzaay()) {
                this.zzfpz.put((zzc) entry.getKey(), com_google_android_gms_common_api_internal_zzz);
            }
            obj4 = obj5;
            obj2 = obj6;
            obj3 = obj7;
        }
        boolean z2 = obj4 != null && obj2 == null && obj3 == null;
        this.zzfqf = z2;
        this.zzfmi = zzbm.zzaiq();
    }

    private final boolean zza(zzz<?> com_google_android_gms_common_api_internal_zzz_, ConnectionResult connectionResult) {
        return !connectionResult.isSuccess() && !connectionResult.hasResolution() && ((Boolean) this.zzfqa.get(com_google_android_gms_common_api_internal_zzz_.zzagl())).booleanValue() && com_google_android_gms_common_api_internal_zzz_.zzahp().zzagg() && this.zzfqc.isUserResolvableError(connectionResult.getErrorCode());
    }

    private final void zzahr() {
        if (this.zzfpx == null) {
            this.zzfqb.zzfsc = Collections.emptySet();
            return;
        }
        Set hashSet = new HashSet(this.zzfpx.zzakv());
        Map zzakx = this.zzfpx.zzakx();
        for (Api api : zzakx.keySet()) {
            ConnectionResult connectionResult = getConnectionResult(api);
            if (connectionResult != null && connectionResult.isSuccess()) {
                hashSet.addAll(((zzt) zzakx.get(api)).zzehs);
            }
        }
        this.zzfqb.zzfsc = hashSet;
    }

    private final void zzahs() {
        while (!this.zzfqg.isEmpty()) {
            zze((zzm) this.zzfqg.remove());
        }
        this.zzfqb.zzj(null);
    }

    private final ConnectionResult zzaht() {
        int i = 0;
        ConnectionResult connectionResult = null;
        int i2 = 0;
        ConnectionResult connectionResult2 = null;
        for (zzz com_google_android_gms_common_api_internal_zzz : this.zzfpy.values()) {
            Api zzagl = com_google_android_gms_common_api_internal_zzz.zzagl();
            ConnectionResult connectionResult3 = (ConnectionResult) this.zzfqi.get(com_google_android_gms_common_api_internal_zzz.zzagn());
            if (!connectionResult3.isSuccess() && (!((Boolean) this.zzfqa.get(zzagl)).booleanValue() || connectionResult3.hasResolution() || this.zzfqc.isUserResolvableError(connectionResult3.getErrorCode()))) {
                int priority;
                if (connectionResult3.getErrorCode() == 4 && this.zzfqe) {
                    priority = zzagl.zzagd().getPriority();
                    if (connectionResult == null || i > priority) {
                        i = priority;
                        connectionResult = connectionResult3;
                    }
                } else {
                    ConnectionResult connectionResult4;
                    int i3;
                    priority = zzagl.zzagd().getPriority();
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

    private final ConnectionResult zzb(zzc<?> com_google_android_gms_common_api_Api_zzc_) {
        this.zzfps.lock();
        try {
            zzz com_google_android_gms_common_api_internal_zzz = (zzz) this.zzfpy.get(com_google_android_gms_common_api_Api_zzc_);
            if (this.zzfqi == null || com_google_android_gms_common_api_internal_zzz == null) {
                this.zzfps.unlock();
                return null;
            }
            ConnectionResult connectionResult = (ConnectionResult) this.zzfqi.get(com_google_android_gms_common_api_internal_zzz.zzagn());
            return connectionResult;
        } finally {
            this.zzfps.unlock();
        }
    }

    private final <T extends zzm<? extends Result, ? extends zzb>> boolean zzg(T t) {
        zzc zzagf = t.zzagf();
        ConnectionResult zzb = zzb(zzagf);
        if (zzb == null || zzb.getErrorCode() != 4) {
            return false;
        }
        t.zzu(new Status(4, null, this.zzfmi.zza(((zzz) this.zzfpy.get(zzagf)).zzagn(), System.identityHashCode(this.zzfqb))));
        return true;
    }

    public final ConnectionResult blockingConnect() {
        connect();
        while (isConnecting()) {
            try {
                this.zzfqd.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return new ConnectionResult(15, null);
            }
        }
        return isConnected() ? ConnectionResult.zzfkr : this.zzfql != null ? this.zzfql : new ConnectionResult(13, null);
    }

    public final void connect() {
        this.zzfps.lock();
        try {
            if (!this.zzfqh) {
                this.zzfqh = true;
                this.zzfqi = null;
                this.zzfqj = null;
                this.zzfqk = null;
                this.zzfql = null;
                this.zzfmi.zzagz();
                this.zzfmi.zza(this.zzfpy.values()).addOnCompleteListener(new zzbha(this.zzall), new zzac());
                this.zzfps.unlock();
            }
        } finally {
            this.zzfps.unlock();
        }
    }

    public final void disconnect() {
        this.zzfps.lock();
        try {
            this.zzfqh = false;
            this.zzfqi = null;
            this.zzfqj = null;
            if (this.zzfqk != null) {
                this.zzfqk.cancel();
                this.zzfqk = null;
            }
            this.zzfql = null;
            while (!this.zzfqg.isEmpty()) {
                zzm com_google_android_gms_common_api_internal_zzm = (zzm) this.zzfqg.remove();
                com_google_android_gms_common_api_internal_zzm.zza(null);
                com_google_android_gms_common_api_internal_zzm.cancel();
            }
            this.zzfqd.signalAll();
        } finally {
            this.zzfps.unlock();
        }
    }

    public final void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }

    public final ConnectionResult getConnectionResult(Api<?> api) {
        return zzb(api.zzagf());
    }

    public final boolean isConnected() {
        this.zzfps.lock();
        try {
            boolean z = this.zzfqi != null && this.zzfql == null;
            this.zzfps.unlock();
            return z;
        } catch (Throwable th) {
            this.zzfps.unlock();
        }
    }

    public final boolean isConnecting() {
        this.zzfps.lock();
        try {
            boolean z = this.zzfqi == null && this.zzfqh;
            this.zzfps.unlock();
            return z;
        } catch (Throwable th) {
            this.zzfps.unlock();
        }
    }

    public final void zzahk() {
    }

    public final <A extends zzb, R extends Result, T extends zzm<R, A>> T zzd(T t) {
        if (this.zzfqe && zzg((zzm) t)) {
            return t;
        }
        if (isConnected()) {
            this.zzfqb.zzfsh.zzb(t);
            return ((zzz) this.zzfpy.get(t.zzagf())).zza(t);
        }
        this.zzfqg.add(t);
        return t;
    }

    public final <A extends zzb, T extends zzm<? extends Result, A>> T zze(T t) {
        zzc zzagf = t.zzagf();
        if (this.zzfqe && zzg((zzm) t)) {
            return t;
        }
        this.zzfqb.zzfsh.zzb(t);
        return ((zzz) this.zzfpy.get(zzagf)).zzb(t);
    }
}
