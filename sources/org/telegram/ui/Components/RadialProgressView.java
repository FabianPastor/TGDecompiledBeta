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
        this.drawingCircleLenght = radialProgressView.drawingCircleLenght;
        this.currentProgressTime = radialProgressView.currentProgressTime;
        this.currentProgress = radialProgressView.currentProgress;
        this.progressTime = radialProgressView.progressTime;
        this.animatedProgress = radialProgressView.animatedProgress;
        this.risingCircleLength = radialProgressView.risingCircleLength;
        this.progressAnimationStart = radialProgressView.progressAnimationStart;
        updateAnimation(85);
    }

    private void updateAnimation() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - this.lastUpdateTime;
        if (j > 17) {
            j = 17;
        }
        this.lastUpdateTime = currentTimeMillis;
        updateAnimation(j);
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x004c  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00f0  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateAnimation(long r9) {
        /*
            r8 = this;
            float r0 = r8.radOffset
            r1 = 360(0x168, double:1.78E-321)
            long r1 = r1 * r9
            float r1 = (float) r1
            r2 = 1157234688(0x44fa0000, float:2000.0)
            float r1 = r1 / r2
            float r0 = r0 + r1
            r8.radOffset = r0
            r1 = 1135869952(0x43b40000, float:360.0)
            float r2 = r0 / r1
            int r2 = (int) r2
            int r2 = r2 * 360
            float r2 = (float) r2
            float r0 = r0 - r2
            r8.radOffset = r0
            boolean r0 = r8.toCircle
            r2 = 1065353216(0x3var_, float:1.0)
            r3 = 0
            if (r0 == 0) goto L_0x0032
            float r4 = r8.toCircleProgress
            int r5 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r5 == 0) goto L_0x0032
            r0 = 1033171465(0x3d94var_, float:0.07272727)
            float r4 = r4 + r0
            r8.toCircleProgress = r4
            int r0 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r0 <= 0) goto L_0x0046
            r8.toCircleProgress = r2
            goto L_0x0046
        L_0x0032:
            if (r0 != 0) goto L_0x0046
            float r0 = r8.toCircleProgress
            int r4 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r4 == 0) goto L_0x0046
            r4 = 1025758986(0x3d23d70a, float:0.04)
            float r0 = r0 - r4
            r8.toCircleProgress = r0
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 >= 0) goto L_0x0046
            r8.toCircleProgress = r3
        L_0x0046:
            boolean r0 = r8.noProgress
            r4 = 1082130432(0x40800000, float:4.0)
            if (r0 == 0) goto L_0x00f0
            float r0 = r8.toCircleProgress
            r5 = 1132789760(0x43850000, float:266.0)
            r6 = 1132920832(0x43870000, float:270.0)
            r7 = 1140457472(0x43fa0000, float:500.0)
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x00a1
            float r0 = r8.currentProgressTime
            float r9 = (float) r9
            float r0 = r0 + r9
            r8.currentProgressTime = r0
            int r9 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r9 < 0) goto L_0x0064
            r8.currentProgressTime = r7
        L_0x0064:
            boolean r9 = r8.risingCircleLength
            if (r9 == 0) goto L_0x0077
            android.view.animation.AccelerateInterpolator r9 = r8.accelerateInterpolator
            float r10 = r8.currentProgressTime
            float r10 = r10 / r7
            float r9 = r9.getInterpolation(r10)
            float r9 = r9 * r5
            float r9 = r9 + r4
            r8.currentCircleLength = r9
            goto L_0x0086
        L_0x0077:
            android.view.animation.DecelerateInterpolator r9 = r8.decelerateInterpolator
            float r10 = r8.currentProgressTime
            float r10 = r10 / r7
            float r9 = r9.getInterpolation(r10)
            float r2 = r2 - r9
            float r2 = r2 * r6
            float r4 = r4 - r2
            r8.currentCircleLength = r4
        L_0x0086:
            float r9 = r8.currentProgressTime
            int r9 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
            if (r9 != 0) goto L_0x0127
            boolean r9 = r8.risingCircleLength
            if (r9 == 0) goto L_0x0099
            float r10 = r8.radOffset
            float r10 = r10 + r6
            r8.radOffset = r10
            r10 = -1014693888(0xffffffffCLASSNAME, float:-266.0)
            r8.currentCircleLength = r10
        L_0x0099:
            r9 = r9 ^ 1
            r8.risingCircleLength = r9
            r8.currentProgressTime = r3
            goto L_0x0127
        L_0x00a1:
            boolean r9 = r8.risingCircleLength
            if (r9 == 0) goto L_0x00c9
            float r9 = r8.currentCircleLength
            android.view.animation.AccelerateInterpolator r10 = r8.accelerateInterpolator
            float r0 = r8.currentProgressTime
            float r0 = r0 / r7
            float r10 = r10.getInterpolation(r0)
            float r10 = r10 * r5
            float r10 = r10 + r4
            r8.currentCircleLength = r10
            float r0 = r8.toCircleProgress
            float r0 = r0 * r1
            float r10 = r10 + r0
            r8.currentCircleLength = r10
            float r0 = r9 - r10
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 <= 0) goto L_0x0127
            float r0 = r8.radOffset
            float r9 = r9 - r10
            float r0 = r0 + r9
            r8.radOffset = r0
            goto L_0x0127
        L_0x00c9:
            float r9 = r8.currentCircleLength
            android.view.animation.DecelerateInterpolator r10 = r8.decelerateInterpolator
            float r0 = r8.currentProgressTime
            float r0 = r0 / r7
            float r10 = r10.getInterpolation(r0)
            float r2 = r2 - r10
            float r2 = r2 * r6
            float r4 = r4 - r2
            r8.currentCircleLength = r4
            r10 = 1136001024(0x43b60000, float:364.0)
            float r0 = r8.toCircleProgress
            float r0 = r0 * r10
            float r4 = r4 - r0
            r8.currentCircleLength = r4
            float r10 = r9 - r4
            int r10 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
            if (r10 <= 0) goto L_0x0127
            float r10 = r8.radOffset
            float r9 = r9 - r4
            float r10 = r10 + r9
            r8.radOffset = r10
            goto L_0x0127
        L_0x00f0:
            float r0 = r8.currentProgress
            float r2 = r8.progressAnimationStart
            float r5 = r0 - r2
            int r3 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r3 <= 0) goto L_0x011d
            int r3 = r8.progressTime
            long r6 = (long) r3
            long r6 = r6 + r9
            int r9 = (int) r6
            r8.progressTime = r9
            float r10 = (float) r9
            r3 = 1128792064(0x43480000, float:200.0)
            int r10 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
            if (r10 < 0) goto L_0x0110
            r8.progressAnimationStart = r0
            r8.animatedProgress = r0
            r9 = 0
            r8.progressTime = r9
            goto L_0x011d
        L_0x0110:
            android.view.animation.DecelerateInterpolator r10 = org.telegram.messenger.AndroidUtilities.decelerateInterpolator
            float r9 = (float) r9
            float r9 = r9 / r3
            float r9 = r10.getInterpolation(r9)
            float r5 = r5 * r9
            float r2 = r2 + r5
            r8.animatedProgress = r2
        L_0x011d:
            float r9 = r8.animatedProgress
            float r9 = r9 * r1
            float r9 = java.lang.Math.max(r4, r9)
            r8.currentCircleLength = r9
        L_0x0127:
            r8.invalidate()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RadialProgressView.updateAnimation(long):void");
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
