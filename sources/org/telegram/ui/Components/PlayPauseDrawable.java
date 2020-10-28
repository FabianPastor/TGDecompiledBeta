package org.telegram.ui.Components;

import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.animation.AnimationUtils;
import org.telegram.messenger.AndroidUtilities;

public class PlayPauseDrawable extends Drawable {
    private long lastUpdateTime;
    private final Paint paint;
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

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0076  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0081  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void draw(android.graphics.Canvas r8) {
        /*
            r7 = this;
            long r0 = android.view.animation.AnimationUtils.currentAnimationTimeMillis()
            long r2 = r7.lastUpdateTime
            long r2 = r0 - r2
            r7.lastUpdateTime = r0
            r0 = 18
            int r4 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
            if (r4 <= 0) goto L_0x0012
            r2 = 16
        L_0x0012:
            boolean r0 = r7.pause
            r1 = 1133903872(0x43960000, float:300.0)
            r4 = 1065353216(0x3var_, float:1.0)
            if (r0 == 0) goto L_0x0030
            float r5 = r7.progress
            int r6 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x0030
            float r0 = (float) r2
            float r0 = r0 / r1
            float r5 = r5 + r0
            r7.progress = r5
            int r0 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1))
            if (r0 < 0) goto L_0x002c
            r7.progress = r4
            goto L_0x0048
        L_0x002c:
            r7.invalidateSelf()
            goto L_0x0048
        L_0x0030:
            if (r0 != 0) goto L_0x0048
            float r0 = r7.progress
            r5 = 0
            int r6 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r6 <= 0) goto L_0x0048
            float r2 = (float) r2
            float r2 = r2 / r1
            float r0 = r0 - r2
            r7.progress = r0
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 > 0) goto L_0x0045
            r7.progress = r5
            goto L_0x0048
        L_0x0045:
            r7.invalidateSelf()
        L_0x0048:
            android.graphics.Rect r0 = r7.getBounds()
            r8.save()
            int r1 = r0.centerX()
            float r1 = (float) r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r2 = (float) r2
            float r3 = r7.progress
            float r3 = r4 - r3
            float r2 = r2 * r3
            float r1 = r1 + r2
            int r0 = r0.centerY()
            float r0 = (float) r0
            r8.translate(r1, r0)
            r0 = 1140457472(0x43fa0000, float:500.0)
            float r1 = r7.progress
            float r1 = r1 * r0
            r0 = -1063256064(0xffffffffc0a00000, float:-5.0)
            r2 = 1120403456(0x42CLASSNAME, float:100.0)
            int r3 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x0081
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            float r2 = r1 / r2
            float r2 = r3.getInterpolation(r2)
            float r2 = r2 * r0
            goto L_0x009a
        L_0x0081:
            r3 = 1139933184(0x43var_, float:484.0)
            int r3 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r3 >= 0) goto L_0x0098
            r3 = 1119748096(0x42be0000, float:95.0)
            org.telegram.ui.Components.CubicBezierInterpolator r5 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            float r2 = r1 - r2
            r6 = 1136656384(0x43CLASSNAME, float:384.0)
            float r2 = r2 / r6
            float r2 = r5.getInterpolation(r2)
            float r2 = r2 * r3
            float r2 = r2 + r0
            goto L_0x009a
        L_0x0098:
            r2 = 1119092736(0x42b40000, float:90.0)
        L_0x009a:
            r0 = 1069128090(0x3fb9999a, float:1.45)
            int r3 = r7.size
            float r3 = (float) r3
            float r3 = r3 * r0
            r0 = 1105199104(0x41e00000, float:28.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r0)
            float r5 = (float) r5
            float r3 = r3 / r5
            r5 = 1069547520(0x3fCLASSNAME, float:1.5)
            int r6 = r7.size
            float r6 = (float) r6
            float r6 = r6 * r5
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            float r0 = (float) r0
            float r6 = r6 / r0
            r8.scale(r3, r6)
            r8.rotate(r2)
            org.telegram.ui.Components.PathAnimator r0 = org.telegram.ui.ActionBar.Theme.playPauseAnimator
            android.graphics.Paint r2 = r7.paint
            r0.draw(r8, r2, r1)
            r0 = -1082130432(0xffffffffbvar_, float:-1.0)
            r8.scale(r4, r0)
            org.telegram.ui.Components.PathAnimator r0 = org.telegram.ui.ActionBar.Theme.playPauseAnimator
            android.graphics.Paint r2 = r7.paint
            r0.draw(r8, r2, r1)
            r8.restore()
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
        this.paint.setAlpha(i);
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
}
