package com.google.android.gms.internal;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.zza;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.Collections;

final class zzbbr implements OnCompleteListener<Void> {
    private /* synthetic */ zzbbo zzaCP;
    private zzbeh zzaCQ;

    zzbbr(zzbbo com_google_android_gms_internal_zzbbo, zzbeh com_google_android_gms_internal_zzbeh) {
        this.zzaCP = com_google_android_gms_internal_zzbbo;
        this.zzaCQ = com_google_android_gms_internal_zzbeh;
    }

    final void cancel() {
        this.zzaCQ.zzmF();
    }

    public final void onComplete(@NonNull Task<Void> task) {
        this.zzaCP.zzaCv.lock();
        try {
            if (this.zzaCP.zzaCK) {
                if (task.isSuccessful()) {
                    this.zzaCP.zzaCM = new ArrayMap(this.zzaCP.zzaCC.size());
                    for (zzbbn zzph : this.zzaCP.zzaCC.values()) {
                        this.zzaCP.zzaCM.put(zzph.zzph(), ConnectionResult.zzazX);
                    }
                } else if (task.getException() instanceof zza) {
                    zza com_google_android_gms_common_api_zza = (zza) task.getException();
                    if (this.zzaCP.zzaCI) {
                        this.zzaCP.zzaCM = new ArrayMap(this.zzaCP.zzaCC.size());
                        for (zzbbn com_google_android_gms_internal_zzbbn : this.zzaCP.zzaCC.values()) {
                            zzbas zzph2 = com_google_android_gms_internal_zzbbn.zzph();
                            ConnectionResult zza = com_google_android_gms_common_api_zza.zza(com_google_android_gms_internal_zzbbn);
                            if (this.zzaCP.zza(com_google_android_gms_internal_zzbbn, zza)) {
                                this.zzaCP.zzaCM.put(zzph2, new ConnectionResult(16));
                            } else {
                                this.zzaCP.zzaCM.put(zzph2, zza);
                            }
                        }
                    } else {
                        this.zzaCP.zzaCM = com_google_android_gms_common_api_zza.zzpf();
                    }
                } else {
                    Log.e("ConnectionlessGAC", "Unexpected availability exception", task.getException());
                    this.zzaCP.zzaCM = Collections.emptyMap();
                }
                if (this.zzaCP.isConnected()) {
                    this.zzaCP.zzaCL.putAll(this.zzaCP.zzaCM);
                    if (this.zzaCP.zzpN() == null) {
                        this.zzaCP.zzpL();
                        this.zzaCP.zzpM();
                        this.zzaCP.zzaCG.signalAll();
                    }
                }
                this.zzaCQ.zzmF();
                this.zzaCP.zzaCv.unlock();
                return;
            }
            this.zzaCQ.zzmF();
        } finally {
            this.zzaCP.zzaCv.unlock();
        }
    }
}
