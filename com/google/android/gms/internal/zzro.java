package com.google.android.gms.internal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class zzro {
    protected final zzrp Bf;

    protected zzro(zzrp com_google_android_gms_internal_zzrp) {
        this.Bf = com_google_android_gms_internal_zzrp;
    }

    protected static zzrp zzc(zzrn com_google_android_gms_internal_zzrn) {
        return com_google_android_gms_internal_zzrn.zzatv() ? zzsd.zza(com_google_android_gms_internal_zzrn.zzatx()) : zzrq.zzt(com_google_android_gms_internal_zzrn.zzatw());
    }

    public static zzrp zzs(Activity activity) {
        return zzc(new zzrn(activity));
    }

    @MainThread
    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }

    public Activity getActivity() {
        return this.Bf.zzaty();
    }

    @MainThread
    public void onActivityResult(int i, int i2, Intent intent) {
    }

    @MainThread
    public void onCreate(Bundle bundle) {
    }

    @MainThread
    public void onDestroy() {
    }

    @MainThread
    public void onSaveInstanceState(Bundle bundle) {
    }

    @MainThread
    public void onStart() {
    }

    @MainThread
    public void onStop() {
    }
}
