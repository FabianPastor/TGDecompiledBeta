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

public class zzbat extends zzbaz {
    private final SparseArray<zza> zzaBB = new SparseArray();

    class zza implements OnConnectionFailedListener {
        public final int zzaBC;
        public final GoogleApiClient zzaBD;
        public final OnConnectionFailedListener zzaBE;
        private /* synthetic */ zzbat zzaBF;

        public zza(zzbat com_google_android_gms_internal_zzbat, int i, GoogleApiClient googleApiClient, OnConnectionFailedListener onConnectionFailedListener) {
            this.zzaBF = com_google_android_gms_internal_zzbat;
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

    private zzbat(zzbds com_google_android_gms_internal_zzbds) {
        super(com_google_android_gms_internal_zzbds);
        this.zzaEG.zza("AutoManageHelper", (zzbdr) this);
    }

    public static zzbat zza(zzbdq com_google_android_gms_internal_zzbdq) {
        zzbds zzb = zzbdr.zzb(com_google_android_gms_internal_zzbdq);
        zzbat com_google_android_gms_internal_zzbat = (zzbat) zzb.zza("AutoManageHelper", zzbat.class);
        return com_google_android_gms_internal_zzbat != null ? com_google_android_gms_internal_zzbat : new zzbat(zzb);
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
        zzbba com_google_android_gms_internal_zzbba = (zzbba) this.zzaBN.get();
        boolean z = this.mStarted;
        String valueOf = String.valueOf(com_google_android_gms_internal_zzbba);
        Log.d("AutoManageHelper", new StringBuilder(String.valueOf(valueOf).length() + 49).append("starting AutoManage for client ").append(i).append(" ").append(z).append(" ").append(valueOf).toString());
        this.zzaBB.put(i, new zza(this, i, googleApiClient, onConnectionFailedListener));
        if (this.mStarted && com_google_android_gms_internal_zzbba == null) {
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
        zza com_google_android_gms_internal_zzbat_zza = (zza) this.zzaBB.get(i);
        if (com_google_android_gms_internal_zzbat_zza != null) {
            zzal(i);
            OnConnectionFailedListener onConnectionFailedListener = com_google_android_gms_internal_zzbat_zza.zzaBE;
            if (onConnectionFailedListener != null) {
                onConnectionFailedListener.onConnectionFailed(connectionResult);
            }
        }
    }

    public final void zzal(int i) {
        zza com_google_android_gms_internal_zzbat_zza = (zza) this.zzaBB.get(i);
        this.zzaBB.remove(i);
        if (com_google_android_gms_internal_zzbat_zza != null) {
            com_google_android_gms_internal_zzbat_zza.zzaBD.unregisterConnectionFailedListener(com_google_android_gms_internal_zzbat_zza);
            com_google_android_gms_internal_zzbat_zza.zzaBD.disconnect();
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
