package org.telegram.ui.Components;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class AnchorSpan extends MetricAffectingSpan {
    private String name;

    public AnchorSpan(String n) {
        this.name = n.toLowerCase();
    }

    public String getName() {
        return this.name;
    }

    public void updateMeasureState(TextPaint p) {
    }

    public void updateDrawState(TextPaint tp) {
    }
}
