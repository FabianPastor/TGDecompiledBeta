package org.telegram.ui.Components;

import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.AnimationUtils;
import org.telegram.messenger.AndroidUtilities;

public class PlayPauseDrawable extends Drawable {
    private int alpha = 255;
    float duration = 300.0f;
    private long lastUpdateTime;
    private final Paint paint;
    private View parent;
    private boolean pause;
    private float progress;
    private final int size;

    public int getOpacity() {
        return -2;
    }

    public PlayPauseDrawable(int i) {
        this.size = AndroidUtilities.dp((float) i);
        Paint paint2 = new Paint(1);
        this.paint = paint2;
        paint2.setColor(-1);
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x0062  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0066  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x009f  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00aa  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void draw(android.graphics.Canvas r10) {
        /*
            r9 = this;
            long r0 = android.view.animation.AnimationUtils.currentAnimationTimeMillis()
            long r2 = r9.lastUpdateTime
            long r2 = r0 - r2
            r9.lastUpdateTime = r0
            r0 = 18
            int r4 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
            if (r4 <= 0) goto L_0x0012
            r2 = 16
        L_0x0012:
            boolean r0 = r9.pause
            r1 = 1065353216(0x3var_, float:1.0)
            if (r0 == 0) goto L_0x0037
            float r4 = r9.progress
            int r5 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r5 >= 0) goto L_0x0037
            float r0 = (float) r2
            float r2 = r9.duration
            float r0 = r0 / r2
            float r4 = r4 + r0
            r9.progress = r4
            int r0 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r0 < 0) goto L_0x002c
            r9.progress = r1
            goto L_0x0058
        L_0x002c:
            android.view.View r0 = r9.parent
            if (r0 == 0) goto L_0x0033
            r0.invalidate()
        L_0x0033:
            r9.invalidateSelf()
            goto L_0x0058
        L_0x0037:
            if (r0 != 0) goto L_0x0058
            float r0 = r9.progress
            r4 = 0
            int r5 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r5 <= 0) goto L_0x0058
            float r2 = (float) r2
            float r3 = r9.duration
            float r2 = r2 / r3
            float r0 = r0 - r2
            r9.progress = r0
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 > 0) goto L_0x004e
            r9.progress = r4
            goto L_0x0058
        L_0x004e:
            android.view.View r0 = r9.parent
            if (r0 == 0) goto L_0x0055
            r0.invalidate()
        L_0x0055:
            r9.invalidateSelf()
        L_0x0058:
            android.graphics.Rect r0 = r9.getBounds()
            int r7 = r9.alpha
            r2 = 255(0xff, float:3.57E-43)
            if (r7 != r2) goto L_0x0066
            r10.save()
            goto L_0x0078
        L_0x0066:
            int r2 = r0.left
            float r3 = (float) r2
            int r2 = r0.top
            float r4 = (float) r2
            int r2 = r0.right
            float r5 = (float) r2
            int r2 = r0.bottom
            float r6 = (float) r2
            r8 = 31
            r2 = r10
            r2.saveLayerAlpha(r3, r4, r5, r6, r7, r8)
        L_0x0078:
            int r2 = r0.centerX()
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r3 = (float) r3
            float r4 = r9.progress
            float r4 = r1 - r4
            float r3 = r3 * r4
            float r2 = r2 + r3
            int r0 = r0.centerY()
            float r0 = (float) r0
            r10.translate(r2, r0)
            r0 = 1140457472(0x43fa0000, float:500.0)
            float r2 = r9.progress
            float r2 = r2 * r0
            r0 = -1063256064(0xffffffffc0a00000, float:-5.0)
            r3 = 1120403456(0x42CLASSNAME, float:100.0)
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 >= 0) goto L_0x00aa
            org.telegram.ui.Components.CubicBezierInterpolator r4 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            float r3 = r2 / r3
            float r3 = r4.getInterpolation(r3)
            float r3 = r3 * r0
            goto L_0x00c3
        L_0x00aa:
            r4 = 1139933184(0x43var_, float:484.0)
            int r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r4 >= 0) goto L_0x00c1
            r4 = 1119748096(0x42be0000, float:95.0)
            org.telegram.ui.Components.CubicBezierInterpolator r5 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            float r3 = r2 - r3
            r6 = 1136656384(0x43CLASSNAME, float:384.0)
            float r3 = r3 / r6
            float r3 = r5.getInterpolation(r3)
            float r3 = r3 * r4
            float r3 = r3 + r0
            goto L_0x00c3
        L_0x00c1:
            r3 = 1119092736(0x42b40000, float:90.0)
        L_0x00c3:
            r0 = 1069128090(0x3fb9999a, float:1.45)
            int r4 = r9.size
            float r4 = (float) r4
            float r4 = r4 * r0
            r0 = 1105199104(0x41e00000, float:28.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r0)
            float r5 = (float) r5
            float r4 = r4 / r5
            r5 = 1069547520(0x3fCLASSNAME, float:1.5)
            int r6 = r9.size
            float r6 = (float) r6
            float r6 = r6 * r5
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            float r0 = (float) r0
            float r6 = r6 / r0
            r10.scale(r4, r6)
            r10.rotate(r3)
            org.telegram.ui.Components.PathAnimator r0 = org.telegram.ui.ActionBar.Theme.playPauseAnimator
            android.graphics.Paint r3 = r9.paint
            r0.draw(r10, r3, r2)
            r0 = -1082130432(0xffffffffbvar_, float:-1.0)
            r10.scale(r1, r0)
            org.telegram.ui.Components.PathAnimator r0 = org.telegram.ui.ActionBar.Theme.playPauseAnimator
            android.graphics.Paint r1 = r9.paint
            r0.draw(r10, r1, r2)
            r10.restore()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PlayPauseDrawable.draw(android.graphics.Canvas):void");
    }

    public void setPause(boolean z) {
        setPause(z, true);
    }

    public void setPause(boolean z, boolean z2) {
        if (this.pause != z) {
            this.pause = z;
            if (!z2) {
                this.progress = z ? 1.0f : 0.0f;
            }
            this.lastUpdateTime = AnimationUtils.currentAnimationTimeMillis();
            invalidateSelf();
        }
    }

    public void setAlpha(int i) {
        this.alpha = i;
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
    }

    public int getIntrinsicWidth() {
        return this.size;
    }

    public int getIntrinsicHeight() {
        return this.size;
    }

    public void setParent(View view) {
        this.parent = view;
    }

    public void setDuration(int i) {
        this.duration = (float) i;
    }
}
