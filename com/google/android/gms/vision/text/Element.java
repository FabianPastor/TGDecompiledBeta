package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.gms.internal.zzbht;
import java.util.ArrayList;
import java.util.List;

public class Element implements Text {
    private zzbht zzbNr;

    Element(zzbht com_google_android_gms_internal_zzbht) {
        this.zzbNr = com_google_android_gms_internal_zzbht;
    }

    public Rect getBoundingBox() {
        return zza.zza((Text) this);
    }

    public List<? extends Text> getComponents() {
        return new ArrayList();
    }

    public Point[] getCornerPoints() {
        return zza.zza(this.zzbNr.zzbNC);
    }

    public String getLanguage() {
        return this.zzbNr.zzbNw;
    }

    public String getValue() {
        return this.zzbNr.zzbNF;
    }
}
