package com.google.android.gms.vision.face;

import android.graphics.PointF;
import java.util.Arrays;
import java.util.List;
import org.telegram.messenger.volley.DefaultRetryPolicy;

public class Face {
    public static final float UNCOMPUTED_PROBABILITY = -1.0f;
    private PointF aKP;
    private float aKQ;
    private float aKR;
    private List<Landmark> aKS;
    private float aKT;
    private float aKU;
    private float aKV;
    private float amJ;
    private float amK;
    private int mId;

    public Face(int i, PointF pointF, float f, float f2, float f3, float f4, Landmark[] landmarkArr, float f5, float f6, float f7) {
        this.mId = i;
        this.aKP = pointF;
        this.amJ = f;
        this.amK = f2;
        this.aKQ = f3;
        this.aKR = f4;
        this.aKS = Arrays.asList(landmarkArr);
        if (f5 < 0.0f || f5 > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            this.aKT = -1.0f;
        } else {
            this.aKT = f5;
        }
        if (f6 < 0.0f || f6 > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            this.aKU = -1.0f;
        } else {
            this.aKU = f6;
        }
        if (f7 < 0.0f || f7 > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            this.aKV = -1.0f;
        } else {
            this.aKV = f7;
        }
    }

    public float getEulerY() {
        return this.aKQ;
    }

    public float getEulerZ() {
        return this.aKR;
    }

    public float getHeight() {
        return this.amK;
    }

    public int getId() {
        return this.mId;
    }

    public float getIsLeftEyeOpenProbability() {
        return this.aKT;
    }

    public float getIsRightEyeOpenProbability() {
        return this.aKU;
    }

    public float getIsSmilingProbability() {
        return this.aKV;
    }

    public List<Landmark> getLandmarks() {
        return this.aKS;
    }

    public PointF getPosition() {
        return new PointF(this.aKP.x - (this.amJ / 2.0f), this.aKP.y - (this.amK / 2.0f));
    }

    public float getWidth() {
        return this.amJ;
    }
}
