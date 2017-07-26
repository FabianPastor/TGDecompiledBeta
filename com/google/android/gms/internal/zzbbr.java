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
    private /* synthetic */ zzbbp zzaCP;

    private zzbbr(zzbbp com_google_android_gms_internal_zzbbp) {
        this.zzaCP = com_google_android_gms_internal_zzbbp;
    }

    public final void onComplete(@NonNull Task<Void> task) {
        this.zzaCP.zzaCv.lock();
        try {
            if (this.zzaCP.zzaCK) {
                if (task.isSuccessful()) {
                    this.zzaCP.zzaCL = new ArrayMap(this.zzaCP.zzaCB.size());
                    for (zzbbo zzph : this.zzaCP.zzaCB.values()) {
                        this.zzaCP.zzaCL.put(zzph.zzph(), ConnectionResult.zzazX);
                    }
                } else if (task.getException() instanceof zza) {
                    zza com_google_android_gms_common_api_zza = (zza) task.getException();
                    if (this.zzaCP.zzaCI) {
                        this.zzaCP.zzaCL = new ArrayMap(this.zzaCP.zzaCB.size());
                        for (zzbbo com_google_android_gms_internal_zzbbo : this.zzaCP.zzaCB.values()) {
                            zzbat zzph2 = com_google_android_gms_internal_zzbbo.zzph();
                            ConnectionResult zza = com_google_android_gms_common_api_zza.zza(com_google_android_gms_internal_zzbbo);
                            if (this.zzaCP.zza(com_google_android_gms_internal_zzbbo, zza)) {
                                this.zzaCP.zzaCL.put(zzph2, new ConnectionResult(16));
                            } else {
                                this.zzaCP.zzaCL.put(zzph2, zza);
                            }
                        }
                    } else {
                        this.zzaCP.zzaCL = com_google_android_gms_common_api_zza.zzpf();
                    }
                    this.zzaCP.zzaCO = this.zzaCP.zzpN();
                } else {
                    Log.e("ConnectionlessGAC", "Unexpected availability exception", task.getException());
                    this.zzaCP.zzaCL = Collections.emptyMap();
                    this.zzaCP.zzaCO = new ConnectionResult(8);
                }
                if (this.zzaCP.zzaCM != null) {
                    this.zzaCP.zzaCL.putAll(this.zzaCP.zzaCM);
                    this.zzaCP.zzaCO = this.zzaCP.zzpN();
                }
                if (this.zzaCP.zzaCO == null) {
                    this.zzaCP.zzpL();
                    this.zzaCP.zzpM();
                } else {
                    this.zzaCP.zzaCK = false;
                    this.zzaCP.zzaCE.zzc(this.zzaCP.zzaCO);
                }
                this.zzaCP.zzaCG.signalAll();
                this.zzaCP.zzaCv.unlock();
            }
        } finally {
            this.zzaCP.zzaCv.unlock();
        }
    }
}
