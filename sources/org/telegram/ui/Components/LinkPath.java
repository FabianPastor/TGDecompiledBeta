package org.telegram.ui.Components;

import android.graphics.CornerPathEffect;
import android.graphics.Path;
import android.os.Build;
import android.text.Layout;
import org.telegram.messenger.AndroidUtilities;
/* loaded from: classes3.dex */
public class LinkPath extends Path {
    private static CornerPathEffect roundedEffect;
    private static int roundedEffectRadius;
    private int baselineShift;
    private Layout currentLayout;
    private int currentLine;
    private float heightOffset;
    private int lineHeight;
    private boolean useRoundRect;
    private float lastTop = -1.0f;
    private boolean allowReset = true;

    public static int getRadius() {
        return AndroidUtilities.dp(4.0f);
    }

    public static CornerPathEffect getRoundedEffect() {
        if (roundedEffect == null || roundedEffectRadius != getRadius()) {
            int radius = getRadius();
            roundedEffectRadius = radius;
            roundedEffect = new CornerPathEffect(radius);
        }
        return roundedEffect;
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
        if (Build.VERSION.SDK_INT < 28 || (lineCount = layout.getLineCount()) <= 0) {
            return;
        }
        int i2 = lineCount - 1;
        this.lineHeight = layout.getLineBottom(i2) - layout.getLineTop(i2);
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

    @Override // android.graphics.Path
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
        if (f < lineRight) {
            if (f <= lineLeft && f3 <= lineLeft) {
                return;
            }
            float f9 = f3 > lineRight ? lineRight : f3;
            if (f >= lineLeft) {
                lineLeft = f;
            }
            float var_ = 0.0f;
            if (Build.VERSION.SDK_INT >= 28) {
                if (f7 - f6 > this.lineHeight) {
                    float var_ = this.heightOffset;
                    if (f7 != this.currentLayout.getHeight()) {
                        var_ = this.currentLayout.getLineBottom(this.currentLine) - this.currentLayout.getSpacingAdd();
                    }
                    f7 = var_ + var_;
                }
            } else {
                if (f7 != this.currentLayout.getHeight()) {
                    var_ = this.currentLayout.getSpacingAdd();
                }
                f7 -= var_;
            }
            int i = this.baselineShift;
            if (i < 0) {
                f7 += i;
            } else if (i > 0) {
                f6 += i;
            }
            float var_ = f7;
            float var_ = f6;
            if (this.useRoundRect) {
                super.addRect(lineLeft - (getRadius() / 2.0f), var_, f9 + (getRadius() / 2.0f), var_, direction);
            } else {
                super.addRect(lineLeft, var_, f9, var_, direction);
            }
        }
    }

    @Override // android.graphics.Path
    public void reset() {
        if (!this.allowReset) {
            return;
        }
        super.reset();
    }
}
