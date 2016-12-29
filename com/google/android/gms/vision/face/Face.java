package com.google.android.gms.vision.face;

import android.graphics.PointF;
import java.util.Arrays;
import java.util.List;

public class Face {
    public static final float UNCOMPUTED_PROBABILITY = -1.0f;
    private int mId;
    private PointF zzbMM;
    private float zzbMN;
    private float zzbMO;
    private List<Landmark> zzbMP;
    private float zzbMQ;
    private float zzbMR;
    private float zzbMS;
    private float zzboP;
    private float zzboQ;

    public Face(int i, PointF pointF, float f, float f2, float f3, float f4, Landmark[] landmarkArr, float f5, float f6, float f7) {
        this.mId = i;
        this.zzbMM = pointF;
        this.zzboP = f;
        this.zzboQ = f2;
        this.zzbMN = f3;
        this.zzbMO = f4;
        this.zzbMP = Arrays.asList(landmarkArr);
        if (f5 < 0.0f || f5 > 1.0f) {
            this.zzbMQ = -1.0f;
        } else {
            this.zzbMQ = f5;
        }
        if (f6 < 0.0f || f6 > 1.0f) {
            this.zzbMR = -1.0f;
        } else {
            this.zzbMR = f6;
        }
        if (f7 < 0.0f || f7 > 1.0f) {
            this.zzbMS = -1.0f;
        } else {
            this.zzbMS = f7;
        }
    }

    public float getEulerY() {
        return this.zzbMN;
    }

    public float getEulerZ() {
        return this.zzbMO;
    }

    public float getHeight() {
        return this.zzboQ;
    }

    public int getId() {
        return this.mId;
    }

    public float getIsLeftEyeOpenProbability() {
        return this.zzbMQ;
    }

    public float getIsRightEyeOpenProbability() {
        return this.zzbMR;
    }

    public float getIsSmilingProbability() {
        return this.zzbMS;
    }

    public List<Landmark> getLandmarks() {
        return this.zzbMP;
    }

    public PointF getPosition() {
        return new PointF(this.zzbMM.x - (this.zzboP / 2.0f), this.zzbMM.y - (this.zzboQ / 2.0f));
    }

    public float getWidth() {
        return this.zzboP;
    }
}
