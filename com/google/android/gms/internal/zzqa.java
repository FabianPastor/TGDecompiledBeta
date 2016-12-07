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

public class zzqa extends zzqd {
    private final SparseArray<zza> wq = new SparseArray();

    private class zza implements OnConnectionFailedListener {
        public final int wr;
        public final GoogleApiClient ws;
        public final OnConnectionFailedListener wt;
        final /* synthetic */ zzqa wu;

        public zza(zzqa com_google_android_gms_internal_zzqa, int i, GoogleApiClient googleApiClient, OnConnectionFailedListener onConnectionFailedListener) {
            this.wu = com_google_android_gms_internal_zzqa;
            this.wr = i;
            this.ws = googleApiClient;
            this.wt = onConnectionFailedListener;
            googleApiClient.registerConnectionFailedListener(this);
        }

        public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            printWriter.append(str).append("GoogleApiClient #").print(this.wr);
            printWriter.println(":");
            this.ws.dump(String.valueOf(str).concat("  "), fileDescriptor, printWriter, strArr);
        }

        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            String valueOf = String.valueOf(connectionResult);
            Log.d("AutoManageHelper", new StringBuilder(String.valueOf(valueOf).length() + 27).append("beginFailureResolution for ").append(valueOf).toString());
            this.wu.zzb(connectionResult, this.wr);
        }

        public void zzaql() {
            this.ws.unregisterConnectionFailedListener(this);
            this.ws.disconnect();
        }
    }

    private zzqa(zzrb com_google_android_gms_internal_zzrb) {
        super(com_google_android_gms_internal_zzrb);
        this.yY.zza("AutoManageHelper", (zzra) this);
    }

    public static zzqa zza(zzqz com_google_android_gms_internal_zzqz) {
        zzrb zzc = zzra.zzc(com_google_android_gms_internal_zzqz);
        zzqa com_google_android_gms_internal_zzqa = (zzqa) zzc.zza("AutoManageHelper", zzqa.class);
        return com_google_android_gms_internal_zzqa != null ? com_google_android_gms_internal_zzqa : new zzqa(zzc);
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        for (int i = 0; i < this.wq.size(); i++) {
            ((zza) this.wq.valueAt(i)).dump(str, fileDescriptor, printWriter, strArr);
        }
    }

    public void onStart() {
        super.onStart();
        boolean z = this.mStarted;
        String valueOf = String.valueOf(this.wq);
        Log.d("AutoManageHelper", new StringBuilder(String.valueOf(valueOf).length() + 14).append("onStart ").append(z).append(" ").append(valueOf).toString());
        if (!this.wy) {
            for (int i = 0; i < this.wq.size(); i++) {
                ((zza) this.wq.valueAt(i)).ws.connect();
            }
        }
    }

    public void onStop() {
        super.onStop();
        for (int i = 0; i < this.wq.size(); i++) {
            ((zza) this.wq.valueAt(i)).ws.disconnect();
        }
    }

    public void zza(int i, GoogleApiClient googleApiClient, OnConnectionFailedListener onConnectionFailedListener) {
        zzac.zzb((Object) googleApiClient, (Object) "GoogleApiClient instance cannot be null");
        zzac.zza(this.wq.indexOfKey(i) < 0, "Already managing a GoogleApiClient with id " + i);
        Log.d("AutoManageHelper", "starting AutoManage for client " + i + " " + this.mStarted + " " + this.wy);
        this.wq.put(i, new zza(this, i, googleApiClient, onConnectionFailedListener));
        if (this.mStarted && !this.wy) {
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
        zza com_google_android_gms_internal_zzqa_zza = (zza) this.wq.get(i);
        if (com_google_android_gms_internal_zzqa_zza != null) {
            zzfq(i);
            OnConnectionFailedListener onConnectionFailedListener = com_google_android_gms_internal_zzqa_zza.wt;
            if (onConnectionFailedListener != null) {
                onConnectionFailedListener.onConnectionFailed(connectionResult);
            }
        }
    }

    protected void zzaqk() {
        for (int i = 0; i < this.wq.size(); i++) {
            ((zza) this.wq.valueAt(i)).ws.connect();
        }
    }

    public void zzfq(int i) {
        zza com_google_android_gms_internal_zzqa_zza = (zza) this.wq.get(i);
        this.wq.remove(i);
        if (com_google_android_gms_internal_zzqa_zza != null) {
            com_google_android_gms_internal_zzqa_zza.zzaql();
        }
    }
}
