package org.telegram.ui.Components;

import android.view.ViewGroup;

public class LerpedLayoutParams extends ViewGroup.MarginLayoutParams {
    private ViewGroup.LayoutParams from;
    private ViewGroup.LayoutParams to;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public LerpedLayoutParams(ViewGroup.LayoutParams from2, ViewGroup.LayoutParams to2) {
        super(from2 == null ? to2 : from2);
        this.from = from2;
        this.to = to2;
    }

    public void apply(float t) {
        float t2 = Math.min(Math.max(t, 0.0f), 1.0f);
        this.width = lerpSz(this.from.width, this.to.width, t2);
        this.height = lerpSz(this.from.height, this.to.height, t2);
        ViewGroup.LayoutParams layoutParams = this.from;
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.LayoutParams layoutParams2 = this.to;
            if (layoutParams2 instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginFrom = (ViewGroup.MarginLayoutParams) layoutParams;
                ViewGroup.MarginLayoutParams marginTo = (ViewGroup.MarginLayoutParams) layoutParams2;
                this.topMargin = lerp(marginFrom.topMargin, marginTo.topMargin, t2);
                this.leftMargin = lerp(marginFrom.leftMargin, marginTo.leftMargin, t2);
                this.rightMargin = lerp(marginFrom.rightMargin, marginTo.rightMargin, t2);
                this.bottomMargin = lerp(marginFrom.bottomMargin, marginTo.bottomMargin, t2);
                return;
            }
        }
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginFrom2 = (ViewGroup.MarginLayoutParams) layoutParams;
            this.topMargin = marginFrom2.topMargin;
            this.leftMargin = marginFrom2.leftMargin;
            this.rightMargin = marginFrom2.rightMargin;
            this.bottomMargin = marginFrom2.bottomMargin;
            return;
        }
        ViewGroup.LayoutParams layoutParams3 = this.to;
        if (layoutParams3 instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginTo2 = (ViewGroup.MarginLayoutParams) layoutParams3;
            this.topMargin = marginTo2.topMargin;
            this.leftMargin = marginTo2.leftMargin;
            this.rightMargin = marginTo2.rightMargin;
            this.bottomMargin = marginTo2.bottomMargin;
        }
    }

    private int lerp(int from2, int to2, float t) {
        return (int) (((float) from2) + (((float) (to2 - from2)) * t));
    }

    private int lerpSz(int from2, int to2, float t) {
        if (from2 < 0 || to2 < 0) {
            return t < 0.5f ? from2 : to2;
        }
        return lerp(from2, to2, t);
    }
}
