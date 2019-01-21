package org.telegram.ui.Components;

import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.text.StaticLayout;
import org.telegram.messenger.AndroidUtilities;

public class LinkPath extends Path {
    private boolean allowReset = true;
    private int baselineShift;
    private StaticLayout currentLayout;
    private int currentLine;
    private float heightOffset;
    private float lastTop = -1.0f;
    private RectF rect;
    private boolean useRoundRect;

    public LinkPath(boolean roundRect) {
        this.useRoundRect = roundRect;
    }

    public void setCurrentLayout(StaticLayout layout, int start, float yOffset) {
        this.currentLayout = layout;
        this.currentLine = layout.getLineForOffset(start);
        this.lastTop = -1.0f;
        this.heightOffset = yOffset;
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

    public void addRect(float left, float top, float right, float bottom, Direction dir) {
        top += this.heightOffset;
        bottom += this.heightOffset;
        if (this.lastTop == -1.0f) {
            this.lastTop = top;
        } else if (this.lastTop != top) {
            this.lastTop = top;
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
            float y = top;
            float y2 = bottom - (bottom != ((float) this.currentLayout.getHeight()) ? this.currentLayout.getSpacingAdd() : 0.0f);
            if (this.baselineShift < 0) {
                y2 += (float) this.baselineShift;
            } else if (this.baselineShift > 0) {
                y += (float) this.baselineShift;
            }
            if (this.useRoundRect) {
                if (this.rect == null) {
                    this.rect = new RectF();
                }
                this.rect.set(left - ((float) AndroidUtilities.dp(4.0f)), y, ((float) AndroidUtilities.dp(4.0f)) + right, y2);
                super.addRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), dir);
                return;
            }
            super.addRect(left, y, right, y2, dir);
        }
    }

    public void reset() {
        if (this.allowReset) {
            super.reset();
        }
    }
}
