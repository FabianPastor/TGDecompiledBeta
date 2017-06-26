package com.google.android.gms.internal;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zza;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.exoplayer2.trackselection.AdaptiveTrackSelection;

public final class zzbda implements Callback {
    public static final Status zzaEc = new Status(4, "Sign-out occurred while this API call was in progress.");
    private static final Status zzaEd = new Status(4, "The user must be signed in to make this API call.");
    private static zzbda zzaEf;
    private static final Object zzuH = new Object();
    private final Context mContext;
    private final Handler mHandler;
    private final GoogleApiAvailability zzaBd;
    private final Map<zzbas<?>, zzbdc<?>> zzaCB = new ConcurrentHashMap(5, AdaptiveTrackSelection.DEFAULT_BANDWIDTH_FRACTION, 1);
    private long zzaDB = 120000;
    private long zzaDC = DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
    private long zzaEe = 10000;
    private int zzaEg = -1;
    private final AtomicInteger zzaEh = new AtomicInteger(1);
    private final AtomicInteger zzaEi = new AtomicInteger(0);
    private zzbbv zzaEj = null;
    private final Set<zzbas<?>> zzaEk = new zza();
    private final Set<zzbas<?>> zzaEl = new zza();

    private zzbda(Context context, Looper looper, GoogleApiAvailability googleApiAvailability) {
        this.mContext = context;
        this.mHandler = new Handler(looper, this);
        this.zzaBd = googleApiAvailability;
        this.mHandler.sendMessage(this.mHandler.obtainMessage(6));
    }

    public static zzbda zzay(Context context) {
        zzbda com_google_android_gms_internal_zzbda;
        synchronized (zzuH) {
            if (zzaEf == null) {
                HandlerThread handlerThread = new HandlerThread("GoogleApiHandler", 9);
                handlerThread.start();
                zzaEf = new zzbda(context.getApplicationContext(), handlerThread.getLooper(), GoogleApiAvailability.getInstance());
            }
            com_google_android_gms_internal_zzbda = zzaEf;
        }
        return com_google_android_gms_internal_zzbda;
    }

    @WorkerThread
    private final void zzc(GoogleApi<?> googleApi) {
        zzbas zzph = googleApi.zzph();
        zzbdc com_google_android_gms_internal_zzbdc = (zzbdc) this.zzaCB.get(zzph);
        if (com_google_android_gms_internal_zzbdc == null) {
            com_google_android_gms_internal_zzbdc = new zzbdc(this, googleApi);
            this.zzaCB.put(zzph, com_google_android_gms_internal_zzbdc);
        }
        if (com_google_android_gms_internal_zzbdc.zzmv()) {
            this.zzaEl.add(zzph);
        }
        com_google_android_gms_internal_zzbdc.connect();
    }

    public static zzbda zzqk() {
        zzbda com_google_android_gms_internal_zzbda;
        synchronized (zzuH) {
            zzbo.zzb(zzaEf, (Object) "Must guarantee manager is non-null before using getInstance");
            com_google_android_gms_internal_zzbda = zzaEf;
        }
        return com_google_android_gms_internal_zzbda;
    }

    public static void zzql() {
        synchronized (zzuH) {
            if (zzaEf != null) {
                zzbda com_google_android_gms_internal_zzbda = zzaEf;
                com_google_android_gms_internal_zzbda.zzaEi.incrementAndGet();
                com_google_android_gms_internal_zzbda.mHandler.sendMessageAtFrontOfQueue(com_google_android_gms_internal_zzbda.mHandler.obtainMessage(10));
            }
        }
    }

    @WorkerThread
    private final void zzqn() {
        for (zzbas remove : this.zzaEl) {
            ((zzbdc) this.zzaCB.remove(remove)).signOut();
        }
        this.zzaEl.clear();
    }

    @WorkerThread
    public final boolean handleMessage(Message message) {
        zzbdc com_google_android_gms_internal_zzbdc;
        switch (message.what) {
            case 1:
                this.zzaEe = ((Boolean) message.obj).booleanValue() ? 10000 : 300000;
                this.mHandler.removeMessages(12);
                for (zzbas obtainMessage : this.zzaCB.keySet()) {
                    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(12, obtainMessage), this.zzaEe);
                }
                break;
            case 2:
                zzbau com_google_android_gms_internal_zzbau = (zzbau) message.obj;
                for (zzbas com_google_android_gms_internal_zzbas : com_google_android_gms_internal_zzbau.zzpt()) {
                    zzbdc com_google_android_gms_internal_zzbdc2 = (zzbdc) this.zzaCB.get(com_google_android_gms_internal_zzbas);
                    if (com_google_android_gms_internal_zzbdc2 == null) {
                        com_google_android_gms_internal_zzbau.zza(com_google_android_gms_internal_zzbas, new ConnectionResult(13));
                        break;
                    } else if (com_google_android_gms_internal_zzbdc2.isConnected()) {
                        com_google_android_gms_internal_zzbau.zza(com_google_android_gms_internal_zzbas, ConnectionResult.zzazX);
                    } else if (com_google_android_gms_internal_zzbdc2.zzqu() != null) {
                        com_google_android_gms_internal_zzbau.zza(com_google_android_gms_internal_zzbas, com_google_android_gms_internal_zzbdc2.zzqu());
                    } else {
                        com_google_android_gms_internal_zzbdc2.zza(com_google_android_gms_internal_zzbau);
                    }
                }
                break;
            case 3:
                for (zzbdc com_google_android_gms_internal_zzbdc3 : this.zzaCB.values()) {
                    com_google_android_gms_internal_zzbdc3.zzqt();
                    com_google_android_gms_internal_zzbdc3.connect();
                }
                break;
            case 4:
            case 8:
            case 13:
                zzbec com_google_android_gms_internal_zzbec = (zzbec) message.obj;
                com_google_android_gms_internal_zzbdc = (zzbdc) this.zzaCB.get(com_google_android_gms_internal_zzbec.zzaET.zzph());
                if (com_google_android_gms_internal_zzbdc == null) {
                    zzc(com_google_android_gms_internal_zzbec.zzaET);
                    com_google_android_gms_internal_zzbdc = (zzbdc) this.zzaCB.get(com_google_android_gms_internal_zzbec.zzaET.zzph());
                }
                if (com_google_android_gms_internal_zzbdc.zzmv() && this.zzaEi.get() != com_google_android_gms_internal_zzbec.zzaES) {
                    com_google_android_gms_internal_zzbec.zzaER.zzp(zzaEc);
                    com_google_android_gms_internal_zzbdc.signOut();
                    break;
                }
                com_google_android_gms_internal_zzbdc.zza(com_google_android_gms_internal_zzbec.zzaER);
                break;
                break;
            case 5:
                String valueOf;
                String valueOf2;
                int i = message.arg1;
                ConnectionResult connectionResult = (ConnectionResult) message.obj;
                for (zzbdc com_google_android_gms_internal_zzbdc4 : this.zzaCB.values()) {
                    if (com_google_android_gms_internal_zzbdc4.getInstanceId() == i) {
                        if (com_google_android_gms_internal_zzbdc4 != null) {
                            Log.wtf("GoogleApiManager", "Could not find API instance " + i + " while trying to fail enqueued calls.", new Exception());
                            break;
                        }
                        valueOf = String.valueOf(this.zzaBd.getErrorString(connectionResult.getErrorCode()));
                        valueOf2 = String.valueOf(connectionResult.getErrorMessage());
                        com_google_android_gms_internal_zzbdc4.zzt(new Status(17, new StringBuilder((String.valueOf(valueOf).length() + 69) + String.valueOf(valueOf2).length()).append("Error resolution was canceled by the user, original error message: ").append(valueOf).append(": ").append(valueOf2).toString()));
                        break;
                    }
                }
                com_google_android_gms_internal_zzbdc4 = null;
                if (com_google_android_gms_internal_zzbdc4 != null) {
                    Log.wtf("GoogleApiManager", "Could not find API instance " + i + " while trying to fail enqueued calls.", new Exception());
                } else {
                    valueOf = String.valueOf(this.zzaBd.getErrorString(connectionResult.getErrorCode()));
                    valueOf2 = String.valueOf(connectionResult.getErrorMessage());
                    com_google_android_gms_internal_zzbdc4.zzt(new Status(17, new StringBuilder((String.valueOf(valueOf).length() + 69) + String.valueOf(valueOf2).length()).append("Error resolution was canceled by the user, original error message: ").append(valueOf).append(": ").append(valueOf2).toString()));
                }
            case 6:
                if (this.mContext.getApplicationContext() instanceof Application) {
                    zzbav.zza((Application) this.mContext.getApplicationContext());
                    zzbav.zzpv().zza(new zzbdb(this));
                    if (!zzbav.zzpv().zzab(true)) {
                        this.zzaEe = 300000;
                        break;
                    }
                }
                break;
            case 7:
                zzc((GoogleApi) message.obj);
                break;
            case 9:
                if (this.zzaCB.containsKey(message.obj)) {
                    ((zzbdc) this.zzaCB.get(message.obj)).resume();
                    break;
                }
                break;
            case 10:
                zzqn();
                break;
            case 11:
                if (this.zzaCB.containsKey(message.obj)) {
                    ((zzbdc) this.zzaCB.get(message.obj)).zzqd();
                    break;
                }
                break;
            case 12:
                if (this.zzaCB.containsKey(message.obj)) {
                    ((zzbdc) this.zzaCB.get(message.obj)).zzqx();
                    break;
                }
                break;
            default:
                Log.w("GoogleApiManager", "Unknown message id: " + message.what);
                return false;
        }
        return true;
    }

    final PendingIntent zza(zzbas<?> com_google_android_gms_internal_zzbas_, int i) {
        zzbdc com_google_android_gms_internal_zzbdc = (zzbdc) this.zzaCB.get(com_google_android_gms_internal_zzbas_);
        if (com_google_android_gms_internal_zzbdc == null) {
            return null;
        }
        zzctj zzqy = com_google_android_gms_internal_zzbdc.zzqy();
        return zzqy == null ? null : PendingIntent.getActivity(this.mContext, i, zzqy.zzmH(), 134217728);
    }

    public final <O extends ApiOptions> Task<Void> zza(@NonNull GoogleApi<O> googleApi, @NonNull zzbdx<?> com_google_android_gms_internal_zzbdx_) {
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        this.mHandler.sendMessage(this.mHandler.obtainMessage(13, new zzbec(new zzbaq(com_google_android_gms_internal_zzbdx_, taskCompletionSource), this.zzaEi.get(), googleApi)));
        return taskCompletionSource.getTask();
    }

    public final <O extends ApiOptions> Task<Void> zza(@NonNull GoogleApi<O> googleApi, @NonNull zzbed<zzb, ?> com_google_android_gms_internal_zzbed_com_google_android_gms_common_api_Api_zzb__, @NonNull zzbex<zzb, ?> com_google_android_gms_internal_zzbex_com_google_android_gms_common_api_Api_zzb__) {
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        this.mHandler.sendMessage(this.mHandler.obtainMessage(8, new zzbec(new zzbao(new zzbee(com_google_android_gms_internal_zzbed_com_google_android_gms_common_api_Api_zzb__, com_google_android_gms_internal_zzbex_com_google_android_gms_common_api_Api_zzb__), taskCompletionSource), this.zzaEi.get(), googleApi)));
        return taskCompletionSource.getTask();
    }

    public final Task<Void> zza(Iterable<? extends GoogleApi<?>> iterable) {
        zzbau com_google_android_gms_internal_zzbau = new zzbau(iterable);
        for (GoogleApi zzph : iterable) {
            zzbdc com_google_android_gms_internal_zzbdc = (zzbdc) this.zzaCB.get(zzph.zzph());
            if (com_google_android_gms_internal_zzbdc != null) {
                if (!com_google_android_gms_internal_zzbdc.isConnected()) {
                }
            }
            this.mHandler.sendMessage(this.mHandler.obtainMessage(2, com_google_android_gms_internal_zzbau));
            return com_google_android_gms_internal_zzbau.getTask();
        }
        com_google_android_gms_internal_zzbau.zzpu();
        return com_google_android_gms_internal_zzbau.getTask();
    }

    public final void zza(ConnectionResult connectionResult, int i) {
        if (!zzc(connectionResult, i)) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(5, i, 0, connectionResult));
        }
    }

    public final <O extends ApiOptions> void zza(GoogleApi<O> googleApi, int i, zzbax<? extends Result, zzb> com_google_android_gms_internal_zzbax__extends_com_google_android_gms_common_api_Result__com_google_android_gms_common_api_Api_zzb) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(4, new zzbec(new zzban(i, com_google_android_gms_internal_zzbax__extends_com_google_android_gms_common_api_Result__com_google_android_gms_common_api_Api_zzb), this.zzaEi.get(), googleApi)));
    }

    public final <O extends ApiOptions, TResult> void zza(GoogleApi<O> googleApi, int i, zzbep<zzb, TResult> com_google_android_gms_internal_zzbep_com_google_android_gms_common_api_Api_zzb__TResult, TaskCompletionSource<TResult> taskCompletionSource, zzbel com_google_android_gms_internal_zzbel) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(4, new zzbec(new zzbap(i, com_google_android_gms_internal_zzbep_com_google_android_gms_common_api_Api_zzb__TResult, taskCompletionSource, com_google_android_gms_internal_zzbel), this.zzaEi.get(), googleApi)));
    }

    public final void zza(@NonNull zzbbv com_google_android_gms_internal_zzbbv) {
        synchronized (zzuH) {
            if (this.zzaEj != com_google_android_gms_internal_zzbbv) {
                this.zzaEj = com_google_android_gms_internal_zzbbv;
                this.zzaEk.clear();
                this.zzaEk.addAll(com_google_android_gms_internal_zzbbv.zzpR());
            }
        }
    }

    public final void zzb(GoogleApi<?> googleApi) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(7, googleApi));
    }

    final void zzb(@NonNull zzbbv com_google_android_gms_internal_zzbbv) {
        synchronized (zzuH) {
            if (this.zzaEj == com_google_android_gms_internal_zzbbv) {
                this.zzaEj = null;
                this.zzaEk.clear();
            }
        }
    }

    final boolean zzc(ConnectionResult connectionResult, int i) {
        return this.zzaBd.zza(this.mContext, connectionResult, i);
    }

    final void zzpl() {
        this.zzaEi.incrementAndGet();
        this.mHandler.sendMessage(this.mHandler.obtainMessage(10));
    }

    public final void zzps() {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3));
    }

    public final int zzqm() {
        return this.zzaEh.getAndIncrement();
    }
}
