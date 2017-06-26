package com.google.android.gms.internal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class zzbdr {
    protected final zzbds zzaEG;

    protected zzbdr(zzbds com_google_android_gms_internal_zzbds) {
        this.zzaEG = com_google_android_gms_internal_zzbds;
    }

    protected static zzbds zzb(zzbdq com_google_android_gms_internal_zzbdq) {
        return com_google_android_gms_internal_zzbdq.zzqC() ? zzben.zza(com_google_android_gms_internal_zzbdq.zzqE()) : zzbdt.zzo(com_google_android_gms_internal_zzbdq.zzqD());
    }

    public static zzbds zzn(Activity activity) {
        return zzb(new zzbdq(activity));
    }

    @MainThread
    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }

    public final Activity getActivity() {
        return this.zzaEG.zzqF();
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
    public void onResume() {
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
