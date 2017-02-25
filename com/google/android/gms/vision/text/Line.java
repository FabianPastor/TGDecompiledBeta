package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.gms.internal.zzbkh;
import com.google.android.gms.internal.zzbkq;
import java.util.ArrayList;
import java.util.List;

public class Line implements Text {
    private zzbkh zzbPr;
    private List<Element> zzbPs;

    Line(zzbkh com_google_android_gms_internal_zzbkh) {
        this.zzbPr = com_google_android_gms_internal_zzbkh;
    }

    public float getAngle() {
        return this.zzbPr.zzbPB.zzbPz;
    }

    public Rect getBoundingBox() {
        return zza.zza((Text) this);
    }

    public List<? extends Text> getComponents() {
        return zzTS();
    }

    public Point[] getCornerPoints() {
        return zza.zza(this.zzbPr.zzbPB);
    }

    public String getLanguage() {
        return this.zzbPr.zzbPv;
    }

    public String getValue() {
        return this.zzbPr.zzbPE;
    }

    public boolean isVertical() {
        return this.zzbPr.zzbPH;
    }

    List<Element> zzTS() {
        if (this.zzbPr.zzbPA.length == 0) {
            return new ArrayList(0);
        }
        if (this.zzbPs == null) {
            this.zzbPs = new ArrayList(this.zzbPr.zzbPA.length);
            for (zzbkq element : this.zzbPr.zzbPA) {
                this.zzbPs.add(new Element(element));
            }
        }
        return this.zzbPs;
    }
}
