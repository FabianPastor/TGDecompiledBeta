package com.google.android.gms.internal;

import android.app.Activity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzb;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.concurrent.CancellationException;

public class zzbeb extends zzbba {
    private TaskCompletionSource<Void> zzalE = new TaskCompletionSource();

    private zzbeb(zzbdt com_google_android_gms_internal_zzbdt) {
        super(com_google_android_gms_internal_zzbdt);
        this.zzaEG.zza("GmsAvailabilityHelper", (zzbds) this);
    }

    public static zzbeb zzp(Activity activity) {
        zzbdt zzn = zzbds.zzn(activity);
        zzbeb com_google_android_gms_internal_zzbeb = (zzbeb) zzn.zza("GmsAvailabilityHelper", zzbeb.class);
        if (com_google_android_gms_internal_zzbeb == null) {
            return new zzbeb(zzn);
        }
        if (!com_google_android_gms_internal_zzbeb.zzalE.getTask().isComplete()) {
            return com_google_android_gms_internal_zzbeb;
        }
        com_google_android_gms_internal_zzbeb.zzalE = new TaskCompletionSource();
        return com_google_android_gms_internal_zzbeb;
    }

    public final Task<Void> getTask() {
        return this.zzalE.getTask();
    }

    public final void onDestroy() {
        super.onDestroy();
        this.zzalE.setException(new CancellationException("Host activity was destroyed before Google Play services could be made available."));
    }

    protected final void zza(ConnectionResult connectionResult, int i) {
        this.zzalE.setException(zzb.zzx(new Status(connectionResult.getErrorCode(), connectionResult.getErrorMessage(), connectionResult.getResolution())));
    }

    protected final void zzps() {
        int isGooglePlayServicesAvailable = this.zzaBd.isGooglePlayServicesAvailable(this.zzaEG.zzqF());
        if (isGooglePlayServicesAvailable == 0) {
            this.zzalE.setResult(null);
        } else if (!this.zzalE.getTask().isComplete()) {
            zzb(new ConnectionResult(isGooglePlayServicesAvailable, null), 0);
        }
    }
}
