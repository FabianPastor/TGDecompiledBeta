package com.google.android.gms.internal;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.zzc;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzal;
import com.google.android.gms.common.internal.zzf.zzf;
import com.google.android.gms.common.internal.zzr;
import com.google.android.gms.common.util.zzt;
import com.google.android.gms.internal.zzzx.zzd;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.exoplayer2.ExoPlayerFactory;
import org.telegram.messenger.exoplayer2.trackselection.AdaptiveVideoTrackSelection;

public class zzaax implements Callback {
    public static final Status zzaCn = new Status(4, "Sign-out occurred while this API call was in progress.");
    private static final Status zzaCo = new Status(4, "The user must be signed in to make this API call.");
    private static zzaax zzaCq;
    private static final Object zztX = new Object();
    private final Context mContext;
    private final Handler mHandler;
    private final Map<zzzz<?>, zza<?>> zzaAM = new ConcurrentHashMap(5, AdaptiveVideoTrackSelection.DEFAULT_BANDWIDTH_FRACTION, 1);
    private long zzaBM = 120000;
    private long zzaBN = ExoPlayerFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
    private long zzaCp = 10000;
    private int zzaCr = -1;
    private final AtomicInteger zzaCs = new AtomicInteger(1);
    private final AtomicInteger zzaCt = new AtomicInteger(0);
    private zzaam zzaCu = null;
    private final Set<zzzz<?>> zzaCv = new com.google.android.gms.common.util.zza();
    private final Set<zzzz<?>> zzaCw = new com.google.android.gms.common.util.zza();
    private final GoogleApiAvailability zzazn;

    private class zzb implements zzf, com.google.android.gms.internal.zzabr.zza {
        private final zze zzaAJ;
        private zzr zzaBw = null;
        private boolean zzaCI = false;
        final /* synthetic */ zzaax zzaCx;
        private Set<Scope> zzakq = null;
        private final zzzz<?> zzayU;

        public zzb(zzaax com_google_android_gms_internal_zzaax, zze com_google_android_gms_common_api_Api_zze, zzzz<?> com_google_android_gms_internal_zzzz_) {
            this.zzaCx = com_google_android_gms_internal_zzaax;
            this.zzaAJ = com_google_android_gms_common_api_Api_zze;
            this.zzayU = com_google_android_gms_internal_zzzz_;
        }

        @WorkerThread
        private void zzwP() {
            if (this.zzaCI && this.zzaBw != null) {
                this.zzaAJ.zza(this.zzaBw, this.zzakq);
            }
        }

        @WorkerThread
        public void zzb(zzr com_google_android_gms_common_internal_zzr, Set<Scope> set) {
            if (com_google_android_gms_common_internal_zzr == null || set == null) {
                Log.wtf("GoogleApiManager", "Received null response from onSignInSuccess", new Exception());
                zzi(new ConnectionResult(4));
                return;
            }
            this.zzaBw = com_google_android_gms_common_internal_zzr;
            this.zzakq = set;
            zzwP();
        }

        public void zzg(@NonNull final ConnectionResult connectionResult) {
            this.zzaCx.mHandler.post(new Runnable(this) {
                final /* synthetic */ zzb zzaCJ;

                public void run() {
                    if (connectionResult.isSuccess()) {
                        this.zzaCJ.zzaCI = true;
                        if (this.zzaCJ.zzaAJ.zzrd()) {
                            this.zzaCJ.zzwP();
                            return;
                        } else {
                            this.zzaCJ.zzaAJ.zza(null, Collections.emptySet());
                            return;
                        }
                    }
                    ((zza) this.zzaCJ.zzaCx.zzaAM.get(this.zzaCJ.zzayU)).onConnectionFailed(connectionResult);
                }
            });
        }

        @WorkerThread
        public void zzi(ConnectionResult connectionResult) {
            ((zza) this.zzaCx.zzaAM.get(this.zzayU)).zzi(connectionResult);
        }
    }

    public class zza<O extends ApiOptions> implements ConnectionCallbacks, OnConnectionFailedListener, zzaah {
        private final zze zzaAJ;
        private boolean zzaBL;
        private final zzaal zzaCA;
        private final Set<zzaab> zzaCB = new HashSet();
        private final Map<com.google.android.gms.internal.zzabh.zzb<?>, zzabn> zzaCC = new HashMap();
        private final int zzaCD;
        private final zzabr zzaCE;
        private ConnectionResult zzaCF = null;
        final /* synthetic */ zzaax zzaCx;
        private final Queue<zzzx> zzaCy = new LinkedList();
        private final com.google.android.gms.common.api.Api.zzb zzaCz;
        private final zzzz<O> zzayU;

        @WorkerThread
        public zza(zzaax com_google_android_gms_internal_zzaax, zzc<O> com_google_android_gms_common_api_zzc_O) {
            this.zzaCx = com_google_android_gms_internal_zzaax;
            this.zzaAJ = com_google_android_gms_common_api_zzc_O.buildApiClient(com_google_android_gms_internal_zzaax.mHandler.getLooper(), this);
            if (this.zzaAJ instanceof zzal) {
                this.zzaCz = ((zzal) this.zzaAJ).zzyn();
            } else {
                this.zzaCz = this.zzaAJ;
            }
            this.zzayU = com_google_android_gms_common_api_zzc_O.getApiKey();
            this.zzaCA = new zzaal();
            this.zzaCD = com_google_android_gms_common_api_zzc_O.getInstanceId();
            if (this.zzaAJ.zzrd()) {
                this.zzaCE = com_google_android_gms_common_api_zzc_O.createSignInCoordinator(com_google_android_gms_internal_zzaax.mContext, com_google_android_gms_internal_zzaax.mHandler);
            } else {
                this.zzaCE = null;
            }
        }

        @WorkerThread
        private void zzb(zzzx com_google_android_gms_internal_zzzx) {
            com_google_android_gms_internal_zzzx.zza(this.zzaCA, zzrd());
            try {
                com_google_android_gms_internal_zzzx.zza(this);
            } catch (DeadObjectException e) {
                onConnectionSuspended(1);
                this.zzaAJ.disconnect();
            }
        }

        @WorkerThread
        private void zzj(ConnectionResult connectionResult) {
            for (zzaab zza : this.zzaCB) {
                zza.zza(this.zzayU, connectionResult);
            }
            this.zzaCB.clear();
        }

        @WorkerThread
        private void zzwF() {
            zzwJ();
            zzj(ConnectionResult.zzayj);
            zzwL();
            Iterator it = this.zzaCC.values().iterator();
            while (it.hasNext()) {
                it.next();
                try {
                    TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
                } catch (DeadObjectException e) {
                    onConnectionSuspended(1);
                    this.zzaAJ.disconnect();
                } catch (RemoteException e2) {
                }
            }
            zzwH();
            zzwM();
        }

        @WorkerThread
        private void zzwG() {
            zzwJ();
            this.zzaBL = true;
            this.zzaCA.zzwa();
            this.zzaCx.mHandler.sendMessageDelayed(Message.obtain(this.zzaCx.mHandler, 9, this.zzayU), this.zzaCx.zzaBN);
            this.zzaCx.mHandler.sendMessageDelayed(Message.obtain(this.zzaCx.mHandler, 11, this.zzayU), this.zzaCx.zzaBM);
            this.zzaCx.zzaCr = -1;
        }

        @WorkerThread
        private void zzwH() {
            while (this.zzaAJ.isConnected() && !this.zzaCy.isEmpty()) {
                zzb((zzzx) this.zzaCy.remove());
            }
        }

        @WorkerThread
        private void zzwL() {
            if (this.zzaBL) {
                this.zzaCx.mHandler.removeMessages(11, this.zzayU);
                this.zzaCx.mHandler.removeMessages(9, this.zzayU);
                this.zzaBL = false;
            }
        }

        private void zzwM() {
            this.zzaCx.mHandler.removeMessages(12, this.zzayU);
            this.zzaCx.mHandler.sendMessageDelayed(this.zzaCx.mHandler.obtainMessage(12, this.zzayU), this.zzaCx.zzaCp);
        }

        @WorkerThread
        public void connect() {
            zzac.zza(this.zzaCx.mHandler);
            if (!this.zzaAJ.isConnected() && !this.zzaAJ.isConnecting()) {
                if (this.zzaAJ.zzvh() && this.zzaCx.zzaCr != 0) {
                    this.zzaCx.zzaCr = this.zzaCx.zzazn.isGooglePlayServicesAvailable(this.zzaCx.mContext);
                    if (this.zzaCx.zzaCr != 0) {
                        onConnectionFailed(new ConnectionResult(this.zzaCx.zzaCr, null));
                        return;
                    }
                }
                Object com_google_android_gms_internal_zzaax_zzb = new zzb(this.zzaCx, this.zzaAJ, this.zzayU);
                if (this.zzaAJ.zzrd()) {
                    this.zzaCE.zza(com_google_android_gms_internal_zzaax_zzb);
                }
                this.zzaAJ.zza(com_google_android_gms_internal_zzaax_zzb);
            }
        }

        public int getInstanceId() {
            return this.zzaCD;
        }

        boolean isConnected() {
            return this.zzaAJ.isConnected();
        }

        public void onConnected(@Nullable Bundle bundle) {
            if (Looper.myLooper() == this.zzaCx.mHandler.getLooper()) {
                zzwF();
            } else {
                this.zzaCx.mHandler.post(new Runnable(this) {
                    final /* synthetic */ zza zzaCG;

                    {
                        this.zzaCG = r1;
                    }

                    public void run() {
                        this.zzaCG.zzwF();
                    }
                });
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        @WorkerThread
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            zzac.zza(this.zzaCx.mHandler);
            if (this.zzaCE != null) {
                this.zzaCE.zzwY();
            }
            zzwJ();
            this.zzaCx.zzaCr = -1;
            zzj(connectionResult);
            if (connectionResult.getErrorCode() == 4) {
                zzD(zzaax.zzaCo);
            } else if (this.zzaCy.isEmpty()) {
                this.zzaCF = connectionResult;
            } else {
                synchronized (zzaax.zztX) {
                    if (this.zzaCx.zzaCu != null && this.zzaCx.zzaCv.contains(this.zzayU)) {
                        this.zzaCx.zzaCu.zzb(connectionResult, this.zzaCD);
                    }
                }
            }
        }

        public void onConnectionSuspended(int i) {
            if (Looper.myLooper() == this.zzaCx.mHandler.getLooper()) {
                zzwG();
            } else {
                this.zzaCx.mHandler.post(new Runnable(this) {
                    final /* synthetic */ zza zzaCG;

                    {
                        this.zzaCG = r1;
                    }

                    public void run() {
                        this.zzaCG.zzwG();
                    }
                });
            }
        }

        @WorkerThread
        public void resume() {
            zzac.zza(this.zzaCx.mHandler);
            if (this.zzaBL) {
                connect();
            }
        }

        @WorkerThread
        public void signOut() {
            zzac.zza(this.zzaCx.mHandler);
            zzD(zzaax.zzaCn);
            this.zzaCA.zzvZ();
            for (com.google.android.gms.internal.zzabh.zzb com_google_android_gms_internal_zzzx_zze : this.zzaCC.keySet()) {
                zza(new zzzx.zze(com_google_android_gms_internal_zzzx_zze, new TaskCompletionSource()));
            }
            zzj(new ConnectionResult(4));
            this.zzaAJ.disconnect();
        }

        @WorkerThread
        public void zzD(Status status) {
            zzac.zza(this.zzaCx.mHandler);
            for (zzzx zzz : this.zzaCy) {
                zzz.zzz(status);
            }
            this.zzaCy.clear();
        }

        public void zza(final ConnectionResult connectionResult, Api<?> api, boolean z) {
            if (Looper.myLooper() == this.zzaCx.mHandler.getLooper()) {
                onConnectionFailed(connectionResult);
            } else {
                this.zzaCx.mHandler.post(new Runnable(this) {
                    final /* synthetic */ zza zzaCG;

                    public void run() {
                        this.zzaCG.onConnectionFailed(connectionResult);
                    }
                });
            }
        }

        @WorkerThread
        public void zza(zzzx com_google_android_gms_internal_zzzx) {
            zzac.zza(this.zzaCx.mHandler);
            if (this.zzaAJ.isConnected()) {
                zzb(com_google_android_gms_internal_zzzx);
                zzwM();
                return;
            }
            this.zzaCy.add(com_google_android_gms_internal_zzzx);
            if (this.zzaCF == null || !this.zzaCF.hasResolution()) {
                connect();
            } else {
                onConnectionFailed(this.zzaCF);
            }
        }

        @WorkerThread
        public void zzb(zzaab com_google_android_gms_internal_zzaab) {
            zzac.zza(this.zzaCx.mHandler);
            this.zzaCB.add(com_google_android_gms_internal_zzaab);
        }

        @WorkerThread
        public void zzi(@NonNull ConnectionResult connectionResult) {
            zzac.zza(this.zzaCx.mHandler);
            this.zzaAJ.disconnect();
            onConnectionFailed(connectionResult);
        }

        public boolean zzrd() {
            return this.zzaAJ.zzrd();
        }

        public zze zzvU() {
            return this.zzaAJ;
        }

        public Map<com.google.android.gms.internal.zzabh.zzb<?>, zzabn> zzwI() {
            return this.zzaCC;
        }

        @WorkerThread
        public void zzwJ() {
            zzac.zza(this.zzaCx.mHandler);
            this.zzaCF = null;
        }

        @WorkerThread
        public ConnectionResult zzwK() {
            zzac.zza(this.zzaCx.mHandler);
            return this.zzaCF;
        }

        @WorkerThread
        public void zzwN() {
            zzac.zza(this.zzaCx.mHandler);
            if (!this.zzaAJ.isConnected() || this.zzaCC.size() != 0) {
                return;
            }
            if (this.zzaCA.zzvY()) {
                zzwM();
            } else {
                this.zzaAJ.disconnect();
            }
        }

        zzbai zzwO() {
            return this.zzaCE == null ? null : this.zzaCE.zzwO();
        }

        @WorkerThread
        public void zzwn() {
            zzac.zza(this.zzaCx.mHandler);
            if (this.zzaBL) {
                zzwL();
                zzD(this.zzaCx.zzazn.isGooglePlayServicesAvailable(this.zzaCx.mContext) == 18 ? new Status(8, "Connection timed out while waiting for Google Play services update to complete.") : new Status(8, "API failed to connect while resuming due to an unknown error."));
                this.zzaAJ.disconnect();
            }
        }
    }

    private zzaax(Context context, Looper looper, GoogleApiAvailability googleApiAvailability) {
        this.mContext = context;
        this.mHandler = new Handler(looper, this);
        this.zzazn = googleApiAvailability;
        this.mHandler.sendMessage(this.mHandler.obtainMessage(6));
    }

    @WorkerThread
    private void zza(int i, ConnectionResult connectionResult) {
        for (zza com_google_android_gms_internal_zzaax_zza : this.zzaAM.values()) {
            if (com_google_android_gms_internal_zzaax_zza.getInstanceId() == i) {
                break;
            }
        }
        zza com_google_android_gms_internal_zzaax_zza2 = null;
        if (com_google_android_gms_internal_zzaax_zza2 != null) {
            String valueOf = String.valueOf(this.zzazn.getErrorString(connectionResult.getErrorCode()));
            String valueOf2 = String.valueOf(connectionResult.getErrorMessage());
            com_google_android_gms_internal_zzaax_zza2.zzD(new Status(17, new StringBuilder((String.valueOf(valueOf).length() + 69) + String.valueOf(valueOf2).length()).append("Error resolution was canceled by the user, original error message: ").append(valueOf).append(": ").append(valueOf2).toString()));
            return;
        }
        Log.wtf("GoogleApiManager", "Could not find API instance " + i + " while trying to fail enqueued calls.", new Exception());
    }

    @WorkerThread
    private void zza(zzaab com_google_android_gms_internal_zzaab) {
        for (zzzz com_google_android_gms_internal_zzzz : com_google_android_gms_internal_zzaab.zzvz()) {
            zza com_google_android_gms_internal_zzaax_zza = (zza) this.zzaAM.get(com_google_android_gms_internal_zzzz);
            if (com_google_android_gms_internal_zzaax_zza == null) {
                com_google_android_gms_internal_zzaab.zza(com_google_android_gms_internal_zzzz, new ConnectionResult(13));
                return;
            } else if (com_google_android_gms_internal_zzaax_zza.isConnected()) {
                com_google_android_gms_internal_zzaab.zza(com_google_android_gms_internal_zzzz, ConnectionResult.zzayj);
            } else if (com_google_android_gms_internal_zzaax_zza.zzwK() != null) {
                com_google_android_gms_internal_zzaab.zza(com_google_android_gms_internal_zzzz, com_google_android_gms_internal_zzaax_zza.zzwK());
            } else {
                com_google_android_gms_internal_zzaax_zza.zzb(com_google_android_gms_internal_zzaab);
            }
        }
    }

    @WorkerThread
    private void zza(zzabl com_google_android_gms_internal_zzabl) {
        zza com_google_android_gms_internal_zzaax_zza = (zza) this.zzaAM.get(com_google_android_gms_internal_zzabl.zzaDe.getApiKey());
        if (com_google_android_gms_internal_zzaax_zza == null) {
            zzc(com_google_android_gms_internal_zzabl.zzaDe);
            com_google_android_gms_internal_zzaax_zza = (zza) this.zzaAM.get(com_google_android_gms_internal_zzabl.zzaDe.getApiKey());
        }
        if (!com_google_android_gms_internal_zzaax_zza.zzrd() || this.zzaCt.get() == com_google_android_gms_internal_zzabl.zzaDd) {
            com_google_android_gms_internal_zzaax_zza.zza(com_google_android_gms_internal_zzabl.zzaDc);
            return;
        }
        com_google_android_gms_internal_zzabl.zzaDc.zzz(zzaCn);
        com_google_android_gms_internal_zzaax_zza.signOut();
    }

    public static zzaax zzaP(Context context) {
        zzaax com_google_android_gms_internal_zzaax;
        synchronized (zztX) {
            if (zzaCq == null) {
                zzaCq = new zzaax(context.getApplicationContext(), zzwy(), GoogleApiAvailability.getInstance());
            }
            com_google_android_gms_internal_zzaax = zzaCq;
        }
        return com_google_android_gms_internal_zzaax;
    }

    @WorkerThread
    private void zzav(boolean z) {
        this.zzaCp = z ? 10000 : 300000;
        this.mHandler.removeMessages(12);
        for (zzzz obtainMessage : this.zzaAM.keySet()) {
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(12, obtainMessage), this.zzaCp);
        }
    }

    @WorkerThread
    private void zzc(zzc<?> com_google_android_gms_common_api_zzc_) {
        zzzz apiKey = com_google_android_gms_common_api_zzc_.getApiKey();
        zza com_google_android_gms_internal_zzaax_zza = (zza) this.zzaAM.get(apiKey);
        if (com_google_android_gms_internal_zzaax_zza == null) {
            com_google_android_gms_internal_zzaax_zza = new zza(this, com_google_android_gms_common_api_zzc_);
            this.zzaAM.put(apiKey, com_google_android_gms_internal_zzaax_zza);
        }
        if (com_google_android_gms_internal_zzaax_zza.zzrd()) {
            this.zzaCw.add(apiKey);
        }
        com_google_android_gms_internal_zzaax_zza.connect();
    }

    @WorkerThread
    private void zzwA() {
        zzt.zzzg();
        if (this.mContext.getApplicationContext() instanceof Application) {
            zzaac.zza((Application) this.mContext.getApplicationContext());
            zzaac.zzvB().zza(new com.google.android.gms.internal.zzaac.zza(this) {
                final /* synthetic */ zzaax zzaCx;

                {
                    this.zzaCx = r1;
                }

                public void zzat(boolean z) {
                    this.zzaCx.mHandler.sendMessage(this.zzaCx.mHandler.obtainMessage(1, Boolean.valueOf(z)));
                }
            });
            if (!zzaac.zzvB().zzas(true)) {
                this.zzaCp = 300000;
            }
        }
    }

    @WorkerThread
    private void zzwB() {
        for (zza com_google_android_gms_internal_zzaax_zza : this.zzaAM.values()) {
            com_google_android_gms_internal_zzaax_zza.zzwJ();
            com_google_android_gms_internal_zzaax_zza.connect();
        }
    }

    @WorkerThread
    private void zzwC() {
        for (zzzz remove : this.zzaCw) {
            ((zza) this.zzaAM.remove(remove)).signOut();
        }
        this.zzaCw.clear();
    }

    public static zzaax zzww() {
        zzaax com_google_android_gms_internal_zzaax;
        synchronized (zztX) {
            zzac.zzb(zzaCq, (Object) "Must guarantee manager is non-null before using getInstance");
            com_google_android_gms_internal_zzaax = zzaCq;
        }
        return com_google_android_gms_internal_zzaax;
    }

    public static void zzwx() {
        synchronized (zztX) {
            if (zzaCq != null) {
                zzaCq.signOut();
            }
        }
    }

    private static Looper zzwy() {
        HandlerThread handlerThread = new HandlerThread("GoogleApiHandler", 9);
        handlerThread.start();
        return handlerThread.getLooper();
    }

    @WorkerThread
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case 1:
                zzav(((Boolean) message.obj).booleanValue());
                break;
            case 2:
                zza((zzaab) message.obj);
                break;
            case 3:
                zzwB();
                break;
            case 4:
            case 8:
            case 13:
                zza((zzabl) message.obj);
                break;
            case 5:
                zza(message.arg1, (ConnectionResult) message.obj);
                break;
            case 6:
                zzwA();
                break;
            case 7:
                zzc((zzc) message.obj);
                break;
            case 9:
                if (this.zzaAM.containsKey(message.obj)) {
                    ((zza) this.zzaAM.get(message.obj)).resume();
                    break;
                }
                break;
            case 10:
                zzwC();
                break;
            case 11:
                if (this.zzaAM.containsKey(message.obj)) {
                    ((zza) this.zzaAM.get(message.obj)).zzwn();
                    break;
                }
                break;
            case 12:
                if (this.zzaAM.containsKey(message.obj)) {
                    ((zza) this.zzaAM.get(message.obj)).zzwN();
                    break;
                }
                break;
            default:
                Log.w("GoogleApiManager", "Unknown message id: " + message.what);
                return false;
        }
        return true;
    }

    public void signOut() {
        this.zzaCt.incrementAndGet();
        this.mHandler.sendMessageAtFrontOfQueue(this.mHandler.obtainMessage(10));
    }

    PendingIntent zza(zzzz<?> com_google_android_gms_internal_zzzz_, int i) {
        if (((zza) this.zzaAM.get(com_google_android_gms_internal_zzzz_)) == null) {
            return null;
        }
        zzbai zzwO = ((zza) this.zzaAM.get(com_google_android_gms_internal_zzzz_)).zzwO();
        return zzwO == null ? null : PendingIntent.getActivity(this.mContext, i, zzwO.zzrs(), 134217728);
    }

    public <O extends ApiOptions> Task<Void> zza(@NonNull zzc<O> com_google_android_gms_common_api_zzc_O, @NonNull com.google.android.gms.internal.zzabh.zzb<?> com_google_android_gms_internal_zzabh_zzb_) {
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        this.mHandler.sendMessage(this.mHandler.obtainMessage(13, new zzabl(new zzzx.zze(com_google_android_gms_internal_zzabh_zzb_, taskCompletionSource), this.zzaCt.get(), com_google_android_gms_common_api_zzc_O)));
        return taskCompletionSource.getTask();
    }

    public <O extends ApiOptions> Task<Void> zza(@NonNull zzc<O> com_google_android_gms_common_api_zzc_O, @NonNull zzabm<com.google.android.gms.common.api.Api.zzb, ?> com_google_android_gms_internal_zzabm_com_google_android_gms_common_api_Api_zzb__, @NonNull zzabz<com.google.android.gms.common.api.Api.zzb, ?> com_google_android_gms_internal_zzabz_com_google_android_gms_common_api_Api_zzb__) {
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        this.mHandler.sendMessage(this.mHandler.obtainMessage(8, new zzabl(new zzzx.zzc(new zzabn(com_google_android_gms_internal_zzabm_com_google_android_gms_common_api_Api_zzb__, com_google_android_gms_internal_zzabz_com_google_android_gms_common_api_Api_zzb__), taskCompletionSource), this.zzaCt.get(), com_google_android_gms_common_api_zzc_O)));
        return taskCompletionSource.getTask();
    }

    public Task<Void> zza(Iterable<? extends zzc<?>> iterable) {
        zzaab com_google_android_gms_internal_zzaab = new zzaab(iterable);
        for (zzc apiKey : iterable) {
            zza com_google_android_gms_internal_zzaax_zza = (zza) this.zzaAM.get(apiKey.getApiKey());
            if (com_google_android_gms_internal_zzaax_zza != null) {
                if (!com_google_android_gms_internal_zzaax_zza.isConnected()) {
                }
            }
            this.mHandler.sendMessage(this.mHandler.obtainMessage(2, com_google_android_gms_internal_zzaab));
            return com_google_android_gms_internal_zzaab.getTask();
        }
        com_google_android_gms_internal_zzaab.zzvA();
        return com_google_android_gms_internal_zzaab.getTask();
    }

    public void zza(ConnectionResult connectionResult, int i) {
        if (!zzc(connectionResult, i)) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(5, i, 0, connectionResult));
        }
    }

    public <O extends ApiOptions> void zza(zzc<O> com_google_android_gms_common_api_zzc_O, int i, com.google.android.gms.internal.zzaad.zza<? extends Result, com.google.android.gms.common.api.Api.zzb> com_google_android_gms_internal_zzaad_zza__extends_com_google_android_gms_common_api_Result__com_google_android_gms_common_api_Api_zzb) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(4, new zzabl(new com.google.android.gms.internal.zzzx.zzb(i, com_google_android_gms_internal_zzaad_zza__extends_com_google_android_gms_common_api_Result__com_google_android_gms_common_api_Api_zzb), this.zzaCt.get(), com_google_android_gms_common_api_zzc_O)));
    }

    public <O extends ApiOptions, TResult> void zza(zzc<O> com_google_android_gms_common_api_zzc_O, int i, zzabv<com.google.android.gms.common.api.Api.zzb, TResult> com_google_android_gms_internal_zzabv_com_google_android_gms_common_api_Api_zzb__TResult, TaskCompletionSource<TResult> taskCompletionSource, zzabs com_google_android_gms_internal_zzabs) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(4, new zzabl(new zzd(i, com_google_android_gms_internal_zzabv_com_google_android_gms_common_api_Api_zzb__TResult, taskCompletionSource, com_google_android_gms_internal_zzabs), this.zzaCt.get(), com_google_android_gms_common_api_zzc_O)));
    }

    public void zza(@NonNull zzaam com_google_android_gms_internal_zzaam) {
        synchronized (zztX) {
            if (this.zzaCu != com_google_android_gms_internal_zzaam) {
                this.zzaCu = com_google_android_gms_internal_zzaam;
                this.zzaCv.clear();
                this.zzaCv.addAll(com_google_android_gms_internal_zzaam.zzwb());
            }
        }
    }

    public void zzb(zzc<?> com_google_android_gms_common_api_zzc_) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(7, com_google_android_gms_common_api_zzc_));
    }

    void zzb(@NonNull zzaam com_google_android_gms_internal_zzaam) {
        synchronized (zztX) {
            if (this.zzaCu == com_google_android_gms_internal_zzaam) {
                this.zzaCu = null;
                this.zzaCv.clear();
            }
        }
    }

    boolean zzc(ConnectionResult connectionResult, int i) {
        return this.zzazn.zza(this.mContext, connectionResult, i);
    }

    void zzvn() {
        this.zzaCt.incrementAndGet();
        this.mHandler.sendMessage(this.mHandler.obtainMessage(10));
    }

    public void zzvx() {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3));
    }

    public int zzwz() {
        return this.zzaCs.getAndIncrement();
    }
}
