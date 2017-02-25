package com.google.android.gms.internal;

import android.app.Activity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzb;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.concurrent.CancellationException;

public class zzabj extends zzaae {
    private TaskCompletionSource<Void> zzazE = new TaskCompletionSource();

    private zzabj(zzabf com_google_android_gms_internal_zzabf) {
        super(com_google_android_gms_internal_zzabf);
        this.zzaCR.zza("GmsAvailabilityHelper", (zzabe) this);
    }

    public static zzabj zzu(Activity activity) {
        zzabf zzs = zzabe.zzs(activity);
        zzabj com_google_android_gms_internal_zzabj = (zzabj) zzs.zza("GmsAvailabilityHelper", zzabj.class);
        if (com_google_android_gms_internal_zzabj == null) {
            return new zzabj(zzs);
        }
        if (!com_google_android_gms_internal_zzabj.zzazE.getTask().isComplete()) {
            return com_google_android_gms_internal_zzabj;
        }
        com_google_android_gms_internal_zzabj.zzazE = new TaskCompletionSource();
        return com_google_android_gms_internal_zzabj;
    }

    public Task<Void> getTask() {
        return this.zzazE.getTask();
    }

    public void onDestroy() {
        super.onDestroy();
        this.zzazE.setException(new CancellationException("Host activity was destroyed before Google Play services could be made available."));
    }

    protected void zza(ConnectionResult connectionResult, int i) {
        this.zzazE.setException(zzb.zzl(connectionResult));
    }

    public void zzk(ConnectionResult connectionResult) {
        zzb(connectionResult, 0);
    }

    protected void zzvx() {
        int isGooglePlayServicesAvailable = this.zzazn.isGooglePlayServicesAvailable(this.zzaCR.zzwV());
        if (isGooglePlayServicesAvailable == 0) {
            this.zzazE.setResult(null);
        } else {
            zzk(new ConnectionResult(isGooglePlayServicesAvailable, null));
        }
    }
}
