package com.google.android.gms.vision.face;

import android.graphics.PointF;
import java.util.Arrays;
import java.util.List;

public class Face {
    public static final float UNCOMPUTED_PROBABILITY = -1.0f;
    private int mId;
    private PointF zzbOH;
    private float zzbOI;
    private float zzbOJ;
    private List<Landmark> zzbOK;
    private float zzbOL;
    private float zzbOM;
    private float zzbON;
    private float zzbpp;
    private float zzbpq;

    public Face(int i, PointF pointF, float f, float f2, float f3, float f4, Landmark[] landmarkArr, float f5, float f6, float f7) {
        this.mId = i;
        this.zzbOH = pointF;
        this.zzbpp = f;
        this.zzbpq = f2;
        this.zzbOI = f3;
        this.zzbOJ = f4;
        this.zzbOK = Arrays.asList(landmarkArr);
        if (f5 < 0.0f || f5 > 1.0f) {
            this.zzbOL = -1.0f;
        } else {
            this.zzbOL = f5;
        }
        if (f6 < 0.0f || f6 > 1.0f) {
            this.zzbOM = -1.0f;
        } else {
            this.zzbOM = f6;
        }
        if (f7 < 0.0f || f7 > 1.0f) {
            this.zzbON = -1.0f;
        } else {
            this.zzbON = f7;
        }
    }

    public float getEulerY() {
        return this.zzbOI;
    }

    public float getEulerZ() {
        return this.zzbOJ;
    }

    public float getHeight() {
        return this.zzbpq;
    }

    public int getId() {
        return this.mId;
    }

    public float getIsLeftEyeOpenProbability() {
        return this.zzbOL;
    }

    public float getIsRightEyeOpenProbability() {
        return this.zzbOM;
    }

    public float getIsSmilingProbability() {
        return this.zzbON;
    }

    public List<Landmark> getLandmarks() {
        return this.zzbOK;
    }

    public PointF getPosition() {
        return new PointF(this.zzbOH.x - (this.zzbpp / 2.0f), this.zzbOH.y - (this.zzbpq / 2.0f));
    }

    public float getWidth() {
        return this.zzbpp;
    }
}
