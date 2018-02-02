package com.google.android.gms.common.api.internal;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.ArraySet;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.internal.zzcxd;
import com.google.android.gms.tasks.Task;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;

public final class zzbm implements Callback {
    private static final Object sLock = new Object();
    public static final Status zzfsy = new Status(4, "Sign-out occurred while this API call was in progress.");
    private static final Status zzfsz = new Status(4, "The user must be signed in to make this API call.");
    private static zzbm zzftb;
    private final Context mContext;
    private final Handler mHandler;
    private final GoogleApiAvailability zzfmy;
    private final Map<zzh<?>, zzbo<?>> zzfpy = new ConcurrentHashMap(5, 0.75f, 1);
    private long zzfrx = 120000;
    private long zzfry = DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
    private long zzfta = 10000;
    private int zzftc = -1;
    private final AtomicInteger zzftd = new AtomicInteger(1);
    private final AtomicInteger zzfte = new AtomicInteger(0);
    private zzah zzftf = null;
    private final Set<zzh<?>> zzftg = new ArraySet();
    private final Set<zzh<?>> zzfth = new ArraySet();

    private zzbm(Context context, Looper looper, GoogleApiAvailability googleApiAvailability) {
        this.mContext = context;
        this.mHandler = new Handler(looper, this);
        this.zzfmy = googleApiAvailability;
        this.mHandler.sendMessage(this.mHandler.obtainMessage(6));
    }

    public static zzbm zzaiq() {
        zzbm com_google_android_gms_common_api_internal_zzbm;
        synchronized (sLock) {
            zzbq.checkNotNull(zzftb, "Must guarantee manager is non-null before using getInstance");
            com_google_android_gms_common_api_internal_zzbm = zzftb;
        }
        return com_google_android_gms_common_api_internal_zzbm;
    }

    private final void zzait() {
        for (zzh remove : this.zzfth) {
            ((zzbo) this.zzfpy.remove(remove)).signOut();
        }
        this.zzfth.clear();
    }

    private final void zzb(GoogleApi<?> googleApi) {
        zzh zzagn = googleApi.zzagn();
        zzbo com_google_android_gms_common_api_internal_zzbo = (zzbo) this.zzfpy.get(zzagn);
        if (com_google_android_gms_common_api_internal_zzbo == null) {
            com_google_android_gms_common_api_internal_zzbo = new zzbo(this, googleApi);
            this.zzfpy.put(zzagn, com_google_android_gms_common_api_internal_zzbo);
        }
        if (com_google_android_gms_common_api_internal_zzbo.zzaay()) {
            this.zzfth.add(zzagn);
        }
        com_google_android_gms_common_api_internal_zzbo.connect();
    }

    public static zzbm zzcj(Context context) {
        zzbm com_google_android_gms_common_api_internal_zzbm;
        synchronized (sLock) {
            if (zzftb == null) {
                HandlerThread handlerThread = new HandlerThread("GoogleApiHandler", 9);
                handlerThread.start();
                zzftb = new zzbm(context.getApplicationContext(), handlerThread.getLooper(), GoogleApiAvailability.getInstance());
            }
            com_google_android_gms_common_api_internal_zzbm = zzftb;
        }
        return com_google_android_gms_common_api_internal_zzbm;
    }

    public final boolean handleMessage(Message message) {
        zzbo com_google_android_gms_common_api_internal_zzbo;
        switch (message.what) {
            case 1:
                this.zzfta = ((Boolean) message.obj).booleanValue() ? 10000 : 300000;
                this.mHandler.removeMessages(12);
                for (zzh obtainMessage : this.zzfpy.keySet()) {
                    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(12, obtainMessage), this.zzfta);
                }
                break;
            case 2:
                zzj com_google_android_gms_common_api_internal_zzj = (zzj) message.obj;
                for (zzh com_google_android_gms_common_api_internal_zzh : com_google_android_gms_common_api_internal_zzj.zzaha()) {
                    zzbo com_google_android_gms_common_api_internal_zzbo2 = (zzbo) this.zzfpy.get(com_google_android_gms_common_api_internal_zzh);
                    if (com_google_android_gms_common_api_internal_zzbo2 == null) {
                        com_google_android_gms_common_api_internal_zzj.zza(com_google_android_gms_common_api_internal_zzh, new ConnectionResult(13), null);
                        break;
                    } else if (com_google_android_gms_common_api_internal_zzbo2.isConnected()) {
                        com_google_android_gms_common_api_internal_zzj.zza(com_google_android_gms_common_api_internal_zzh, ConnectionResult.zzfkr, com_google_android_gms_common_api_internal_zzbo2.zzahp().zzagi());
                    } else if (com_google_android_gms_common_api_internal_zzbo2.zzaja() != null) {
                        com_google_android_gms_common_api_internal_zzj.zza(com_google_android_gms_common_api_internal_zzh, com_google_android_gms_common_api_internal_zzbo2.zzaja(), null);
                    } else {
                        com_google_android_gms_common_api_internal_zzbo2.zza(com_google_android_gms_common_api_internal_zzj);
                    }
                }
                break;
            case 3:
                for (zzbo com_google_android_gms_common_api_internal_zzbo3 : this.zzfpy.values()) {
                    com_google_android_gms_common_api_internal_zzbo3.zzaiz();
                    com_google_android_gms_common_api_internal_zzbo3.connect();
                }
                break;
            case 4:
            case 8:
            case 13:
                zzcp com_google_android_gms_common_api_internal_zzcp = (zzcp) message.obj;
                com_google_android_gms_common_api_internal_zzbo = (zzbo) this.zzfpy.get(com_google_android_gms_common_api_internal_zzcp.zzfur.zzagn());
                if (com_google_android_gms_common_api_internal_zzbo == null) {
                    zzb(com_google_android_gms_common_api_internal_zzcp.zzfur);
                    com_google_android_gms_common_api_internal_zzbo = (zzbo) this.zzfpy.get(com_google_android_gms_common_api_internal_zzcp.zzfur.zzagn());
                }
                if (com_google_android_gms_common_api_internal_zzbo.zzaay() && this.zzfte.get() != com_google_android_gms_common_api_internal_zzcp.zzfuq) {
                    com_google_android_gms_common_api_internal_zzcp.zzfup.zzs(zzfsy);
                    com_google_android_gms_common_api_internal_zzbo.signOut();
                    break;
                }
                com_google_android_gms_common_api_internal_zzbo.zza(com_google_android_gms_common_api_internal_zzcp.zzfup);
                break;
                break;
            case 5:
                String errorString;
                String errorMessage;
                int i = message.arg1;
                ConnectionResult connectionResult = (ConnectionResult) message.obj;
                for (zzbo com_google_android_gms_common_api_internal_zzbo4 : this.zzfpy.values()) {
                    if (com_google_android_gms_common_api_internal_zzbo4.getInstanceId() == i) {
                        if (com_google_android_gms_common_api_internal_zzbo4 != null) {
                            Log.wtf("GoogleApiManager", "Could not find API instance " + i + " while trying to fail enqueued calls.", new Exception());
                            break;
                        }
                        errorString = this.zzfmy.getErrorString(connectionResult.getErrorCode());
                        errorMessage = connectionResult.getErrorMessage();
                        com_google_android_gms_common_api_internal_zzbo4.zzw(new Status(17, new StringBuilder((String.valueOf(errorString).length() + 69) + String.valueOf(errorMessage).length()).append("Error resolution was canceled by the user, original error message: ").append(errorString).append(": ").append(errorMessage).toString()));
                        break;
                    }
                }
                com_google_android_gms_common_api_internal_zzbo4 = null;
                if (com_google_android_gms_common_api_internal_zzbo4 != null) {
                    Log.wtf("GoogleApiManager", "Could not find API instance " + i + " while trying to fail enqueued calls.", new Exception());
                } else {
                    errorString = this.zzfmy.getErrorString(connectionResult.getErrorCode());
                    errorMessage = connectionResult.getErrorMessage();
                    com_google_android_gms_common_api_internal_zzbo4.zzw(new Status(17, new StringBuilder((String.valueOf(errorString).length() + 69) + String.valueOf(errorMessage).length()).append("Error resolution was canceled by the user, original error message: ").append(errorString).append(": ").append(errorMessage).toString()));
                }
            case 6:
                if (this.mContext.getApplicationContext() instanceof Application) {
                    zzk.zza((Application) this.mContext.getApplicationContext());
                    zzk.zzahb().zza(new zzbn(this));
                    if (!zzk.zzahb().zzbe(true)) {
                        this.zzfta = 300000;
                        break;
                    }
                }
                break;
            case 7:
                zzb((GoogleApi) message.obj);
                break;
            case 9:
                if (this.zzfpy.containsKey(message.obj)) {
                    ((zzbo) this.zzfpy.get(message.obj)).resume();
                    break;
                }
                break;
            case 10:
                zzait();
                break;
            case 11:
                if (this.zzfpy.containsKey(message.obj)) {
                    ((zzbo) this.zzfpy.get(message.obj)).zzaij();
                    break;
                }
                break;
            case 12:
                if (this.zzfpy.containsKey(message.obj)) {
                    ((zzbo) this.zzfpy.get(message.obj)).zzajd();
                    break;
                }
                break;
            default:
                Log.w("GoogleApiManager", "Unknown message id: " + message.what);
                return false;
        }
        return true;
    }

    final PendingIntent zza(zzh<?> com_google_android_gms_common_api_internal_zzh_, int i) {
        zzbo com_google_android_gms_common_api_internal_zzbo = (zzbo) this.zzfpy.get(com_google_android_gms_common_api_internal_zzh_);
        if (com_google_android_gms_common_api_internal_zzbo == null) {
            return null;
        }
        zzcxd zzaje = com_google_android_gms_common_api_internal_zzbo.zzaje();
        return zzaje == null ? null : PendingIntent.getActivity(this.mContext, i, zzaje.getSignInIntent(), 134217728);
    }

    public final Task<Map<zzh<?>, String>> zza(Iterable<? extends GoogleApi<?>> iterable) {
        zzj com_google_android_gms_common_api_internal_zzj = new zzj(iterable);
        for (GoogleApi googleApi : iterable) {
            zzbo com_google_android_gms_common_api_internal_zzbo = (zzbo) this.zzfpy.get(googleApi.zzagn());
            if (com_google_android_gms_common_api_internal_zzbo == null || !com_google_android_gms_common_api_internal_zzbo.isConnected()) {
                this.mHandler.sendMessage(this.mHandler.obtainMessage(2, com_google_android_gms_common_api_internal_zzj));
                return com_google_android_gms_common_api_internal_zzj.getTask();
            }
            com_google_android_gms_common_api_internal_zzj.zza(googleApi.zzagn(), ConnectionResult.zzfkr, com_google_android_gms_common_api_internal_zzbo.zzahp().zzagi());
        }
        return com_google_android_gms_common_api_internal_zzj.getTask();
    }

    public final void zza(ConnectionResult connectionResult, int i) {
        if (!zzc(connectionResult, i)) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(5, i, 0, connectionResult));
        }
    }

    public final void zza(GoogleApi<?> googleApi) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(7, googleApi));
    }

    public final <O extends ApiOptions> void zza(GoogleApi<O> googleApi, int i, zzm<? extends Result, zzb> com_google_android_gms_common_api_internal_zzm__extends_com_google_android_gms_common_api_Result__com_google_android_gms_common_api_Api_zzb) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(4, new zzcp(new zzc(i, com_google_android_gms_common_api_internal_zzm__extends_com_google_android_gms_common_api_Result__com_google_android_gms_common_api_Api_zzb), this.zzfte.get(), googleApi)));
    }

    public final void zza(zzah com_google_android_gms_common_api_internal_zzah) {
        synchronized (sLock) {
            if (this.zzftf != com_google_android_gms_common_api_internal_zzah) {
                this.zzftf = com_google_android_gms_common_api_internal_zzah;
                this.zzftg.clear();
                this.zzftg.addAll(com_google_android_gms_common_api_internal_zzah.zzahx());
            }
        }
    }

    public final void zzagz() {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3));
    }

    public final int zzais() {
        return this.zzftd.getAndIncrement();
    }

    final void zzb(zzah com_google_android_gms_common_api_internal_zzah) {
        synchronized (sLock) {
            if (this.zzftf == com_google_android_gms_common_api_internal_zzah) {
                this.zzftf = null;
                this.zzftg.clear();
            }
        }
    }

    final boolean zzc(ConnectionResult connectionResult, int i) {
        return this.zzfmy.zza(this.mContext, connectionResult, i);
    }
}
