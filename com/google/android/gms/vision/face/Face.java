package com.google.android.gms.vision.face;

import android.graphics.PointF;
import java.util.Arrays;
import java.util.List;
import org.telegram.messenger.volley.DefaultRetryPolicy;

public class Face {
    public static final float UNCOMPUTED_PROBABILITY = -1.0f;
    private PointF aOa;
    private float aOb;
    private float aOc;
    private List<Landmark> aOd;
    private float aOe;
    private float aOf;
    private float aOg;
    private float apP;
    private float apQ;
    private int mId;

    public Face(int i, PointF pointF, float f, float f2, float f3, float f4, Landmark[] landmarkArr, float f5, float f6, float f7) {
        this.mId = i;
        this.aOa = pointF;
        this.apP = f;
        this.apQ = f2;
        this.aOb = f3;
        this.aOc = f4;
        this.aOd = Arrays.asList(landmarkArr);
        if (f5 < 0.0f || f5 > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            this.aOe = -1.0f;
        } else {
            this.aOe = f5;
        }
        if (f6 < 0.0f || f6 > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            this.aOf = -1.0f;
        } else {
            this.aOf = f6;
        }
        if (f7 < 0.0f || f7 > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            this.aOg = -1.0f;
        } else {
            this.aOg = f7;
        }
    }

    public float getEulerY() {
        return this.aOb;
    }

    public float getEulerZ() {
        return this.aOc;
    }

    public float getHeight() {
        return this.apQ;
    }

    public int getId() {
        return this.mId;
    }

    public float getIsLeftEyeOpenProbability() {
        return this.aOe;
    }

    public float getIsRightEyeOpenProbability() {
        return this.aOf;
    }

    public float getIsSmilingProbability() {
        return this.aOg;
    }

    public List<Landmark> getLandmarks() {
        return this.aOd;
    }

    public PointF getPosition() {
        return new PointF(this.aOa.x - (this.apP / 2.0f), this.aOa.y - (this.apQ / 2.0f));
    }

    public float getWidth() {
        return this.apP;
    }
}
