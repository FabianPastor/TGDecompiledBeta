package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.gms.internal.zzbhk;
import com.google.android.gms.internal.zzbht;
import java.util.ArrayList;
import java.util.List;

public class Line implements Text {
    private zzbhk zzbNs;
    private List<Element> zzbNt;

    Line(zzbhk com_google_android_gms_internal_zzbhk) {
        this.zzbNs = com_google_android_gms_internal_zzbhk;
    }

    public float getAngle() {
        return this.zzbNs.zzbNC.zzbNA;
    }

    public Rect getBoundingBox() {
        return zza.zza((Text) this);
    }

    public List<? extends Text> getComponents() {
        return zzSr();
    }

    public Point[] getCornerPoints() {
        return zza.zza(this.zzbNs.zzbNC);
    }

    public String getLanguage() {
        return this.zzbNs.zzbNw;
    }

    public String getValue() {
        return this.zzbNs.zzbNF;
    }

    public boolean isVertical() {
        return this.zzbNs.zzbNI;
    }

    List<Element> zzSr() {
        if (this.zzbNs.zzbNB.length == 0) {
            return new ArrayList(0);
        }
        if (this.zzbNt == null) {
            this.zzbNt = new ArrayList(this.zzbNs.zzbNB.length);
            for (zzbht element : this.zzbNs.zzbNB) {
                this.zzbNt.add(new Element(element));
            }
        }
        return this.zzbNt;
    }
}
