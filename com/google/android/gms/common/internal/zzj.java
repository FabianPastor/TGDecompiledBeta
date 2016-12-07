package com.google.android.gms.common.internal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import com.google.android.gms.internal.zzrb;

public abstract class zzj implements OnClickListener {

    class AnonymousClass1 extends zzj {
        final /* synthetic */ Activity val$activity;
        final /* synthetic */ Intent val$intent;
        final /* synthetic */ int val$requestCode;

        AnonymousClass1(Intent intent, Activity activity, int i) {
            this.val$intent = intent;
            this.val$activity = activity;
            this.val$requestCode = i;
        }

        public void zzauo() {
            if (this.val$intent != null) {
                this.val$activity.startActivityForResult(this.val$intent, this.val$requestCode);
            }
        }
    }

    class AnonymousClass2 extends zzj {
        final /* synthetic */ Fragment val$fragment;
        final /* synthetic */ Intent val$intent;
        final /* synthetic */ int val$requestCode;

        AnonymousClass2(Intent intent, Fragment fragment, int i) {
            this.val$intent = intent;
            this.val$fragment = fragment;
            this.val$requestCode = i;
        }

        public void zzauo() {
            if (this.val$intent != null) {
                this.val$fragment.startActivityForResult(this.val$intent, this.val$requestCode);
            }
        }
    }

    class AnonymousClass3 extends zzj {
        final /* synthetic */ zzrb Cd;
        final /* synthetic */ Intent val$intent;
        final /* synthetic */ int val$requestCode;

        AnonymousClass3(Intent intent, zzrb com_google_android_gms_internal_zzrb, int i) {
            this.val$intent = intent;
            this.Cd = com_google_android_gms_internal_zzrb;
            this.val$requestCode = i;
        }

        @TargetApi(11)
        public void zzauo() {
            if (this.val$intent != null) {
                this.Cd.startActivityForResult(this.val$intent, this.val$requestCode);
            }
        }
    }

    public static zzj zza(Activity activity, Intent intent, int i) {
        return new AnonymousClass1(intent, activity, i);
    }

    public static zzj zza(@NonNull Fragment fragment, Intent intent, int i) {
        return new AnonymousClass2(intent, fragment, i);
    }

    public static zzj zza(@NonNull zzrb com_google_android_gms_internal_zzrb, Intent intent, int i) {
        return new AnonymousClass3(intent, com_google_android_gms_internal_zzrb, i);
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        try {
            zzauo();
            dialogInterface.dismiss();
        } catch (Throwable e) {
            Log.e("DialogRedirect", "Can't redirect to app settings for Google Play services", e);
        }
    }

    public abstract void zzauo();
}
