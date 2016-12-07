package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.gms.vision.text.internal.client.WordBoxParcel;
import java.util.ArrayList;
import java.util.List;

public class Element implements Text {
    private WordBoxParcel aLu;

    Element(WordBoxParcel wordBoxParcel) {
        this.aLu = wordBoxParcel;
    }

    public Rect getBoundingBox() {
        return zza.zza((Text) this);
    }

    public List<? extends Text> getComponents() {
        return new ArrayList();
    }

    public Point[] getCornerPoints() {
        return zza.zza(this.aLu.aLG);
    }

    public String getLanguage() {
        return this.aLu.aLz;
    }

    public String getValue() {
        return this.aLu.aLJ;
    }
}
