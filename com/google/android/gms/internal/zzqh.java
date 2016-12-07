package com.google.android.gms.internal;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzh;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import org.telegram.messenger.exoplayer.C;

final class zzqh implements zzqy {
    private final Context mContext;
    private final zzqp wV;
    private final zzqr wW;
    private final zzqr wX;
    private final Map<zzc<?>, zzqr> wY;
    private final Set<zzrl> wZ = Collections.newSetFromMap(new WeakHashMap());
    private final zze xa;
    private Bundle xb;
    private ConnectionResult xc = null;
    private ConnectionResult xd = null;
    private boolean xe = false;
    private final Lock xf;
    private int xg = 0;
    private final Looper zzajn;

    private class zza implements com.google.android.gms.internal.zzqy.zza {
        final /* synthetic */ zzqh xh;

        private zza(zzqh com_google_android_gms_internal_zzqh) {
            this.xh = com_google_android_gms_internal_zzqh;
        }

        public void zzc(int i, boolean z) {
            this.xh.xf.lock();
            try {
                if (this.xh.xe || this.xh.xd == null || !this.xh.xd.isSuccess()) {
                    this.xh.xe = false;
                    this.xh.zzb(i, z);
                    return;
                }
                this.xh.xe = true;
                this.xh.wX.onConnectionSuspended(i);
                this.xh.xf.unlock();
            } finally {
                this.xh.xf.unlock();
            }
        }

        public void zzd(@NonNull ConnectionResult connectionResult) {
            this.xh.xf.lock();
            try {
                this.xh.xc = connectionResult;
                this.xh.zzarb();
            } finally {
                this.xh.xf.unlock();
            }
        }

        public void zzn(@Nullable Bundle bundle) {
            this.xh.xf.lock();
            try {
                this.xh.zzm(bundle);
                this.xh.xc = ConnectionResult.uJ;
                this.xh.zzarb();
            } finally {
                this.xh.xf.unlock();
            }
        }
    }

    private class zzb implements com.google.android.gms.internal.zzqy.zza {
        final /* synthetic */ zzqh xh;

        private zzb(zzqh com_google_android_gms_internal_zzqh) {
            this.xh = com_google_android_gms_internal_zzqh;
        }

        public void zzc(int i, boolean z) {
            this.xh.xf.lock();
            try {
                if (this.xh.xe) {
                    this.xh.xe = false;
                    this.xh.zzb(i, z);
                    return;
                }
                this.xh.xe = true;
                this.xh.wW.onConnectionSuspended(i);
                this.xh.xf.unlock();
            } finally {
                this.xh.xf.unlock();
            }
        }

        public void zzd(@NonNull ConnectionResult connectionResult) {
            this.xh.xf.lock();
            try {
                this.xh.xd = connectionResult;
                this.xh.zzarb();
            } finally {
                this.xh.xf.unlock();
            }
        }

        public void zzn(@Nullable Bundle bundle) {
            this.xh.xf.lock();
            try {
                this.xh.xd = ConnectionResult.uJ;
                this.xh.zzarb();
            } finally {
                this.xh.xf.unlock();
            }
        }
    }

    private zzqh(Context context, zzqp com_google_android_gms_internal_zzqp, Lock lock, Looper looper, com.google.android.gms.common.zzc com_google_android_gms_common_zzc, Map<zzc<?>, zze> map, Map<zzc<?>, zze> map2, zzh com_google_android_gms_common_internal_zzh, com.google.android.gms.common.api.Api.zza<? extends zzwz, zzxa> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzwz__com_google_android_gms_internal_zzxa, zze com_google_android_gms_common_api_Api_zze, ArrayList<zzqf> arrayList, ArrayList<zzqf> arrayList2, Map<Api<?>, Integer> map3, Map<Api<?>, Integer> map4) {
        this.mContext = context;
        this.wV = com_google_android_gms_internal_zzqp;
        this.xf = lock;
        this.zzajn = looper;
        this.xa = com_google_android_gms_common_api_Api_zze;
        this.wW = new zzqr(context, this.wV, lock, looper, com_google_android_gms_common_zzc, map2, null, map4, null, arrayList2, new zza());
        this.wX = new zzqr(context, this.wV, lock, looper, com_google_android_gms_common_zzc, map, com_google_android_gms_common_internal_zzh, map3, com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzwz__com_google_android_gms_internal_zzxa, arrayList, new zzb());
        Map arrayMap = new ArrayMap();
        for (zzc put : map2.keySet()) {
            arrayMap.put(put, this.wW);
        }
        for (zzc put2 : map.keySet()) {
            arrayMap.put(put2, this.wX);
        }
        this.wY = Collections.unmodifiableMap(arrayMap);
    }

    public static zzqh zza(Context context, zzqp com_google_android_gms_internal_zzqp, Lock lock, Looper looper, com.google.android.gms.common.zzc com_google_android_gms_common_zzc, Map<zzc<?>, zze> map, zzh com_google_android_gms_common_internal_zzh, Map<Api<?>, Integer> map2, com.google.android.gms.common.api.Api.zza<? extends zzwz, zzxa> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzwz__com_google_android_gms_internal_zzxa, ArrayList<zzqf> arrayList) {
        zze com_google_android_gms_common_api_Api_zze = null;
        Map arrayMap = new ArrayMap();
        Map arrayMap2 = new ArrayMap();
        for (Entry entry : map.entrySet()) {
            zze com_google_android_gms_common_api_Api_zze2 = (zze) entry.getValue();
            if (com_google_android_gms_common_api_Api_zze2.zzahs()) {
                com_google_android_gms_common_api_Api_zze = com_google_android_gms_common_api_Api_zze2;
            }
            if (com_google_android_gms_common_api_Api_zze2.zzahd()) {
                arrayMap.put((zzc) entry.getKey(), com_google_android_gms_common_api_Api_zze2);
            } else {
                arrayMap2.put((zzc) entry.getKey(), com_google_android_gms_common_api_Api_zze2);
            }
        }
        zzac.zza(!arrayMap.isEmpty(), (Object) "CompositeGoogleApiClient should not be used without any APIs that require sign-in.");
        Map arrayMap3 = new ArrayMap();
        Map arrayMap4 = new ArrayMap();
        for (Api api : map2.keySet()) {
            zzc zzapp = api.zzapp();
            if (arrayMap.containsKey(zzapp)) {
                arrayMap3.put(api, (Integer) map2.get(api));
            } else if (arrayMap2.containsKey(zzapp)) {
                arrayMap4.put(api, (Integer) map2.get(api));
            } else {
                throw new IllegalStateException("Each API in the apiTypeMap must have a corresponding client in the clients map.");
            }
        }
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            zzqf com_google_android_gms_internal_zzqf = (zzqf) it.next();
            if (arrayMap3.containsKey(com_google_android_gms_internal_zzqf.tv)) {
                arrayList2.add(com_google_android_gms_internal_zzqf);
            } else if (arrayMap4.containsKey(com_google_android_gms_internal_zzqf.tv)) {
                arrayList3.add(com_google_android_gms_internal_zzqf);
            } else {
                throw new IllegalStateException("Each ClientCallbacks must have a corresponding API in the apiTypeMap");
            }
        }
        return new zzqh(context, com_google_android_gms_internal_zzqp, lock, looper, com_google_android_gms_common_zzc, arrayMap, arrayMap2, com_google_android_gms_common_internal_zzh, com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzwz__com_google_android_gms_internal_zzxa, com_google_android_gms_common_api_Api_zze, arrayList2, arrayList3, arrayMap3, arrayMap4);
    }

    private void zzara() {
        this.xd = null;
        this.xc = null;
        this.wW.connect();
        this.wX.connect();
    }

    private void zzarb() {
        if (zzc(this.xc)) {
            if (zzc(this.xd) || zzare()) {
                zzarc();
            } else if (this.xd == null) {
            } else {
                if (this.xg == 1) {
                    zzard();
                    return;
                }
                zzb(this.xd);
                this.wW.disconnect();
            }
        } else if (this.xc != null && zzc(this.xd)) {
            this.wX.disconnect();
            zzb(this.xc);
        } else if (this.xc != null && this.xd != null) {
            ConnectionResult connectionResult = this.xc;
            if (this.wX.yo < this.wW.yo) {
                connectionResult = this.xd;
            }
            zzb(connectionResult);
        }
    }

    private void zzarc() {
        switch (this.xg) {
            case 1:
                break;
            case 2:
                this.wV.zzn(this.xb);
                break;
            default:
                Log.wtf("CompositeGAC", "Attempted to call success callbacks in CONNECTION_MODE_NONE. Callbacks should be disabled via GmsClientSupervisor", new AssertionError());
                break;
        }
        zzard();
        this.xg = 0;
    }

    private void zzard() {
        for (zzrl zzahr : this.wZ) {
            zzahr.zzahr();
        }
        this.wZ.clear();
    }

    private boolean zzare() {
        return this.xd != null && this.xd.getErrorCode() == 4;
    }

    @Nullable
    private PendingIntent zzarf() {
        return this.xa == null ? null : PendingIntent.getActivity(this.mContext, this.wV.getSessionId(), this.xa.zzaht(), C.SAMPLE_FLAG_DECODE_ONLY);
    }

    private void zzb(int i, boolean z) {
        this.wV.zzc(i, z);
        this.xd = null;
        this.xc = null;
    }

    private void zzb(ConnectionResult connectionResult) {
        switch (this.xg) {
            case 1:
                break;
            case 2:
                this.wV.zzd(connectionResult);
                break;
            default:
                Log.wtf("CompositeGAC", "Attempted to call failure callbacks in CONNECTION_MODE_NONE. Callbacks should be disabled via GmsClientSupervisor", new Exception());
                break;
        }
        zzard();
        this.xg = 0;
    }

    private static boolean zzc(ConnectionResult connectionResult) {
        return connectionResult != null && connectionResult.isSuccess();
    }

    private boolean zze(com.google.android.gms.internal.zzqc.zza<? extends Result, ? extends com.google.android.gms.common.api.Api.zzb> com_google_android_gms_internal_zzqc_zza__extends_com_google_android_gms_common_api_Result___extends_com_google_android_gms_common_api_Api_zzb) {
        zzc zzapp = com_google_android_gms_internal_zzqc_zza__extends_com_google_android_gms_common_api_Result___extends_com_google_android_gms_common_api_Api_zzb.zzapp();
        zzac.zzb(this.wY.containsKey(zzapp), (Object) "GoogleApiClient is not configured to use the API required for this call.");
        return ((zzqr) this.wY.get(zzapp)).equals(this.wX);
    }

    private void zzm(Bundle bundle) {
        if (this.xb == null) {
            this.xb = bundle;
        } else if (bundle != null) {
            this.xb.putAll(bundle);
        }
    }

    public ConnectionResult blockingConnect() {
        throw new UnsupportedOperationException();
    }

    public ConnectionResult blockingConnect(long j, @NonNull TimeUnit timeUnit) {
        throw new UnsupportedOperationException();
    }

    public void connect() {
        this.xg = 2;
        this.xe = false;
        zzara();
    }

    public void disconnect() {
        this.xd = null;
        this.xc = null;
        this.xg = 0;
        this.wW.disconnect();
        this.wX.disconnect();
        zzard();
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.append(str).append("authClient").println(":");
        this.wX.dump(String.valueOf(str).concat("  "), fileDescriptor, printWriter, strArr);
        printWriter.append(str).append("anonClient").println(":");
        this.wW.dump(String.valueOf(str).concat("  "), fileDescriptor, printWriter, strArr);
    }

    @Nullable
    public ConnectionResult getConnectionResult(@NonNull Api<?> api) {
        return ((zzqr) this.wY.get(api.zzapp())).equals(this.wX) ? zzare() ? new ConnectionResult(4, zzarf()) : this.wX.getConnectionResult(api) : this.wW.getConnectionResult(api);
    }

    public boolean isConnected() {
        boolean z = true;
        this.xf.lock();
        try {
            if (!(this.wW.isConnected() && (zzaqz() || zzare() || this.xg == 1))) {
                z = false;
            }
            this.xf.unlock();
            return z;
        } catch (Throwable th) {
            this.xf.unlock();
        }
    }

    public boolean isConnecting() {
        this.xf.lock();
        try {
            boolean z = this.xg == 2;
            this.xf.unlock();
            return z;
        } catch (Throwable th) {
            this.xf.unlock();
        }
    }

    public boolean zza(zzrl com_google_android_gms_internal_zzrl) {
        this.xf.lock();
        try {
            if ((isConnecting() || isConnected()) && !zzaqz()) {
                this.wZ.add(com_google_android_gms_internal_zzrl);
                if (this.xg == 0) {
                    this.xg = 1;
                }
                this.xd = null;
                this.wX.connect();
                return true;
            }
            this.xf.unlock();
            return false;
        } finally {
            this.xf.unlock();
        }
    }

    public void zzaqb() {
        this.xf.lock();
        try {
            boolean isConnecting = isConnecting();
            this.wX.disconnect();
            this.xd = new ConnectionResult(4);
            if (isConnecting) {
                new Handler(this.zzajn).post(new Runnable(this) {
                    final /* synthetic */ zzqh xh;

                    {
                        this.xh = r1;
                    }

                    public void run() {
                        this.xh.xf.lock();
                        try {
                            this.xh.zzarb();
                        } finally {
                            this.xh.xf.unlock();
                        }
                    }
                });
            } else {
                zzard();
            }
            this.xf.unlock();
        } catch (Throwable th) {
            this.xf.unlock();
        }
    }

    public void zzaqy() {
        this.wW.zzaqy();
        this.wX.zzaqy();
    }

    public boolean zzaqz() {
        return this.wX.isConnected();
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, R extends Result, T extends com.google.android.gms.internal.zzqc.zza<R, A>> T zzc(@NonNull T t) {
        if (!zze((com.google.android.gms.internal.zzqc.zza) t)) {
            return this.wW.zzc(t);
        }
        if (!zzare()) {
            return this.wX.zzc(t);
        }
        t.zzz(new Status(4, null, zzarf()));
        return t;
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, T extends com.google.android.gms.internal.zzqc.zza<? extends Result, A>> T zzd(@NonNull T t) {
        if (!zze((com.google.android.gms.internal.zzqc.zza) t)) {
            return this.wW.zzd(t);
        }
        if (!zzare()) {
            return this.wX.zzd(t);
        }
        t.zzz(new Status(4, null, zzarf()));
        return t;
    }
}
