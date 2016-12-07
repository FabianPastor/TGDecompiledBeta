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
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzf;
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

final class zzqt implements zzrm {
    private final Context mContext;
    private final zzrd yW;
    private final zzrf yX;
    private final zzrf yY;
    private final Map<zzc<?>, zzrf> yZ;
    private final Set<zzsa> za = Collections.newSetFromMap(new WeakHashMap());
    private final zze zb;
    private Bundle zc;
    private ConnectionResult zd = null;
    private ConnectionResult ze = null;
    private boolean zf = false;
    private final Lock zg;
    private int zh = 0;
    private final Looper zzajy;

    private class zza implements com.google.android.gms.internal.zzrm.zza {
        final /* synthetic */ zzqt zi;

        private zza(zzqt com_google_android_gms_internal_zzqt) {
            this.zi = com_google_android_gms_internal_zzqt;
        }

        public void zzc(int i, boolean z) {
            this.zi.zg.lock();
            try {
                if (this.zi.zf || this.zi.ze == null || !this.zi.ze.isSuccess()) {
                    this.zi.zf = false;
                    this.zi.zzb(i, z);
                    return;
                }
                this.zi.zf = true;
                this.zi.yY.onConnectionSuspended(i);
                this.zi.zg.unlock();
            } finally {
                this.zi.zg.unlock();
            }
        }

        public void zzc(@NonNull ConnectionResult connectionResult) {
            this.zi.zg.lock();
            try {
                this.zi.zd = connectionResult;
                this.zi.zzasc();
            } finally {
                this.zi.zg.unlock();
            }
        }

        public void zzn(@Nullable Bundle bundle) {
            this.zi.zg.lock();
            try {
                this.zi.zzm(bundle);
                this.zi.zd = ConnectionResult.wO;
                this.zi.zzasc();
            } finally {
                this.zi.zg.unlock();
            }
        }
    }

    private class zzb implements com.google.android.gms.internal.zzrm.zza {
        final /* synthetic */ zzqt zi;

        private zzb(zzqt com_google_android_gms_internal_zzqt) {
            this.zi = com_google_android_gms_internal_zzqt;
        }

        public void zzc(int i, boolean z) {
            this.zi.zg.lock();
            try {
                if (this.zi.zf) {
                    this.zi.zf = false;
                    this.zi.zzb(i, z);
                    return;
                }
                this.zi.zf = true;
                this.zi.yX.onConnectionSuspended(i);
                this.zi.zg.unlock();
            } finally {
                this.zi.zg.unlock();
            }
        }

        public void zzc(@NonNull ConnectionResult connectionResult) {
            this.zi.zg.lock();
            try {
                this.zi.ze = connectionResult;
                this.zi.zzasc();
            } finally {
                this.zi.zg.unlock();
            }
        }

        public void zzn(@Nullable Bundle bundle) {
            this.zi.zg.lock();
            try {
                this.zi.ze = ConnectionResult.wO;
                this.zi.zzasc();
            } finally {
                this.zi.zg.unlock();
            }
        }
    }

    private zzqt(Context context, zzrd com_google_android_gms_internal_zzrd, Lock lock, Looper looper, com.google.android.gms.common.zzc com_google_android_gms_common_zzc, Map<zzc<?>, zze> map, Map<zzc<?>, zze> map2, zzf com_google_android_gms_common_internal_zzf, com.google.android.gms.common.api.Api.zza<? extends zzxp, zzxq> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzxp__com_google_android_gms_internal_zzxq, zze com_google_android_gms_common_api_Api_zze, ArrayList<zzqr> arrayList, ArrayList<zzqr> arrayList2, Map<Api<?>, Integer> map3, Map<Api<?>, Integer> map4) {
        this.mContext = context;
        this.yW = com_google_android_gms_internal_zzrd;
        this.zg = lock;
        this.zzajy = looper;
        this.zb = com_google_android_gms_common_api_Api_zze;
        this.yX = new zzrf(context, this.yW, lock, looper, com_google_android_gms_common_zzc, map2, null, map4, null, arrayList2, new zza());
        this.yY = new zzrf(context, this.yW, lock, looper, com_google_android_gms_common_zzc, map, com_google_android_gms_common_internal_zzf, map3, com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzxp__com_google_android_gms_internal_zzxq, arrayList, new zzb());
        Map arrayMap = new ArrayMap();
        for (zzc put : map2.keySet()) {
            arrayMap.put(put, this.yX);
        }
        for (zzc put2 : map.keySet()) {
            arrayMap.put(put2, this.yY);
        }
        this.yZ = Collections.unmodifiableMap(arrayMap);
    }

    public static zzqt zza(Context context, zzrd com_google_android_gms_internal_zzrd, Lock lock, Looper looper, com.google.android.gms.common.zzc com_google_android_gms_common_zzc, Map<zzc<?>, zze> map, zzf com_google_android_gms_common_internal_zzf, Map<Api<?>, Integer> map2, com.google.android.gms.common.api.Api.zza<? extends zzxp, zzxq> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzxp__com_google_android_gms_internal_zzxq, ArrayList<zzqr> arrayList) {
        zze com_google_android_gms_common_api_Api_zze = null;
        Map arrayMap = new ArrayMap();
        Map arrayMap2 = new ArrayMap();
        for (Entry entry : map.entrySet()) {
            zze com_google_android_gms_common_api_Api_zze2 = (zze) entry.getValue();
            if (com_google_android_gms_common_api_Api_zze2.zzajc()) {
                com_google_android_gms_common_api_Api_zze = com_google_android_gms_common_api_Api_zze2;
            }
            if (com_google_android_gms_common_api_Api_zze2.zzain()) {
                arrayMap.put((zzc) entry.getKey(), com_google_android_gms_common_api_Api_zze2);
            } else {
                arrayMap2.put((zzc) entry.getKey(), com_google_android_gms_common_api_Api_zze2);
            }
        }
        zzaa.zza(!arrayMap.isEmpty(), (Object) "CompositeGoogleApiClient should not be used without any APIs that require sign-in.");
        Map arrayMap3 = new ArrayMap();
        Map arrayMap4 = new ArrayMap();
        for (Api api : map2.keySet()) {
            zzc zzaqv = api.zzaqv();
            if (arrayMap.containsKey(zzaqv)) {
                arrayMap3.put(api, (Integer) map2.get(api));
            } else if (arrayMap2.containsKey(zzaqv)) {
                arrayMap4.put(api, (Integer) map2.get(api));
            } else {
                throw new IllegalStateException("Each API in the apiTypeMap must have a corresponding client in the clients map.");
            }
        }
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            zzqr com_google_android_gms_internal_zzqr = (zzqr) it.next();
            if (arrayMap3.containsKey(com_google_android_gms_internal_zzqr.vS)) {
                arrayList2.add(com_google_android_gms_internal_zzqr);
            } else if (arrayMap4.containsKey(com_google_android_gms_internal_zzqr.vS)) {
                arrayList3.add(com_google_android_gms_internal_zzqr);
            } else {
                throw new IllegalStateException("Each ClientCallbacks must have a corresponding API in the apiTypeMap");
            }
        }
        return new zzqt(context, com_google_android_gms_internal_zzrd, lock, looper, com_google_android_gms_common_zzc, arrayMap, arrayMap2, com_google_android_gms_common_internal_zzf, com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzxp__com_google_android_gms_internal_zzxq, com_google_android_gms_common_api_Api_zze, arrayList2, arrayList3, arrayMap3, arrayMap4);
    }

    private void zza(ConnectionResult connectionResult) {
        switch (this.zh) {
            case 1:
                break;
            case 2:
                this.yW.zzc(connectionResult);
                break;
            default:
                Log.wtf("CompositeGAC", "Attempted to call failure callbacks in CONNECTION_MODE_NONE. Callbacks should be disabled via GmsClientSupervisor", new Exception());
                break;
        }
        zzase();
        this.zh = 0;
    }

    private void zzasb() {
        this.ze = null;
        this.zd = null;
        this.yX.connect();
        this.yY.connect();
    }

    private void zzasc() {
        if (zzb(this.zd)) {
            if (zzb(this.ze) || zzasf()) {
                zzasd();
            } else if (this.ze == null) {
            } else {
                if (this.zh == 1) {
                    zzase();
                    return;
                }
                zza(this.ze);
                this.yX.disconnect();
            }
        } else if (this.zd != null && zzb(this.ze)) {
            this.yY.disconnect();
            zza(this.zd);
        } else if (this.zd != null && this.ze != null) {
            ConnectionResult connectionResult = this.zd;
            if (this.yY.AB < this.yX.AB) {
                connectionResult = this.ze;
            }
            zza(connectionResult);
        }
    }

    private void zzasd() {
        switch (this.zh) {
            case 1:
                break;
            case 2:
                this.yW.zzn(this.zc);
                break;
            default:
                Log.wtf("CompositeGAC", "Attempted to call success callbacks in CONNECTION_MODE_NONE. Callbacks should be disabled via GmsClientSupervisor", new AssertionError());
                break;
        }
        zzase();
        this.zh = 0;
    }

    private void zzase() {
        for (zzsa zzajb : this.za) {
            zzajb.zzajb();
        }
        this.za.clear();
    }

    private boolean zzasf() {
        return this.ze != null && this.ze.getErrorCode() == 4;
    }

    @Nullable
    private PendingIntent zzasg() {
        return this.zb == null ? null : PendingIntent.getActivity(this.mContext, this.yW.getSessionId(), this.zb.zzajd(), 134217728);
    }

    private void zzb(int i, boolean z) {
        this.yW.zzc(i, z);
        this.ze = null;
        this.zd = null;
    }

    private static boolean zzb(ConnectionResult connectionResult) {
        return connectionResult != null && connectionResult.isSuccess();
    }

    private boolean zzc(com.google.android.gms.internal.zzqo.zza<? extends Result, ? extends com.google.android.gms.common.api.Api.zzb> com_google_android_gms_internal_zzqo_zza__extends_com_google_android_gms_common_api_Result___extends_com_google_android_gms_common_api_Api_zzb) {
        zzc zzaqv = com_google_android_gms_internal_zzqo_zza__extends_com_google_android_gms_common_api_Result___extends_com_google_android_gms_common_api_Api_zzb.zzaqv();
        zzaa.zzb(this.yZ.containsKey(zzaqv), (Object) "GoogleApiClient is not configured to use the API required for this call.");
        return ((zzrf) this.yZ.get(zzaqv)).equals(this.yY);
    }

    private void zzm(Bundle bundle) {
        if (this.zc == null) {
            this.zc = bundle;
        } else if (bundle != null) {
            this.zc.putAll(bundle);
        }
    }

    public ConnectionResult blockingConnect() {
        throw new UnsupportedOperationException();
    }

    public ConnectionResult blockingConnect(long j, @NonNull TimeUnit timeUnit) {
        throw new UnsupportedOperationException();
    }

    public void connect() {
        this.zh = 2;
        this.zf = false;
        zzasb();
    }

    public void disconnect() {
        this.ze = null;
        this.zd = null;
        this.zh = 0;
        this.yX.disconnect();
        this.yY.disconnect();
        zzase();
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.append(str).append("authClient").println(":");
        this.yY.dump(String.valueOf(str).concat("  "), fileDescriptor, printWriter, strArr);
        printWriter.append(str).append("anonClient").println(":");
        this.yX.dump(String.valueOf(str).concat("  "), fileDescriptor, printWriter, strArr);
    }

    @Nullable
    public ConnectionResult getConnectionResult(@NonNull Api<?> api) {
        return ((zzrf) this.yZ.get(api.zzaqv())).equals(this.yY) ? zzasf() ? new ConnectionResult(4, zzasg()) : this.yY.getConnectionResult(api) : this.yX.getConnectionResult(api);
    }

    public boolean isConnected() {
        boolean z = true;
        this.zg.lock();
        try {
            if (!(this.yX.isConnected() && (zzasa() || zzasf() || this.zh == 1))) {
                z = false;
            }
            this.zg.unlock();
            return z;
        } catch (Throwable th) {
            this.zg.unlock();
        }
    }

    public boolean isConnecting() {
        this.zg.lock();
        try {
            boolean z = this.zh == 2;
            this.zg.unlock();
            return z;
        } catch (Throwable th) {
            this.zg.unlock();
        }
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, R extends Result, T extends com.google.android.gms.internal.zzqo.zza<R, A>> T zza(@NonNull T t) {
        if (!zzc((com.google.android.gms.internal.zzqo.zza) t)) {
            return this.yX.zza((com.google.android.gms.internal.zzqo.zza) t);
        }
        if (!zzasf()) {
            return this.yY.zza((com.google.android.gms.internal.zzqo.zza) t);
        }
        t.zzaa(new Status(4, null, zzasg()));
        return t;
    }

    public boolean zza(zzsa com_google_android_gms_internal_zzsa) {
        this.zg.lock();
        try {
            if ((isConnecting() || isConnected()) && !zzasa()) {
                this.za.add(com_google_android_gms_internal_zzsa);
                if (this.zh == 0) {
                    this.zh = 1;
                }
                this.ze = null;
                this.yY.connect();
                return true;
            }
            this.zg.unlock();
            return false;
        } finally {
            this.zg.unlock();
        }
    }

    public void zzard() {
        this.zg.lock();
        try {
            boolean isConnecting = isConnecting();
            this.yY.disconnect();
            this.ze = new ConnectionResult(4);
            if (isConnecting) {
                new Handler(this.zzajy).post(new Runnable(this) {
                    final /* synthetic */ zzqt zi;

                    {
                        this.zi = r1;
                    }

                    public void run() {
                        this.zi.zg.lock();
                        try {
                            this.zi.zzasc();
                        } finally {
                            this.zi.zg.unlock();
                        }
                    }
                });
            } else {
                zzase();
            }
            this.zg.unlock();
        } catch (Throwable th) {
            this.zg.unlock();
        }
    }

    public void zzarz() {
        this.yX.zzarz();
        this.yY.zzarz();
    }

    public boolean zzasa() {
        return this.yY.isConnected();
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, T extends com.google.android.gms.internal.zzqo.zza<? extends Result, A>> T zzb(@NonNull T t) {
        if (!zzc((com.google.android.gms.internal.zzqo.zza) t)) {
            return this.yX.zzb((com.google.android.gms.internal.zzqo.zza) t);
        }
        if (!zzasf()) {
            return this.yY.zzb((com.google.android.gms.internal.zzqo.zza) t);
        }
        t.zzaa(new Status(4, null, zzasg()));
        return t;
    }
}
