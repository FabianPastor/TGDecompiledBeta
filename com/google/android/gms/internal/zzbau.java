package com.google.android.gms.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzbo;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class zzbau extends zzbba {
    private final SparseArray<zza> zzaBB = new SparseArray();

    class zza implements OnConnectionFailedListener {
        public final int zzaBC;
        public final GoogleApiClient zzaBD;
        public final OnConnectionFailedListener zzaBE;
        private /* synthetic */ zzbau zzaBF;

        public zza(zzbau com_google_android_gms_internal_zzbau, int i, GoogleApiClient googleApiClient, OnConnectionFailedListener onConnectionFailedListener) {
            this.zzaBF = com_google_android_gms_internal_zzbau;
            this.zzaBC = i;
            this.zzaBD = googleApiClient;
            this.zzaBE = onConnectionFailedListener;
            googleApiClient.registerConnectionFailedListener(this);
        }

        public final void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            String valueOf = String.valueOf(connectionResult);
            Log.d("AutoManageHelper", new StringBuilder(String.valueOf(valueOf).length() + 27).append("beginFailureResolution for ").append(valueOf).toString());
            this.zzaBF.zzb(connectionResult, this.zzaBC);
        }
    }

    private zzbau(zzbdt com_google_android_gms_internal_zzbdt) {
        super(com_google_android_gms_internal_zzbdt);
        this.zzaEG.zza("AutoManageHelper", (zzbds) this);
    }

    public static zzbau zza(zzbdr com_google_android_gms_internal_zzbdr) {
        zzbdt zzb = zzbds.zzb(com_google_android_gms_internal_zzbdr);
        zzbau com_google_android_gms_internal_zzbau = (zzbau) zzb.zza("AutoManageHelper", zzbau.class);
        return com_google_android_gms_internal_zzbau != null ? com_google_android_gms_internal_zzbau : new zzbau(zzb);
    }

    @Nullable
    private final zza zzam(int i) {
        return this.zzaBB.size() <= i ? null : (zza) this.zzaBB.get(this.zzaBB.keyAt(i));
    }

    public final void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        for (int i = 0; i < this.zzaBB.size(); i++) {
            zza zzam = zzam(i);
            if (zzam != null) {
                printWriter.append(str).append("GoogleApiClient #").print(zzam.zzaBC);
                printWriter.println(":");
                zzam.zzaBD.dump(String.valueOf(str).concat("  "), fileDescriptor, printWriter, strArr);
            }
        }
    }

    public final void onStart() {
        super.onStart();
        boolean z = this.mStarted;
        String valueOf = String.valueOf(this.zzaBB);
        Log.d("AutoManageHelper", new StringBuilder(String.valueOf(valueOf).length() + 14).append("onStart ").append(z).append(" ").append(valueOf).toString());
        if (this.zzaBN.get() == null) {
            for (int i = 0; i < this.zzaBB.size(); i++) {
                zza zzam = zzam(i);
                if (zzam != null) {
                    zzam.zzaBD.connect();
                }
            }
        }
    }

    public final void onStop() {
        super.onStop();
        for (int i = 0; i < this.zzaBB.size(); i++) {
            zza zzam = zzam(i);
            if (zzam != null) {
                zzam.zzaBD.disconnect();
            }
        }
    }

    public final void zza(int i, GoogleApiClient googleApiClient, OnConnectionFailedListener onConnectionFailedListener) {
        zzbo.zzb((Object) googleApiClient, (Object) "GoogleApiClient instance cannot be null");
        zzbo.zza(this.zzaBB.indexOfKey(i) < 0, "Already managing a GoogleApiClient with id " + i);
        zzbbb com_google_android_gms_internal_zzbbb = (zzbbb) this.zzaBN.get();
        boolean z = this.mStarted;
        String valueOf = String.valueOf(com_google_android_gms_internal_zzbbb);
        Log.d("AutoManageHelper", new StringBuilder(String.valueOf(valueOf).length() + 49).append("starting AutoManage for client ").append(i).append(" ").append(z).append(" ").append(valueOf).toString());
        this.zzaBB.put(i, new zza(this, i, googleApiClient, onConnectionFailedListener));
        if (this.mStarted && com_google_android_gms_internal_zzbbb == null) {
            String valueOf2 = String.valueOf(googleApiClient);
            Log.d("AutoManageHelper", new StringBuilder(String.valueOf(valueOf2).length() + 11).append("connecting ").append(valueOf2).toString());
            googleApiClient.connect();
        }
    }

    protected final void zza(ConnectionResult connectionResult, int i) {
        Log.w("AutoManageHelper", "Unresolved error while connecting client. Stopping auto-manage.");
        if (i < 0) {
            Log.wtf("AutoManageHelper", "AutoManageLifecycleHelper received onErrorResolutionFailed callback but no failing client ID is set", new Exception());
            return;
        }
        zza com_google_android_gms_internal_zzbau_zza = (zza) this.zzaBB.get(i);
        if (com_google_android_gms_internal_zzbau_zza != null) {
            zzal(i);
            OnConnectionFailedListener onConnectionFailedListener = com_google_android_gms_internal_zzbau_zza.zzaBE;
            if (onConnectionFailedListener != null) {
                onConnectionFailedListener.onConnectionFailed(connectionResult);
            }
        }
    }

    public final void zzal(int i) {
        zza com_google_android_gms_internal_zzbau_zza = (zza) this.zzaBB.get(i);
        this.zzaBB.remove(i);
        if (com_google_android_gms_internal_zzbau_zza != null) {
            com_google_android_gms_internal_zzbau_zza.zzaBD.unregisterConnectionFailedListener(com_google_android_gms_internal_zzbau_zza);
            com_google_android_gms_internal_zzbau_zza.zzaBD.disconnect();
        }
    }

    protected final void zzps() {
        for (int i = 0; i < this.zzaBB.size(); i++) {
            zza zzam = zzam(i);
            if (zzam != null) {
                zzam.zzaBD.connect();
            }
        }
    }
}
