package com.google.android.gms.common.internal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import com.google.android.gms.internal.zzrp;

public abstract class zzh implements OnClickListener {

    class AnonymousClass1 extends zzh {
        final /* synthetic */ Activity val$activity;
        final /* synthetic */ Intent val$intent;
        final /* synthetic */ int val$requestCode;

        AnonymousClass1(Intent intent, Activity activity, int i) {
            this.val$intent = intent;
            this.val$activity = activity;
            this.val$requestCode = i;
        }

        public void zzavx() {
            if (this.val$intent != null) {
                this.val$activity.startActivityForResult(this.val$intent, this.val$requestCode);
            }
        }
    }

    class AnonymousClass2 extends zzh {
        final /* synthetic */ Fragment val$fragment;
        final /* synthetic */ Intent val$intent;
        final /* synthetic */ int val$requestCode;

        AnonymousClass2(Intent intent, Fragment fragment, int i) {
            this.val$intent = intent;
            this.val$fragment = fragment;
            this.val$requestCode = i;
        }

        public void zzavx() {
            if (this.val$intent != null) {
                this.val$fragment.startActivityForResult(this.val$intent, this.val$requestCode);
            }
        }
    }

    class AnonymousClass3 extends zzh {
        final /* synthetic */ zzrp DP;
        final /* synthetic */ Intent val$intent;
        final /* synthetic */ int val$requestCode;

        AnonymousClass3(Intent intent, zzrp com_google_android_gms_internal_zzrp, int i) {
            this.val$intent = intent;
            this.DP = com_google_android_gms_internal_zzrp;
            this.val$requestCode = i;
        }

        @TargetApi(11)
        public void zzavx() {
            if (this.val$intent != null) {
                this.DP.startActivityForResult(this.val$intent, this.val$requestCode);
            }
        }
    }

    public static zzh zza(Activity activity, Intent intent, int i) {
        return new AnonymousClass1(intent, activity, i);
    }

    public static zzh zza(@NonNull Fragment fragment, Intent intent, int i) {
        return new AnonymousClass2(intent, fragment, i);
    }

    public static zzh zza(@NonNull zzrp com_google_android_gms_internal_zzrp, Intent intent, int i) {
        return new AnonymousClass3(intent, com_google_android_gms_internal_zzrp, i);
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        try {
            zzavx();
        } catch (Throwable e) {
            Log.e("DialogRedirect", "Failed to start resolution intent", e);
        } finally {
            dialogInterface.dismiss();
        }
    }

    protected abstract void zzavx();
}
