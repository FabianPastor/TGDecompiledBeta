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

    /* JADX WARNING: Removed duplicated region for block: B:18:0x005e  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0104  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateAnimation() {
        /*
            r13 = this;
            long r0 = java.lang.System.currentTimeMillis()
            long r2 = r13.lastUpdateTime
            long r2 = r0 - r2
            r4 = 17
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 <= 0) goto L_0x0010
            r2 = 17
        L_0x0010:
            r13.lastUpdateTime = r0
            float r4 = r13.radOffset
            r5 = 360(0x168, double:1.78E-321)
            long r5 = r5 * r2
            float r5 = (float) r5
            r6 = 1157234688(0x44fa0000, float:2000.0)
            float r5 = r5 / r6
            float r4 = r4 + r5
            r13.radOffset = r4
            r5 = 1135869952(0x43b40000, float:360.0)
            float r6 = r4 / r5
            int r6 = (int) r6
            int r7 = r6 * 360
            float r7 = (float) r7
            float r4 = r4 - r7
            r13.radOffset = r4
            boolean r4 = r13.toCircle
            r7 = 1065353216(0x3var_, float:1.0)
            r8 = 0
            if (r4 == 0) goto L_0x0044
            float r9 = r13.toCircleProgress
            int r10 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
            if (r10 == 0) goto L_0x0044
            r4 = 1033171465(0x3d94var_, float:0.07272727)
            float r9 = r9 + r4
            r13.toCircleProgress = r9
            int r4 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
            if (r4 <= 0) goto L_0x0058
            r13.toCircleProgress = r7
            goto L_0x0058
        L_0x0044:
            if (r4 != 0) goto L_0x0058
            float r4 = r13.toCircleProgress
            int r9 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r9 == 0) goto L_0x0058
            r9 = 1025758986(0x3d23d70a, float:0.04)
            float r4 = r4 - r9
            r13.toCircleProgress = r4
            int r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r4 >= 0) goto L_0x0058
            r13.toCircleProgress = r8
        L_0x0058:
            boolean r4 = r13.noProgress
            r9 = 1082130432(0x40800000, float:4.0)
            if (r4 == 0) goto L_0x0104
            float r4 = r13.toCircleProgress
            r10 = 1132789760(0x43850000, float:266.0)
            r11 = 1132920832(0x43870000, float:270.0)
            r12 = 1140457472(0x43fa0000, float:500.0)
            int r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r4 != 0) goto L_0x00b3
            float r4 = r13.currentProgressTime
            float r5 = (float) r2
            float r4 = r4 + r5
            r13.currentProgressTime = r4
            int r4 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1))
            if (r4 < 0) goto L_0x0076
            r13.currentProgressTime = r12
        L_0x0076:
            boolean r4 = r13.risingCircleLength
            if (r4 == 0) goto L_0x0089
            android.view.animation.AccelerateInterpolator r4 = r13.accelerateInterpolator
            float r5 = r13.currentProgressTime
            float r5 = r5 / r12
            float r4 = r4.getInterpolation(r5)
            float r4 = r4 * r10
            float r4 = r4 + r9
            r13.currentCircleLength = r4
            goto L_0x0098
        L_0x0089:
            android.view.animation.DecelerateInterpolator r4 = r13.decelerateInterpolator
            float r5 = r13.currentProgressTime
            float r5 = r5 / r12
            float r4 = r4.getInterpolation(r5)
            float r7 = r7 - r4
            float r7 = r7 * r11
            float r9 = r9 - r7
            r13.currentCircleLength = r9
        L_0x0098:
            float r4 = r13.currentProgressTime
            int r4 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1))
            if (r4 != 0) goto L_0x013d
            boolean r4 = r13.risingCircleLength
            if (r4 == 0) goto L_0x00ab
            float r5 = r13.radOffset
            float r5 = r5 + r11
            r13.radOffset = r5
            r5 = -1014693888(0xffffffffCLASSNAME, float:-266.0)
            r13.currentCircleLength = r5
        L_0x00ab:
            r4 = r4 ^ 1
            r13.risingCircleLength = r4
            r13.currentProgressTime = r8
            goto L_0x013d
        L_0x00b3:
            boolean r4 = r13.risingCircleLength
            if (r4 == 0) goto L_0x00dc
            float r4 = r13.currentCircleLength
            android.view.animation.AccelerateInterpolator r7 = r13.accelerateInterpolator
            float r11 = r13.currentProgressTime
            float r11 = r11 / r12
            float r7 = r7.getInterpolation(r11)
            float r7 = r7 * r10
            float r7 = r7 + r9
            r13.currentCircleLength = r7
            float r9 = r13.toCircleProgress
            float r9 = r9 * r5
            float r7 = r7 + r9
            r13.currentCircleLength = r7
            float r5 = r4 - r7
            int r8 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r8 <= 0) goto L_0x00db
            float r8 = r13.radOffset
            float r7 = r4 - r7
            float r8 = r8 + r7
            r13.radOffset = r8
        L_0x00db:
            goto L_0x013d
        L_0x00dc:
            float r4 = r13.currentCircleLength
            android.view.animation.DecelerateInterpolator r5 = r13.decelerateInterpolator
            float r10 = r13.currentProgressTime
            float r10 = r10 / r12
            float r5 = r5.getInterpolation(r10)
            float r7 = r7 - r5
            float r7 = r7 * r11
            float r9 = r9 - r7
            r13.currentCircleLength = r9
            r5 = 1136001024(0x43b60000, float:364.0)
            float r7 = r13.toCircleProgress
            float r7 = r7 * r5
            float r9 = r9 - r7
            r13.currentCircleLength = r9
            float r5 = r4 - r9
            int r7 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r7 <= 0) goto L_0x0103
            float r7 = r13.radOffset
            float r8 = r4 - r9
            float r7 = r7 + r8
            r13.radOffset = r7
        L_0x0103:
            goto L_0x013d
        L_0x0104:
            float r4 = r13.currentProgress
            float r7 = r13.progressAnimationStart
            float r10 = r4 - r7
            int r8 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
            if (r8 <= 0) goto L_0x0133
            int r8 = r13.progressTime
            long r11 = (long) r8
            long r11 = r11 + r2
            int r8 = (int) r11
            r13.progressTime = r8
            float r8 = (float) r8
            r11 = 1128792064(0x43480000, float:200.0)
            int r8 = (r8 > r11 ? 1 : (r8 == r11 ? 0 : -1))
            if (r8 < 0) goto L_0x0124
            r13.progressAnimationStart = r4
            r13.animatedProgress = r4
            r4 = 0
            r13.progressTime = r4
            goto L_0x0133
        L_0x0124:
            android.view.animation.DecelerateInterpolator r4 = org.telegram.messenger.AndroidUtilities.decelerateInterpolator
            int r8 = r13.progressTime
            float r8 = (float) r8
            float r8 = r8 / r11
            float r4 = r4.getInterpolation(r8)
            float r4 = r4 * r10
            float r7 = r7 + r4
            r13.animatedProgress = r7
        L_0x0133:
            float r4 = r13.animatedProgress
            float r4 = r4 * r5
            float r4 = java.lang.Math.max(r9, r4)
            r13.currentCircleLength = r4
        L_0x013d:
            r13.invalidate()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RadialProgressView.updateAnimation():void");
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
