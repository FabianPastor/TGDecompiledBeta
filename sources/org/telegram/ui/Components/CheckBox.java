package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.Keep;
import org.telegram.messenger.AndroidUtilities;

public class CheckBox extends View {
    private static Paint backgroundPaint = null;
    private static Paint checkPaint = null;
    private static Paint eraser = null;
    private static Paint eraser2 = null;
    private static Paint paint = null;
    private static final float progressBounceDiff = 0.2f;
    private boolean attachedToWindow;
    private Canvas bitmapCanvas;
    private ObjectAnimator checkAnimator;
    private Bitmap checkBitmap;
    private Canvas checkCanvas;
    private Drawable checkDrawable;
    private int checkOffset;
    private String checkedText;
    private int color;
    private boolean drawBackground;
    private Bitmap drawBitmap;
    private boolean hasBorder;
    private boolean isCheckAnimation = true;
    private boolean isChecked;
    private float progress;
    private int size = 22;
    private TextPaint textPaint;

    public CheckBox(Context context, int i) {
        super(context);
        if (paint == null) {
            paint = new Paint(1);
            eraser = new Paint(1);
            eraser.setColor(0);
            eraser.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            eraser2 = new Paint(1);
            eraser2.setColor(0);
            eraser2.setStyle(Style.STROKE);
            eraser2.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            backgroundPaint = new Paint(1);
            backgroundPaint.setColor(-1);
            backgroundPaint.setStyle(Style.STROKE);
        }
        eraser2.setStrokeWidth((float) AndroidUtilities.dp(28.0f));
        backgroundPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.textPaint = new TextPaint(1);
        this.textPaint.setTextSize((float) AndroidUtilities.dp(18.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.checkDrawable = context.getResources().getDrawable(i).mutate();
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i == 0 && this.drawBitmap == null) {
            try {
                this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp((float) this.size), AndroidUtilities.dp((float) this.size), Config.ARGB_4444);
                this.bitmapCanvas = new Canvas(this.drawBitmap);
                this.checkBitmap = Bitmap.createBitmap(AndroidUtilities.dp((float) this.size), AndroidUtilities.dp((float) this.size), Config.ARGB_4444);
                this.checkCanvas = new Canvas(this.checkBitmap);
            } catch (Throwable unused) {
            }
        }
    }

    @Keep
    public void setProgress(float f) {
        if (this.progress != f) {
            this.progress = f;
            invalidate();
        }
    }

    public void setDrawBackground(boolean z) {
        this.drawBackground = z;
    }

    public void setHasBorder(boolean z) {
        this.hasBorder = z;
    }

    public void setCheckOffset(int i) {
        this.checkOffset = i;
    }

    public void setSize(int i) {
        this.size = i;
        if (i == 40) {
            this.textPaint.setTextSize((float) AndroidUtilities.dp(24.0f));
        }
    }

    public float getProgress() {
        return this.progress;
    }

    public void setColor(int i, int i2) {
        this.color = i;
        this.checkDrawable.setColorFilter(new PorterDuffColorFilter(i2, Mode.MULTIPLY));
        this.textPaint.setColor(i2);
        invalidate();
    }

    public void setBackgroundColor(int i) {
        this.color = i;
        invalidate();
    }

    public void setCheckColor(int i) {
        this.checkDrawable.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
        this.textPaint.setColor(i);
        invalidate();
    }

    private void cancelCheckAnimator() {
        ObjectAnimator objectAnimator = this.checkAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.checkAnimator = null;
        }
    }

    private void animateToCheckedState(boolean z) {
        this.isCheckAnimation = z;
        float[] fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.0f;
        this.checkAnimator = ObjectAnimator.ofFloat(this, "progress", fArr);
        this.checkAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (animator.equals(CheckBox.this.checkAnimator)) {
                    CheckBox.this.checkAnimator = null;
                }
                if (!CheckBox.this.isChecked) {
                    CheckBox.this.checkedText = null;
                }
            }
        });
        this.checkAnimator.setDuration(300);
        this.checkAnimator.start();
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = true;
    }

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
    }

    public void setChecked(boolean z, boolean z2) {
        setChecked(-1, z, z2);
    }

    public void setNum(int i) {
        if (i >= 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(i + 1);
            this.checkedText = stringBuilder.toString();
        } else if (this.checkAnimator == null) {
            this.checkedText = null;
        }
        invalidate();
    }

    public void setChecked(int i, boolean z, boolean z2) {
        if (i >= 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(i + 1);
            this.checkedText = stringBuilder.toString();
            invalidate();
        }
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

    public boolean isChecked() {
        return this.isChecked;
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0089  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00cf  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0140  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0112  */
    public void onDraw(android.graphics.Canvas r12) {
        /*
        r11 = this;
        r0 = r11.getVisibility();
        if (r0 != 0) goto L_0x01a6;
    L_0x0006:
        r0 = r11.drawBitmap;
        if (r0 == 0) goto L_0x01a6;
    L_0x000a:
        r0 = r11.checkBitmap;
        if (r0 != 0) goto L_0x0010;
    L_0x000e:
        goto L_0x01a6;
    L_0x0010:
        r0 = r11.drawBackground;
        r1 = 0;
        if (r0 != 0) goto L_0x001b;
    L_0x0015:
        r0 = r11.progress;
        r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1));
        if (r0 == 0) goto L_0x01a6;
    L_0x001b:
        r0 = eraser2;
        r2 = r11.size;
        r2 = r2 + 6;
        r2 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r0.setStrokeWidth(r2);
        r0 = r11.drawBitmap;
        r2 = 0;
        r0.eraseColor(r2);
        r0 = r11.getMeasuredWidth();
        r0 = r0 / 2;
        r0 = (float) r0;
        r3 = r11.progress;
        r4 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
        r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1));
        if (r6 < 0) goto L_0x0044;
    L_0x0041:
        r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0045;
    L_0x0044:
        r3 = r3 / r4;
    L_0x0045:
        r6 = r11.progress;
        r7 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1));
        if (r7 >= 0) goto L_0x004d;
    L_0x004b:
        r4 = 0;
        goto L_0x0050;
    L_0x004d:
        r6 = r6 - r4;
        r4 = r6 / r4;
    L_0x0050:
        r6 = r11.isCheckAnimation;
        if (r6 == 0) goto L_0x0057;
    L_0x0054:
        r6 = r11.progress;
        goto L_0x005b;
    L_0x0057:
        r6 = r11.progress;
        r6 = r5 - r6;
    L_0x005b:
        r7 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r8 = NUM; // 0x3e4ccccd float:0.2 double:5.164075695E-315;
        r9 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r9 >= 0) goto L_0x006e;
    L_0x0064:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r9 = (float) r9;
        r9 = r9 * r6;
        r9 = r9 / r8;
    L_0x006c:
        r0 = r0 - r9;
        goto L_0x0085;
    L_0x006e:
        r9 = NUM; // 0x3ecccccd float:0.4 double:5.205520926E-315;
        r9 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1));
        if (r9 >= 0) goto L_0x0085;
    L_0x0075:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r9 = (float) r9;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r10 = (float) r10;
        r6 = r6 - r8;
        r10 = r10 * r6;
        r10 = r10 / r8;
        r9 = r9 - r10;
        goto L_0x006c;
    L_0x0085:
        r6 = r11.drawBackground;
        if (r6 == 0) goto L_0x00c4;
    L_0x0089:
        r6 = paint;
        r8 = NUM; // 0x44000000 float:512.0 double:5.63655132E-315;
        r6.setColor(r8);
        r6 = r11.getMeasuredWidth();
        r6 = r6 / 2;
        r6 = (float) r6;
        r8 = r11.getMeasuredHeight();
        r8 = r8 / 2;
        r8 = (float) r8;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r9 = (float) r9;
        r9 = r0 - r9;
        r10 = paint;
        r12.drawCircle(r6, r8, r9, r10);
        r6 = r11.getMeasuredWidth();
        r6 = r6 / 2;
        r6 = (float) r6;
        r8 = r11.getMeasuredHeight();
        r8 = r8 / 2;
        r8 = (float) r8;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r9 = (float) r9;
        r9 = r0 - r9;
        r10 = backgroundPaint;
        r12.drawCircle(r6, r8, r9, r10);
    L_0x00c4:
        r6 = paint;
        r8 = r11.color;
        r6.setColor(r8);
        r6 = r11.hasBorder;
        if (r6 == 0) goto L_0x00d5;
    L_0x00cf:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r6 = (float) r6;
        r0 = r0 - r6;
    L_0x00d5:
        r6 = r11.bitmapCanvas;
        r7 = r11.getMeasuredWidth();
        r7 = r7 / 2;
        r7 = (float) r7;
        r8 = r11.getMeasuredHeight();
        r8 = r8 / 2;
        r8 = (float) r8;
        r9 = paint;
        r6.drawCircle(r7, r8, r0, r9);
        r6 = r11.bitmapCanvas;
        r7 = r11.getMeasuredWidth();
        r7 = r7 / 2;
        r7 = (float) r7;
        r8 = r11.getMeasuredHeight();
        r8 = r8 / 2;
        r8 = (float) r8;
        r3 = r5 - r3;
        r0 = r0 * r3;
        r3 = eraser;
        r6.drawCircle(r7, r8, r0, r3);
        r0 = r11.drawBitmap;
        r3 = 0;
        r12.drawBitmap(r0, r1, r1, r3);
        r0 = r11.checkBitmap;
        r0.eraseColor(r2);
        r0 = r11.checkedText;
        if (r0 == 0) goto L_0x0140;
    L_0x0112:
        r2 = r11.textPaint;
        r0 = r2.measureText(r0);
        r6 = (double) r0;
        r6 = java.lang.Math.ceil(r6);
        r0 = (int) r6;
        r2 = r11.checkCanvas;
        r6 = r11.checkedText;
        r7 = r11.getMeasuredWidth();
        r7 = r7 - r0;
        r7 = r7 / 2;
        r0 = (float) r7;
        r7 = r11.size;
        r8 = 40;
        if (r7 != r8) goto L_0x0133;
    L_0x0130:
        r7 = NUM; // 0x41e00000 float:28.0 double:5.46040909E-315;
        goto L_0x0135;
    L_0x0133:
        r7 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
    L_0x0135:
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r7 = (float) r7;
        r8 = r11.textPaint;
        r2.drawText(r6, r0, r7, r8);
        goto L_0x016d;
    L_0x0140:
        r0 = r11.checkDrawable;
        r0 = r0.getIntrinsicWidth();
        r2 = r11.checkDrawable;
        r2 = r2.getIntrinsicHeight();
        r6 = r11.getMeasuredWidth();
        r6 = r6 - r0;
        r6 = r6 / 2;
        r7 = r11.getMeasuredHeight();
        r7 = r7 - r2;
        r7 = r7 / 2;
        r8 = r11.checkDrawable;
        r9 = r11.checkOffset;
        r10 = r7 + r9;
        r0 = r0 + r6;
        r7 = r7 + r2;
        r7 = r7 + r9;
        r8.setBounds(r6, r10, r0, r7);
        r0 = r11.checkDrawable;
        r2 = r11.checkCanvas;
        r0.draw(r2);
    L_0x016d:
        r0 = r11.checkCanvas;
        r2 = r11.getMeasuredWidth();
        r2 = r2 / 2;
        r6 = NUM; // 0x40200000 float:2.5 double:5.315350785E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r2 = r2 - r6;
        r2 = (float) r2;
        r6 = r11.getMeasuredHeight();
        r6 = r6 / 2;
        r7 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r6 = r6 + r7;
        r6 = (float) r6;
        r7 = r11.getMeasuredWidth();
        r8 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r7 = r7 + r8;
        r7 = r7 / 2;
        r7 = (float) r7;
        r5 = r5 - r4;
        r7 = r7 * r5;
        r4 = eraser2;
        r0.drawCircle(r2, r6, r7, r4);
        r0 = r11.checkBitmap;
        r12.drawBitmap(r0, r1, r1, r3);
    L_0x01a6:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.CheckBox.onDraw(android.graphics.Canvas):void");
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName("android.widget.CheckBox");
        accessibilityNodeInfo.setCheckable(true);
        accessibilityNodeInfo.setChecked(this.isChecked);
    }
}
