package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.gms.internal.zzbkh;
import com.google.android.gms.internal.zzbkq;
import java.util.ArrayList;
import java.util.List;

public class Line implements Text {
    private zzbkh zzbPq;
    private List<Element> zzbPr;

    Line(zzbkh com_google_android_gms_internal_zzbkh) {
        this.zzbPq = com_google_android_gms_internal_zzbkh;
    }

    public float getAngle() {
        return this.zzbPq.zzbPA.zzbPy;
    }

    public Rect getBoundingBox() {
        return zza.zza((Text) this);
    }

    public List<? extends Text> getComponents() {
        return zzTV();
    }

    public Point[] getCornerPoints() {
        return zza.zza(this.zzbPq.zzbPA);
    }

    public String getLanguage() {
        return this.zzbPq.zzbPu;
    }

    public String getValue() {
        return this.zzbPq.zzbPD;
    }

    public boolean isVertical() {
        return this.zzbPq.zzbPG;
    }

    List<Element> zzTV() {
        if (this.zzbPq.zzbPz.length == 0) {
            return new ArrayList(0);
        }
        if (this.zzbPr == null) {
            this.zzbPr = new ArrayList(this.zzbPq.zzbPz.length);
            for (zzbkq element : this.zzbPq.zzbPz) {
                this.zzbPr.add(new Element(element));
            }
        }
        return this.zzbPr;
    }
}
