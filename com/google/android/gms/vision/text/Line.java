package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.gms.internal.fk;
import com.google.android.gms.internal.ft;
import java.util.ArrayList;
import java.util.List;

public class Line implements Text {
    private fk zzbNO;
    private List<Element> zzbNP;

    Line(fk fkVar) {
        this.zzbNO = fkVar;
    }

    public float getAngle() {
        return this.zzbNO.zzbNY.zzbNW;
    }

    public Rect getBoundingBox() {
        return zzc.zza((Text) this);
    }

    public List<? extends Text> getComponents() {
        if (this.zzbNO.zzbNX.length == 0) {
            return new ArrayList(0);
        }
        if (this.zzbNP == null) {
            this.zzbNP = new ArrayList(this.zzbNO.zzbNX.length);
            for (ft element : this.zzbNO.zzbNX) {
                this.zzbNP.add(new Element(element));
            }
        }
        return this.zzbNP;
    }

    public Point[] getCornerPoints() {
        return zzc.zza(this.zzbNO.zzbNY);
    }

    public String getLanguage() {
        return this.zzbNO.zzbNS;
    }

    public String getValue() {
        return this.zzbNO.zzbOb;
    }

    public boolean isVertical() {
        return this.zzbNO.zzbOe;
    }
}
