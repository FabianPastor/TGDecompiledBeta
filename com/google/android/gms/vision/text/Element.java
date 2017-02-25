package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.gms.internal.zzbkq;
import java.util.ArrayList;
import java.util.List;

public class Element implements Text {
    private zzbkq zzbPq;

    Element(zzbkq com_google_android_gms_internal_zzbkq) {
        this.zzbPq = com_google_android_gms_internal_zzbkq;
    }

    public Rect getBoundingBox() {
        return zza.zza((Text) this);
    }

    public List<? extends Text> getComponents() {
        return new ArrayList();
    }

    public Point[] getCornerPoints() {
        return zza.zza(this.zzbPq.zzbPB);
    }

    public String getLanguage() {
        return this.zzbPq.zzbPv;
    }

    public String getValue() {
        return this.zzbPq.zzbPE;
    }
}
