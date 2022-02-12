package org.telegram.ui.Components;

import android.graphics.CornerPathEffect;
import android.graphics.Path;
import android.os.Build;
import android.text.Layout;
import org.telegram.messenger.AndroidUtilities;

public class LinkPath extends Path {
    private static final int halfRadius;
    private static final int radius;
    public static final CornerPathEffect roundedEffect;
    private boolean allowReset = true;
    private int baselineShift;
    private Layout currentLayout;
    private int currentLine;
    private float heightOffset;
    private float lastTop = -1.0f;
    private int lineHeight;
    private boolean useRoundRect;

    static {
        int dp = AndroidUtilities.dp(4.0f);
        radius = dp;
        halfRadius = dp >> 1;
        roundedEffect = new CornerPathEffect((float) dp);
    }

    public LinkPath() {
    }

    public LinkPath(boolean z) {
        this.useRoundRect = z;
    }

    public void setCurrentLayout(Layout layout, int i, float f) {
        int lineCount;
        this.currentLayout = layout;
        this.currentLine = layout.getLineForOffset(i);
        this.lastTop = -1.0f;
        this.heightOffset = f;
        if (Build.VERSION.SDK_INT >= 28 && (lineCount = layout.getLineCount()) > 0) {
            int i2 = lineCount - 1;
            this.lineHeight = layout.getLineBottom(i2) - layout.getLineTop(i2);
        }
    }

    public void setAllowReset(boolean z) {
        this.allowReset = z;
    }

    public void setUseRoundRect(boolean z) {
        this.useRoundRect = z;
    }

    public void setBaselineShift(int i) {
        this.baselineShift = i;
    }

    public void addRect(float f, float f2, float f3, float f4, Path.Direction direction) {
        float f5 = this.heightOffset;
        float f6 = f2 + f5;
        float f7 = f5 + f4;
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
            if (f >= lineLeft) {
                lineLeft = f;
            }
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
            float var_ = f7;
            float var_ = f6;
            if (this.useRoundRect) {
                int i2 = halfRadius;
                super.addRect(lineLeft - ((float) i2), var_, f9 + ((float) i2), var_, direction);
                return;
            }
            super.addRect(lineLeft, var_, f9, var_, direction);
        }
    }

    public void reset() {
        if (this.allowReset) {
            super.reset();
        }
    }
}
