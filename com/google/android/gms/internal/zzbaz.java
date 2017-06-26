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

public abstract class zzbaz extends zzbdr implements OnCancelListener {
    protected volatile boolean mStarted;
    protected final AtomicReference<zzbba> zzaBN;
    private final Handler zzaBO;
    protected final GoogleApiAvailability zzaBd;

    protected zzbaz(zzbds com_google_android_gms_internal_zzbds) {
        this(com_google_android_gms_internal_zzbds, GoogleApiAvailability.getInstance());
    }

    private zzbaz(zzbds com_google_android_gms_internal_zzbds, GoogleApiAvailability googleApiAvailability) {
        super(com_google_android_gms_internal_zzbds);
        this.zzaBN = new AtomicReference(null);
        this.zzaBO = new Handler(Looper.getMainLooper());
        this.zzaBd = googleApiAvailability;
    }

    private static int zza(@Nullable zzbba com_google_android_gms_internal_zzbba) {
        return com_google_android_gms_internal_zzbba == null ? -1 : com_google_android_gms_internal_zzbba.zzpy();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void onActivityResult(int i, int i2, Intent intent) {
        Object obj;
        int i3 = 13;
        zzbba com_google_android_gms_internal_zzbba = (zzbba) this.zzaBN.get();
        switch (i) {
            case 1:
                if (i2 != -1) {
                    if (i2 == 0) {
                        if (intent != null) {
                            i3 = intent.getIntExtra("<<ResolutionFailureErrorDetail>>", 13);
                        }
                        zzbba com_google_android_gms_internal_zzbba2 = new zzbba(new ConnectionResult(i3, null), zza(com_google_android_gms_internal_zzbba));
                        this.zzaBN.set(com_google_android_gms_internal_zzbba2);
                        com_google_android_gms_internal_zzbba = com_google_android_gms_internal_zzbba2;
                        obj = null;
                        break;
                    }
                }
                i3 = 1;
                break;
            case 2:
                int isGooglePlayServicesAvailable = this.zzaBd.isGooglePlayServicesAvailable(getActivity());
                obj = isGooglePlayServicesAvailable == 0 ? 1 : null;
                if (com_google_android_gms_internal_zzbba == null) {
                    return;
                }
                if (com_google_android_gms_internal_zzbba.zzpz().getErrorCode() == 18 && isGooglePlayServicesAvailable == 18) {
                    return;
                }
            default:
                obj = null;
                break;
        }
        if (obj != null) {
            zzpx();
        } else if (com_google_android_gms_internal_zzbba != null) {
            zza(com_google_android_gms_internal_zzbba.zzpz(), com_google_android_gms_internal_zzbba.zzpy());
        }
    }

    public void onCancel(DialogInterface dialogInterface) {
        zza(new ConnectionResult(13, null), zza((zzbba) this.zzaBN.get()));
        zzpx();
    }

    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.zzaBN.set(bundle.getBoolean("resolving_error", false) ? new zzbba(new ConnectionResult(bundle.getInt("failed_status"), (PendingIntent) bundle.getParcelable("failed_resolution")), bundle.getInt("failed_client_id", -1)) : null);
        }
    }

    public final void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        zzbba com_google_android_gms_internal_zzbba = (zzbba) this.zzaBN.get();
        if (com_google_android_gms_internal_zzbba != null) {
            bundle.putBoolean("resolving_error", true);
            bundle.putInt("failed_client_id", com_google_android_gms_internal_zzbba.zzpy());
            bundle.putInt("failed_status", com_google_android_gms_internal_zzbba.zzpz().getErrorCode());
            bundle.putParcelable("failed_resolution", com_google_android_gms_internal_zzbba.zzpz().getResolution());
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
        zzbba com_google_android_gms_internal_zzbba = new zzbba(connectionResult, i);
        if (this.zzaBN.compareAndSet(null, com_google_android_gms_internal_zzbba)) {
            this.zzaBO.post(new zzbbb(this, com_google_android_gms_internal_zzbba));
        }
    }

    protected abstract void zzps();

    protected final void zzpx() {
        this.zzaBN.set(null);
        zzps();
    }
}
