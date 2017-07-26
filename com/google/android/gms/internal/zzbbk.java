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
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzq;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

final class zzbbk implements zzbdp {
    private final Context mContext;
    private final zzbcp zzaCl;
    private final zzbcx zzaCm;
    private final zzbcx zzaCn;
    private final Map<zzc<?>, zzbcx> zzaCo;
    private final Set<zzbei> zzaCp = Collections.newSetFromMap(new WeakHashMap());
    private final zze zzaCq;
    private Bundle zzaCr;
    private ConnectionResult zzaCs = null;
    private ConnectionResult zzaCt = null;
    private boolean zzaCu = false;
    private final Lock zzaCv;
    private int zzaCw = 0;
    private final Looper zzrM;

    private zzbbk(Context context, zzbcp com_google_android_gms_internal_zzbcp, Lock lock, Looper looper, com.google.android.gms.common.zze com_google_android_gms_common_zze, Map<zzc<?>, zze> map, Map<zzc<?>, zze> map2, zzq com_google_android_gms_common_internal_zzq, zza<? extends zzctk, zzctl> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzctk__com_google_android_gms_internal_zzctl, zze com_google_android_gms_common_api_Api_zze, ArrayList<zzbbi> arrayList, ArrayList<zzbbi> arrayList2, Map<Api<?>, Boolean> map3, Map<Api<?>, Boolean> map4) {
        this.mContext = context;
        this.zzaCl = com_google_android_gms_internal_zzbcp;
        this.zzaCv = lock;
        this.zzrM = looper;
        this.zzaCq = com_google_android_gms_common_api_Api_zze;
        this.zzaCm = new zzbcx(context, this.zzaCl, lock, looper, com_google_android_gms_common_zze, map2, null, map4, null, arrayList2, new zzbbm());
        this.zzaCn = new zzbcx(context, this.zzaCl, lock, looper, com_google_android_gms_common_zze, map, com_google_android_gms_common_internal_zzq, map3, com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzctk__com_google_android_gms_internal_zzctl, arrayList, new zzbbn());
        Map arrayMap = new ArrayMap();
        for (zzc put : map2.keySet()) {
            arrayMap.put(put, this.zzaCm);
        }
        for (zzc put2 : map.keySet()) {
            arrayMap.put(put2, this.zzaCn);
        }
        this.zzaCo = Collections.unmodifiableMap(arrayMap);
    }

    public static zzbbk zza(Context context, zzbcp com_google_android_gms_internal_zzbcp, Lock lock, Looper looper, com.google.android.gms.common.zze com_google_android_gms_common_zze, Map<zzc<?>, zze> map, zzq com_google_android_gms_common_internal_zzq, Map<Api<?>, Boolean> map2, zza<? extends zzctk, zzctl> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzctk__com_google_android_gms_internal_zzctl, ArrayList<zzbbi> arrayList) {
        zze com_google_android_gms_common_api_Api_zze = null;
        Map arrayMap = new ArrayMap();
        Map arrayMap2 = new ArrayMap();
        for (Entry entry : map.entrySet()) {
            zze com_google_android_gms_common_api_Api_zze2 = (zze) entry.getValue();
            if (com_google_android_gms_common_api_Api_zze2.zzmG()) {
                com_google_android_gms_common_api_Api_zze = com_google_android_gms_common_api_Api_zze2;
            }
            if (com_google_android_gms_common_api_Api_zze2.zzmv()) {
                arrayMap.put((zzc) entry.getKey(), com_google_android_gms_common_api_Api_zze2);
            } else {
                arrayMap2.put((zzc) entry.getKey(), com_google_android_gms_common_api_Api_zze2);
            }
        }
        zzbo.zza(!arrayMap.isEmpty(), (Object) "CompositeGoogleApiClient should not be used without any APIs that require sign-in.");
        Map arrayMap3 = new ArrayMap();
        Map arrayMap4 = new ArrayMap();
        for (Api api : map2.keySet()) {
            zzc zzpd = api.zzpd();
            if (arrayMap.containsKey(zzpd)) {
                arrayMap3.put(api, (Boolean) map2.get(api));
            } else if (arrayMap2.containsKey(zzpd)) {
                arrayMap4.put(api, (Boolean) map2.get(api));
            } else {
                throw new IllegalStateException("Each API in the isOptionalMap must have a corresponding client in the clients map.");
            }
        }
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = arrayList;
        int size = arrayList4.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList4.get(i);
            i++;
            zzbbi com_google_android_gms_internal_zzbbi = (zzbbi) obj;
            if (arrayMap3.containsKey(com_google_android_gms_internal_zzbbi.zzayW)) {
                arrayList2.add(com_google_android_gms_internal_zzbbi);
            } else if (arrayMap4.containsKey(com_google_android_gms_internal_zzbbi.zzayW)) {
                arrayList3.add(com_google_android_gms_internal_zzbbi);
            } else {
                throw new IllegalStateException("Each ClientCallbacks must have a corresponding API in the isOptionalMap");
            }
        }
        return new zzbbk(context, com_google_android_gms_internal_zzbcp, lock, looper, com_google_android_gms_common_zze, arrayMap, arrayMap2, com_google_android_gms_common_internal_zzq, com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzctk__com_google_android_gms_internal_zzctl, com_google_android_gms_common_api_Api_zze, arrayList2, arrayList3, arrayMap3, arrayMap4);
    }

    private final void zza(ConnectionResult connectionResult) {
        switch (this.zzaCw) {
            case 1:
                break;
            case 2:
                this.zzaCl.zzc(connectionResult);
                break;
            default:
                Log.wtf("CompositeGAC", "Attempted to call failure callbacks in CONNECTION_MODE_NONE. Callbacks should be disabled via GmsClientSupervisor", new Exception());
                break;
        }
        zzpG();
        this.zzaCw = 0;
    }

    private static boolean zzb(ConnectionResult connectionResult) {
        return connectionResult != null && connectionResult.isSuccess();
    }

    private final void zzd(int i, boolean z) {
        this.zzaCl.zze(i, z);
        this.zzaCt = null;
        this.zzaCs = null;
    }

    private final boolean zzf(zzbay<? extends Result, ? extends zzb> com_google_android_gms_internal_zzbay__extends_com_google_android_gms_common_api_Result___extends_com_google_android_gms_common_api_Api_zzb) {
        zzc zzpd = com_google_android_gms_internal_zzbay__extends_com_google_android_gms_common_api_Result___extends_com_google_android_gms_common_api_Api_zzb.zzpd();
        zzbo.zzb(this.zzaCo.containsKey(zzpd), (Object) "GoogleApiClient is not configured to use the API required for this call.");
        return ((zzbcx) this.zzaCo.get(zzpd)).equals(this.zzaCn);
    }

    private final void zzl(Bundle bundle) {
        if (this.zzaCr == null) {
            this.zzaCr = bundle;
        } else if (bundle != null) {
            this.zzaCr.putAll(bundle);
        }
    }

    private final void zzpF() {
        if (zzb(this.zzaCs)) {
            if (zzb(this.zzaCt) || zzpH()) {
                switch (this.zzaCw) {
                    case 1:
                        break;
                    case 2:
                        this.zzaCl.zzm(this.zzaCr);
                        break;
                    default:
                        Log.wtf("CompositeGAC", "Attempted to call success callbacks in CONNECTION_MODE_NONE. Callbacks should be disabled via GmsClientSupervisor", new AssertionError());
                        break;
                }
                zzpG();
                this.zzaCw = 0;
            } else if (this.zzaCt == null) {
            } else {
                if (this.zzaCw == 1) {
                    zzpG();
                    return;
                }
                zza(this.zzaCt);
                this.zzaCm.disconnect();
            }
        } else if (this.zzaCs != null && zzb(this.zzaCt)) {
            this.zzaCn.disconnect();
            zza(this.zzaCs);
        } else if (this.zzaCs != null && this.zzaCt != null) {
            ConnectionResult connectionResult = this.zzaCs;
            if (this.zzaCn.zzaDX < this.zzaCm.zzaDX) {
                connectionResult = this.zzaCt;
            }
            zza(connectionResult);
        }
    }

    private final void zzpG() {
        for (zzbei zzmF : this.zzaCp) {
            zzmF.zzmF();
        }
        this.zzaCp.clear();
    }

    private final boolean zzpH() {
        return this.zzaCt != null && this.zzaCt.getErrorCode() == 4;
    }

    @Nullable
    private final PendingIntent zzpI() {
        return this.zzaCq == null ? null : PendingIntent.getActivity(this.mContext, System.identityHashCode(this.zzaCl), this.zzaCq.zzmH(), 134217728);
    }

    public final ConnectionResult blockingConnect() {
        throw new UnsupportedOperationException();
    }

    public final ConnectionResult blockingConnect(long j, @NonNull TimeUnit timeUnit) {
        throw new UnsupportedOperationException();
    }

    public final void connect() {
        this.zzaCw = 2;
        this.zzaCu = false;
        this.zzaCt = null;
        this.zzaCs = null;
        this.zzaCm.connect();
        this.zzaCn.connect();
    }

    public final void disconnect() {
        this.zzaCt = null;
        this.zzaCs = null;
        this.zzaCw = 0;
        this.zzaCm.disconnect();
        this.zzaCn.disconnect();
        zzpG();
    }

    public final void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.append(str).append("authClient").println(":");
        this.zzaCn.dump(String.valueOf(str).concat("  "), fileDescriptor, printWriter, strArr);
        printWriter.append(str).append("anonClient").println(":");
        this.zzaCm.dump(String.valueOf(str).concat("  "), fileDescriptor, printWriter, strArr);
    }

    @Nullable
    public final ConnectionResult getConnectionResult(@NonNull Api<?> api) {
        return ((zzbcx) this.zzaCo.get(api.zzpd())).equals(this.zzaCn) ? zzpH() ? new ConnectionResult(4, zzpI()) : this.zzaCn.getConnectionResult(api) : this.zzaCm.getConnectionResult(api);
    }

    public final boolean isConnected() {
        boolean z = true;
        this.zzaCv.lock();
        try {
            if (!(this.zzaCm.isConnected() && (this.zzaCn.isConnected() || zzpH() || this.zzaCw == 1))) {
                z = false;
            }
            this.zzaCv.unlock();
            return z;
        } catch (Throwable th) {
            this.zzaCv.unlock();
        }
    }

    public final boolean isConnecting() {
        this.zzaCv.lock();
        try {
            boolean z = this.zzaCw == 2;
            this.zzaCv.unlock();
            return z;
        } catch (Throwable th) {
            this.zzaCv.unlock();
        }
    }

    public final boolean zza(zzbei com_google_android_gms_internal_zzbei) {
        this.zzaCv.lock();
        try {
            if ((isConnecting() || isConnected()) && !this.zzaCn.isConnected()) {
                this.zzaCp.add(com_google_android_gms_internal_zzbei);
                if (this.zzaCw == 0) {
                    this.zzaCw = 1;
                }
                this.zzaCt = null;
                this.zzaCn.connect();
                return true;
            }
            this.zzaCv.unlock();
            return false;
        } finally {
            this.zzaCv.unlock();
        }
    }

    public final <A extends zzb, R extends Result, T extends zzbay<R, A>> T zzd(@NonNull T t) {
        if (!zzf((zzbay) t)) {
            return this.zzaCm.zzd(t);
        }
        if (!zzpH()) {
            return this.zzaCn.zzd(t);
        }
        t.zzr(new Status(4, null, zzpI()));
        return t;
    }

    public final <A extends zzb, T extends zzbay<? extends Result, A>> T zze(@NonNull T t) {
        if (!zzf((zzbay) t)) {
            return this.zzaCm.zze(t);
        }
        if (!zzpH()) {
            return this.zzaCn.zze(t);
        }
        t.zzr(new Status(4, null, zzpI()));
        return t;
    }

    public final void zzpE() {
        this.zzaCm.zzpE();
        this.zzaCn.zzpE();
    }

    public final void zzpl() {
        this.zzaCv.lock();
        try {
            boolean isConnecting = isConnecting();
            this.zzaCn.disconnect();
            this.zzaCt = new ConnectionResult(4);
            if (isConnecting) {
                new Handler(this.zzrM).post(new zzbbl(this));
            } else {
                zzpG();
            }
            this.zzaCv.unlock();
        } catch (Throwable th) {
            this.zzaCv.unlock();
        }
    }
}
