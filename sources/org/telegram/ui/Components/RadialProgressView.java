package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class RadialProgressView extends View {
    private static final float risingTime = 500.0f;
    private static final float rotationTime = 2000.0f;
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

    public void setUseSelfAlpha(boolean value) {
        this.useSelfAlpha = value;
    }

    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        if (this.useSelfAlpha) {
            Drawable background = getBackground();
            int a = (int) (255.0f * alpha);
            if (background != null) {
                background.setAlpha(a);
            }
            this.progressPaint.setAlpha(a);
        }
    }

    public void setNoProgress(boolean value) {
        this.noProgress = value;
    }

    public void setProgress(float value) {
        this.currentProgress = value;
        if (this.animatedProgress > value) {
            this.animatedProgress = value;
        }
        this.progressAnimationStart = this.animatedProgress;
        this.progressTime = 0;
    }

    public void sync(RadialProgressView from) {
        this.lastUpdateTime = from.lastUpdateTime;
        this.radOffset = from.radOffset;
        this.toCircle = from.toCircle;
        this.toCircleProgress = from.toCircleProgress;
        this.noProgress = from.noProgress;
        this.currentCircleLength = from.currentCircleLength;
        this.drawingCircleLenght = from.drawingCircleLenght;
        this.currentProgressTime = from.currentProgressTime;
        this.currentProgress = from.currentProgress;
        this.progressTime = from.progressTime;
        this.animatedProgress = from.animatedProgress;
        this.risingCircleLength = from.risingCircleLength;
        this.progressAnimationStart = from.progressAnimationStart;
        updateAnimation(85);
    }

    private void updateAnimation() {
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastUpdateTime;
        if (dt > 17) {
            dt = 17;
        }
        this.lastUpdateTime = newTime;
        updateAnimation(dt);
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x004c  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00f2  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateAnimation(long r10) {
        /*
            r9 = this;
            float r0 = r9.radOffset
            r1 = 360(0x168, double:1.78E-321)
            long r1 = r1 * r10
            float r1 = (float) r1
            r2 = 1157234688(0x44fa0000, float:2000.0)
            float r1 = r1 / r2
            float r0 = r0 + r1
            r9.radOffset = r0
            r1 = 1135869952(0x43b40000, float:360.0)
            float r2 = r0 / r1
            int r2 = (int) r2
            int r3 = r2 * 360
            float r3 = (float) r3
            float r0 = r0 - r3
            r9.radOffset = r0
            boolean r0 = r9.toCircle
            r3 = 1065353216(0x3var_, float:1.0)
            r4 = 0
            if (r0 == 0) goto L_0x0032
            float r5 = r9.toCircleProgress
            int r6 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r6 == 0) goto L_0x0032
            r0 = 1033171465(0x3d94var_, float:0.07272727)
            float r5 = r5 + r0
            r9.toCircleProgress = r5
            int r0 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r0 <= 0) goto L_0x0046
            r9.toCircleProgress = r3
            goto L_0x0046
        L_0x0032:
            if (r0 != 0) goto L_0x0046
            float r0 = r9.toCircleProgress
            int r5 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r5 == 0) goto L_0x0046
            r5 = 1025758986(0x3d23d70a, float:0.04)
            float r0 = r0 - r5
            r9.toCircleProgress = r0
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 >= 0) goto L_0x0046
            r9.toCircleProgress = r4
        L_0x0046:
            boolean r0 = r9.noProgress
            r5 = 1082130432(0x40800000, float:4.0)
            if (r0 == 0) goto L_0x00f2
            float r0 = r9.toCircleProgress
            r6 = 1132789760(0x43850000, float:266.0)
            r7 = 1132920832(0x43870000, float:270.0)
            r8 = 1140457472(0x43fa0000, float:500.0)
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x00a1
            float r0 = r9.currentProgressTime
            float r1 = (float) r10
            float r0 = r0 + r1
            r9.currentProgressTime = r0
            int r0 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
            if (r0 < 0) goto L_0x0064
            r9.currentProgressTime = r8
        L_0x0064:
            boolean r0 = r9.risingCircleLength
            if (r0 == 0) goto L_0x0077
            android.view.animation.AccelerateInterpolator r0 = r9.accelerateInterpolator
            float r1 = r9.currentProgressTime
            float r1 = r1 / r8
            float r0 = r0.getInterpolation(r1)
            float r0 = r0 * r6
            float r0 = r0 + r5
            r9.currentCircleLength = r0
            goto L_0x0086
        L_0x0077:
            android.view.animation.DecelerateInterpolator r0 = r9.decelerateInterpolator
            float r1 = r9.currentProgressTime
            float r1 = r1 / r8
            float r0 = r0.getInterpolation(r1)
            float r3 = r3 - r0
            float r3 = r3 * r7
            float r5 = r5 - r3
            r9.currentCircleLength = r5
        L_0x0086:
            float r0 = r9.currentProgressTime
            int r0 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
            if (r0 != 0) goto L_0x012b
            boolean r0 = r9.risingCircleLength
            if (r0 == 0) goto L_0x0099
            float r1 = r9.radOffset
            float r1 = r1 + r7
            r9.radOffset = r1
            r1 = -1014693888(0xffffffffCLASSNAME, float:-266.0)
            r9.currentCircleLength = r1
        L_0x0099:
            r0 = r0 ^ 1
            r9.risingCircleLength = r0
            r9.currentProgressTime = r4
            goto L_0x012b
        L_0x00a1:
            boolean r0 = r9.risingCircleLength
            if (r0 == 0) goto L_0x00ca
            float r0 = r9.currentCircleLength
            android.view.animation.AccelerateInterpolator r3 = r9.accelerateInterpolator
            float r7 = r9.currentProgressTime
            float r7 = r7 / r8
            float r3 = r3.getInterpolation(r7)
            float r3 = r3 * r6
            float r3 = r3 + r5
            r9.currentCircleLength = r3
            float r5 = r9.toCircleProgress
            float r5 = r5 * r1
            float r3 = r3 + r5
            r9.currentCircleLength = r3
            float r1 = r0 - r3
            int r4 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r4 <= 0) goto L_0x00c9
            float r4 = r9.radOffset
            float r3 = r0 - r3
            float r4 = r4 + r3
            r9.radOffset = r4
        L_0x00c9:
            goto L_0x012b
        L_0x00ca:
            float r0 = r9.currentCircleLength
            android.view.animation.DecelerateInterpolator r1 = r9.decelerateInterpolator
            float r6 = r9.currentProgressTime
            float r6 = r6 / r8
            float r1 = r1.getInterpolation(r6)
            float r3 = r3 - r1
            float r3 = r3 * r7
            float r5 = r5 - r3
            r9.currentCircleLength = r5
            r1 = 1136001024(0x43b60000, float:364.0)
            float r3 = r9.toCircleProgress
            float r3 = r3 * r1
            float r5 = r5 - r3
            r9.currentCircleLength = r5
            float r1 = r0 - r5
            int r3 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r3 <= 0) goto L_0x00f1
            float r3 = r9.radOffset
            float r4 = r0 - r5
            float r3 = r3 + r4
            r9.radOffset = r3
        L_0x00f1:
            goto L_0x012b
        L_0x00f2:
            float r0 = r9.currentProgress
            float r3 = r9.progressAnimationStart
            float r6 = r0 - r3
            int r4 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r4 <= 0) goto L_0x0121
            int r4 = r9.progressTime
            long r7 = (long) r4
            long r7 = r7 + r10
            int r4 = (int) r7
            r9.progressTime = r4
            float r4 = (float) r4
            r7 = 1128792064(0x43480000, float:200.0)
            int r4 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r4 < 0) goto L_0x0112
            r9.progressAnimationStart = r0
            r9.animatedProgress = r0
            r0 = 0
            r9.progressTime = r0
            goto L_0x0121
        L_0x0112:
            android.view.animation.DecelerateInterpolator r0 = org.telegram.messenger.AndroidUtilities.decelerateInterpolator
            int r4 = r9.progressTime
            float r4 = (float) r4
            float r4 = r4 / r7
            float r0 = r0.getInterpolation(r4)
            float r0 = r0 * r6
            float r3 = r3 + r0
            r9.animatedProgress = r3
        L_0x0121:
            float r0 = r9.animatedProgress
            float r0 = r0 * r1
            float r0 = java.lang.Math.max(r5, r0)
            r9.currentCircleLength = r0
        L_0x012b:
            r9.invalidate()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RadialProgressView.updateAnimation(long):void");
    }

    public void setSize(int value) {
        this.size = value;
        invalidate();
    }

    public void setStrokeWidth(float value) {
        this.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(value));
    }

    public void setProgressColor(int color) {
        this.progressColor = color;
        this.progressPaint.setColor(color);
    }

    public void toCircle(boolean toCircle2, boolean animated) {
        this.toCircle = toCircle2;
        if (!animated) {
            this.toCircleProgress = toCircle2 ? 1.0f : 0.0f;
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int x = (getMeasuredWidth() - this.size) / 2;
        int measuredHeight = getMeasuredHeight();
        int i = this.size;
        int y = (measuredHeight - i) / 2;
        this.cicleRect.set((float) x, (float) y, (float) (x + i), (float) (i + y));
        RectF rectF = this.cicleRect;
        float f = this.radOffset;
        float f2 = this.currentCircleLength;
        this.drawingCircleLenght = f2;
        canvas.drawArc(rectF, f, f2, false, this.progressPaint);
        updateAnimation();
    }

    public void draw(Canvas canvas, float cx, float cy) {
        RectF rectF = this.cicleRect;
        int i = this.size;
        rectF.set(cx - (((float) i) / 2.0f), cy - (((float) i) / 2.0f), (((float) i) / 2.0f) + cx, (((float) i) / 2.0f) + cy);
        RectF rectF2 = this.cicleRect;
        float f = this.radOffset;
        float f2 = this.currentCircleLength;
        this.drawingCircleLenght = f2;
        canvas.drawArc(rectF2, f, f2, false, this.progressPaint);
        updateAnimation();
    }

    public boolean isCircle() {
        return Math.abs(this.drawingCircleLenght) >= 360.0f;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
