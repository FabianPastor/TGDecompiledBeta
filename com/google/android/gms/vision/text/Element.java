package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.gms.vision.text.internal.client.WordBoxParcel;
import java.util.ArrayList;
import java.util.List;

public class Element implements Text {
    private WordBoxParcel aOF;

    Element(WordBoxParcel wordBoxParcel) {
        this.aOF = wordBoxParcel;
    }

    public Rect getBoundingBox() {
        return zza.zza((Text) this);
    }

    public List<? extends Text> getComponents() {
        return new ArrayList();
    }

    public Point[] getCornerPoints() {
        return zza.zza(this.aOF.aOR);
    }

    public String getLanguage() {
        return this.aOF.aOK;
    }

    public String getValue() {
        return this.aOF.aOU;
    }
}
