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

public abstract class zzqd extends zzra implements OnCancelListener {
    protected boolean mStarted;
    protected final GoogleApiAvailability vP;
    private int wA;
    private final Handler wB;
    protected boolean wy;
    private ConnectionResult wz;

    private class zza implements Runnable {
        final /* synthetic */ zzqd wC;

        private zza(zzqd com_google_android_gms_internal_zzqd) {
            this.wC = com_google_android_gms_internal_zzqd;
        }

        @MainThread
        public void run() {
            if (!this.wC.mStarted) {
                return;
            }
            if (this.wC.wz.hasResolution()) {
                this.wC.yY.startActivityForResult(GoogleApiActivity.zzb(this.wC.getActivity(), this.wC.wz.getResolution(), this.wC.wA, false), 1);
            } else if (this.wC.vP.isUserResolvableError(this.wC.wz.getErrorCode())) {
                this.wC.vP.zza(this.wC.getActivity(), this.wC.yY, this.wC.wz.getErrorCode(), 2, this.wC);
            } else if (this.wC.wz.getErrorCode() == 18) {
                final Dialog zza = this.wC.vP.zza(this.wC.getActivity(), this.wC);
                this.wC.vP.zza(this.wC.getActivity().getApplicationContext(), new com.google.android.gms.internal.zzqv.zza(this) {
                    final /* synthetic */ zza wE;

                    public void zzaqp() {
                        this.wE.wC.zzaqo();
                        if (zza.isShowing()) {
                            zza.dismiss();
                        }
                    }
                });
            } else {
                this.wC.zza(this.wC.wz, this.wC.wA);
            }
        }
    }

    protected zzqd(zzrb com_google_android_gms_internal_zzrb) {
        this(com_google_android_gms_internal_zzrb, GoogleApiAvailability.getInstance());
    }

    zzqd(zzrb com_google_android_gms_internal_zzrb, GoogleApiAvailability googleApiAvailability) {
        super(com_google_android_gms_internal_zzrb);
        this.wA = -1;
        this.wB = new Handler(Looper.getMainLooper());
        this.vP = googleApiAvailability;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onActivityResult(int i, int i2, Intent intent) {
        Object obj = 1;
        switch (i) {
            case 1:
                if (i2 != -1) {
                    if (i2 == 0) {
                        this.wz = new ConnectionResult(intent != null ? intent.getIntExtra("<<ResolutionFailureErrorDetail>>", 13) : 13, null);
                    }
                }
                break;
            case 2:
                int isGooglePlayServicesAvailable = this.vP.isGooglePlayServicesAvailable(getActivity());
                if (isGooglePlayServicesAvailable != 0) {
                    obj = null;
                }
                if (this.wz.getErrorCode() == 18 && isGooglePlayServicesAvailable == 18) {
                    return;
                }
            default:
                obj = null;
                break;
        }
        if (obj != null) {
            zzaqo();
        } else {
            zza(this.wz, this.wA);
        }
    }

    public void onCancel(DialogInterface dialogInterface) {
        zza(new ConnectionResult(13, null), this.wA);
        zzaqo();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.wy = bundle.getBoolean("resolving_error", false);
            if (this.wy) {
                this.wA = bundle.getInt("failed_client_id", -1);
                this.wz = new ConnectionResult(bundle.getInt("failed_status"), (PendingIntent) bundle.getParcelable("failed_resolution"));
            }
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("resolving_error", this.wy);
        if (this.wy) {
            bundle.putInt("failed_client_id", this.wA);
            bundle.putInt("failed_status", this.wz.getErrorCode());
            bundle.putParcelable("failed_resolution", this.wz.getResolution());
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

    protected abstract void zzaqk();

    protected void zzaqo() {
        this.wA = -1;
        this.wy = false;
        this.wz = null;
        zzaqk();
    }

    public void zzb(ConnectionResult connectionResult, int i) {
        if (!this.wy) {
            this.wy = true;
            this.wA = i;
            this.wz = connectionResult;
            this.wB.post(new zza());
        }
    }
}
