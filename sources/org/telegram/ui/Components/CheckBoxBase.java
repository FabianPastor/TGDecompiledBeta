package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import androidx.annotation.Keep;
import org.telegram.messenger.AndroidUtilities;

public class CheckBoxBase {
    private static Paint backgroundPaint;
    private static Paint checkPaint;
    private static Paint eraser;
    private static Paint paint;
    private boolean attachedToWindow;
    private String background2ColorKey;
    private float backgroundAlpha = 1.0f;
    private String backgroundColorKey;
    private Canvas bitmapCanvas;
    private Rect bounds = new Rect();
    private ObjectAnimator checkAnimator;
    private String checkColorKey = "checkboxCheck";
    private int drawBackgroundAsArc;
    private Bitmap drawBitmap;
    private boolean drawUnchecked;
    private boolean isChecked;
    private View parentView;
    private Path path = new Path();
    private float progress;
    private ProgressDelegate progressDelegate;
    private RectF rect = new RectF();
    private float size;

    public interface ProgressDelegate {
        void setProgress(float f);
    }

    public CheckBoxBase(View view) {
        String str = "chat_serviceBackground";
        this.backgroundColorKey = str;
        this.background2ColorKey = str;
        this.drawUnchecked = true;
        this.size = 21.0f;
        this.parentView = view;
        if (paint == null) {
            paint = new Paint(1);
            eraser = new Paint(1);
            eraser.setColor(0);
            eraser.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            backgroundPaint = new Paint(1);
            backgroundPaint.setStyle(Style.STROKE);
            checkPaint = new Paint(1);
            checkPaint.setStrokeCap(Cap.ROUND);
            checkPaint.setStyle(Style.STROKE);
            checkPaint.setStrokeJoin(Join.ROUND);
        }
        checkPaint.setStrokeWidth((float) AndroidUtilities.dp(1.9f));
        backgroundPaint.setStrokeWidth((float) AndroidUtilities.dp(1.2f));
        this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(this.size), AndroidUtilities.dp(this.size), Config.ARGB_4444);
        this.bitmapCanvas = new Canvas(this.drawBitmap);
    }

    public void onAttachedToWindow() {
        this.attachedToWindow = true;
    }

    public void onDetachedFromWindow() {
        this.attachedToWindow = false;
    }

    public void setBounds(int i, int i2, int i3, int i4) {
        Rect rect = this.bounds;
        rect.left = i;
        rect.top = i2;
        rect.right = i + i3;
        rect.bottom = i2 + i4;
    }

    public void setDrawUnchecked(boolean z) {
        this.drawUnchecked = z;
    }

    @Keep
    public void setProgress(float f) {
        if (this.progress != f) {
            this.progress = f;
            if (this.parentView.getParent() != null) {
                ((View) this.parentView.getParent()).invalidate();
            }
            this.parentView.invalidate();
            ProgressDelegate progressDelegate = this.progressDelegate;
            if (progressDelegate != null) {
                progressDelegate.setProgress(f);
            }
        }
    }

    public void setProgressDelegate(ProgressDelegate progressDelegate) {
        this.progressDelegate = progressDelegate;
    }

    public void setSize(int i) {
        this.size = (float) i;
    }

    public float getProgress() {
        return this.progress;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setDrawBackgroundAsArc(int i) {
        this.drawBackgroundAsArc = i;
        if (i == 4) {
            backgroundPaint.setStrokeWidth((float) AndroidUtilities.dp(1.9f));
        } else if (i == 3) {
            backgroundPaint.setStrokeWidth((float) AndroidUtilities.dp(1.2f));
        } else if (i != 0) {
            backgroundPaint.setStrokeWidth((float) AndroidUtilities.dp(1.5f));
        }
    }

    private void cancelCheckAnimator() {
        ObjectAnimator objectAnimator = this.checkAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.checkAnimator = null;
        }
    }

    private void animateToCheckedState(boolean z) {
        float[] fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.0f;
        this.checkAnimator = ObjectAnimator.ofFloat(this, "progress", fArr);
        this.checkAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (animator.equals(CheckBoxBase.this.checkAnimator)) {
                    CheckBoxBase.this.checkAnimator = null;
                }
            }
        });
        this.checkAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.checkAnimator.setDuration(200);
        this.checkAnimator.start();
    }

    public void setColor(String str, String str2, String str3) {
        this.backgroundColorKey = str;
        this.background2ColorKey = str2;
        this.checkColorKey = str3;
    }

    public void setBackgroundAlpha(float f) {
        this.backgroundAlpha = f;
    }

    public void setChecked(boolean z, boolean z2) {
        if (z != this.isChecked) {
            this.isChecked = z;
            if (this.attachedToWindow && z2) {
                animateToCheckedState(z);
            } else {
                cancelCheckAnimator();
                setProgress(z ? 1.0f : 0.0f);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:53:0x014f  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0145  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x01b3  */
    public void draw(android.graphics.Canvas r17) {
        /*
        r16 = this;
        r0 = r16;
        r7 = r17;
        r1 = r0.drawBitmap;
        if (r1 != 0) goto L_0x0009;
    L_0x0008:
        return;
    L_0x0009:
        r2 = 0;
        r1.eraseColor(r2);
        r1 = r0.size;
        r8 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r1 = r1 / r8;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r9 = (float) r1;
        r1 = r0.drawBackgroundAsArc;
        if (r1 == 0) goto L_0x0026;
    L_0x001b:
        r1 = NUM; // 0x3e4ccccd float:0.2 double:5.164075695E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = (float) r1;
        r1 = r9 - r1;
        goto L_0x0027;
    L_0x0026:
        r1 = r9;
    L_0x0027:
        r3 = r0.progress;
        r10 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r11 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
        r4 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1));
        if (r4 < 0) goto L_0x0034;
    L_0x0031:
        r12 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0036;
    L_0x0034:
        r3 = r3 / r11;
        r12 = r3;
    L_0x0036:
        r3 = r0.bounds;
        r13 = r3.centerX();
        r3 = r0.bounds;
        r14 = r3.centerY();
        r3 = r0.backgroundColorKey;
        r4 = 16777215; // 0xffffff float:2.3509886E-38 double:8.2890456E-317;
        if (r3 == 0) goto L_0x007f;
    L_0x0049:
        r2 = r0.drawUnchecked;
        if (r2 == 0) goto L_0x0066;
    L_0x004d:
        r2 = paint;
        r3 = org.telegram.ui.ActionBar.Theme.getServiceMessageColor();
        r3 = r3 & r4;
        r4 = NUM; // 0x28000000 float:7.1054274E-15 double:3.315618423E-315;
        r3 = r3 | r4;
        r2.setColor(r3);
        r2 = backgroundPaint;
        r3 = r0.checkColorKey;
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setColor(r3);
        goto L_0x00c0;
    L_0x0066:
        r2 = backgroundPaint;
        r3 = r0.background2ColorKey;
        if (r3 == 0) goto L_0x006d;
    L_0x006c:
        goto L_0x006f;
    L_0x006d:
        r3 = r0.checkColorKey;
    L_0x006f:
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r5 = r0.progress;
        r6 = r0.backgroundAlpha;
        r3 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r4, r3, r5, r6);
        r2.setColor(r3);
        goto L_0x00c0;
    L_0x007f:
        r3 = r0.drawUnchecked;
        if (r3 == 0) goto L_0x00a8;
    L_0x0083:
        r3 = paint;
        r4 = NUM; // 0x41CLASSNAME float:25.0 double:5.45263811E-315;
        r5 = r0.backgroundAlpha;
        r5 = r5 * r4;
        r4 = (int) r5;
        r2 = android.graphics.Color.argb(r4, r2, r2, r2);
        r3.setColor(r2);
        r2 = backgroundPaint;
        r3 = -1;
        r4 = r0.checkColorKey;
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r5 = r0.progress;
        r6 = r0.backgroundAlpha;
        r3 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r3, r4, r5, r6);
        r2.setColor(r3);
        goto L_0x00c0;
    L_0x00a8:
        r2 = backgroundPaint;
        r3 = r0.background2ColorKey;
        if (r3 == 0) goto L_0x00af;
    L_0x00ae:
        goto L_0x00b1;
    L_0x00af:
        r3 = r0.checkColorKey;
    L_0x00b1:
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r5 = r0.progress;
        r6 = r0.backgroundAlpha;
        r3 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r4, r3, r5, r6);
        r2.setColor(r3);
    L_0x00c0:
        r2 = r0.drawUnchecked;
        if (r2 == 0) goto L_0x00cb;
    L_0x00c4:
        r2 = (float) r13;
        r3 = (float) r14;
        r4 = paint;
        r7.drawCircle(r2, r3, r9, r4);
    L_0x00cb:
        r2 = paint;
        r3 = r0.checkColorKey;
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setColor(r3);
        r2 = r0.drawBackgroundAsArc;
        if (r2 != 0) goto L_0x00e2;
    L_0x00da:
        r1 = (float) r13;
        r2 = (float) r14;
        r3 = backgroundPaint;
        r7.drawCircle(r1, r2, r9, r3);
        goto L_0x0115;
    L_0x00e2:
        r2 = r0.rect;
        r3 = (float) r13;
        r4 = r3 - r1;
        r5 = (float) r14;
        r6 = r5 - r1;
        r3 = r3 + r1;
        r5 = r5 + r1;
        r2.set(r4, r6, r3, r5);
        r1 = r0.drawBackgroundAsArc;
        r2 = 1;
        if (r1 != r2) goto L_0x00fb;
    L_0x00f4:
        r1 = -90;
        r2 = -NUM; // 0xffffffffCLASSNAME float:-270.0 double:NaN;
        r3 = r0.progress;
        goto L_0x0101;
    L_0x00fb:
        r1 = 90;
        r2 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r3 = r0.progress;
    L_0x0101:
        r3 = r3 * r2;
        r2 = (int) r3;
        r3 = r0.rect;
        r4 = (float) r1;
        r5 = (float) r2;
        r6 = 0;
        r15 = backgroundPaint;
        r1 = r17;
        r2 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r6;
        r6 = r15;
        r1.drawArc(r2, r3, r4, r5, r6);
    L_0x0115:
        r1 = 0;
        r2 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1));
        if (r2 <= 0) goto L_0x0206;
    L_0x011a:
        r2 = r0.progress;
        r3 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1));
        if (r3 >= 0) goto L_0x0122;
    L_0x0120:
        r2 = 0;
        goto L_0x0124;
    L_0x0122:
        r2 = r2 - r11;
        r2 = r2 / r11;
    L_0x0124:
        r3 = r0.drawUnchecked;
        if (r3 != 0) goto L_0x0136;
    L_0x0128:
        r3 = r0.backgroundColorKey;
        if (r3 == 0) goto L_0x0136;
    L_0x012c:
        r4 = paint;
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r4.setColor(r3);
        goto L_0x0141;
    L_0x0136:
        r3 = paint;
        r4 = "checkbox";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r3.setColor(r4);
    L_0x0141:
        r3 = r0.checkColorKey;
        if (r3 == 0) goto L_0x014f;
    L_0x0145:
        r4 = checkPaint;
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r4.setColor(r3);
        goto L_0x015a;
    L_0x014f:
        r3 = checkPaint;
        r4 = "checkboxCheck";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r3.setColor(r4);
    L_0x015a:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r3 = (float) r3;
        r9 = r9 - r3;
        r3 = r0.bitmapCanvas;
        r4 = r0.drawBitmap;
        r4 = r4.getWidth();
        r4 = r4 / 2;
        r4 = (float) r4;
        r5 = r0.drawBitmap;
        r5 = r5.getHeight();
        r5 = r5 / 2;
        r5 = (float) r5;
        r6 = paint;
        r3.drawCircle(r4, r5, r9, r6);
        r3 = r0.bitmapCanvas;
        r4 = r0.drawBitmap;
        r4 = r4.getWidth();
        r4 = r4 / 2;
        r4 = (float) r4;
        r5 = r0.drawBitmap;
        r5 = r5.getWidth();
        r5 = r5 / 2;
        r5 = (float) r5;
        r10 = r10 - r12;
        r9 = r9 * r10;
        r6 = eraser;
        r3.drawCircle(r4, r5, r9, r6);
        r3 = r0.drawBitmap;
        r4 = r3.getWidth();
        r4 = r4 / 2;
        r4 = r13 - r4;
        r4 = (float) r4;
        r5 = r0.drawBitmap;
        r5 = r5.getHeight();
        r5 = r5 / 2;
        r5 = r14 - r5;
        r5 = (float) r5;
        r6 = 0;
        r7.drawBitmap(r3, r4, r5, r6);
        r1 = (r2 > r1 ? 1 : (r2 == r1 ? 0 : -1));
        if (r1 == 0) goto L_0x0206;
    L_0x01b3:
        r1 = r0.path;
        r1.reset();
        r1 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = (float) r1;
        r1 = r1 * r2;
        r3 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = (float) r4;
        r4 = r4 * r2;
        r2 = NUM; // 0x3fCLASSNAME float:1.5 double:5.28426686E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r13 = r13 - r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r14 = r14 + r2;
        r4 = r4 * r4;
        r4 = r4 / r8;
        r2 = (double) r4;
        r2 = java.lang.Math.sqrt(r2);
        r2 = (float) r2;
        r3 = r0.path;
        r4 = (float) r13;
        r5 = r4 - r2;
        r6 = (float) r14;
        r2 = r6 - r2;
        r3.moveTo(r5, r2);
        r2 = r0.path;
        r2.lineTo(r4, r6);
        r1 = r1 * r1;
        r1 = r1 / r8;
        r1 = (double) r1;
        r1 = java.lang.Math.sqrt(r1);
        r1 = (float) r1;
        r2 = r0.path;
        r4 = r4 + r1;
        r6 = r6 - r1;
        r2.lineTo(r4, r6);
        r1 = r0.path;
        r2 = checkPaint;
        r7.drawPath(r1, r2);
    L_0x0206:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.CheckBoxBase.draw(android.graphics.Canvas):void");
    }
}
