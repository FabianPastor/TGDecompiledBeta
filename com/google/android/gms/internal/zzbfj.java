package com.google.android.gms.internal;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;

final class zzbfj extends ConstantState {
    int mChangingConfigurations;
    int zzaGD;

    zzbfj(zzbfj com_google_android_gms_internal_zzbfj) {
        if (com_google_android_gms_internal_zzbfj != null) {
            this.mChangingConfigurations = com_google_android_gms_internal_zzbfj.mChangingConfigurations;
            this.zzaGD = com_google_android_gms_internal_zzbfj.zzaGD;
        }
    }

    public final int getChangingConfigurations() {
        return this.mChangingConfigurations;
    }

    public final Drawable newDrawable() {
        return new zzbff(this);
    }
}
