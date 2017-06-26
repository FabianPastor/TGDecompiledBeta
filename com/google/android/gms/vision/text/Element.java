package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.gms.internal.fs;
import java.util.ArrayList;
import java.util.List;

public class Element implements Text {
    private fs zzbNL;

    Element(fs fsVar) {
        this.zzbNL = fsVar;
    }

    public Rect getBoundingBox() {
        return zzc.zza((Text) this);
    }

    public List<? extends Text> getComponents() {
        return new ArrayList();
    }

    public Point[] getCornerPoints() {
        return zzc.zza(this.zzbNL.zzbNW);
    }

    public String getLanguage() {
        return this.zzbNL.zzbNQ;
    }

    public String getValue() {
        return this.zzbNL.zzbNZ;
    }
}
