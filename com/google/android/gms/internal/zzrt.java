package com.google.android.gms.internal;

import android.app.Activity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzb;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.concurrent.CancellationException;

public class zzrt extends zzqp {
    private TaskCompletionSource<Void> yg = new TaskCompletionSource();

    private zzrt(zzrp com_google_android_gms_internal_zzrp) {
        super(com_google_android_gms_internal_zzrp);
        this.Bf.zza("GmsAvailabilityHelper", (zzro) this);
    }

    public static zzrt zzu(Activity activity) {
        zzrp zzs = zzro.zzs(activity);
        zzrt com_google_android_gms_internal_zzrt = (zzrt) zzs.zza("GmsAvailabilityHelper", zzrt.class);
        if (com_google_android_gms_internal_zzrt == null) {
            return new zzrt(zzs);
        }
        if (!com_google_android_gms_internal_zzrt.yg.getTask().isComplete()) {
            return com_google_android_gms_internal_zzrt;
        }
        com_google_android_gms_internal_zzrt.yg = new TaskCompletionSource();
        return com_google_android_gms_internal_zzrt;
    }

    public Task<Void> getTask() {
        return this.yg.getTask();
    }

    public void onDestroy() {
        super.onDestroy();
        this.yg.setException(new CancellationException("Host activity was destroyed before Google Play services could be made available."));
    }

    protected void zza(ConnectionResult connectionResult, int i) {
        this.yg.setException(zzb.zzk(connectionResult));
    }

    protected void zzarm() {
        int isGooglePlayServicesAvailable = this.xP.isGooglePlayServicesAvailable(this.Bf.zzaty());
        if (isGooglePlayServicesAvailable == 0) {
            this.yg.setResult(null);
        } else {
            zzj(new ConnectionResult(isGooglePlayServicesAvailable, null));
        }
    }

    public void zzj(ConnectionResult connectionResult) {
        zzb(connectionResult, 0);
    }
}
