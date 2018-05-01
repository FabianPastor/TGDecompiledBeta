package org.telegram.ui.Components;

import android.graphics.Path;
import android.graphics.Path.Direction;
import android.text.StaticLayout;

public class LinkPath extends Path {
    private StaticLayout currentLayout;
    private int currentLine;
    private float heightOffset;
    private float lastTop = -1.0f;

    public void setCurrentLayout(StaticLayout staticLayout, int i, float f) {
        this.currentLayout = staticLayout;
        this.currentLine = staticLayout.getLineForOffset(i);
        this.lastTop = -1.0f;
        this.heightOffset = f;
    }

    public void addRect(float f, float f2, float f3, float f4, Direction direction) {
        float f5 = f2 + this.heightOffset;
        f4 += this.heightOffset;
        if (this.lastTop == -1.0f) {
            this.lastTop = f5;
        } else if (this.lastTop != f5) {
            this.lastTop = f5;
            this.currentLine++;
        }
        f2 = this.currentLayout.getLineRight(this.currentLine);
        float lineLeft = this.currentLayout.getLineLeft(this.currentLine);
        if (f < f2) {
            super.addRect(f < lineLeft ? lineLeft : f, f5, f3 > f2 ? f2 : f3, f4 - (f4 != ((float) this.currentLayout.getHeight()) ? this.currentLayout.getSpacingAdd() : 0.0f), direction);
        }
    }
}
