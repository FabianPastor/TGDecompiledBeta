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

public abstract class zzzw extends zzaaw implements OnCancelListener {
    protected boolean mStarted;
    protected final GoogleApiAvailability zzaxX;
    protected boolean zzayG;
    private ConnectionResult zzayH;
    private int zzayI;
    private final Handler zzayJ;

    private class zza implements Runnable {
        final /* synthetic */ zzzw zzayK;

        private zza(zzzw com_google_android_gms_internal_zzzw) {
            this.zzayK = com_google_android_gms_internal_zzzw;
        }

        @MainThread
        public void run() {
            if (!this.zzayK.mStarted) {
                return;
            }
            if (this.zzayK.zzayH.hasResolution()) {
                this.zzayK.zzaBs.startActivityForResult(GoogleApiActivity.zzb(this.zzayK.getActivity(), this.zzayK.zzayH.getResolution(), this.zzayK.zzayI, false), 1);
            } else if (this.zzayK.zzaxX.isUserResolvableError(this.zzayK.zzayH.getErrorCode())) {
                this.zzayK.zzaxX.zza(this.zzayK.getActivity(), this.zzayK.zzaBs, this.zzayK.zzayH.getErrorCode(), 2, this.zzayK);
            } else if (this.zzayK.zzayH.getErrorCode() == 18) {
                final Dialog zza = this.zzayK.zzaxX.zza(this.zzayK.getActivity(), this.zzayK);
                this.zzayK.zzaxX.zza(this.zzayK.getActivity().getApplicationContext(), new com.google.android.gms.internal.zzaar.zza(this) {
                    final /* synthetic */ zza zzayM;

                    public void zzvb() {
                        this.zzayM.zzayK.zzva();
                        if (zza.isShowing()) {
                            zza.dismiss();
                        }
                    }
                });
            } else {
                this.zzayK.zza(this.zzayK.zzayH, this.zzayK.zzayI);
            }
        }
    }

    protected zzzw(zzaax com_google_android_gms_internal_zzaax) {
        this(com_google_android_gms_internal_zzaax, GoogleApiAvailability.getInstance());
    }

    zzzw(zzaax com_google_android_gms_internal_zzaax, GoogleApiAvailability googleApiAvailability) {
        super(com_google_android_gms_internal_zzaax);
        this.zzayI = -1;
        this.zzayJ = new Handler(Looper.getMainLooper());
        this.zzaxX = googleApiAvailability;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onActivityResult(int i, int i2, Intent intent) {
        Object obj = 1;
        switch (i) {
            case 1:
                if (i2 != -1) {
                    if (i2 == 0) {
                        this.zzayH = new ConnectionResult(intent != null ? intent.getIntExtra("<<ResolutionFailureErrorDetail>>", 13) : 13, null);
                    }
                }
                break;
            case 2:
                int isGooglePlayServicesAvailable = this.zzaxX.isGooglePlayServicesAvailable(getActivity());
                if (isGooglePlayServicesAvailable != 0) {
                    obj = null;
                }
                if (this.zzayH.getErrorCode() == 18 && isGooglePlayServicesAvailable == 18) {
                    return;
                }
            default:
                obj = null;
                break;
        }
        if (obj != null) {
            zzva();
        } else {
            zza(this.zzayH, this.zzayI);
        }
    }

    public void onCancel(DialogInterface dialogInterface) {
        zza(new ConnectionResult(13, null), this.zzayI);
        zzva();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.zzayG = bundle.getBoolean("resolving_error", false);
            if (this.zzayG) {
                this.zzayI = bundle.getInt("failed_client_id", -1);
                this.zzayH = new ConnectionResult(bundle.getInt("failed_status"), (PendingIntent) bundle.getParcelable("failed_resolution"));
            }
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("resolving_error", this.zzayG);
        if (this.zzayG) {
            bundle.putInt("failed_client_id", this.zzayI);
            bundle.putInt("failed_status", this.zzayH.getErrorCode());
            bundle.putParcelable("failed_resolution", this.zzayH.getResolution());
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
        if (!this.zzayG) {
            this.zzayG = true;
            this.zzayI = i;
            this.zzayH = connectionResult;
            this.zzayJ.post(new zza());
        }
    }

    protected abstract void zzuW();

    protected void zzva() {
        this.zzayI = -1;
        this.zzayG = false;
        this.zzayH = null;
        zzuW();
    }
}
