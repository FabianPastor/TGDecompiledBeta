package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.support.annotation.Keep;
import android.text.TextPaint;
import android.view.View;
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

    /* renamed from: org.telegram.ui.Components.CheckBox$1 */
    class C11161 extends AnimatorListenerAdapter {
        C11161() {
        }

        public void onAnimationEnd(Animator animator) {
            if (animator.equals(CheckBox.this.checkAnimator) != null) {
                CheckBox.this.checkAnimator = null;
            }
            if (CheckBox.this.isChecked == null) {
                CheckBox.this.checkedText = null;
            }
        }
    }

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

    public void setVisibility(int r3) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r2 = this;
        super.setVisibility(r3);
        if (r3 != 0) goto L_0x0047;
    L_0x0005:
        r3 = r2.drawBitmap;
        if (r3 != 0) goto L_0x0047;
    L_0x0009:
        r3 = r2.size;	 Catch:{ Throwable -> 0x0047 }
        r3 = (float) r3;	 Catch:{ Throwable -> 0x0047 }
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);	 Catch:{ Throwable -> 0x0047 }
        r0 = r2.size;	 Catch:{ Throwable -> 0x0047 }
        r0 = (float) r0;	 Catch:{ Throwable -> 0x0047 }
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);	 Catch:{ Throwable -> 0x0047 }
        r1 = android.graphics.Bitmap.Config.ARGB_4444;	 Catch:{ Throwable -> 0x0047 }
        r3 = android.graphics.Bitmap.createBitmap(r3, r0, r1);	 Catch:{ Throwable -> 0x0047 }
        r2.drawBitmap = r3;	 Catch:{ Throwable -> 0x0047 }
        r3 = new android.graphics.Canvas;	 Catch:{ Throwable -> 0x0047 }
        r0 = r2.drawBitmap;	 Catch:{ Throwable -> 0x0047 }
        r3.<init>(r0);	 Catch:{ Throwable -> 0x0047 }
        r2.bitmapCanvas = r3;	 Catch:{ Throwable -> 0x0047 }
        r3 = r2.size;	 Catch:{ Throwable -> 0x0047 }
        r3 = (float) r3;	 Catch:{ Throwable -> 0x0047 }
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);	 Catch:{ Throwable -> 0x0047 }
        r0 = r2.size;	 Catch:{ Throwable -> 0x0047 }
        r0 = (float) r0;	 Catch:{ Throwable -> 0x0047 }
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);	 Catch:{ Throwable -> 0x0047 }
        r1 = android.graphics.Bitmap.Config.ARGB_4444;	 Catch:{ Throwable -> 0x0047 }
        r3 = android.graphics.Bitmap.createBitmap(r3, r0, r1);	 Catch:{ Throwable -> 0x0047 }
        r2.checkBitmap = r3;	 Catch:{ Throwable -> 0x0047 }
        r3 = new android.graphics.Canvas;	 Catch:{ Throwable -> 0x0047 }
        r0 = r2.checkBitmap;	 Catch:{ Throwable -> 0x0047 }
        r3.<init>(r0);	 Catch:{ Throwable -> 0x0047 }
        r2.checkCanvas = r3;	 Catch:{ Throwable -> 0x0047 }
    L_0x0047:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.CheckBox.setVisibility(int):void");
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
        if (this.checkAnimator != null) {
            this.checkAnimator.cancel();
            this.checkAnimator = null;
        }
    }

    private void animateToCheckedState(boolean z) {
        this.isCheckAnimation = z;
        String str = "progress";
        float[] fArr = new float[1];
        fArr[0] = z ? true : false;
        this.checkAnimator = ObjectAnimator.ofFloat(this, str, fArr);
        this.checkAnimator.addListener(new C11161());
        this.checkAnimator.setDuration(300);
        this.checkAnimator.start();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = true;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
    }

    public void setChecked(boolean z, boolean z2) {
        setChecked(-1, z, z2);
    }

    public void setNum(int i) {
        if (i >= 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
            stringBuilder.append(i + 1);
            this.checkedText = stringBuilder.toString();
        } else if (this.checkAnimator == 0) {
            this.checkedText = 0;
        }
        invalidate();
    }

    public void setChecked(int i, boolean z, boolean z2) {
        if (i >= 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
            stringBuilder.append(i + 1);
            this.checkedText = stringBuilder.toString();
            invalidate();
        }
        if (z != this.isChecked) {
            this.isChecked = z;
            if (this.attachedToWindow == 0 || !z2) {
                cancelCheckAnimator();
                setProgress(z ? NUM : 0);
            } else {
                animateToCheckedState(z);
            }
        }
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    protected void onDraw(Canvas canvas) {
        if (getVisibility() == 0 && this.drawBitmap != null) {
            if (this.checkBitmap != null) {
                if (this.drawBackground || this.progress != 0.0f) {
                    eraser2.setStrokeWidth((float) AndroidUtilities.dp((float) (this.size + 6)));
                    this.drawBitmap.eraseColor(0);
                    float measuredWidth = (float) (getMeasuredWidth() / 2);
                    float f = this.progress >= 0.5f ? 1.0f : this.progress / 0.5f;
                    float f2 = this.progress < 0.5f ? 0.0f : (this.progress - 0.5f) / 0.5f;
                    float f3 = this.isCheckAnimation ? this.progress : 1.0f - this.progress;
                    if (f3 < progressBounceDiff) {
                        measuredWidth -= (((float) AndroidUtilities.dp(2.0f)) * f3) / progressBounceDiff;
                    } else if (f3 < 0.4f) {
                        measuredWidth -= ((float) AndroidUtilities.dp(2.0f)) - ((((float) AndroidUtilities.dp(2.0f)) * (f3 - progressBounceDiff)) / progressBounceDiff);
                    }
                    if (this.drawBackground) {
                        paint.setColor(NUM);
                        canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), measuredWidth - ((float) AndroidUtilities.dp(1.0f)), paint);
                        canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), measuredWidth - ((float) AndroidUtilities.dp(1.0f)), backgroundPaint);
                    }
                    paint.setColor(this.color);
                    if (this.hasBorder) {
                        measuredWidth -= (float) AndroidUtilities.dp(2.0f);
                    }
                    this.bitmapCanvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), measuredWidth, paint);
                    this.bitmapCanvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), measuredWidth * (1.0f - f), eraser);
                    canvas.drawBitmap(this.drawBitmap, 0.0f, 0.0f, null);
                    this.checkBitmap.eraseColor(0);
                    if (this.checkedText != null) {
                        this.checkCanvas.drawText(this.checkedText, (float) ((getMeasuredWidth() - ((int) Math.ceil((double) this.textPaint.measureText(this.checkedText)))) / 2), (float) AndroidUtilities.dp(this.size == 40 ? 28.0f : 21.0f), this.textPaint);
                    } else {
                        int intrinsicWidth = this.checkDrawable.getIntrinsicWidth();
                        int intrinsicHeight = this.checkDrawable.getIntrinsicHeight();
                        int measuredWidth2 = (getMeasuredWidth() - intrinsicWidth) / 2;
                        int measuredHeight = (getMeasuredHeight() - intrinsicHeight) / 2;
                        this.checkDrawable.setBounds(measuredWidth2, this.checkOffset + measuredHeight, intrinsicWidth + measuredWidth2, (measuredHeight + intrinsicHeight) + this.checkOffset);
                        this.checkDrawable.draw(this.checkCanvas);
                    }
                    this.checkCanvas.drawCircle((float) ((getMeasuredWidth() / 2) - AndroidUtilities.dp(2.5f)), (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(4.0f)), ((float) ((getMeasuredWidth() + AndroidUtilities.dp(6.0f)) / 2)) * (1.0f - f2), eraser2);
                    canvas.drawBitmap(this.checkBitmap, 0.0f, 0.0f, null);
                }
            }
        }
    }
}
