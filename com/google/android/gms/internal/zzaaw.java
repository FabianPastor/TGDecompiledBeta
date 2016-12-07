package com.google.android.gms.internal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class zzaaw {
    protected final zzaax zzaBs;

    protected zzaaw(zzaax com_google_android_gms_internal_zzaax) {
        this.zzaBs = com_google_android_gms_internal_zzaax;
    }

    protected static zzaax zzc(zzaav com_google_android_gms_internal_zzaav) {
        return com_google_android_gms_internal_zzaav.zzwl() ? zzabm.zza(com_google_android_gms_internal_zzaav.zzwn()) : zzaay.zzt(com_google_android_gms_internal_zzaav.zzwm());
    }

    public static zzaax zzs(Activity activity) {
        return zzc(new zzaav(activity));
    }

    @MainThread
    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }

    public Activity getActivity() {
        return this.zzaBs.zzwo();
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
