package com.google.android.gms.internal;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzaa;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class zzqm extends zzqp {
    private final SparseArray<zza> yq = new SparseArray();

    private class zza implements OnConnectionFailedListener {
        public final int yr;
        public final GoogleApiClient ys;
        public final OnConnectionFailedListener yt;
        final /* synthetic */ zzqm yu;

        public zza(zzqm com_google_android_gms_internal_zzqm, int i, GoogleApiClient googleApiClient, OnConnectionFailedListener onConnectionFailedListener) {
            this.yu = com_google_android_gms_internal_zzqm;
            this.yr = i;
            this.ys = googleApiClient;
            this.yt = onConnectionFailedListener;
            googleApiClient.registerConnectionFailedListener(this);
        }

        public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            printWriter.append(str).append("GoogleApiClient #").print(this.yr);
            printWriter.println(":");
            this.ys.dump(String.valueOf(str).concat("  "), fileDescriptor, printWriter, strArr);
        }

        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            String valueOf = String.valueOf(connectionResult);
            Log.d("AutoManageHelper", new StringBuilder(String.valueOf(valueOf).length() + 27).append("beginFailureResolution for ").append(valueOf).toString());
            this.yu.zzb(connectionResult, this.yr);
        }

        public void zzarn() {
            this.ys.unregisterConnectionFailedListener(this);
            this.ys.disconnect();
        }
    }

    private zzqm(zzrp com_google_android_gms_internal_zzrp) {
        super(com_google_android_gms_internal_zzrp);
        this.Bf.zza("AutoManageHelper", (zzro) this);
    }

    public static zzqm zza(zzrn com_google_android_gms_internal_zzrn) {
        zzrp zzc = zzro.zzc(com_google_android_gms_internal_zzrn);
        zzqm com_google_android_gms_internal_zzqm = (zzqm) zzc.zza("AutoManageHelper", zzqm.class);
        return com_google_android_gms_internal_zzqm != null ? com_google_android_gms_internal_zzqm : new zzqm(zzc);
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        for (int i = 0; i < this.yq.size(); i++) {
            ((zza) this.yq.valueAt(i)).dump(str, fileDescriptor, printWriter, strArr);
        }
    }

    public void onStart() {
        super.onStart();
        boolean z = this.mStarted;
        String valueOf = String.valueOf(this.yq);
        Log.d("AutoManageHelper", new StringBuilder(String.valueOf(valueOf).length() + 14).append("onStart ").append(z).append(" ").append(valueOf).toString());
        if (!this.yz) {
            for (int i = 0; i < this.yq.size(); i++) {
                ((zza) this.yq.valueAt(i)).ys.connect();
            }
        }
    }

    public void onStop() {
        super.onStop();
        for (int i = 0; i < this.yq.size(); i++) {
            ((zza) this.yq.valueAt(i)).ys.disconnect();
        }
    }

    public void zza(int i, GoogleApiClient googleApiClient, OnConnectionFailedListener onConnectionFailedListener) {
        zzaa.zzb((Object) googleApiClient, (Object) "GoogleApiClient instance cannot be null");
        zzaa.zza(this.yq.indexOfKey(i) < 0, "Already managing a GoogleApiClient with id " + i);
        Log.d("AutoManageHelper", "starting AutoManage for client " + i + " " + this.mStarted + " " + this.yz);
        this.yq.put(i, new zza(this, i, googleApiClient, onConnectionFailedListener));
        if (this.mStarted && !this.yz) {
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
        zza com_google_android_gms_internal_zzqm_zza = (zza) this.yq.get(i);
        if (com_google_android_gms_internal_zzqm_zza != null) {
            zzfs(i);
            OnConnectionFailedListener onConnectionFailedListener = com_google_android_gms_internal_zzqm_zza.yt;
            if (onConnectionFailedListener != null) {
                onConnectionFailedListener.onConnectionFailed(connectionResult);
            }
        }
    }

    protected void zzarm() {
        for (int i = 0; i < this.yq.size(); i++) {
            ((zza) this.yq.valueAt(i)).ys.connect();
        }
    }

    public void zzfs(int i) {
        zza com_google_android_gms_internal_zzqm_zza = (zza) this.yq.get(i);
        this.yq.remove(i);
        if (com_google_android_gms_internal_zzqm_zza != null) {
            com_google_android_gms_internal_zzqm_zza.zzarn();
        }
    }
}
