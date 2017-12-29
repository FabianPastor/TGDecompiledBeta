package com.google.android.gms.common.api.internal;

import android.support.v4.util.ArrayMap;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.AvailabilityException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.Collections;
import java.util.Map;

final class zzac implements OnCompleteListener<Map<zzh<?>, String>> {
    private /* synthetic */ zzaa zzfqm;

    private zzac(zzaa com_google_android_gms_common_api_internal_zzaa) {
        this.zzfqm = com_google_android_gms_common_api_internal_zzaa;
    }

    public final void onComplete(Task<Map<zzh<?>, String>> task) {
        this.zzfqm.zzfps.lock();
        try {
            if (this.zzfqm.zzfqh) {
                if (task.isSuccessful()) {
                    this.zzfqm.zzfqi = new ArrayMap(this.zzfqm.zzfpy.size());
                    for (zzz zzagn : this.zzfqm.zzfpy.values()) {
                        this.zzfqm.zzfqi.put(zzagn.zzagn(), ConnectionResult.zzfkr);
                    }
                } else if (task.getException() instanceof AvailabilityException) {
                    AvailabilityException availabilityException = (AvailabilityException) task.getException();
                    if (this.zzfqm.zzfqf) {
                        this.zzfqm.zzfqi = new ArrayMap(this.zzfqm.zzfpy.size());
                        for (zzz com_google_android_gms_common_api_internal_zzz : this.zzfqm.zzfpy.values()) {
                            zzh zzagn2 = com_google_android_gms_common_api_internal_zzz.zzagn();
                            ConnectionResult connectionResult = availabilityException.getConnectionResult(com_google_android_gms_common_api_internal_zzz);
                            if (this.zzfqm.zza(com_google_android_gms_common_api_internal_zzz, connectionResult)) {
                                this.zzfqm.zzfqi.put(zzagn2, new ConnectionResult(16));
                            } else {
                                this.zzfqm.zzfqi.put(zzagn2, connectionResult);
                            }
                        }
                    } else {
                        this.zzfqm.zzfqi = availabilityException.zzagj();
                    }
                    this.zzfqm.zzfql = this.zzfqm.zzaht();
                } else {
                    Log.e("ConnectionlessGAC", "Unexpected availability exception", task.getException());
                    this.zzfqm.zzfqi = Collections.emptyMap();
                    this.zzfqm.zzfql = new ConnectionResult(8);
                }
                if (this.zzfqm.zzfqj != null) {
                    this.zzfqm.zzfqi.putAll(this.zzfqm.zzfqj);
                    this.zzfqm.zzfql = this.zzfqm.zzaht();
                }
                if (this.zzfqm.zzfql == null) {
                    this.zzfqm.zzahr();
                    this.zzfqm.zzahs();
                } else {
                    this.zzfqm.zzfqh = false;
                    this.zzfqm.zzfqb.zzc(this.zzfqm.zzfql);
                }
                this.zzfqm.zzfqd.signalAll();
                this.zzfqm.zzfps.unlock();
            }
        } finally {
            this.zzfqm.zzfps.unlock();
        }
    }
}
