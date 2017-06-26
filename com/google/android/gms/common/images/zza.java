package com.google.android.gms.common.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import com.google.android.gms.common.internal.zzc;
import com.google.android.gms.internal.zzbfl;

public abstract class zza {
    final zzb zzaGf;
    private int zzaGg = 0;
    protected int zzaGh = 0;
    private boolean zzaGi = false;
    private boolean zzaGj = true;
    private boolean zzaGk = false;
    private boolean zzaGl = true;

    public zza(Uri uri, int i) {
        this.zzaGf = new zzb(uri);
        this.zzaGh = i;
    }

    final void zza(Context context, Bitmap bitmap, boolean z) {
        zzc.zzr(bitmap);
        zza(new BitmapDrawable(context.getResources(), bitmap), z, false, true);
    }

    final void zza(Context context, zzbfl com_google_android_gms_internal_zzbfl) {
        if (this.zzaGl) {
            zza(null, false, true, false);
        }
    }

    final void zza(Context context, zzbfl com_google_android_gms_internal_zzbfl, boolean z) {
        Drawable drawable = null;
        if (this.zzaGh != 0) {
            drawable = context.getResources().getDrawable(this.zzaGh);
        }
        zza(drawable, z, false, false);
    }

    protected abstract void zza(Drawable drawable, boolean z, boolean z2, boolean z3);

    protected final boolean zzc(boolean z, boolean z2) {
        return (!this.zzaGj || z2 || z) ? false : true;
    }
}
