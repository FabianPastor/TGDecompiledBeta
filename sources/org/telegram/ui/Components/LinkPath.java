package org.telegram.ui.Components;

import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
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

    public LinkPath() {
    }

    public LinkPath(boolean z) {
        this.useRoundRect = z;
    }

    public void setCurrentLayout(StaticLayout staticLayout, int i, float f) {
        int lineCount;
        this.currentLayout = staticLayout;
        this.currentLine = staticLayout.getLineForOffset(i);
        this.lastTop = -1.0f;
        this.heightOffset = f;
        if (Build.VERSION.SDK_INT >= 28 && (lineCount = staticLayout.getLineCount()) > 0) {
            int i2 = lineCount - 1;
            this.lineHeight = staticLayout.getLineBottom(i2) - staticLayout.getLineTop(i2);
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

    public void addRect(float f, float f2, float f3, float f4, Path.Direction direction) {
        float f5 = this.heightOffset;
        float f6 = f2 + f5;
        float f7 = f4 + f5;
        float f8 = this.lastTop;
        if (f8 == -1.0f) {
            this.lastTop = f6;
        } else if (f8 != f6) {
            this.lastTop = f6;
            this.currentLine++;
        }
        float lineRight = this.currentLayout.getLineRight(this.currentLine);
        float lineLeft = this.currentLayout.getLineLeft(this.currentLine);
        if (f >= lineRight) {
            return;
        }
        if (f > lineLeft || f3 > lineLeft) {
            float f9 = f3 > lineRight ? lineRight : f3;
            float var_ = f < lineLeft ? lineLeft : f;
            float var_ = 0.0f;
            if (Build.VERSION.SDK_INT < 28) {
                if (f7 != ((float) this.currentLayout.getHeight())) {
                    var_ = this.currentLayout.getSpacingAdd();
                }
                f7 -= var_;
            } else if (f7 - f6 > ((float) this.lineHeight)) {
                float var_ = this.heightOffset;
                if (f7 != ((float) this.currentLayout.getHeight())) {
                    var_ = ((float) this.currentLayout.getLineBottom(this.currentLine)) - this.currentLayout.getSpacingAdd();
                }
                f7 = var_ + var_;
            }
            int i = this.baselineShift;
            if (i < 0) {
                f7 += (float) i;
            } else if (i > 0) {
                f6 += (float) i;
            }
            float var_ = f6;
            float var_ = f7;
            if (this.useRoundRect) {
                if (this.rect == null) {
                    this.rect = new RectF();
                }
                this.rect.set(var_ - ((float) AndroidUtilities.dp(4.0f)), var_, f9 + ((float) AndroidUtilities.dp(4.0f)), var_);
                super.addRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), direction);
                return;
            }
            super.addRect(var_, var_, f9, var_, direction);
        }
    }

    public void reset() {
        if (this.allowReset) {
            super.reset();
        }
    }
}
