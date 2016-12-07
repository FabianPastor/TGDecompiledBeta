package com.google.android.gms.internal;

import android.graphics.Canvas;
import android.net.Uri;
import android.widget.ImageView;

public final class zzsk extends ImageView {
    private Uri Dc;
    private int Dd;

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
    }

    public int zzauy() {
        return this.Dd;
    }

    public void zzgi(int i) {
        this.Dd = i;
    }

    public void zzr(Uri uri) {
        this.Dc = uri;
    }
}
