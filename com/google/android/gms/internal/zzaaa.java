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
import com.google.android.gms.common.internal.zzg;
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

final class zzaaa implements zzaau {
    private final Context mContext;
    private final zzaal zzazd;
    private final zzaan zzaze;
    private final zzaan zzazf;
    private final Map<zzc<?>, zzaan> zzazg;
    private final Set<zzabi> zzazh = Collections.newSetFromMap(new WeakHashMap());
    private final zze zzazi;
    private Bundle zzazj;
    private ConnectionResult zzazk = null;
    private ConnectionResult zzazl = null;
    private boolean zzazm = false;
    private final Lock zzazn;
    private int zzazo = 0;
    private final Looper zzrx;

    private class zza implements com.google.android.gms.internal.zzaau.zza {
        final /* synthetic */ zzaaa zzazp;

        private zza(zzaaa com_google_android_gms_internal_zzaaa) {
            this.zzazp = com_google_android_gms_internal_zzaaa;
        }

        public void zzc(int i, boolean z) {
            this.zzazp.zzazn.lock();
            try {
                if (this.zzazp.zzazm || this.zzazp.zzazl == null || !this.zzazp.zzazl.isSuccess()) {
                    this.zzazp.zzazm = false;
                    this.zzazp.zzb(i, z);
                    return;
                }
                this.zzazp.zzazm = true;
                this.zzazp.zzazf.onConnectionSuspended(i);
                this.zzazp.zzazn.unlock();
            } finally {
                this.zzazp.zzazn.unlock();
            }
        }

        public void zzc(@NonNull ConnectionResult connectionResult) {
            this.zzazp.zzazn.lock();
            try {
                this.zzazp.zzazk = connectionResult;
                this.zzazp.zzvm();
            } finally {
                this.zzazp.zzazn.unlock();
            }
        }

        public void zzo(@Nullable Bundle bundle) {
            this.zzazp.zzazn.lock();
            try {
                this.zzazp.zzn(bundle);
                this.zzazp.zzazk = ConnectionResult.zzawX;
                this.zzazp.zzvm();
            } finally {
                this.zzazp.zzazn.unlock();
            }
        }
    }

    private class zzb implements com.google.android.gms.internal.zzaau.zza {
        final /* synthetic */ zzaaa zzazp;

        private zzb(zzaaa com_google_android_gms_internal_zzaaa) {
            this.zzazp = com_google_android_gms_internal_zzaaa;
        }

        public void zzc(int i, boolean z) {
            this.zzazp.zzazn.lock();
            try {
                if (this.zzazp.zzazm) {
                    this.zzazp.zzazm = false;
                    this.zzazp.zzb(i, z);
                    return;
                }
                this.zzazp.zzazm = true;
                this.zzazp.zzaze.onConnectionSuspended(i);
                this.zzazp.zzazn.unlock();
            } finally {
                this.zzazp.zzazn.unlock();
            }
        }

        public void zzc(@NonNull ConnectionResult connectionResult) {
            this.zzazp.zzazn.lock();
            try {
                this.zzazp.zzazl = connectionResult;
                this.zzazp.zzvm();
            } finally {
                this.zzazp.zzazn.unlock();
            }
        }

        public void zzo(@Nullable Bundle bundle) {
            this.zzazp.zzazn.lock();
            try {
                this.zzazp.zzazl = ConnectionResult.zzawX;
                this.zzazp.zzvm();
            } finally {
                this.zzazp.zzazn.unlock();
            }
        }
    }

    private zzaaa(Context context, zzaal com_google_android_gms_internal_zzaal, Lock lock, Looper looper, com.google.android.gms.common.zzc com_google_android_gms_common_zzc, Map<zzc<?>, zze> map, Map<zzc<?>, zze> map2, zzg com_google_android_gms_common_internal_zzg, com.google.android.gms.common.api.Api.zza<? extends zzaxn, zzaxo> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzaxn__com_google_android_gms_internal_zzaxo, zze com_google_android_gms_common_api_Api_zze, ArrayList<zzzy> arrayList, ArrayList<zzzy> arrayList2, Map<Api<?>, Integer> map3, Map<Api<?>, Integer> map4) {
        this.mContext = context;
        this.zzazd = com_google_android_gms_internal_zzaal;
        this.zzazn = lock;
        this.zzrx = looper;
        this.zzazi = com_google_android_gms_common_api_Api_zze;
        this.zzaze = new zzaan(context, this.zzazd, lock, looper, com_google_android_gms_common_zzc, map2, null, map4, null, arrayList2, new zza());
        this.zzazf = new zzaan(context, this.zzazd, lock, looper, com_google_android_gms_common_zzc, map, com_google_android_gms_common_internal_zzg, map3, com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzaxn__com_google_android_gms_internal_zzaxo, arrayList, new zzb());
        Map arrayMap = new ArrayMap();
        for (zzc put : map2.keySet()) {
            arrayMap.put(put, this.zzaze);
        }
        for (zzc put2 : map.keySet()) {
            arrayMap.put(put2, this.zzazf);
        }
        this.zzazg = Collections.unmodifiableMap(arrayMap);
    }

    public static zzaaa zza(Context context, zzaal com_google_android_gms_internal_zzaal, Lock lock, Looper looper, com.google.android.gms.common.zzc com_google_android_gms_common_zzc, Map<zzc<?>, zze> map, zzg com_google_android_gms_common_internal_zzg, Map<Api<?>, Integer> map2, com.google.android.gms.common.api.Api.zza<? extends zzaxn, zzaxo> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzaxn__com_google_android_gms_internal_zzaxo, ArrayList<zzzy> arrayList) {
        zze com_google_android_gms_common_api_Api_zze = null;
        Map arrayMap = new ArrayMap();
        Map arrayMap2 = new ArrayMap();
        for (Entry entry : map.entrySet()) {
            zze com_google_android_gms_common_api_Api_zze2 = (zze) entry.getValue();
            if (com_google_android_gms_common_api_Api_zze2.zzqS()) {
                com_google_android_gms_common_api_Api_zze = com_google_android_gms_common_api_Api_zze2;
            }
            if (com_google_android_gms_common_api_Api_zze2.zzqD()) {
                arrayMap.put((zzc) entry.getKey(), com_google_android_gms_common_api_Api_zze2);
            } else {
                arrayMap2.put((zzc) entry.getKey(), com_google_android_gms_common_api_Api_zze2);
            }
        }
        zzac.zza(!arrayMap.isEmpty(), (Object) "CompositeGoogleApiClient should not be used without any APIs that require sign-in.");
        Map arrayMap3 = new ArrayMap();
        Map arrayMap4 = new ArrayMap();
        for (Api api : map2.keySet()) {
            zzc zzuH = api.zzuH();
            if (arrayMap.containsKey(zzuH)) {
                arrayMap3.put(api, (Integer) map2.get(api));
            } else if (arrayMap2.containsKey(zzuH)) {
                arrayMap4.put(api, (Integer) map2.get(api));
            } else {
                throw new IllegalStateException("Each API in the apiTypeMap must have a corresponding client in the clients map.");
            }
        }
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            zzzy com_google_android_gms_internal_zzzy = (zzzy) it.next();
            if (arrayMap3.containsKey(com_google_android_gms_internal_zzzy.zzawb)) {
                arrayList2.add(com_google_android_gms_internal_zzzy);
            } else if (arrayMap4.containsKey(com_google_android_gms_internal_zzzy.zzawb)) {
                arrayList3.add(com_google_android_gms_internal_zzzy);
            } else {
                throw new IllegalStateException("Each ClientCallbacks must have a corresponding API in the apiTypeMap");
            }
        }
        return new zzaaa(context, com_google_android_gms_internal_zzaal, lock, looper, com_google_android_gms_common_zzc, arrayMap, arrayMap2, com_google_android_gms_common_internal_zzg, com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzaxn__com_google_android_gms_internal_zzaxo, com_google_android_gms_common_api_Api_zze, arrayList2, arrayList3, arrayMap3, arrayMap4);
    }

    private void zza(ConnectionResult connectionResult) {
        switch (this.zzazo) {
            case 1:
                break;
            case 2:
                this.zzazd.zzc(connectionResult);
                break;
            default:
                Log.wtf("CompositeGAC", "Attempted to call failure callbacks in CONNECTION_MODE_NONE. Callbacks should be disabled via GmsClientSupervisor", new Exception());
                break;
        }
        zzvo();
        this.zzazo = 0;
    }

    private void zzb(int i, boolean z) {
        this.zzazd.zzc(i, z);
        this.zzazl = null;
        this.zzazk = null;
    }

    private static boolean zzb(ConnectionResult connectionResult) {
        return connectionResult != null && connectionResult.isSuccess();
    }

    private boolean zzc(com.google.android.gms.internal.zzzv.zza<? extends Result, ? extends com.google.android.gms.common.api.Api.zzb> com_google_android_gms_internal_zzzv_zza__extends_com_google_android_gms_common_api_Result___extends_com_google_android_gms_common_api_Api_zzb) {
        zzc zzuH = com_google_android_gms_internal_zzzv_zza__extends_com_google_android_gms_common_api_Result___extends_com_google_android_gms_common_api_Api_zzb.zzuH();
        zzac.zzb(this.zzazg.containsKey(zzuH), (Object) "GoogleApiClient is not configured to use the API required for this call.");
        return ((zzaan) this.zzazg.get(zzuH)).equals(this.zzazf);
    }

    private void zzn(Bundle bundle) {
        if (this.zzazj == null) {
            this.zzazj = bundle;
        } else if (bundle != null) {
            this.zzazj.putAll(bundle);
        }
    }

    private void zzvl() {
        this.zzazl = null;
        this.zzazk = null;
        this.zzaze.connect();
        this.zzazf.connect();
    }

    private void zzvm() {
        if (zzb(this.zzazk)) {
            if (zzb(this.zzazl) || zzvp()) {
                zzvn();
            } else if (this.zzazl == null) {
            } else {
                if (this.zzazo == 1) {
                    zzvo();
                    return;
                }
                zza(this.zzazl);
                this.zzaze.disconnect();
            }
        } else if (this.zzazk != null && zzb(this.zzazl)) {
            this.zzazf.disconnect();
            zza(this.zzazk);
        } else if (this.zzazk != null && this.zzazl != null) {
            ConnectionResult connectionResult = this.zzazk;
            if (this.zzazf.zzaAJ < this.zzaze.zzaAJ) {
                connectionResult = this.zzazl;
            }
            zza(connectionResult);
        }
    }

    private void zzvn() {
        switch (this.zzazo) {
            case 1:
                break;
            case 2:
                this.zzazd.zzo(this.zzazj);
                break;
            default:
                Log.wtf("CompositeGAC", "Attempted to call success callbacks in CONNECTION_MODE_NONE. Callbacks should be disabled via GmsClientSupervisor", new AssertionError());
                break;
        }
        zzvo();
        this.zzazo = 0;
    }

    private void zzvo() {
        for (zzabi zzqR : this.zzazh) {
            zzqR.zzqR();
        }
        this.zzazh.clear();
    }

    private boolean zzvp() {
        return this.zzazl != null && this.zzazl.getErrorCode() == 4;
    }

    @Nullable
    private PendingIntent zzvq() {
        return this.zzazi == null ? null : PendingIntent.getActivity(this.mContext, this.zzazd.getSessionId(), this.zzazi.zzqT(), 134217728);
    }

    public ConnectionResult blockingConnect() {
        throw new UnsupportedOperationException();
    }

    public ConnectionResult blockingConnect(long j, @NonNull TimeUnit timeUnit) {
        throw new UnsupportedOperationException();
    }

    public void connect() {
        this.zzazo = 2;
        this.zzazm = false;
        zzvl();
    }

    public void disconnect() {
        this.zzazl = null;
        this.zzazk = null;
        this.zzazo = 0;
        this.zzaze.disconnect();
        this.zzazf.disconnect();
        zzvo();
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.append(str).append("authClient").println(":");
        this.zzazf.dump(String.valueOf(str).concat("  "), fileDescriptor, printWriter, strArr);
        printWriter.append(str).append("anonClient").println(":");
        this.zzaze.dump(String.valueOf(str).concat("  "), fileDescriptor, printWriter, strArr);
    }

    @Nullable
    public ConnectionResult getConnectionResult(@NonNull Api<?> api) {
        return ((zzaan) this.zzazg.get(api.zzuH())).equals(this.zzazf) ? zzvp() ? new ConnectionResult(4, zzvq()) : this.zzazf.getConnectionResult(api) : this.zzaze.getConnectionResult(api);
    }

    public boolean isConnected() {
        boolean z = true;
        this.zzazn.lock();
        try {
            if (!(this.zzaze.isConnected() && (zzvk() || zzvp() || this.zzazo == 1))) {
                z = false;
            }
            this.zzazn.unlock();
            return z;
        } catch (Throwable th) {
            this.zzazn.unlock();
        }
    }

    public boolean isConnecting() {
        this.zzazn.lock();
        try {
            boolean z = this.zzazo == 2;
            this.zzazn.unlock();
            return z;
        } catch (Throwable th) {
            this.zzazn.unlock();
        }
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, R extends Result, T extends com.google.android.gms.internal.zzzv.zza<R, A>> T zza(@NonNull T t) {
        if (!zzc((com.google.android.gms.internal.zzzv.zza) t)) {
            return this.zzaze.zza((com.google.android.gms.internal.zzzv.zza) t);
        }
        if (!zzvp()) {
            return this.zzazf.zza((com.google.android.gms.internal.zzzv.zza) t);
        }
        t.zzA(new Status(4, null, zzvq()));
        return t;
    }

    public boolean zza(zzabi com_google_android_gms_internal_zzabi) {
        this.zzazn.lock();
        try {
            if ((isConnecting() || isConnected()) && !zzvk()) {
                this.zzazh.add(com_google_android_gms_internal_zzabi);
                if (this.zzazo == 0) {
                    this.zzazo = 1;
                }
                this.zzazl = null;
                this.zzazf.connect();
                return true;
            }
            this.zzazn.unlock();
            return false;
        } finally {
            this.zzazn.unlock();
        }
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, T extends com.google.android.gms.internal.zzzv.zza<? extends Result, A>> T zzb(@NonNull T t) {
        if (!zzc((com.google.android.gms.internal.zzzv.zza) t)) {
            return this.zzaze.zzb((com.google.android.gms.internal.zzzv.zza) t);
        }
        if (!zzvp()) {
            return this.zzazf.zzb((com.google.android.gms.internal.zzzv.zza) t);
        }
        t.zzA(new Status(4, null, zzvq()));
        return t;
    }

    public void zzuN() {
        this.zzazn.lock();
        try {
            boolean isConnecting = isConnecting();
            this.zzazf.disconnect();
            this.zzazl = new ConnectionResult(4);
            if (isConnecting) {
                new Handler(this.zzrx).post(new Runnable(this) {
                    final /* synthetic */ zzaaa zzazp;

                    {
                        this.zzazp = r1;
                    }

                    public void run() {
                        this.zzazp.zzazn.lock();
                        try {
                            this.zzazp.zzvm();
                        } finally {
                            this.zzazp.zzazn.unlock();
                        }
                    }
                });
            } else {
                zzvo();
            }
            this.zzazn.unlock();
        } catch (Throwable th) {
            this.zzazn.unlock();
        }
    }

    public void zzvj() {
        this.zzaze.zzvj();
        this.zzazf.zzvj();
    }

    public boolean zzvk() {
        return this.zzazf.isConnected();
    }
}
