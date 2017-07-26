package com.google.android.gms.internal;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;

final class zzbfk extends ConstantState {
    int mChangingConfigurations;
    int zzaGD;

    zzbfk(zzbfk com_google_android_gms_internal_zzbfk) {
        if (com_google_android_gms_internal_zzbfk != null) {
            this.mChangingConfigurations = com_google_android_gms_internal_zzbfk.mChangingConfigurations;
            this.zzaGD = com_google_android_gms_internal_zzbfk.zzaGD;
        }
    }

    public final int getChangingConfigurations() {
        return this.mChangingConfigurations;
    }

    public final Drawable newDrawable() {
        return new zzbfg(this);
    }
}
