package com.google.android.gms.internal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class zzabe {
    protected final zzabf zzaCR;

    protected zzabe(zzabf com_google_android_gms_internal_zzabf) {
        this.zzaCR = com_google_android_gms_internal_zzabf;
    }

    protected static zzabf zzc(zzabd com_google_android_gms_internal_zzabd) {
        return com_google_android_gms_internal_zzabd.zzwS() ? zzabu.zza(com_google_android_gms_internal_zzabd.zzwU()) : zzabg.zzt(com_google_android_gms_internal_zzabd.zzwT());
    }

    public static zzabf zzs(Activity activity) {
        return zzc(new zzabd(activity));
    }

    @MainThread
    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }

    public Activity getActivity() {
        return this.zzaCR.zzwV();
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
