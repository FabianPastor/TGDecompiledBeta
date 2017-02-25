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

public class zzaaa extends zzaae {
    private final SparseArray<zza> zzazN = new SparseArray();

    private class zza implements OnConnectionFailedListener {
        public final int zzazO;
        public final GoogleApiClient zzazP;
        public final OnConnectionFailedListener zzazQ;
        final /* synthetic */ zzaaa zzazR;

        public zza(zzaaa com_google_android_gms_internal_zzaaa, int i, GoogleApiClient googleApiClient, OnConnectionFailedListener onConnectionFailedListener) {
            this.zzazR = com_google_android_gms_internal_zzaaa;
            this.zzazO = i;
            this.zzazP = googleApiClient;
            this.zzazQ = onConnectionFailedListener;
            googleApiClient.registerConnectionFailedListener(this);
        }

        public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            printWriter.append(str).append("GoogleApiClient #").print(this.zzazO);
            printWriter.println(":");
            this.zzazP.dump(String.valueOf(str).concat("  "), fileDescriptor, printWriter, strArr);
        }

        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            String valueOf = String.valueOf(connectionResult);
            Log.d("AutoManageHelper", new StringBuilder(String.valueOf(valueOf).length() + 27).append("beginFailureResolution for ").append(valueOf).toString());
            this.zzazR.zzb(connectionResult, this.zzazO);
        }

        public void zzvy() {
            this.zzazP.unregisterConnectionFailedListener(this);
            this.zzazP.disconnect();
        }
    }

    private zzaaa(zzabf com_google_android_gms_internal_zzabf) {
        super(com_google_android_gms_internal_zzabf);
        this.zzaCR.zza("AutoManageHelper", (zzabe) this);
    }

    public static zzaaa zza(zzabd com_google_android_gms_internal_zzabd) {
        zzabf zzc = zzabe.zzc(com_google_android_gms_internal_zzabd);
        zzaaa com_google_android_gms_internal_zzaaa = (zzaaa) zzc.zza("AutoManageHelper", zzaaa.class);
        return com_google_android_gms_internal_zzaaa != null ? com_google_android_gms_internal_zzaaa : new zzaaa(zzc);
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        for (int i = 0; i < this.zzazN.size(); i++) {
            ((zza) this.zzazN.valueAt(i)).dump(str, fileDescriptor, printWriter, strArr);
        }
    }

    public void onStart() {
        super.onStart();
        boolean z = this.mStarted;
        String valueOf = String.valueOf(this.zzazN);
        Log.d("AutoManageHelper", new StringBuilder(String.valueOf(valueOf).length() + 14).append("onStart ").append(z).append(" ").append(valueOf).toString());
        if (!this.zzazZ) {
            for (int i = 0; i < this.zzazN.size(); i++) {
                ((zza) this.zzazN.valueAt(i)).zzazP.connect();
            }
        }
    }

    public void onStop() {
        super.onStop();
        for (int i = 0; i < this.zzazN.size(); i++) {
            ((zza) this.zzazN.valueAt(i)).zzazP.disconnect();
        }
    }

    public void zza(int i, GoogleApiClient googleApiClient, OnConnectionFailedListener onConnectionFailedListener) {
        zzac.zzb((Object) googleApiClient, (Object) "GoogleApiClient instance cannot be null");
        zzac.zza(this.zzazN.indexOfKey(i) < 0, "Already managing a GoogleApiClient with id " + i);
        Log.d("AutoManageHelper", "starting AutoManage for client " + i + " " + this.mStarted + " " + this.zzazZ);
        this.zzazN.put(i, new zza(this, i, googleApiClient, onConnectionFailedListener));
        if (this.mStarted && !this.zzazZ) {
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
        zza com_google_android_gms_internal_zzaaa_zza = (zza) this.zzazN.get(i);
        if (com_google_android_gms_internal_zzaaa_zza != null) {
            zzcA(i);
            OnConnectionFailedListener onConnectionFailedListener = com_google_android_gms_internal_zzaaa_zza.zzazQ;
            if (onConnectionFailedListener != null) {
                onConnectionFailedListener.onConnectionFailed(connectionResult);
            }
        }
    }

    public void zzcA(int i) {
        zza com_google_android_gms_internal_zzaaa_zza = (zza) this.zzazN.get(i);
        this.zzazN.remove(i);
        if (com_google_android_gms_internal_zzaaa_zza != null) {
            com_google_android_gms_internal_zzaaa_zza.zzvy();
        }
    }

    protected void zzvx() {
        for (int i = 0; i < this.zzazN.size(); i++) {
            ((zza) this.zzazN.valueAt(i)).zzazP.connect();
        }
    }
}
