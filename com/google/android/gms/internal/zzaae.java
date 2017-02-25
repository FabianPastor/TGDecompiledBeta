package com.google.android.gms.internal;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiActivity;

public abstract class zzaae extends zzabe implements OnCancelListener {
    protected boolean mStarted;
    private ConnectionResult zzaAa;
    private int zzaAb;
    private final Handler zzaAc;
    protected boolean zzazZ;
    protected final GoogleApiAvailability zzazn;

    private class zza implements Runnable {
        final /* synthetic */ zzaae zzaAd;

        private zza(zzaae com_google_android_gms_internal_zzaae) {
            this.zzaAd = com_google_android_gms_internal_zzaae;
        }

        @MainThread
        public void run() {
            if (!this.zzaAd.mStarted) {
                return;
            }
            if (this.zzaAd.zzaAa.hasResolution()) {
                this.zzaAd.zzaCR.startActivityForResult(GoogleApiActivity.zzb(this.zzaAd.getActivity(), this.zzaAd.zzaAa.getResolution(), this.zzaAd.zzaAb, false), 1);
            } else if (this.zzaAd.zzazn.isUserResolvableError(this.zzaAd.zzaAa.getErrorCode())) {
                this.zzaAd.zzazn.zza(this.zzaAd.getActivity(), this.zzaAd.zzaCR, this.zzaAd.zzaAa.getErrorCode(), 2, this.zzaAd);
            } else if (this.zzaAd.zzaAa.getErrorCode() == 18) {
                final Dialog zza = this.zzaAd.zzazn.zza(this.zzaAd.getActivity(), this.zzaAd);
                this.zzaAd.zzazn.zza(this.zzaAd.getActivity().getApplicationContext(), new com.google.android.gms.internal.zzaaz.zza(this) {
                    final /* synthetic */ zza zzaAf;

                    public void zzvE() {
                        this.zzaAf.zzaAd.zzvD();
                        if (zza.isShowing()) {
                            zza.dismiss();
                        }
                    }
                });
            } else {
                this.zzaAd.zza(this.zzaAd.zzaAa, this.zzaAd.zzaAb);
            }
        }
    }

    protected zzaae(zzabf com_google_android_gms_internal_zzabf) {
        this(com_google_android_gms_internal_zzabf, GoogleApiAvailability.getInstance());
    }

    zzaae(zzabf com_google_android_gms_internal_zzabf, GoogleApiAvailability googleApiAvailability) {
        super(com_google_android_gms_internal_zzabf);
        this.zzaAb = -1;
        this.zzaAc = new Handler(Looper.getMainLooper());
        this.zzazn = googleApiAvailability;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onActivityResult(int i, int i2, Intent intent) {
        Object obj = 1;
        switch (i) {
            case 1:
                if (i2 != -1) {
                    if (i2 == 0) {
                        this.zzaAa = new ConnectionResult(intent != null ? intent.getIntExtra("<<ResolutionFailureErrorDetail>>", 13) : 13, null);
                    }
                }
                break;
            case 2:
                int isGooglePlayServicesAvailable = this.zzazn.isGooglePlayServicesAvailable(getActivity());
                if (isGooglePlayServicesAvailable != 0) {
                    obj = null;
                }
                if (this.zzaAa.getErrorCode() == 18 && isGooglePlayServicesAvailable == 18) {
                    return;
                }
            default:
                obj = null;
                break;
        }
        if (obj != null) {
            zzvD();
        } else {
            zza(this.zzaAa, this.zzaAb);
        }
    }

    public void onCancel(DialogInterface dialogInterface) {
        zza(new ConnectionResult(13, null), this.zzaAb);
        zzvD();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.zzazZ = bundle.getBoolean("resolving_error", false);
            if (this.zzazZ) {
                this.zzaAb = bundle.getInt("failed_client_id", -1);
                this.zzaAa = new ConnectionResult(bundle.getInt("failed_status"), (PendingIntent) bundle.getParcelable("failed_resolution"));
            }
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("resolving_error", this.zzazZ);
        if (this.zzazZ) {
            bundle.putInt("failed_client_id", this.zzaAb);
            bundle.putInt("failed_status", this.zzaAa.getErrorCode());
            bundle.putParcelable("failed_resolution", this.zzaAa.getResolution());
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

    public void zzb(ConnectionResult connectionResult, int i) {
        if (!this.zzazZ) {
            this.zzazZ = true;
            this.zzaAb = i;
            this.zzaAa = connectionResult;
            this.zzaAc.post(new zza());
        }
    }

    protected void zzvD() {
        this.zzaAb = -1;
        this.zzazZ = false;
        this.zzaAa = null;
        zzvx();
    }

    protected abstract void zzvx();
}
