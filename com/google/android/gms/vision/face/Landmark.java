package com.google.android.gms.vision.face;

import android.graphics.PointF;

public final class Landmark {
    private final int zzeie;
    private final PointF zzkwr;

    public Landmark(PointF pointF, int i) {
        this.zzkwr = pointF;
        this.zzeie = i;
    }

    public final PointF getPosition() {
        return this.zzkwr;
    }

    public final int getType() {
        return this.zzeie;
    }
}
