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

public final class zzbdb implements Callback {
    public static final Status zzaEc = new Status(4, "Sign-out occurred while this API call was in progress.");
    private static final Status zzaEd = new Status(4, "The user must be signed in to make this API call.");
    private static zzbdb zzaEf;
    private static final Object zzuF = new Object();
    private final Context mContext;
    private final Handler mHandler;
    private final GoogleApiAvailability zzaBd;
    private final Map<zzbat<?>, zzbdd<?>> zzaCB = new ConcurrentHashMap(5, AdaptiveTrackSelection.DEFAULT_BANDWIDTH_FRACTION, 1);
    private long zzaDB = 120000;
    private long zzaDC = DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
    private long zzaEe = 10000;
    private int zzaEg = -1;
    private final AtomicInteger zzaEh = new AtomicInteger(1);
    private final AtomicInteger zzaEi = new AtomicInteger(0);
    private zzbbw zzaEj = null;
    private final Set<zzbat<?>> zzaEk = new zza();
    private final Set<zzbat<?>> zzaEl = new zza();

    private zzbdb(Context context, Looper looper, GoogleApiAvailability googleApiAvailability) {
        this.mContext = context;
        this.mHandler = new Handler(looper, this);
        this.zzaBd = googleApiAvailability;
        this.mHandler.sendMessage(this.mHandler.obtainMessage(6));
    }

    public static zzbdb zzay(Context context) {
        zzbdb com_google_android_gms_internal_zzbdb;
        synchronized (zzuF) {
            if (zzaEf == null) {
                HandlerThread handlerThread = new HandlerThread("GoogleApiHandler", 9);
                handlerThread.start();
                zzaEf = new zzbdb(context.getApplicationContext(), handlerThread.getLooper(), GoogleApiAvailability.getInstance());
            }
            com_google_android_gms_internal_zzbdb = zzaEf;
        }
        return com_google_android_gms_internal_zzbdb;
    }

    @WorkerThread
    private final void zzc(GoogleApi<?> googleApi) {
        zzbat zzph = googleApi.zzph();
        zzbdd com_google_android_gms_internal_zzbdd = (zzbdd) this.zzaCB.get(zzph);
        if (com_google_android_gms_internal_zzbdd == null) {
            com_google_android_gms_internal_zzbdd = new zzbdd(this, googleApi);
            this.zzaCB.put(zzph, com_google_android_gms_internal_zzbdd);
        }
        if (com_google_android_gms_internal_zzbdd.zzmv()) {
            this.zzaEl.add(zzph);
        }
        com_google_android_gms_internal_zzbdd.connect();
    }

    public static zzbdb zzqk() {
        zzbdb com_google_android_gms_internal_zzbdb;
        synchronized (zzuF) {
            zzbo.zzb(zzaEf, (Object) "Must guarantee manager is non-null before using getInstance");
            com_google_android_gms_internal_zzbdb = zzaEf;
        }
        return com_google_android_gms_internal_zzbdb;
    }

    public static void zzql() {
        synchronized (zzuF) {
            if (zzaEf != null) {
                zzbdb com_google_android_gms_internal_zzbdb = zzaEf;
                com_google_android_gms_internal_zzbdb.zzaEi.incrementAndGet();
                com_google_android_gms_internal_zzbdb.mHandler.sendMessageAtFrontOfQueue(com_google_android_gms_internal_zzbdb.mHandler.obtainMessage(10));
            }
        }
    }

    @WorkerThread
    private final void zzqn() {
        for (zzbat remove : this.zzaEl) {
            ((zzbdd) this.zzaCB.remove(remove)).signOut();
        }
        this.zzaEl.clear();
    }

    @WorkerThread
    public final boolean handleMessage(Message message) {
        zzbdd com_google_android_gms_internal_zzbdd;
        switch (message.what) {
            case 1:
                this.zzaEe = ((Boolean) message.obj).booleanValue() ? 10000 : 300000;
                this.mHandler.removeMessages(12);
                for (zzbat obtainMessage : this.zzaCB.keySet()) {
                    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(12, obtainMessage), this.zzaEe);
                }
                break;
            case 2:
                zzbav com_google_android_gms_internal_zzbav = (zzbav) message.obj;
                for (zzbat com_google_android_gms_internal_zzbat : com_google_android_gms_internal_zzbav.zzpt()) {
                    zzbdd com_google_android_gms_internal_zzbdd2 = (zzbdd) this.zzaCB.get(com_google_android_gms_internal_zzbat);
                    if (com_google_android_gms_internal_zzbdd2 == null) {
                        com_google_android_gms_internal_zzbav.zza(com_google_android_gms_internal_zzbat, new ConnectionResult(13));
                        break;
                    } else if (com_google_android_gms_internal_zzbdd2.isConnected()) {
                        com_google_android_gms_internal_zzbav.zza(com_google_android_gms_internal_zzbat, ConnectionResult.zzazX);
                    } else if (com_google_android_gms_internal_zzbdd2.zzqu() != null) {
                        com_google_android_gms_internal_zzbav.zza(com_google_android_gms_internal_zzbat, com_google_android_gms_internal_zzbdd2.zzqu());
                    } else {
                        com_google_android_gms_internal_zzbdd2.zza(com_google_android_gms_internal_zzbav);
                    }
                }
                break;
            case 3:
                for (zzbdd com_google_android_gms_internal_zzbdd3 : this.zzaCB.values()) {
                    com_google_android_gms_internal_zzbdd3.zzqt();
                    com_google_android_gms_internal_zzbdd3.connect();
                }
                break;
            case 4:
            case 8:
            case 13:
                zzbed com_google_android_gms_internal_zzbed = (zzbed) message.obj;
                com_google_android_gms_internal_zzbdd = (zzbdd) this.zzaCB.get(com_google_android_gms_internal_zzbed.zzaET.zzph());
                if (com_google_android_gms_internal_zzbdd == null) {
                    zzc(com_google_android_gms_internal_zzbed.zzaET);
                    com_google_android_gms_internal_zzbdd = (zzbdd) this.zzaCB.get(com_google_android_gms_internal_zzbed.zzaET.zzph());
                }
                if (com_google_android_gms_internal_zzbdd.zzmv() && this.zzaEi.get() != com_google_android_gms_internal_zzbed.zzaES) {
                    com_google_android_gms_internal_zzbed.zzaER.zzp(zzaEc);
                    com_google_android_gms_internal_zzbdd.signOut();
                    break;
                }
                com_google_android_gms_internal_zzbdd.zza(com_google_android_gms_internal_zzbed.zzaER);
                break;
                break;
            case 5:
                String valueOf;
                String valueOf2;
                int i = message.arg1;
                ConnectionResult connectionResult = (ConnectionResult) message.obj;
                for (zzbdd com_google_android_gms_internal_zzbdd4 : this.zzaCB.values()) {
                    if (com_google_android_gms_internal_zzbdd4.getInstanceId() == i) {
                        if (com_google_android_gms_internal_zzbdd4 != null) {
                            Log.wtf("GoogleApiManager", "Could not find API instance " + i + " while trying to fail enqueued calls.", new Exception());
                            break;
                        }
                        valueOf = String.valueOf(this.zzaBd.getErrorString(connectionResult.getErrorCode()));
                        valueOf2 = String.valueOf(connectionResult.getErrorMessage());
                        com_google_android_gms_internal_zzbdd4.zzt(new Status(17, new StringBuilder((String.valueOf(valueOf).length() + 69) + String.valueOf(valueOf2).length()).append("Error resolution was canceled by the user, original error message: ").append(valueOf).append(": ").append(valueOf2).toString()));
                        break;
                    }
                }
                com_google_android_gms_internal_zzbdd4 = null;
                if (com_google_android_gms_internal_zzbdd4 != null) {
                    Log.wtf("GoogleApiManager", "Could not find API instance " + i + " while trying to fail enqueued calls.", new Exception());
                } else {
                    valueOf = String.valueOf(this.zzaBd.getErrorString(connectionResult.getErrorCode()));
                    valueOf2 = String.valueOf(connectionResult.getErrorMessage());
                    com_google_android_gms_internal_zzbdd4.zzt(new Status(17, new StringBuilder((String.valueOf(valueOf).length() + 69) + String.valueOf(valueOf2).length()).append("Error resolution was canceled by the user, original error message: ").append(valueOf).append(": ").append(valueOf2).toString()));
                }
            case 6:
                if (this.mContext.getApplicationContext() instanceof Application) {
                    zzbaw.zza((Application) this.mContext.getApplicationContext());
                    zzbaw.zzpv().zza(new zzbdc(this));
                    if (!zzbaw.zzpv().zzab(true)) {
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
                    ((zzbdd) this.zzaCB.get(message.obj)).resume();
                    break;
                }
                break;
            case 10:
                zzqn();
                break;
            case 11:
                if (this.zzaCB.containsKey(message.obj)) {
                    ((zzbdd) this.zzaCB.get(message.obj)).zzqd();
                    break;
                }
                break;
            case 12:
                if (this.zzaCB.containsKey(message.obj)) {
                    ((zzbdd) this.zzaCB.get(message.obj)).zzqx();
                    break;
                }
                break;
            default:
                Log.w("GoogleApiManager", "Unknown message id: " + message.what);
                return false;
        }
        return true;
    }

    final PendingIntent zza(zzbat<?> com_google_android_gms_internal_zzbat_, int i) {
        zzbdd com_google_android_gms_internal_zzbdd = (zzbdd) this.zzaCB.get(com_google_android_gms_internal_zzbat_);
        if (com_google_android_gms_internal_zzbdd == null) {
            return null;
        }
        zzctk zzqy = com_google_android_gms_internal_zzbdd.zzqy();
        return zzqy == null ? null : PendingIntent.getActivity(this.mContext, i, zzqy.zzmH(), 134217728);
    }

    public final <O extends ApiOptions> Task<Void> zza(@NonNull GoogleApi<O> googleApi, @NonNull zzbdy<?> com_google_android_gms_internal_zzbdy_) {
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        this.mHandler.sendMessage(this.mHandler.obtainMessage(13, new zzbed(new zzbar(com_google_android_gms_internal_zzbdy_, taskCompletionSource), this.zzaEi.get(), googleApi)));
        return taskCompletionSource.getTask();
    }

    public final <O extends ApiOptions> Task<Void> zza(@NonNull GoogleApi<O> googleApi, @NonNull zzbee<zzb, ?> com_google_android_gms_internal_zzbee_com_google_android_gms_common_api_Api_zzb__, @NonNull zzbey<zzb, ?> com_google_android_gms_internal_zzbey_com_google_android_gms_common_api_Api_zzb__) {
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        this.mHandler.sendMessage(this.mHandler.obtainMessage(8, new zzbed(new zzbap(new zzbef(com_google_android_gms_internal_zzbee_com_google_android_gms_common_api_Api_zzb__, com_google_android_gms_internal_zzbey_com_google_android_gms_common_api_Api_zzb__), taskCompletionSource), this.zzaEi.get(), googleApi)));
        return taskCompletionSource.getTask();
    }

    public final Task<Void> zza(Iterable<? extends GoogleApi<?>> iterable) {
        zzbav com_google_android_gms_internal_zzbav = new zzbav(iterable);
        for (GoogleApi zzph : iterable) {
            zzbdd com_google_android_gms_internal_zzbdd = (zzbdd) this.zzaCB.get(zzph.zzph());
            if (com_google_android_gms_internal_zzbdd != null) {
                if (!com_google_android_gms_internal_zzbdd.isConnected()) {
                }
            }
            this.mHandler.sendMessage(this.mHandler.obtainMessage(2, com_google_android_gms_internal_zzbav));
            return com_google_android_gms_internal_zzbav.getTask();
        }
        com_google_android_gms_internal_zzbav.zzpu();
        return com_google_android_gms_internal_zzbav.getTask();
    }

    public final void zza(ConnectionResult connectionResult, int i) {
        if (!zzc(connectionResult, i)) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(5, i, 0, connectionResult));
        }
    }

    public final <O extends ApiOptions> void zza(GoogleApi<O> googleApi, int i, zzbay<? extends Result, zzb> com_google_android_gms_internal_zzbay__extends_com_google_android_gms_common_api_Result__com_google_android_gms_common_api_Api_zzb) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(4, new zzbed(new zzbao(i, com_google_android_gms_internal_zzbay__extends_com_google_android_gms_common_api_Result__com_google_android_gms_common_api_Api_zzb), this.zzaEi.get(), googleApi)));
    }

    public final <O extends ApiOptions, TResult> void zza(GoogleApi<O> googleApi, int i, zzbeq<zzb, TResult> com_google_android_gms_internal_zzbeq_com_google_android_gms_common_api_Api_zzb__TResult, TaskCompletionSource<TResult> taskCompletionSource, zzbem com_google_android_gms_internal_zzbem) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(4, new zzbed(new zzbaq(i, com_google_android_gms_internal_zzbeq_com_google_android_gms_common_api_Api_zzb__TResult, taskCompletionSource, com_google_android_gms_internal_zzbem), this.zzaEi.get(), googleApi)));
    }

    public final void zza(@NonNull zzbbw com_google_android_gms_internal_zzbbw) {
        synchronized (zzuF) {
            if (this.zzaEj != com_google_android_gms_internal_zzbbw) {
                this.zzaEj = com_google_android_gms_internal_zzbbw;
                this.zzaEk.clear();
                this.zzaEk.addAll(com_google_android_gms_internal_zzbbw.zzpR());
            }
        }
    }

    public final void zzb(GoogleApi<?> googleApi) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(7, googleApi));
    }

    final void zzb(@NonNull zzbbw com_google_android_gms_internal_zzbbw) {
        synchronized (zzuF) {
            if (this.zzaEj == com_google_android_gms_internal_zzbbw) {
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
