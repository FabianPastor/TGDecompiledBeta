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

    public PlayPauseDrawable(int size2) {
        this.size = AndroidUtilities.dp((float) size2);
        Paint paint2 = new Paint(1);
        this.paint = paint2;
        paint2.setColor(-1);
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x0062  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0066  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00a1  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00ac  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void draw(android.graphics.Canvas r15) {
        /*
            r14 = this;
            long r0 = android.view.animation.AnimationUtils.currentAnimationTimeMillis()
            long r2 = r14.lastUpdateTime
            long r2 = r0 - r2
            r14.lastUpdateTime = r0
            r4 = 18
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 <= 0) goto L_0x0012
            r2 = 16
        L_0x0012:
            boolean r4 = r14.pause
            r5 = 1065353216(0x3var_, float:1.0)
            if (r4 == 0) goto L_0x0037
            float r6 = r14.progress
            int r7 = (r6 > r5 ? 1 : (r6 == r5 ? 0 : -1))
            if (r7 >= 0) goto L_0x0037
            float r4 = (float) r2
            float r7 = r14.duration
            float r4 = r4 / r7
            float r6 = r6 + r4
            r14.progress = r6
            int r4 = (r6 > r5 ? 1 : (r6 == r5 ? 0 : -1))
            if (r4 < 0) goto L_0x002c
            r14.progress = r5
            goto L_0x0058
        L_0x002c:
            android.view.View r4 = r14.parent
            if (r4 == 0) goto L_0x0033
            r4.invalidate()
        L_0x0033:
            r14.invalidateSelf()
            goto L_0x0058
        L_0x0037:
            if (r4 != 0) goto L_0x0058
            float r4 = r14.progress
            r6 = 0
            int r7 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r7 <= 0) goto L_0x0058
            float r7 = (float) r2
            float r8 = r14.duration
            float r7 = r7 / r8
            float r4 = r4 - r7
            r14.progress = r4
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r4 > 0) goto L_0x004e
            r14.progress = r6
            goto L_0x0058
        L_0x004e:
            android.view.View r4 = r14.parent
            if (r4 == 0) goto L_0x0055
            r4.invalidate()
        L_0x0055:
            r14.invalidateSelf()
        L_0x0058:
            android.graphics.Rect r4 = r14.getBounds()
            int r6 = r14.alpha
            r7 = 255(0xff, float:3.57E-43)
            if (r6 != r7) goto L_0x0066
            r15.save()
            goto L_0x007a
        L_0x0066:
            int r6 = r4.left
            float r8 = (float) r6
            int r6 = r4.top
            float r9 = (float) r6
            int r6 = r4.right
            float r10 = (float) r6
            int r6 = r4.bottom
            float r11 = (float) r6
            int r12 = r14.alpha
            r13 = 31
            r7 = r15
            r7.saveLayerAlpha(r8, r9, r10, r11, r12, r13)
        L_0x007a:
            int r6 = r4.centerX()
            float r6 = (float) r6
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r7 = (float) r7
            float r8 = r14.progress
            float r8 = r5 - r8
            float r7 = r7 * r8
            float r6 = r6 + r7
            int r7 = r4.centerY()
            float r7 = (float) r7
            r15.translate(r6, r7)
            r6 = 1140457472(0x43fa0000, float:500.0)
            float r7 = r14.progress
            float r7 = r7 * r6
            r6 = -1063256064(0xffffffffc0a00000, float:-5.0)
            r8 = 1120403456(0x42CLASSNAME, float:100.0)
            int r9 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r9 >= 0) goto L_0x00ac
            org.telegram.ui.Components.CubicBezierInterpolator r9 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            float r8 = r7 / r8
            float r8 = r9.getInterpolation(r8)
            float r8 = r8 * r6
            goto L_0x00c5
        L_0x00ac:
            r9 = 1139933184(0x43var_, float:484.0)
            int r9 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r9 >= 0) goto L_0x00c3
            r9 = 1119748096(0x42be0000, float:95.0)
            org.telegram.ui.Components.CubicBezierInterpolator r10 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            float r8 = r7 - r8
            r11 = 1136656384(0x43CLASSNAME, float:384.0)
            float r8 = r8 / r11
            float r8 = r10.getInterpolation(r8)
            float r8 = r8 * r9
            float r8 = r8 + r6
            goto L_0x00c5
        L_0x00c3:
            r8 = 1119092736(0x42b40000, float:90.0)
        L_0x00c5:
            r6 = 1069128090(0x3fb9999a, float:1.45)
            int r9 = r14.size
            float r9 = (float) r9
            float r9 = r9 * r6
            r6 = 1105199104(0x41e00000, float:28.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r10 = (float) r10
            float r9 = r9 / r10
            r10 = 1069547520(0x3fCLASSNAME, float:1.5)
            int r11 = r14.size
            float r11 = (float) r11
            float r11 = r11 * r10
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r11 = r11 / r6
            r15.scale(r9, r11)
            r15.rotate(r8)
            org.telegram.ui.Components.PathAnimator r6 = org.telegram.ui.ActionBar.Theme.playPauseAnimator
            android.graphics.Paint r9 = r14.paint
            r6.draw(r15, r9, r7)
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            r15.scale(r5, r6)
            org.telegram.ui.Components.PathAnimator r5 = org.telegram.ui.ActionBar.Theme.playPauseAnimator
            android.graphics.Paint r6 = r14.paint
            r5.draw(r15, r6, r7)
            r15.restore()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PlayPauseDrawable.draw(android.graphics.Canvas):void");
    }

    public void setPause(boolean pause2) {
        setPause(pause2, true);
    }

    public void setPause(boolean pause2, boolean animated) {
        if (this.pause != pause2) {
            this.pause = pause2;
            if (!animated) {
                this.progress = pause2 ? 1.0f : 0.0f;
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

    public int getOpacity() {
        return -2;
    }

    public int getIntrinsicWidth() {
        return this.size;
    }

    public int getIntrinsicHeight() {
        return this.size;
    }

    public void setParent(View parent2) {
        this.parent = parent2;
    }

    public void setDuration(int duration2) {
        this.duration = (float) duration2;
    }
}
