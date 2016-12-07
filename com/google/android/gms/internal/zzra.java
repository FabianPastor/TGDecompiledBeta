package com.google.android.gms.internal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class zzra {
    protected final zzrb yY;

    protected zzra(zzrb com_google_android_gms_internal_zzrb) {
        this.yY = com_google_android_gms_internal_zzrb;
    }

    protected static zzrb zzc(zzqz com_google_android_gms_internal_zzqz) {
        return com_google_android_gms_internal_zzqz.zzasn() ? zzrn.zza(com_google_android_gms_internal_zzqz.zzasp()) : zzrc.zzt(com_google_android_gms_internal_zzqz.zzaso());
    }

    protected static zzrb zzs(Activity activity) {
        return zzc(new zzqz(activity));
    }

    @MainThread
    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }

    public Activity getActivity() {
        return this.yY.zzasq();
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
