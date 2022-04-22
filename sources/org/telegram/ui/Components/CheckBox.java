package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.Keep;
import org.telegram.messenger.AndroidUtilities;

public class CheckBox extends View {
    private static Paint backgroundPaint;
    private static Paint eraser;
    private static Paint eraser2;
    private static Paint paint;
    private boolean attachedToWindow;
    private Canvas bitmapCanvas;
    /* access modifiers changed from: private */
    public ObjectAnimator checkAnimator;
    private Bitmap checkBitmap;
    private Canvas checkCanvas;
    private Drawable checkDrawable;
    private int checkOffset;
    /* access modifiers changed from: private */
    public String checkedText;
    private int color;
    private boolean drawBackground;
    private Bitmap drawBitmap;
    private boolean hasBorder;
    private boolean isCheckAnimation = true;
    /* access modifiers changed from: private */
    public boolean isChecked;
    private float progress;
    private int size = 22;
    private TextPaint textPaint;

    public CheckBox(Context context, int i) {
        super(context);
        if (paint == null) {
            paint = new Paint(1);
            Paint paint2 = new Paint(1);
            eraser = paint2;
            paint2.setColor(0);
            eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            Paint paint3 = new Paint(1);
            eraser2 = paint3;
            paint3.setColor(0);
            eraser2.setStyle(Paint.Style.STROKE);
            eraser2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            Paint paint4 = new Paint(1);
            backgroundPaint = paint4;
            paint4.setColor(-1);
            backgroundPaint.setStyle(Paint.Style.STROKE);
        }
        eraser2.setStrokeWidth((float) AndroidUtilities.dp(28.0f));
        backgroundPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        TextPaint textPaint2 = new TextPaint(1);
        this.textPaint = textPaint2;
        textPaint2.setTextSize((float) AndroidUtilities.dp(18.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.checkDrawable = context.getResources().getDrawable(i).mutate();
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i == 0 && this.drawBitmap == null) {
            try {
                this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp((float) this.size), AndroidUtilities.dp((float) this.size), Bitmap.Config.ARGB_4444);
                this.bitmapCanvas = new Canvas(this.drawBitmap);
                this.checkBitmap = Bitmap.createBitmap(AndroidUtilities.dp((float) this.size), AndroidUtilities.dp((float) this.size), Bitmap.Config.ARGB_4444);
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

    public void setStrokeWidth(int i) {
        backgroundPaint.setStrokeWidth((float) i);
    }

    @Keep
    public float getProgress() {
        return this.progress;
    }

    public void setColor(int i, int i2) {
        this.color = i;
        this.checkDrawable.setColorFilter(new PorterDuffColorFilter(i2, PorterDuff.Mode.MULTIPLY));
        this.textPaint.setColor(i2);
        invalidate();
    }

    public void setBackgroundColor(int i) {
        this.color = i;
        invalidate();
    }

    public void setCheckColor(int i) {
        this.checkDrawable.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
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
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "progress", fArr);
        this.checkAnimator = ofFloat;
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (animator.equals(CheckBox.this.checkAnimator)) {
                    ObjectAnimator unused = CheckBox.this.checkAnimator = null;
                }
                if (!CheckBox.this.isChecked) {
                    String unused2 = CheckBox.this.checkedText = null;
                }
            }
        });
        this.checkAnimator.setDuration(300);
        this.checkAnimator.start();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = true;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
    }

    public void setChecked(boolean z, boolean z2) {
        setChecked(-1, z, z2);
    }

    public void setNum(int i) {
        if (i >= 0) {
            this.checkedText = "" + (i + 1);
        } else if (this.checkAnimator == null) {
            this.checkedText = null;
        }
        invalidate();
    }

    public void setChecked(int i, boolean z, boolean z2) {
        if (i >= 0) {
            this.checkedText = "" + (i + 1);
            invalidate();
        }
        if (z != this.isChecked) {
            this.isChecked = z;
            if (!this.attachedToWindow || !z2) {
                cancelCheckAnimator();
                setProgress(z ? 1.0f : 0.0f);
                return;
            }
            animateToCheckedState(z);
        }
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0084  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00ca  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x010d  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x013b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r12) {
        /*
            r11 = this;
            int r0 = r11.getVisibility()
            if (r0 != 0) goto L_0x01a1
            android.graphics.Bitmap r0 = r11.drawBitmap
            if (r0 == 0) goto L_0x01a1
            android.graphics.Bitmap r0 = r11.checkBitmap
            if (r0 != 0) goto L_0x0010
            goto L_0x01a1
        L_0x0010:
            boolean r0 = r11.drawBackground
            r1 = 0
            if (r0 != 0) goto L_0x001b
            float r0 = r11.progress
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x01a1
        L_0x001b:
            android.graphics.Paint r0 = eraser2
            int r2 = r11.size
            int r2 = r2 + 6
            float r2 = (float) r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r0.setStrokeWidth(r2)
            android.graphics.Bitmap r0 = r11.drawBitmap
            r2 = 0
            r0.eraseColor(r2)
            int r0 = r11.getMeasuredWidth()
            int r0 = r0 / 2
            float r0 = (float) r0
            float r3 = r11.progress
            r4 = 1056964608(0x3var_, float:0.5)
            r5 = 1065353216(0x3var_, float:1.0)
            int r6 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r6 < 0) goto L_0x0044
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x0046
        L_0x0044:
            float r6 = r3 / r4
        L_0x0046:
            int r7 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r7 >= 0) goto L_0x004c
            r7 = 0
            goto L_0x004f
        L_0x004c:
            float r7 = r3 - r4
            float r7 = r7 / r4
        L_0x004f:
            boolean r4 = r11.isCheckAnimation
            if (r4 == 0) goto L_0x0054
            goto L_0x0056
        L_0x0054:
            float r3 = r5 - r3
        L_0x0056:
            r4 = 1073741824(0x40000000, float:2.0)
            r8 = 1045220557(0x3e4ccccd, float:0.2)
            int r9 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r9 >= 0) goto L_0x0069
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r9 = (float) r9
            float r9 = r9 * r3
            float r9 = r9 / r8
        L_0x0067:
            float r0 = r0 - r9
            goto L_0x0080
        L_0x0069:
            r9 = 1053609165(0x3ecccccd, float:0.4)
            int r9 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r9 >= 0) goto L_0x0080
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r9 = (float) r9
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r10 = (float) r10
            float r3 = r3 - r8
            float r10 = r10 * r3
            float r10 = r10 / r8
            float r9 = r9 - r10
            goto L_0x0067
        L_0x0080:
            boolean r3 = r11.drawBackground
            if (r3 == 0) goto L_0x00bf
            android.graphics.Paint r3 = paint
            r8 = 1140850688(0x44000000, float:512.0)
            r3.setColor(r8)
            int r3 = r11.getMeasuredWidth()
            int r3 = r3 / 2
            float r3 = (float) r3
            int r8 = r11.getMeasuredHeight()
            int r8 = r8 / 2
            float r8 = (float) r8
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r9 = (float) r9
            float r9 = r0 - r9
            android.graphics.Paint r10 = paint
            r12.drawCircle(r3, r8, r9, r10)
            int r3 = r11.getMeasuredWidth()
            int r3 = r3 / 2
            float r3 = (float) r3
            int r8 = r11.getMeasuredHeight()
            int r8 = r8 / 2
            float r8 = (float) r8
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r9 = (float) r9
            float r9 = r0 - r9
            android.graphics.Paint r10 = backgroundPaint
            r12.drawCircle(r3, r8, r9, r10)
        L_0x00bf:
            android.graphics.Paint r3 = paint
            int r8 = r11.color
            r3.setColor(r8)
            boolean r3 = r11.hasBorder
            if (r3 == 0) goto L_0x00d0
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r3 = (float) r3
            float r0 = r0 - r3
        L_0x00d0:
            android.graphics.Canvas r3 = r11.bitmapCanvas
            int r4 = r11.getMeasuredWidth()
            int r4 = r4 / 2
            float r4 = (float) r4
            int r8 = r11.getMeasuredHeight()
            int r8 = r8 / 2
            float r8 = (float) r8
            android.graphics.Paint r9 = paint
            r3.drawCircle(r4, r8, r0, r9)
            android.graphics.Canvas r3 = r11.bitmapCanvas
            int r4 = r11.getMeasuredWidth()
            int r4 = r4 / 2
            float r4 = (float) r4
            int r8 = r11.getMeasuredHeight()
            int r8 = r8 / 2
            float r8 = (float) r8
            float r6 = r5 - r6
            float r0 = r0 * r6
            android.graphics.Paint r6 = eraser
            r3.drawCircle(r4, r8, r0, r6)
            android.graphics.Bitmap r0 = r11.drawBitmap
            r3 = 0
            r12.drawBitmap(r0, r1, r1, r3)
            android.graphics.Bitmap r0 = r11.checkBitmap
            r0.eraseColor(r2)
            java.lang.String r0 = r11.checkedText
            if (r0 == 0) goto L_0x013b
            android.text.TextPaint r2 = r11.textPaint
            float r0 = r2.measureText(r0)
            double r8 = (double) r0
            double r8 = java.lang.Math.ceil(r8)
            int r0 = (int) r8
            android.graphics.Canvas r2 = r11.checkCanvas
            java.lang.String r4 = r11.checkedText
            int r6 = r11.getMeasuredWidth()
            int r6 = r6 - r0
            int r6 = r6 / 2
            float r0 = (float) r6
            int r6 = r11.size
            r8 = 40
            if (r6 != r8) goto L_0x012e
            r6 = 1105199104(0x41e00000, float:28.0)
            goto L_0x0130
        L_0x012e:
            r6 = 1101529088(0x41a80000, float:21.0)
        L_0x0130:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            android.text.TextPaint r8 = r11.textPaint
            r2.drawText(r4, r0, r6, r8)
            goto L_0x0168
        L_0x013b:
            android.graphics.drawable.Drawable r0 = r11.checkDrawable
            int r0 = r0.getIntrinsicWidth()
            android.graphics.drawable.Drawable r2 = r11.checkDrawable
            int r2 = r2.getIntrinsicHeight()
            int r4 = r11.getMeasuredWidth()
            int r4 = r4 - r0
            int r4 = r4 / 2
            int r6 = r11.getMeasuredHeight()
            int r6 = r6 - r2
            int r6 = r6 / 2
            android.graphics.drawable.Drawable r8 = r11.checkDrawable
            int r9 = r11.checkOffset
            int r10 = r6 + r9
            int r0 = r0 + r4
            int r6 = r6 + r2
            int r6 = r6 + r9
            r8.setBounds(r4, r10, r0, r6)
            android.graphics.drawable.Drawable r0 = r11.checkDrawable
            android.graphics.Canvas r2 = r11.checkCanvas
            r0.draw(r2)
        L_0x0168:
            android.graphics.Canvas r0 = r11.checkCanvas
            int r2 = r11.getMeasuredWidth()
            int r2 = r2 / 2
            r4 = 1075838976(0x40200000, float:2.5)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r2 = r2 - r4
            float r2 = (float) r2
            int r4 = r11.getMeasuredHeight()
            int r4 = r4 / 2
            r6 = 1082130432(0x40800000, float:4.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r4 = r4 + r6
            float r4 = (float) r4
            int r6 = r11.getMeasuredWidth()
            r8 = 1086324736(0x40CLASSNAME, float:6.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r6 = r6 + r8
            int r6 = r6 / 2
            float r6 = (float) r6
            float r5 = r5 - r7
            float r6 = r6 * r5
            android.graphics.Paint r5 = eraser2
            r0.drawCircle(r2, r4, r6, r5)
            android.graphics.Bitmap r0 = r11.checkBitmap
            r12.drawBitmap(r0, r1, r1, r3)
        L_0x01a1:
            return
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
