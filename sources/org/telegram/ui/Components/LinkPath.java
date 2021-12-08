package org.telegram.ui.Components;

import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.text.Layout;
import org.telegram.messenger.AndroidUtilities;

public class LinkPath extends Path {
    private boolean allowReset = true;
    private int baselineShift;
    private Layout currentLayout;
    private int currentLine;
    private float heightOffset;
    private float lastTop = -1.0f;
    private int lineHeight;
    private RectF rect;
    private boolean useRoundRect;

    public LinkPath() {
    }

    public LinkPath(boolean roundRect) {
        this.useRoundRect = roundRect;
    }

    public void setCurrentLayout(Layout layout, int start, float yOffset) {
        int lineCount;
        this.currentLayout = layout;
        this.currentLine = layout.getLineForOffset(start);
        this.lastTop = -1.0f;
        this.heightOffset = yOffset;
        if (Build.VERSION.SDK_INT >= 28 && (lineCount = layout.getLineCount()) > 0) {
            this.lineHeight = layout.getLineBottom(lineCount - 1) - layout.getLineTop(lineCount - 1);
        }
    }

    public void setAllowReset(boolean value) {
        this.allowReset = value;
    }

    public void setUseRoundRect(boolean value) {
        this.useRoundRect = value;
    }

    public boolean isUsingRoundRect() {
        return this.useRoundRect;
    }

    public void setBaselineShift(int value) {
        this.baselineShift = value;
    }

    public void addRect(float left, float top, float right, float bottom, Path.Direction dir) {
        float y2;
        float y22;
        float y;
        float f = this.heightOffset;
        float top2 = top + f;
        float bottom2 = bottom + f;
        float f2 = this.lastTop;
        if (f2 == -1.0f) {
            this.lastTop = top2;
        } else if (f2 != top2) {
            this.lastTop = top2;
            this.currentLine++;
        }
        float lineRight = this.currentLayout.getLineRight(this.currentLine);
        float lineLeft = this.currentLayout.getLineLeft(this.currentLine);
        if (left >= lineRight) {
            return;
        }
        if (left > lineLeft || right > lineLeft) {
            if (right > lineRight) {
                right = lineRight;
            }
            if (left < lineLeft) {
                left = lineLeft;
            }
            float y3 = top2;
            float f3 = 0.0f;
            if (Build.VERSION.SDK_INT >= 28) {
                y2 = bottom2;
                if (bottom2 - top2 > ((float) this.lineHeight)) {
                    float f4 = this.heightOffset;
                    if (bottom2 != ((float) this.currentLayout.getHeight())) {
                        f3 = ((float) this.currentLayout.getLineBottom(this.currentLine)) - this.currentLayout.getSpacingAdd();
                    }
                    y2 = f4 + f3;
                }
            } else {
                if (bottom2 != ((float) this.currentLayout.getHeight())) {
                    f3 = this.currentLayout.getSpacingAdd();
                }
                y2 = bottom2 - f3;
            }
            int i = this.baselineShift;
            if (i < 0) {
                y = y3;
                y22 = y2 + ((float) i);
            } else if (i > 0) {
                y = y3 + ((float) i);
                y22 = y2;
            } else {
                y = y3;
                y22 = y2;
            }
            if (this.useRoundRect) {
                if (this.rect == null) {
                    this.rect = new RectF();
                }
                this.rect.set(left - ((float) AndroidUtilities.dp(4.0f)), y, ((float) AndroidUtilities.dp(4.0f)) + right, y22);
                super.addRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), dir);
                return;
            }
            super.addRect(left, y, right, y22, dir);
        }
    }

    public void reset() {
        if (this.allowReset) {
            super.reset();
        }
    }
}
