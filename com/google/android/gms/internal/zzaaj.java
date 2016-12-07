package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.BinderThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzaf;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.internal.zzr;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;

public class zzaaj implements zzaam {
    private final Context mContext;
    private final com.google.android.gms.common.api.Api.zza<? extends zzaxn, zzaxo> zzaxY;
    private ConnectionResult zzazA;
    private final zzaan zzazK;
    private int zzazN;
    private int zzazO = 0;
    private int zzazP;
    private final Bundle zzazQ = new Bundle();
    private final Set<com.google.android.gms.common.api.Api.zzc> zzazR = new HashSet();
    private zzaxn zzazS;
    private int zzazT;
    private boolean zzazU;
    private boolean zzazV;
    private zzr zzazW;
    private boolean zzazX;
    private boolean zzazY;
    private ArrayList<Future<?>> zzazZ = new ArrayList();
    private final Lock zzazn;
    private final zzg zzazs;
    private final Map<Api<?>, Integer> zzazu;
    private final com.google.android.gms.common.zzc zzazw;

    private abstract class zzf implements Runnable {
        final /* synthetic */ zzaaj zzaAa;

        private zzf(zzaaj com_google_android_gms_internal_zzaaj) {
            this.zzaAa = com_google_android_gms_internal_zzaaj;
        }

        @WorkerThread
        public void run() {
            this.zzaAa.zzazn.lock();
            try {
                if (!Thread.interrupted()) {
                    zzvA();
                    this.zzaAa.zzazn.unlock();
                }
            } catch (RuntimeException e) {
                this.zzaAa.zzazK.zza(e);
            } finally {
                this.zzaAa.zzazn.unlock();
            }
        }

        @WorkerThread
        protected abstract void zzvA();
    }

    private static class zza implements com.google.android.gms.common.internal.zzf.zzf {
        private final WeakReference<zzaaj> zzaAb;
        private final Api<?> zzawb;
        private final int zzazb;

        public zza(zzaaj com_google_android_gms_internal_zzaaj, Api<?> api, int i) {
            this.zzaAb = new WeakReference(com_google_android_gms_internal_zzaaj);
            this.zzawb = api;
            this.zzazb = i;
        }

        public void zzg(@NonNull ConnectionResult connectionResult) {
            boolean z = false;
            zzaaj com_google_android_gms_internal_zzaaj = (zzaaj) this.zzaAb.get();
            if (com_google_android_gms_internal_zzaaj != null) {
                if (Looper.myLooper() == com_google_android_gms_internal_zzaaj.zzazK.zzazd.getLooper()) {
                    z = true;
                }
                zzac.zza(z, (Object) "onReportServiceBinding must be called on the GoogleApiClient handler thread");
                com_google_android_gms_internal_zzaaj.zzazn.lock();
                try {
                    if (com_google_android_gms_internal_zzaaj.zzcv(0)) {
                        if (!connectionResult.isSuccess()) {
                            com_google_android_gms_internal_zzaaj.zzb(connectionResult, this.zzawb, this.zzazb);
                        }
                        if (com_google_android_gms_internal_zzaaj.zzvB()) {
                            com_google_android_gms_internal_zzaaj.zzvC();
                        }
                        com_google_android_gms_internal_zzaaj.zzazn.unlock();
                    }
                } finally {
                    com_google_android_gms_internal_zzaaj.zzazn.unlock();
                }
            }
        }
    }

    private class zzb extends zzf {
        final /* synthetic */ zzaaj zzaAa;
        private final Map<com.google.android.gms.common.api.Api.zze, zza> zzaAc;

        public zzb(zzaaj com_google_android_gms_internal_zzaaj, Map<com.google.android.gms.common.api.Api.zze, zza> map) {
            this.zzaAa = com_google_android_gms_internal_zzaaj;
            super();
            this.zzaAc = map;
        }

        @WorkerThread
        public void zzvA() {
            int i;
            int i2 = 1;
            int i3 = 0;
            int i4 = 1;
            int i5 = 0;
            for (com.google.android.gms.common.api.Api.zze com_google_android_gms_common_api_Api_zze : this.zzaAc.keySet()) {
                if (!com_google_android_gms_common_api_Api_zze.zzuI()) {
                    i = 0;
                    i4 = i5;
                } else if (((zza) this.zzaAc.get(com_google_android_gms_common_api_Api_zze)).zzazb == 0) {
                    i = 1;
                    break;
                } else {
                    i = i4;
                    i4 = 1;
                }
                i5 = i4;
                i4 = i;
            }
            i2 = i5;
            i = 0;
            if (i2 != 0) {
                i3 = this.zzaAa.zzazw.isGooglePlayServicesAvailable(this.zzaAa.mContext);
            }
            if (i3 == 0 || (r0 == 0 && i4 == 0)) {
                if (this.zzaAa.zzazU) {
                    this.zzaAa.zzazS.connect();
                }
                for (com.google.android.gms.common.api.Api.zze com_google_android_gms_common_api_Api_zze2 : this.zzaAc.keySet()) {
                    final com.google.android.gms.common.internal.zzf.zzf com_google_android_gms_common_internal_zzf_zzf = (com.google.android.gms.common.internal.zzf.zzf) this.zzaAc.get(com_google_android_gms_common_api_Api_zze2);
                    if (!com_google_android_gms_common_api_Api_zze2.zzuI() || i3 == 0) {
                        com_google_android_gms_common_api_Api_zze2.zza(com_google_android_gms_common_internal_zzf_zzf);
                    } else {
                        this.zzaAa.zzazK.zza(new zza(this, this.zzaAa) {
                            public void zzvA() {
                                com_google_android_gms_common_internal_zzf_zzf.zzg(new ConnectionResult(16, null));
                            }
                        });
                    }
                }
                return;
            }
            final ConnectionResult connectionResult = new ConnectionResult(i3, null);
            this.zzaAa.zzazK.zza(new zza(this, this.zzaAa) {
                final /* synthetic */ zzb zzaAe;

                public void zzvA() {
                    this.zzaAe.zzaAa.zzf(connectionResult);
                }
            });
        }
    }

    private class zzc extends zzf {
        final /* synthetic */ zzaaj zzaAa;
        private final ArrayList<com.google.android.gms.common.api.Api.zze> zzaAg;

        public zzc(zzaaj com_google_android_gms_internal_zzaaj, ArrayList<com.google.android.gms.common.api.Api.zze> arrayList) {
            this.zzaAa = com_google_android_gms_internal_zzaaj;
            super();
            this.zzaAg = arrayList;
        }

        @WorkerThread
        public void zzvA() {
            this.zzaAa.zzazK.zzazd.zzaAs = this.zzaAa.zzvH();
            Iterator it = this.zzaAg.iterator();
            while (it.hasNext()) {
                ((com.google.android.gms.common.api.Api.zze) it.next()).zza(this.zzaAa.zzazW, this.zzaAa.zzazK.zzazd.zzaAs);
            }
        }
    }

    private class zze implements ConnectionCallbacks, OnConnectionFailedListener {
        final /* synthetic */ zzaaj zzaAa;

        private zze(zzaaj com_google_android_gms_internal_zzaaj) {
            this.zzaAa = com_google_android_gms_internal_zzaaj;
        }

        public void onConnected(Bundle bundle) {
            this.zzaAa.zzazS.zza(new zzd(this.zzaAa));
        }

        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            this.zzaAa.zzazn.lock();
            try {
                if (this.zzaAa.zze(connectionResult)) {
                    this.zzaAa.zzvF();
                    this.zzaAa.zzvC();
                } else {
                    this.zzaAa.zzf(connectionResult);
                }
                this.zzaAa.zzazn.unlock();
            } catch (Throwable th) {
                this.zzaAa.zzazn.unlock();
            }
        }

        public void onConnectionSuspended(int i) {
        }
    }

    private static class zzd extends zzaxr {
        private final WeakReference<zzaaj> zzaAb;

        zzd(zzaaj com_google_android_gms_internal_zzaaj) {
            this.zzaAb = new WeakReference(com_google_android_gms_internal_zzaaj);
        }

        @BinderThread
        public void zzb(final zzayb com_google_android_gms_internal_zzayb) {
            final zzaaj com_google_android_gms_internal_zzaaj = (zzaaj) this.zzaAb.get();
            if (com_google_android_gms_internal_zzaaj != null) {
                com_google_android_gms_internal_zzaaj.zzazK.zza(new zza(this, com_google_android_gms_internal_zzaaj) {
                    public void zzvA() {
                        com_google_android_gms_internal_zzaaj.zza(com_google_android_gms_internal_zzayb);
                    }
                });
            }
        }
    }

    public zzaaj(zzaan com_google_android_gms_internal_zzaan, zzg com_google_android_gms_common_internal_zzg, Map<Api<?>, Integer> map, com.google.android.gms.common.zzc com_google_android_gms_common_zzc, com.google.android.gms.common.api.Api.zza<? extends zzaxn, zzaxo> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzaxn__com_google_android_gms_internal_zzaxo, Lock lock, Context context) {
        this.zzazK = com_google_android_gms_internal_zzaan;
        this.zzazs = com_google_android_gms_common_internal_zzg;
        this.zzazu = map;
        this.zzazw = com_google_android_gms_common_zzc;
        this.zzaxY = com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzaxn__com_google_android_gms_internal_zzaxo;
        this.zzazn = lock;
        this.mContext = context;
    }

    private void zza(zzayb com_google_android_gms_internal_zzayb) {
        if (zzcv(0)) {
            ConnectionResult zzxA = com_google_android_gms_internal_zzayb.zzxA();
            if (zzxA.isSuccess()) {
                zzaf zzOp = com_google_android_gms_internal_zzayb.zzOp();
                ConnectionResult zzxA2 = zzOp.zzxA();
                if (zzxA2.isSuccess()) {
                    this.zzazV = true;
                    this.zzazW = zzOp.zzxz();
                    this.zzazX = zzOp.zzxB();
                    this.zzazY = zzOp.zzxC();
                    zzvC();
                    return;
                }
                String valueOf = String.valueOf(zzxA2);
                Log.wtf("GoogleApiClientConnecting", new StringBuilder(String.valueOf(valueOf).length() + 48).append("Sign-in succeeded with resolve account failure: ").append(valueOf).toString(), new Exception());
                zzf(zzxA2);
            } else if (zze(zzxA)) {
                zzvF();
                zzvC();
            } else {
                zzf(zzxA);
            }
        }
    }

    private boolean zza(int i, int i2, ConnectionResult connectionResult) {
        return (i2 != 1 || zzd(connectionResult)) ? this.zzazA == null || i < this.zzazN : false;
    }

    private void zzaq(boolean z) {
        if (this.zzazS != null) {
            if (this.zzazS.isConnected() && z) {
                this.zzazS.zzOe();
            }
            this.zzazS.disconnect();
            this.zzazW = null;
        }
    }

    private void zzb(ConnectionResult connectionResult, Api<?> api, int i) {
        if (i != 2) {
            int priority = api.zzuF().getPriority();
            if (zza(priority, i, connectionResult)) {
                this.zzazA = connectionResult;
                this.zzazN = priority;
            }
        }
        this.zzazK.zzaAG.put(api.zzuH(), connectionResult);
    }

    private boolean zzcv(int i) {
        if (this.zzazO == i) {
            return true;
        }
        Log.w("GoogleApiClientConnecting", this.zzazK.zzazd.zzvN());
        String valueOf = String.valueOf(this);
        Log.w("GoogleApiClientConnecting", new StringBuilder(String.valueOf(valueOf).length() + 23).append("Unexpected callback in ").append(valueOf).toString());
        Log.w("GoogleApiClientConnecting", "mRemainingConnections=" + this.zzazP);
        valueOf = String.valueOf(zzcw(this.zzazO));
        String valueOf2 = String.valueOf(zzcw(i));
        Log.wtf("GoogleApiClientConnecting", new StringBuilder((String.valueOf(valueOf).length() + 70) + String.valueOf(valueOf2).length()).append("GoogleApiClient connecting is in step ").append(valueOf).append(" but received callback for step ").append(valueOf2).toString(), new Exception());
        zzf(new ConnectionResult(8, null));
        return false;
    }

    private String zzcw(int i) {
        switch (i) {
            case 0:
                return "STEP_SERVICE_BINDINGS_AND_SIGN_IN";
            case 1:
                return "STEP_GETTING_REMOTE_SERVICE";
            default:
                return "UNKNOWN";
        }
    }

    private boolean zzd(ConnectionResult connectionResult) {
        return connectionResult.hasResolution() || this.zzazw.zzcr(connectionResult.getErrorCode()) != null;
    }

    private boolean zze(ConnectionResult connectionResult) {
        return this.zzazT != 2 ? this.zzazT == 1 && !connectionResult.hasResolution() : true;
    }

    private void zzf(ConnectionResult connectionResult) {
        zzvG();
        zzaq(!connectionResult.hasResolution());
        this.zzazK.zzh(connectionResult);
        this.zzazK.zzaAK.zzc(connectionResult);
    }

    private boolean zzvB() {
        this.zzazP--;
        if (this.zzazP > 0) {
            return false;
        }
        if (this.zzazP < 0) {
            Log.w("GoogleApiClientConnecting", this.zzazK.zzazd.zzvN());
            Log.wtf("GoogleApiClientConnecting", "GoogleApiClient received too many callbacks for the given step. Clients may be in an unexpected state; GoogleApiClient will now disconnect.", new Exception());
            zzf(new ConnectionResult(8, null));
            return false;
        } else if (this.zzazA == null) {
            return true;
        } else {
            this.zzazK.zzaAJ = this.zzazN;
            zzf(this.zzazA);
            return false;
        }
    }

    private void zzvC() {
        if (this.zzazP == 0) {
            if (!this.zzazU || this.zzazV) {
                zzvD();
            }
        }
    }

    private void zzvD() {
        ArrayList arrayList = new ArrayList();
        this.zzazO = 1;
        this.zzazP = this.zzazK.zzaAr.size();
        for (com.google.android.gms.common.api.Api.zzc com_google_android_gms_common_api_Api_zzc : this.zzazK.zzaAr.keySet()) {
            if (!this.zzazK.zzaAG.containsKey(com_google_android_gms_common_api_Api_zzc)) {
                arrayList.add((com.google.android.gms.common.api.Api.zze) this.zzazK.zzaAr.get(com_google_android_gms_common_api_Api_zzc));
            } else if (zzvB()) {
                zzvE();
            }
        }
        if (!arrayList.isEmpty()) {
            this.zzazZ.add(zzaao.zzvR().submit(new zzc(this, arrayList)));
        }
    }

    private void zzvE() {
        this.zzazK.zzvP();
        zzaao.zzvR().execute(new Runnable(this) {
            final /* synthetic */ zzaaj zzaAa;

            {
                this.zzaAa = r1;
            }

            public void run() {
                this.zzaAa.zzazw.zzan(this.zzaAa.mContext);
            }
        });
        if (this.zzazS != null) {
            if (this.zzazX) {
                this.zzazS.zza(this.zzazW, this.zzazY);
            }
            zzaq(false);
        }
        for (com.google.android.gms.common.api.Api.zzc com_google_android_gms_common_api_Api_zzc : this.zzazK.zzaAG.keySet()) {
            ((com.google.android.gms.common.api.Api.zze) this.zzazK.zzaAr.get(com_google_android_gms_common_api_Api_zzc)).disconnect();
        }
        this.zzazK.zzaAK.zzo(this.zzazQ.isEmpty() ? null : this.zzazQ);
    }

    private void zzvF() {
        this.zzazU = false;
        this.zzazK.zzazd.zzaAs = Collections.emptySet();
        for (com.google.android.gms.common.api.Api.zzc com_google_android_gms_common_api_Api_zzc : this.zzazR) {
            if (!this.zzazK.zzaAG.containsKey(com_google_android_gms_common_api_Api_zzc)) {
                this.zzazK.zzaAG.put(com_google_android_gms_common_api_Api_zzc, new ConnectionResult(17, null));
            }
        }
    }

    private void zzvG() {
        Iterator it = this.zzazZ.iterator();
        while (it.hasNext()) {
            ((Future) it.next()).cancel(true);
        }
        this.zzazZ.clear();
    }

    private Set<Scope> zzvH() {
        if (this.zzazs == null) {
            return Collections.emptySet();
        }
        Set<Scope> hashSet = new HashSet(this.zzazs.zzxe());
        Map zzxg = this.zzazs.zzxg();
        for (Api api : zzxg.keySet()) {
            if (!this.zzazK.zzaAG.containsKey(api.zzuH())) {
                hashSet.addAll(((com.google.android.gms.common.internal.zzg.zza) zzxg.get(api)).zzajm);
            }
        }
        return hashSet;
    }

    public void begin() {
        this.zzazK.zzaAG.clear();
        this.zzazU = false;
        this.zzazA = null;
        this.zzazO = 0;
        this.zzazT = 2;
        this.zzazV = false;
        this.zzazX = false;
        Map hashMap = new HashMap();
        int i = 0;
        for (Api api : this.zzazu.keySet()) {
            com.google.android.gms.common.api.Api.zze com_google_android_gms_common_api_Api_zze = (com.google.android.gms.common.api.Api.zze) this.zzazK.zzaAr.get(api.zzuH());
            int intValue = ((Integer) this.zzazu.get(api)).intValue();
            int i2 = (api.zzuF().getPriority() == 1 ? 1 : 0) | i;
            if (com_google_android_gms_common_api_Api_zze.zzqD()) {
                this.zzazU = true;
                if (intValue < this.zzazT) {
                    this.zzazT = intValue;
                }
                if (intValue != 0) {
                    this.zzazR.add(api.zzuH());
                }
            }
            hashMap.put(com_google_android_gms_common_api_Api_zze, new zza(this, api, intValue));
            i = i2;
        }
        if (i != 0) {
            this.zzazU = false;
        }
        if (this.zzazU) {
            this.zzazs.zzc(Integer.valueOf(this.zzazK.zzazd.getSessionId()));
            ConnectionCallbacks com_google_android_gms_internal_zzaaj_zze = new zze();
            this.zzazS = (zzaxn) this.zzaxY.zza(this.mContext, this.zzazK.zzazd.getLooper(), this.zzazs, this.zzazs.zzxk(), com_google_android_gms_internal_zzaaj_zze, com_google_android_gms_internal_zzaaj_zze);
        }
        this.zzazP = this.zzazK.zzaAr.size();
        this.zzazZ.add(zzaao.zzvR().submit(new zzb(this, hashMap)));
    }

    public void connect() {
    }

    public boolean disconnect() {
        zzvG();
        zzaq(true);
        this.zzazK.zzh(null);
        return true;
    }

    public void onConnected(Bundle bundle) {
        if (zzcv(1)) {
            if (bundle != null) {
                this.zzazQ.putAll(bundle);
            }
            if (zzvB()) {
                zzvE();
            }
        }
    }

    public void onConnectionSuspended(int i) {
        zzf(new ConnectionResult(8, null));
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, R extends Result, T extends com.google.android.gms.internal.zzzv.zza<R, A>> T zza(T t) {
        this.zzazK.zzazd.zzaAl.add(t);
        return t;
    }

    public void zza(ConnectionResult connectionResult, Api<?> api, int i) {
        if (zzcv(1)) {
            zzb(connectionResult, api, i);
            if (zzvB()) {
                zzvE();
            }
        }
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, T extends com.google.android.gms.internal.zzzv.zza<? extends Result, A>> T zzb(T t) {
        throw new IllegalStateException("GoogleApiClient is not connected yet.");
    }
}
