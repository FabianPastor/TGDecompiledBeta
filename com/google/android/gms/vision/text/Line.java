package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.gms.vision.text.internal.client.LineBoxParcel;
import com.google.android.gms.vision.text.internal.client.WordBoxParcel;
import java.util.ArrayList;
import java.util.List;

public class Line implements Text {
    private LineBoxParcel aLv;
    private List<Element> aLw;

    Line(LineBoxParcel lineBoxParcel) {
        this.aLv = lineBoxParcel;
    }

    public float getAngle() {
        return this.aLv.aLG.aLE;
    }

    public Rect getBoundingBox() {
        return zza.zza((Text) this);
    }

    public List<? extends Text> getComponents() {
        return zzclu();
    }

    public Point[] getCornerPoints() {
        return zza.zza(this.aLv.aLG);
    }

    public String getLanguage() {
        return this.aLv.aLz;
    }

    public String getValue() {
        return this.aLv.aLJ;
    }

    public boolean isVertical() {
        return this.aLv.aLM;
    }

    List<Element> zzclu() {
        if (this.aLv.aLF.length == 0) {
            return new ArrayList(0);
        }
        if (this.aLw == null) {
            this.aLw = new ArrayList(this.aLv.aLF.length);
            for (WordBoxParcel element : this.aLv.aLF) {
                this.aLw.add(new Element(element));
            }
        }
        return this.aLw;
    }
}
