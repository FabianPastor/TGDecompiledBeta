package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.gms.internal.ft;
import java.util.ArrayList;
import java.util.List;

public class Element implements Text {
    private ft zzbNN;

    Element(ft ftVar) {
        this.zzbNN = ftVar;
    }

    public Rect getBoundingBox() {
        return zzc.zza((Text) this);
    }

    public List<? extends Text> getComponents() {
        return new ArrayList();
    }

    public Point[] getCornerPoints() {
        return zzc.zza(this.zzbNN.zzbNY);
    }

    public String getLanguage() {
        return this.zzbNN.zzbNS;
    }

    public String getValue() {
        return this.zzbNN.zzbOb;
    }
}
