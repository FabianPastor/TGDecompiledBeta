package org.telegram.ui.Components;

import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.text.Layout;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;

public class LinkPath extends Path {
    private boolean allowReset = true;
    private int baselineShift;
    private Layout currentLayout;
    private int currentLine;
    private final int halfRadius;
    private float heightOffset;
    private float lastTop = -1.0f;
    private int lineHeight;
    private final int radius;
    private ArrayList<RectF> rects = new ArrayList<>();
    private boolean useRoundRect;

    public LinkPath() {
        int dp = AndroidUtilities.dp(4.0f);
        this.radius = dp;
        this.halfRadius = dp >> 1;
    }

    public LinkPath(boolean z) {
        int dp = AndroidUtilities.dp(4.0f);
        this.radius = dp;
        this.halfRadius = dp >> 1;
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
                RectF rectF = new RectF();
                int i2 = this.halfRadius;
                rectF.set(var_ - ((float) i2), var_, f9 + ((float) i2), var_);
                this.rects.add(rectF);
                return;
            }
            super.addRect(var_, var_, f9, var_, direction);
        }
    }

    public void reset() {
        if (this.allowReset) {
            super.reset();
            this.rects.clear();
        }
    }

    private boolean containsPoint(float f, float f2) {
        Iterator<RectF> it = this.rects.iterator();
        while (it.hasNext()) {
            if (it.next().contains(f, f2)) {
                return true;
            }
        }
        return false;
    }

    public void onPathEnd() {
        if (this.useRoundRect) {
            super.reset();
            int size = this.rects.size();
            for (int i = 0; i < size; i++) {
                float[] fArr = new float[8];
                RectF rectF = this.rects.get(i);
                float f = 0.0f;
                float f2 = containsPoint(rectF.left, rectF.top - ((float) this.radius)) ? 0.0f : (float) this.radius;
                fArr[1] = f2;
                fArr[0] = f2;
                float f3 = containsPoint(rectF.right, rectF.top - ((float) this.radius)) ? 0.0f : (float) this.radius;
                fArr[3] = f3;
                fArr[2] = f3;
                float f4 = containsPoint(rectF.right, rectF.bottom + ((float) this.radius)) ? 0.0f : (float) this.radius;
                fArr[5] = f4;
                fArr[4] = f4;
                if (!containsPoint(rectF.left, rectF.bottom + ((float) this.radius))) {
                    f = (float) this.radius;
                }
                fArr[7] = f;
                fArr[6] = f;
                super.addRoundRect(rectF, fArr, Path.Direction.CW);
            }
        }
    }
}
