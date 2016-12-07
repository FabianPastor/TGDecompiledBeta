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
import com.google.android.gms.common.internal.ResolveAccountResponse;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzp;
import com.google.android.gms.signin.internal.SignInResponse;
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

public class zzrb implements zzre {
    private final Context mContext;
    private final com.google.android.gms.common.api.Api.zza<? extends zzxp, zzxq> xQ;
    private final zzrf zA;
    private int zD;
    private int zE = 0;
    private int zF;
    private final Bundle zG = new Bundle();
    private final Set<com.google.android.gms.common.api.Api.zzc> zH = new HashSet();
    private zzxp zI;
    private int zJ;
    private boolean zK;
    private boolean zL;
    private zzp zM;
    private boolean zN;
    private boolean zO;
    private final com.google.android.gms.common.internal.zzf zP;
    private ArrayList<Future<?>> zQ = new ArrayList();
    private final Lock zg;
    private final Map<Api<?>, Integer> zk;
    private final com.google.android.gms.common.zzc zm;
    private ConnectionResult zq;

    private abstract class zzf implements Runnable {
        final /* synthetic */ zzrb zR;

        private zzf(zzrb com_google_android_gms_internal_zzrb) {
            this.zR = com_google_android_gms_internal_zzrb;
        }

        @WorkerThread
        public void run() {
            this.zR.zg.lock();
            try {
                if (!Thread.interrupted()) {
                    zzaso();
                    this.zR.zg.unlock();
                }
            } catch (RuntimeException e) {
                this.zR.zA.zza(e);
            } finally {
                this.zR.zg.unlock();
            }
        }

        @WorkerThread
        protected abstract void zzaso();
    }

    private static class zza implements com.google.android.gms.common.internal.zze.zzf {
        private final Api<?> vS;
        private final int yU;
        private final WeakReference<zzrb> zS;

        public zza(zzrb com_google_android_gms_internal_zzrb, Api<?> api, int i) {
            this.zS = new WeakReference(com_google_android_gms_internal_zzrb);
            this.vS = api;
            this.yU = i;
        }

        public void zzg(@NonNull ConnectionResult connectionResult) {
            boolean z = false;
            zzrb com_google_android_gms_internal_zzrb = (zzrb) this.zS.get();
            if (com_google_android_gms_internal_zzrb != null) {
                if (Looper.myLooper() == com_google_android_gms_internal_zzrb.zA.yW.getLooper()) {
                    z = true;
                }
                zzaa.zza(z, (Object) "onReportServiceBinding must be called on the GoogleApiClient handler thread");
                com_google_android_gms_internal_zzrb.zg.lock();
                try {
                    if (com_google_android_gms_internal_zzrb.zzft(0)) {
                        if (!connectionResult.isSuccess()) {
                            com_google_android_gms_internal_zzrb.zzb(connectionResult, this.vS, this.yU);
                        }
                        if (com_google_android_gms_internal_zzrb.zzasp()) {
                            com_google_android_gms_internal_zzrb.zzasq();
                        }
                        com_google_android_gms_internal_zzrb.zg.unlock();
                    }
                } finally {
                    com_google_android_gms_internal_zzrb.zg.unlock();
                }
            }
        }
    }

    private class zzb extends zzf {
        final /* synthetic */ zzrb zR;
        private final Map<com.google.android.gms.common.api.Api.zze, zza> zT;

        public zzb(zzrb com_google_android_gms_internal_zzrb, Map<com.google.android.gms.common.api.Api.zze, zza> map) {
            this.zR = com_google_android_gms_internal_zzrb;
            super();
            this.zT = map;
        }

        @WorkerThread
        public void zzaso() {
            int i;
            int i2 = 1;
            int i3 = 0;
            int i4 = 1;
            int i5 = 0;
            for (com.google.android.gms.common.api.Api.zze com_google_android_gms_common_api_Api_zze : this.zT.keySet()) {
                if (!com_google_android_gms_common_api_Api_zze.zzaqx()) {
                    i = 0;
                    i4 = i5;
                } else if (((zza) this.zT.get(com_google_android_gms_common_api_Api_zze)).yU == 0) {
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
                i3 = this.zR.zm.isGooglePlayServicesAvailable(this.zR.mContext);
            }
            if (i3 == 0 || (r0 == 0 && i4 == 0)) {
                if (this.zR.zK) {
                    this.zR.zI.connect();
                }
                for (com.google.android.gms.common.api.Api.zze com_google_android_gms_common_api_Api_zze2 : this.zT.keySet()) {
                    final com.google.android.gms.common.internal.zze.zzf com_google_android_gms_common_internal_zze_zzf = (com.google.android.gms.common.internal.zze.zzf) this.zT.get(com_google_android_gms_common_api_Api_zze2);
                    if (!com_google_android_gms_common_api_Api_zze2.zzaqx() || i3 == 0) {
                        com_google_android_gms_common_api_Api_zze2.zza(com_google_android_gms_common_internal_zze_zzf);
                    } else {
                        this.zR.zA.zza(new zza(this, this.zR) {
                            final /* synthetic */ zzb zV;

                            public void zzaso() {
                                com_google_android_gms_common_internal_zze_zzf.zzg(new ConnectionResult(16, null));
                            }
                        });
                    }
                }
                return;
            }
            final ConnectionResult connectionResult = new ConnectionResult(i3, null);
            this.zR.zA.zza(new zza(this, this.zR) {
                final /* synthetic */ zzb zV;

                public void zzaso() {
                    this.zV.zR.zzf(connectionResult);
                }
            });
        }
    }

    private class zzc extends zzf {
        final /* synthetic */ zzrb zR;
        private final ArrayList<com.google.android.gms.common.api.Api.zze> zX;

        public zzc(zzrb com_google_android_gms_internal_zzrb, ArrayList<com.google.android.gms.common.api.Api.zze> arrayList) {
            this.zR = com_google_android_gms_internal_zzrb;
            super();
            this.zX = arrayList;
        }

        @WorkerThread
        public void zzaso() {
            this.zR.zA.yW.Ak = this.zR.zzasv();
            Iterator it = this.zX.iterator();
            while (it.hasNext()) {
                ((com.google.android.gms.common.api.Api.zze) it.next()).zza(this.zR.zM, this.zR.zA.yW.Ak);
            }
        }
    }

    private class zze implements ConnectionCallbacks, OnConnectionFailedListener {
        final /* synthetic */ zzrb zR;

        private zze(zzrb com_google_android_gms_internal_zzrb) {
            this.zR = com_google_android_gms_internal_zzrb;
        }

        public void onConnected(Bundle bundle) {
            this.zR.zI.zza(new zzd(this.zR));
        }

        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            this.zR.zg.lock();
            try {
                if (this.zR.zze(connectionResult)) {
                    this.zR.zzast();
                    this.zR.zzasq();
                } else {
                    this.zR.zzf(connectionResult);
                }
                this.zR.zg.unlock();
            } catch (Throwable th) {
                this.zR.zg.unlock();
            }
        }

        public void onConnectionSuspended(int i) {
        }
    }

    private static class zzd extends com.google.android.gms.signin.internal.zzb {
        private final WeakReference<zzrb> zS;

        zzd(zzrb com_google_android_gms_internal_zzrb) {
            this.zS = new WeakReference(com_google_android_gms_internal_zzrb);
        }

        @BinderThread
        public void zzb(final SignInResponse signInResponse) {
            final zzrb com_google_android_gms_internal_zzrb = (zzrb) this.zS.get();
            if (com_google_android_gms_internal_zzrb != null) {
                com_google_android_gms_internal_zzrb.zA.zza(new zza(this, com_google_android_gms_internal_zzrb) {
                    final /* synthetic */ zzd Aa;

                    public void zzaso() {
                        com_google_android_gms_internal_zzrb.zza(signInResponse);
                    }
                });
            }
        }
    }

    public zzrb(zzrf com_google_android_gms_internal_zzrf, com.google.android.gms.common.internal.zzf com_google_android_gms_common_internal_zzf, Map<Api<?>, Integer> map, com.google.android.gms.common.zzc com_google_android_gms_common_zzc, com.google.android.gms.common.api.Api.zza<? extends zzxp, zzxq> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzxp__com_google_android_gms_internal_zzxq, Lock lock, Context context) {
        this.zA = com_google_android_gms_internal_zzrf;
        this.zP = com_google_android_gms_common_internal_zzf;
        this.zk = map;
        this.zm = com_google_android_gms_common_zzc;
        this.xQ = com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzxp__com_google_android_gms_internal_zzxq;
        this.zg = lock;
        this.mContext = context;
    }

    private void zza(SignInResponse signInResponse) {
        if (zzft(0)) {
            ConnectionResult zzawn = signInResponse.zzawn();
            if (zzawn.isSuccess()) {
                ResolveAccountResponse zzcdn = signInResponse.zzcdn();
                ConnectionResult zzawn2 = zzcdn.zzawn();
                if (zzawn2.isSuccess()) {
                    this.zL = true;
                    this.zM = zzcdn.zzawm();
                    this.zN = zzcdn.zzawo();
                    this.zO = zzcdn.zzawp();
                    zzasq();
                    return;
                }
                String valueOf = String.valueOf(zzawn2);
                Log.wtf("GoogleApiClientConnecting", new StringBuilder(String.valueOf(valueOf).length() + 48).append("Sign-in succeeded with resolve account failure: ").append(valueOf).toString(), new Exception());
                zzf(zzawn2);
            } else if (zze(zzawn)) {
                zzast();
                zzasq();
            } else {
                zzf(zzawn);
            }
        }
    }

    private boolean zza(int i, int i2, ConnectionResult connectionResult) {
        return (i2 != 1 || zzd(connectionResult)) ? this.zq == null || i < this.zD : false;
    }

    private boolean zzasp() {
        this.zF--;
        if (this.zF > 0) {
            return false;
        }
        if (this.zF < 0) {
            Log.w("GoogleApiClientConnecting", this.zA.yW.zzatb());
            Log.wtf("GoogleApiClientConnecting", "GoogleApiClient received too many callbacks for the given step. Clients may be in an unexpected state; GoogleApiClient will now disconnect.", new Exception());
            zzf(new ConnectionResult(8, null));
            return false;
        } else if (this.zq == null) {
            return true;
        } else {
            this.zA.AB = this.zD;
            zzf(this.zq);
            return false;
        }
    }

    private void zzasq() {
        if (this.zF == 0) {
            if (!this.zK || this.zL) {
                zzasr();
            }
        }
    }

    private void zzasr() {
        ArrayList arrayList = new ArrayList();
        this.zE = 1;
        this.zF = this.zA.Aj.size();
        for (com.google.android.gms.common.api.Api.zzc com_google_android_gms_common_api_Api_zzc : this.zA.Aj.keySet()) {
            if (!this.zA.Ay.containsKey(com_google_android_gms_common_api_Api_zzc)) {
                arrayList.add((com.google.android.gms.common.api.Api.zze) this.zA.Aj.get(com_google_android_gms_common_api_Api_zzc));
            } else if (zzasp()) {
                zzass();
            }
        }
        if (!arrayList.isEmpty()) {
            this.zQ.add(zzrg.zzatf().submit(new zzc(this, arrayList)));
        }
    }

    private void zzass() {
        this.zA.zzatd();
        zzrg.zzatf().execute(new Runnable(this) {
            final /* synthetic */ zzrb zR;

            {
                this.zR = r1;
            }

            public void run() {
                this.zR.zm.zzbn(this.zR.mContext);
            }
        });
        if (this.zI != null) {
            if (this.zN) {
                this.zI.zza(this.zM, this.zO);
            }
            zzbr(false);
        }
        for (com.google.android.gms.common.api.Api.zzc com_google_android_gms_common_api_Api_zzc : this.zA.Ay.keySet()) {
            ((com.google.android.gms.common.api.Api.zze) this.zA.Aj.get(com_google_android_gms_common_api_Api_zzc)).disconnect();
        }
        this.zA.AC.zzn(this.zG.isEmpty() ? null : this.zG);
    }

    private void zzast() {
        this.zK = false;
        this.zA.yW.Ak = Collections.emptySet();
        for (com.google.android.gms.common.api.Api.zzc com_google_android_gms_common_api_Api_zzc : this.zH) {
            if (!this.zA.Ay.containsKey(com_google_android_gms_common_api_Api_zzc)) {
                this.zA.Ay.put(com_google_android_gms_common_api_Api_zzc, new ConnectionResult(17, null));
            }
        }
    }

    private void zzasu() {
        Iterator it = this.zQ.iterator();
        while (it.hasNext()) {
            ((Future) it.next()).cancel(true);
        }
        this.zQ.clear();
    }

    private Set<Scope> zzasv() {
        if (this.zP == null) {
            return Collections.emptySet();
        }
        Set<Scope> hashSet = new HashSet(this.zP.zzavp());
        Map zzavr = this.zP.zzavr();
        for (Api api : zzavr.keySet()) {
            if (!this.zA.Ay.containsKey(api.zzaqv())) {
                hashSet.addAll(((com.google.android.gms.common.internal.zzf.zza) zzavr.get(api)).jw);
            }
        }
        return hashSet;
    }

    private void zzb(ConnectionResult connectionResult, Api<?> api, int i) {
        if (i != 2) {
            int priority = api.zzaqs().getPriority();
            if (zza(priority, i, connectionResult)) {
                this.zq = connectionResult;
                this.zD = priority;
            }
        }
        this.zA.Ay.put(api.zzaqv(), connectionResult);
    }

    private void zzbr(boolean z) {
        if (this.zI != null) {
            if (this.zI.isConnected() && z) {
                this.zI.zzcdc();
            }
            this.zI.disconnect();
            this.zM = null;
        }
    }

    private boolean zzd(ConnectionResult connectionResult) {
        return connectionResult.hasResolution() || this.zm.zzfp(connectionResult.getErrorCode()) != null;
    }

    private boolean zze(ConnectionResult connectionResult) {
        return this.zJ != 2 ? this.zJ == 1 && !connectionResult.hasResolution() : true;
    }

    private void zzf(ConnectionResult connectionResult) {
        zzasu();
        zzbr(!connectionResult.hasResolution());
        this.zA.zzh(connectionResult);
        this.zA.AC.zzc(connectionResult);
    }

    private boolean zzft(int i) {
        if (this.zE == i) {
            return true;
        }
        Log.w("GoogleApiClientConnecting", this.zA.yW.zzatb());
        String valueOf = String.valueOf(this);
        Log.w("GoogleApiClientConnecting", new StringBuilder(String.valueOf(valueOf).length() + 23).append("Unexpected callback in ").append(valueOf).toString());
        Log.w("GoogleApiClientConnecting", "mRemainingConnections=" + this.zF);
        valueOf = String.valueOf(zzfu(this.zE));
        String valueOf2 = String.valueOf(zzfu(i));
        Log.wtf("GoogleApiClientConnecting", new StringBuilder((String.valueOf(valueOf).length() + 70) + String.valueOf(valueOf2).length()).append("GoogleApiClient connecting is in step ").append(valueOf).append(" but received callback for step ").append(valueOf2).toString(), new Exception());
        zzf(new ConnectionResult(8, null));
        return false;
    }

    private String zzfu(int i) {
        switch (i) {
            case 0:
                return "STEP_SERVICE_BINDINGS_AND_SIGN_IN";
            case 1:
                return "STEP_GETTING_REMOTE_SERVICE";
            default:
                return "UNKNOWN";
        }
    }

    public void begin() {
        this.zA.Ay.clear();
        this.zK = false;
        this.zq = null;
        this.zE = 0;
        this.zJ = 2;
        this.zL = false;
        this.zN = false;
        Map hashMap = new HashMap();
        int i = 0;
        for (Api api : this.zk.keySet()) {
            com.google.android.gms.common.api.Api.zze com_google_android_gms_common_api_Api_zze = (com.google.android.gms.common.api.Api.zze) this.zA.Aj.get(api.zzaqv());
            int intValue = ((Integer) this.zk.get(api)).intValue();
            int i2 = (api.zzaqs().getPriority() == 1 ? 1 : 0) | i;
            if (com_google_android_gms_common_api_Api_zze.zzain()) {
                this.zK = true;
                if (intValue < this.zJ) {
                    this.zJ = intValue;
                }
                if (intValue != 0) {
                    this.zH.add(api.zzaqv());
                }
            }
            hashMap.put(com_google_android_gms_common_api_Api_zze, new zza(this, api, intValue));
            i = i2;
        }
        if (i != 0) {
            this.zK = false;
        }
        if (this.zK) {
            this.zP.zzc(Integer.valueOf(this.zA.yW.getSessionId()));
            ConnectionCallbacks com_google_android_gms_internal_zzrb_zze = new zze();
            this.zI = (zzxp) this.xQ.zza(this.mContext, this.zA.yW.getLooper(), this.zP, this.zP.zzavv(), com_google_android_gms_internal_zzrb_zze, com_google_android_gms_internal_zzrb_zze);
        }
        this.zF = this.zA.Aj.size();
        this.zQ.add(zzrg.zzatf().submit(new zzb(this, hashMap)));
    }

    public void connect() {
    }

    public boolean disconnect() {
        zzasu();
        zzbr(true);
        this.zA.zzh(null);
        return true;
    }

    public void onConnected(Bundle bundle) {
        if (zzft(1)) {
            if (bundle != null) {
                this.zG.putAll(bundle);
            }
            if (zzasp()) {
                zzass();
            }
        }
    }

    public void onConnectionSuspended(int i) {
        zzf(new ConnectionResult(8, null));
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, R extends Result, T extends com.google.android.gms.internal.zzqo.zza<R, A>> T zza(T t) {
        this.zA.yW.Ad.add(t);
        return t;
    }

    public void zza(ConnectionResult connectionResult, Api<?> api, int i) {
        if (zzft(1)) {
            zzb(connectionResult, api, i);
            if (zzasp()) {
                zzass();
            }
        }
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, T extends com.google.android.gms.internal.zzqo.zza<? extends Result, A>> T zzb(T t) {
        throw new IllegalStateException("GoogleApiClient is not connected yet.");
    }
}
