package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.gms.internal.fj;
import com.google.android.gms.internal.fs;
import java.util.ArrayList;
import java.util.List;

public class Line implements Text {
    private fj zzbNM;
    private List<Element> zzbNN;

    Line(fj fjVar) {
        this.zzbNM = fjVar;
    }

    public float getAngle() {
        return this.zzbNM.zzbNW.zzbNU;
    }

    public Rect getBoundingBox() {
        return zzc.zza((Text) this);
    }

    public List<? extends Text> getComponents() {
        if (this.zzbNM.zzbNV.length == 0) {
            return new ArrayList(0);
        }
        if (this.zzbNN == null) {
            this.zzbNN = new ArrayList(this.zzbNM.zzbNV.length);
            for (fs element : this.zzbNM.zzbNV) {
                this.zzbNN.add(new Element(element));
            }
        }
        return this.zzbNN;
    }

    public Point[] getCornerPoints() {
        return zzc.zza(this.zzbNM.zzbNW);
    }

    public String getLanguage() {
        return this.zzbNM.zzbNQ;
    }

    public String getValue() {
        return this.zzbNM.zzbNZ;
    }

    public boolean isVertical() {
        return this.zzbNM.zzbOc;
    }
}
