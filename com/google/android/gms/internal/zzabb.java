package com.google.android.gms.internal;

import android.app.Activity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzb;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.concurrent.CancellationException;

public class zzabb extends zzzw {
    private TaskCompletionSource<Void> zzayo = new TaskCompletionSource();

    private zzabb(zzaax com_google_android_gms_internal_zzaax) {
        super(com_google_android_gms_internal_zzaax);
        this.zzaBs.zza("GmsAvailabilityHelper", (zzaaw) this);
    }

    public static zzabb zzu(Activity activity) {
        zzaax zzs = zzaaw.zzs(activity);
        zzabb com_google_android_gms_internal_zzabb = (zzabb) zzs.zza("GmsAvailabilityHelper", zzabb.class);
        if (com_google_android_gms_internal_zzabb == null) {
            return new zzabb(zzs);
        }
        if (!com_google_android_gms_internal_zzabb.zzayo.getTask().isComplete()) {
            return com_google_android_gms_internal_zzabb;
        }
        com_google_android_gms_internal_zzabb.zzayo = new TaskCompletionSource();
        return com_google_android_gms_internal_zzabb;
    }

    public Task<Void> getTask() {
        return this.zzayo.getTask();
    }

    public void onDestroy() {
        super.onDestroy();
        this.zzayo.setException(new CancellationException("Host activity was destroyed before Google Play services could be made available."));
    }

    protected void zza(ConnectionResult connectionResult, int i) {
        this.zzayo.setException(zzb.zzl(connectionResult));
    }

    public void zzk(ConnectionResult connectionResult) {
        zzb(connectionResult, 0);
    }

    protected void zzuW() {
        int isGooglePlayServicesAvailable = this.zzaxX.isGooglePlayServicesAvailable(this.zzaBs.zzwo());
        if (isGooglePlayServicesAvailable == 0) {
            this.zzayo.setResult(null);
        } else {
            zzk(new ConnectionResult(isGooglePlayServicesAvailable, null));
        }
    }
}
