package com.google.android.gms.common.api.internal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Keep;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class LifecycleCallback {
    protected final zzcf zzfud;

    protected LifecycleCallback(zzcf com_google_android_gms_common_api_internal_zzcf) {
        this.zzfud = com_google_android_gms_common_api_internal_zzcf;
    }

    @Keep
    private static zzcf getChimeraLifecycleFragmentImpl(zzce com_google_android_gms_common_api_internal_zzce) {
        throw new IllegalStateException("Method not available in SDK.");
    }

    protected static zzcf zzb(zzce com_google_android_gms_common_api_internal_zzce) {
        if (com_google_android_gms_common_api_internal_zzce.zzajj()) {
            return zzdb.zza(com_google_android_gms_common_api_internal_zzce.zzajm());
        }
        if (com_google_android_gms_common_api_internal_zzce.zzajk()) {
            return zzcg.zzo(com_google_android_gms_common_api_internal_zzce.zzajl());
        }
        throw new IllegalArgumentException("Can't get fragment for unexpected activity.");
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }

    public final Activity getActivity() {
        return this.zzfud.zzajn();
    }

    public void onActivityResult(int i, int i2, Intent intent) {
    }

    public void onCreate(Bundle bundle) {
    }

    public void onDestroy() {
    }

    public void onResume() {
    }

    public void onSaveInstanceState(Bundle bundle) {
    }

    public void onStart() {
    }

    public void onStop() {
    }
}
