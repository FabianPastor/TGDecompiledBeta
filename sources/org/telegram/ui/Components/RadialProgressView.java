package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import androidx.annotation.Keep;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class RadialProgressView extends View {
    private AccelerateInterpolator accelerateInterpolator;
    private float animatedProgress;
    private RectF cicleRect;
    private float currentCircleLength;
    private float currentProgress;
    private float currentProgressTime;
    private DecelerateInterpolator decelerateInterpolator;
    private float drawingCircleLenght;
    private long lastUpdateTime;
    private boolean noProgress;
    private float progressAnimationStart;
    private int progressColor;
    private Paint progressPaint;
    private int progressTime;
    private float radOffset;
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean risingCircleLength;
    private int size;
    private boolean toCircle;
    private float toCircleProgress;
    private boolean useSelfAlpha;

    public RadialProgressView(Context context) {
        this(context, (Theme.ResourcesProvider) null);
    }

    public RadialProgressView(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.cicleRect = new RectF();
        this.noProgress = true;
        this.resourcesProvider = resourcesProvider2;
        this.size = AndroidUtilities.dp(40.0f);
        this.progressColor = getThemedColor("progressCircle");
        this.decelerateInterpolator = new DecelerateInterpolator();
        this.accelerateInterpolator = new AccelerateInterpolator();
        Paint paint = new Paint(1);
        this.progressPaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.progressPaint.setStrokeCap(Paint.Cap.ROUND);
        this.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        this.progressPaint.setColor(this.progressColor);
    }

    public void setUseSelfAlpha(boolean z) {
        this.useSelfAlpha = z;
    }

    @Keep
    public void setAlpha(float f) {
        super.setAlpha(f);
        if (this.useSelfAlpha) {
            Drawable background = getBackground();
            int i = (int) (f * 255.0f);
            if (background != null) {
                background.setAlpha(i);
            }
            this.progressPaint.setAlpha(i);
        }
    }

    public void setNoProgress(boolean z) {
        this.noProgress = z;
    }

    public void setProgress(float f) {
        this.currentProgress = f;
        if (this.animatedProgress > f) {
            this.animatedProgress = f;
        }
        this.progressAnimationStart = this.animatedProgress;
        this.progressTime = 0;
    }

    public void sync(RadialProgressView radialProgressView) {
        this.lastUpdateTime = radialProgressView.lastUpdateTime;
        this.radOffset = radialProgressView.radOffset;
        this.toCircle = radialProgressView.toCircle;
        this.toCircleProgress = radialProgressView.toCircleProgress;
        this.noProgress = radialProgressView.noProgress;
        this.currentCircleLength = radialProgressView.currentCircleLength;
        this.currentProgressTime = radialProgressView.currentProgressTime;
        this.currentProgress = radialProgressView.currentProgress;
        this.progressTime = radialProgressView.progressTime;
        this.animatedProgress = radialProgressView.animatedProgress;
        this.risingCircleLength = radialProgressView.risingCircleLength;
        this.progressAnimationStart = radialProgressView.progressAnimationStart;
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x005d  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0101  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateAnimation() {
        /*
            r10 = this;
            long r0 = java.lang.System.currentTimeMillis()
            long r2 = r10.lastUpdateTime
            long r2 = r0 - r2
            r4 = 17
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 <= 0) goto L_0x000f
            r2 = r4
        L_0x000f:
            r10.lastUpdateTime = r0
            float r0 = r10.radOffset
            r4 = 360(0x168, double:1.78E-321)
            long r4 = r4 * r2
            float r1 = (float) r4
            r4 = 1157234688(0x44fa0000, float:2000.0)
            float r1 = r1 / r4
            float r0 = r0 + r1
            r10.radOffset = r0
            r1 = 1135869952(0x43b40000, float:360.0)
            float r4 = r0 / r1
            int r4 = (int) r4
            int r4 = r4 * 360
            float r4 = (float) r4
            float r0 = r0 - r4
            r10.radOffset = r0
            boolean r0 = r10.toCircle
            r4 = 1065353216(0x3var_, float:1.0)
            r5 = 0
            if (r0 == 0) goto L_0x0043
            float r6 = r10.toCircleProgress
            int r7 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r7 == 0) goto L_0x0043
            r0 = 1033171465(0x3d94var_, float:0.07272727)
            float r6 = r6 + r0
            r10.toCircleProgress = r6
            int r0 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r0 <= 0) goto L_0x0057
            r10.toCircleProgress = r4
            goto L_0x0057
        L_0x0043:
            if (r0 != 0) goto L_0x0057
            float r0 = r10.toCircleProgress
            int r6 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r6 == 0) goto L_0x0057
            r6 = 1025758986(0x3d23d70a, float:0.04)
            float r0 = r0 - r6
            r10.toCircleProgress = r0
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 >= 0) goto L_0x0057
            r10.toCircleProgress = r5
        L_0x0057:
            boolean r0 = r10.noProgress
            r6 = 1082130432(0x40800000, float:4.0)
            if (r0 == 0) goto L_0x0101
            float r0 = r10.toCircleProgress
            r7 = 1132789760(0x43850000, float:266.0)
            r8 = 1132920832(0x43870000, float:270.0)
            r9 = 1140457472(0x43fa0000, float:500.0)
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 != 0) goto L_0x00b2
            float r0 = r10.currentProgressTime
            float r1 = (float) r2
            float r0 = r0 + r1
            r10.currentProgressTime = r0
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 < 0) goto L_0x0075
            r10.currentProgressTime = r9
        L_0x0075:
            boolean r0 = r10.risingCircleLength
            if (r0 == 0) goto L_0x0088
            android.view.animation.AccelerateInterpolator r0 = r10.accelerateInterpolator
            float r1 = r10.currentProgressTime
            float r1 = r1 / r9
            float r0 = r0.getInterpolation(r1)
            float r0 = r0 * r7
            float r0 = r0 + r6
            r10.currentCircleLength = r0
            goto L_0x0097
        L_0x0088:
            android.view.animation.DecelerateInterpolator r0 = r10.decelerateInterpolator
            float r1 = r10.currentProgressTime
            float r1 = r1 / r9
            float r0 = r0.getInterpolation(r1)
            float r4 = r4 - r0
            float r4 = r4 * r8
            float r6 = r6 - r4
            r10.currentCircleLength = r6
        L_0x0097:
            float r0 = r10.currentProgressTime
            int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r0 != 0) goto L_0x0138
            boolean r0 = r10.risingCircleLength
            if (r0 == 0) goto L_0x00aa
            float r1 = r10.radOffset
            float r1 = r1 + r8
            r10.radOffset = r1
            r1 = -1014693888(0xffffffffCLASSNAME, float:-266.0)
            r10.currentCircleLength = r1
        L_0x00aa:
            r0 = r0 ^ 1
            r10.risingCircleLength = r0
            r10.currentProgressTime = r5
            goto L_0x0138
        L_0x00b2:
            boolean r0 = r10.risingCircleLength
            if (r0 == 0) goto L_0x00da
            float r0 = r10.currentCircleLength
            android.view.animation.AccelerateInterpolator r2 = r10.accelerateInterpolator
            float r3 = r10.currentProgressTime
            float r3 = r3 / r9
            float r2 = r2.getInterpolation(r3)
            float r2 = r2 * r7
            float r2 = r2 + r6
            r10.currentCircleLength = r2
            float r3 = r10.toCircleProgress
            float r3 = r3 * r1
            float r2 = r2 + r3
            r10.currentCircleLength = r2
            float r1 = r0 - r2
            int r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x0138
            float r1 = r10.radOffset
            float r0 = r0 - r2
            float r1 = r1 + r0
            r10.radOffset = r1
            goto L_0x0138
        L_0x00da:
            float r0 = r10.currentCircleLength
            android.view.animation.DecelerateInterpolator r1 = r10.decelerateInterpolator
            float r2 = r10.currentProgressTime
            float r2 = r2 / r9
            float r1 = r1.getInterpolation(r2)
            float r4 = r4 - r1
            float r4 = r4 * r8
            float r6 = r6 - r4
            r10.currentCircleLength = r6
            r1 = 1136001024(0x43b60000, float:364.0)
            float r2 = r10.toCircleProgress
            float r2 = r2 * r1
            float r6 = r6 - r2
            r10.currentCircleLength = r6
            float r1 = r0 - r6
            int r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x0138
            float r1 = r10.radOffset
            float r0 = r0 - r6
            float r1 = r1 + r0
            r10.radOffset = r1
            goto L_0x0138
        L_0x0101:
            float r0 = r10.currentProgress
            float r4 = r10.progressAnimationStart
            float r7 = r0 - r4
            int r5 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r5 <= 0) goto L_0x012e
            int r5 = r10.progressTime
            long r8 = (long) r5
            long r8 = r8 + r2
            int r2 = (int) r8
            r10.progressTime = r2
            float r3 = (float) r2
            r5 = 1128792064(0x43480000, float:200.0)
            int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r3 < 0) goto L_0x0121
            r10.progressAnimationStart = r0
            r10.animatedProgress = r0
            r0 = 0
            r10.progressTime = r0
            goto L_0x012e
        L_0x0121:
            android.view.animation.DecelerateInterpolator r0 = org.telegram.messenger.AndroidUtilities.decelerateInterpolator
            float r2 = (float) r2
            float r2 = r2 / r5
            float r0 = r0.getInterpolation(r2)
            float r7 = r7 * r0
            float r4 = r4 + r7
            r10.animatedProgress = r4
        L_0x012e:
            float r0 = r10.animatedProgress
            float r0 = r0 * r1
            float r0 = java.lang.Math.max(r6, r0)
            r10.currentCircleLength = r0
        L_0x0138:
            r10.invalidate()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RadialProgressView.updateAnimation():void");
    }

    public void setSize(int i) {
        this.size = i;
        invalidate();
    }

    public void setStrokeWidth(float f) {
        this.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(f));
    }

    public void setProgressColor(int i) {
        this.progressColor = i;
        this.progressPaint.setColor(i);
    }

    public void toCircle(boolean z, boolean z2) {
        this.toCircle = z;
        if (!z2) {
            this.toCircleProgress = z ? 1.0f : 0.0f;
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int measuredWidth = (getMeasuredWidth() - this.size) / 2;
        int measuredHeight = getMeasuredHeight();
        int i = this.size;
        int i2 = (measuredHeight - i) / 2;
        this.cicleRect.set((float) measuredWidth, (float) i2, (float) (measuredWidth + i), (float) (i2 + i));
        RectF rectF = this.cicleRect;
        float f = this.radOffset;
        float f2 = this.currentCircleLength;
        this.drawingCircleLenght = f2;
        canvas.drawArc(rectF, f, f2, false, this.progressPaint);
        updateAnimation();
    }

    public void draw(Canvas canvas, float f, float f2) {
        RectF rectF = this.cicleRect;
        int i = this.size;
        rectF.set(f - (((float) i) / 2.0f), f2 - (((float) i) / 2.0f), f + (((float) i) / 2.0f), f2 + (((float) i) / 2.0f));
        RectF rectF2 = this.cicleRect;
        float f3 = this.radOffset;
        float f4 = this.currentCircleLength;
        this.drawingCircleLenght = f4;
        canvas.drawArc(rectF2, f3, f4, false, this.progressPaint);
        updateAnimation();
    }

    public boolean isCircle() {
        return Math.abs(this.drawingCircleLenght) >= 360.0f;
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
