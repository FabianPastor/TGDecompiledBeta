package com.google.android.gms.internal;

import android.graphics.Canvas;
import android.net.Uri;
import android.widget.ImageView;

public final class zzru extends ImageView {
    private Uri AT;
    private int AU;

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
    }

    public int zzatp() {
        return this.AU;
    }

    public void zzgj(int i) {
        this.AU = i;
    }

    public void zzq(Uri uri) {
        this.AT = uri;
    }
}
