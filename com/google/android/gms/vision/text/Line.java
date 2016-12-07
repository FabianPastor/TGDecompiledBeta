package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.gms.vision.text.internal.client.LineBoxParcel;
import com.google.android.gms.vision.text.internal.client.WordBoxParcel;
import java.util.ArrayList;
import java.util.List;

public class Line implements Text {
    private LineBoxParcel aOG;
    private List<Element> aOH;

    Line(LineBoxParcel lineBoxParcel) {
        this.aOG = lineBoxParcel;
    }

    public float getAngle() {
        return this.aOG.aOR.aOP;
    }

    public Rect getBoundingBox() {
        return zza.zza((Text) this);
    }

    public List<? extends Text> getComponents() {
        return zzclt();
    }

    public Point[] getCornerPoints() {
        return zza.zza(this.aOG.aOR);
    }

    public String getLanguage() {
        return this.aOG.aOK;
    }

    public String getValue() {
        return this.aOG.aOU;
    }

    public boolean isVertical() {
        return this.aOG.aOX;
    }

    List<Element> zzclt() {
        if (this.aOG.aOQ.length == 0) {
            return new ArrayList(0);
        }
        if (this.aOH == null) {
            this.aOH = new ArrayList(this.aOG.aOQ.length);
            for (WordBoxParcel element : this.aOG.aOQ) {
                this.aOH.add(new Element(element));
            }
        }
        return this.aOH;
    }
}
