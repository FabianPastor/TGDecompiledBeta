package com.google.android.gms.vision.face;

import android.graphics.PointF;
import java.util.Arrays;
import java.util.List;

public class Face {
    public static final float UNCOMPUTED_PROBABILITY = -1.0f;
    private int mId;
    private PointF zzbOK;
    private float zzbOL;
    private float zzbOM;
    private List<Landmark> zzbON;
    private float zzbOO;
    private float zzbOP;
    private float zzbOQ;
    private float zzbpo;
    private float zzbpp;

    public Face(int i, PointF pointF, float f, float f2, float f3, float f4, Landmark[] landmarkArr, float f5, float f6, float f7) {
        this.mId = i;
        this.zzbOK = pointF;
        this.zzbpo = f;
        this.zzbpp = f2;
        this.zzbOL = f3;
        this.zzbOM = f4;
        this.zzbON = Arrays.asList(landmarkArr);
        if (f5 < 0.0f || f5 > 1.0f) {
            this.zzbOO = -1.0f;
        } else {
            this.zzbOO = f5;
        }
        if (f6 < 0.0f || f6 > 1.0f) {
            this.zzbOP = -1.0f;
        } else {
            this.zzbOP = f6;
        }
        if (f7 < 0.0f || f7 > 1.0f) {
            this.zzbOQ = -1.0f;
        } else {
            this.zzbOQ = f7;
        }
    }

    public float getEulerY() {
        return this.zzbOL;
    }

    public float getEulerZ() {
        return this.zzbOM;
    }

    public float getHeight() {
        return this.zzbpp;
    }

    public int getId() {
        return this.mId;
    }

    public float getIsLeftEyeOpenProbability() {
        return this.zzbOO;
    }

    public float getIsRightEyeOpenProbability() {
        return this.zzbOP;
    }

    public float getIsSmilingProbability() {
        return this.zzbOQ;
    }

    public List<Landmark> getLandmarks() {
        return this.zzbON;
    }

    public PointF getPosition() {
        return new PointF(this.zzbOK.x - (this.zzbpo / 2.0f), this.zzbOK.y - (this.zzbpp / 2.0f));
    }

    public float getWidth() {
        return this.zzbpo;
    }
}
