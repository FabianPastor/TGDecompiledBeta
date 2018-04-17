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
    class C11101 extends AnimatorListenerAdapter {
        C11101() {
        }

        public void onAnimationEnd(Animator animation) {
            if (animation.equals(CheckBox.this.checkAnimator)) {
                CheckBox.this.checkAnimator = null;
            }
            if (!CheckBox.this.isChecked) {
                CheckBox.this.checkedText = null;
            }
        }
    }

    public CheckBox(Context context, int resId) {
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
        this.checkDrawable = context.getResources().getDrawable(resId).mutate();
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == 0 && this.drawBitmap == null) {
            try {
                this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp((float) this.size), AndroidUtilities.dp((float) this.size), Config.ARGB_4444);
                this.bitmapCanvas = new Canvas(this.drawBitmap);
                this.checkBitmap = Bitmap.createBitmap(AndroidUtilities.dp((float) this.size), AndroidUtilities.dp((float) this.size), Config.ARGB_4444);
                this.checkCanvas = new Canvas(this.checkBitmap);
            } catch (Throwable th) {
            }
        }
    }

    @Keep
    public void setProgress(float value) {
        if (this.progress != value) {
            this.progress = value;
            invalidate();
        }
    }

    public void setDrawBackground(boolean value) {
        this.drawBackground = value;
    }

    public void setHasBorder(boolean value) {
        this.hasBorder = value;
    }

    public void setCheckOffset(int value) {
        this.checkOffset = value;
    }

    public void setSize(int size) {
        this.size = size;
        if (size == 40) {
            this.textPaint.setTextSize((float) AndroidUtilities.dp(24.0f));
        }
    }

    public float getProgress() {
        return this.progress;
    }

    public void setColor(int backgroundColor, int checkColor) {
        this.color = backgroundColor;
        this.checkDrawable.setColorFilter(new PorterDuffColorFilter(checkColor, Mode.MULTIPLY));
        this.textPaint.setColor(checkColor);
        invalidate();
    }

    public void setBackgroundColor(int backgroundColor) {
        this.color = backgroundColor;
        invalidate();
    }

    public void setCheckColor(int checkColor) {
        this.checkDrawable.setColorFilter(new PorterDuffColorFilter(checkColor, Mode.MULTIPLY));
        this.textPaint.setColor(checkColor);
        invalidate();
    }

    private void cancelCheckAnimator() {
        if (this.checkAnimator != null) {
            this.checkAnimator.cancel();
            this.checkAnimator = null;
        }
    }

    private void animateToCheckedState(boolean newCheckedState) {
        this.isCheckAnimation = newCheckedState;
        String str = "progress";
        float[] fArr = new float[1];
        fArr[0] = newCheckedState ? 1.0f : 0.0f;
        this.checkAnimator = ObjectAnimator.ofFloat(this, str, fArr);
        this.checkAnimator.addListener(new C11101());
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

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void setChecked(boolean checked, boolean animated) {
        setChecked(-1, checked, animated);
    }

    public void setNum(int num) {
        if (num >= 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
            stringBuilder.append(num + 1);
            this.checkedText = stringBuilder.toString();
        } else if (this.checkAnimator == null) {
            this.checkedText = null;
        }
        invalidate();
    }

    public void setChecked(int num, boolean checked, boolean animated) {
        if (num >= 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
            stringBuilder.append(num + 1);
            this.checkedText = stringBuilder.toString();
            invalidate();
        }
        if (checked != this.isChecked) {
            this.isChecked = checked;
            if (this.attachedToWindow && animated) {
                animateToCheckedState(checked);
            } else {
                cancelCheckAnimator();
                setProgress(checked ? 1.0f : 0.0f);
            }
        }
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    protected void onDraw(Canvas canvas) {
        CheckBox checkBox = this;
        Canvas canvas2 = canvas;
        if (getVisibility() == 0 && checkBox.drawBitmap != null) {
            if (checkBox.checkBitmap != null) {
                if (checkBox.drawBackground || checkBox.progress != 0.0f) {
                    eraser2.setStrokeWidth((float) AndroidUtilities.dp((float) (checkBox.size + 6)));
                    checkBox.drawBitmap.eraseColor(0);
                    float rad = (float) (getMeasuredWidth() / 2);
                    float roundProgress = checkBox.progress >= 0.5f ? 1.0f : checkBox.progress / 0.5f;
                    float checkProgress = checkBox.progress < 0.5f ? 0.0f : (checkBox.progress - 0.5f) / 0.5f;
                    float roundProgressCheckState = checkBox.isCheckAnimation ? checkBox.progress : 1.0f - checkBox.progress;
                    if (roundProgressCheckState < progressBounceDiff) {
                        rad -= (((float) AndroidUtilities.dp(2.0f)) * roundProgressCheckState) / progressBounceDiff;
                    } else if (roundProgressCheckState < 0.4f) {
                        rad -= ((float) AndroidUtilities.dp(2.0f)) - ((((float) AndroidUtilities.dp(2.0f)) * (roundProgressCheckState - progressBounceDiff)) / progressBounceDiff);
                    }
                    if (checkBox.drawBackground) {
                        paint.setColor(NUM);
                        canvas2.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), rad - ((float) AndroidUtilities.dp(1.0f)), paint);
                        canvas2.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), rad - ((float) AndroidUtilities.dp(1.0f)), backgroundPaint);
                    }
                    paint.setColor(checkBox.color);
                    if (checkBox.hasBorder) {
                        rad -= (float) AndroidUtilities.dp(2.0f);
                    }
                    checkBox.bitmapCanvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), rad, paint);
                    checkBox.bitmapCanvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (1.0f - roundProgress) * rad, eraser);
                    canvas2.drawBitmap(checkBox.drawBitmap, 0.0f, 0.0f, null);
                    checkBox.checkBitmap.eraseColor(0);
                    if (checkBox.checkedText != null) {
                        checkBox.checkCanvas.drawText(checkBox.checkedText, (float) ((getMeasuredWidth() - ((int) Math.ceil((double) checkBox.textPaint.measureText(checkBox.checkedText)))) / 2), (float) AndroidUtilities.dp(checkBox.size == 40 ? 28.0f : 21.0f), checkBox.textPaint);
                    } else {
                        int w = checkBox.checkDrawable.getIntrinsicWidth();
                        int h = checkBox.checkDrawable.getIntrinsicHeight();
                        int x = (getMeasuredWidth() - w) / 2;
                        int y = (getMeasuredHeight() - h) / 2;
                        checkBox.checkDrawable.setBounds(x, checkBox.checkOffset + y, x + w, (y + h) + checkBox.checkOffset);
                        checkBox.checkDrawable.draw(checkBox.checkCanvas);
                    }
                    checkBox.checkCanvas.drawCircle((float) ((getMeasuredWidth() / 2) - AndroidUtilities.dp(2.5f)), (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(4.0f)), ((float) ((getMeasuredWidth() + AndroidUtilities.dp(6.0f)) / 2)) * (1.0f - checkProgress), eraser2);
                    canvas2.drawBitmap(checkBox.checkBitmap, 0.0f, 0.0f, null);
                }
            }
        }
    }
}
