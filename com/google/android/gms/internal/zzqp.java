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

public abstract class zzqp extends zzro implements OnCancelListener {
    protected boolean mStarted;
    protected final GoogleApiAvailability xP;
    private ConnectionResult yA;
    private int yB;
    private final Handler yC;
    protected boolean yz;

    private class zza implements Runnable {
        final /* synthetic */ zzqp yD;

        private zza(zzqp com_google_android_gms_internal_zzqp) {
            this.yD = com_google_android_gms_internal_zzqp;
        }

        @MainThread
        public void run() {
            if (!this.yD.mStarted) {
                return;
            }
            if (this.yD.yA.hasResolution()) {
                this.yD.Bf.startActivityForResult(GoogleApiActivity.zzb(this.yD.getActivity(), this.yD.yA.getResolution(), this.yD.yB, false), 1);
            } else if (this.yD.xP.isUserResolvableError(this.yD.yA.getErrorCode())) {
                this.yD.xP.zza(this.yD.getActivity(), this.yD.Bf, this.yD.yA.getErrorCode(), 2, this.yD);
            } else if (this.yD.yA.getErrorCode() == 18) {
                final Dialog zza = this.yD.xP.zza(this.yD.getActivity(), this.yD);
                this.yD.xP.zza(this.yD.getActivity().getApplicationContext(), new com.google.android.gms.internal.zzrj.zza(this) {
                    final /* synthetic */ zza yF;

                    public void zzarr() {
                        this.yF.yD.zzarq();
                        if (zza.isShowing()) {
                            zza.dismiss();
                        }
                    }
                });
            } else {
                this.yD.zza(this.yD.yA, this.yD.yB);
            }
        }
    }

    protected zzqp(zzrp com_google_android_gms_internal_zzrp) {
        this(com_google_android_gms_internal_zzrp, GoogleApiAvailability.getInstance());
    }

    zzqp(zzrp com_google_android_gms_internal_zzrp, GoogleApiAvailability googleApiAvailability) {
        super(com_google_android_gms_internal_zzrp);
        this.yB = -1;
        this.yC = new Handler(Looper.getMainLooper());
        this.xP = googleApiAvailability;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onActivityResult(int i, int i2, Intent intent) {
        Object obj = 1;
        switch (i) {
            case 1:
                if (i2 != -1) {
                    if (i2 == 0) {
                        this.yA = new ConnectionResult(intent != null ? intent.getIntExtra("<<ResolutionFailureErrorDetail>>", 13) : 13, null);
                    }
                }
                break;
            case 2:
                int isGooglePlayServicesAvailable = this.xP.isGooglePlayServicesAvailable(getActivity());
                if (isGooglePlayServicesAvailable != 0) {
                    obj = null;
                }
                if (this.yA.getErrorCode() == 18 && isGooglePlayServicesAvailable == 18) {
                    return;
                }
            default:
                obj = null;
                break;
        }
        if (obj != null) {
            zzarq();
        } else {
            zza(this.yA, this.yB);
        }
    }

    public void onCancel(DialogInterface dialogInterface) {
        zza(new ConnectionResult(13, null), this.yB);
        zzarq();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.yz = bundle.getBoolean("resolving_error", false);
            if (this.yz) {
                this.yB = bundle.getInt("failed_client_id", -1);
                this.yA = new ConnectionResult(bundle.getInt("failed_status"), (PendingIntent) bundle.getParcelable("failed_resolution"));
            }
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("resolving_error", this.yz);
        if (this.yz) {
            bundle.putInt("failed_client_id", this.yB);
            bundle.putInt("failed_status", this.yA.getErrorCode());
            bundle.putParcelable("failed_resolution", this.yA.getResolution());
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

    protected abstract void zzarm();

    protected void zzarq() {
        this.yB = -1;
        this.yz = false;
        this.yA = null;
        zzarm();
    }

    public void zzb(ConnectionResult connectionResult, int i) {
        if (!this.yz) {
            this.yz = true;
            this.yB = i;
            this.yA = connectionResult;
            this.yC.post(new zza());
        }
    }
}
