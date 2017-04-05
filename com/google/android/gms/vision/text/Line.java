package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.gms.internal.zzbkh;
import com.google.android.gms.internal.zzbkq;
import java.util.ArrayList;
import java.util.List;

public class Line implements Text {
    private zzbkh zzbPn;
    private List<Element> zzbPo;

    Line(zzbkh com_google_android_gms_internal_zzbkh) {
        this.zzbPn = com_google_android_gms_internal_zzbkh;
    }

    public float getAngle() {
        return this.zzbPn.zzbPx.zzbPv;
    }

    public Rect getBoundingBox() {
        return zza.zza((Text) this);
    }

    public List<? extends Text> getComponents() {
        return zzTT();
    }

    public Point[] getCornerPoints() {
        return zza.zza(this.zzbPn.zzbPx);
    }

    public String getLanguage() {
        return this.zzbPn.zzbPr;
    }

    public String getValue() {
        return this.zzbPn.zzbPA;
    }

    public boolean isVertical() {
        return this.zzbPn.zzbPD;
    }

    List<Element> zzTT() {
        if (this.zzbPn.zzbPw.length == 0) {
            return new ArrayList(0);
        }
        if (this.zzbPo == null) {
            this.zzbPo = new ArrayList(this.zzbPn.zzbPw.length);
            for (zzbkq element : this.zzbPn.zzbPw) {
                this.zzbPo.add(new Element(element));
            }
        }
        return this.zzbPo;
    }
}
