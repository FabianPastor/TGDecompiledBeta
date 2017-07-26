package com.google.android.gms.internal;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import java.util.concurrent.atomic.AtomicReference;

public abstract class zzbba extends zzbds implements OnCancelListener {
    protected volatile boolean mStarted;
    protected final AtomicReference<zzbbb> zzaBN;
    private final Handler zzaBO;
    protected final GoogleApiAvailability zzaBd;

    protected zzbba(zzbdt com_google_android_gms_internal_zzbdt) {
        this(com_google_android_gms_internal_zzbdt, GoogleApiAvailability.getInstance());
    }

    private zzbba(zzbdt com_google_android_gms_internal_zzbdt, GoogleApiAvailability googleApiAvailability) {
        super(com_google_android_gms_internal_zzbdt);
        this.zzaBN = new AtomicReference(null);
        this.zzaBO = new Handler(Looper.getMainLooper());
        this.zzaBd = googleApiAvailability;
    }

    private static int zza(@Nullable zzbbb com_google_android_gms_internal_zzbbb) {
        return com_google_android_gms_internal_zzbbb == null ? -1 : com_google_android_gms_internal_zzbbb.zzpy();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void onActivityResult(int i, int i2, Intent intent) {
        Object obj;
        int i3 = 13;
        zzbbb com_google_android_gms_internal_zzbbb = (zzbbb) this.zzaBN.get();
        switch (i) {
            case 1:
                if (i2 != -1) {
                    if (i2 == 0) {
                        if (intent != null) {
                            i3 = intent.getIntExtra("<<ResolutionFailureErrorDetail>>", 13);
                        }
                        zzbbb com_google_android_gms_internal_zzbbb2 = new zzbbb(new ConnectionResult(i3, null), zza(com_google_android_gms_internal_zzbbb));
                        this.zzaBN.set(com_google_android_gms_internal_zzbbb2);
                        com_google_android_gms_internal_zzbbb = com_google_android_gms_internal_zzbbb2;
                        obj = null;
                        break;
                    }
                }
                i3 = 1;
                break;
            case 2:
                int isGooglePlayServicesAvailable = this.zzaBd.isGooglePlayServicesAvailable(getActivity());
                obj = isGooglePlayServicesAvailable == 0 ? 1 : null;
                if (com_google_android_gms_internal_zzbbb == null) {
                    return;
                }
                if (com_google_android_gms_internal_zzbbb.zzpz().getErrorCode() == 18 && isGooglePlayServicesAvailable == 18) {
                    return;
                }
            default:
                obj = null;
                break;
        }
        if (obj != null) {
            zzpx();
        } else if (com_google_android_gms_internal_zzbbb != null) {
            zza(com_google_android_gms_internal_zzbbb.zzpz(), com_google_android_gms_internal_zzbbb.zzpy());
        }
    }

    public void onCancel(DialogInterface dialogInterface) {
        zza(new ConnectionResult(13, null), zza((zzbbb) this.zzaBN.get()));
        zzpx();
    }

    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.zzaBN.set(bundle.getBoolean("resolving_error", false) ? new zzbbb(new ConnectionResult(bundle.getInt("failed_status"), (PendingIntent) bundle.getParcelable("failed_resolution")), bundle.getInt("failed_client_id", -1)) : null);
        }
    }

    public final void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        zzbbb com_google_android_gms_internal_zzbbb = (zzbbb) this.zzaBN.get();
        if (com_google_android_gms_internal_zzbbb != null) {
            bundle.putBoolean("resolving_error", true);
            bundle.putInt("failed_client_id", com_google_android_gms_internal_zzbbb.zzpy());
            bundle.putInt("failed_status", com_google_android_gms_internal_zzbbb.zzpz().getErrorCode());
            bundle.putParcelable("failed_resolution", com_google_android_gms_internal_zzbbb.zzpz().getResolution());
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

    public final void zzb(ConnectionResult connectionResult, int i) {
        zzbbb com_google_android_gms_internal_zzbbb = new zzbbb(connectionResult, i);
        if (this.zzaBN.compareAndSet(null, com_google_android_gms_internal_zzbbb)) {
            this.zzaBO.post(new zzbbc(this, com_google_android_gms_internal_zzbbb));
        }
    }

    protected abstract void zzps();

    protected final void zzpx() {
        this.zzaBN.set(null);
        zzps();
    }
}
