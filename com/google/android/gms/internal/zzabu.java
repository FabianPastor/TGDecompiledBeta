package com.google.android.gms.internal;

import android.graphics.Canvas;
import android.net.Uri;
import android.widget.ImageView;

public final class zzabu extends ImageView {
    private Uri zzaDu;
    private int zzaDv;

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
    }

    public void zzcK(int i) {
        this.zzaDv = i;
    }

    public void zzr(Uri uri) {
        this.zzaDu = uri;
    }

    public int zzwO() {
        return this.zzaDv;
    }
}
