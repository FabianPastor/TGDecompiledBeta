package com.google.android.gms.dynamic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

final class zze implements zzi {
    private /* synthetic */ ViewGroup val$container;
    private /* synthetic */ Bundle zzail;
    private /* synthetic */ zza zzgwh;
    private /* synthetic */ FrameLayout zzgwj;
    private /* synthetic */ LayoutInflater zzgwk;

    zze(zza com_google_android_gms_dynamic_zza, FrameLayout frameLayout, LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.zzgwh = com_google_android_gms_dynamic_zza;
        this.zzgwj = frameLayout;
        this.zzgwk = layoutInflater;
        this.val$container = viewGroup;
        this.zzail = bundle;
    }

    public final int getState() {
        return 2;
    }

    public final void zzb(LifecycleDelegate lifecycleDelegate) {
        this.zzgwj.removeAllViews();
        this.zzgwj.addView(this.zzgwh.zzgwd.onCreateView(this.zzgwk, this.val$container, this.zzail));
    }
}
