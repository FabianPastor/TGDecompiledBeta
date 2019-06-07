package org.telegram.ui.Components;

import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.text.StaticLayout;
import org.telegram.messenger.AndroidUtilities;

public class LinkPath extends Path {
    private boolean allowReset = true;
    private int baselineShift;
    private StaticLayout currentLayout;
    private int currentLine;
    private float heightOffset;
    private float lastTop = -1.0f;
    private int lineHeight;
    private RectF rect;
    private boolean useRoundRect;

    public LinkPath(boolean z) {
        this.useRoundRect = z;
    }

    public void setCurrentLayout(StaticLayout staticLayout, int i, float f) {
        this.currentLayout = staticLayout;
        this.currentLine = staticLayout.getLineForOffset(i);
        this.lastTop = -1.0f;
        this.heightOffset = f;
        if (VERSION.SDK_INT >= 28) {
            i = staticLayout.getLineCount();
            if (i > 0) {
                i--;
                this.lineHeight = staticLayout.getLineBottom(i) - staticLayout.getLineTop(i);
            }
        }
    }

    public void setAllowReset(boolean z) {
        this.allowReset = z;
    }

    public void setUseRoundRect(boolean z) {
        this.useRoundRect = z;
    }

    public boolean isUsingRoundRect() {
        return this.useRoundRect;
    }

    public void setBaselineShift(int i) {
        this.baselineShift = i;
    }

    public void addRect(float f, float f2, float f3, float f4, Direction direction) {
        float f5 = this.heightOffset;
        f2 += f5;
        f4 += f5;
        f5 = this.lastTop;
        if (f5 == -1.0f) {
            this.lastTop = f2;
        } else if (f5 != f2) {
            this.lastTop = f2;
            this.currentLine++;
        }
        f5 = this.currentLayout.getLineRight(this.currentLine);
        float lineLeft = this.currentLayout.getLineLeft(this.currentLine);
        if (f >= f5) {
            return;
        }
        if (f > lineLeft || f3 > lineLeft) {
            float f6 = f3 > f5 ? f5 : f3;
            float f7 = f < lineLeft ? lineLeft : f;
            f5 = 0.0f;
            if (VERSION.SDK_INT < 28) {
                if (f4 != ((float) this.currentLayout.getHeight())) {
                    f5 = this.currentLayout.getSpacingAdd();
                }
                f4 -= f5;
            } else if (f4 - f2 > ((float) this.lineHeight)) {
                f = this.heightOffset;
                if (f4 != ((float) this.currentLayout.getHeight())) {
                    f5 = ((float) this.currentLayout.getLineBottom(this.currentLine)) - this.currentLayout.getSpacingAdd();
                }
                f4 = f + f5;
            }
            int i = this.baselineShift;
            if (i < 0) {
                f4 += (float) i;
            } else if (i > 0) {
                f2 += (float) i;
            }
            float f8 = f2;
            float f9 = f4;
            if (this.useRoundRect) {
                if (this.rect == null) {
                    this.rect = new RectF();
                }
                this.rect.set(f7 - ((float) AndroidUtilities.dp(4.0f)), f8, f6 + ((float) AndroidUtilities.dp(4.0f)), f9);
                super.addRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), direction);
                return;
            }
            super.addRect(f7, f8, f6, f9, direction);
        }
    }

    public void reset() {
        if (this.allowReset) {
            super.reset();
        }
    }
}
