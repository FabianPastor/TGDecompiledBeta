package com.google.android.gms.internal;

import android.graphics.Canvas;
import android.net.Uri;
import android.widget.ImageView;

public final class zzacc extends ImageView {
    private Uri zzaET;
    private int zzaEU;

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
    }

    public void zzcQ(int i) {
        this.zzaEU = i;
    }

    public void zzr(Uri uri) {
        this.zzaET = uri;
    }

    public int zzxu() {
        return this.zzaEU;
    }
}
