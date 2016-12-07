package com.google.android.gms.internal;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzac;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class zzzt extends zzzw {
    private final SparseArray<zza> zzayx = new SparseArray();

    private class zza implements OnConnectionFailedListener {
        public final OnConnectionFailedListener zzayA;
        final /* synthetic */ zzzt zzayB;
        public final int zzayy;
        public final GoogleApiClient zzayz;

        public zza(zzzt com_google_android_gms_internal_zzzt, int i, GoogleApiClient googleApiClient, OnConnectionFailedListener onConnectionFailedListener) {
            this.zzayB = com_google_android_gms_internal_zzzt;
            this.zzayy = i;
            this.zzayz = googleApiClient;
            this.zzayA = onConnectionFailedListener;
            googleApiClient.registerConnectionFailedListener(this);
        }

        public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            printWriter.append(str).append("GoogleApiClient #").print(this.zzayy);
            printWriter.println(":");
            this.zzayz.dump(String.valueOf(str).concat("  "), fileDescriptor, printWriter, strArr);
        }

        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            String valueOf = String.valueOf(connectionResult);
            Log.d("AutoManageHelper", new StringBuilder(String.valueOf(valueOf).length() + 27).append("beginFailureResolution for ").append(valueOf).toString());
            this.zzayB.zzb(connectionResult, this.zzayy);
        }

        public void zzuX() {
            this.zzayz.unregisterConnectionFailedListener(this);
            this.zzayz.disconnect();
        }
    }

    private zzzt(zzaax com_google_android_gms_internal_zzaax) {
        super(com_google_android_gms_internal_zzaax);
        this.zzaBs.zza("AutoManageHelper", (zzaaw) this);
    }

    public static zzzt zza(zzaav com_google_android_gms_internal_zzaav) {
        zzaax zzc = zzaaw.zzc(com_google_android_gms_internal_zzaav);
        zzzt com_google_android_gms_internal_zzzt = (zzzt) zzc.zza("AutoManageHelper", zzzt.class);
        return com_google_android_gms_internal_zzzt != null ? com_google_android_gms_internal_zzzt : new zzzt(zzc);
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        for (int i = 0; i < this.zzayx.size(); i++) {
            ((zza) this.zzayx.valueAt(i)).dump(str, fileDescriptor, printWriter, strArr);
        }
    }

    public void onStart() {
        super.onStart();
        boolean z = this.mStarted;
        String valueOf = String.valueOf(this.zzayx);
        Log.d("AutoManageHelper", new StringBuilder(String.valueOf(valueOf).length() + 14).append("onStart ").append(z).append(" ").append(valueOf).toString());
        if (!this.zzayG) {
            for (int i = 0; i < this.zzayx.size(); i++) {
                ((zza) this.zzayx.valueAt(i)).zzayz.connect();
            }
        }
    }

    public void onStop() {
        super.onStop();
        for (int i = 0; i < this.zzayx.size(); i++) {
            ((zza) this.zzayx.valueAt(i)).zzayz.disconnect();
        }
    }

    public void zza(int i, GoogleApiClient googleApiClient, OnConnectionFailedListener onConnectionFailedListener) {
        zzac.zzb((Object) googleApiClient, (Object) "GoogleApiClient instance cannot be null");
        zzac.zza(this.zzayx.indexOfKey(i) < 0, "Already managing a GoogleApiClient with id " + i);
        Log.d("AutoManageHelper", "starting AutoManage for client " + i + " " + this.mStarted + " " + this.zzayG);
        this.zzayx.put(i, new zza(this, i, googleApiClient, onConnectionFailedListener));
        if (this.mStarted && !this.zzayG) {
            String valueOf = String.valueOf(googleApiClient);
            Log.d("AutoManageHelper", new StringBuilder(String.valueOf(valueOf).length() + 11).append("connecting ").append(valueOf).toString());
            googleApiClient.connect();
        }
    }

    protected void zza(ConnectionResult connectionResult, int i) {
        Log.w("AutoManageHelper", "Unresolved error while connecting client. Stopping auto-manage.");
        if (i < 0) {
            Log.wtf("AutoManageHelper", "AutoManageLifecycleHelper received onErrorResolutionFailed callback but no failing client ID is set", new Exception());
            return;
        }
        zza com_google_android_gms_internal_zzzt_zza = (zza) this.zzayx.get(i);
        if (com_google_android_gms_internal_zzzt_zza != null) {
            zzcu(i);
            OnConnectionFailedListener onConnectionFailedListener = com_google_android_gms_internal_zzzt_zza.zzayA;
            if (onConnectionFailedListener != null) {
                onConnectionFailedListener.onConnectionFailed(connectionResult);
            }
        }
    }

    public void zzcu(int i) {
        zza com_google_android_gms_internal_zzzt_zza = (zza) this.zzayx.get(i);
        this.zzayx.remove(i);
        if (com_google_android_gms_internal_zzzt_zza != null) {
            com_google_android_gms_internal_zzzt_zza.zzuX();
        }
    }

    protected void zzuW() {
        for (int i = 0; i < this.zzayx.size(); i++) {
            ((zza) this.zzayx.valueAt(i)).zzayz.connect();
        }
    }
}
