package com.google.android.gms.internal;

import android.app.Activity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzb;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.concurrent.CancellationException;

public class zzrf extends zzqd {
    private TaskCompletionSource<Void> wh = new TaskCompletionSource();

    private zzrf(zzrb com_google_android_gms_internal_zzrb) {
        super(com_google_android_gms_internal_zzrb);
        this.yY.zza("GmsAvailabilityHelper", (zzra) this);
    }

    public static zzrf zzu(Activity activity) {
        zzrb zzs = zzra.zzs(activity);
        zzrf com_google_android_gms_internal_zzrf = (zzrf) zzs.zza("GmsAvailabilityHelper", zzrf.class);
        if (com_google_android_gms_internal_zzrf == null) {
            return new zzrf(zzs);
        }
        if (!com_google_android_gms_internal_zzrf.wh.getTask().isComplete()) {
            return com_google_android_gms_internal_zzrf;
        }
        com_google_android_gms_internal_zzrf.wh = new TaskCompletionSource();
        return com_google_android_gms_internal_zzrf;
    }

    public Task<Void> getTask() {
        return this.wh.getTask();
    }

    public void onDestroy() {
        super.onDestroy();
        this.wh.setException(new CancellationException("Host activity was destroyed before Google Play services could be made available."));
    }

    protected void zza(ConnectionResult connectionResult, int i) {
        this.wh.setException(zzb.zzl(connectionResult));
    }

    protected void zzaqk() {
        int isGooglePlayServicesAvailable = this.vP.isGooglePlayServicesAvailable(this.yY.zzasq());
        if (isGooglePlayServicesAvailable == 0) {
            this.wh.setResult(null);
        } else {
            zzk(new ConnectionResult(isGooglePlayServicesAvailable, null));
        }
    }

    public void zzk(ConnectionResult connectionResult) {
        zzb(connectionResult, 0);
    }
}
