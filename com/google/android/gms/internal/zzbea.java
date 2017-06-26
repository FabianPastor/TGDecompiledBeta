package com.google.android.gms.internal;

import android.app.Activity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzb;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.concurrent.CancellationException;

public class zzbea extends zzbaz {
    private TaskCompletionSource<Void> zzalE = new TaskCompletionSource();

    private zzbea(zzbds com_google_android_gms_internal_zzbds) {
        super(com_google_android_gms_internal_zzbds);
        this.zzaEG.zza("GmsAvailabilityHelper", (zzbdr) this);
    }

    public static zzbea zzp(Activity activity) {
        zzbds zzn = zzbdr.zzn(activity);
        zzbea com_google_android_gms_internal_zzbea = (zzbea) zzn.zza("GmsAvailabilityHelper", zzbea.class);
        if (com_google_android_gms_internal_zzbea == null) {
            return new zzbea(zzn);
        }
        if (!com_google_android_gms_internal_zzbea.zzalE.getTask().isComplete()) {
            return com_google_android_gms_internal_zzbea;
        }
        com_google_android_gms_internal_zzbea.zzalE = new TaskCompletionSource();
        return com_google_android_gms_internal_zzbea;
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
