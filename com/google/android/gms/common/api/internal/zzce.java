package com.google.android.gms.common.api.internal;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;

public final class zzce {
    private final Object zzfuc;

    public final boolean zzajj() {
        return this.zzfuc instanceof FragmentActivity;
    }

    public final boolean zzajk() {
        return this.zzfuc instanceof Activity;
    }

    public final Activity zzajl() {
        return (Activity) this.zzfuc;
    }

    public final FragmentActivity zzajm() {
        return (FragmentActivity) this.zzfuc;
    }
}
