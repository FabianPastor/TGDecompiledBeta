package com.google.android.gms.vision.face;

import android.graphics.PointF;
import java.util.Arrays;
import java.util.List;

public class Face {
    public static final float UNCOMPUTED_PROBABILITY = -1.0f;
    private int mId;
    private PointF zzbNj;
    private float zzbNk;
    private float zzbNl;
    private List<Landmark> zzbNm;
    private float zzbNn;
    private float zzbNo;
    private float zzbNp;
    private float zzbnr;
    private float zzbns;

    public Face(int i, PointF pointF, float f, float f2, float f3, float f4, Landmark[] landmarkArr, float f5, float f6, float f7) {
        this.mId = i;
        this.zzbNj = pointF;
        this.zzbnr = f;
        this.zzbns = f2;
        this.zzbNk = f3;
        this.zzbNl = f4;
        this.zzbNm = Arrays.asList(landmarkArr);
        if (f5 < 0.0f || f5 > 1.0f) {
            this.zzbNn = -1.0f;
        } else {
            this.zzbNn = f5;
        }
        if (f6 < 0.0f || f6 > 1.0f) {
            this.zzbNo = -1.0f;
        } else {
            this.zzbNo = f6;
        }
        if (f7 < 0.0f || f7 > 1.0f) {
            this.zzbNp = -1.0f;
        } else {
            this.zzbNp = f7;
        }
    }

    public float getEulerY() {
        return this.zzbNk;
    }

    public float getEulerZ() {
        return this.zzbNl;
    }

    public float getHeight() {
        return this.zzbns;
    }

    public int getId() {
        return this.mId;
    }

    public float getIsLeftEyeOpenProbability() {
        return this.zzbNn;
    }

    public float getIsRightEyeOpenProbability() {
        return this.zzbNo;
    }

    public float getIsSmilingProbability() {
        return this.zzbNp;
    }

    public List<Landmark> getLandmarks() {
        return this.zzbNm;
    }

    public PointF getPosition() {
        return new PointF(this.zzbNj.x - (this.zzbnr / 2.0f), this.zzbNj.y - (this.zzbns / 2.0f));
    }

    public float getWidth() {
        return this.zzbnr;
    }
}
