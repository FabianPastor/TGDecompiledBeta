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

final class zzaai implements zzabc {
    private final Context mContext;
    private final Set<zzabq> zzaAA = Collections.newSetFromMap(new WeakHashMap());
    private final zze zzaAB;
    private Bundle zzaAC;
    private ConnectionResult zzaAD = null;
    private ConnectionResult zzaAE = null;
    private boolean zzaAF = false;
    private final Lock zzaAG;
    private int zzaAH = 0;
    private final zzaat zzaAw;
    private final zzaav zzaAx;
    private final zzaav zzaAy;
    private final Map<zzc<?>, zzaav> zzaAz;
    private final Looper zzrs;

    private class zza implements com.google.android.gms.internal.zzabc.zza {
        final /* synthetic */ zzaai zzaAI;

        private zza(zzaai com_google_android_gms_internal_zzaai) {
            this.zzaAI = com_google_android_gms_internal_zzaai;
        }

        public void zzc(int i, boolean z) {
            this.zzaAI.zzaAG.lock();
            try {
                if (this.zzaAI.zzaAF || this.zzaAI.zzaAE == null || !this.zzaAI.zzaAE.isSuccess()) {
                    this.zzaAI.zzaAF = false;
                    this.zzaAI.zzb(i, z);
                    return;
                }
                this.zzaAI.zzaAF = true;
                this.zzaAI.zzaAy.onConnectionSuspended(i);
                this.zzaAI.zzaAG.unlock();
            } finally {
                this.zzaAI.zzaAG.unlock();
            }
        }

        public void zzc(@NonNull ConnectionResult connectionResult) {
            this.zzaAI.zzaAG.lock();
            try {
                this.zzaAI.zzaAD = connectionResult;
                this.zzaAI.zzvP();
            } finally {
                this.zzaAI.zzaAG.unlock();
            }
        }

        public void zzo(@Nullable Bundle bundle) {
            this.zzaAI.zzaAG.lock();
            try {
                this.zzaAI.zzn(bundle);
                this.zzaAI.zzaAD = ConnectionResult.zzayj;
                this.zzaAI.zzvP();
            } finally {
                this.zzaAI.zzaAG.unlock();
            }
        }
    }

    private class zzb implements com.google.android.gms.internal.zzabc.zza {
        final /* synthetic */ zzaai zzaAI;

        private zzb(zzaai com_google_android_gms_internal_zzaai) {
            this.zzaAI = com_google_android_gms_internal_zzaai;
        }

        public void zzc(int i, boolean z) {
            this.zzaAI.zzaAG.lock();
            try {
                if (this.zzaAI.zzaAF) {
                    this.zzaAI.zzaAF = false;
                    this.zzaAI.zzb(i, z);
                    return;
                }
                this.zzaAI.zzaAF = true;
                this.zzaAI.zzaAx.onConnectionSuspended(i);
                this.zzaAI.zzaAG.unlock();
            } finally {
                this.zzaAI.zzaAG.unlock();
            }
        }

        public void zzc(@NonNull ConnectionResult connectionResult) {
            this.zzaAI.zzaAG.lock();
            try {
                this.zzaAI.zzaAE = connectionResult;
                this.zzaAI.zzvP();
            } finally {
                this.zzaAI.zzaAG.unlock();
            }
        }

        public void zzo(@Nullable Bundle bundle) {
            this.zzaAI.zzaAG.lock();
            try {
                this.zzaAI.zzaAE = ConnectionResult.zzayj;
                this.zzaAI.zzvP();
            } finally {
                this.zzaAI.zzaAG.unlock();
            }
        }
    }

    private zzaai(Context context, zzaat com_google_android_gms_internal_zzaat, Lock lock, Looper looper, com.google.android.gms.common.zze com_google_android_gms_common_zze, Map<zzc<?>, zze> map, Map<zzc<?>, zze> map2, zzg com_google_android_gms_common_internal_zzg, com.google.android.gms.common.api.Api.zza<? extends zzbai, zzbaj> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzbai__com_google_android_gms_internal_zzbaj, zze com_google_android_gms_common_api_Api_zze, ArrayList<zzaag> arrayList, ArrayList<zzaag> arrayList2, Map<Api<?>, Boolean> map3, Map<Api<?>, Boolean> map4) {
        this.mContext = context;
        this.zzaAw = com_google_android_gms_internal_zzaat;
        this.zzaAG = lock;
        this.zzrs = looper;
        this.zzaAB = com_google_android_gms_common_api_Api_zze;
        this.zzaAx = new zzaav(context, this.zzaAw, lock, looper, com_google_android_gms_common_zze, map2, null, map4, null, arrayList2, new zza());
        this.zzaAy = new zzaav(context, this.zzaAw, lock, looper, com_google_android_gms_common_zze, map, com_google_android_gms_common_internal_zzg, map3, com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzbai__com_google_android_gms_internal_zzbaj, arrayList, new zzb());
        Map arrayMap = new ArrayMap();
        for (zzc put : map2.keySet()) {
            arrayMap.put(put, this.zzaAx);
        }
        for (zzc put2 : map.keySet()) {
            arrayMap.put(put2, this.zzaAy);
        }
        this.zzaAz = Collections.unmodifiableMap(arrayMap);
    }

    public static zzaai zza(Context context, zzaat com_google_android_gms_internal_zzaat, Lock lock, Looper looper, com.google.android.gms.common.zze com_google_android_gms_common_zze, Map<zzc<?>, zze> map, zzg com_google_android_gms_common_internal_zzg, Map<Api<?>, Boolean> map2, com.google.android.gms.common.api.Api.zza<? extends zzbai, zzbaj> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzbai__com_google_android_gms_internal_zzbaj, ArrayList<zzaag> arrayList) {
        zze com_google_android_gms_common_api_Api_zze = null;
        Map arrayMap = new ArrayMap();
        Map arrayMap2 = new ArrayMap();
        for (Entry entry : map.entrySet()) {
            zze com_google_android_gms_common_api_Api_zze2 = (zze) entry.getValue();
            if (com_google_android_gms_common_api_Api_zze2.zzrr()) {
                com_google_android_gms_common_api_Api_zze = com_google_android_gms_common_api_Api_zze2;
            }
            if (com_google_android_gms_common_api_Api_zze2.zzrd()) {
                arrayMap.put((zzc) entry.getKey(), com_google_android_gms_common_api_Api_zze2);
            } else {
                arrayMap2.put((zzc) entry.getKey(), com_google_android_gms_common_api_Api_zze2);
            }
        }
        zzac.zza(!arrayMap.isEmpty(), (Object) "CompositeGoogleApiClient should not be used without any APIs that require sign-in.");
        Map arrayMap3 = new ArrayMap();
        Map arrayMap4 = new ArrayMap();
        for (Api api : map2.keySet()) {
            zzc zzvg = api.zzvg();
            if (arrayMap.containsKey(zzvg)) {
                arrayMap3.put(api, (Boolean) map2.get(api));
            } else if (arrayMap2.containsKey(zzvg)) {
                arrayMap4.put(api, (Boolean) map2.get(api));
            } else {
                throw new IllegalStateException("Each API in the isOptionalMap must have a corresponding client in the clients map.");
            }
        }
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            zzaag com_google_android_gms_internal_zzaag = (zzaag) it.next();
            if (arrayMap3.containsKey(com_google_android_gms_internal_zzaag.zzaxf)) {
                arrayList2.add(com_google_android_gms_internal_zzaag);
            } else if (arrayMap4.containsKey(com_google_android_gms_internal_zzaag.zzaxf)) {
                arrayList3.add(com_google_android_gms_internal_zzaag);
            } else {
                throw new IllegalStateException("Each ClientCallbacks must have a corresponding API in the isOptionalMap");
            }
        }
        return new zzaai(context, com_google_android_gms_internal_zzaat, lock, looper, com_google_android_gms_common_zze, arrayMap, arrayMap2, com_google_android_gms_common_internal_zzg, com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzbai__com_google_android_gms_internal_zzbaj, com_google_android_gms_common_api_Api_zze, arrayList2, arrayList3, arrayMap3, arrayMap4);
    }

    private void zza(ConnectionResult connectionResult) {
        switch (this.zzaAH) {
            case 1:
                break;
            case 2:
                this.zzaAw.zzc(connectionResult);
                break;
            default:
                Log.wtf("CompositeGAC", "Attempted to call failure callbacks in CONNECTION_MODE_NONE. Callbacks should be disabled via GmsClientSupervisor", new Exception());
                break;
        }
        zzvR();
        this.zzaAH = 0;
    }

    private void zzb(int i, boolean z) {
        this.zzaAw.zzc(i, z);
        this.zzaAE = null;
        this.zzaAD = null;
    }

    private static boolean zzb(ConnectionResult connectionResult) {
        return connectionResult != null && connectionResult.isSuccess();
    }

    private boolean zzc(com.google.android.gms.internal.zzaad.zza<? extends Result, ? extends com.google.android.gms.common.api.Api.zzb> com_google_android_gms_internal_zzaad_zza__extends_com_google_android_gms_common_api_Result___extends_com_google_android_gms_common_api_Api_zzb) {
        zzc zzvg = com_google_android_gms_internal_zzaad_zza__extends_com_google_android_gms_common_api_Result___extends_com_google_android_gms_common_api_Api_zzb.zzvg();
        zzac.zzb(this.zzaAz.containsKey(zzvg), (Object) "GoogleApiClient is not configured to use the API required for this call.");
        return ((zzaav) this.zzaAz.get(zzvg)).equals(this.zzaAy);
    }

    private void zzn(Bundle bundle) {
        if (this.zzaAC == null) {
            this.zzaAC = bundle;
        } else if (bundle != null) {
            this.zzaAC.putAll(bundle);
        }
    }

    private void zzvO() {
        this.zzaAE = null;
        this.zzaAD = null;
        this.zzaAx.connect();
        this.zzaAy.connect();
    }

    private void zzvP() {
        if (zzb(this.zzaAD)) {
            if (zzb(this.zzaAE) || zzvS()) {
                zzvQ();
            } else if (this.zzaAE == null) {
            } else {
                if (this.zzaAH == 1) {
                    zzvR();
                    return;
                }
                zza(this.zzaAE);
                this.zzaAx.disconnect();
            }
        } else if (this.zzaAD != null && zzb(this.zzaAE)) {
            this.zzaAy.disconnect();
            zza(this.zzaAD);
        } else if (this.zzaAD != null && this.zzaAE != null) {
            ConnectionResult connectionResult = this.zzaAD;
            if (this.zzaAy.zzaCi < this.zzaAx.zzaCi) {
                connectionResult = this.zzaAE;
            }
            zza(connectionResult);
        }
    }

    private void zzvQ() {
        switch (this.zzaAH) {
            case 1:
                break;
            case 2:
                this.zzaAw.zzo(this.zzaAC);
                break;
            default:
                Log.wtf("CompositeGAC", "Attempted to call success callbacks in CONNECTION_MODE_NONE. Callbacks should be disabled via GmsClientSupervisor", new AssertionError());
                break;
        }
        zzvR();
        this.zzaAH = 0;
    }

    private void zzvR() {
        for (zzabq zzrq : this.zzaAA) {
            zzrq.zzrq();
        }
        this.zzaAA.clear();
    }

    private boolean zzvS() {
        return this.zzaAE != null && this.zzaAE.getErrorCode() == 4;
    }

    @Nullable
    private PendingIntent zzvT() {
        return this.zzaAB == null ? null : PendingIntent.getActivity(this.mContext, this.zzaAw.getSessionId(), this.zzaAB.zzrs(), 134217728);
    }

    public ConnectionResult blockingConnect() {
        throw new UnsupportedOperationException();
    }

    public ConnectionResult blockingConnect(long j, @NonNull TimeUnit timeUnit) {
        throw new UnsupportedOperationException();
    }

    public void connect() {
        this.zzaAH = 2;
        this.zzaAF = false;
        zzvO();
    }

    public void disconnect() {
        this.zzaAE = null;
        this.zzaAD = null;
        this.zzaAH = 0;
        this.zzaAx.disconnect();
        this.zzaAy.disconnect();
        zzvR();
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.append(str).append("authClient").println(":");
        this.zzaAy.dump(String.valueOf(str).concat("  "), fileDescriptor, printWriter, strArr);
        printWriter.append(str).append("anonClient").println(":");
        this.zzaAx.dump(String.valueOf(str).concat("  "), fileDescriptor, printWriter, strArr);
    }

    @Nullable
    public ConnectionResult getConnectionResult(@NonNull Api<?> api) {
        return ((zzaav) this.zzaAz.get(api.zzvg())).equals(this.zzaAy) ? zzvS() ? new ConnectionResult(4, zzvT()) : this.zzaAy.getConnectionResult(api) : this.zzaAx.getConnectionResult(api);
    }

    public boolean isConnected() {
        boolean z = true;
        this.zzaAG.lock();
        try {
            if (!(this.zzaAx.isConnected() && (zzvN() || zzvS() || this.zzaAH == 1))) {
                z = false;
            }
            this.zzaAG.unlock();
            return z;
        } catch (Throwable th) {
            this.zzaAG.unlock();
        }
    }

    public boolean isConnecting() {
        this.zzaAG.lock();
        try {
            boolean z = this.zzaAH == 2;
            this.zzaAG.unlock();
            return z;
        } catch (Throwable th) {
            this.zzaAG.unlock();
        }
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, R extends Result, T extends com.google.android.gms.internal.zzaad.zza<R, A>> T zza(@NonNull T t) {
        if (!zzc((com.google.android.gms.internal.zzaad.zza) t)) {
            return this.zzaAx.zza((com.google.android.gms.internal.zzaad.zza) t);
        }
        if (!zzvS()) {
            return this.zzaAy.zza((com.google.android.gms.internal.zzaad.zza) t);
        }
        t.zzB(new Status(4, null, zzvT()));
        return t;
    }

    public boolean zza(zzabq com_google_android_gms_internal_zzabq) {
        this.zzaAG.lock();
        try {
            if ((isConnecting() || isConnected()) && !zzvN()) {
                this.zzaAA.add(com_google_android_gms_internal_zzabq);
                if (this.zzaAH == 0) {
                    this.zzaAH = 1;
                }
                this.zzaAE = null;
                this.zzaAy.connect();
                return true;
            }
            this.zzaAG.unlock();
            return false;
        } finally {
            this.zzaAG.unlock();
        }
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, T extends com.google.android.gms.internal.zzaad.zza<? extends Result, A>> T zzb(@NonNull T t) {
        if (!zzc((com.google.android.gms.internal.zzaad.zza) t)) {
            return this.zzaAx.zzb((com.google.android.gms.internal.zzaad.zza) t);
        }
        if (!zzvS()) {
            return this.zzaAy.zzb((com.google.android.gms.internal.zzaad.zza) t);
        }
        t.zzB(new Status(4, null, zzvT()));
        return t;
    }

    public void zzvM() {
        this.zzaAx.zzvM();
        this.zzaAy.zzvM();
    }

    public boolean zzvN() {
        return this.zzaAy.isConnected();
    }

    public void zzvn() {
        this.zzaAG.lock();
        try {
            boolean isConnecting = isConnecting();
            this.zzaAy.disconnect();
            this.zzaAE = new ConnectionResult(4);
            if (isConnecting) {
                new Handler(this.zzrs).post(new Runnable(this) {
                    final /* synthetic */ zzaai zzaAI;

                    {
                        this.zzaAI = r1;
                    }

                    public void run() {
                        this.zzaAI.zzaAG.lock();
                        try {
                            this.zzaAI.zzvP();
                        } finally {
                            this.zzaAI.zzaAG.unlock();
                        }
                    }
                });
            } else {
                zzvR();
            }
            this.zzaAG.unlock();
        } catch (Throwable th) {
            this.zzaAG.unlock();
        }
    }
}
