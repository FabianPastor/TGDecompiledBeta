package com.google.android.gms.vision.face;

import android.graphics.PointF;
import java.util.Arrays;
import java.util.List;

public class Face {
    public static final float UNCOMPUTED_PROBABILITY = -1.0f;
    private int mId;
    private PointF zzbNh;
    private float zzbNi;
    private float zzbNj;
    private List<Landmark> zzbNk;
    private float zzbNl;
    private float zzbNm;
    private float zzbNn;
    private float zzbnr;
    private float zzbns;

    public Face(int i, PointF pointF, float f, float f2, float f3, float f4, Landmark[] landmarkArr, float f5, float f6, float f7) {
        this.mId = i;
        this.zzbNh = pointF;
        this.zzbnr = f;
        this.zzbns = f2;
        this.zzbNi = f3;
        this.zzbNj = f4;
        this.zzbNk = Arrays.asList(landmarkArr);
        if (f5 < 0.0f || f5 > 1.0f) {
            this.zzbNl = -1.0f;
        } else {
            this.zzbNl = f5;
        }
        if (f6 < 0.0f || f6 > 1.0f) {
            this.zzbNm = -1.0f;
        } else {
            this.zzbNm = f6;
        }
        if (f7 < 0.0f || f7 > 1.0f) {
            this.zzbNn = -1.0f;
        } else {
            this.zzbNn = f7;
        }
    }

    public float getEulerY() {
        return this.zzbNi;
    }

    public float getEulerZ() {
        return this.zzbNj;
    }

    public float getHeight() {
        return this.zzbns;
    }

    public int getId() {
        return this.mId;
    }

    public float getIsLeftEyeOpenProbability() {
        return this.zzbNl;
    }

    public float getIsRightEyeOpenProbability() {
        return this.zzbNm;
    }

    public float getIsSmilingProbability() {
        return this.zzbNn;
    }

    public List<Landmark> getLandmarks() {
        return this.zzbNk;
    }

    public PointF getPosition() {
        return new PointF(this.zzbNh.x - (this.zzbnr / 2.0f), this.zzbNh.y - (this.zzbns / 2.0f));
    }

    public float getWidth() {
        return this.zzbnr;
    }
}
