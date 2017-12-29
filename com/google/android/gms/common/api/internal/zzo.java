package com.google.android.gms.common.api.internal;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import java.util.concurrent.atomic.AtomicReference;

public abstract class zzo extends LifecycleCallback implements OnCancelListener {
    protected volatile boolean mStarted;
    protected final GoogleApiAvailability zzfmy;
    protected final AtomicReference<zzp> zzfol;
    private final Handler zzfom;

    protected zzo(zzcf com_google_android_gms_common_api_internal_zzcf) {
        this(com_google_android_gms_common_api_internal_zzcf, GoogleApiAvailability.getInstance());
    }

    private zzo(zzcf com_google_android_gms_common_api_internal_zzcf, GoogleApiAvailability googleApiAvailability) {
        super(com_google_android_gms_common_api_internal_zzcf);
        this.zzfol = new AtomicReference(null);
        this.zzfom = new Handler(Looper.getMainLooper());
        this.zzfmy = googleApiAvailability;
    }

    private static int zza(zzp com_google_android_gms_common_api_internal_zzp) {
        return com_google_android_gms_common_api_internal_zzp == null ? -1 : com_google_android_gms_common_api_internal_zzp.zzahe();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void onActivityResult(int i, int i2, Intent intent) {
        Object obj;
        int i3 = 13;
        zzp com_google_android_gms_common_api_internal_zzp = (zzp) this.zzfol.get();
        switch (i) {
            case 1:
                if (i2 != -1) {
                    if (i2 == 0) {
                        if (intent != null) {
                            i3 = intent.getIntExtra("<<ResolutionFailureErrorDetail>>", 13);
                        }
                        zzp com_google_android_gms_common_api_internal_zzp2 = new zzp(new ConnectionResult(i3, null), zza(com_google_android_gms_common_api_internal_zzp));
                        this.zzfol.set(com_google_android_gms_common_api_internal_zzp2);
                        com_google_android_gms_common_api_internal_zzp = com_google_android_gms_common_api_internal_zzp2;
                        obj = null;
                        break;
                    }
                }
                i3 = 1;
                break;
            case 2:
                int isGooglePlayServicesAvailable = this.zzfmy.isGooglePlayServicesAvailable(getActivity());
                obj = isGooglePlayServicesAvailable == 0 ? 1 : null;
                if (com_google_android_gms_common_api_internal_zzp == null) {
                    return;
                }
                if (com_google_android_gms_common_api_internal_zzp.zzahf().getErrorCode() == 18 && isGooglePlayServicesAvailable == 18) {
                    return;
                }
            default:
                obj = null;
                break;
        }
        if (obj != null) {
            zzahd();
        } else if (com_google_android_gms_common_api_internal_zzp != null) {
            zza(com_google_android_gms_common_api_internal_zzp.zzahf(), com_google_android_gms_common_api_internal_zzp.zzahe());
        }
    }

    public void onCancel(DialogInterface dialogInterface) {
        zza(new ConnectionResult(13, null), zza((zzp) this.zzfol.get()));
        zzahd();
    }

    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.zzfol.set(bundle.getBoolean("resolving_error", false) ? new zzp(new ConnectionResult(bundle.getInt("failed_status"), (PendingIntent) bundle.getParcelable("failed_resolution")), bundle.getInt("failed_client_id", -1)) : null);
        }
    }

    public final void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        zzp com_google_android_gms_common_api_internal_zzp = (zzp) this.zzfol.get();
        if (com_google_android_gms_common_api_internal_zzp != null) {
            bundle.putBoolean("resolving_error", true);
            bundle.putInt("failed_client_id", com_google_android_gms_common_api_internal_zzp.zzahe());
            bundle.putInt("failed_status", com_google_android_gms_common_api_internal_zzp.zzahf().getErrorCode());
            bundle.putParcelable("failed_resolution", com_google_android_gms_common_api_internal_zzp.zzahf().getResolution());
        }
    }

    public void onStart() {
        super.onStart();
        this.mStarted = true;
    }

    public void onStop() {
        super.onStop();
        this.mStarted = false;
    }

    protected abstract void zza(ConnectionResult connectionResult, int i);

    protected abstract void zzagz();

    protected final void zzahd() {
        this.zzfol.set(null);
        zzagz();
    }

    public final void zzb(ConnectionResult connectionResult, int i) {
        zzp com_google_android_gms_common_api_internal_zzp = new zzp(connectionResult, i);
        if (this.zzfol.compareAndSet(null, com_google_android_gms_common_api_internal_zzp)) {
            this.zzfom.post(new zzq(this, com_google_android_gms_common_api_internal_zzp));
        }
    }
}
