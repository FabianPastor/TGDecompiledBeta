package com.google.android.gms.vision.face;

import android.graphics.PointF;
import java.util.Arrays;
import java.util.List;

public class Face {
    public static final float UNCOMPUTED_PROBABILITY = -1.0f;
    private int mId;
    private PointF zzbOL;
    private float zzbOM;
    private float zzbON;
    private List<Landmark> zzbOO;
    private float zzbOP;
    private float zzbOQ;
    private float zzbOR;
    private float zzbpt;
    private float zzbpu;

    public Face(int i, PointF pointF, float f, float f2, float f3, float f4, Landmark[] landmarkArr, float f5, float f6, float f7) {
        this.mId = i;
        this.zzbOL = pointF;
        this.zzbpt = f;
        this.zzbpu = f2;
        this.zzbOM = f3;
        this.zzbON = f4;
        this.zzbOO = Arrays.asList(landmarkArr);
        if (f5 < 0.0f || f5 > 1.0f) {
            this.zzbOP = -1.0f;
        } else {
            this.zzbOP = f5;
        }
        if (f6 < 0.0f || f6 > 1.0f) {
            this.zzbOQ = -1.0f;
        } else {
            this.zzbOQ = f6;
        }
        if (f7 < 0.0f || f7 > 1.0f) {
            this.zzbOR = -1.0f;
        } else {
            this.zzbOR = f7;
        }
    }

    public float getEulerY() {
        return this.zzbOM;
    }

    public float getEulerZ() {
        return this.zzbON;
    }

    public float getHeight() {
        return this.zzbpu;
    }

    public int getId() {
        return this.mId;
    }

    public float getIsLeftEyeOpenProbability() {
        return this.zzbOP;
    }

    public float getIsRightEyeOpenProbability() {
        return this.zzbOQ;
    }

    public float getIsSmilingProbability() {
        return this.zzbOR;
    }

    public List<Landmark> getLandmarks() {
        return this.zzbOO;
    }

    public PointF getPosition() {
        return new PointF(this.zzbOL.x - (this.zzbpt / 2.0f), this.zzbOL.y - (this.zzbpu / 2.0f));
    }

    public float getWidth() {
        return this.zzbpt;
    }
}
